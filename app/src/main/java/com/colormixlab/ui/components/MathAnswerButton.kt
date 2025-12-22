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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Answer button component for math challenge dialogs.
 * Displays an answer option with animations for correct/incorrect feedback.
 *
 * @param answer The numeric answer to display
 * @param isCorrect Whether this is the correct answer
 * @param isSelected Whether this answer has been selected
 * @param showingAnswer Whether to show answer feedback
 * @param modifier Modifier for customization
 * @param onClick Callback when button is clicked
 */
@Composable
fun MathAnswerButton(
    answer: Int,
    isCorrect: Boolean,
    isSelected: Boolean,
    showingAnswer: Boolean,
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
            isSelected && showingAnswer -> if (isCorrect) 1.15f else 0.95f
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

    val borderWidth = if (showingAnswer && isCorrect) 4.dp else 2.dp

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale)
            .offset(x = offsetX.value.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect for correct answer
        if (showingAnswer && isSelected && isCorrect && pulse > 0.1f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.15f + pulse * 0.15f)
                    .background(
                        color = Color(0xFF4CAF50).copy(alpha = 0.25f * pulse),
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }

        // Main answer button
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(borderWidth, borderColor),
            enabled = !showingAnswer,
            contentPadding = PaddingValues(4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Answer number
                Text(
                    text = answer.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (showingAnswer && (isCorrect || (isSelected && !isCorrect))) {
                        Color.White
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                // Feedback icons
                if (showingAnswer) {
                    when {
                        isSelected && isCorrect -> FeedbackIcon(Icons.Filled.Check, "Correct")
                        isSelected && !isCorrect -> FeedbackIcon(Icons.Filled.Close, "Wrong")
                        isCorrect && !isSelected -> FeedbackIcon(Icons.Filled.Check, "Correct Answer")
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
    contentDescription: String
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = Color.White,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(2.dp)
            .size(16.dp)
    )
}
