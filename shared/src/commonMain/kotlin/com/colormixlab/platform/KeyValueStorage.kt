package com.colormixlab.platform

/**
 * Platform-agnostic key-value storage interface.
 *
 * Allows storage consumers like `LeaderboardManager` to be tested with an
 * in-memory fake instead of requiring SharedPreferences (Android) or
 * NSUserDefaults (iOS) plumbing.
 */
interface KeyValueStorage {
    /** Persist [value] under [key]. */
    fun saveString(key: String, value: String)

    /** Read the value for [key], or null if not present. */
    fun getString(key: String): String?

    /** Remove the value for [key]. */
    fun remove(key: String)

    /** Wipe all stored values. */
    fun clear()
}

/** Adapter exposing the platform-native [PlatformStorage] as a [KeyValueStorage]. */
fun PlatformStorage.asKeyValueStorage(): KeyValueStorage = object : KeyValueStorage {
    override fun saveString(key: String, value: String) = this@asKeyValueStorage.saveString(key, value)
    override fun getString(key: String): String? = this@asKeyValueStorage.getString(key)
    override fun remove(key: String) = this@asKeyValueStorage.remove(key)
    override fun clear() = this@asKeyValueStorage.clear()
}
