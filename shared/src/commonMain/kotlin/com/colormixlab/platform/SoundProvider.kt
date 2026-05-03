package com.colormixlab.platform

/**
 * Sound effect types supported across platforms
 */
enum class SoundType {
    DROP_COLOR,   // Color drop sound
    SUCCESS,      // Success sound
    CLEAR,        // Clear/reset sound
    LEVEL_UP      // Level up sound
}

/**
 * Platform-independent sound playback interface.
 * Android uses SoundPool, iOS uses AVAudioPlayer.
 */
expect class SoundProvider {
    /**
     * Play a sound effect of the specified type.
     */
    fun playSound(type: SoundType)

    /**
     * Release all sound resources.
     */
    fun release()
}
