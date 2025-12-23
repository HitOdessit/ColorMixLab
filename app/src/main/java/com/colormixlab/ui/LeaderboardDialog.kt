package com.colormixlab.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.colormixlab.data.LeaderboardManager
import com.colormixlab.model.LeaderboardEntry
import java.text.SimpleDateFormat
import java.util.*

enum class LeaderboardTab {
    TODAY, WEEK, MONTH, ALL_TIME
}

@Composable
fun LeaderboardDialog(
    entries: List<LeaderboardEntry>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val leaderboardManager = remember {
        LeaderboardManager(com.colormixlab.platform.PlatformStorage(context))
    }
    var selectedTab by remember { mutableStateOf(LeaderboardTab.TODAY) }
    
    // Get filtered entries based on selected tab
    val displayedEntries = remember(selectedTab) {
        when (selectedTab) {
            LeaderboardTab.TODAY -> leaderboardManager.getTodayEntries(5)
            LeaderboardTab.WEEK -> leaderboardManager.getWeekEntries(5)
            LeaderboardTab.MONTH -> leaderboardManager.getMonthEntries(5)
            LeaderboardTab.ALL_TIME -> leaderboardManager.getAllTimeEntries(10)
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Leaderboard",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Leaderboard",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Tab Row
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 8.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTab == LeaderboardTab.TODAY,
                        onClick = { selectedTab = LeaderboardTab.TODAY },
                        text = { 
                            Text(
                                "Today",
                                fontSize = 15.sp,
                                fontWeight = if (selectedTab == LeaderboardTab.TODAY) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == LeaderboardTab.WEEK,
                        onClick = { selectedTab = LeaderboardTab.WEEK },
                        text = { 
                            Text(
                                "This Week",
                                fontSize = 15.sp,
                                fontWeight = if (selectedTab == LeaderboardTab.WEEK) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == LeaderboardTab.MONTH,
                        onClick = { selectedTab = LeaderboardTab.MONTH },
                        text = { 
                            Text(
                                "This Month",
                                fontSize = 15.sp,
                                fontWeight = if (selectedTab == LeaderboardTab.MONTH) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == LeaderboardTab.ALL_TIME,
                        onClick = { selectedTab = LeaderboardTab.ALL_TIME },
                        text = { 
                            Text(
                                "All Time",
                                fontSize = 15.sp,
                                fontWeight = if (selectedTab == LeaderboardTab.ALL_TIME) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }

                Divider()

                // Entries
                if (displayedEntries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🏆",
                                fontSize = 48.sp
                            )
                            Text(
                                text = "No scores yet!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = when (selectedTab) {
                                    LeaderboardTab.TODAY -> "Be the first today!"
                                    LeaderboardTab.WEEK -> "Be the first this week!"
                                    LeaderboardTab.MONTH -> "Be the first this month!"
                                    LeaderboardTab.ALL_TIME -> "Complete a game to appear here!"
                                },
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        itemsIndexed(displayedEntries) { index, entry ->
                            LeaderboardEntryItem(
                                rank = index + 1,
                                entry = entry,
                                showDate = selectedTab != LeaderboardTab.ALL_TIME
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardEntryItem(
    rank: Int,
    entry: LeaderboardEntry,
    showDate: Boolean = true
) {
    val backgroundColor = when (rank) {
        1 -> Color(0xFFFFD700).copy(alpha = 0.2f) // Gold
        2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f) // Silver
        3 -> Color(0xFFCD7F32).copy(alpha = 0.2f) // Bronze
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val rankColor = when (rank) {
        1 -> Color(0xFFB8860B) // Dark gold
        2 -> Color(0xFF808080) // Dark silver
        3 -> Color(0xFF8B4513) // Dark bronze
        else -> MaterialTheme.colorScheme.onSurface
    }

    val difficultyEmoji = when (entry.difficulty) {
        com.colormixlab.game.Difficulty.EASY -> "🟢"
        com.colormixlab.game.Difficulty.MEDIUM -> "🟡"
        com.colormixlab.game.Difficulty.HARD -> "🔴"
    }

    val difficultyName = when (entry.difficulty) {
        com.colormixlab.game.Difficulty.EASY -> "Easy"
        com.colormixlab.game.Difficulty.MEDIUM -> "Medium"
        com.colormixlab.game.Difficulty.HARD -> "Hard"
    }

    // Format timestamp
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(entry.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Rank with trophy for top 3
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(44.dp)
            ) {
                if (rank <= 3) {
                    Text(
                        text = when (rank) {
                            1 -> "🥇"
                            2 -> "🥈"
                            3 -> "🥉"
                            else -> ""
                        },
                        fontSize = 22.sp
                    )
                }
                Text(
                    text = "#$rank",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = rankColor
                )
            }

            // Nickname and details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = entry.nickname,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$difficultyEmoji $difficultyName",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "•",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Lvl ${entry.level}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
                if (showDate) {
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Score
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${entry.score}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "pts",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
        }
    }
}
