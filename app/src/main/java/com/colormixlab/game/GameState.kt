package com.colormixlab.game

import androidx.compose.ui.graphics.Color
import com.colormixlab.model.GameColor

enum class Difficulty {
    EASY,    // No timer, 0.5x points
    MEDIUM,  // 40s, 1.0x points  
    HARD     // 20s, 1.5x points
}

data class GameState(
    val currentLevel: Int = 1,
    val targetColor: Color = Color.White,
    val targetRecipe: Map<GameColor, Int> = emptyMap(),
    val mixedColor: Color = Color.White,
    val drops: Map<GameColor, Int> = emptyMap(),
    val unlockedColors: List<GameColor> = GameColor.getAvailableColors(1),
    val isMatched: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val similarity: Float = 0f,
    val currentScore: Int = 0,
    val hasCheckedThisRound: Boolean = false,
    val isGameCompleted: Boolean = false,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val timeRemainingSeconds: Int? = null,
    val isTimerActive: Boolean = false,
    val isTimerPaused: Boolean = false
) {
    fun getDropCount(color: GameColor): Int = drops[color] ?: 0
    
    fun getTotalDrops(): Int = drops.values.sum()
    
    companion object {
        const val MAX_LEVEL = 30
        
        fun getTimerDuration(difficulty: Difficulty): Int? {
            return when (difficulty) {
                Difficulty.EASY -> null  // No timer
                Difficulty.MEDIUM -> 40
                Difficulty.HARD -> 20
            }
        }
    }
}

