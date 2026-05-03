package com.colormixlab.game

import com.colormixlab.model.GameColor
import com.colormixlab.model.PlatformColor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameControllerTest {

    private lateinit var controller: GameController

    @Before
    fun setup() {
        GameColor.resetColors()
        controller = GameController(Difficulty.EASY)
    }

    @Test
    fun `initial state has level 1 with a target color`() {
        val state = controller.gameState.value

        assertEquals(1, state.currentLevel)
        assertNotEquals(PlatformColor.White, state.targetColor)
        assertTrue(state.targetRecipe.isNotEmpty())
        assertEquals(0, state.currentScore)
    }

    @Test
    fun `addColorDrop updates drops and mixed color`() {
        controller.addColorDrop(GameColor.Red)

        val state = controller.gameState.value

        assertEquals(1, state.getDropCount(GameColor.Red))
        assertEquals(1, state.getTotalDrops())
        assertNotEquals(PlatformColor.White, state.mixedColor)
    }

    @Test
    fun `addColorDrop accumulates drops for same color`() {
        controller.addColorDrop(GameColor.Red)
        controller.addColorDrop(GameColor.Red)
        controller.addColorDrop(GameColor.Red)

        assertEquals(3, controller.gameState.value.getDropCount(GameColor.Red))
    }

    @Test
    fun `addColorDrop updates similarity`() {
        val target = controller.gameState.value.targetColor
        val recipe = controller.gameState.value.targetRecipe

        recipe.forEach { (color, count) ->
            repeat(count) {
                controller.addColorDrop(color)
            }
        }

        assertTrue(controller.gameState.value.similarity > 0.9f)
    }

    @Test
    fun `clearBowl resets drops and mixed color`() {
        controller.addColorDrop(GameColor.Red)
        controller.addColorDrop(GameColor.Blue)

        controller.clearBowl()

        val state = controller.gameState.value
        assertTrue(state.drops.isEmpty())
        assertEquals(PlatformColor.White, state.mixedColor)
        assertEquals(0f, state.similarity, 0.001f)
    }

    @Test
    fun `checkMatch with good similarity succeeds`() {
        val recipe = controller.gameState.value.targetRecipe
        recipe.forEach { (color, count) ->
            repeat(count) { controller.addColorDrop(color) }
        }

        controller.checkMatch()

        val state = controller.gameState.value
        assertTrue(state.isMatched)
        assertTrue(state.showSuccessDialog)
        assertTrue(state.hasCheckedThisRound)
        assertTrue(state.currentScore > 0)
    }

    @Test
    fun `checkMatch with low similarity fails`() {
        controller.addColorDrop(GameColor.Red)

        controller.checkMatch()

        val state = controller.gameState.value
        assertTrue(state.showSuccessDialog)
        assertTrue(state.hasCheckedThisRound)
    }

    @Test
    fun `checkMatch prevents double-checking`() {
        controller.addColorDrop(GameColor.Red)
        controller.checkMatch()
        val scoreAfterFirst = controller.gameState.value.currentScore

        controller.checkMatch()

        assertEquals(scoreAfterFirst, controller.gameState.value.currentScore)
    }

    @Test
    fun `nextLevel advances to next level`() {
        val recipe = controller.gameState.value.targetRecipe
        recipe.forEach { (color, count) ->
            repeat(count) { controller.addColorDrop(color) }
        }
        controller.checkMatch()

        controller.nextLevel()

        val state = controller.gameState.value
        assertEquals(2, state.currentLevel)
        assertTrue(state.drops.isEmpty())
        assertFalse(state.showSuccessDialog)
        assertFalse(state.hasCheckedThisRound)
    }

    @Test
    fun `nextLevel triggers math challenge at color unlock levels`() {
        val colorUnlockLevels = listOf(4, 7, 10, 13, 16, 19)

        for (targetLevel in colorUnlockLevels) {
            GameColor.resetColors()
            val ctrl = GameController(Difficulty.EASY)

            // Advance to the level just before the unlock
            while (ctrl.gameState.value.currentLevel < targetLevel - 1) {
                val r = ctrl.gameState.value.targetRecipe
                r.forEach { (c, n) -> repeat(n) { ctrl.addColorDrop(c) } }
                ctrl.checkMatch()
                ctrl.nextLevel()
                if (ctrl.gameState.value.needsMathChallenge) {
                    ctrl.completeMathChallenge()
                }
            }

            // Complete the current level and advance
            val r = ctrl.gameState.value.targetRecipe
            r.forEach { (c, n) -> repeat(n) { ctrl.addColorDrop(c) } }
            ctrl.checkMatch()
            ctrl.nextLevel()

            assertTrue(
                "Level $targetLevel should trigger math challenge",
                ctrl.gameState.value.needsMathChallenge
            )
        }
    }

    @Test
    fun `nextLevel at MAX_LEVEL completes the game`() {
        GameColor.resetColors()
        val ctrl = GameController(Difficulty.EASY)

        // Advance to max level
        while (ctrl.gameState.value.currentLevel < GameState.MAX_LEVEL) {
            val r = ctrl.gameState.value.targetRecipe
            r.forEach { (c, n) -> repeat(n) { ctrl.addColorDrop(c) } }
            ctrl.checkMatch()
            ctrl.nextLevel()
            if (ctrl.gameState.value.needsMathChallenge) {
                ctrl.completeMathChallenge()
            }
        }

        // Complete the final level
        val r = ctrl.gameState.value.targetRecipe
        r.forEach { (c, n) -> repeat(n) { ctrl.addColorDrop(c) } }
        ctrl.checkMatch()
        ctrl.nextLevel()

        assertTrue(ctrl.gameState.value.isGameCompleted)
        assertTrue(ctrl.gameState.value.completedAllLevels)
    }

    @Test
    fun `retryLevel resets drops but keeps level and score`() {
        controller.addColorDrop(GameColor.Red)
        controller.checkMatch()
        val score = controller.gameState.value.currentScore
        val level = controller.gameState.value.currentLevel

        controller.retryLevel()

        val state = controller.gameState.value
        assertEquals(level, state.currentLevel)
        assertEquals(score, state.currentScore)
        assertTrue(state.drops.isEmpty())
        assertFalse(state.hasCheckedThisRound)
        assertEquals(0f, state.similarity, 0.001f)
    }

    @Test
    fun `resetGame returns to level 1 with score 0`() {
        val recipe = controller.gameState.value.targetRecipe
        recipe.forEach { (color, count) ->
            repeat(count) { controller.addColorDrop(color) }
        }
        controller.checkMatch()
        controller.nextLevel()

        controller.resetGame()

        val state = controller.gameState.value
        assertEquals(1, state.currentLevel)
        assertEquals(0, state.currentScore)
        assertTrue(state.drops.isEmpty())
        assertFalse(state.isGameCompleted)
    }

    @Test
    fun `calculatePoints returns positive for good match`() {
        val points = controller.calculatePoints(0.95f)

        assertTrue(points > 0)
    }

    @Test
    fun `calculatePoints returns negative for poor match`() {
        val points = controller.calculatePoints(0.40f)

        assertTrue(points < 0)
    }

    @Test
    fun `calculatePoints scales with difficulty`() {
        val easyCtrl = GameController(Difficulty.EASY)
        val hardCtrl = GameController(Difficulty.HARD)

        val easyPoints = easyCtrl.calculatePoints(0.95f)
        val hardPoints = hardCtrl.calculatePoints(0.95f)

        assertTrue(hardPoints > easyPoints)
    }

    @Test
    fun `getResultMessage returns appropriate messages`() {
        assertEquals("Perfect Match!", controller.getResultMessage(1.0f))
        assertEquals("Nice Work!", controller.getResultMessage(0.80f))
        assertEquals("Try Harder!", controller.getResultMessage(0.30f))
    }

    @Test
    fun `getResultEmoji returns appropriate emojis`() {
        assertEquals("🎉", controller.getResultEmoji(1.0f))
        assertEquals("😢", controller.getResultEmoji(0.30f))
    }

    @Test
    fun `tickTimer decrements time`() {
        GameColor.resetColors()
        val ctrl = GameController(Difficulty.MEDIUM)
        val initialTime = ctrl.gameState.value.timeRemainingSeconds!!

        ctrl.tickTimer()

        assertEquals(initialTime - 1, ctrl.gameState.value.timeRemainingSeconds)
    }

    @Test
    fun `tickTimer does nothing when paused`() {
        GameColor.resetColors()
        val ctrl = GameController(Difficulty.MEDIUM)
        val initialTime = ctrl.gameState.value.timeRemainingSeconds!!

        ctrl.pauseTimer()
        ctrl.tickTimer()

        assertEquals(initialTime, ctrl.gameState.value.timeRemainingSeconds)
    }

    @Test
    fun `tickTimer triggers expiry at zero`() {
        GameColor.resetColors()
        val ctrl = GameController(Difficulty.HARD)

        val duration = ctrl.gameState.value.timeRemainingSeconds!!
        repeat(duration) { ctrl.tickTimer() }

        assertTrue(ctrl.gameState.value.showSuccessDialog)
        assertFalse(ctrl.gameState.value.isTimerActive)
    }

    @Test
    fun `forceFinishGame sets completion without all-levels flag`() {
        controller.forceFinishGame()

        val state = controller.gameState.value
        assertTrue(state.isGameCompleted)
        assertFalse(state.completedAllLevels)
        assertFalse(state.isTimerActive)
    }

    @Test
    fun `completeMathChallenge unlocks colors and starts timer`() {
        GameColor.resetColors()
        val ctrl = GameController(Difficulty.MEDIUM)

        // Advance to level 4 (math challenge)
        while (ctrl.gameState.value.currentLevel < 3) {
            val r = ctrl.gameState.value.targetRecipe
            r.forEach { (c, n) -> repeat(n) { ctrl.addColorDrop(c) } }
            ctrl.checkMatch()
            ctrl.nextLevel()
        }
        val r = ctrl.gameState.value.targetRecipe
        r.forEach { (c, n) -> repeat(n) { ctrl.addColorDrop(c) } }
        ctrl.checkMatch()
        ctrl.nextLevel()

        assertTrue(ctrl.gameState.value.needsMathChallenge)
        val colorsBefore = ctrl.gameState.value.unlockedColors.size

        ctrl.completeMathChallenge()

        val state = ctrl.gameState.value
        assertFalse(state.needsMathChallenge)
        assertTrue(state.mathChallengeCompleted)
        assertTrue(state.unlockedColors.size > colorsBefore)
        assertTrue(state.isTimerActive)
    }

    @Test
    fun `deductPointsForWrongMathAnswer reduces score`() {
        val recipe = controller.gameState.value.targetRecipe
        recipe.forEach { (color, count) ->
            repeat(count) { controller.addColorDrop(color) }
        }
        controller.checkMatch()
        val scoreBefore = controller.gameState.value.currentScore

        controller.deductPointsForWrongMathAnswer()

        assertTrue(controller.gameState.value.currentScore < scoreBefore)
    }

    @Test
    fun `score cannot go below zero`() {
        repeat(10) {
            controller.deductPointsForWrongMathAnswer()
        }

        assertEquals(0, controller.gameState.value.currentScore)
    }

    @Test
    fun `setDifficulty updates difficulty`() {
        controller.setDifficulty(Difficulty.HARD)

        assertEquals(Difficulty.HARD, controller.gameState.value.difficulty)
    }

    @Test
    fun `pauseTimer and resumeTimer toggle pause state`() {
        GameColor.resetColors()
        val ctrl = GameController(Difficulty.MEDIUM)

        ctrl.pauseTimer()
        assertTrue(ctrl.gameState.value.isTimerPaused)

        ctrl.resumeTimer()
        assertFalse(ctrl.gameState.value.isTimerPaused)
    }
}
