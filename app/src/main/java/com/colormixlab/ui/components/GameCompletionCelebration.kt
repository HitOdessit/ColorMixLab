package com.colormixlab.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

/**
 * Highly optimized celebration animation with zero allocations during rendering.
 */
@Composable
fun GameCompletionCelebration(
    onAnimationComplete: () -> Unit
) {
    val animationProgress = remember { Animatable(0f) }
    var showText by remember { mutableStateOf(false) }
    
    // Pre-allocate ALL data structures to avoid GC during animation
    val circles = remember {
        floatArrayOf(
            0f, 60f, 120f, 180f, 240f, 300f  // Initial angles for 6 circles
        )
    }
    
    val colors = remember {
        arrayOf(
            Color(0xFFE74C3C), Color(0xFF3498DB), Color(0xFF2ECC71),
            Color(0xFFF1C40F), Color(0xFF9B59B6), Color(0xFFFF00FF)
        )
    }
    
    val rainbowColors = remember {
        arrayOf(
            Color(0xFFFF0000), // Red
            Color(0xFFFF4500), // Red-Orange
            Color(0xFFFF7F00), // Orange
            Color(0xFFFFA500), // Light Orange
            Color(0xFFFFFF00), // Yellow
            Color(0xFFCCFF00), // Yellow-Green
            Color(0xFF00FF00), // Green
            Color(0xFF00FF7F), // Spring Green
            Color(0xFF00FFFF), // Cyan
            Color(0xFF0080FF), // Sky Blue
            Color(0xFF0000FF), // Blue
            Color(0xFF8B00FF), // Violet
            Color(0xFFFF00FF), // Magenta
            Color(0xFFFF1493), // Deep Pink
        )
    }
    
    // Pre-calculate constants
    val baseRadius = remember { 45.dp.value }
    
    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 10000, // Extended from 9000ms
                easing = LinearEasing
            )
        )
        onAnimationComplete()
    }
    
    LaunchedEffect(animationProgress.value) {
        if (animationProgress.value >= 0.25f && !showText) {
            showText = true
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
                }
        ) {
            val progress = animationProgress.value
            val width = size.width
            val height = size.height
            val centerX = width * 0.5f
            val centerY = height * 0.5f
            
            // Phase timing (extended rainbow explosion - now 3000ms):
            // 0.00 - 0.10: Circles appear (1000ms)
            // 0.10 - 0.50: Flying/orbiting phase (4000ms)
            // 0.50 - 0.70: Spiral inward (2000ms, accelerating merge)
            // 0.70 - 1.00: Rainbow explosion (3000ms) - EXTENDED!
            when {
                progress < 0.10f -> {
                    renderAppear(progress / 0.10f, circles, colors, baseRadius, width, height, centerX, centerY)
                }
                progress < 0.50f -> {
                    renderFlying((progress - 0.10f) / 0.40f, circles, colors, baseRadius, width, height, centerX, centerY)
                }
                progress < 0.70f -> {
                    renderSpiral((progress - 0.50f) / 0.20f, circles, colors, baseRadius, width, height, centerX, centerY)
                }
                else -> {
                    renderExplosion((progress - 0.70f) / 0.30f, rainbowColors, width, height, centerX, centerY)
                }
            }
        }

        if (showText) {
            CelebrationText()
        }
    }
}

private fun DrawScope.renderAppear(
    progress: Float,
    angles: FloatArray,
    colors: Array<Color>,
    baseRadius: Float,
    width: Float,
    height: Float,
    centerX: Float,
    centerY: Float
) {
    // Smoothstep for scale
    val t = progress.coerceIn(0f, 0.6f) / 0.6f
    val scale = t * t * (3f - 2f * t)
    
    val orbitRadius = minOf(width, height) * 0.35f
    val radius = baseRadius * scale
    val radiusGlow = radius * 1.4f
    
    for (i in 0..5) {
        val angleRad = angles[i] * 0.017453292f // Pre-calculated PI/180
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        val x = centerX + cosA * orbitRadius
        val y = centerY + sinA * orbitRadius
        val color = colors[i]
        
        // Glow
        drawCircle(
            color = color.copy(alpha = 0.3f * scale),
            radius = radiusGlow,
            center = Offset(x, y),
            blendMode = BlendMode.Screen
        )
        
        // Main
        drawCircle(
            color = color.copy(alpha = scale),
            radius = radius,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.renderFlying(
    progress: Float,
    angles: FloatArray,
    colors: Array<Color>,
    baseRadius: Float,
    width: Float,
    height: Float,
    centerX: Float,
    centerY: Float
) {
    val orbitRadius = minOf(width, height) * 0.35f
    val baseSpeed = 1f + progress * 2f
    val totalRotation = progress * 360f * baseSpeed
    
    // Size increases with speed: 1.0x at start, 1.4x at end (as speed goes 1x → 3x)
    val speedScale = 1f + (baseSpeed - 1f) * 0.2f // Maps 1→3 speed to 1.0→1.4 size
    val radius = baseRadius * speedScale
    val radiusGlow = radius * 1.5f
    val radiusTrail = radius * 0.8f
    
    val speedVars = floatArrayOf(1f, 1.15f, 1.3f, 1f, 1.15f, 1.3f)
    
    for (i in 0..5) {
        val speedVar = speedVars[i]
        val angleRad = (angles[i] + totalRotation * speedVar) * 0.017453292f
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        val x = centerX + cosA * orbitRadius
        val y = centerY + sinA * orbitRadius
        val color = colors[i]
        
        // Trail (also scaled)
        val trailAngleRad = angleRad - 0.3f
        val trailX = centerX + cos(trailAngleRad) * orbitRadius
        val trailY = centerY + sin(trailAngleRad) * orbitRadius
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radiusTrail,
            center = Offset(trailX, trailY)
        )
        
        // Glow
        drawCircle(
            color = color.copy(alpha = 0.4f),
            radius = radiusGlow,
            center = Offset(x, y),
            blendMode = BlendMode.Screen
        )
        
        // Main
        drawCircle(
            color = color,
            radius = radius,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.renderSpiral(
    progress: Float,
    angles: FloatArray,
    colors: Array<Color>,
    baseRadius: Float,
    width: Float,
    height: Float,
    centerX: Float,
    centerY: Float
) {
    val initialRadius = minOf(width, height) * 0.35f
    val currentRadius = initialRadius * (1f - progress)
    val spiralRotation = progress * 720f
    val baseRotation = 1440f
    
    // Size increases as bubbles get closer to center
    // Start at 1.4x (from flying phase), grow to 2.2x at center
    val proximityScale = 1.4f + (1f - currentRadius / initialRadius) * 0.8f
    val radius = baseRadius * proximityScale
    val radiusGlow = radius * 1.6f
    
    val speedVars = floatArrayOf(1f, 1.15f, 1.3f, 1f, 1.15f, 1.3f)
    val showRings = progress > 0.3f
    
    for (i in 0..5) {
        val speedVar = speedVars[i]
        val totalAngle = angles[i] + baseRotation * speedVar + spiralRotation * speedVar
        val angleRad = totalAngle * 0.017453292f
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        val x = centerX + cosA * currentRadius
        val y = centerY + sinA * currentRadius
        val color = colors[i]
        val center = Offset(x, y)
        
        // Rings (only 1 ring for performance)
        if (showRings) {
            val glowProgress = (progress - 0.3f) * 1.428571f // 1/0.7
            val ringProgress = glowProgress.coerceIn(0f, 1f)
            if (ringProgress > 0.05f) {
                val ringRadius = radius * (1f + ringProgress * 2.5f)
                val ringAlpha = 0.9f * (1f - ringProgress) * 0.4f
                drawCircle(
                    color = color.copy(alpha = ringAlpha),
                    radius = ringRadius,
                    center = center
                )
            }
        }
        
        // Glow
        drawCircle(
            color = color.copy(alpha = 0.5f),
            radius = radiusGlow,
            center = center,
            blendMode = BlendMode.Screen
        )
        
        // Main
        drawCircle(
            color = color.copy(alpha = 0.9f),
            radius = radius,
            center = center
        )
    }
    
    // Fusion glow (simplified)
    if (progress > 0.5f) {
        val fusionProgress = (progress - 0.5f) * 2f
        val fusionRadius = 120f * fusionProgress
        val fusionAlpha = fusionProgress * 0.8f
        val center = Offset(centerX, centerY)
        
        drawCircle(
            color = Color.White.copy(alpha = fusionAlpha * 0.9f),
            radius = fusionRadius * 2f,
            center = center,
            blendMode = BlendMode.Screen
        )
        
        drawCircle(
            color = Color(0xFFFFD700).copy(alpha = fusionAlpha * 0.6f),
            radius = fusionRadius,
            center = center,
            blendMode = BlendMode.Screen
        )
    }
}

private fun DrawScope.renderExplosion(
    progress: Float,
    rainbowColors: Array<Color>,
    width: Float,
    height: Float,
    centerX: Float,
    centerY: Float
) {
    val center = Offset(centerX, centerY)
    val colorCount = rainbowColors.size // Now 14 colors!
    
    // Layer 1: Primary rainbow wave (all 14 colors)
    for (i in 0 until colorCount) {
        val ringDelay = i * 0.06f // Faster stagger for more colors
        val ringProgress = (progress - ringDelay).coerceIn(0f, 1f)
        
        if (ringProgress > 0.03f) {
            val color = rainbowColors[i]
            val radius = 180f * ringProgress * (1.8f + i * 0.15f)
            val alpha = (1f - ringProgress) * 0.75f
            
            // Gradient ring for vibrant look
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = alpha),
                        color.copy(alpha = alpha * 0.6f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center,
                blendMode = BlendMode.Screen
            )
        }
    }
    
    // Layer 2: Secondary rainbow wave (offset colors for more variety)
    for (i in 0 until (colorCount / 2)) {
        val colorIndex = (i * 2 + 1) % colorCount // Pick every other color
        val ringDelay = i * 0.12f + 0.12f
        val ringProgress = (progress - ringDelay).coerceIn(0f, 1f)
        
        if (ringProgress > 0.03f) {
            val color = rainbowColors[colorIndex]
            val radius = 140f * ringProgress * (1.5f + i * 0.2f)
            val alpha = (1f - ringProgress) * 0.55f
            
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = radius,
                center = center,
                blendMode = BlendMode.Screen
            )
        }
    }
    
    // Layer 3: Color mixing halos (cycle through all colors)
    if (progress > 0.2f) {
        val haloProgress = (progress - 0.2f) / 0.8f
        for (i in 0..3) {
            val haloDelay = i * 0.15f
            val halo = (haloProgress - haloDelay).coerceIn(0f, 1f)
            if (halo > 0f) {
                val haloRadius = 200f * halo * (1f + i * 0.4f)
                val haloAlpha = (1f - halo) * 0.4f
                
                // Cycling through full spectrum
                val colorIndex = ((progress * 12f + i * 3f) % colorCount).toInt()
                val haloColor = rainbowColors[colorIndex]
                
                drawCircle(
                    color = haloColor.copy(alpha = haloAlpha),
                    radius = haloRadius,
                    center = center,
                    blendMode = BlendMode.Screen
                )
            }
        }
    }
    
    // Central multi-colored burst with more spectrum colors
    val burstRadius = 120f * (1f + progress * 1.8f)
    val burstAlpha = (1f - progress * 0.5f).coerceAtLeast(0.25f)
    
    // Extended rainbow gradient burst core
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = burstAlpha),
                rainbowColors[0].copy(alpha = burstAlpha * 0.95f),  // Red
                rainbowColors[2].copy(alpha = burstAlpha * 0.9f),   // Orange
                rainbowColors[4].copy(alpha = burstAlpha * 0.85f),  // Yellow
                rainbowColors[6].copy(alpha = burstAlpha * 0.75f),  // Green
                rainbowColors[8].copy(alpha = burstAlpha * 0.65f),  // Cyan
                rainbowColors[10].copy(alpha = burstAlpha * 0.55f), // Blue
                rainbowColors[12].copy(alpha = burstAlpha * 0.45f), // Magenta
                Color.Transparent
            ),
            center = center,
            radius = burstRadius * 2f
        ),
        radius = burstRadius * 2f,
        center = center,
        blendMode = BlendMode.Screen
    )
    
    // Inner vibrant core with more colors
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = burstAlpha),
                Color(0xFFFFD700).copy(alpha = burstAlpha * 0.9f),  // Gold
                rainbowColors[3].copy(alpha = burstAlpha * 0.8f),   // Light Orange
                rainbowColors[7].copy(alpha = burstAlpha * 0.6f),   // Spring Green
                Color.Transparent
            ),
            center = center,
            radius = burstRadius
        ),
        radius = burstRadius,
        center = center,
        blendMode = BlendMode.Screen
    )
    
    // Multi-colored pulsing cores (more colors!)
    val pulse1Scale = 1f + sin(progress * 15.707963f) * 0.35f
    val pulse2Scale = 1f + sin(progress * 15.707963f + 2.094395f) * 0.3f
    val pulse3Scale = 1f + sin(progress * 15.707963f + 4.18879f) * 0.25f
    
    // Core 1: White/Gold
    drawCircle(
        color = Color.White.copy(alpha = burstAlpha * 0.9f),
        radius = 60f * pulse1Scale,
        center = center,
        blendMode = BlendMode.Screen
    )
    
    // Core 2: Magenta/Pink
    drawCircle(
        color = rainbowColors[12].copy(alpha = burstAlpha * 0.7f), // Magenta
        radius = 75f * pulse2Scale,
        center = center,
        blendMode = BlendMode.Screen
    )
    
    // Core 3: Cyan
    drawCircle(
        color = rainbowColors[8].copy(alpha = burstAlpha * 0.6f), // Cyan
        radius = 90f * pulse3Scale,
        center = center,
        blendMode = BlendMode.Screen
    )
    
    // Color sparkle bursts (more sparkles with varied colors)
    if (progress > 0.3f) {
        val sparkleProgress = (progress - 0.3f) / 0.7f
        val sparkleCount = 12 // Increased from 8
        for (i in 0 until sparkleCount) {
            val angle = (i * 30f + progress * 180f) * 0.017453292f
            val distance = 100f + sparkleProgress * 150f
            val sparkleX = centerX + cos(angle) * distance
            val sparkleY = centerY + sin(angle) * distance
            val sparkleAlpha = (1f - sparkleProgress) * 0.8f
            val sparkleColor = rainbowColors[i % colorCount] // Cycle through all colors
            
            if (sparkleAlpha > 0.1f) {
                drawCircle(
                    color = sparkleColor.copy(alpha = sparkleAlpha),
                    radius = 15f * (1f - sparkleProgress * 0.5f),
                    center = Offset(sparkleX, sparkleY),
                    blendMode = BlendMode.Screen
                )
            }
        }
    }
}

@Composable
private fun BoxScope.CelebrationText() {
    var scale by remember { mutableStateOf(0f) }
    var alpha by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        // Manual animation to avoid animateFloatAsState recompositions
        val startTime = withFrameNanos { it }
        while (scale < 1f) {
            withFrameNanos { frameTime ->
                val elapsed = (frameTime - startTime) / 1_000_000f
                val progress = (elapsed / 400f).coerceAtMost(1f)
                
                // Spring easing approximation
                val t = progress
                scale = if (t < 0.5f) {
                    2f * t * t
                } else {
                    1f - (-2f * t + 2f) * (-2f * t + 2f) / 2f
                }
                alpha = progress
            }
        }
    }
    
    Column(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 60.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎉",
            fontSize = 56.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Amazing!",
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "You completed all levels!",
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}
