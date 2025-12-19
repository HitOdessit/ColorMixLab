package com.colormixlab.game

import androidx.compose.ui.graphics.Color
import com.colormixlab.model.GameColor
import kotlin.math.sqrt

object ColorMixer {
    /**
     * Mix colors based on the simplified educational model:
     * - Red + Yellow = Orange
     * - Red + Blue = Purple
     * - Blue + Yellow = Green
     * - All three = Brown
     */
    fun mixColors(drops: Map<GameColor, Int>): Color {
        val redDrops = drops[GameColor.Red] ?: 0
        val blueDrops = drops[GameColor.Blue] ?: 0
        val yellowDrops = drops[GameColor.Yellow] ?: 0
        val orangeDrops = drops[GameColor.Orange] ?: 0
        val purpleDrops = drops[GameColor.Purple] ?: 0
        val greenDrops = drops[GameColor.Green] ?: 0
        val pinkDrops = drops[GameColor.Pink] ?: 0
        val whiteDrops = drops[GameColor.White] ?: 0
        val blackDrops = drops[GameColor.Black] ?: 0
        
        val totalDrops = redDrops + blueDrops + yellowDrops + orangeDrops + purpleDrops + greenDrops + pinkDrops + whiteDrops + blackDrops
        
        if (totalDrops == 0) {
            return Color.White // Empty bowl
        }
        
        // Convert each color to RGB components and average them
        var r = 0f
        var g = 0f
        var b = 0f
        
        r += GameColor.Red.rgb.red * redDrops
        g += GameColor.Red.rgb.green * redDrops
        b += GameColor.Red.rgb.blue * redDrops
        
        r += GameColor.Blue.rgb.red * blueDrops
        g += GameColor.Blue.rgb.green * blueDrops
        b += GameColor.Blue.rgb.blue * blueDrops
        
        r += GameColor.Yellow.rgb.red * yellowDrops
        g += GameColor.Yellow.rgb.green * yellowDrops
        b += GameColor.Yellow.rgb.blue * yellowDrops
        
        r += GameColor.Orange.rgb.red * orangeDrops
        g += GameColor.Orange.rgb.green * orangeDrops
        b += GameColor.Orange.rgb.blue * orangeDrops
        
        r += GameColor.Purple.rgb.red * purpleDrops
        g += GameColor.Purple.rgb.green * purpleDrops
        b += GameColor.Purple.rgb.blue * purpleDrops
        
        r += GameColor.Green.rgb.red * greenDrops
        g += GameColor.Green.rgb.green * greenDrops
        b += GameColor.Green.rgb.blue * greenDrops
        
        r += GameColor.Pink.rgb.red * pinkDrops
        g += GameColor.Pink.rgb.green * pinkDrops
        b += GameColor.Pink.rgb.blue * pinkDrops
        
        r += GameColor.White.rgb.red * whiteDrops
        g += GameColor.White.rgb.green * whiteDrops
        b += GameColor.White.rgb.blue * whiteDrops
        
        r += GameColor.Black.rgb.red * blackDrops
        g += GameColor.Black.rgb.green * blackDrops
        b += GameColor.Black.rgb.blue * blackDrops
        
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

