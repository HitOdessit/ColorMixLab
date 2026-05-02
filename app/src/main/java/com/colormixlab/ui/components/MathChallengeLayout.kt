package com.colormixlab.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.colormixlab.game.Difficulty
import com.colormixlab.game.MathChallengeType
import com.colormixlab.game.math.MathChallengeState
import com.colormixlab.model.GameColor

/**
 * Unified math challenge layout component.
 * Used by both pre-game (MathChallengeScreen) and in-game (MathChallengeDialog) challenges.
 * Renders identically in both contexts with adaptive portrait/landscape layouts.
 *
 * @param challengeState Current state of the math challenge
 * @param difficulty Game difficulty level
 * @param requiredCount Number of correct answers required (5 for pre-game, 3 for in-game)
 * @param challengeType Type of challenge (only used for in-game)
 * @param nextColorToUnlock Color to unlock (only used for in-game)
 * @param onAnswerClick Callback when an answer is clicked
 * @param onEasyContinue Callback when Easy mode OK button is clicked
 */
@Composable
fun MathChallengeLayout(
    challengeState: MathChallengeState,
    difficulty: Difficulty,
    requiredCount: Int,
    challengeType: MathChallengeType = MathChallengeType.NONE,
    nextColorToUnlock: GameColor? = null,
    onAnswerClick: (Int) -> Unit,
    onEasyContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight
        val isLandscape = availableWidth > availableHeight

        if (isLandscape) {
            // Landscape: Two-column layout (1/3 left, 2/3 right)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(availableWidth * 0.015f)
            ) {
                // Left column - Header and Progress (1/3)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (challengeType != MathChallengeType.NONE) {
                        // In-game challenge - show full header with challenge type
                        MathChallengeHeader(
                            challengeType = challengeType,
                            nextColorToUnlock = nextColorToUnlock,
                            correctCount = challengeState.consecutiveCorrect,
                            requiredCount = requiredCount,
                            timeRemaining = challengeState.timeRemaining,
                            availableWidth = availableWidth * 0.3f
                        )
                    } else {
                        // Pre-game challenge - show simple header
                        SimpleChallengeHeader(
                            correctCount = challengeState.consecutiveCorrect,
                            requiredCount = requiredCount,
                            timeRemaining = challengeState.timeRemaining,
                            availableWidth = availableWidth * 0.3f
                        )
                    }
                }

                // Right column - Question and Answers (2/3)
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    challengeState.currentQuestion?.let { question ->
                        MathQuestionGrid(
                            question = question,
                            selectedAnswer = challengeState.selectedAnswer,
                            showingAnswer = challengeState.showingAnswer,
                            onAnswerClick = onAnswerClick,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                    }

                    // OK button for Easy mode
                    if (difficulty == Difficulty.EASY && challengeState.showingAnswer) {
                        EasyContinueButton(
                            onContinue = onEasyContinue,
                            buttonHeight = availableHeight * 0.08f
                        )
                    }
                }
            }
        } else {
            // Portrait: Single column layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(availableWidth * 0.05f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(availableHeight * 0.025f)
            ) {
                // Header
                if (challengeType != MathChallengeType.NONE) {
                    // In-game challenge - show full header with challenge type
                    MathChallengeHeader(
                        challengeType = challengeType,
                        nextColorToUnlock = nextColorToUnlock,
                        correctCount = challengeState.consecutiveCorrect,
                        requiredCount = requiredCount,
                        timeRemaining = challengeState.timeRemaining,
                        availableWidth = availableWidth
                    )
                } else {
                    // Pre-game challenge - show simple header
                    SimpleChallengeHeader(
                        correctCount = challengeState.consecutiveCorrect,
                        requiredCount = requiredCount,
                        timeRemaining = challengeState.timeRemaining,
                        availableWidth = availableWidth
                    )
                }

                // Question and answer grid
                challengeState.currentQuestion?.let { question ->
                    MathQuestionGrid(
                        question = question,
                        selectedAnswer = challengeState.selectedAnswer,
                        showingAnswer = challengeState.showingAnswer,
                        onAnswerClick = onAnswerClick,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                // OK button for Easy mode
                if (difficulty == Difficulty.EASY && challengeState.showingAnswer) {
                    EasyContinueButton(
                        onContinue = onEasyContinue,
                        buttonHeight = availableHeight * 0.08f
                    )
                }
            }
        }
    }
}

/**
 * Simple header for pre-game challenge (no challenge type info).
 */
@Composable
private fun SimpleChallengeHeader(
    correctCount: Int,
    requiredCount: Int,
    timeRemaining: Int?,
    availableWidth: androidx.compose.ui.unit.Dp
) {
    // Calculate sizes based on available width
    val titleFontSize = androidx.compose.ui.unit.TextUnit(
        (availableWidth.value * 0.065f).coerceIn(22f, 32f),
        androidx.compose.ui.unit.TextUnitType.Sp
    )
    val instructionFontSize = androidx.compose.ui.unit.TextUnit(
        (availableWidth.value * 0.038f).coerceIn(14f, 18f),
        androidx.compose.ui.unit.TextUnitType.Sp
    )
    val countFontSize = androidx.compose.ui.unit.TextUnit(
        (availableWidth.value * 0.048f).coerceIn(18f, 24f),
        androidx.compose.ui.unit.TextUnitType.Sp
    )
    val progressHeight = (availableWidth.value * 0.024f).dp.coerceIn(8.dp, 12.dp)
    val verticalSpacing = (availableWidth.value * 0.018f).dp.coerceIn(6.dp, 10.dp)
    val cardPadding = (availableWidth.value * 0.028f).dp.coerceIn(10.dp, 14.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        // Title
        Text(
            text = "🧮 Math Challenge!",
            fontSize = titleFontSize,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(verticalSpacing))

        // Instructions
        Text(
            text = "Answer $requiredCount questions correctly to start!",
            fontSize = instructionFontSize,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(verticalSpacing))

        // Progress card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(cardPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(verticalSpacing)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Correct answer count
                    Text(
                        text = "Correct: $correctCount/$requiredCount",
                        fontSize = countFontSize,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    // Timer display (if enabled)
                    timeRemaining?.let { time ->
                        SimpleTimerDisplay(time, availableWidth)
                    }
                }

                // Progress bar
                LinearProgressIndicator(
                    progress = { correctCount.toFloat() / requiredCount },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(progressHeight),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

/**
 * Simple timer display for pre-game challenge.
 */
@Composable
private fun SimpleTimerDisplay(timeRemaining: Int, availableWidth: androidx.compose.ui.unit.Dp) {
    val emojiSize = androidx.compose.ui.unit.TextUnit(
        (availableWidth.value * 0.042f).coerceIn(14f, 22f),
        androidx.compose.ui.unit.TextUnitType.Sp
    )
    val timeFontSize = androidx.compose.ui.unit.TextUnit(
        (availableWidth.value * 0.048f).coerceIn(16f, 24f),
        androidx.compose.ui.unit.TextUnitType.Sp
    )
    val unitFontSize = androidx.compose.ui.unit.TextUnit(
        (availableWidth.value * 0.032f).coerceIn(11f, 16f),
        androidx.compose.ui.unit.TextUnitType.Sp
    )

    val timerColor = when {
        timeRemaining <= com.colormixlab.utils.MathChallengeTimer.getWarningThreshold() -> androidx.compose.ui.graphics.Color.Red
        timeRemaining <= com.colormixlab.utils.MathChallengeTimer.getCriticalThreshold() -> androidx.compose.ui.graphics.Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = "⏱", fontSize = emojiSize)
        Text(
            text = "$timeRemaining",
            fontSize = timeFontSize,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = timerColor
        )
        Text(
            text = "s",
            fontSize = unitFontSize,
            color = timerColor.copy(alpha = 0.7f)
        )
    }
}

/**
 * Continue button for Easy mode.
 */
@Composable
private fun EasyContinueButton(
    onContinue: () -> Unit,
    buttonHeight: androidx.compose.ui.unit.Dp
) {
    val buttonFontSize = androidx.compose.ui.unit.TextUnit(
        (buttonHeight.value * 0.38f).coerceIn(16f, 24f),
        androidx.compose.ui.unit.TextUnitType.Sp
    )

    Button(
        onClick = onContinue,
        modifier = Modifier
            .fillMaxWidth()
            .height(buttonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = "OK",
            fontSize = buttonFontSize,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}
