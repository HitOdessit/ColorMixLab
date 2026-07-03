package com.colormixlab.data

import com.colormixlab.game.GameConstants
import com.colormixlab.model.LeaderboardEntry
import com.colormixlab.platform.KeyValueStorage
import com.colormixlab.platform.PlatformStorage
import com.colormixlab.platform.asKeyValueStorage
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Cross-platform leaderboard manager backed by [PlatformStorage].
 *
 * Persists up to [GameConstants.LEADERBOARD_MAX_ENTRIES] entries as JSON.
 * Provides time-windowed queries (today, week, month, all-time) for the UI's
 * leaderboard tabs.
 *
 * On Android this is backed by SharedPreferences; on iOS by NSUserDefaults.
 */
class LeaderboardManager(private val storage: KeyValueStorage) {

    /** Convenience constructor for production callers using [PlatformStorage]. */
    constructor(platformStorage: PlatformStorage) : this(platformStorage.asKeyValueStorage())

    private companion object {
        const val KEY_ENTRIES = "leaderboard_entries"
        const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /**
     * Persist [entry] and prune the list to the top [GameConstants.LEADERBOARD_MAX_ENTRIES]
     * entries by sort order ([LeaderboardEntry.compareTo]).
     */
    fun addEntry(entry: LeaderboardEntry) {
        val entries = getEntries().toMutableList()
        entries.add(entry)
        entries.sort()

        val topEntries = entries.take(GameConstants.LEADERBOARD_MAX_ENTRIES)
        saveEntries(topEntries)
    }

    /** Return all stored entries, or an empty list if storage is empty or corrupted. */
    fun getEntries(): List<LeaderboardEntry> {
        val jsonString = storage.getString(KEY_ENTRIES) ?: return emptyList()

        return try {
            json.decodeFromString<List<LeaderboardEntry>>(jsonString)
        } catch (_: Exception) {
            // Corrupt or legacy payload — recover by starting from an empty list.
            emptyList()
        }
    }

    /** Top entries from today (since local-time midnight). */
    fun getTodayEntries(limit: Int = GameConstants.LEADERBOARD_DEFAULT_PAGE_SIZE): List<LeaderboardEntry> {
        val todayStartMillis = startOfTodayMillis()
        return entriesSince(todayStartMillis, limit)
    }

    /** Top entries from this week (since local-time Monday midnight). */
    fun getWeekEntries(limit: Int = GameConstants.LEADERBOARD_DEFAULT_PAGE_SIZE): List<LeaderboardEntry> {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val daysSinceMonday = localDateTime.dayOfWeek.ordinal
        val weekStartMillis = startOfTodayMillis() - daysSinceMonday * MILLIS_PER_DAY
        return entriesSince(weekStartMillis, limit)
    }

    /** Top entries from this month (since local-time midnight on the 1st). */
    fun getMonthEntries(limit: Int = GameConstants.LEADERBOARD_DEFAULT_PAGE_SIZE): List<LeaderboardEntry> {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val daysIntoMonth = localDateTime.dayOfMonth - 1
        val monthStartMillis = startOfTodayMillis() - daysIntoMonth * MILLIS_PER_DAY
        return entriesSince(monthStartMillis, limit)
    }

    /** Top all-time entries. */
    fun getAllTimeEntries(limit: Int = 10): List<LeaderboardEntry> {
        return getEntries()
            .sortedByDescending { it.score }
            .take(limit)
    }

    /** Wipe all stored entries. */
    fun clearLeaderboard() {
        storage.remove(KEY_ENTRIES)
    }

    /** The current top score, or 0 if the leaderboard is empty. */
    fun getTopScore(): Int {
        return getEntries().firstOrNull()?.score ?: 0
    }

    /**
     * Calculate where [score] would rank if added now (1 = best).
     * Returns `entries.size + 1` if [score] is below all existing entries.
     */
    fun getRank(score: Int): Int {
        val entries = getEntries()
        val index = entries.indexOfFirst { it.score <= score }
        return if (index == -1) entries.size + 1 else index + 1
    }

    // ========== PRIVATE HELPERS ==========

    private fun startOfTodayMillis(): Long {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        return now.toEpochMilliseconds() - localDateTime.millisSinceMidnight()
    }

    private fun LocalDateTime.millisSinceMidnight(): Long =
        hour * 3_600_000L + minute * 60_000L + second * 1_000L + nanosecond / 1_000_000L

    private fun entriesSince(thresholdMillis: Long, limit: Int): List<LeaderboardEntry> =
        getEntries()
            .filter { it.timestamp >= thresholdMillis }
            .sortedByDescending { it.score }
            .take(limit)

    private fun saveEntries(entries: List<LeaderboardEntry>) {
        val jsonString = json.encodeToString(entries)
        storage.saveString(KEY_ENTRIES, jsonString)
    }
}
