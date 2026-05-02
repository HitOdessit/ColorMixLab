package com.colormixlab.game

import com.colormixlab.model.GameColor
import com.colormixlab.model.PlatformColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock

/**
 * Central game state machine. Owns every gameplay action: adding color drops,
 * checking matches, advancing levels, ticking the timer, and computing scores.
 *
 * State is exposed as a [StateFlow] of immutable [GameState] snapshots. All
 * mutations go through [MutableStateFlow.update] so concurrent callers (the
 * UI thread and the game-tick coroutine) can never observe a partially updated
 * state.
 *
 * Lives in `commonMain` so Android and iOS share a single source of truth.
 */
class GameController(initialDifficulty: Difficulty = Difficulty.MEDIUM) {
    private val _gameState = MutableStateFlow(GameState(difficulty = initialDifficulty))

    /** Observable state stream. UI layers collect from this to render. */
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    init {
        val seed = Clock.System.now().toEpochMilliseconds()
        GameColor.initializeGameColors(seed)
        startNewLevel()
    }

    // ========== GAME ACTIONS ==========

    /** Switch difficulty mid-session. Affects scoring multipliers and timer durations from the next level. */
    fun setDifficulty(difficulty: Difficulty) {
        _gameState.update { it.copy(difficulty = difficulty) }
    }

    /** Add one drop of [color] to the bowl, recalculating the mixed color and similarity. */
    fun addColorDrop(color: GameColor) {
        _gameState.update { state ->
            val currentDrops = state.drops.toMutableMap()
            currentDrops[color] = (currentDrops[color] ?: 0) + 1

            val newMixedColor = ColorMixer.mixColors(currentDrops)
            val similarity = ColorMixer.calculateSimilarity(state.targetColor, newMixedColor)

            state.copy(
                drops = currentDrops,
                mixedColor = newMixedColor,
                similarity = similarity
            )
        }
    }

    /** Empty the bowl, resetting drops, mixed color, and similarity. */
    fun clearBowl() {
        _gameState.update { it.copy(
            drops = emptyMap(),
            mixedColor = PlatformColor.White,
            similarity = 0f
        ) }
    }

    /**
     * Check the current mix against the target. Awards points based on similarity
     * and (on Medium/Hard) adds a time bonus for remaining seconds. Idempotent
     * within a level — a second call in the same round is a no-op.
     */
    fun checkMatch() {
        _gameState.update { state ->
            if (state.hasCheckedThisRound) return@update state

            val similarity = state.similarity
            val points = calculatePointsInternal(similarity, state.difficulty)
            val timeBonus = calculateTimeBonusInternal(similarity, state.timeRemainingSeconds, state.difficulty)
            val totalPoints = points + timeBonus
            val newScore = (state.currentScore + totalPoints).coerceAtLeast(0)
            val isSuccess = similarity >= GameConstants.MATCH_SUCCESS_THRESHOLD

            state.copy(
                isMatched = isSuccess,
                showSuccessDialog = true,
                hasCheckedThisRound = true,
                currentScore = newScore,
                lastBasePoints = points,
                lastTimeBonus = timeBonus
            )
        }
    }

    /**
     * Advance to the next level. If the next level requires a math challenge
     * (color unlock or milestone), flags [GameState.needsMathChallenge] and
     * pauses the timer until [completeMathChallenge] is called.
     */
    fun nextLevel() {
        _gameState.update { state ->
            val currentLevel = state.currentLevel

            if (currentLevel >= GameState.MAX_LEVEL) {
                return@update state.copy(
                    showSuccessDialog = false,
                    isGameCompleted = true,
                    completedAllLevels = true,
                    isTimerActive = false
                )
            }

            val newLevel = currentLevel + 1

            val needsColorUnlock = newLevel in GameConstants.COLOR_UNLOCK_LEVELS
            val needsMilestone = newLevel > GameConstants.MILESTONE_START_LEVEL &&
                (newLevel - GameConstants.MILESTONE_START_LEVEL) % GameConstants.MILESTONE_INTERVAL == 0

            val needsChallenge = needsColorUnlock || needsMilestone
            val challengeType = when {
                needsColorUnlock -> MathChallengeType.COLOR_UNLOCK
                needsMilestone -> MathChallengeType.MILESTONE
                else -> MathChallengeType.NONE
            }

            val previousTarget = state.targetColor
            val (targetColor, recipe) = LevelManager.generateTargetColor(newLevel, previousTarget)

            state.copy(
                currentLevel = newLevel,
                unlockedColors = if (needsChallenge) state.unlockedColors else GameColor.getAvailableColors(newLevel),
                targetColor = targetColor,
                targetRecipe = recipe,
                mixedColor = PlatformColor.White,
                drops = emptyMap(),
                isMatched = false,
                showSuccessDialog = false,
                hasCheckedThisRound = false,
                similarity = 0f,
                needsMathChallenge = needsChallenge,
                mathChallengeType = challengeType,
                mathChallengeCompleted = false,
                isTimerActive = !needsChallenge,
                timeRemainingSeconds = if (!needsChallenge) GameState.getTimerDuration(state.difficulty) else null
            )
        }
    }

    /** Generate a fresh target for the current level, keeping the score and level. */
    fun retryLevel() {
        _gameState.update { state ->
            val previousTarget = state.targetColor
            val (targetColor, recipe) = LevelManager.generateTargetColor(state.currentLevel, previousTarget)

            state.copy(
                targetColor = targetColor,
                targetRecipe = recipe,
                mixedColor = PlatformColor.White,
                drops = emptyMap(),
                showSuccessDialog = false,
                hasCheckedThisRound = false,
                similarity = 0f,
                isTimerActive = true,
                timeRemainingSeconds = GameState.getTimerDuration(state.difficulty)
            )
        }
    }

    /** Mark the current math challenge complete and unlock the gated color tier. */
    fun completeMathChallenge() {
        _gameState.update { state ->
            state.copy(
                needsMathChallenge = false,
                mathChallengeCompleted = true,
                unlockedColors = GameColor.getAvailableColors(state.currentLevel),
                isTimerActive = true,
                timeRemainingSeconds = GameState.getTimerDuration(state.difficulty)
            )
        }
    }

    /**
     * Returns the active math challenge (type + the color about to unlock), or
     * `null` if no challenge is currently required.
     */
    fun getCurrentMathChallenge(): Pair<MathChallengeType, GameColor?>? {
        val state = _gameState.value
        if (!state.needsMathChallenge) return null

        val challengeType = state.mathChallengeType
        val currentUnlocked = state.unlockedColors
        val nextLevelColors = GameColor.getAvailableColors(state.currentLevel)

        val nextColor = nextLevelColors.firstOrNull { color ->
            !currentUnlocked.any { it.name == color.name }
        }

        return Pair(challengeType, nextColor)
    }

    /** Apply the wrong-answer score penalty (clamped at zero). */
    fun deductPointsForWrongMathAnswer() {
        _gameState.update { state ->
            state.copy(currentScore = (state.currentScore - GameConstants.WRONG_MATH_ANSWER_PENALTY).coerceAtLeast(0))
        }
    }

    /** Reset to a fresh game at level 1 with a new random color palette. */
    fun resetGame() {
        GameColor.resetColors()
        val seed = Clock.System.now().toEpochMilliseconds()
        GameColor.initializeGameColors(seed)

        _gameState.value = GameState(difficulty = _gameState.value.difficulty)
        startNewLevel()
    }

    /** End the current game from the menu (not a natural completion). */
    fun forceFinishGame() {
        _gameState.update { it.copy(
            isGameCompleted = true,
            completedAllLevels = false,
            isTimerActive = false
        ) }
    }

    // ========== TIMER MANAGEMENT ==========

    /**
     * Decrement the timer by one second. Triggers automatic match-check when
     * time hits zero. Called every second by the platform-side coroutine.
     */
    fun tickTimer() {
        _gameState.update { state ->
            val currentTime = state.timeRemainingSeconds ?: return@update state
            if (state.isTimerPaused || !state.isTimerActive) return@update state

            val newTime = (currentTime - 1).coerceAtLeast(0)

            if (newTime == 0) {
                val similarity = state.similarity
                val points = calculatePointsInternal(similarity, state.difficulty)
                val timeBonus = calculateTimeBonusInternal(similarity, 0, state.difficulty)
                val totalPoints = points + timeBonus
                val newScore = (state.currentScore + totalPoints).coerceAtLeast(0)
                val isSuccess = similarity >= GameConstants.MATCH_SUCCESS_THRESHOLD

                state.copy(
                    timeRemainingSeconds = 0,
                    isMatched = isSuccess,
                    showSuccessDialog = true,
                    hasCheckedThisRound = true,
                    currentScore = newScore,
                    isTimerActive = false,
                    lastBasePoints = points,
                    lastTimeBonus = timeBonus
                )
            } else {
                state.copy(timeRemainingSeconds = newTime)
            }
        }
    }

    /** Pause the timer (e.g., when a dialog is showing). */
    fun pauseTimer() {
        _gameState.update { it.copy(isTimerPaused = true) }
    }

    /** Resume a paused timer. */
    fun resumeTimer() {
        _gameState.update { it.copy(isTimerPaused = false) }
    }

    // ========== SCORE CALCULATION ==========

    /** Calculate base points for a given match similarity at the current difficulty. */
    fun calculatePoints(similarity: Float): Int {
        return calculatePointsInternal(similarity, _gameState.value.difficulty)
    }

    private fun calculatePointsInternal(similarity: Float, difficulty: Difficulty): Int {
        val basePoints = when {
            similarity >= 1.0f -> 150
            similarity >= 0.95f -> 100
            similarity >= 0.90f -> 80
            similarity >= 0.85f -> 60
            similarity >= GameConstants.MATCH_SUCCESS_THRESHOLD -> 40
            similarity >= 0.75f -> -10
            similarity >= 0.70f -> -15
            similarity >= 0.65f -> -20
            similarity >= 0.60f -> -25
            similarity >= 0.50f -> -35
            else -> -50
        }

        val multiplier = when (difficulty) {
            Difficulty.EASY -> GameConstants.EASY_MULTIPLIER
            Difficulty.MEDIUM -> GameConstants.MEDIUM_MULTIPLIER
            Difficulty.HARD -> GameConstants.HARD_MULTIPLIER
        }

        return (basePoints * multiplier).toInt()
    }

    private fun calculateTimeBonusInternal(similarity: Float, timeRemaining: Int?, difficulty: Difficulty): Int {
        if (similarity < GameConstants.MATCH_SUCCESS_THRESHOLD) return 0

        val time = timeRemaining ?: return 0
        val totalDuration = GameState.getTimerDuration(difficulty) ?: return 0

        val timePercent = time.toFloat() / totalDuration.toFloat()
        return (GameConstants.MAX_TIME_BONUS * timePercent).toInt()
    }

    // ========== UI MESSAGES ==========

    /** Human-readable feedback message for a given match similarity. */
    fun getResultMessage(similarity: Float): String {
        return when {
            similarity >= 1.0f -> "Perfect Match!"
            similarity >= 0.95f -> "Excellent Mix!"
            similarity >= 0.90f -> "Great Job!"
            similarity >= 0.85f -> "Very Good!"
            similarity >= GameConstants.MATCH_SUCCESS_THRESHOLD -> "Nice Work!"
            similarity >= 0.75f -> "Almost There!"
            similarity >= 0.70f -> "Close!"
            similarity >= 0.65f -> "Getting Closer!"
            similarity >= 0.60f -> "Keep Trying!"
            similarity >= 0.50f -> "Not Quite!"
            else -> "Try Harder!"
        }
    }

    /** Emoji corresponding to a given match similarity. */
    fun getResultEmoji(similarity: Float): String {
        return when {
            similarity >= 1.0f -> "🎉"
            similarity >= 0.95f -> "⭐"
            similarity >= 0.90f -> "👍"
            similarity >= 0.85f -> "😊"
            similarity >= GameConstants.MATCH_SUCCESS_THRESHOLD -> "👌"
            similarity >= 0.75f -> "🎨"
            similarity >= 0.70f -> "💪"
            similarity >= 0.65f -> "🤔"
            similarity >= 0.60f -> "🔄"
            similarity >= 0.50f -> "😕"
            else -> "😢"
        }
    }

    // ========== PRIVATE HELPERS ==========

    private fun startNewLevel() {
        val currentState = _gameState.value
        val previousTarget = if (currentState.currentLevel > 1) {
            currentState.targetColor
        } else null

        val (targetColor, recipe) = LevelManager.generateTargetColor(
            currentState.currentLevel,
            previousTarget
        )

        _gameState.update { it.copy(
            targetColor = targetColor,
            targetRecipe = recipe,
            mixedColor = PlatformColor.White,
            drops = emptyMap(),
            isMatched = false,
            showSuccessDialog = false,
            similarity = 0f,
            isTimerActive = true,
            timeRemainingSeconds = GameState.getTimerDuration(it.difficulty)
        ) }
    }
}
