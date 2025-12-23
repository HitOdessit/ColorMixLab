package com.colormixlab.game

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colormixlab.model.GameColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val _gameState = mutableStateOf(GameState())
    val gameState: State<GameState> = _gameState
    
    private var timerJob: Job? = null
    
    init {
        // Initialize game colors at the start
        GameColor.initializeGameColors()
        startNewLevel()
    }
    
    fun setDifficulty(difficulty: Difficulty) {
        _gameState.value = _gameState.value.copy(difficulty = difficulty)
    }
    
    fun addColorDrop(color: GameColor) {
        val currentDrops = _gameState.value.drops.toMutableMap()
        currentDrops[color] = (currentDrops[color] ?: 0) + 1
        
        val newMixedColor = ColorMixer.mixColors(currentDrops)
        val similarity = ColorMixer.calculateSimilarity(_gameState.value.targetColor, newMixedColor)
        
        _gameState.value = _gameState.value.copy(
            drops = currentDrops,
            mixedColor = newMixedColor,
            similarity = similarity
        )
    }
    
    fun clearBowl() {
        _gameState.value = _gameState.value.copy(
            drops = emptyMap(),
            mixedColor = androidx.compose.ui.graphics.Color.White,
            similarity = 0f
        )
    }
    
    fun checkMatch() {
        // Only allow one check per round
        if (_gameState.value.hasCheckedThisRound) {
            return
        }

        val similarity = _gameState.value.similarity
        val points = calculatePoints(similarity)
        val timeBonus = calculateTimeBonus(similarity)
        val totalPoints = points + timeBonus
        val newScore = (_gameState.value.currentScore + totalPoints).coerceAtLeast(0)

        val isSuccess = similarity >= 0.80f  // Adjusted to 80%

        _gameState.value = _gameState.value.copy(
            isMatched = isSuccess,
            showSuccessDialog = true,
            hasCheckedThisRound = true,
            currentScore = newScore,
            lastBasePoints = points,
            lastTimeBonus = timeBonus
        )
    }
    
    fun calculatePoints(similarity: Float): Int {
        val basePoints = when {
            similarity >= 1.0f -> 150  // 100 + 50 bonus - Perfect match
            similarity >= 0.95f -> 100  // Excellent
            similarity >= 0.90f -> 80   // Great
            similarity >= 0.85f -> 60   // Very good
            similarity >= 0.80f -> 40   // Good - Pass threshold
            // Below 80% - graduated negative points
            similarity >= 0.75f -> -10  // Close but not enough
            similarity >= 0.70f -> -15  // Need more precision
            similarity >= 0.65f -> -20  // Getting further
            similarity >= 0.60f -> -25  // Not close enough
            similarity >= 0.50f -> -35  // Way off
            else -> -50  // Very far from target
        }

        val multiplier = when (_gameState.value.difficulty) {
            Difficulty.EASY -> 0.75f
            Difficulty.MEDIUM -> 1.0f
            Difficulty.HARD -> 1.25f
        }

        return (basePoints * multiplier).toInt()
    }

    private fun calculateTimeBonus(similarity: Float): Int {
        // Only give time bonus if answer is above threshold
        if (similarity < 0.80f) return 0

        // No timer in EASY mode
        val timeRemaining = _gameState.value.timeRemainingSeconds ?: return 0
        val totalDuration = GameState.getTimerDuration(_gameState.value.difficulty) ?: return 0

        // Calculate percentage of time remaining (0.0 to 1.0)
        val timePercent = timeRemaining.toFloat() / totalDuration.toFloat()

        // Maximum time bonus is 50 points, scaled by time remaining
        // This is independent of difficulty level
        return (50 * timePercent).toInt()
    }
    
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
    
    fun nextLevel() {
        val currentLevel = _gameState.value.currentLevel
        
        // Check if game is completed
        if (currentLevel >= GameState.MAX_LEVEL) {
            cancelTimer()
            _gameState.value = _gameState.value.copy(
                showSuccessDialog = false,
                isGameCompleted = true
            )
            return
        }
        
        val newLevel = currentLevel + 1
        
        // Check if this level unlocks a new color
        val colorUnlockLevels = listOf(4, 7, 10, 13, 16, 19)
        val needsColorUnlockChallenge = newLevel in colorUnlockLevels
        
        // Check if this is a milestone level (every 3 levels after 19)
        val needsMilestoneChallenge = newLevel > 19 && (newLevel - 19) % 3 == 0
        
        val needsChallenge = needsColorUnlockChallenge || needsMilestoneChallenge
        val challengeType = when {
            needsColorUnlockChallenge -> MathChallengeType.COLOR_UNLOCK
            needsMilestoneChallenge -> MathChallengeType.MILESTONE
            else -> MathChallengeType.NONE
        }
        
        val previousTarget = _gameState.value.targetColor
        val (targetColor, recipe) = LevelManager.generateTargetColor(newLevel, previousTarget)
        
        cancelTimer()
        
        _gameState.value = _gameState.value.copy(
            currentLevel = newLevel,
            unlockedColors = if (needsChallenge) _gameState.value.unlockedColors else GameColor.getAvailableColors(newLevel),
            targetColor = targetColor,
            targetRecipe = recipe,
            mixedColor = androidx.compose.ui.graphics.Color.White,
            drops = emptyMap(),
            isMatched = false,
            showSuccessDialog = false,
            hasCheckedThisRound = false,
            similarity = 0f,
            needsMathChallenge = needsChallenge,
            mathChallengeType = challengeType,
            mathChallengeCompleted = false
        )
        
        if (!needsChallenge) {
            startTimer()
        }
    }
    
    fun retryLevel() {
        val previousTarget = _gameState.value.targetColor
        val (targetColor, recipe) = LevelManager.generateTargetColor(_gameState.value.currentLevel, previousTarget)
        
        cancelTimer()
        
        _gameState.value = _gameState.value.copy(
            targetColor = targetColor,
            targetRecipe = recipe,
            mixedColor = androidx.compose.ui.graphics.Color.White,
            drops = emptyMap(),
            showSuccessDialog = false,
            hasCheckedThisRound = false,
            similarity = 0f
        )
        
        startTimer()
    }
    
    fun dismissSuccessDialog() {
        _gameState.value = _gameState.value.copy(showSuccessDialog = false)
    }
    
    fun completeMathChallenge() {
        _gameState.value = _gameState.value.copy(
            needsMathChallenge = false,
            mathChallengeCompleted = true,
            unlockedColors = GameColor.getAvailableColors(_gameState.value.currentLevel)
        )
        startTimer()
    }

    fun getCurrentMathChallenge(): Pair<MathChallengeType, GameColor?>? {
        if (!_gameState.value.needsMathChallenge) return null

        val challengeType = _gameState.value.mathChallengeType

        // Find the next color to unlock
        val currentLevel = _gameState.value.currentLevel
        val currentUnlockedColors = _gameState.value.unlockedColors
        val nextLevelColors = GameColor.getAvailableColors(currentLevel)

        // Find the first color that will be newly unlocked
        val nextColorToUnlock = nextLevelColors.firstOrNull { color ->
            !currentUnlockedColors.contains(color)
        }

        return Pair(challengeType, nextColorToUnlock)
    }

    fun deductPointsForWrongMathAnswer() {
        val newScore = (_gameState.value.currentScore - 75).coerceAtLeast(0)
        _gameState.value = _gameState.value.copy(currentScore = newScore)
    }

    fun exitGame() {
        cancelTimer()
    }

    private fun startNewLevel() {
        val previousTarget = if (_gameState.value.currentLevel > 1) _gameState.value.targetColor else null
        val (targetColor, recipe) = LevelManager.generateTargetColor(_gameState.value.currentLevel, previousTarget)
        
        _gameState.value = _gameState.value.copy(
            targetColor = targetColor,
            targetRecipe = recipe,
            mixedColor = androidx.compose.ui.graphics.Color.White,
            drops = emptyMap(),
            isMatched = false,
            showSuccessDialog = false,
            similarity = 0f
        )
        
        startTimer()
    }
    
    fun resetGame() {
        cancelTimer()
        // Reset and reinitialize colors for a new game session
        GameColor.resetColors()
        GameColor.initializeGameColors()
        _gameState.value = GameState(difficulty = _gameState.value.difficulty)
        startNewLevel()
    }
    
    fun completeGame() {
        _gameState.value = _gameState.value.copy(
            isGameCompleted = false
        )
    }

    fun forceFinishGame() {
        cancelTimer()
        _gameState.value = _gameState.value.copy(
            isGameCompleted = true,
            showSuccessDialog = false
        )
    }
    
    // Timer functions
    fun startTimer() {
        val duration = GameState.getTimerDuration(_gameState.value.difficulty) ?: return

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _gameState.value = _gameState.value.copy(
                timeRemainingSeconds = duration,
                isTimerActive = true,
                isTimerPaused = false
            )

            while (true) {
                val currentTime = _gameState.value.timeRemainingSeconds ?: break
                if (currentTime <= 0) break

                delay(1000)
                if (!_gameState.value.isTimerPaused) {
                    val newTime = currentTime - 1
                    _gameState.value = _gameState.value.copy(
                        timeRemainingSeconds = newTime
                    )

                    if (newTime == 0) {
                        onTimerExpired()
                    }
                }
            }
        }
    }
    
    fun pauseTimer() {
        _gameState.value = _gameState.value.copy(isTimerPaused = true)
    }
    
    fun resumeTimer() {
        _gameState.value = _gameState.value.copy(isTimerPaused = false)
    }
    
    fun cancelTimer() {
        timerJob?.cancel()
        _gameState.value = _gameState.value.copy(
            isTimerActive = false,
            timeRemainingSeconds = null,
            isTimerPaused = false
        )
    }
    
    private fun onTimerExpired() {
        // Submit current color mix as is and calculate points normally
        val similarity = _gameState.value.similarity
        val points = calculatePoints(similarity)
        val timeBonus = calculateTimeBonus(similarity)  // Will be 0 since time ran out
        val totalPoints = points + timeBonus
        val newScore = (_gameState.value.currentScore + totalPoints).coerceAtLeast(0)

        val isSuccess = similarity >= 0.80f

        // Show the end-of-level dialog
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
}

