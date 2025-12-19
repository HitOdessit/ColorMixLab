package com.colormixlab.game

import androidx.compose.ui.graphics.Color
import com.colormixlab.model.GameColor
import kotlin.random.Random

object LevelManager {
    /**
     * Generate a target color for a given level.
     * All levels require at least 2 colors mixed together.
     * Higher levels have more complex mixes.
     */
    fun generateTargetColor(level: Int): Pair<Color, Map<GameColor, Int>> {
        val availableColors = GameColor.getAvailableColors(level)
        
        return when {
            level <= 3 -> generateSimpleTarget(availableColors)
            level <= 9 -> generateMediumTarget(availableColors)
            level <= 15 -> generateComplexTarget(availableColors)
            else -> generateAdvancedTarget(availableColors)
        }
    }
    
    private fun generateSimpleTarget(colors: List<GameColor>): Pair<Color, Map<GameColor, Int>> {
        // Levels 1-3: 2-color mixes only
        val color1 = colors.random()
        val color2 = colors.filter { it != color1 }.random()
        val drops1 = Random.nextInt(1, 4)
        val drops2 = Random.nextInt(1, 4)
        val map = mapOf(color1 to drops1, color2 to drops2)
        return Pair(ColorMixer.mixColors(map), map)
    }
    
    private fun generateMediumTarget(colors: List<GameColor>): Pair<Color, Map<GameColor, Int>> {
        // Levels 4-9: 2-3 color mixes
        val numColors = Random.nextInt(2, 4).coerceAtMost(colors.size)
        val selectedColors = colors.shuffled().take(numColors)
        
        val map = selectedColors.associateWith { Random.nextInt(1, 5) }
        return Pair(ColorMixer.mixColors(map), map)
    }
    
    private fun generateComplexTarget(colors: List<GameColor>): Pair<Color, Map<GameColor, Int>> {
        // Levels 10-15: 2-4 color mixes
        val numColors = Random.nextInt(2, 5).coerceAtMost(colors.size)
        val selectedColors = colors.shuffled().take(numColors)
        
        val map = selectedColors.associateWith { Random.nextInt(1, 6) }
        return Pair(ColorMixer.mixColors(map), map)
    }
    
    private fun generateAdvancedTarget(colors: List<GameColor>): Pair<Color, Map<GameColor, Int>> {
        // Levels 16+: 2-5 color mixes with all available colors
        val numColors = Random.nextInt(2, 6).coerceAtMost(colors.size)
        val selectedColors = colors.shuffled().take(numColors)
        
        val map = selectedColors.associateWith { Random.nextInt(1, 7) }
        return Pair(ColorMixer.mixColors(map), map)
    }
    
    /**
     * Get tolerance for color matching based on level.
     * Earlier levels are more forgiving.
     */
    fun getToleranceForLevel(level: Int): Float {
        return when {
            level <= 3 -> 0.20f  // Very forgiving
            level <= 6 -> 0.17f  // Forgiving
            level <= 9 -> 0.15f  // Moderate
            else -> 0.12f        // Stricter
        }
    }
}

