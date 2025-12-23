package com.colormixlab.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.colormixlab.game.Difficulty
import com.colormixlab.game.GameState
import com.colormixlab.game.GameViewModel
import com.colormixlab.ui.components.ColorButton
import com.colormixlab.ui.components.MixingBowl
import com.colormixlab.ui.components.LevelDisplay
import com.colormixlab.ui.components.TargetColor
import com.colormixlab.utils.HapticManager
import kotlinx.coroutines.delay

/**
 * Timer display component for game screen.
 * Shows remaining time with visual and haptic feedback.
 *
 * Features:
 * - Infinity symbol for Easy mode (no timer)
 * - Red blinking animation when time is low (≤5 seconds)
 * - Haptic feedback every second in warning state
 *
 * @param timeRemaining Seconds remaining (null if no timer)
 * @param difficulty Current difficulty level
 */
@Composable
fun TimerDisplay(
    timeRemaining: Int?,
    difficulty: Difficulty
) {
    val context = LocalContext.current
    val hapticManager = remember { HapticManager(context) }

    // Easy mode shows infinity symbol (no timer)
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

    // Haptic feedback in warning state
    LaunchedEffect(time) {
        if (isWarning) {
            hapticManager.performHaptic(HapticManager.HapticType.LIGHT_TAP)
        }
    }

    // Blinking animation when warning
    val alpha by animateFloatAsState(
        targetValue = if (isWarning) {
            if ((time * 2) % 2 == 0) 1f else 0.3f
        } else {
            1f
        },
        animationSpec = tween(durationMillis = 300),
        label = "timerBlink"
    )

    val timerColor = if (isWarning) Color.Red else MaterialTheme.colorScheme.primary

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

/**
 * Portrait orientation game layout.
 * Arranges UI elements vertically with mixing bowl in the center.
 *
 * Layout structure:
 * - Top: Menu button, Level, Score, Target color
 * - Timer display
 * - Mixing bowl
 * - Color palette (flow row)
 * - Action buttons (Check Match, Clear Bowl)
 *
 * @param state Current game state
 * @param viewModel Game view model for actions
 * @param hapticManager Haptic feedback manager
 * @param onShowMenu Callback to show menu
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PortraitGameLayout(
    state: GameState,
    viewModel: GameViewModel,
    hapticManager: HapticManager,
    onShowMenu: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top section: Level, Score, and Target
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Menu, Level, and Score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onShowMenu,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                LevelDisplay(level = state.currentLevel)

                // Animated score display
                val animatedScore by animateIntAsState(
                    targetValue = state.currentScore,
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 200f),
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

            // Right side: Target color
            TargetColor(targetColor = state.targetColor)
        }

        // Timer
        TimerDisplay(
            timeRemaining = state.timeRemainingSeconds,
            difficulty = state.difficulty
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mixing bowl
        MixingBowl(
            drops = state.drops,
            mixedColor = state.mixedColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Color palette
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
        GameActionButtons(
            state = state,
            viewModel = viewModel,
            hapticManager = hapticManager
        )

        Spacer(modifier = Modifier.height(4.dp))
    }
}

/**
 * Landscape orientation game layout.
 * Arranges UI in two columns for better use of horizontal space.
 *
 * Layout structure:
 * - Left column: Menu, Level, Score, Target, Mixing bowl, Action buttons
 * - Right column: Color palette grid (4 columns)
 *
 * @param state Current game state
 * @param viewModel Game view model for actions
 * @param hapticManager Haptic feedback manager
 * @param onShowMenu Callback to show menu
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LandscapeGameLayout(
    state: GameState,
    viewModel: GameViewModel,
    hapticManager: HapticManager,
    onShowMenu: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left side: Target, Mixing Bowl, and Buttons
        Column(
            modifier = Modifier
                .weight(0.45f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: Menu, Level, Score, Target
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onShowMenu,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        LevelDisplay(level = state.currentLevel)
                    }

                    // Animated score
                    val animatedScore by animateIntAsState(
                        targetValue = state.currentScore,
                        animationSpec = spring(dampingRatio = 0.7f, stiffness = 200f),
                        label = "scoreAnimation"
                    )

                    Text(
                        text = "$animatedScore",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.animateContentSize()
                    )
                }

                TargetColor(targetColor = state.targetColor)

                TimerDisplay(
                    timeRemaining = state.timeRemainingSeconds,
                    difficulty = state.difficulty
                )
            }

            // Center: Mixing Bowl
            MixingBowl(
                drops = state.drops,
                mixedColor = state.mixedColor
            )

            // Bottom: Action buttons
            GameActionButtons(
                state = state,
                viewModel = viewModel,
                hapticManager = hapticManager,
                isLandscape = true
            )
        }

        // Right side: Color palette in a grid (4 columns for landscape)
        Column(
            modifier = Modifier
                .weight(0.55f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                maxItemsInEachRow = 4
            ) {
                state.unlockedColors.forEach { color ->
                    ColorButton(
                        color = color,
                        dropCount = state.getDropCount(color),
                        onClick = {
                            hapticManager.performHaptic(HapticManager.HapticType.LIGHT_TAP)
                            viewModel.addColorDrop(color)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Game action buttons (Check Match and Clear Bowl).
 * Handles button state and haptic feedback.
 *
 * @param state Current game state
 * @param viewModel Game view model for actions
 * @param hapticManager Haptic feedback manager
 * @param isLandscape Whether in landscape orientation
 */
@Composable
private fun GameActionButtons(
    state: GameState,
    viewModel: GameViewModel,
    hapticManager: HapticManager,
    isLandscape: Boolean = false
) {
    val buttonHeight = if (isLandscape) 52.dp else 48.dp
    val clearButtonHeight = if (isLandscape) 44.dp else 48.dp
    val fontSize = if (isLandscape) 17.sp else 18.sp
    val clearFontSize = if (isLandscape) 16.sp else 18.sp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (!isLandscape) Modifier.padding(horizontal = 12.dp) else Modifier)
    ) {
        // Check Match button
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
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight)
        ) {
            Text(
                text = if (state.hasCheckedThisRound) "Already Checked" else "Check Match!",
                fontSize = fontSize,
                fontWeight = FontWeight.Bold
            )
        }

        // Clear Bowl button
        Button(
            onClick = {
                hapticManager.performHaptic(HapticManager.HapticType.LIGHT_TAP)
                viewModel.clearBowl()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(clearButtonHeight)
        ) {
            Text(
                text = "Clear Bowl",
                fontSize = clearFontSize,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
