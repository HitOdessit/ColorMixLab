package com.colormixlab.utils

import com.colormixlab.game.Difficulty
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for MathChallengeTimer utility class.
 * Tests timer configuration logic for different difficulty levels.
 */
class MathChallengeTimerTest {

    @Test
    fun `getTimerDuration returns null for EASY difficulty`() {
        val duration = MathChallengeTimer.getTimerDuration(Difficulty.EASY)

        assertNull("Easy mode should have no timer", duration)
    }

    @Test
    fun `getTimerDuration returns 20 seconds for MEDIUM difficulty`() {
        val duration = MathChallengeTimer.getTimerDuration(Difficulty.MEDIUM)

        assertEquals("Medium mode should have 20 second timer", 20, duration)
    }

    @Test
    fun `getTimerDuration returns 10 seconds for HARD difficulty`() {
        val duration = MathChallengeTimer.getTimerDuration(Difficulty.HARD)

        assertEquals("Hard mode should have 10 second timer", 10, duration)
    }

    @Test
    fun `isTimerEnabled returns false for EASY difficulty`() {
        val isEnabled = MathChallengeTimer.isTimerEnabled(Difficulty.EASY)

        assertFalse("Timer should not be enabled for EASY mode", isEnabled)
    }

    @Test
    fun `isTimerEnabled returns true for MEDIUM difficulty`() {
        val isEnabled = MathChallengeTimer.isTimerEnabled(Difficulty.MEDIUM)

        assertTrue("Timer should be enabled for MEDIUM mode", isEnabled)
    }

    @Test
    fun `isTimerEnabled returns true for HARD difficulty`() {
        val isEnabled = MathChallengeTimer.isTimerEnabled(Difficulty.HARD)

        assertTrue("Timer should be enabled for HARD mode", isEnabled)
    }

    @Test
    fun `getWarningThreshold returns 5 seconds`() {
        val threshold = MathChallengeTimer.getWarningThreshold()

        assertEquals("Warning threshold should be 5 seconds", 5, threshold)
    }

    @Test
    fun `getCriticalThreshold returns 10 seconds`() {
        val threshold = MathChallengeTimer.getCriticalThreshold()

        assertEquals("Critical threshold should be 10 seconds", 10, threshold)
    }

    @Test
    fun `warning threshold is less than MEDIUM timer duration`() {
        val warningThreshold = MathChallengeTimer.getWarningThreshold()
        val mediumDuration = MathChallengeTimer.getTimerDuration(Difficulty.MEDIUM)!!

        assertTrue(
            "Warning threshold should be less than MEDIUM duration",
            warningThreshold < mediumDuration
        )
    }

    @Test
    fun `critical threshold equals HARD timer duration`() {
        val criticalThreshold = MathChallengeTimer.getCriticalThreshold()
        val hardDuration = MathChallengeTimer.getTimerDuration(Difficulty.HARD)!!

        assertEquals(
            "Critical threshold should equal HARD duration",
            criticalThreshold,
            hardDuration
        )
    }
}
