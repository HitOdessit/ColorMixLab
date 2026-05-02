package com.colormixlab.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.colormixlab.game.Difficulty
import com.colormixlab.game.MathChallengeType
import com.colormixlab.game.math.MathChallengeState
import com.colormixlab.game.math.MathQuestionGenerator
import com.colormixlab.model.GameColor
import com.colormixlab.utils.HapticManager
import com.colormixlab.utils.MathChallengeTimer
import kotlinx.coroutines.delay

/**
 * Math challenge dialog component.
 * Presents multiplication questions that must be answered correctly to unlock colors or progress.
 *
 * This component handles:
 * - Timer countdown (if difficulty requires it)
 * - Answer validation and feedback
 * - Progress tracking (3 correct answers required)
 * - Haptic feedback
 * - Point deduction for wrong answers
 *
 * @param difficulty Game difficulty level (affects timer duration)
 * @param level Current game level (affects question difficulty)
 * @param challengeType Type of challenge (color unlock, milestone, etc.)
 * @param nextColorToUnlock Color to be unlocked upon completion
 * @param onDismiss Callback when challenge is completed successfully
 * @param onExit Callback when user chooses to exit the game
 * @param onWrongAnswer Callback when user answers incorrectly (for point deduction)
 */
@Composable
fun MathChallengeDialog(
    difficulty: Difficulty,
    level: Int,
    challengeType: MathChallengeType,
    nextColorToUnlock: GameColor?,
    onDismiss: () -> Unit,
    onExit: () -> Unit = {},
    onWrongAnswer: () -> Unit = {}
) {
    // State management
    var challengeState by remember {
        mutableStateOf(
            MathChallengeState(
                currentQuestion = MathQuestionGenerator.generateQuestion(difficulty, level),
                timeRemaining = MathChallengeTimer.getTimerDuration(difficulty),
                isTimerActive = MathChallengeTimer.isTimerEnabled(difficulty)
            )
        )
    }
    var showExitConfirmation by remember { mutableStateOf(false) }

    // Utilities
    val context = LocalContext.current
    val hapticManager = remember { HapticManager(context) }

    // Timer countdown effect
    TimerCountdownEffect(
        challengeState = challengeState,
        hapticManager = hapticManager,
        onTimerExpired = {
            onWrongAnswer()
            challengeState = challengeState.copy(
                showingAnswer = true,
                lastAnswerCorrect = false,
                consecutiveCorrect = 0,
                isTimerActive = false
            )
        },
        onTimerTick = { updatedState ->
            challengeState = updatedState
        }
    )

    // Auto-advance effect (for Medium/Hard difficulties)
    AutoAdvanceEffect(
        difficulty = difficulty,
        challengeState = challengeState,
        onDismiss = onDismiss,
        onNextQuestion = { newQuestion ->
            challengeState = challengeState.copy(
                currentQuestion = newQuestion,
                showingAnswer = false,
                selectedAnswer = null,
                timeRemaining = MathChallengeTimer.getTimerDuration(difficulty),
                isTimerActive = MathChallengeTimer.isTimerEnabled(difficulty)
            )
        }
    )

    // Main dialog
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    Dialog(
        onDismissRequest = { /* Prevent dismissal */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.98f else 1f)
                .then(
                    if (isLandscape) Modifier.fillMaxHeight(0.95f)
                    else Modifier.wrapContentHeight()
                )
                .padding(if (isLandscape) 4.dp else 16.dp),
            shape = RoundedCornerShape(if (isLandscape) 12.dp else 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Box {
                // Unified math challenge layout
                MathChallengeLayout(
                    challengeState = challengeState,
                    difficulty = difficulty,
                    requiredCount = 3,
                    challengeType = challengeType,
                    nextColorToUnlock = nextColorToUnlock,
                    onAnswerClick = { answer ->
                        handleAnswerClick(
                            answer = answer,
                            correctAnswer = challengeState.currentQuestion?.correctAnswer ?: 0,
                            challengeState = challengeState,
                            hapticManager = hapticManager,
                            onWrongAnswer = onWrongAnswer,
                            onStateUpdate = { newState -> challengeState = newState }
                        )
                    },
                    onEasyContinue = {
                        if (challengeState.consecutiveCorrect >= 3) {
                            onDismiss()
                        } else {
                            challengeState = challengeState.copy(
                                currentQuestion = MathQuestionGenerator.generateQuestion(difficulty, level),
                                showingAnswer = false,
                                selectedAnswer = null
                            )
                        }
                    }
                )

                // Exit button
                ExitButton(onClick = { showExitConfirmation = true })
            }
        }
    }

    // Exit confirmation dialog
    if (showExitConfirmation) {
        ExitConfirmationDialog(
            onConfirm = {
                showExitConfirmation = false
                onExit()
            },
            onDismiss = { showExitConfirmation = false }
        )
    }
}

/**
 * Timer countdown effect.
 * Manages the timer countdown and triggers callbacks when timer expires.
 */
@Composable
private fun TimerCountdownEffect(
    challengeState: MathChallengeState,
    hapticManager: HapticManager,
    onTimerExpired: () -> Unit,
    onTimerTick: (MathChallengeState) -> Unit
) {
    LaunchedEffect(challengeState.isTimerActive, challengeState.timeRemaining, challengeState.showingAnswer) {
        if (challengeState.isTimerActive && !challengeState.showingAnswer) {
            val timeRemaining = challengeState.timeRemaining ?: return@LaunchedEffect

            if (timeRemaining > 0) {
                delay(1000)

                // Haptic feedback at warning threshold
                if (timeRemaining <= MathChallengeTimer.getWarningThreshold()) {
                    hapticManager.performHaptic(HapticManager.HapticType.LIGHT_TAP)
                }

                onTimerTick(challengeState.copy(timeRemaining = timeRemaining - 1))
            } else {
                // Timer expired
                hapticManager.performHaptic(HapticManager.HapticType.ERROR)
                onTimerExpired()
            }
        }
    }
}

/**
 * Auto-advance effect for Medium and Hard difficulties.
 * Automatically advances to next question or completes the challenge.
 */
@Composable
private fun AutoAdvanceEffect(
    difficulty: Difficulty,
    challengeState: MathChallengeState,
    onDismiss: () -> Unit,
    onNextQuestion: (com.colormixlab.game.math.MathQuestion) -> Unit
) {
    LaunchedEffect(challengeState.showingAnswer) {
        if (challengeState.showingAnswer && difficulty != Difficulty.EASY) {
            val delayMs = if (difficulty == Difficulty.HARD) 500L else 550L
            delay(delayMs)

            if (challengeState.consecutiveCorrect >= 3) {
                delay(if (difficulty == Difficulty.HARD) 300L else 350L)
                onDismiss()
            } else {
                onNextQuestion(MathQuestionGenerator.generateQuestion(difficulty, 1))
            }
        }
    }
}

/**
 * Handle answer click logic.
 * Validates answer, provides feedback, and updates state.
 */
private fun handleAnswerClick(
    answer: Int,
    correctAnswer: Int,
    challengeState: MathChallengeState,
    hapticManager: HapticManager,
    onWrongAnswer: () -> Unit,
    onStateUpdate: (MathChallengeState) -> Unit
) {
    if (challengeState.showingAnswer) return

    val isCorrect = answer == correctAnswer

    // Haptic feedback
    if (isCorrect) {
        hapticManager.performHaptic(HapticManager.HapticType.SUCCESS)
    } else {
        hapticManager.performHaptic(HapticManager.HapticType.ERROR)
        onWrongAnswer()
    }

    // Update state
    onStateUpdate(
        challengeState.copy(
            selectedAnswer = answer,
            showingAnswer = true,
            lastAnswerCorrect = isCorrect,
            isTimerActive = false,
            consecutiveCorrect = if (isCorrect) {
                challengeState.consecutiveCorrect + 1
            } else {
                0
            }
        )
    )
}

/**
 * Exit button in top-right corner.
 */
@Composable
private fun BoxScope.ExitButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        tonalElevation = 2.dp
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Exit Game",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Exit confirmation dialog.
 */
@Composable
private fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Exit Game?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Are you sure you want to exit? Your current game progress will be lost.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Exit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
