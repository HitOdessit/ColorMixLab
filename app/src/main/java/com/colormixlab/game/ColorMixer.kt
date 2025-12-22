package com.colormixlab.game

import androidx.compose.ui.graphics.Color
import com.colormixlab.model.GameColor
import kotlin.math.sqrt

object ColorMixer {
    /**
     * Mix colors dynamically based on RGB averaging.
     * Works with any colors in the game.
     */
    fun mixColors(drops: Map<GameColor, Int>): Color {
        val totalDrops = drops.values.sum()
        
        if (totalDrops == 0) {
            return Color.White // Empty bowl
        }
        
        // Convert each color to RGB components and average them
        var r = 0f
        var g = 0f
        var b = 0f
        
        // Iterate through all drops and add their RGB contributions
        drops.forEach { (color, count) ->
            r += color.rgb.red * count
            g += color.rgb.green * count
            b += color.rgb.blue * count
        }
        
        // Average
        r /= totalDrops
        g /= totalDrops
        b /= totalDrops
        
        return Color(r, g, b)
    }
    
    /**
     * Check if two colors match within a tolerance.
     * Uses Euclidean distance in RGB space.
     */
    fun colorsMatch(target: Color, mixed: Color, tolerance: Float = 0.15f): Boolean {
        val rDiff = target.red - mixed.red
        val gDiff = target.green - mixed.green
        val bDiff = target.blue - mixed.blue
        
        val distance = sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff)
        
        return distance <= tolerance
    }
    
    /**
     * Calculate how close the mixed color is to the target (0.0 to 1.0)
     * where 1.0 is perfect match
     */
    fun calculateSimilarity(target: Color, mixed: Color): Float {
        val rDiff = target.red - mixed.red
        val gDiff = target.green - mixed.green
        val bDiff = target.blue - mixed.blue
        
        val distance = sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff)
        val maxDistance = sqrt(3f) // Maximum possible distance in RGB cube
        
        return 1f - (distance / maxDistance)
    }
}

