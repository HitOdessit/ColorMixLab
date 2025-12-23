package com.colormixlab.model

import kotlin.random.Random

sealed class GameColor(val name: String, val color: PlatformColor, val unlockLevel: Int) {
    // Concrete implementation for dynamically created colors with custom unlock levels
    class Dynamic(name: String, color: PlatformColor, unlockLevel: Int) : GameColor(name, color, unlockLevel)

    // Level 1: Always available base colors
    object Red : GameColor("Red", PlatformColor(0xFFE74C3Cu), 1)
    object Blue : GameColor("Blue", PlatformColor(0xFF3498DBu), 1)
    object Green : GameColor("Green", PlatformColor(0xFF2ECC71u), 1)

    // Level 4: First unlockable tier
    object Yellow : GameColor("Yellow", PlatformColor(0xFFF1C40Fu), 4)
    object Cyan : GameColor("Cyan", PlatformColor(0xFF00BCD4u), 4)
    object Gray : GameColor("Gray", PlatformColor(0xFF9E9E9Eu), 4)

    // Level 7: Second unlockable tier
    object Orange : GameColor("Orange", PlatformColor(0xFFE67E22u), 7)
    object Magenta : GameColor("Magenta", PlatformColor(0xFFFF00FFu), 7)
    object Coral : GameColor("Coral", PlatformColor(0xFFFF7F50u), 7)

    // Level 10: Third unlockable tier
    object Purple : GameColor("Purple", PlatformColor(0xFF9B59B6u), 10)
    object Lime : GameColor("Lime", PlatformColor(0xFFCDDC39u), 10)
    object Turquoise : GameColor("Turquoise", PlatformColor(0xFF40E0D0u), 10)

    // Level 13: Fourth unlockable tier
    object Pink : GameColor("Pink", PlatformColor(0xFFE91E63u), 13)
    object Teal : GameColor("Teal", PlatformColor(0xFF009688u), 13)

    // Level 16: Fifth unlockable tier
    object White : GameColor("White", PlatformColor(0xFFFFFFFFu), 16)
    object Indigo : GameColor("Indigo", PlatformColor(0xFF3F51B5u), 16)

    // Level 19: Sixth unlockable tier
    object Black : GameColor("Black", PlatformColor(0xFF000000u), 19)
    object Brown : GameColor("Brown", PlatformColor(0xFF795548u), 19)

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
        fun initializeGameColors(seed: Long): List<GameColor> {
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
