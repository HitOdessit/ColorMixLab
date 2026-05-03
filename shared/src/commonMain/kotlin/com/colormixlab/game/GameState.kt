package com.colormixlab.game

import com.colormixlab.model.GameColor
import com.colormixlab.model.PlatformColor

/**
 * Difficulty mode. Controls timer duration and scoring multiplier.
 */
enum class Difficulty {
    /** No timer; scoring multiplier 0.75x. Suited for the youngest players. */
    EASY,

    /** 40s per level + 20s math timer; scoring multiplier 1.0x. */
    MEDIUM,

    /** 20s per level + 10s math timer; scoring multiplier 1.25x. */
    HARD
}

/**
 * What kind of math challenge (if any) blocks the next level.
 */
enum class MathChallengeType {
    /** No challenge required. */
    NONE,

    /** Pre-color-unlock challenge: 3 correct multiplications to reveal a new color. */
    COLOR_UNLOCK,

    /** Recurring challenge every 3 levels after level 19 to keep difficulty rising. */
    MILESTONE
}

/**
 * Immutable snapshot of the entire game. Driven by [GameController] via a
 * [kotlinx.coroutines.flow.StateFlow]; consumed by both Android (Compose
 * `State`) and iOS (`@Published` ObservableObject).
 */
data class GameState(
    /** 1-indexed level number, capped at [MAX_LEVEL]. */
    val currentLevel: Int = 1,
    /** Color the player is trying to reproduce in the bowl. */
    val targetColor: PlatformColor = PlatformColor.White,
    /** Recipe of color-to-drop-count that produces [targetColor]. */
    val targetRecipe: Map<GameColor, Int> = emptyMap(),
    /** Color currently produced by mixing the [drops]. */
    val mixedColor: PlatformColor = PlatformColor.White,
    /** Current bowl contents: how many drops of each color. */
    val drops: Map<GameColor, Int> = emptyMap(),
    /** Colors the player can use at the current level. */
    val unlockedColors: List<GameColor> = GameColor.getAvailableColors(1),
    /** True when the last [GameController.checkMatch] call succeeded. */
    val isMatched: Boolean = false,
    /** Whether the post-check result dialog is visible. */
    val showSuccessDialog: Boolean = false,
    /** Similarity between [mixedColor] and [targetColor], 0.0 - 1.0. */
    val similarity: Float = 0f,
    /** Cumulative score for the current game. */
    val currentScore: Int = 0,
    /** Whether the player has already submitted a check this round (prevents double-scoring). */
    val hasCheckedThisRound: Boolean = false,
    /** True when the game has ended (naturally or via menu). */
    val isGameCompleted: Boolean = false,
    /** True only when level 30 was actually completed (vs. force-finish from menu). */
    val completedAllLevels: Boolean = false,
    /** Active difficulty for scoring and timer config. */
    val difficulty: Difficulty = Difficulty.MEDIUM,
    /** Seconds remaining on the level timer, or null if no timer (Easy mode). */
    val timeRemainingSeconds: Int? = null,
    /** Whether the timer is currently counting down. */
    val isTimerActive: Boolean = false,
    /** Whether the timer is paused (e.g., behind a dialog). */
    val isTimerPaused: Boolean = false,
    /** Base points awarded for the most recent match check. */
    val lastBasePoints: Int = 0,
    /** Time bonus added to the most recent match check. */
    val lastTimeBonus: Int = 0,
    /** True when a math challenge gates the next level. */
    val needsMathChallenge: Boolean = false,
    /** Type of active math challenge. */
    val mathChallengeType: MathChallengeType = MathChallengeType.NONE,
    /** True after the active math challenge has been completed successfully. */
    val mathChallengeCompleted: Boolean = false
) {
    /** How many drops of [color] are currently in the bowl. */
    fun getDropCount(color: GameColor): Int = drops[color] ?: 0

    /** Total drops in the bowl across all colors. */
    fun getTotalDrops(): Int = drops.values.sum()

    companion object {
        /** Final level. Reaching and completing this level wins the game. */
        const val MAX_LEVEL = 30

        /**
         * Per-level timer duration in seconds, or null for Easy (no timer).
         */
        fun getTimerDuration(difficulty: Difficulty): Int? {
            return when (difficulty) {
                Difficulty.EASY -> null
                Difficulty.MEDIUM -> 40
                Difficulty.HARD -> 20
            }
        }
    }
}
