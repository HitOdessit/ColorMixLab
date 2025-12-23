package com.colormixlab.game

import androidx.compose.ui.graphics.Color
import com.colormixlab.model.GameColor
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for GameState helper methods and configuration.
 */
class GameStateTest {

    @Test
    fun `getDropCount returns 0 for color not in drops`() {
        val state = GameState(drops = emptyMap())

        val count = state.getDropCount(GameColor.Red)

        assertEquals("Count should be 0 for missing color", 0, count)
    }

    @Test
    fun `getDropCount returns correct count for color in drops`() {
        val state = GameState(drops = mapOf(GameColor.Red to 5))

        val count = state.getDropCount(GameColor.Red)

        assertEquals("Count should match drops map", 5, count)
    }

    @Test
    fun `getTotalDrops returns 0 for empty drops`() {
        val state = GameState(drops = emptyMap())

        val total = state.getTotalDrops()

        assertEquals("Total should be 0 for empty drops", 0, total)
    }

    @Test
    fun `getTotalDrops returns sum of all drops`() {
        val state = GameState(
            drops = mapOf(
                GameColor.Red to 3,
                GameColor.Blue to 5,
                GameColor.Green to 2
            )
        )

        val total = state.getTotalDrops()

        assertEquals("Total should sum all drop counts", 10, total)
    }

    @Test
    fun `getTotalDrops with single color`() {
        val state = GameState(drops = mapOf(GameColor.Red to 7))

        val total = state.getTotalDrops()

        assertEquals("Total should equal single color count", 7, total)
    }

    @Test
    fun `getTimerDuration returns null for EASY difficulty`() {
        val duration = GameState.getTimerDuration(Difficulty.EASY)

        assertNull("EASY mode should have no timer", duration)
    }

    @Test
    fun `getTimerDuration returns 40 seconds for MEDIUM difficulty`() {
        val duration = GameState.getTimerDuration(Difficulty.MEDIUM)

        assertEquals("MEDIUM mode should have 40 second timer", 40, duration)
    }

    @Test
    fun `getTimerDuration returns 20 seconds for HARD difficulty`() {
        val duration = GameState.getTimerDuration(Difficulty.HARD)

        assertEquals("HARD mode should have 20 second timer", 20, duration)
    }

    @Test
    fun `default GameState has level 1`() {
        val state = GameState()

        assertEquals("Default level should be 1", 1, state.currentLevel)
    }

    @Test
    fun `default GameState has score 0`() {
        val state = GameState()

        assertEquals("Default score should be 0", 0, state.currentScore)
    }

    @Test
    fun `default GameState has MEDIUM difficulty`() {
        val state = GameState()

        assertEquals("Default difficulty should be MEDIUM", Difficulty.MEDIUM, state.difficulty)
    }

    @Test
    fun `default GameState has white target color`() {
        val state = GameState()

        assertEquals("Default target should be white", Color.White, state.targetColor)
    }

    @Test
    fun `default GameState has white mixed color`() {
        val state = GameState()

        assertEquals("Default mixed color should be white", Color.White, state.mixedColor)
    }

    @Test
    fun `default GameState has empty drops`() {
        val state = GameState()

        assertTrue("Default drops should be empty", state.drops.isEmpty())
    }

    @Test
    fun `default GameState has similarity 0`() {
        val state = GameState()

        assertEquals("Default similarity should be 0", 0f, state.similarity, 0.001f)
    }

    @Test
    fun `default GameState is not matched`() {
        val state = GameState()

        assertFalse("Default state should not be matched", state.isMatched)
    }

    @Test
    fun `default GameState does not show success dialog`() {
        val state = GameState()

        assertFalse("Default state should not show success dialog", state.showSuccessDialog)
    }

    @Test
    fun `default GameState has not checked this round`() {
        val state = GameState()

        assertFalse("Default state should not have checked this round", state.hasCheckedThisRound)
    }

    @Test
    fun `default GameState is not game completed`() {
        val state = GameState()

        assertFalse("Default state should not be game completed", state.isGameCompleted)
    }

    @Test
    fun `default GameState has base colors unlocked`() {
        val state = GameState()

        assertTrue("Base colors should be unlocked at level 1", state.unlockedColors.isNotEmpty())
        assertTrue("Red should be available", state.unlockedColors.any { it.name == "Red" })
        assertTrue("Blue should be available", state.unlockedColors.any { it.name == "Blue" })
        assertTrue("Green should be available", state.unlockedColors.any { it.name == "Green" })
    }

    @Test
    fun `MAX_LEVEL is 30`() {
        assertEquals("Maximum level should be 30", 30, GameState.MAX_LEVEL)
    }

    @Test
    fun `default GameState timer is not active`() {
        val state = GameState()

        assertFalse("Timer should not be active by default", state.isTimerActive)
    }

    @Test
    fun `default GameState timer is not paused`() {
        val state = GameState()

        assertFalse("Timer should not be paused by default", state.isTimerPaused)
    }

    @Test
    fun `default GameState has no math challenge`() {
        val state = GameState()

        assertFalse("Should not need math challenge by default", state.needsMathChallenge)
    }

    @Test
    fun `default GameState has NONE math challenge type`() {
        val state = GameState()

        assertEquals(
            "Default math challenge type should be NONE",
            MathChallengeType.NONE,
            state.mathChallengeType
        )
    }

    @Test
    fun `getDropCount handles multiple colors`() {
        val state = GameState(
            drops = mapOf(
                GameColor.Red to 3,
                GameColor.Blue to 7,
                GameColor.Green to 2
            )
        )

        assertEquals(3, state.getDropCount(GameColor.Red))
        assertEquals(7, state.getDropCount(GameColor.Blue))
        assertEquals(2, state.getDropCount(GameColor.Green))
    }
}
