package com.colormixlab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.colormixlab.game.Difficulty
import com.colormixlab.game.math.MathChallengeState
import com.colormixlab.game.math.MathQuestionGenerator
import com.colormixlab.ui.components.ConfettiEffect
import com.colormixlab.ui.components.MathChallengeLayout
import com.colormixlab.utils.HapticManager
import com.colormixlab.utils.MathChallengeTimer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Full-screen math challenge component.
 * Used for the initial math challenge before starting the game.
 *
 * Requires 5 correct answers to complete (vs 3 for in-game challenges).
 * Shows confetti celebration upon completion.
 *
 * @param difficulty Difficulty level for question generation and timer
 * @param onChallengeComplete Callback when 5 questions answered correctly
 * @param onBack Callback for back button press
 */
@Composable
fun MathChallengeScreen(
    difficulty: Difficulty,
    onChallengeComplete: () -> Unit,
    onBack: () -> Unit = {}
) {
    // State management
    var challengeState by remember {
        mutableStateOf(
            MathChallengeState(
                currentQuestion = MathQuestionGenerator.generateQuestion(difficulty, 1),
                timeRemaining = MathChallengeTimer.getTimerDuration(difficulty),
                isTimerActive = MathChallengeTimer.isTimerEnabled(difficulty)
            )
        )
    }
    var showConfetti by remember { mutableStateOf(false) }

    // Utilities
    val context = LocalContext.current
    val hapticManager = remember { HapticManager(context) }
    val scope = rememberCoroutineScope()

    // Timer countdown effect
    ChallengeTimerEffect(
        challengeState = challengeState,
        hapticManager = hapticManager,
        onTimerExpired = {
            challengeState = challengeState.copy(
                showingAnswer = true,
                lastAnswerCorrect = false,
                isTimerActive = false
            )
        },
        onTimerTick = { updatedState ->
            challengeState = updatedState
        }
    )

    // Auto-advance effect for Medium/Hard
    ChallengeAutoAdvanceEffect(
        difficulty = difficulty,
        challengeState = challengeState,
        onComplete = {
            showConfetti = true
            scope.launch {
                delay(2000)
                onChallengeComplete()
            }
        },
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

    // Main UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        // Back button
        ChallengeBackButton(onClick = onBack)

        // Unified math challenge layout
        MathChallengeLayout(
            challengeState = challengeState,
            difficulty = difficulty,
            requiredCount = 5,
            onAnswerClick = { answer ->
                handleChallengeAnswerClick(
                    answer = answer,
                    correctAnswer = challengeState.currentQuestion?.correctAnswer ?: 0,
                    challengeState = challengeState,
                    hapticManager = hapticManager,
                    onStateUpdate = { newState -> challengeState = newState }
                )
            },
            onEasyContinue = {
                if (challengeState.consecutiveCorrect >= 5) {
                    showConfetti = true
                    scope.launch {
                        delay(2000)
                        onChallengeComplete()
                    }
                } else {
                    challengeState = challengeState.copy(
                        currentQuestion = MathQuestionGenerator.generateQuestion(difficulty, 1),
                        showingAnswer = false,
                        selectedAnswer = null
                    )
                }
            },
            modifier = Modifier.padding(top = 48.dp)
        )

        // Confetti celebration
        if (showConfetti) {
            ConfettiEffect()
        }
    }
}

/**
 * Timer countdown effect for challenge screen.
 */
@Composable
private fun ChallengeTimerEffect(
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

                // Haptic at warning threshold
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
 * Auto-advance effect for Medium/Hard difficulties.
 */
@Composable
private fun ChallengeAutoAdvanceEffect(
    difficulty: Difficulty,
    challengeState: MathChallengeState,
    onComplete: () -> Unit,
    onNextQuestion: (com.colormixlab.game.math.MathQuestion) -> Unit
) {
    LaunchedEffect(challengeState.showingAnswer) {
        if (challengeState.showingAnswer && difficulty != Difficulty.EASY) {
            val delayMs = if (difficulty == Difficulty.HARD) 500L else 1500L
            delay(delayMs)

            if (challengeState.consecutiveCorrect >= 5) {
                onComplete()
            } else {
                onNextQuestion(MathQuestionGenerator.generateQuestion(difficulty, 1))
            }
        }
    }
}

/**
 * Handle answer click for challenge screen.
 */
private fun handleChallengeAnswerClick(
    answer: Int,
    correctAnswer: Int,
    challengeState: MathChallengeState,
    hapticManager: HapticManager,
    onStateUpdate: (MathChallengeState) -> Unit
) {
    if (challengeState.showingAnswer) return

    val isCorrect = answer == correctAnswer

    // Haptic feedback
    if (isCorrect) {
        hapticManager.performHaptic(HapticManager.HapticType.SUCCESS)
    } else {
        hapticManager.performHaptic(HapticManager.HapticType.ERROR)
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
                0 // Reset on wrong answer
            }
        )
    )
}

/**
 * Back button component.
 */
@Composable
private fun BoxScope.ChallengeBackButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(top = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back to Intro",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

