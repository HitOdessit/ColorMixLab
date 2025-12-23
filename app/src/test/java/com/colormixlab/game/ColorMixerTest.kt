package com.colormixlab.game

import androidx.compose.ui.graphics.Color
import com.colormixlab.model.GameColor
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

/**
 * Unit tests for ColorMixer object.
 * Tests color mixing algorithms and similarity calculations.
 */
class ColorMixerTest {

    @Test
    fun `mixColors returns white for empty drops`() {
        val result = ColorMixer.mixColors(emptyMap())

        assertEquals("Empty bowl should be white", Color.White, result)
    }

    @Test
    fun `mixColors with single color returns that color`() {
        val drops: Map<GameColor, Int> = mapOf(GameColor.Red to 1)

        val result = ColorMixer.mixColors(drops)

        assertEquals("Single drop should return same color", GameColor.Red.rgb, result)
    }

    @Test
    fun `mixColors averages RGB values correctly`() {
        // Red (1, 0, 0) + Blue (0, 0, 1) should give purple-ish (0.5, 0, 0.5)
        val drops = mapOf(
            GameColor.Red to 1,
            GameColor.Blue to 1
        )

        val result = ColorMixer.mixColors(drops)

        // Check that it's averaging the RGB values
        val expectedRed = (GameColor.Red.rgb.red + GameColor.Blue.rgb.red) / 2
        val expectedGreen = (GameColor.Red.rgb.green + GameColor.Blue.rgb.green) / 2
        val expectedBlue = (GameColor.Red.rgb.blue + GameColor.Blue.rgb.blue) / 2

        assertEquals(expectedRed, result.red, 0.01f)
        assertEquals(expectedGreen, result.green, 0.01f)
        assertEquals(expectedBlue, result.blue, 0.01f)
    }

    @Test
    fun `mixColors weighs colors by drop count`() {
        // 3 red + 1 blue should be more red than blue
        val drops = mapOf(
            GameColor.Red to 3,
            GameColor.Blue to 1
        )

        val result = ColorMixer.mixColors(drops)

        // Result should be more red than blue
        assertTrue("Mixed color should have more red component", result.red > result.blue)
    }

    @Test
    fun `calculateSimilarity returns 1 for identical colors`() {
        val color = Color(0.5f, 0.3f, 0.8f)

        val similarity = ColorMixer.calculateSimilarity(color, color)

        assertEquals("Identical colors should have similarity of 1", 1.0f, similarity, 0.001f)
    }

    @Test
    fun `calculateSimilarity returns value between 0 and 1`() {
        val target = Color.Red
        val mixed = Color.Blue

        val similarity = ColorMixer.calculateSimilarity(target, mixed)

        assertTrue("Similarity should be >= 0", similarity >= 0f)
        assertTrue("Similarity should be <= 1", similarity <= 1f)
    }

    @Test
    fun `calculateSimilarity returns lower value for more different colors`() {
        val target = Color.Red
        val closeMix = Color(0.95f, 0.05f, 0.05f) // Very close to red
        val farMix = Color.Blue // Very different from red

        val closeSimilarity = ColorMixer.calculateSimilarity(target, closeMix)
        val farSimilarity = ColorMixer.calculateSimilarity(target, farMix)

        assertTrue(
            "Close color should have higher similarity than far color",
            closeSimilarity > farSimilarity
        )
    }

    @Test
    fun `calculateSimilarity is symmetric`() {
        val color1 = Color(0.3f, 0.5f, 0.7f)
        val color2 = Color(0.4f, 0.6f, 0.8f)

        val similarity1 = ColorMixer.calculateSimilarity(color1, color2)
        val similarity2 = ColorMixer.calculateSimilarity(color2, color1)

        assertEquals(
            "Similarity should be symmetric",
            similarity1,
            similarity2,
            0.001f
        )
    }

    @Test
    fun `colorsMatch returns true for identical colors`() {
        val color = Color(0.5f, 0.5f, 0.5f)

        val match = ColorMixer.colorsMatch(color, color)

        assertTrue("Identical colors should match", match)
    }

    @Test
    fun `colorsMatch returns false for very different colors`() {
        val match = ColorMixer.colorsMatch(Color.Red, Color.Blue)

        assertFalse("Red and blue should not match", match)
    }

    @Test
    fun `colorsMatch respects tolerance parameter`() {
        val target = Color(0.5f, 0.5f, 0.5f)
        val similar = Color(0.52f, 0.52f, 0.52f)

        val strictMatch = ColorMixer.colorsMatch(target, similar, tolerance = 0.01f)
        val lenientMatch = ColorMixer.colorsMatch(target, similar, tolerance = 0.5f)

        assertFalse("Should not match with strict tolerance", strictMatch)
        assertTrue("Should match with lenient tolerance", lenientMatch)
    }

    @Test
    fun `colorsMatch uses default tolerance of 0_15`() {
        val target = Color(0.5f, 0.5f, 0.5f)
        val similar = Color(0.55f, 0.55f, 0.55f) // Small difference

        // This should depend on the default tolerance (0.15f)
        // Distance calculation is sqrt((0.05^2 * 3)) = ~0.087
        val match = ColorMixer.colorsMatch(target, similar)

        assertTrue("Should match within default tolerance", match)
    }

    @Test
    fun `mixColors with all three base colors`() {
        val drops = mapOf(
            GameColor.Red to 1,
            GameColor.Green to 1,
            GameColor.Blue to 1
        )

        val result = ColorMixer.mixColors(drops)

        // Equal parts RGB should give a grayish color
        // Each component should be roughly equal
        val delta = 0.15f // Allow some tolerance due to color variations
        assertTrue(
            "Red and Green should be similar",
            abs(result.red - result.green) < delta
        )
        assertTrue(
            "Green and Blue should be similar",
            abs(result.green - result.blue) < delta
        )
    }

    @Test
    fun `calculateSimilarity for black and white returns low similarity`() {
        val similarity = ColorMixer.calculateSimilarity(Color.Black, Color.White)

        assertTrue(
            "Black and white should have very low similarity",
            similarity < 0.1f
        )
    }

    @Test
    fun `mixColors handles large drop counts`() {
        val drops = mapOf(
            GameColor.Red to 100,
            GameColor.Blue to 100
        )

        val result = ColorMixer.mixColors(drops)

        // Should still average correctly regardless of magnitude
        val expectedRed = (GameColor.Red.rgb.red + GameColor.Blue.rgb.red) / 2
        val expectedBlue = (GameColor.Red.rgb.blue + GameColor.Blue.rgb.blue) / 2

        assertEquals(expectedRed, result.red, 0.01f)
        assertEquals(expectedBlue, result.blue, 0.01f)
    }
}
