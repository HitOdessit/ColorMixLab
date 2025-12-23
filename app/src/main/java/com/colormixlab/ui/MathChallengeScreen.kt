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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.colormixlab.game.Difficulty
import com.colormixlab.game.math.MathChallengeState
import com.colormixlab.game.math.MathQuestionGenerator
import com.colormixlab.ui.components.ConfettiEffect
import com.colormixlab.ui.components.MathQuestionGrid
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
            .padding(16.dp)
    ) {
        // Back button
        ChallengeBackButton(onClick = onBack)

        // Challenge content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            // Header
            ChallengeHeader(
                correctCount = challengeState.consecutiveCorrect,
                requiredCount = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress card
            ChallengeProgressCard(
                correctCount = challengeState.consecutiveCorrect,
                requiredCount = 5,
                timeRemaining = challengeState.timeRemaining
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Question and answers
            challengeState.currentQuestion?.let { question ->
                MathQuestionGrid(
                    question = question,
                    selectedAnswer = challengeState.selectedAnswer,
                    showingAnswer = challengeState.showingAnswer,
                    onAnswerClick = { answer ->
                        handleChallengeAnswerClick(
                            answer = answer,
                            correctAnswer = question.correctAnswer,
                            challengeState = challengeState,
                            hapticManager = hapticManager,
                            onStateUpdate = { newState -> challengeState = newState }
                        )
                    }
                )
            }

            // OK button for Easy mode
            if (difficulty == Difficulty.EASY && challengeState.showingAnswer) {
                Spacer(modifier = Modifier.height(16.dp))

                EasyChallengeButton(
                    onContinue = {
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
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

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

/**
 * Challenge header with title and instructions.
 */
@Composable
private fun ChallengeHeader(
    correctCount: Int,
    requiredCount: Int
) {
    Text(
        text = "🧮 Math Challenge!",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = "Answer $requiredCount questions correctly to start!",
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onBackground
    )
}

/**
 * Progress card showing correct count and timer.
 */
@Composable
private fun ChallengeProgressCard(
    correctCount: Int,
    requiredCount: Int,
    timeRemaining: Int?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Correct: $correctCount/$requiredCount",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = correctCount.toFloat() / requiredCount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Timer display (if applicable)
            timeRemaining?.let { time ->
                Spacer(modifier = Modifier.height(8.dp))
                ChallengeTimerDisplay(time)
            }
        }
    }
}

/**
 * Inline timer display for challenge screen.
 */
@Composable
private fun ChallengeTimerDisplay(timeRemaining: Int) {
    val timerColor = when {
        timeRemaining <= MathChallengeTimer.getWarningThreshold() -> androidx.compose.ui.graphics.Color.Red
        timeRemaining <= MathChallengeTimer.getCriticalThreshold() -> androidx.compose.ui.graphics.Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "⏱", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$timeRemaining",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = timerColor
        )
        Text(
            text = "s",
            fontSize = 12.sp,
            color = timerColor.copy(alpha = 0.7f)
        )
    }
}

/**
 * OK button for Easy mode.
 */
@Composable
private fun EasyChallengeButton(onContinue: () -> Unit) {
    Button(
        onClick = onContinue,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = "OK",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
