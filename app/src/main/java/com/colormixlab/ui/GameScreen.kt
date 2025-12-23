package com.colormixlab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.colormixlab.data.LeaderboardManager
import com.colormixlab.game.GameViewModel
import com.colormixlab.model.LeaderboardEntry
import com.colormixlab.ui.components.LandscapeGameLayout
import com.colormixlab.ui.components.MathChallengeDialog
import com.colormixlab.ui.components.PortraitGameLayout
import com.colormixlab.ui.components.GameCompletionCelebration
import com.colormixlab.ui.dialogs.MenuDialog
import com.colormixlab.ui.dialogs.NicknameDialog
import com.colormixlab.ui.dialogs.ResultDialog
import com.colormixlab.ui.LeaderboardDialog
import com.colormixlab.utils.HapticManager

/**
 * Main game screen component.
 * Manages game state, layout orientation, and displays game UI.
 *
 * Features:
 * - Adaptive layout (portrait/landscape)
 * - Keeps screen on during gameplay
 * - Manages dialogs (results, menu, nickname, math challenge)
 * - Timer pause during dialogs
 * - Leaderboard integration
 *
 * @param viewModel Game view model managing game state and logic
 * @param onNavigateToIntro Callback to navigate back to intro screen
 */
@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
    onNavigateToIntro: () -> Unit = {}
) {
    // Game state and utilities
    val state = viewModel.gameState.value
    val context = LocalContext.current
    val hapticManager = remember { HapticManager(context) }
    val leaderboardManager = remember { LeaderboardManager(context) }

    // Dialog states
    var showMenu by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }
    var showNicknameDialog by remember { mutableStateOf(false) }
    var showFinalLeaderboard by remember { mutableStateOf(false) }

    // Keep screen on during game
    KeepScreenOn()

    // Determine orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Pause timer when dialogs are open
    PauseTimerDuringDialogs(
        showSuccessDialog = state.showSuccessDialog,
        showMenu = showMenu,
        needsMathChallenge = state.needsMathChallenge,
        viewModel = viewModel
    )

    // Show celebration animation when game completes
    LaunchedEffect(state.isGameCompleted) {
        if (state.isGameCompleted && !showCelebration && !showNicknameDialog) {
            showCelebration = true
        }
    }

    // Main game layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(if (isLandscape) 8.dp else 12.dp)
    ) {
        if (isLandscape) {
            LandscapeGameLayout(
                state = state,
                viewModel = viewModel,
                hapticManager = hapticManager,
                onShowMenu = { showMenu = true }
            )
        } else {
            PortraitGameLayout(
                state = state,
                viewModel = viewModel,
                hapticManager = hapticManager,
                onShowMenu = { showMenu = true }
            )
        }
    }

    // Dialogs
    GameDialogs(
        state = state,
        viewModel = viewModel,
        showMenu = showMenu,
        showNicknameDialog = showNicknameDialog,
        showFinalLeaderboard = showFinalLeaderboard,
        leaderboardManager = leaderboardManager,
        onDismissMenu = { showMenu = false },
        onDismissNickname = { showNicknameDialog = false },
        onDismissFinalLeaderboard = { showFinalLeaderboard = false },
        onNavigateToIntro = onNavigateToIntro,
        onShowFinalLeaderboard = { showFinalLeaderboard = true }
    )

    // Celebration animation on game completion
    if (showCelebration) {
        GameCompletionCelebration(
            onAnimationComplete = {
                showCelebration = false
                showNicknameDialog = true
            }
        )
    }
}

/**
 * Effect to keep screen on during gameplay.
 */
@Composable
private fun KeepScreenOn() {
    val view = LocalView.current
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose {
            view.keepScreenOn = false
        }
    }
}

/**
 * Effect to pause timer when any dialog is open.
 */
@Composable
private fun PauseTimerDuringDialogs(
    showSuccessDialog: Boolean,
    showMenu: Boolean,
    needsMathChallenge: Boolean,
    viewModel: GameViewModel
) {
    LaunchedEffect(showSuccessDialog, showMenu, needsMathChallenge) {
        if (showSuccessDialog || showMenu || needsMathChallenge) {
            viewModel.pauseTimer()
        } else {
            viewModel.resumeTimer()
        }
    }
}

/**
 * All game dialogs (results, menu, nickname, math challenge, leaderboard).
 */
@Composable
private fun GameDialogs(
    state: com.colormixlab.game.GameState,
    viewModel: GameViewModel,
    showMenu: Boolean,
    showNicknameDialog: Boolean,
    showFinalLeaderboard: Boolean,
    leaderboardManager: LeaderboardManager,
    onDismissMenu: () -> Unit,
    onDismissNickname: () -> Unit,
    onDismissFinalLeaderboard: () -> Unit,
    onNavigateToIntro: () -> Unit,
    onShowFinalLeaderboard: () -> Unit
) {
    // Success/Failure result dialog
    if (state.showSuccessDialog) {
        ResultDialog(
            similarity = state.similarity,
            isSuccess = state.isMatched,
            level = state.currentLevel,
            basePoints = state.lastBasePoints,
            timeBonus = state.lastTimeBonus,
            unlockedColors = state.unlockedColors,
            onNextLevel = { viewModel.nextLevel() },
            onRetry = { viewModel.retryLevel() },
            getMessage = { viewModel.getResultMessage(it) },
            getEmoji = { viewModel.getResultEmoji(it) }
        )
    }

    // Menu dialog
    if (showMenu) {
        MenuDialog(
            onDismiss = onDismissMenu,
            onRestartGame = {
                onDismissMenu()
                onNavigateToIntro()
            },
            onShowLeaderboard = {
                onDismissMenu()
                // Handled within menu
            },
            onResetLeaderboard = {
                leaderboardManager.clearLeaderboard()
            },
            onFinishGame = {
                viewModel.forceFinishGame()
            },
            leaderboardEntries = leaderboardManager.getEntries()
        )
    }

    // Nickname entry dialog (after game completion)
    if (showNicknameDialog) {
        NicknameDialog(
            finalScore = state.currentScore,
            finalLevel = state.currentLevel,
            onSubmit = { nickname ->
                // Save to leaderboard
                leaderboardManager.addEntry(
                    LeaderboardEntry(
                        nickname = nickname,
                        score = state.currentScore,
                        level = state.currentLevel,
                        difficulty = state.difficulty
                    )
                )
                onDismissNickname()
                onShowFinalLeaderboard()
            }
        )
    }

    // Final leaderboard (shown after nickname entry)
    if (showFinalLeaderboard) {
        LeaderboardDialog(
            entries = leaderboardManager.getEntries(),
            onDismiss = {
                onDismissFinalLeaderboard()
                onNavigateToIntro()
            }
        )
    }

    // Math challenge dialog (for color unlocks)
    if (state.needsMathChallenge) {
        val challengeInfo = viewModel.getCurrentMathChallenge()
        challengeInfo?.let { (type, nextColor) ->
            MathChallengeDialog(
                difficulty = state.difficulty,
                level = state.currentLevel,
                challengeType = type,
                nextColorToUnlock = nextColor,
                onDismiss = {
                    viewModel.completeMathChallenge()
                },
                onExit = {
                    viewModel.exitGame()
                    onNavigateToIntro()
                },
                onWrongAnswer = {
                    viewModel.deductPointsForWrongMathAnswer()
                }
            )
        }
    }
}
