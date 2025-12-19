package com.colormixlab.model

data class LeaderboardEntry(
    val nickname: String,
    val score: Int,
    val level: Int,
    val timestamp: Long = System.currentTimeMillis()
) : Comparable<LeaderboardEntry> {
    override fun compareTo(other: LeaderboardEntry): Int {
        // Sort by score descending, then by level descending, then by timestamp ascending
        return when {
            this.score != other.score -> other.score.compareTo(this.score)
            this.level != other.level -> other.level.compareTo(this.level)
            else -> this.timestamp.compareTo(other.timestamp)
        }
    }
}

