package com.colormixlab.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.colormixlab.model.LeaderboardEntry
import com.colormixlab.ui.LeaderboardDialog

/**
 * In-game menu dialog.
 * Provides access to leaderboard, game restart, and other game options.
 *
 * Features:
 * - View leaderboard
 * - Restart game (with confirmation)
 * - Finish game early (with confirmation)
 * - Reset leaderboard (with confirmation)
 *
 * @param onDismiss Callback to close the menu
 * @param onRestartGame Callback to restart the game from level 1
 * @param onShowLeaderboard Callback when leaderboard should be shown
 * @param onResetLeaderboard Callback to clear all leaderboard entries
 * @param onFinishGame Callback to end game early and submit score
 * @param leaderboardEntries Current leaderboard entries
 */
@Composable
fun MenuDialog(
    onDismiss: () -> Unit,
    onRestartGame: () -> Unit,
    onShowLeaderboard: () -> Unit,
    onResetLeaderboard: () -> Unit,
    onFinishGame: () -> Unit,
    leaderboardEntries: List<LeaderboardEntry>
) {
    // State for various confirmation dialogs
    var showRestartConfirmation by remember { mutableStateOf(false) }
    var showResetLeaderboardConfirmation by remember { mutableStateOf(false) }
    var showFinishGameConfirmation by remember { mutableStateOf(false) }
    var showLeaderboard by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Menu title
                Text(
                    text = "Menu",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Divider(modifier = Modifier.padding(vertical = 6.dp))

                // Menu buttons
                LeaderboardButton(onClick = { showLeaderboard = true })
                RestartGameButton(onClick = { showRestartConfirmation = true })
                FinishGameButton(onClick = { showFinishGameConfirmation = true })
                ResetLeaderboardButton(onClick = { showResetLeaderboardConfirmation = true })
                CloseButton(onClick = onDismiss)
            }
        }
    }

    // Confirmation dialogs
    if (showRestartConfirmation) {
        RestartConfirmationDialog(
            onConfirm = {
                showRestartConfirmation = false
                onRestartGame()
            },
            onDismiss = { showRestartConfirmation = false }
        )
    }

    if (showResetLeaderboardConfirmation) {
        ResetLeaderboardConfirmationDialog(
            onConfirm = {
                showResetLeaderboardConfirmation = false
                onResetLeaderboard()
            },
            onDismiss = { showResetLeaderboardConfirmation = false }
        )
    }

    if (showFinishGameConfirmation) {
        FinishGameConfirmationDialog(
            onConfirm = {
                showFinishGameConfirmation = false
                onDismiss()
                onFinishGame()
            },
            onDismiss = { showFinishGameConfirmation = false }
        )
    }

    // Leaderboard dialog
    if (showLeaderboard) {
        LeaderboardDialog(
            entries = leaderboardEntries,
            onDismiss = { showLeaderboard = false }
        )
    }
}

/**
 * Leaderboard button.
 */
@Composable
private fun LeaderboardButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFD700)
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Leaderboard",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Leaderboard",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

/**
 * Restart game button.
 */
@Composable
private fun RestartGameButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF44336)
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Restart",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Restart Game",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Finish game button.
 */
@Composable
private fun FinishGameButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF9C27B0)
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Finish Game",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Reset leaderboard button.
 */
@Composable
private fun ResetLeaderboardButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFF44336)
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Reset Leaderboard",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Close menu button.
 */
@Composable
private fun CloseButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Close",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Restart game confirmation dialog.
 */
@Composable
private fun RestartConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Restart Game?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("This will reset your progress to Level 1 and clear your score. Are you sure?")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFF44336)
                )
            ) {
                Text("Restart", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Reset leaderboard confirmation dialog.
 */
@Composable
private fun ResetLeaderboardConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reset Leaderboard?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("This will permanently delete all leaderboard entries. This action cannot be undone!")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFF44336)
                )
            ) {
                Text("Delete All", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Finish game confirmation dialog.
 */
@Composable
private fun FinishGameConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Finish Game?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("This will end your current game and submit your score to the leaderboard.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF9C27B0)
                )
            ) {
                Text("Finish", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
