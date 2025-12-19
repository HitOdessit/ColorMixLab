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
        
        val isSuccess = similarity >= 0.75f
        
        _gameState.value = _gameState.value.copy(
            isMatched = isSuccess,
            showSuccessDialog = true,
            hasCheckedThisRound = true,
            currentScore = newScore
        )
    }
    
    fun calculatePoints(similarity: Float): Int {
        return when {
            similarity >= 1.0f -> 150  // 100 + 50 bonus
            similarity >= 0.90f -> 80
            similarity >= 0.80f -> 60
            similarity >= 0.75f -> 40  // Pass threshold
            // Below 75% - graduated negative points
            similarity >= 0.70f -> -10  // Small penalty
            similarity >= 0.60f -> -20  // Medium penalty
            similarity >= 0.50f -> -30  // Large penalty
            similarity >= 0.40f -> -40  // Very large penalty
            else -> -50  // Maximum penalty
        }
    }
    
    fun getResultMessage(similarity: Float): String {
        return when {
            similarity >= 1.0f -> "Perfect Match!"
            similarity >= 0.90f -> "Excellent Mix!"
            similarity >= 0.80f -> "Great Job!"
            similarity >= 0.75f -> "Nice Work!"
            similarity >= 0.70f -> "Almost There!"
            similarity >= 0.60f -> "Keep Trying!"
            similarity >= 0.50f -> "Try Again!"
            else -> "Not Close Enough!"
        }
    }
    
    fun getResultEmoji(similarity: Float): String {
        return when {
            similarity >= 1.0f -> "🎉"
            similarity >= 0.90f -> "⭐"
            similarity >= 0.80f -> "👍"
            similarity >= 0.75f -> "👌"
            similarity >= 0.70f -> "🎨"
            similarity >= 0.60f -> "💪"
            similarity >= 0.50f -> "🔄"
            else -> "😕"
        }
    }
    
    fun nextLevel() {
        val newLevel = _gameState.value.currentLevel + 1
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
}

