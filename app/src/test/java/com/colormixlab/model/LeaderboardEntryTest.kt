package com.colormixlab.model

import com.colormixlab.game.Difficulty
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for LeaderboardEntry comparison logic.
 * Tests sorting behavior based on score, level, and timestamp.
 */
class LeaderboardEntryTest {

    @Test
    fun `entry with higher score comes first`() {
        val entry1 = LeaderboardEntry("Player1", score = 1000, level = 10)
        val entry2 = LeaderboardEntry("Player2", score = 500, level = 10)

        val comparison = entry1.compareTo(entry2)

        assertTrue("Higher score should come first", comparison < 0)
    }

    @Test
    fun `entry with lower score comes second`() {
        val entry1 = LeaderboardEntry("Player1", score = 300, level = 10)
        val entry2 = LeaderboardEntry("Player2", score = 800, level = 10)

        val comparison = entry1.compareTo(entry2)

        assertTrue("Lower score should come second", comparison > 0)
    }

    @Test
    fun `entries with same score sorted by level descending`() {
        val entry1 = LeaderboardEntry("Player1", score = 1000, level = 20)
        val entry2 = LeaderboardEntry("Player2", score = 1000, level = 15)

        val comparison = entry1.compareTo(entry2)

        assertTrue("Higher level should come first when scores equal", comparison < 0)
    }

    @Test
    fun `entries with same score and level sorted by timestamp ascending`() {
        val earlierTime = System.currentTimeMillis() - 10000
        val laterTime = System.currentTimeMillis()

        val entry1 = LeaderboardEntry("Player1", score = 1000, level = 10, timestamp = earlierTime)
        val entry2 = LeaderboardEntry("Player2", score = 1000, level = 10, timestamp = laterTime)

        val comparison = entry1.compareTo(entry2)

        assertTrue("Earlier timestamp should come first when score and level equal", comparison < 0)
    }

    @Test
    fun `identical entries are equal`() {
        val timestamp = System.currentTimeMillis()
        val entry1 = LeaderboardEntry("Player1", score = 1000, level = 10, timestamp = timestamp)
        val entry2 = LeaderboardEntry("Player1", score = 1000, level = 10, timestamp = timestamp)

        val comparison = entry1.compareTo(entry2)

        assertEquals("Identical entries should be equal", 0, comparison)
    }

    @Test
    fun `entries sort correctly in a list`() {
        val entries = listOf(
            LeaderboardEntry("Low Score", score = 100, level = 5),
            LeaderboardEntry("High Score", score = 2000, level = 30),
            LeaderboardEntry("Medium Score", score = 500, level = 15),
            LeaderboardEntry("Same High", score = 2000, level = 25), // Same score, lower level
        )

        val sorted = entries.sorted()

        assertEquals("First should be highest score", 2000, sorted[0].score)
        assertEquals("First of same score should have higher level", 30, sorted[0].level)
        assertEquals("Second should be same high score but lower level", 25, sorted[1].level)
        assertEquals("Third should be medium score", 500, sorted[2].score)
        assertEquals("Last should be lowest score", 100, sorted[3].score)
    }

    @Test
    fun `difficulty is stored correctly`() {
        val easyEntry = LeaderboardEntry("Easy Player", 100, 10, Difficulty.EASY)
        val mediumEntry = LeaderboardEntry("Medium Player", 100, 10, Difficulty.MEDIUM)
        val hardEntry = LeaderboardEntry("Hard Player", 100, 10, Difficulty.HARD)

        assertEquals(Difficulty.EASY, easyEntry.difficulty)
        assertEquals(Difficulty.MEDIUM, mediumEntry.difficulty)
        assertEquals(Difficulty.HARD, hardEntry.difficulty)
    }

    @Test
    fun `default difficulty is MEDIUM`() {
        val entry = LeaderboardEntry("Player", 100, 10)

        assertEquals("Default difficulty should be MEDIUM", Difficulty.MEDIUM, entry.difficulty)
    }

    @Test
    fun `timestamp defaults to current time`() {
        val beforeCreation = System.currentTimeMillis()
        val entry = LeaderboardEntry("Player", 100, 10)
        val afterCreation = System.currentTimeMillis()

        assertTrue("Timestamp should be >= time before creation", entry.timestamp >= beforeCreation)
        assertTrue("Timestamp should be <= time after creation", entry.timestamp <= afterCreation)
    }

    @Test
    fun `multiple entries with same score different timestamps sort chronologically`() {
        val time1 = 1000L
        val time2 = 2000L
        val time3 = 3000L

        val entries = listOf(
            LeaderboardEntry("Third", score = 500, level = 10, timestamp = time3),
            LeaderboardEntry("First", score = 500, level = 10, timestamp = time1),
            LeaderboardEntry("Second", score = 500, level = 10, timestamp = time2),
        )

        val sorted = entries.sorted()

        assertEquals("First should have earliest timestamp", time1, sorted[0].timestamp)
        assertEquals("Second should have middle timestamp", time2, sorted[1].timestamp)
        assertEquals("Third should have latest timestamp", time3, sorted[2].timestamp)
    }

    @Test
    fun `score takes precedence over level in sorting`() {
        val highScoreLowLevel = LeaderboardEntry("HS-LL", score = 1000, level = 5)
        val lowScoreHighLevel = LeaderboardEntry("LS-HL", score = 500, level = 30)

        val comparison = highScoreLowLevel.compareTo(lowScoreHighLevel)

        assertTrue(
            "Higher score should come first regardless of level",
            comparison < 0
        )
    }
}
