package com.colormixlab.game

import com.colormixlab.model.GameColor
import com.colormixlab.model.PlatformColor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.math.abs

class ColorMixerTest {

    @Before
    fun setup() {
        GameColor.initializeGameColors(42L)
    }

    @Test
    fun `mixColors returns white for empty drops`() {
        val result = ColorMixer.mixColors(emptyMap())

        assertEquals(PlatformColor.White, result)
    }

    @Test
    fun `mixColors with single color returns that color`() {
        val drops: Map<GameColor, Int> = mapOf(GameColor.Red to 1)

        val result = ColorMixer.mixColors(drops)

        assertEquals(GameColor.Red.color, result)
    }

    @Test
    fun `mixColors averages RGB values correctly`() {
        val drops = mapOf(
            GameColor.Red to 1,
            GameColor.Blue to 1
        )

        val result = ColorMixer.mixColors(drops)

        val expectedRed = (GameColor.Red.color.redFloat + GameColor.Blue.color.redFloat) / 2
        val expectedGreen = (GameColor.Red.color.greenFloat + GameColor.Blue.color.greenFloat) / 2
        val expectedBlue = (GameColor.Red.color.blueFloat + GameColor.Blue.color.blueFloat) / 2

        assertEquals(expectedRed, result.redFloat, 0.02f)
        assertEquals(expectedGreen, result.greenFloat, 0.02f)
        assertEquals(expectedBlue, result.blueFloat, 0.02f)
    }

    @Test
    fun `mixColors weighs colors by drop count`() {
        val drops = mapOf(
            GameColor.Red to 3,
            GameColor.Blue to 1
        )

        val result = ColorMixer.mixColors(drops)

        assertTrue("Mixed color should have more red component", result.redFloat > result.blueFloat)
    }

    @Test
    fun `calculateSimilarity returns 1 for identical colors`() {
        val color = PlatformColor(128, 77, 204)

        val similarity = ColorMixer.calculateSimilarity(color, color)

        assertEquals(1.0f, similarity, 0.001f)
    }

    @Test
    fun `calculateSimilarity returns value between 0 and 1`() {
        val similarity = ColorMixer.calculateSimilarity(PlatformColor.Red, PlatformColor.Blue)

        assertTrue(similarity >= 0f)
        assertTrue(similarity <= 1f)
    }

    @Test
    fun `calculateSimilarity returns lower value for more different colors`() {
        val target = PlatformColor.Red
        val closeMix = PlatformColor(242, 13, 13)
        val farMix = PlatformColor.Blue

        val closeSimilarity = ColorMixer.calculateSimilarity(target, closeMix)
        val farSimilarity = ColorMixer.calculateSimilarity(target, farMix)

        assertTrue(closeSimilarity > farSimilarity)
    }

    @Test
    fun `calculateSimilarity is symmetric`() {
        val color1 = PlatformColor(77, 128, 179)
        val color2 = PlatformColor(102, 153, 204)

        val similarity1 = ColorMixer.calculateSimilarity(color1, color2)
        val similarity2 = ColorMixer.calculateSimilarity(color2, color1)

        assertEquals(similarity1, similarity2, 0.001f)
    }

    @Test
    fun `mixColors with all three base colors`() {
        val drops = mapOf(
            GameColor.Red to 1,
            GameColor.Green to 1,
            GameColor.Blue to 1
        )

        val result = ColorMixer.mixColors(drops)

        val delta = 0.15f
        assertTrue(abs(result.redFloat - result.greenFloat) < delta)
        assertTrue(abs(result.greenFloat - result.blueFloat) < delta)
    }

    @Test
    fun `calculateSimilarity for black and white returns low similarity`() {
        val similarity = ColorMixer.calculateSimilarity(PlatformColor.Black, PlatformColor.White)

        assertTrue(similarity < 0.1f)
    }

    @Test
    fun `mixColors handles large drop counts`() {
        val drops = mapOf(
            GameColor.Red to 100,
            GameColor.Blue to 100
        )

        val result = ColorMixer.mixColors(drops)

        val expectedRed = (GameColor.Red.color.redFloat + GameColor.Blue.color.redFloat) / 2
        val expectedBlue = (GameColor.Red.color.blueFloat + GameColor.Blue.color.blueFloat) / 2

        assertEquals(expectedRed, result.redFloat, 0.02f)
        assertEquals(expectedBlue, result.blueFloat, 0.02f)
    }
}
