package com.colormixlab.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.colormixlab.model.GameColor
import com.colormixlab.ui.theme.ColorMixLabTheme
import com.colormixlab.utils.toComposeColor

@Composable
fun ColorButton(
    color: GameColor,
    dropCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.15f else 1f,  // Reduced from 1.2f
        animationSpec = spring(
            dampingRatio = 0.6f,  // More damping
            stiffness = 500f  // Faster
        ),
        finishedListener = {
            if (isPressed) isPressed = false
        },
        label = "buttonScale"
    )

    // Use remember to avoid recreating interaction source
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null  // Remove ripple for better performance
            ) {
                isPressed = true
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Color circle
        Box(
            modifier = Modifier
                .size(60.dp)
                .border(3.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                .background(color.toComposeColor(), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Drop count badge
            if (dropCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dropCount.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Color name
        Text(
            text = color.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(name = "ColorButton — no drops", showBackground = true)
@Composable
private fun ColorButtonNoDropsPreview() {
    ColorMixLabTheme {
        ColorButton(color = GameColor.Red, dropCount = 0, onClick = {})
    }
}

@Preview(name = "ColorButton — with badge", showBackground = true)
@Composable
private fun ColorButtonWithBadgePreview() {
    ColorMixLabTheme {
        ColorButton(color = GameColor.Blue, dropCount = 3, onClick = {})
    }
}
