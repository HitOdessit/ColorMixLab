package com.colormixlab.platform

import platform.Foundation.NSUserDefaults

/**
 * iOS implementation of PlatformStorage using NSUserDefaults
 */
actual class PlatformStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun saveString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
        defaults.synchronize()
    }

    actual fun getString(key: String): String? {
        return defaults.stringForKey(key)
    }

    actual fun remove(key: String) {
        defaults.removeObjectForKey(key)
        defaults.synchronize()
    }

    actual fun clear() {
        // Get all keys and remove ColorMixLab-related ones
        val domain = platform.Foundation.NSBundle.mainBundle.bundleIdentifier
        if (domain != null) {
            defaults.removePersistentDomainForName(domain)
        }
        defaults.synchronize()
    }
}
