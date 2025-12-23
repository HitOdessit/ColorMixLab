package com.colormixlab.game

import androidx.compose.ui.graphics.Color
import com.colormixlab.model.GameColor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for LevelManager.
 * Tests target color generation logic for different levels.
 */
class LevelManagerTest {

    @Before
    fun setup() {
        // Initialize game colors before each test
        GameColor.initializeGameColors()
    }

    @Test
    fun `generateTargetColor returns color and recipe`() {
        val (color, recipe) = LevelManager.generateTargetColor(1)

        assertNotNull("Color should not be null", color)
        assertNotNull("Recipe should not be null", recipe)
        assertTrue("Recipe should not be empty", recipe.isNotEmpty())
    }

    @Test
    fun `generateTargetColor recipe contains at least 2 colors for level 1`() {
        val (_, recipe) = LevelManager.generateTargetColor(1)

        assertTrue("Level 1 should use at least 2 colors", recipe.size >= 2)
    }

    @Test
    fun `generateTargetColor for level 1-3 uses simple mixes`() {
        repeat(10) {
            val (_, recipe) = LevelManager.generateTargetColor(level = 2)

            assertEquals("Levels 1-3 should use exactly 2 colors", 2, recipe.size)
        }
    }

    @Test
    fun `generateTargetColor for level 4-9 uses medium mixes`() {
        repeat(10) {
            val (_, recipe) = LevelManager.generateTargetColor(level = 7)

            assertTrue("Levels 4-9 should use 2-3 colors", recipe.size in 2..3)
        }
    }

    @Test
    fun `generateTargetColor for level 10-15 uses complex mixes`() {
        repeat(10) {
            val (_, recipe) = LevelManager.generateTargetColor(level = 12)

            assertTrue("Levels 10-15 should use 2-4 colors", recipe.size in 2..4)
        }
    }

    @Test
    fun `generateTargetColor for level 16+ uses advanced mixes`() {
        repeat(10) {
            val (_, recipe) = LevelManager.generateTargetColor(level = 20)

            assertTrue("Levels 16+ should use 2-5 colors", recipe.size in 2..5)
        }
    }

    @Test
    fun `generateTargetColor recipe has positive drop counts`() {
        val (_, recipe) = LevelManager.generateTargetColor(5)

        recipe.values.forEach { dropCount ->
            assertTrue("Drop counts should be positive", dropCount > 0)
        }
    }

    @Test
    fun `generateTargetColor color matches mixed recipe`() {
        val (targetColor, recipe) = LevelManager.generateTargetColor(5)

        val mixedColor = ColorMixer.mixColors(recipe)

        assertEquals("Target color should match mixed recipe", mixedColor, targetColor)
    }

    @Test
    fun `generateTargetColor uses available colors for level`() {
        val (_, recipe) = LevelManager.generateTargetColor(1)

        val availableColors = GameColor.getAvailableColors(1)

        recipe.keys.forEach { color ->
            assertTrue(
                "Recipe should only use colors available at level 1",
                availableColors.contains(color)
            )
        }
    }

    @Test
    fun `generateTargetColor avoids previous target`() {
        val (firstTarget, _) = LevelManager.generateTargetColor(5)

        // Generate multiple times to check it tries to avoid the same color
        val subsequentTargets = List(10) {
            LevelManager.generateTargetColor(5, firstTarget).first
        }

        // At least some should be different from the first target
        val differentTargets = subsequentTargets.count { it != firstTarget }

        assertTrue(
            "Should generate different targets when previous is provided (got $differentTargets different out of 10)",
            differentTargets >= 5
        )
    }

    @Test
    fun `getToleranceForLevel returns correct values`() {
        assertEquals("Level 1-3 should have 0.20 tolerance", 0.20f, LevelManager.getToleranceForLevel(1), 0.001f)
        assertEquals("Level 1-3 should have 0.20 tolerance", 0.20f, LevelManager.getToleranceForLevel(3), 0.001f)

        assertEquals("Level 4-6 should have 0.17 tolerance", 0.17f, LevelManager.getToleranceForLevel(4), 0.001f)
        assertEquals("Level 4-6 should have 0.17 tolerance", 0.17f, LevelManager.getToleranceForLevel(6), 0.001f)

        assertEquals("Level 7-9 should have 0.15 tolerance", 0.15f, LevelManager.getToleranceForLevel(7), 0.001f)
        assertEquals("Level 7-9 should have 0.15 tolerance", 0.15f, LevelManager.getToleranceForLevel(9), 0.001f)

        assertEquals("Level 10+ should have 0.12 tolerance", 0.12f, LevelManager.getToleranceForLevel(10), 0.001f)
        assertEquals("Level 10+ should have 0.12 tolerance", 0.12f, LevelManager.getToleranceForLevel(30), 0.001f)
    }

    @Test
    fun `getToleranceForLevel decreases with level`() {
        val level3Tolerance = LevelManager.getToleranceForLevel(3)
        val level7Tolerance = LevelManager.getToleranceForLevel(7)
        val level15Tolerance = LevelManager.getToleranceForLevel(15)

        assertTrue("Tolerance should decrease with level", level3Tolerance > level7Tolerance)
        assertTrue("Tolerance should decrease with level", level7Tolerance > level15Tolerance)
    }

    @Test
    fun `generateTargetColor creates variety across multiple calls`() {
        val targets = List(20) {
            LevelManager.generateTargetColor(10).first
        }

        // Convert to string representation for comparison
        val uniqueTargets = targets.map {
            "${it.red}_${it.green}_${it.blue}"
        }.toSet()

        assertTrue(
            "Should generate variety of targets (got ${uniqueTargets.size} unique out of 20)",
            uniqueTargets.size > 5
        )
    }

    @Test
    fun `generateTargetColor recipe drop counts are reasonable`() {
        repeat(20) {
            val (_, recipe) = LevelManager.generateTargetColor(15)

            recipe.values.forEach { drops ->
                assertTrue("Drop count should be >= 1", drops >= 1)
                assertTrue("Drop count should be <= 6", drops <= 6)
            }
        }
    }

    @Test
    fun `generateTargetColor handles maximum level`() {
        val (color, recipe) = LevelManager.generateTargetColor(GameState.MAX_LEVEL)

        assertNotNull("Should generate target for max level", color)
        assertTrue("Should have recipe for max level", recipe.isNotEmpty())
    }

    @Test
    fun `generateTargetColor recipe uses distinct colors`() {
        repeat(20) {
            val (_, recipe) = LevelManager.generateTargetColor(10)

            val uniqueColors = recipe.keys.toSet()

            assertEquals(
                "Recipe should use distinct colors",
                recipe.size,
                uniqueColors.size
            )
        }
    }

    @Test
    fun `generateTargetColor for low levels uses only base colors`() {
        repeat(10) {
            val (_, recipe) = LevelManager.generateTargetColor(1)

            recipe.keys.forEach { color ->
                assertTrue(
                    "Level 1 should only use base colors",
                    color.unlockLevel == 1
                )
            }
        }
    }

    @Test
    fun `generateTargetColor complexity increases with level`() {
        // Average number of colors should increase with level
        val level3Recipes = List(20) { LevelManager.generateTargetColor(3).second }
        val level12Recipes = List(20) { LevelManager.generateTargetColor(12).second }
        val level25Recipes = List(20) { LevelManager.generateTargetColor(25).second }

        val avgLevel3Colors = level3Recipes.map { it.size }.average()
        val avgLevel12Colors = level12Recipes.map { it.size }.average()
        val avgLevel25Colors = level25Recipes.map { it.size }.average()

        assertTrue(
            "Higher levels should tend to use more colors on average",
            avgLevel12Colors >= avgLevel3Colors && avgLevel25Colors >= avgLevel12Colors
        )
    }
}
