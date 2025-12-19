package com.colormixlab.data

import android.content.Context
import android.content.SharedPreferences
import com.colormixlab.model.LeaderboardEntry
import org.json.JSONArray
import org.json.JSONObject

class LeaderboardManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "ColorMixLabLeaderboard",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_ENTRIES = "leaderboard_entries"
        private const val MAX_ENTRIES = 50
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
                LeaderboardEntry(
                    nickname = jsonObject.getString("nickname"),
                    score = jsonObject.getInt("score"),
                    level = jsonObject.getInt("level"),
                    timestamp = jsonObject.getLong("timestamp")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
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

