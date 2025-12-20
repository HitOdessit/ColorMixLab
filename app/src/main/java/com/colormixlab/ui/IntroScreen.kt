package com.colormixlab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.colormixlab.BuildConfig
import com.colormixlab.data.LeaderboardManager
import com.colormixlab.game.Difficulty

@Composable
fun IntroScreen(
    onStartGame: (Difficulty) -> Unit
) {
    var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIUM) }
    var showLeaderboard by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val leaderboardManager = remember { LeaderboardManager(context) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title
            Text(
                text = "🎨 ColorMixLab 🎨",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            // Version
            Text(
                text = "v${BuildConfig.VERSION_NAME}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = (-8).dp)
            )

            Spacer(modifier = Modifier.height(0.dp))
            
            // Instructions Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "How to Play:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    listOf(
                        "• Match the target color by mixing",
                        "• Need 80+% similarity to advance",
                        "• Complete up to 30 levels",
                        "• Higher similarity = more points",
                        "• Harder difficulty = more points"
                    ).forEach { instruction ->
                        Text(
                            text = instruction,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Difficulty Selection
            Text(
                text = "Select Difficulty:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Easy Button
            DifficultyButton(
                difficulty = Difficulty.EASY,
                isSelected = selectedDifficulty == Difficulty.EASY,
                emoji = "🟢",
                title = "Easy",
                description = "No Timer • 75% Points",
                onClick = { selectedDifficulty = Difficulty.EASY }
            )
            
            // Medium Button
            DifficultyButton(
                difficulty = Difficulty.MEDIUM,
                isSelected = selectedDifficulty == Difficulty.MEDIUM,
                emoji = "🟡",
                title = "Medium",
                description = "40s • 100% Points + Time Bonus",
                onClick = { selectedDifficulty = Difficulty.MEDIUM }
            )
            
            // Hard Button
            DifficultyButton(
                difficulty = Difficulty.HARD,
                isSelected = selectedDifficulty == Difficulty.HARD,
                emoji = "🔴",
                title = "Hard",
                description = "20s • 125% Points + Time Bonus",
                onClick = { selectedDifficulty = Difficulty.HARD }
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Start Game Button
            Button(
                onClick = { onStartGame(selectedDifficulty) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Start Game",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Leaderboard Button
            OutlinedButton(
                onClick = { showLeaderboard = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Leaderboard",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Leaderboard",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
    
    // Leaderboard Dialog
    if (showLeaderboard) {
        LeaderboardDialog(
            entries = leaderboardManager.getEntries(),
            onDismiss = { showLeaderboard = false }
        )
    }
}

@Composable
fun DifficultyButton(
    difficulty: Difficulty,
    isSelected: Boolean,
    emoji: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    val backgroundColor = when (difficulty) {
        Difficulty.EASY -> Color(0xFF4CAF50)
        Difficulty.MEDIUM -> Color(0xFFFFC107)
        Difficulty.HARD -> Color(0xFFF44336)
    }
    
    val borderModifier = if (isSelected) {
        Modifier.border(
            width = 3.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(12.dp)
        )
    } else {
        Modifier
    }
    
    Card(
        onClick = onClick,
        modifier = borderModifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = emoji,
                    fontSize = 24.sp
                )
                
                Column {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = backgroundColor
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            if (isSelected) {
                Text(
                    text = "✓",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

