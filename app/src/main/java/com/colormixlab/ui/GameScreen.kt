package com.colormixlab.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.colormixlab.data.LeaderboardManager
import com.colormixlab.game.Difficulty
import com.colormixlab.game.GameViewModel
import com.colormixlab.model.LeaderboardEntry
import com.colormixlab.ui.components.ColorButton
import com.colormixlab.ui.components.ConfettiEffect
import com.colormixlab.ui.components.LevelDisplay
import com.colormixlab.ui.components.MixingBowl
import com.colormixlab.ui.components.SparkleEffect
import com.colormixlab.ui.components.TargetColor
import com.colormixlab.utils.HapticManager

@OptIn(ExperimentalLayoutApi::class)

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
    onNavigateToIntro: () -> Unit = {}
) {
    val state = viewModel.gameState.value
    val context = LocalContext.current
    val hapticManager = remember { HapticManager(context) }
    val leaderboardManager = remember { LeaderboardManager(context) }
    var showMenu by remember { mutableStateOf(false) }
    var showNicknameDialog by remember { mutableStateOf(false) }
    var showLeaderboardAfterNickname by remember { mutableStateOf(false) }
    var showFinalLeaderboard by remember { mutableStateOf(false) }
    
    // Pause timer when dialogs are open
    LaunchedEffect(state.showSuccessDialog, showMenu) {
        if (state.showSuccessDialog || showMenu) {
            viewModel.pauseTimer()
        } else {
            viewModel.resumeTimer()
        }
    }
    
    // Check if game is completed
    LaunchedEffect(state.isGameCompleted) {
        if (state.isGameCompleted) {
            showNicknameDialog = true
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {
        // Main content in portrait layout
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section: Level and Target
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Level and Score on the left
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Menu button
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    LevelDisplay(level = state.currentLevel)
                    
                    // Animated score counter
                    val animatedScore by animateIntAsState(
                        targetValue = state.currentScore,
                        animationSpec = spring(
                            dampingRatio = 0.7f,
                            stiffness = 200f
                        ),
                        label = "scoreAnimation"
                    )
                    
                    Text(
                        text = "Score: $animatedScore",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.animateContentSize()
                    )
                }
                TargetColor(targetColor = state.targetColor)
            }
            
            // Timer Display (center top)
            TimerDisplay(
                timeRemaining = state.timeRemainingSeconds,
                difficulty = state.difficulty
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // Center: Mixing Bowl
            MixingBowl(
                drops = state.drops,
                mixedColor = state.mixedColor
            )

            Spacer(modifier = Modifier.height(8.dp))
            
            // Color buttons in flexible grid
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 3
            ) {
                state.unlockedColors.forEach { color ->
                    ColorButton(
                        color = color,
                        dropCount = state.getDropCount(color),
                        onClick = {
                            hapticManager.performHaptic(HapticManager.HapticType.LIGHT_TAP)
                            viewModel.addColorDrop(color)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Action buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
            ) {
                Button(
                    onClick = {
                        hapticManager.performHaptic(HapticManager.HapticType.LIGHT_TAP)
                        viewModel.checkMatch()
                        if (viewModel.gameState.value.isMatched) {
                            hapticManager.performHaptic(HapticManager.HapticType.SUCCESS)
                        }
                    },
                    enabled = state.getTotalDrops() > 0 && !state.hasCheckedThisRound,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(
                        text = if (state.hasCheckedThisRound) "Already Checked" else "Check Match!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Button(
                    onClick = {
                        hapticManager.performHaptic(HapticManager.HapticType.LIGHT_TAP)
                        viewModel.clearBowl()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(
                        text = "Clear Bowl",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
    
    // Success Dialog - use key to prevent re-rendering issues
    if (state.showSuccessDialog) {
        ResultDialog(
            similarity = state.similarity,
            isSuccess = state.isMatched,
            level = state.currentLevel,
            basePoints = state.lastBasePoints,
            timeBonus = state.lastTimeBonus,
            onNextLevel = { viewModel.nextLevel() },
            onRetry = { viewModel.retryLevel() },
            getMessage = { viewModel.getResultMessage(it) },
            getEmoji = { viewModel.getResultEmoji(it) }
        )
    }
    
    // Menu Dialog
    if (showMenu) {
        MenuDialog(
            onDismiss = { showMenu = false },
            onRestartGame = {
                showMenu = false
                onNavigateToIntro()
            },
            onShowLeaderboard = {
                showMenu = false
                // Will be handled in menu
            },
            onResetLeaderboard = {
                leaderboardManager.clearLeaderboard()
            },
            onFinishGame = {
                showLeaderboardAfterNickname = true
                viewModel.forceFinishGame()
            },
            leaderboardEntries = leaderboardManager.getEntries()
        )
    }
    
    // Nickname Dialog (Game Completion)
    if (showNicknameDialog) {
        NicknameDialog(
            finalScore = state.currentScore,
            finalLevel = state.currentLevel,
            onSubmit = { nickname ->
                leaderboardManager.addEntry(
                    LeaderboardEntry(
                        nickname = nickname,
                        score = state.currentScore,
                        level = state.currentLevel,
                        difficulty = state.difficulty
                    )
                )
                showNicknameDialog = false
                viewModel.completeGame()

                // Check if we should show leaderboard or navigate to intro
                if (showLeaderboardAfterNickname) {
                    showLeaderboardAfterNickname = false
                    showFinalLeaderboard = true
                } else {
                    onNavigateToIntro()
                }
            }
        )
    }

    // Final Leaderboard Dialog (shown after finish game)
    if (showFinalLeaderboard) {
        LeaderboardDialog(
            entries = leaderboardManager.getEntries(),
            onDismiss = {
                showFinalLeaderboard = false
                onNavigateToIntro()
            }
        )
    }
}

@Composable
fun ResultDialog(
    similarity: Float,
    isSuccess: Boolean,
    level: Int,
    basePoints: Int,
    timeBonus: Int,
    onNextLevel: () -> Unit,
    onRetry: () -> Unit,
    getMessage: (Float) -> String,
    getEmoji: (Float) -> String
) {
    // Remember values to prevent recalculation
    val message = remember(similarity) { getMessage(similarity) }
    val emoji = remember(similarity) { getEmoji(similarity) }
    val percentage = remember(similarity) { (similarity * 100).toInt() }
    val totalPoints = basePoints + timeBonus

    Dialog(onDismissRequest = { }) {
        Box(modifier = Modifier.fillMaxWidth(0.98f)) {
            // Show confetti for perfect match - use key to prevent re-rendering
            if (similarity >= 1.0f) {
                androidx.compose.runtime.key("confetti-$level") {
                    ConfettiEffect(modifier = Modifier.fillMaxSize())
                }
            }
            // Show sparkles for high scores
            else if (similarity >= 0.80f && isSuccess) {
                androidx.compose.runtime.key("sparkle-$level") {
                    SparkleEffect(modifier = Modifier.fillMaxSize())
                }
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
                    // Emoji with scale animation
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

                    // Similarity percentage
                    Text(
                        text = "Similarity: $percentage%",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1
                    )

                    // Points breakdown
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
                    
                    Text(
                        text = if (isSuccess) "Level $level Complete!" else "Try Level $level Again",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Show unlock message for special levels (on success)
                    if (isSuccess) {
                        when (level) {
                            3 -> {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF1C40F).copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = "🎨 Yellow Unlocked!",
                                        fontSize = 20.sp,
                                        color = Color(0xFFF1C40F),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                            6 -> {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFE67E22).copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = "🧡 Orange Unlocked!",
                                        fontSize = 20.sp,
                                        color = Color(0xFFE67E22),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                            9 -> {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF9B59B6).copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = "💜 Purple Unlocked!",
                                        fontSize = 20.sp,
                                        color = Color(0xFF9B59B6),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                            12 -> {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFE91E63).copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = "💖 Pink Unlocked!",
                                        fontSize = 20.sp,
                                        color = Color(0xFFE91E63),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                            15 -> {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFFFFFFF).copy(alpha = 0.2f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = "🤍 White Unlocked!",
                                        fontSize = 20.sp,
                                        color = Color(0xFF666666),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                            18 -> {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF000000).copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = "🖤 Black Unlocked!",
                                        fontSize = 20.sp,
                                        color = Color(0xFF000000),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = if (isSuccess) onNextLevel else onRetry,
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
    }
}

@Composable
fun MenuDialog(
    onDismiss: () -> Unit,
    onRestartGame: () -> Unit,
    onShowLeaderboard: () -> Unit,
    onResetLeaderboard: () -> Unit,
    onFinishGame: () -> Unit,
    leaderboardEntries: List<LeaderboardEntry>
) {
    var showConfirmation by remember { mutableStateOf(false) }
    var showResetLeaderboardConfirmation by remember { mutableStateOf(false) }
    var showFinishGameConfirmation by remember { mutableStateOf(false) }
    var showLeaderboard by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Menu",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Divider(modifier = Modifier.padding(vertical = 6.dp))
                
                // Leaderboard Button
                Button(
                    onClick = { showLeaderboard = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
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
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // Restart Game Button
                Button(
                    onClick = { showConfirmation = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Restart Game",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Finish Game Button
                Button(
                    onClick = { showFinishGameConfirmation = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C27B0)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Finish Game",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Reset Leaderboard Button
                OutlinedButton(
                    onClick = { showResetLeaderboardConfirmation = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFF44336)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Reset Leaderboard",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Close Button
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Close",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
    
    // Restart Game Confirmation
    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = {
                Text(
                    text = "Restart Game?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("This will reset your progress to Level 1 and clear your score. Are you sure?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmation = false
                        onRestartGame()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Restart", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Reset Leaderboard Confirmation
    if (showResetLeaderboardConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetLeaderboardConfirmation = false },
            title = {
                Text(
                    text = "Reset Leaderboard?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("This will permanently delete all leaderboard entries. This action cannot be undone!")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetLeaderboardConfirmation = false
                        onResetLeaderboard()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Delete All", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetLeaderboardConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Finish Game Confirmation
    if (showFinishGameConfirmation) {
        AlertDialog(
            onDismissRequest = { showFinishGameConfirmation = false },
            title = {
                Text(
                    text = "Finish Game?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("This will end your current game and submit your score to the leaderboard.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFinishGameConfirmation = false
                        onDismiss()
                        onFinishGame()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF9C27B0)
                    )
                ) {
                    Text("Finish", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishGameConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Leaderboard Dialog
    if (showLeaderboard) {
        LeaderboardDialog(
            entries = leaderboardEntries,
            onDismiss = { showLeaderboard = false }
        )
    }
}

@Composable
fun NicknameDialog(
    finalScore: Int,
    finalLevel: Int,
    onSubmit: (String) -> Unit
) {
    var nickname by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { /* Prevent dismissing */ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Celebration
                Text(
                    text = "🎉",
                    fontSize = 64.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Game Complete!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (finalLevel >= 30) "You completed all $finalLevel levels!" else "You reached level $finalLevel!",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Score display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Final Score",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$finalScore",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Nickname input
                Text(
                    text = "Enter your name for the leaderboard:",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = nickname,
                    onValueChange = {
                        if (it.length <= 15) nickname = it
                    },
                    label = { Text("Nickname") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    text = "${nickname.length}/15 characters",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Submit button
                Button(
                    onClick = {
                        val finalNickname = nickname.trim().ifEmpty { "Anonymous" }
                        onSubmit(finalNickname)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = nickname.trim().isNotEmpty() || nickname.isEmpty()
                ) {
                    Text(
                        text = "Submit Score",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = { onSubmit("Anonymous") }
                ) {
                    Text("Skip (submit as Anonymous)", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun TimerDisplay(
    timeRemaining: Int?,
    difficulty: Difficulty
) {
    val context = LocalContext.current
    val hapticManager = remember { HapticManager(context) }

    // Don't show timer for Easy mode
    if (difficulty == Difficulty.EASY) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "∞",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
        return
    }

    val time = timeRemaining ?: 0
    val isWarning = time <= 5 && time > 0

    // Trigger haptic every second when timer is in warning state
    LaunchedEffect(time) {
        if (isWarning) {
            hapticManager.performHaptic(HapticManager.HapticType.LIGHT_TAP)
        }
    }

    // Blink animation when warning
    val alpha by animateFloatAsState(
        targetValue = if (isWarning) {
            // Create a blinking effect
            if ((time * 2) % 2 == 0) 1f else 0.3f
        } else {
            1f
        },
        animationSpec = tween(durationMillis = 300),
        label = "timerBlink"
    )

    val timerColor = if (isWarning) {
        Color.Red
    } else {
        MaterialTheme.colorScheme.primary
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "⏱",
            fontSize = 20.sp,
            modifier = Modifier.alpha(alpha)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$time",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = timerColor,
            modifier = Modifier.alpha(alpha)
        )
        Text(
            text = "s",
            fontSize = 16.sp,
            color = timerColor.copy(alpha = 0.7f),
            modifier = Modifier
                .padding(start = 2.dp)
                .alpha(alpha)
        )
    }
}
