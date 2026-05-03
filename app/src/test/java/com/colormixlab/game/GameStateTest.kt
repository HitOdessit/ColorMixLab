package com.colormixlab.game

import com.colormixlab.model.GameColor
import com.colormixlab.model.PlatformColor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameStateTest {

    @Before
    fun setup() {
        GameColor.initializeGameColors(42L)
    }

    @Test
    fun `getDropCount returns 0 for color not in drops`() {
        val state = GameState(drops = emptyMap())

        assertEquals(0, state.getDropCount(GameColor.Red))
    }

    @Test
    fun `getDropCount returns correct count for color in drops`() {
        val state = GameState(drops = mapOf(GameColor.Red to 5))

        assertEquals(5, state.getDropCount(GameColor.Red))
    }

    @Test
    fun `getTotalDrops returns 0 for empty drops`() {
        val state = GameState(drops = emptyMap())

        assertEquals(0, state.getTotalDrops())
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

        assertEquals(10, state.getTotalDrops())
    }

    @Test
    fun `getTotalDrops with single color`() {
        val state = GameState(drops = mapOf(GameColor.Red to 7))

        assertEquals(7, state.getTotalDrops())
    }

    @Test
    fun `getTimerDuration returns null for EASY difficulty`() {
        assertNull(GameState.getTimerDuration(Difficulty.EASY))
    }

    @Test
    fun `getTimerDuration returns 40 seconds for MEDIUM difficulty`() {
        assertEquals(40, GameState.getTimerDuration(Difficulty.MEDIUM))
    }

    @Test
    fun `getTimerDuration returns 20 seconds for HARD difficulty`() {
        assertEquals(20, GameState.getTimerDuration(Difficulty.HARD))
    }

    @Test
    fun `default GameState has level 1`() {
        assertEquals(1, GameState().currentLevel)
    }

    @Test
    fun `default GameState has score 0`() {
        assertEquals(0, GameState().currentScore)
    }

    @Test
    fun `default GameState has MEDIUM difficulty`() {
        assertEquals(Difficulty.MEDIUM, GameState().difficulty)
    }

    @Test
    fun `default GameState has white target color`() {
        assertEquals(PlatformColor.White, GameState().targetColor)
    }

    @Test
    fun `default GameState has white mixed color`() {
        assertEquals(PlatformColor.White, GameState().mixedColor)
    }

    @Test
    fun `default GameState has empty drops`() {
        assertTrue(GameState().drops.isEmpty())
    }

    @Test
    fun `default GameState has similarity 0`() {
        assertEquals(0f, GameState().similarity, 0.001f)
    }

    @Test
    fun `default GameState is not matched`() {
        assertFalse(GameState().isMatched)
    }

    @Test
    fun `default GameState does not show success dialog`() {
        assertFalse(GameState().showSuccessDialog)
    }

    @Test
    fun `default GameState has not checked this round`() {
        assertFalse(GameState().hasCheckedThisRound)
    }

    @Test
    fun `default GameState is not game completed`() {
        assertFalse(GameState().isGameCompleted)
    }

    @Test
    fun `default GameState has base colors unlocked`() {
        val state = GameState()

        assertTrue(state.unlockedColors.isNotEmpty())
        assertTrue(state.unlockedColors.any { it.name == "Red" })
        assertTrue(state.unlockedColors.any { it.name == "Blue" })
        assertTrue(state.unlockedColors.any { it.name == "Green" })
    }

    @Test
    fun `MAX_LEVEL is 30`() {
        assertEquals(30, GameState.MAX_LEVEL)
    }

    @Test
    fun `default GameState timer is not active`() {
        assertFalse(GameState().isTimerActive)
    }

    @Test
    fun `default GameState timer is not paused`() {
        assertFalse(GameState().isTimerPaused)
    }

    @Test
    fun `default GameState has no math challenge`() {
        assertFalse(GameState().needsMathChallenge)
    }

    @Test
    fun `default GameState has NONE math challenge type`() {
        assertEquals(MathChallengeType.NONE, GameState().mathChallengeType)
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
