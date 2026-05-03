package com.colormixlab.model

import com.colormixlab.game.Difficulty
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntry(
    val nickname: String,
    val score: Int,
    val level: Int,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
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
