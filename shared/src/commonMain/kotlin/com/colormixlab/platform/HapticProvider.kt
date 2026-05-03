package com.colormixlab.platform

/**
 * Haptic feedback types supported across platforms
 */
enum class HapticType {
    LIGHT_TAP,    // Light tap/click feedback
    SUCCESS,      // Success/completion feedback
    ERROR         // Error/warning feedback
}

/**
 * Platform-independent haptic feedback interface.
 * Android uses Vibrator, iOS uses UIImpactFeedbackGenerator.
 */
expect class HapticProvider {
    /**
     * Perform haptic feedback of the specified type.
     */
    fun performHaptic(type: HapticType)
}
