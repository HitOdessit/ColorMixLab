package com.colormixlab.game

import com.colormixlab.model.GameColor
import com.colormixlab.model.PlatformColor

enum class Difficulty {
    EASY,    // No timer, 0.75x points
    MEDIUM,  // 40s, 1.0x points
    HARD     // 20s, 1.25x points
}

enum class MathChallengeType {
    NONE,           // No challenge needed
    COLOR_UNLOCK,   // Before unlocking new color (3 questions)
    MILESTONE       // Every 3 levels after level 19 (3 questions)
}

data class GameState(
    val currentLevel: Int = 1,
    val targetColor: PlatformColor = PlatformColor.White,
    val targetRecipe: Map<GameColor, Int> = emptyMap(),
    val mixedColor: PlatformColor = PlatformColor.White,
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
    val isTimerPaused: Boolean = false,
    val lastBasePoints: Int = 0,
    val lastTimeBonus: Int = 0,
    val needsMathChallenge: Boolean = false,
    val mathChallengeType: MathChallengeType = MathChallengeType.NONE,
    val mathChallengeCompleted: Boolean = false
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
