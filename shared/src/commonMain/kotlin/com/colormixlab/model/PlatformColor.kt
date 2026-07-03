package com.colormixlab.model

/**
 * Platform-independent color representation using ARGB values.
 * This replaces androidx.compose.ui.graphics.Color for shared code.
 */
data class PlatformColor(val argb: UInt) {
    constructor(red: Int, green: Int, blue: Int, alpha: Int = 255) : this(
        (((alpha and 0xFF) shl 24) or
        ((red and 0xFF) shl 16) or
        ((green and 0xFF) shl 8) or
        (blue and 0xFF)).toUInt()
    )

    val alpha: Int get() = ((argb shr 24) and 0xFFu).toInt()
    val red: Int get() = ((argb shr 16) and 0xFFu).toInt()
    val green: Int get() = ((argb shr 8) and 0xFFu).toInt()
    val blue: Int get() = (argb and 0xFFu).toInt()

    // Float components for calculations (0.0 - 1.0)
    val redFloat: Float get() = red / 255f
    val greenFloat: Float get() = green / 255f
    val blueFloat: Float get() = blue / 255f
    val alphaFloat: Float get() = alpha / 255f

    companion object {
        val White = PlatformColor(255, 255, 255)
        val Black = PlatformColor(0, 0, 0)
        val Transparent = PlatformColor(0, 0, 0, 0)

        // Primary references used for mixing math and tests. The full game
        // palette lives in GameColor — don't duplicate it here.
        val Red = PlatformColor(0xFFFF0000u)
        val Blue = PlatformColor(0xFF0000FFu)
    }
}
