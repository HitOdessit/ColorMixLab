package com.colormixlab.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes

class SoundManager(private val context: Context) {
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<SoundType, Int>()
    
    enum class SoundType {
        DROP_COLOR,
        SUCCESS,
        CLEAR,
        LEVEL_UP
    }
    
    init {
        initSoundPool()
    }
    
    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
        
        // Note: In a real app, you would load actual sound files from res/raw/
        // For now, we'll use system sounds as placeholders
        // loadSound(SoundType.DROP_COLOR, R.raw.drop_color)
        // loadSound(SoundType.SUCCESS, R.raw.success)
        // loadSound(SoundType.CLEAR, R.raw.clear)
        // loadSound(SoundType.LEVEL_UP, R.raw.level_up)
    }
    
    private fun loadSound(type: SoundType, @RawRes resourceId: Int) {
        soundPool?.let { pool ->
            val soundId = pool.load(context, resourceId, 1)
            soundMap[type] = soundId
        }
    }
    
    fun playSound(type: SoundType) {
        soundMap[type]?.let { soundId ->
            soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }
    
    fun release() {
        soundPool?.release()
        soundPool = null
        soundMap.clear()
    }
}

