package com.colormixlab.platform

import android.content.Context
import android.content.SharedPreferences

/**
 * Android implementation of PlatformStorage using SharedPreferences
 */
actual class PlatformStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "ColorMixLabStorage",
        Context.MODE_PRIVATE
    )

    actual fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun getString(key: String): String? {
        return prefs.getString(key, null)
    }

    actual fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    actual fun clear() {
        prefs.edit().clear().apply()
    }
}
