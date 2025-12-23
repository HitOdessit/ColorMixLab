package com.colormixlab.data

import com.colormixlab.game.Difficulty
import com.colormixlab.model.LeaderboardEntry
import com.colormixlab.platform.PlatformStorage
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Cross-platform leaderboard manager.
 * Uses PlatformStorage (SharedPreferences on Android, UserDefaults on iOS).
 */
class LeaderboardManager(private val storage: PlatformStorage) {

    companion object {
        private const val KEY_ENTRIES = "leaderboard_entries"
        private const val MAX_ENTRIES = 100
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun addEntry(entry: LeaderboardEntry) {
        val entries = getEntries().toMutableList()
        entries.add(entry)
        entries.sort()

        // Keep only top MAX_ENTRIES
        val topEntries = entries.take(MAX_ENTRIES)
        saveEntries(topEntries)
    }

    fun getEntries(): List<LeaderboardEntry> {
        val jsonString = storage.getString(KEY_ENTRIES) ?: return emptyList()

        return try {
            json.decodeFromString<List<LeaderboardEntry>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Get entries from today (since midnight local time)
     */
    fun getTodayEntries(limit: Int = 5): List<LeaderboardEntry> {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())

        // Calculate milliseconds since midnight today
        val millisSinceMidnight = (localDateTime.hour * 3600000L) +
                                   (localDateTime.minute * 60000L) +
                                   (localDateTime.second * 1000L) +
                                   localDateTime.nanosecond / 1000000L
        val todayStartMillis = now.toEpochMilliseconds() - millisSinceMidnight

        return getEntries()
            .filter { it.timestamp >= todayStartMillis }
            .sortedByDescending { it.score }
            .take(limit)
    }

    /**
     * Get entries from this week (since Monday)
     */
    fun getWeekEntries(limit: Int = 5): List<LeaderboardEntry> {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val dayOfWeek = localDateTime.dayOfWeek.ordinal // 0 = Monday

        // Calculate milliseconds to subtract to get to Monday midnight
        val millisSinceMidnight = (localDateTime.hour * 3600000L) +
                                   (localDateTime.minute * 60000L) +
                                   (localDateTime.second * 1000L) +
                                   localDateTime.nanosecond / 1000000L
        val todayMidnightMillis = now.toEpochMilliseconds() - millisSinceMidnight
        val weekStartMillis = todayMidnightMillis - (dayOfWeek * 24 * 60 * 60 * 1000L)

        return getEntries()
            .filter { it.timestamp >= weekStartMillis }
            .sortedByDescending { it.score }
            .take(limit)
    }

    /**
     * Get entries from this month
     */
    fun getMonthEntries(limit: Int = 5): List<LeaderboardEntry> {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())

        // Calculate milliseconds since midnight on the 1st of this month
        val millisSinceMidnight = (localDateTime.hour * 3600000L) +
                                   (localDateTime.minute * 60000L) +
                                   (localDateTime.second * 1000L) +
                                   localDateTime.nanosecond / 1000000L
        val todayMidnightMillis = now.toEpochMilliseconds() - millisSinceMidnight
        val daysIntoMonth = localDateTime.dayOfMonth - 1
        val monthStartMillis = todayMidnightMillis - (daysIntoMonth * 24 * 60 * 60 * 1000L)

        return getEntries()
            .filter { it.timestamp >= monthStartMillis }
            .sortedByDescending { it.score }
            .take(limit)
    }

    /**
     * Get all-time top entries
     */
    fun getAllTimeEntries(limit: Int = 10): List<LeaderboardEntry> {
        return getEntries()
            .sortedByDescending { it.score }
            .take(limit)
    }

    fun clearLeaderboard() {
        storage.remove(KEY_ENTRIES)
    }

    private fun saveEntries(entries: List<LeaderboardEntry>) {
        val jsonString = json.encodeToString(entries)
        storage.saveString(KEY_ENTRIES, jsonString)
    }

    fun getTopScore(): Int {
        return getEntries().firstOrNull()?.score ?: 0
    }

    fun getRank(score: Int): Int {
        val entries = getEntries()
        return entries.indexOfFirst { it.score <= score } + 1
    }
}
