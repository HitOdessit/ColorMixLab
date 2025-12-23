package com.colormixlab.data

import android.content.Context
import com.colormixlab.platform.PlatformStorage

/**
 * Factory object to create LeaderboardManager instances for Android.
 * Uses the shared module's LeaderboardManager with Android's PlatformStorage implementation.
 */
object LeaderboardManagerFactory {
    /**
     * Create a LeaderboardManager instance for Android.
     */
    fun create(context: Context): LeaderboardManager {
        val storage = PlatformStorage(context)
        return LeaderboardManager(storage)
    }
}
