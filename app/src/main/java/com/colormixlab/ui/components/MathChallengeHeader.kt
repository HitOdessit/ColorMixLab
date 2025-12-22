package com.colormixlab.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.colormixlab.game.MathChallengeType
import com.colormixlab.model.GameColor
import com.colormixlab.utils.MathChallengeTimer

/**
 * Header component for math challenge dialog.
 * Displays the challenge title, instructions, and progress.
 *
 * @param challengeType The type of challenge (color unlock, milestone, etc.)
 * @param nextColorToUnlock The color that will be unlocked upon completion
 * @param correctCount Number of questions answered correctly
 * @param requiredCount Number of correct answers required
 * @param timeRemaining Time remaining in seconds (null if no timer)
 */
@Composable
fun MathChallengeHeader(
    challengeType: MathChallengeType,
    nextColorToUnlock: GameColor?,
    correctCount: Int,
    requiredCount: Int = 3,
    timeRemaining: Int? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Challenge title
        val headerText = when (challengeType) {
            MathChallengeType.COLOR_UNLOCK -> "🔓 Unlock ${nextColorToUnlock?.name ?: "New Color"}!"
            MathChallengeType.MILESTONE -> "🎯 Keep Going!"
            MathChallengeType.NONE -> ""
        }

        Text(
            text = headerText,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        // Instructions
        Text(
            text = "Answer $requiredCount questions correctly",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Penalty warning
        Text(
            text = "❌ Wrong answer: -75 points",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Progress card with timer
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Correct answer count
                    Text(
                        text = "Correct: $correctCount/$requiredCount",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    // Timer display (if enabled)
                    timeRemaining?.let { time ->
                        TimerDisplay(time)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar
                LinearProgressIndicator(
                    progress = correctCount.toFloat() / requiredCount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

/**
 * Timer display component with color-coded urgency indicators.
 *
 * @param timeRemaining Time remaining in seconds
 */
@Composable
private fun TimerDisplay(timeRemaining: Int) {
    val timerColor = when {
        timeRemaining <= MathChallengeTimer.getWarningThreshold() -> Color.Red
        timeRemaining <= MathChallengeTimer.getCriticalThreshold() -> Color(0xFFFF9800) // Orange
        else -> MaterialTheme.colorScheme.primary
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "⏱",
            fontSize = 16.sp
        )
        Text(
            text = "$timeRemaining",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = timerColor
        )
        Text(
            text = "s",
            fontSize = 12.sp,
            color = timerColor.copy(alpha = 0.7f)
        )
    }
}
