package com.colormixlab.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MixingBowl(
    mixedColor: Color,
    modifier: Modifier = Modifier
) {
    // Animate color changes smoothly
    val animatedColor by animateColorAsState(
        targetValue = mixedColor,
        animationSpec = tween(durationMillis = 300),  // Faster
        label = "bowlColor"
    )
    
    // Track color changes for ripple effect
    var showRipple by remember { mutableStateOf(false) }
    var previousColor by remember { mutableStateOf(mixedColor) }
    
    // Only trigger ripple when color actually changes and is not white
    LaunchedEffect(mixedColor) {
        if (mixedColor != previousColor && mixedColor != Color.White) {
            showRipple = true
            previousColor = mixedColor
        } else if (mixedColor == Color.White) {
            showRipple = false
            previousColor = mixedColor
        }
    }
    
    // Simplified scale animation
    val scale by animateFloatAsState(
        targetValue = if (showRipple) 1.05f else 1f,  // Less dramatic
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        finishedListener = {
            if (showRipple) showRipple = false
        },
        label = "bowlScale"
    )
    
    Box(
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        // Ripple effect - only show when needed
        if (showRipple) {
            RippleEffect(
                trigger = showRipple,
                color = animatedColor,
                modifier = Modifier.size(160.dp)
            )
        }
        
        // Main bowl
        Box(
            modifier = Modifier
                .size(160.dp)
                .scale(scale)
                .border(6.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                .background(animatedColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Optional: Add "Empty" text when white
            if (mixedColor == Color.White) {
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

