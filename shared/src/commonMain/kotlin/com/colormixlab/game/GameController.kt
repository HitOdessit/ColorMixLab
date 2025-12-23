package com.colormixlab.game

import com.colormixlab.model.GameColor
import com.colormixlab.model.PlatformColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

/**
 * Shared game controller - ALL game logic lives here!
 * Both Android and iOS use this same class.
 * Platform-specific ViewModels are just thin wrappers for UI observation.
 */
class GameController(initialDifficulty: Difficulty = Difficulty.MEDIUM) {
    private val _gameState = MutableStateFlow(GameState(difficulty = initialDifficulty))
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    init {
        val seed = Clock.System.now().toEpochMilliseconds()
        GameColor.initializeGameColors(seed)
        startNewLevel()
    }

    // ========== GAME ACTIONS ==========

    fun setDifficulty(difficulty: Difficulty) {
        _gameState.value = _gameState.value.copy(difficulty = difficulty)
    }

    fun addColorDrop(color: GameColor) {
        val currentDrops = _gameState.value.drops.toMutableMap()
        currentDrops[color] = (currentDrops[color] ?: 0) + 1

        val newMixedColor = ColorMixer.mixColors(currentDrops)
        val similarity = ColorMixer.calculateSimilarity(
            _gameState.value.targetColor,
            newMixedColor
        )

        _gameState.value = _gameState.value.copy(
            drops = currentDrops,
            mixedColor = newMixedColor,
            similarity = similarity
        )
    }

    fun clearBowl() {
        _gameState.value = _gameState.value.copy(
            drops = emptyMap(),
            mixedColor = PlatformColor.White,
            similarity = 0f
        )
    }

    fun checkMatch() {
        if (_gameState.value.hasCheckedThisRound) return

        val similarity = _gameState.value.similarity
        val points = calculatePoints(similarity)
        val timeBonus = calculateTimeBonus(similarity)
        val totalPoints = points + timeBonus
        val newScore = (_gameState.value.currentScore + totalPoints).coerceAtLeast(0)

        val isSuccess = similarity >= 0.80f

        _gameState.value = _gameState.value.copy(
            isMatched = isSuccess,
            showSuccessDialog = true,
            hasCheckedThisRound = true,
            currentScore = newScore,
            lastBasePoints = points,
            lastTimeBonus = timeBonus
        )
    }

    fun nextLevel() {
        val currentLevel = _gameState.value.currentLevel

        if (currentLevel >= GameState.MAX_LEVEL) {
            _gameState.value = _gameState.value.copy(
                showSuccessDialog = false,
                isGameCompleted = true,
                isTimerActive = false
            )
            return
        }

        val newLevel = currentLevel + 1

        // Check for math challenges
        val colorUnlockLevels = listOf(4, 7, 10, 13, 16, 19)
        val needsColorUnlock = newLevel in colorUnlockLevels
        val needsMilestone = newLevel > 19 && (newLevel - 19) % 3 == 0

        val needsChallenge = needsColorUnlock || needsMilestone
        val challengeType = when {
            needsColorUnlock -> MathChallengeType.COLOR_UNLOCK
            needsMilestone -> MathChallengeType.MILESTONE
            else -> MathChallengeType.NONE
        }

        val previousTarget = _gameState.value.targetColor
        val (targetColor, recipe) = LevelManager.generateTargetColor(newLevel, previousTarget)

        _gameState.value = _gameState.value.copy(
            currentLevel = newLevel,
            unlockedColors = if (needsChallenge) _gameState.value.unlockedColors else GameColor.getAvailableColors(newLevel),
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
            timeRemainingSeconds = if (!needsChallenge) GameState.getTimerDuration(_gameState.value.difficulty) else null
        )
    }

    fun retryLevel() {
        val previousTarget = _gameState.value.targetColor
        val (targetColor, recipe) = LevelManager.generateTargetColor(
            _gameState.value.currentLevel,
            previousTarget
        )

        _gameState.value = _gameState.value.copy(
            targetColor = targetColor,
            targetRecipe = recipe,
            mixedColor = PlatformColor.White,
            drops = emptyMap(),
            showSuccessDialog = false,
            hasCheckedThisRound = false,
            similarity = 0f,
            isTimerActive = true,
            timeRemainingSeconds = GameState.getTimerDuration(_gameState.value.difficulty)
        )
    }

    fun completeMathChallenge() {
        _gameState.value = _gameState.value.copy(
            needsMathChallenge = false,
            mathChallengeCompleted = true,
            unlockedColors = GameColor.getAvailableColors(_gameState.value.currentLevel),
            isTimerActive = true,
            timeRemainingSeconds = GameState.getTimerDuration(_gameState.value.difficulty)
        )
    }

    fun getCurrentMathChallenge(): Pair<MathChallengeType, GameColor?>? {
        if (!_gameState.value.needsMathChallenge) return null

        val challengeType = _gameState.value.mathChallengeType
        val currentUnlocked = _gameState.value.unlockedColors
        val nextLevelColors = GameColor.getAvailableColors(_gameState.value.currentLevel)

        val nextColor = nextLevelColors.firstOrNull { color ->
            !currentUnlocked.any { it.name == color.name }
        }

        return Pair(challengeType, nextColor)
    }

    fun deductPointsForWrongMathAnswer() {
        val newScore = (_gameState.value.currentScore - 75).coerceAtLeast(0)
        _gameState.value = _gameState.value.copy(currentScore = newScore)
    }

    fun resetGame() {
        GameColor.resetColors()
        val seed = Clock.System.now().toEpochMilliseconds()
        GameColor.initializeGameColors(seed)

        _gameState.value = GameState(difficulty = _gameState.value.difficulty)
        startNewLevel()
    }

    fun forceFinishGame() {
        _gameState.value = _gameState.value.copy(
            isGameCompleted = true,
            isTimerActive = false
        )
    }

    // ========== TIMER MANAGEMENT ==========

    fun tickTimer() {
        val currentTime = _gameState.value.timeRemainingSeconds ?: return
        if (_gameState.value.isTimerPaused || !_gameState.value.isTimerActive) return

        val newTime = (currentTime - 1).coerceAtLeast(0)
        _gameState.value = _gameState.value.copy(timeRemainingSeconds = newTime)

        if (newTime == 0) {
            onTimerExpired()
        }
    }

    fun pauseTimer() {
        _gameState.value = _gameState.value.copy(isTimerPaused = true)
    }

    fun resumeTimer() {
        _gameState.value = _gameState.value.copy(isTimerPaused = false)
    }

    private fun onTimerExpired() {
        val similarity = _gameState.value.similarity
        val points = calculatePoints(similarity)
        val timeBonus = calculateTimeBonus(similarity)
        val totalPoints = points + timeBonus
        val newScore = (_gameState.value.currentScore + totalPoints).coerceAtLeast(0)

        val isSuccess = similarity >= 0.80f

        _gameState.value = _gameState.value.copy(
            isMatched = isSuccess,
            showSuccessDialog = true,
            hasCheckedThisRound = true,
            currentScore = newScore,
            isTimerActive = false,
            lastBasePoints = points,
            lastTimeBonus = timeBonus
        )
    }

    // ========== SCORE CALCULATION ==========

    fun calculatePoints(similarity: Float): Int {
        val basePoints = when {
            similarity >= 1.0f -> 150
            similarity >= 0.95f -> 100
            similarity >= 0.90f -> 80
            similarity >= 0.85f -> 60
            similarity >= 0.80f -> 40
            similarity >= 0.75f -> -10
            similarity >= 0.70f -> -15
            similarity >= 0.65f -> -20
            similarity >= 0.60f -> -25
            similarity >= 0.50f -> -35
            else -> -50
        }

        val multiplier = when (_gameState.value.difficulty) {
            Difficulty.EASY -> 0.75f
            Difficulty.MEDIUM -> 1.0f
            Difficulty.HARD -> 1.25f
        }

        return (basePoints * multiplier).toInt()
    }

    private fun calculateTimeBonus(similarity: Float): Int {
        if (similarity < 0.80f) return 0

        val timeRemaining = _gameState.value.timeRemainingSeconds ?: return 0
        val totalDuration = GameState.getTimerDuration(_gameState.value.difficulty) ?: return 0

        val timePercent = timeRemaining.toFloat() / totalDuration.toFloat()
        return (50 * timePercent).toInt()
    }

    // ========== UI MESSAGES ==========

    fun getResultMessage(similarity: Float): String {
        return when {
            similarity >= 1.0f -> "Perfect Match!"
            similarity >= 0.95f -> "Excellent Mix!"
            similarity >= 0.90f -> "Great Job!"
            similarity >= 0.85f -> "Very Good!"
            similarity >= 0.80f -> "Nice Work!"
            similarity >= 0.75f -> "Almost There!"
            similarity >= 0.70f -> "Close!"
            similarity >= 0.65f -> "Getting Closer!"
            similarity >= 0.60f -> "Keep Trying!"
            similarity >= 0.50f -> "Not Quite!"
            else -> "Try Harder!"
        }
    }

    fun getResultEmoji(similarity: Float): String {
        return when {
            similarity >= 1.0f -> "🎉"
            similarity >= 0.95f -> "⭐"
            similarity >= 0.90f -> "👍"
            similarity >= 0.85f -> "😊"
            similarity >= 0.80f -> "👌"
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
        val previousTarget = if (_gameState.value.currentLevel > 1) {
            _gameState.value.targetColor
        } else null

        val (targetColor, recipe) = LevelManager.generateTargetColor(
            _gameState.value.currentLevel,
            previousTarget
        )

        _gameState.value = _gameState.value.copy(
            targetColor = targetColor,
            targetRecipe = recipe,
            mixedColor = PlatformColor.White,
            drops = emptyMap(),
            isMatched = false,
            showSuccessDialog = false,
            similarity = 0f,
            isTimerActive = true,
            timeRemainingSeconds = GameState.getTimerDuration(_gameState.value.difficulty)
        )
    }
}
