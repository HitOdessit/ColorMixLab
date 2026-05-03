package com.colormixlab.utils

import androidx.compose.ui.graphics.Color
import com.colormixlab.model.PlatformColor

/**
 * Extension functions to convert between Compose Color and PlatformColor
 */

/**
 * Convert Compose Color to PlatformColor
 */
fun Color.toPlatformColor(): PlatformColor {
    return PlatformColor(
        red = (this.red * 255).toInt(),
        green = (this.green * 255).toInt(),
        blue = (this.blue * 255).toInt(),
        alpha = (this.alpha * 255).toInt()
    )
}

/**
 * Convert PlatformColor to Compose Color
 */
fun PlatformColor.toComposeColor(): Color {
    return Color(
        red = this.redFloat,
        green = this.greenFloat,
        blue = this.blueFloat,
        alpha = this.alphaFloat
    )
}

/**
 * Get Compose Color from GameColor
 */
fun com.colormixlab.model.GameColor.toComposeColor(): Color {
    return this.color.toComposeColor()
}
