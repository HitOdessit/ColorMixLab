package com.colormixlab.platform

/**
 * Platform-independent storage interface.
 * Android implementation uses SharedPreferences, iOS uses UserDefaults.
 */
expect class PlatformStorage {
    /**
     * Save a string value with the given key.
     */
    fun saveString(key: String, value: String)

    /**
     * Retrieve a string value for the given key.
     * Returns null if not found.
     */
    fun getString(key: String): String?

    /**
     * Remove a value for the given key.
     */
    fun remove(key: String)

    /**
     * Clear all stored values.
     */
    fun clear()
}
