package com.colormixlab.game

import androidx.compose.ui.graphics.Color
import com.colormixlab.model.GameColor

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
    val hasCheckedThisRound: Boolean = false
) {
    fun getDropCount(color: GameColor): Int = drops[color] ?: 0
    
    fun getTotalDrops(): Int = drops.values.sum()
}

