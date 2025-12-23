package com.colormixlab.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSBundle
import platform.Foundation.NSURL

/**
 * iOS implementation of SoundProvider using AVAudioPlayer
 */
@OptIn(ExperimentalForeignApi::class)
actual class SoundProvider {
    private val players = mutableMapOf<SoundType, AVAudioPlayer>()

    init {
        // Load sound files from bundle
        // Note: Sound files need to be added to the iOS app bundle
        // loadSound(SoundType.DROP_COLOR, "drop_color")
        // loadSound(SoundType.SUCCESS, "success")
        // loadSound(SoundType.CLEAR, "clear")
        // loadSound(SoundType.LEVEL_UP, "level_up")
    }

    private fun loadSound(type: SoundType, filename: String) {
        val bundle = NSBundle.mainBundle
        val soundPath = bundle.pathForResource(filename, ofType = "mp3")
            ?: bundle.pathForResource(filename, ofType = "wav")

        soundPath?.let { path ->
            val url = NSURL.fileURLWithPath(path)
            val player = AVAudioPlayer(url, null)
            player?.prepareToPlay()
            players[type] = player
        }
    }

    actual fun playSound(type: SoundType) {
        players[type]?.play()
    }

    actual fun release() {
        players.values.forEach { it.stop() }
        players.clear()
    }
}
