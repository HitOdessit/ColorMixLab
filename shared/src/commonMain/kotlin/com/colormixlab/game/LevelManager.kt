package com.colormixlab.game

import com.colormixlab.model.GameColor
import com.colormixlab.model.PlatformColor
import kotlin.random.Random

object LevelManager {
    /**
     * Generate a target color for a given level.
     * All levels require at least 2 colors mixed together.
     * Higher levels have more complex mixes.
     * Ensures the new target is different from the previous one.
     */
    fun generateTargetColor(level: Int, previousTarget: PlatformColor? = null): Pair<PlatformColor, Map<GameColor, Int>> {
        val availableColors = GameColor.getAvailableColors(level)

        var result = when {
            level <= 3 -> generateSimpleTarget(availableColors)
            level <= 9 -> generateMediumTarget(availableColors)
            level <= 15 -> generateComplexTarget(availableColors)
            else -> generateAdvancedTarget(availableColors)
        }

        // If we have a previous target and it matches the new one, regenerate
        var attempts = 0
        while (previousTarget != null && colorsAreSame(result.first, previousTarget) && attempts < 10) {
            result = when {
                level <= 3 -> generateSimpleTarget(availableColors)
                level <= 9 -> generateMediumTarget(availableColors)
                level <= 15 -> generateComplexTarget(availableColors)
                else -> generateAdvancedTarget(availableColors)
            }
            attempts++
        }

        return result
    }

    /**
     * Check if two colors are effectively the same
     */
    private fun colorsAreSame(color1: PlatformColor, color2: PlatformColor): Boolean {
        return color1.red == color2.red &&
               color1.green == color2.green &&
               color1.blue == color2.blue
    }

    private fun generateSimpleTarget(colors: List<GameColor>): Pair<PlatformColor, Map<GameColor, Int>> {
        // Levels 1-3: 2-color mixes only
        val color1 = colors.random()
        val color2 = colors.filter { it != color1 }.random()
        val drops1 = Random.nextInt(1, 4)
        val drops2 = Random.nextInt(1, 4)
        val map = mapOf(color1 to drops1, color2 to drops2)
        return Pair(ColorMixer.mixColors(map), map)
    }

    private fun generateMediumTarget(colors: List<GameColor>): Pair<PlatformColor, Map<GameColor, Int>> {
        // Levels 4-9: 2-3 color mixes
        val numColors = Random.nextInt(2, 4).coerceAtMost(colors.size)
        val selectedColors = colors.shuffled().take(numColors)

        val map = selectedColors.associateWith { Random.nextInt(1, 5) }
        return Pair(ColorMixer.mixColors(map), map)
    }

    private fun generateComplexTarget(colors: List<GameColor>): Pair<PlatformColor, Map<GameColor, Int>> {
        // Levels 10-15: 2-4 color mixes
        val numColors = Random.nextInt(2, 5).coerceAtMost(colors.size)
        val selectedColors = colors.shuffled().take(numColors)

        val map = selectedColors.associateWith { Random.nextInt(1, 6) }
        return Pair(ColorMixer.mixColors(map), map)
    }

    private fun generateAdvancedTarget(colors: List<GameColor>): Pair<PlatformColor, Map<GameColor, Int>> {
        // Levels 16+: 2-5 color mixes with all available colors
        val numColors = Random.nextInt(2, 6).coerceAtMost(colors.size)
        val selectedColors = colors.shuffled().take(numColors)

        val map = selectedColors.associateWith { Random.nextInt(1, 7) }
        return Pair(ColorMixer.mixColors(map), map)
    }
}
