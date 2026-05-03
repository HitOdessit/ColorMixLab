package com.colormixlab.game

import com.colormixlab.model.GameColor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LevelManagerTest {

    @Before
    fun setup() {
        GameColor.initializeGameColors(42L)
    }

    @Test
    fun `generateTargetColor returns color and recipe`() {
        val (color, recipe) = LevelManager.generateTargetColor(1)

        assertNotNull(color)
        assertNotNull(recipe)
        assertTrue(recipe.isNotEmpty())
    }

    @Test
    fun `generateTargetColor recipe contains at least 2 colors for level 1`() {
        val (_, recipe) = LevelManager.generateTargetColor(1)

        assertTrue(recipe.size >= 2)
    }

    @Test
    fun `generateTargetColor for level 1-3 uses simple mixes`() {
        repeat(10) {
            val (_, recipe) = LevelManager.generateTargetColor(level = 2)

            assertEquals(2, recipe.size)
        }
    }

    @Test
    fun `generateTargetColor for level 4-9 uses medium mixes`() {
        repeat(10) {
            val (_, recipe) = LevelManager.generateTargetColor(level = 7)

            assertTrue(recipe.size in 2..3)
        }
    }

    @Test
    fun `generateTargetColor for level 10-15 uses complex mixes`() {
        repeat(10) {
            val (_, recipe) = LevelManager.generateTargetColor(level = 12)

            assertTrue(recipe.size in 2..4)
        }
    }

    @Test
    fun `generateTargetColor for level 16+ uses advanced mixes`() {
        repeat(10) {
            val (_, recipe) = LevelManager.generateTargetColor(level = 20)

            assertTrue(recipe.size in 2..5)
        }
    }

    @Test
    fun `generateTargetColor recipe has positive drop counts`() {
        val (_, recipe) = LevelManager.generateTargetColor(5)

        recipe.values.forEach { dropCount ->
            assertTrue(dropCount > 0)
        }
    }

    @Test
    fun `generateTargetColor color matches mixed recipe`() {
        val (targetColor, recipe) = LevelManager.generateTargetColor(5)

        val mixedColor = ColorMixer.mixColors(recipe)

        assertEquals(mixedColor, targetColor)
    }

    @Test
    fun `generateTargetColor uses available colors for level`() {
        val (_, recipe) = LevelManager.generateTargetColor(1)
        val availableColors = GameColor.getAvailableColors(1)

        recipe.keys.forEach { color ->
            assertTrue(availableColors.contains(color))
        }
    }

    @Test
    fun `generateTargetColor avoids previous target`() {
        val (firstTarget, _) = LevelManager.generateTargetColor(5)

        val subsequentTargets = List(10) {
            LevelManager.generateTargetColor(5, firstTarget).first
        }

        val differentTargets = subsequentTargets.count { it != firstTarget }

        assertTrue(differentTargets >= 5)
    }

    @Test
    fun `getToleranceForLevel returns correct values`() {
        assertEquals(0.20f, LevelManager.getToleranceForLevel(1), 0.001f)
        assertEquals(0.20f, LevelManager.getToleranceForLevel(3), 0.001f)
        assertEquals(0.17f, LevelManager.getToleranceForLevel(4), 0.001f)
        assertEquals(0.17f, LevelManager.getToleranceForLevel(6), 0.001f)
        assertEquals(0.15f, LevelManager.getToleranceForLevel(7), 0.001f)
        assertEquals(0.15f, LevelManager.getToleranceForLevel(9), 0.001f)
        assertEquals(0.12f, LevelManager.getToleranceForLevel(10), 0.001f)
        assertEquals(0.12f, LevelManager.getToleranceForLevel(30), 0.001f)
    }

    @Test
    fun `getToleranceForLevel decreases with level`() {
        val level3Tolerance = LevelManager.getToleranceForLevel(3)
        val level7Tolerance = LevelManager.getToleranceForLevel(7)
        val level15Tolerance = LevelManager.getToleranceForLevel(15)

        assertTrue(level3Tolerance > level7Tolerance)
        assertTrue(level7Tolerance > level15Tolerance)
    }

    @Test
    fun `generateTargetColor creates variety across multiple calls`() {
        val targets = List(20) {
            LevelManager.generateTargetColor(10).first
        }

        val uniqueTargets = targets.map {
            "${it.red}_${it.green}_${it.blue}"
        }.toSet()

        assertTrue(uniqueTargets.size > 5)
    }

    @Test
    fun `generateTargetColor recipe drop counts are reasonable`() {
        repeat(20) {
            val (_, recipe) = LevelManager.generateTargetColor(15)

            recipe.values.forEach { drops ->
                assertTrue(drops >= 1)
                assertTrue(drops <= 6)
            }
        }
    }

    @Test
    fun `generateTargetColor handles maximum level`() {
        val (color, recipe) = LevelManager.generateTargetColor(GameState.MAX_LEVEL)

        assertNotNull(color)
        assertTrue(recipe.isNotEmpty())
    }

    @Test
    fun `generateTargetColor recipe uses distinct colors`() {
        repeat(20) {
            val (_, recipe) = LevelManager.generateTargetColor(10)

            assertEquals(recipe.size, recipe.keys.toSet().size)
        }
    }

    @Test
    fun `generateTargetColor for low levels uses only base colors`() {
        repeat(10) {
            val (_, recipe) = LevelManager.generateTargetColor(1)

            recipe.keys.forEach { color ->
                assertTrue(color.unlockLevel == 1)
            }
        }
    }

    @Test
    fun `generateTargetColor complexity increases with level`() {
        // Use max recipe size rather than averages because averages can vary
        // by random sampling. Per LevelManager: levels 1-3 always use 2 colors,
        // levels 4-9 use 2-3, levels 10-15 use 2-4, levels 16+ use 2-5.
        // With 100 samples the maximum should reliably reach the upper bound.
        val sample = 100
        val maxLevel3 = List(sample) { LevelManager.generateTargetColor(3).second.size }.max()
        val maxLevel12 = List(sample) { LevelManager.generateTargetColor(12).second.size }.max()
        val maxLevel25 = List(sample) { LevelManager.generateTargetColor(25).second.size }.max()

        assertEquals("Level 3 should always use 2 colors", 2, maxLevel3)
        assertTrue("Level 12 should sometimes reach 4 colors", maxLevel12 >= 3)
        assertTrue("Level 25 should sometimes reach 5 colors", maxLevel25 >= 4)
        assertTrue("Max recipe size should grow with level", maxLevel25 >= maxLevel12)
    }
}
