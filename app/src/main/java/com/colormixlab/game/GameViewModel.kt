package com.colormixlab.game

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.colormixlab.model.GameColor

class GameViewModel : ViewModel() {
    private val _gameState = mutableStateOf(GameState())
    val gameState: State<GameState> = _gameState
    
    init {
        startNewLevel()
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
        val newScore = (_gameState.value.currentScore + points).coerceAtLeast(0)
        
        val isSuccess = similarity >= 0.80f  // Adjusted to 80%
        
        _gameState.value = _gameState.value.copy(
            isMatched = isSuccess,
            showSuccessDialog = true,
            hasCheckedThisRound = true,
            currentScore = newScore
        )
    }
    
    fun calculatePoints(similarity: Float): Int {
        return when {
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
            _gameState.value = _gameState.value.copy(
                showSuccessDialog = false,
                isGameCompleted = true
            )
            return
        }
        
        val newLevel = currentLevel + 1
        val (targetColor, recipe) = LevelManager.generateTargetColor(newLevel)
        
        _gameState.value = _gameState.value.copy(
            currentLevel = newLevel,
            unlockedColors = GameColor.getAvailableColors(newLevel),
            targetColor = targetColor,
            targetRecipe = recipe,
            mixedColor = androidx.compose.ui.graphics.Color.White,
            drops = emptyMap(),
            isMatched = false,
            showSuccessDialog = false,
            hasCheckedThisRound = false,
            similarity = 0f
        )
    }
    
    fun retryLevel() {
        val (targetColor, recipe) = LevelManager.generateTargetColor(_gameState.value.currentLevel)
        
        _gameState.value = _gameState.value.copy(
            targetColor = targetColor,
            targetRecipe = recipe,
            mixedColor = androidx.compose.ui.graphics.Color.White,
            drops = emptyMap(),
            showSuccessDialog = false,
            hasCheckedThisRound = false,
            similarity = 0f
        )
    }
    
    fun dismissSuccessDialog() {
        _gameState.value = _gameState.value.copy(showSuccessDialog = false)
    }
    
    private fun startNewLevel() {
        val (targetColor, recipe) = LevelManager.generateTargetColor(_gameState.value.currentLevel)
        
        _gameState.value = _gameState.value.copy(
            targetColor = targetColor,
            targetRecipe = recipe,
            mixedColor = androidx.compose.ui.graphics.Color.White,
            drops = emptyMap(),
            isMatched = false,
            showSuccessDialog = false,
            similarity = 0f
        )
    }
    
    fun resetGame() {
        _gameState.value = GameState()
        startNewLevel()
    }
    
    fun completeGame() {
        _gameState.value = _gameState.value.copy(
            isGameCompleted = false
        )
    }
}

