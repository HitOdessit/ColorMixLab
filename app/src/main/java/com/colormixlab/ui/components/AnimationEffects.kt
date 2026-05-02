package com.colormixlab.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class ConfettiParticle(
    val startX: Float,
    val startY: Float,
    val color: Color,
    val size: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotation: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    particleCount: Int = 30  // Reduced from 50
) {
    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                startX = Random.nextFloat(),
                startY = -0.1f,
                color = listOf(
                    Color(0xFFE74C3C), // Red
                    Color(0xFF3498DB), // Blue
                    Color(0xFFF1C40F), // Yellow
                    Color(0xFFE67E22), // Orange
                    Color(0xFF9B59B6), // Purple
                    Color(0xFF2ECC71)  // Green
                ).random(),
                size = Random.nextFloat() * 12f + 8f,  // Slightly smaller
                velocityX = Random.nextFloat() * 3f - 1.5f,
                velocityY = Random.nextFloat() * 2.5f + 1.5f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 8f - 4f
            )
        }
    }
    
    // Single animation instead of infinite
    val animationProgress = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(2500, easing = LinearEasing)  // Reduced from 3000ms
        )
    }
    
    val progress = animationProgress.value
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val currentY = particle.startY + particle.velocityY * progress
            val currentX = particle.startX + particle.velocityX * progress * 0.1f
            val currentRotation = particle.rotation + particle.rotationSpeed * progress * 360f
            
            if (currentY < 1.2f) {
                rotate(currentRotation, pivot = Offset(currentX * size.width, currentY * size.height)) {
                    drawRect(
                        color = particle.color.copy(alpha = 1f - progress * 0.5f),  // Fade out
                        topLeft = Offset(
                            currentX * size.width - particle.size / 2,
                            currentY * size.height - particle.size / 2
                        ),
                        size = androidx.compose.ui.geometry.Size(particle.size, particle.size)
                    )
                }
            }
        }
    }
}

data class SparkleParticle(
    val angle: Float,
    val speed: Float,
    val color: Color
)

@Composable
fun SparkleEffect(
    modifier: Modifier = Modifier,
    sparkleCount: Int = 12  // Reduced from 20
) {
    val sparkles = remember {
        List(sparkleCount) {
            val angle = (360f / sparkleCount) * it
            SparkleParticle(
                angle = angle,
                speed = Random.nextFloat() * 0.2f + 0.15f,
                color = listOf(
                    Color(0xFFFDD835), // Bright Yellow
                    Color(0xFFFFEB3B), // Yellow
                    Color(0xFFFFFFFF), // White
                    Color(0xFFFFC107)  // Amber
                ).random()
            )
        }
    }
    
    // Single animation instead of infinite
    val animationProgress = remember { Animatable(0f) }
    val animationAlpha = remember { Animatable(1f) }
    
    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)  // Reduced from 1500ms
        )
        animationAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
    }
    
    val progress = animationProgress.value
    val alpha = animationAlpha.value
    
    Canvas(modifier = modifier.fillMaxSize()) {
        sparkles.forEach { sparkle ->
            val distance = progress * sparkle.speed * size.minDimension
            val centerX = 0.5f * size.width
            val centerY = 0.5f * size.height
            
            val radians = Math.toRadians(sparkle.angle.toDouble())
            val currentX = centerX + (cos(radians) * distance).toFloat()
            val currentY = centerY + (sin(radians) * distance).toFloat()
            
            val sparkleSize = (1f - progress) * 10f  // Slightly smaller
            
            drawCircle(
                color = sparkle.color.copy(alpha = alpha),
                radius = sparkleSize,
                center = Offset(currentX, currentY)
            )
            
            // Draw star points
            val lineAlpha = alpha * 0.6f
            drawLine(
                color = sparkle.color.copy(alpha = lineAlpha),
                start = Offset(currentX - sparkleSize * 1.2f, currentY),
                end = Offset(currentX + sparkleSize * 1.2f, currentY),
                strokeWidth = 1.5f
            )
            drawLine(
                color = sparkle.color.copy(alpha = lineAlpha),
                start = Offset(currentX, currentY - sparkleSize * 1.2f),
                end = Offset(currentX, currentY + sparkleSize * 1.2f),
                strokeWidth = 1.5f
            )
        }
    }
}



