package com.colormixlab.game

/**
 * Centralized game constants.
 *
 * All gameplay-tuning values live here so a single edit cascades through the
 * controller, scoring, leaderboard, and UI. Adding a new color tier or
 * adjusting balance should never require touching multiple files.
 */
object GameConstants {

    /** Minimum similarity required to count as a successful match (0.0 - 1.0). */
    const val MATCH_SUCCESS_THRESHOLD = 0.80f

    /** Levels at which a math challenge unlocks a new color tier. */
    val COLOR_UNLOCK_LEVELS = listOf(4, 7, 10, 13, 16, 19)

    /** First level eligible for milestone challenges (every 3 levels after this). */
    const val MILESTONE_START_LEVEL = 19

    /** Spacing between milestone challenges, after [MILESTONE_START_LEVEL]. */
    const val MILESTONE_INTERVAL = 3

    /** Score penalty applied for each wrong math challenge answer. */
    const val WRONG_MATH_ANSWER_PENALTY = 75

    /** Maximum time bonus awarded for fast successful matches. */
    const val MAX_TIME_BONUS = 50

    /** Difficulty multipliers applied to base points. */
    const val EASY_MULTIPLIER = 0.75f
    const val MEDIUM_MULTIPLIER = 1.0f
    const val HARD_MULTIPLIER = 1.25f

    /** Maximum entries retained in the local leaderboard. */
    const val LEADERBOARD_MAX_ENTRIES = 100

    /** Default page size for leaderboard queries. */
    const val LEADERBOARD_DEFAULT_PAGE_SIZE = 5
}
