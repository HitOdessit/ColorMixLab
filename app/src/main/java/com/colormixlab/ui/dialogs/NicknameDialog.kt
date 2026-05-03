package com.colormixlab.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Dialog for collecting player nickname after game completion.
 * Displays final score and level reached, then collects nickname for leaderboard.
 *
 * Features:
 * - Displays celebration emoji and final stats
 * - Nickname input with 15 character limit
 * - Option to skip and submit as "Anonymous"
 * - Prevents dismissal until submission
 *
 * @param finalScore The player's final score
 * @param finalLevel The highest level reached
 * @param onSubmit Callback with the submitted nickname
 */
@Composable
fun NicknameDialog(
    finalScore: Int,
    finalLevel: Int,
    onSubmit: (String) -> Unit
) {
    var nickname by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { /* Prevent dismissal */ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Celebration emoji
                Text(
                    text = "🎉",
                    fontSize = 64.sp,
                    textAlign = TextAlign.Center
                )

                // Title
                Text(
                    text = "Game Complete!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                // Level completion message
                Text(
                    text = if (finalLevel >= 30) {
                        "You completed all $finalLevel levels!"
                    } else {
                        "You reached level $finalLevel!"
                    },
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Final score card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Final Score",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$finalScore",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Nickname input section
                Text(
                    text = "Enter your name for the leaderboard:",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = nickname,
                    onValueChange = { newValue ->
                        // Limit to 15 characters
                        if (newValue.length <= 15) {
                            nickname = newValue
                        }
                    },
                    label = { Text("Nickname") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Character counter
                Text(
                    text = "${nickname.length}/15 characters",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Submit button
                Button(
                    onClick = {
                        val finalNickname = nickname.trim().ifEmpty { "Anonymous" }
                        onSubmit(finalNickname)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Submit Score",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Skip button
                TextButton(
                    onClick = { onSubmit("Anonymous") }
                ) {
                    Text("Skip (submit as Anonymous)", fontSize = 13.sp)
                }
            }
        }
    }
}
