package com.colormixlab.utils

import com.colormixlab.game.Difficulty

/**
 * Utility object for managing math challenge timer configuration.
 * Centralizes timer duration logic based on difficulty level.
 */
object MathChallengeTimer {

    /**
     * Get the timer duration in seconds for a given difficulty level.
     *
     * @param difficulty The difficulty level
     * @return Timer duration in seconds, or null if no timer should be used
     */
    fun getTimerDuration(difficulty: Difficulty): Int? {
        return when (difficulty) {
            Difficulty.EASY -> null  // No timer for easy mode
            Difficulty.MEDIUM -> 20   // 20 seconds for medium
            Difficulty.HARD -> 10     // 10 seconds for hard
        }
    }

    /**
     * Check if timer should be active for given difficulty.
     *
     * @param difficulty The difficulty level
     * @return true if timer should be active, false otherwise
     */
    fun isTimerEnabled(difficulty: Difficulty): Boolean {
        return getTimerDuration(difficulty) != null
    }

    /**
     * Get the warning threshold in seconds (when timer turns red).
     *
     * @return Number of seconds remaining when warning should be shown
     */
    fun getWarningThreshold(): Int = 5

    /**
     * Get the critical threshold in seconds (for additional feedback).
     *
     * @return Number of seconds remaining for critical warning
     */
    fun getCriticalThreshold(): Int = 10
}
