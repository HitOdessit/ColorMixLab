package com.colormixlab.game

import com.colormixlab.model.GameColor
import com.colormixlab.model.PlatformColor
import kotlin.math.sqrt

object ColorMixer {
    /**
     * Mix colors dynamically based on RGB averaging.
     * Works with any colors in the game.
     */
    fun mixColors(drops: Map<GameColor, Int>): PlatformColor {
        val totalDrops = drops.values.sum()

        if (totalDrops == 0) {
            return PlatformColor.White // Empty bowl
        }

        // Convert each color to RGB components and average them
        var r = 0f
        var g = 0f
        var b = 0f

        // Iterate through all drops and add their RGB contributions
        drops.forEach { (gameColor, count) ->
            r += gameColor.color.redFloat * count
            g += gameColor.color.greenFloat * count
            b += gameColor.color.blueFloat * count
        }

        // Average
        r /= totalDrops
        g /= totalDrops
        b /= totalDrops

        return PlatformColor(
            red = (r * 255).toInt(),
            green = (g * 255).toInt(),
            blue = (b * 255).toInt()
        )
    }

    /**
     * Check if two colors match within a tolerance.
     * Uses Euclidean distance in RGB space.
     */
    fun colorsMatch(target: PlatformColor, mixed: PlatformColor, tolerance: Float = 0.15f): Boolean {
        val rDiff = target.redFloat - mixed.redFloat
        val gDiff = target.greenFloat - mixed.greenFloat
        val bDiff = target.blueFloat - mixed.blueFloat

        val distance = sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff)

        return distance <= tolerance
    }

    /**
     * Calculate how close the mixed color is to the target (0.0 to 1.0)
     * where 1.0 is perfect match
     */
    fun calculateSimilarity(target: PlatformColor, mixed: PlatformColor): Float {
        val rDiff = target.redFloat - mixed.redFloat
        val gDiff = target.greenFloat - mixed.greenFloat
        val bDiff = target.blueFloat - mixed.blueFloat

        val distance = sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff)
        val maxDistance = sqrt(3f) // Maximum possible distance in RGB cube

        return 1f - (distance / maxDistance)
    }
}
