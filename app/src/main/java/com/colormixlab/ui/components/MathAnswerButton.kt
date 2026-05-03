package com.colormixlab.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.colormixlab.ui.theme.ColorMixLabTheme

/**
 * Answer button component for math challenge dialogs.
 * Displays an answer option with animations for correct/incorrect feedback.
 * Size and text scale automatically based on container size.
 *
 * @param answer The numeric answer to display
 * @param isCorrect Whether this is the correct answer
 * @param isSelected Whether this answer has been selected
 * @param showingAnswer Whether to show answer feedback
 * @param fontSize Font size for the answer text
 * @param modifier Modifier for customization
 * @param onClick Callback when button is clicked
 */
@Composable
fun MathAnswerButton(
    answer: Int,
    isCorrect: Boolean,
    isSelected: Boolean,
    showingAnswer: Boolean,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Animation state for shake effect on wrong answers
    val offsetX = remember { Animatable(0f) }

    // Trigger shake animation for wrong answers
    LaunchedEffect(showingAnswer, isSelected, isCorrect) {
        if (showingAnswer && isSelected && !isCorrect) {
            // Quick shake animation (3 cycles)
            repeat(3) {
                offsetX.animateTo(6f, animationSpec = tween(50))
                offsetX.animateTo(-6f, animationSpec = tween(50))
            }
            offsetX.animateTo(0f, animationSpec = tween(50))
        }
    }

    // Scale animation for selected state
    val scale by animateFloatAsState(
        targetValue = when {
            isSelected && showingAnswer -> if (isCorrect) 1.12f else 0.95f
            isSelected -> 0.95f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    // Pulse animation for correct answer
    val pulse by animateFloatAsState(
        targetValue = if (showingAnswer && isSelected && isCorrect) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "pulse"
    )

    // Visual styling based on answer state
    val backgroundColor = when {
        showingAnswer && isCorrect -> Color(0xFF4CAF50) // Green for correct
        showingAnswer && isSelected && !isCorrect -> Color(0xFFF44336) // Red for wrong
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        showingAnswer && isCorrect -> Color(0xFF2E7D32) // Dark green
        showingAnswer && isSelected && !isCorrect -> Color(0xFFC62828) // Dark red
        else -> MaterialTheme.colorScheme.outline
    }

    val borderWidth = if (showingAnswer && isCorrect) 3.dp else 2.dp
    val cornerRadius = (fontSize.value * 0.2f).dp.coerceIn(4.dp, 12.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .offset(x = offsetX.value.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect for correct answer
        if (showingAnswer && isSelected && isCorrect && pulse > 0.1f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.12f + pulse * 0.12f)
                    .background(
                        color = Color(0xFF4CAF50).copy(alpha = 0.2f * pulse),
                        shape = RoundedCornerShape(cornerRadius)
                    )
            )
        }

        // Main answer button
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(cornerRadius),
            border = BorderStroke(borderWidth, borderColor),
            enabled = !showingAnswer,
            contentPadding = PaddingValues(2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Answer number
                Text(
                    text = answer.toString(),
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = if (showingAnswer && (isCorrect || (isSelected && !isCorrect))) {
                        Color.White
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                // Feedback icons
                if (showingAnswer) {
                    val iconSize = (fontSize.value * 0.4f).dp.coerceIn(10.dp, 24.dp)
                    when {
                        isSelected && isCorrect -> FeedbackIcon(Icons.Filled.Check, "Correct", iconSize)
                        isSelected && !isCorrect -> FeedbackIcon(Icons.Filled.Close, "Wrong", iconSize)
                        isCorrect && !isSelected -> FeedbackIcon(Icons.Filled.Check, "Correct Answer", iconSize)
                    }
                }
            }
        }
    }
}

/**
 * Helper composable for displaying feedback icons.
 */
@Composable
private fun BoxScope.FeedbackIcon(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    iconSize: androidx.compose.ui.unit.Dp
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = Color.White,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(2.dp)
            .size(iconSize)
    )
}

@Preview(name = "MathAnswer — idle", showBackground = true, widthDp = 120, heightDp = 80)
@Composable
private fun MathAnswerButtonIdlePreview() {
    ColorMixLabTheme {
        MathAnswerButton(
            answer = 42,
            isCorrect = false,
            isSelected = false,
            showingAnswer = false,
            fontSize = 32.sp,
            modifier = Modifier.fillMaxSize(),
            onClick = {}
        )
    }
}

@Preview(name = "MathAnswer — correct selected", showBackground = true, widthDp = 120, heightDp = 80)
@Composable
private fun MathAnswerButtonCorrectPreview() {
    ColorMixLabTheme {
        MathAnswerButton(
            answer = 42,
            isCorrect = true,
            isSelected = true,
            showingAnswer = true,
            fontSize = 32.sp,
            modifier = Modifier.fillMaxSize(),
            onClick = {}
        )
    }
}

@Preview(name = "MathAnswer — wrong selected", showBackground = true, widthDp = 120, heightDp = 80)
@Composable
private fun MathAnswerButtonWrongPreview() {
    ColorMixLabTheme {
        MathAnswerButton(
            answer = 41,
            isCorrect = false,
            isSelected = true,
            showingAnswer = true,
            fontSize = 32.sp,
            modifier = Modifier.fillMaxSize(),
            onClick = {}
        )
    }
}
