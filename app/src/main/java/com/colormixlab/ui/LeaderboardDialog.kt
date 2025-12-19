package com.colormixlab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.colormixlab.model.LeaderboardEntry
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LeaderboardDialog(
    entries: List<LeaderboardEntry>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.99f)
                .fillMaxHeight(0.88f)
                .padding(6.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Leaderboard",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Leaderboard",
                            fontSize = 26.sp,
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

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Entries
                if (entries.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No scores yet!\nBe the first to complete the game!",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(entries) { index, entry ->
                            LeaderboardEntryItem(
                                rank = index + 1,
                                entry = entry
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
    entry: LeaderboardEntry
) {
    val backgroundColor = when (rank) {
        1 -> Color(0xFFFFD700).copy(alpha = 0.2f) // Gold
        2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f) // Silver
        3 -> Color(0xFFCD7F32).copy(alpha = 0.2f) // Bronze
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    }

    val rankColor = when (rank) {
        1 -> Color(0xFFB8860B) // Dark gold for better contrast
        2 -> Color(0xFF808080) // Dark silver for better contrast
        3 -> Color(0xFF8B4513) // Dark bronze for better contrast
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
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Rank
            Text(
                text = "#$rank",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = rankColor,
                modifier = Modifier.width(38.dp)
            )

            // Nickname and details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = entry.nickname,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$difficultyEmoji $difficultyName • Lvl ${entry.level}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Visible
                )
                Text(
                    text = formattedDate,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }

            // Score
            Text(
                text = "${entry.score}",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

