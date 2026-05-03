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
 * @param availableWidth Width available for layout calculations
 */
@Composable
fun MathChallengeHeader(
    challengeType: MathChallengeType,
    nextColorToUnlock: GameColor?,
    correctCount: Int,
    requiredCount: Int = 3,
    timeRemaining: Int? = null,
    availableWidth: androidx.compose.ui.unit.Dp
) {
    // Calculate sizes based on available width
    val titleFontSize = androidx.compose.ui.unit.TextUnit(
        (availableWidth.value * 0.065f).coerceIn(22f, 32f),
        androidx.compose.ui.unit.TextUnitType.Sp
    )
    val instructionFontSize = androidx.compose.ui.unit.TextUnit(
        (availableWidth.value * 0.038f).coerceIn(14f, 18f),
        androidx.compose.ui.unit.TextUnitType.Sp
    )
    val warningFontSize = androidx.compose.ui.unit.TextUnit(
        (availableWidth.value * 0.033f).coerceIn(12f, 16f),
        androidx.compose.ui.unit.TextUnitType.Sp
    )
    val countFontSize = androidx.compose.ui.unit.TextUnit(
        (availableWidth.value * 0.048f).coerceIn(18f, 24f),
        androidx.compose.ui.unit.TextUnitType.Sp
    )
    val progressHeight = (availableWidth.value * 0.024f).dp.coerceIn(8.dp, 12.dp)
    val verticalSpacing = (availableWidth.value * 0.018f).dp.coerceIn(6.dp, 10.dp)
    val cardPadding = (availableWidth.value * 0.028f).dp.coerceIn(10.dp, 14.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        // Challenge title
        val headerText = when (challengeType) {
            MathChallengeType.COLOR_UNLOCK -> "🔓 Unlock ${nextColorToUnlock?.name ?: "New Color"}!"
            MathChallengeType.MILESTONE -> "🎯 Keep Going!"
            MathChallengeType.NONE -> ""
        }

        Text(
            text = headerText,
            fontSize = titleFontSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            lineHeight = titleFontSize * 1.15f
        )

        // Instructions
        Text(
            text = "Answer $requiredCount correctly",
            fontSize = instructionFontSize,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = instructionFontSize * 1.15f
        )

        // Penalty warning
        Text(
            text = "❌ Wrong: -75 pts",
            fontSize = warningFontSize,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Medium,
            lineHeight = warningFontSize * 1.15f
        )

        Spacer(modifier = Modifier.height(verticalSpacing))

        // Progress card with timer
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(cardPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(verticalSpacing)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Correct answer count
                    Text(
                        text = "$correctCount/$requiredCount",
                        fontSize = countFontSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    // Timer display (if enabled)
                    timeRemaining?.let { time ->
                        TimerDisplay(time, availableWidth)
                    }
                }

                // Progress bar
                LinearProgressIndicator(
                    progress = { correctCount.toFloat() / requiredCount },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(progressHeight),
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
 * @param availableWidth Width available for layout calculations
 */
@Composable
private fun TimerDisplay(timeRemaining: Int, availableWidth: androidx.compose.ui.unit.Dp) {
    // Calculate sizes based on available width
    val emojiSize = androidx.compose.ui.unit.TextUnit(
        (availableWidth.value * 0.042f).coerceIn(14f, 22f),
        androidx.compose.ui.unit.TextUnitType.Sp
    )
    val timeFontSize = androidx.compose.ui.unit.TextUnit(
        (availableWidth.value * 0.048f).coerceIn(16f, 24f),
        androidx.compose.ui.unit.TextUnitType.Sp
    )
    val unitFontSize = androidx.compose.ui.unit.TextUnit(
        (availableWidth.value * 0.032f).coerceIn(11f, 16f),
        androidx.compose.ui.unit.TextUnitType.Sp
    )

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
            fontSize = emojiSize
        )
        Text(
            text = "$timeRemaining",
            fontSize = timeFontSize,
            fontWeight = FontWeight.Bold,
            color = timerColor
        )
        Text(
            text = "s",
            fontSize = unitFontSize,
            color = timerColor.copy(alpha = 0.7f)
        )
    }
}
