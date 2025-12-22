package com.colormixlab.model

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

sealed class GameColor(val name: String, val rgb: Color, val unlockLevel: Int) {
    // Concrete implementation for dynamically created colors with custom unlock levels
    class Dynamic(name: String, rgb: Color, unlockLevel: Int) : GameColor(name, rgb, unlockLevel)
    
    // Level 1: Always available base colors
    object Red : GameColor("Red", Color(0xFFE74C3C), 1)
    object Blue : GameColor("Blue", Color(0xFF3498DB), 1)
    object Green : GameColor("Green", Color(0xFF2ECC71), 1)

    // Level 4: First unlockable tier
    object Yellow : GameColor("Yellow", Color(0xFFF1C40F), 4)
    object Cyan : GameColor("Cyan", Color(0xFF00BCD4), 4)
    object Gray : GameColor("Gray", Color(0xFF9E9E9E), 4)

    // Level 7: Second unlockable tier
    object Orange : GameColor("Orange", Color(0xFFE67E22), 7)
    object Magenta : GameColor("Magenta", Color(0xFFFF00FF), 7)
    object Coral : GameColor("Coral", Color(0xFFFF7F50), 7)

    // Level 10: Third unlockable tier
    object Purple : GameColor("Purple", Color(0xFF9B59B6), 10)
    object Lime : GameColor("Lime", Color(0xFFCDDC39), 10)
    object Turquoise : GameColor("Turquoise", Color(0xFF40E0D0), 10)

    // Level 13: Fourth unlockable tier
    object Pink : GameColor("Pink", Color(0xFFE91E63), 13)
    object Teal : GameColor("Teal", Color(0xFF009688), 13)

    // Level 16: Fifth unlockable tier
    object White : GameColor("White", Color(0xFFFFFFFF), 16)
    object Indigo : GameColor("Indigo", Color(0xFF3F51B5), 16)

    // Level 19: Sixth unlockable tier
    object Black : GameColor("Black", Color(0xFF000000), 19)
    object Brown : GameColor("Brown", Color(0xFF795548), 19)
    
    companion object {
        // Always available base colors
        private fun getBaseColors() = listOf(Red, Blue, Green)
        
        // Color tiers organized by unlock level
        private fun getColorsByTier(): Map<Int, List<GameColor>> = mapOf(
            4 to listOf(Yellow, Cyan, Gray),
            7 to listOf(Orange, Magenta, Coral),
            10 to listOf(Purple, Lime, Turquoise),
            13 to listOf(Pink, Teal),
            16 to listOf(White, Indigo),
            19 to listOf(Black, Brown)
        )
        
        // Selected unlockable colors for current game session (stored here to persist across levels)
        private var selectedUnlockableColors: List<GameColor>? = null
        
        /**
         * Initialize the game with 6 randomly selected unlockable colors.
         * Selects ONE color from each tier (levels 4, 7, 10, 13, 16, 19).
         * Call this at game start.
         */
        fun initializeGameColors(seed: Long = System.currentTimeMillis()): List<GameColor> {
            val random = Random(seed)
            val colorsByTier = getColorsByTier()
            
            // Select one random color from each tier
            selectedUnlockableColors = listOf(4, 7, 10, 13, 16, 19).map { level ->
                val tiersColors = colorsByTier[level] ?: emptyList()
                tiersColors.random(random)
            }
            
            return getAllColors()
        }
        
        fun getAllColors(): List<GameColor> {
            val colors = getBaseColors().toMutableList()
            selectedUnlockableColors?.let { colors.addAll(it) }
            return colors
        }
        
        fun getAvailableColors(level: Int): List<GameColor> {
            return getAllColors().filter { it.unlockLevel <= level }
        }
        
        /**
         * Reset the selected colors (for new game)
         */
        fun resetColors() {
            selectedUnlockableColors = null
        }
    }
}

