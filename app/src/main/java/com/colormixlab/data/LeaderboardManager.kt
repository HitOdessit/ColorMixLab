package com.colormixlab.data

import android.content.Context
import android.content.SharedPreferences
import com.colormixlab.game.Difficulty
import com.colormixlab.model.LeaderboardEntry
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class LeaderboardManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "ColorMixLabLeaderboard",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_ENTRIES = "leaderboard_entries"
        private const val MAX_ENTRIES = 100 // Increased to store more history
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
        val jsonString = prefs.getString(KEY_ENTRIES, null) ?: return emptyList()
        
        return try {
            val jsonArray = JSONArray(jsonString)
            (0 until jsonArray.length()).map { i ->
                val jsonObject = jsonArray.getJSONObject(i)
                val difficultyString = jsonObject.optString("difficulty", "MEDIUM")
                val difficulty = try {
                    Difficulty.valueOf(difficultyString)
                } catch (e: Exception) {
                    Difficulty.MEDIUM
                }
                
                LeaderboardEntry(
                    nickname = jsonObject.getString("nickname"),
                    score = jsonObject.getInt("score"),
                    level = jsonObject.getInt("level"),
                    difficulty = difficulty,
                    timestamp = jsonObject.getLong("timestamp")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get entries from today (since midnight)
     */
    fun getTodayEntries(limit: Int = 5): List<LeaderboardEntry> {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        return getEntries()
            .filter { it.timestamp >= todayStart }
            .sortedByDescending { it.score }
            .take(limit)
    }
    
    /**
     * Get entries from this week (since Monday)
     */
    fun getWeekEntries(limit: Int = 5): List<LeaderboardEntry> {
        val weekStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        return getEntries()
            .filter { it.timestamp >= weekStart }
            .sortedByDescending { it.score }
            .take(limit)
    }
    
    /**
     * Get entries from this month
     */
    fun getMonthEntries(limit: Int = 5): List<LeaderboardEntry> {
        val monthStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        return getEntries()
            .filter { it.timestamp >= monthStart }
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
        prefs.edit().remove(KEY_ENTRIES).apply()
    }
    
    private fun saveEntries(entries: List<LeaderboardEntry>) {
        val jsonArray = JSONArray()
        entries.forEach { entry ->
            val jsonObject = JSONObject().apply {
                put("nickname", entry.nickname)
                put("score", entry.score)
                put("level", entry.level)
                put("difficulty", entry.difficulty.name)
                put("timestamp", entry.timestamp)
            }
            jsonArray.put(jsonObject)
        }
        
        prefs.edit().putString(KEY_ENTRIES, jsonArray.toString()).apply()
    }
    
    fun getTopScore(): Int {
        return getEntries().firstOrNull()?.score ?: 0
    }
    
    fun getRank(score: Int): Int {
        val entries = getEntries()
        return entries.indexOfFirst { it.score <= score } + 1
    }
}
