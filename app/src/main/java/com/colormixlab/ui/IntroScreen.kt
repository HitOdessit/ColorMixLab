package com.colormixlab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.colormixlab.BuildConfig
import com.colormixlab.data.LeaderboardManager
import com.colormixlab.game.Difficulty
import com.colormixlab.ui.components.GameCompletionCelebration

@Composable
fun IntroScreen(
    onStartGame: (Difficulty) -> Unit
) {
    var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIUM) }
    var showLeaderboard by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val leaderboardManager = remember {
        LeaderboardManager(com.colormixlab.platform.PlatformStorage(context))
    }
    
    // Check orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    // Show celebration animation as overlay
    if (showCelebration) {
        GameCompletionCelebration(
            onAnimationComplete = {
                showCelebration = false
            }
        )
    } else {
        // Normal intro screen
        if (isLandscape) {
            IntroScreenLandscape(
                selectedDifficulty = selectedDifficulty,
                onDifficultySelected = { selectedDifficulty = it },
                onStartGame = onStartGame,
                onShowLeaderboard = { showLeaderboard = true },
                onVersionDoubleClick = { showCelebration = true }
            )
        } else {
            IntroScreenPortrait(
                selectedDifficulty = selectedDifficulty,
                onDifficultySelected = { selectedDifficulty = it },
                onStartGame = onStartGame,
                onShowLeaderboard = { showLeaderboard = true },
                onVersionDoubleClick = { showCelebration = true }
            )
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
fun IntroScreenPortrait(
    selectedDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit,
    onStartGame: (Difficulty) -> Unit,
    onShowLeaderboard: () -> Unit,
    onVersionDoubleClick: () -> Unit
) {
    // Double-click tracking for version
    var lastClickTime by remember { mutableStateOf(0L) }
    
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

            // Version - with double click detection
            Text(
                text = "v${BuildConfig.VERSION_NAME}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(y = (-8).dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastClickTime < 500) { // Double click threshold
                                    onVersionDoubleClick()
                                }
                                lastClickTime = currentTime
                            }
                        )
                    }
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
            
            // Difficulty buttons
            DifficultyButton(
                difficulty = Difficulty.EASY,
                isSelected = selectedDifficulty == Difficulty.EASY,
                emoji = "🟢",
                title = "Easy",
                description = "No Timer • 75% Points",
                onClick = { onDifficultySelected(Difficulty.EASY) }
            )
            
            DifficultyButton(
                difficulty = Difficulty.MEDIUM,
                isSelected = selectedDifficulty == Difficulty.MEDIUM,
                emoji = "🟡",
                title = "Medium",
                description = "40s • 100% Points + Time Bonus",
                onClick = { onDifficultySelected(Difficulty.MEDIUM) }
            )
            
            DifficultyButton(
                difficulty = Difficulty.HARD,
                isSelected = selectedDifficulty == Difficulty.HARD,
                emoji = "🔴",
                title = "Hard",
                description = "20s • 125% Points + Time Bonus",
                onClick = { onDifficultySelected(Difficulty.HARD) }
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
                onClick = onShowLeaderboard,
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
}

@Composable
fun IntroScreenLandscape(
    selectedDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit,
    onStartGame: (Difficulty) -> Unit,
    onShowLeaderboard: () -> Unit,
    onVersionDoubleClick: () -> Unit
) {
    // Double-click tracking for version
    var lastClickTime by remember { mutableStateOf(0L) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title and subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "🎨 ColorMixLab 🎨",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "v${BuildConfig.VERSION_NAME}  •  Match colors • 30 levels • Beat the timer",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastClickTime < 500) { // Double click threshold
                                    onVersionDoubleClick()
                                }
                                lastClickTime = currentTime
                            }
                        )
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Three difficulty cards in a row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Easy Card
                DifficultyCardLandscape(
                    difficulty = Difficulty.EASY,
                    isSelected = selectedDifficulty == Difficulty.EASY,
                    emoji = "🟢",
                    title = "EASY",
                    timerText = "No Timer",
                    pointsText = "75% Points",
                    description = "Relaxed play",
                    onClick = { onDifficultySelected(Difficulty.EASY) },
                    modifier = Modifier.weight(1f)
                )
                
                // Medium Card
                DifficultyCardLandscape(
                    difficulty = Difficulty.MEDIUM,
                    isSelected = selectedDifficulty == Difficulty.MEDIUM,
                    emoji = "🟡",
                    title = "MEDIUM",
                    timerText = "40 sec",
                    pointsText = "100% Points",
                    bonusText = "+ Time Bonus",
                    description = "Balanced",
                    onClick = { onDifficultySelected(Difficulty.MEDIUM) },
                    modifier = Modifier.weight(1f)
                )
                
                // Hard Card
                DifficultyCardLandscape(
                    difficulty = Difficulty.HARD,
                    isSelected = selectedDifficulty == Difficulty.HARD,
                    emoji = "🔴",
                    title = "HARD",
                    timerText = "20 sec",
                    pointsText = "125% Points",
                    bonusText = "+ Time Bonus",
                    description = "Time rush",
                    onClick = { onDifficultySelected(Difficulty.HARD) },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Leaderboard Button
                OutlinedButton(
                    onClick = onShowLeaderboard,
                    modifier = Modifier
                        .weight(0.3f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Leaderboard",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Leaderboard",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Start Game Button
                Button(
                    onClick = { onStartGame(selectedDifficulty) },
                    modifier = Modifier
                        .weight(0.7f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Start Game",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Brief instructions at bottom
            Text(
                text = "How to Play: Mix colors to match target • Need 80% similarity to advance",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
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

@Composable
fun DifficultyCardLandscape(
    difficulty: Difficulty,
    isSelected: Boolean,
    emoji: String,
    title: String,
    timerText: String,
    pointsText: String,
    bonusText: String? = null,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (difficulty) {
        Difficulty.EASY -> Color(0xFF4CAF50)
        Difficulty.MEDIUM -> Color(0xFFFFC107)
        Difficulty.HARD -> Color(0xFFF44336)
    }
    
    val borderModifier = if (isSelected) {
        Modifier.border(
            width = 4.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(16.dp)
        )
    } else {
        Modifier
    }
    
    Card(
        onClick = onClick,
        modifier = modifier
            .then(borderModifier)
            .fillMaxHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            // Emoji - Large
            Text(
                text = emoji,
                fontSize = 56.sp
            )
            
            // Title
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = backgroundColor,
                textAlign = TextAlign.Center
            )
            
            // Divider
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(vertical = 4.dp),
                color = backgroundColor.copy(alpha = 0.3f),
                thickness = 2.dp
            )
            
            // Timer info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "⏱",
                        fontSize = 20.sp
                    )
                    Text(
                        text = timerText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = pointsText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                
                if (bonusText != null) {
                    Text(
                        text = bonusText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = backgroundColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Description
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
            
            // Selected indicator
            if (isSelected) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Selected",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}
