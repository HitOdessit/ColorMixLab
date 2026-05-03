package com.colormixlab.data

import com.colormixlab.game.Difficulty
import com.colormixlab.model.LeaderboardEntry
import com.colormixlab.platform.KeyValueStorage
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LeaderboardManagerTest {

    private lateinit var storage: FakeStorage
    private lateinit var manager: LeaderboardManager

    @Before
    fun setup() {
        storage = FakeStorage()
        manager = LeaderboardManager(storage)
    }

    @Test
    fun `getEntries returns empty list when storage is empty`() {
        assertTrue(manager.getEntries().isEmpty())
    }

    @Test
    fun `addEntry persists entry to storage`() {
        manager.addEntry(LeaderboardEntry("Alice", 100, 5))

        val entries = manager.getEntries()
        assertEquals(1, entries.size)
        assertEquals("Alice", entries[0].nickname)
        assertEquals(100, entries[0].score)
    }

    @Test
    fun `addEntry preserves multiple entries sorted by score`() {
        manager.addEntry(LeaderboardEntry("Low", 100, 5))
        manager.addEntry(LeaderboardEntry("High", 1000, 20))
        manager.addEntry(LeaderboardEntry("Mid", 500, 10))

        val entries = manager.getEntries()
        assertEquals(3, entries.size)
        assertEquals(1000, entries[0].score)
        assertEquals(500, entries[1].score)
        assertEquals(100, entries[2].score)
    }

    @Test
    fun `addEntry caps at MAX_ENTRIES`() {
        repeat(105) { i ->
            manager.addEntry(LeaderboardEntry("Player $i", i * 10, 1))
        }

        assertEquals(100, manager.getEntries().size)
    }

    @Test
    fun `addEntry keeps highest scores when at capacity`() {
        repeat(105) { i ->
            manager.addEntry(LeaderboardEntry("Player $i", i, 1))
        }

        val entries = manager.getEntries()
        // Top entry should be the highest score added (104)
        assertEquals(104, entries.first().score)
        // Lowest retained entry should be score 5 (i.e., dropped 0..4)
        assertEquals(5, entries.last().score)
    }

    @Test
    fun `getEntries returns empty list on corrupted JSON`() {
        storage.saveString("leaderboard_entries", "{not valid json")

        assertTrue(manager.getEntries().isEmpty())
    }

    @Test
    fun `clearLeaderboard wipes all entries`() {
        manager.addEntry(LeaderboardEntry("A", 100, 1))
        manager.addEntry(LeaderboardEntry("B", 200, 2))

        manager.clearLeaderboard()

        assertTrue(manager.getEntries().isEmpty())
    }

    @Test
    fun `getTopScore returns 0 for empty leaderboard`() {
        assertEquals(0, manager.getTopScore())
    }

    @Test
    fun `getTopScore returns highest score`() {
        manager.addEntry(LeaderboardEntry("A", 100, 1))
        manager.addEntry(LeaderboardEntry("B", 500, 1))
        manager.addEntry(LeaderboardEntry("C", 300, 1))

        assertEquals(500, manager.getTopScore())
    }

    @Test
    fun `getRank returns 1 for the highest score`() {
        manager.addEntry(LeaderboardEntry("A", 100, 1))
        manager.addEntry(LeaderboardEntry("B", 200, 1))

        assertEquals(1, manager.getRank(300))
    }

    @Test
    fun `getRank returns size+1 when score is below all entries`() {
        manager.addEntry(LeaderboardEntry("A", 500, 1))
        manager.addEntry(LeaderboardEntry("B", 400, 1))
        manager.addEntry(LeaderboardEntry("C", 300, 1))

        assertEquals(4, manager.getRank(50))
    }

    @Test
    fun `getRank returns 1 for empty leaderboard`() {
        assertEquals(1, manager.getRank(100))
    }

    @Test
    fun `getAllTimeEntries respects limit`() {
        repeat(20) { i ->
            manager.addEntry(LeaderboardEntry("Player $i", i * 10, 1))
        }

        val top = manager.getAllTimeEntries(limit = 5)
        assertEquals(5, top.size)
        assertEquals(190, top[0].score)
    }

    @Test
    fun `getTodayEntries excludes entries from yesterday`() {
        val now = System.currentTimeMillis()
        val yesterday = now - 25L * 60 * 60 * 1000

        manager.addEntry(LeaderboardEntry("Today", 100, 1, Difficulty.MEDIUM, timestamp = now))
        manager.addEntry(LeaderboardEntry("Yesterday", 500, 1, Difficulty.MEDIUM, timestamp = yesterday))

        val todayOnly = manager.getTodayEntries()
        assertEquals(1, todayOnly.size)
        assertEquals("Today", todayOnly[0].nickname)
    }

    @Test
    fun `getMonthEntries excludes entries older than 31 days`() {
        val now = System.currentTimeMillis()
        val twoMonthsAgo = now - 62L * 24 * 60 * 60 * 1000

        manager.addEntry(LeaderboardEntry("Recent", 100, 1, Difficulty.MEDIUM, timestamp = now))
        manager.addEntry(LeaderboardEntry("Old", 500, 1, Difficulty.MEDIUM, timestamp = twoMonthsAgo))

        val monthOnly = manager.getMonthEntries()
        assertTrue(monthOnly.any { it.nickname == "Recent" })
        assertFalse(monthOnly.any { it.nickname == "Old" })
    }

    /** In-memory KeyValueStorage for testing. */
    private class FakeStorage : KeyValueStorage {
        private val map = mutableMapOf<String, String>()

        override fun saveString(key: String, value: String) {
            map[key] = value
        }

        override fun getString(key: String): String? = map[key]

        override fun remove(key: String) {
            map.remove(key)
        }

        override fun clear() {
            map.clear()
        }
    }
}
