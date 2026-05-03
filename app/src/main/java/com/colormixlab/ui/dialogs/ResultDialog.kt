package com.colormixlab.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.colormixlab.game.GameConstants
import com.colormixlab.model.GameColor
import com.colormixlab.ui.components.ConfettiEffect
import com.colormixlab.ui.components.SparkleEffect
import com.colormixlab.ui.theme.ColorMixLabTheme
import com.colormixlab.utils.toComposeColor

/**
 * Dialog shown after completing a level attempt.
 * Displays the match result, points earned, and any unlocked colors.
 *
 * Features:
 * - Animated feedback (confetti for perfect match, sparkles for high score)
 * - Points breakdown (base points + time bonus)
 * - Color unlock notification
 * - Next level or retry action
 *
 * @param similarity Color similarity percentage (0.0 to 1.0)
 * @param isSuccess Whether the level was passed (similarity >= 80%)
 * @param level Current level number
 * @param basePoints Points earned from similarity
 * @param timeBonus Bonus points for quick completion
 * @param unlockedColors List of currently unlocked colors
 * @param onNextLevel Callback to advance to next level
 * @param onRetry Callback to retry current level
 * @param getMessage Function to generate result message based on similarity
 * @param getEmoji Function to generate emoji based on similarity
 */
@Composable
fun ResultDialog(
    similarity: Float,
    isSuccess: Boolean,
    level: Int,
    basePoints: Int,
    timeBonus: Int,
    unlockedColors: List<GameColor>,
    onNextLevel: () -> Unit,
    onRetry: () -> Unit,
    getMessage: (Float) -> String,
    getEmoji: (Float) -> String
) {
    Dialog(onDismissRequest = { /* Prevent dismissal */ }) {
        Box(modifier = Modifier.fillMaxWidth(0.98f)) {
            // Celebration effects
            when {
                similarity >= 1.0f -> {
                    androidx.compose.runtime.key("confetti-$level") {
                        ConfettiEffect(modifier = Modifier.fillMaxSize())
                    }
                }
                similarity >= 0.80f && isSuccess -> {
                    androidx.compose.runtime.key("sparkle-$level") {
                        SparkleEffect(modifier = Modifier.fillMaxSize())
                    }
                }
            }

            ResultDialogContent(
                similarity = similarity,
                isSuccess = isSuccess,
                level = level,
                basePoints = basePoints,
                timeBonus = timeBonus,
                unlockedColors = unlockedColors,
                getMessage = getMessage,
                getEmoji = getEmoji,
                onAction = if (isSuccess) onNextLevel else onRetry
            )
        }
    }
}

/**
 * Points breakdown display showing base points, time bonus, and total.
 */
@Composable
private fun PointsBreakdown(
    basePoints: Int,
    timeBonus: Int,
    totalPoints: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Base points
        Text(
            text = if (basePoints >= 0) "+$basePoints points" else "$basePoints points",
            fontSize = 22.sp,
            color = if (basePoints >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Time bonus (only show if > 0)
        if (timeBonus > 0) {
            Text(
                text = "⚡ +$timeBonus time bonus",
                fontSize = 18.sp,
                color = Color(0xFFFF9800),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Total points
            Text(
                text = "Total: +$totalPoints points",
                fontSize = 20.sp,
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Snapshot-friendly inline rendering of the result card content (no `Dialog`
 * wrapper). Used for `@Preview` and Paparazzi snapshot tests; the production
 * call site wraps this content in a `Dialog`.
 */
@Composable
internal fun ResultDialogContent(
    similarity: Float,
    isSuccess: Boolean,
    level: Int,
    basePoints: Int,
    timeBonus: Int,
    unlockedColors: List<GameColor>,
    getMessage: (Float) -> String,
    getEmoji: (Float) -> String,
    onAction: () -> Unit
) {
    val message = getMessage(similarity)
    val emoji = getEmoji(similarity)
    val percentage = (similarity * 100).toInt()
    val totalPoints = basePoints + timeBonus

    val newlyUnlockedColor = if (isSuccess && (level + 1) in GameConstants.COLOR_UNLOCK_LEVELS) {
        val nextLevel = level + 1
        GameColor.getAllColors().find { it.unlockLevel == nextLevel }
    } else {
        null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 60.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = message,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSuccess) MaterialTheme.colorScheme.primary else Color(0xFFF44336),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Similarity: $percentage%",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )
            PointsBreakdown(
                basePoints = basePoints,
                timeBonus = timeBonus,
                totalPoints = totalPoints
            )
            Text(
                text = if (isSuccess) "Level $level Complete!" else "Try Level $level Again",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            newlyUnlockedColor?.let { color ->
                ColorUnlockNotification(color)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSuccess) MaterialTheme.colorScheme.primary else Color(0xFFF44336)
                )
            ) {
                Text(
                    text = if (isSuccess) "Next Level →" else "Try Again",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(name = "ResultDialog — success, no unlock", showBackground = true, widthDp = 380, heightDp = 720)
@Composable
private fun ResultDialogSuccessPreview() {
    ColorMixLabTheme {
        ResultDialogContent(
            similarity = 0.92f,
            isSuccess = true,
            level = 2,
            basePoints = 120,
            timeBonus = 25,
            unlockedColors = listOf(GameColor.Red, GameColor.Blue, GameColor.Green),
            getMessage = { "Great Match!" },
            getEmoji = { "🎉" },
            onAction = {}
        )
    }
}

@Preview(name = "ResultDialog — failure", showBackground = true, widthDp = 380, heightDp = 720)
@Composable
private fun ResultDialogFailurePreview() {
    ColorMixLabTheme {
        ResultDialogContent(
            similarity = 0.62f,
            isSuccess = false,
            level = 5,
            basePoints = -25,
            timeBonus = 0,
            unlockedColors = listOf(GameColor.Red, GameColor.Blue, GameColor.Green, GameColor.Yellow),
            getMessage = { "Not quite — try again" },
            getEmoji = { "😅" },
            onAction = {}
        )
    }
}

/**
 * Color unlock notification card with preview and name.
 */
@Composable
private fun ColorUnlockNotification(color: GameColor) {
    Spacer(modifier = Modifier.height(8.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.toComposeColor().copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Color preview box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = color.toComposeColor(),
                        shape = RoundedCornerShape(12.dp)
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Unlock message
            Text(
                text = "🎨 ${color.name} Unlocked!",
                fontSize = 20.sp,
                color = color.toComposeColor(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
