package com.colormixlab.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.colormixlab.model.GameColor
import com.colormixlab.model.PlatformColor
import com.colormixlab.utils.toComposeColor
import kotlinx.coroutines.launch
import kotlin.math.sqrt

data class AnimatedSlice(
    val color: GameColor,
    val angle: Animatable<Float, *> = Animatable(0f),
    val id: String = "${color.name}_${System.nanoTime()}"
)

@Composable
fun MixingBowl(
    drops: Map<GameColor, Int>,
    mixedColor: PlatformColor,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var animatedSlices by remember { mutableStateOf<List<AnimatedSlice>>(emptyList()) }

    // Animate the mixed color - convert to Compose Color
    val animatedMixedColor by animateColorAsState(
        targetValue = mixedColor.toComposeColor(),
        animationSpec = tween(durationMillis = 300),
        label = "mixedColor"
    )

    // Update slices when drops change
    LaunchedEffect(drops) {
        val newSliceCount = drops.values.sum()
        val currentSliceCount = animatedSlices.size
        val targetAngle = if (newSliceCount > 0) 360f / newSliceCount else 0f

        if (newSliceCount == 0) {
            // Clear all slices
            animatedSlices = emptyList()
        } else if (newSliceCount > currentSliceCount) {
            // Add new slices
            val newSlices = drops.flatMap { (color, count) ->
                List(count) { AnimatedSlice(color) }
            }

            animatedSlices = newSlices

            // Animate all slices to their target angle
            newSlices.forEachIndexed { index, slice ->
                scope.launch {
                    if (index >= currentSliceCount) {
                        // New slice: start from 0 and grow
                        slice.angle.snapTo(0f)
                        slice.angle.animateTo(
                            targetValue = targetAngle,
                            animationSpec = spring(
                                dampingRatio = 0.7f,
                                stiffness = 300f
                            )
                        )
                    } else {
                        // Existing slice: shrink to new size
                        slice.angle.animateTo(
                            targetValue = targetAngle,
                            animationSpec = spring(
                                dampingRatio = 0.7f,
                                stiffness = 300f
                            )
                        )
                    }
                }
            }
        } else if (newSliceCount < currentSliceCount) {
            // Remove slices
            val newSlices = drops.flatMap { (color, count) ->
                List(count) { AnimatedSlice(color) }
            }

            // Keep existing slices up to new count and update their angles
            val retained = animatedSlices.take(newSliceCount).zip(newSlices).map { (old, new) ->
                AnimatedSlice(new.color, old.angle, new.id)
            }

            animatedSlices = retained

            // Animate remaining slices to new target angle
            retained.forEach { slice ->
                scope.launch {
                    slice.angle.animateTo(
                        targetValue = targetAngle,
                        animationSpec = spring(
                            dampingRatio = 0.7f,
                            stiffness = 300f
                        )
                    )
                }
            }
        }
    }

    val isEmpty = animatedSlices.isEmpty()

    Box(
        modifier = modifier.size(186.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(203.dp)
                .border(6.dp, MaterialTheme.colorScheme.onBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(186.dp)) {
                val radius = size.minDimension / 2
                val centerX = size.width / 2
                val centerY = size.height / 2

                val innerRadius = 69.3f * density

                // Draw the mixed color in the center
                drawCircle(
                    color = animatedMixedColor,
                    radius = innerRadius,
                    center = androidx.compose.ui.geometry.Offset(centerX, centerY)
                )

                // Draw individual color slices in the outer ring
                if (!isEmpty) {
                    var currentAngle = -90f // Start from top

                    animatedSlices.forEach { slice ->
                        val color = slice.color.toComposeColor()
                        val sweepAngle = slice.angle.value

                        // Draw arc as a ring segment (outer ring only)
                        drawArc(
                            color = color,
                            startAngle = currentAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                centerX - radius,
                                centerY - radius
                            ),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                        )

                        // Cover the inner part to create ring effect
                        drawArc(
                            color = animatedMixedColor,
                            startAngle = currentAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                centerX - innerRadius,
                                centerY - innerRadius
                            ),
                            size = androidx.compose.ui.geometry.Size(innerRadius * 2, innerRadius * 2)
                        )

                        currentAngle += sweepAngle
                    }
                }
            }

            // Show "Empty" text when no drops
            if (isEmpty && mixedColor == PlatformColor.White) {
                Text(
                    text = "Empty",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }
        }
    }
}

