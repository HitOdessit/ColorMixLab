package com.colormixlab.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.offset
import com.colormixlab.game.Difficulty
import com.colormixlab.game.math.MathChallengeState
import com.colormixlab.game.math.MathQuestion
import com.colormixlab.game.math.MathQuestionGenerator
import com.colormixlab.ui.components.ConfettiEffect
import com.colormixlab.utils.HapticManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MathChallengeScreen(
    difficulty: Difficulty,
    onChallengeComplete: () -> Unit
) {
    var challengeState by remember {
        mutableStateOf(
            MathChallengeState(
                currentQuestion = MathQuestionGenerator.generateQuestion(difficulty, 1)
            )
        )
    }
    
    var showConfetti by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val hapticManager = remember { HapticManager(context) }
    val scope = rememberCoroutineScope()
    
    // Handle answer feedback timing
    LaunchedEffect(challengeState.showingAnswer) {
        if (challengeState.showingAnswer) {
            when (difficulty) {
                Difficulty.EASY -> {
                    // Wait for user to click OK button (handled in UI)
                }
                Difficulty.MEDIUM -> {
                    delay(3000)
                    // Move to next question
                    if (challengeState.consecutiveCorrect >= 5) {
                        showConfetti = true
                        delay(2000)
                        onChallengeComplete()
                    } else {
                        challengeState = challengeState.copy(
                            currentQuestion = MathQuestionGenerator.generateQuestion(difficulty, 1),
                            showingAnswer = false,
                            selectedAnswer = null
                        )
                    }
                }
                Difficulty.HARD -> {
                    delay(500)
                    // Move to next question immediately
                    if (challengeState.consecutiveCorrect >= 5) {
                        showConfetti = true
                        delay(2000)
                        onChallengeComplete()
                    } else {
                        challengeState = challengeState.copy(
                            currentQuestion = MathQuestionGenerator.generateQuestion(difficulty, 1),
                            showingAnswer = false,
                            selectedAnswer = null
                        )
                    }
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Text(
                text = "🧮 Math Challenge!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Answer 5 questions correctly to start!",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Progress
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Correct: ${challengeState.consecutiveCorrect}/5",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = challengeState.consecutiveCorrect / 5f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Question
            challengeState.currentQuestion?.let { question ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = "What is ${question.multiplier1} × ${question.multiplier2}?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Answer grid (3x3)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (row in 0..2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            for (col in 0..2) {
                                val index = row * 3 + col
                                val answer = question.allOptions[index]
                                
                                AnswerButton(
                                    answer = answer,
                                    isCorrect = answer == question.correctAnswer,
                                    isSelected = challengeState.selectedAnswer == answer,
                                    showingAnswer = challengeState.showingAnswer,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        if (!challengeState.showingAnswer) {
                                            val isCorrect = answer == question.correctAnswer

                                            if (isCorrect) {
                                                hapticManager.performHaptic(HapticManager.HapticType.SUCCESS)
                                            } else {
                                                hapticManager.performHaptic(HapticManager.HapticType.ERROR)
                                            }
                                            
                                            challengeState = challengeState.copy(
                                                selectedAnswer = answer,
                                                showingAnswer = true,
                                                lastAnswerCorrect = isCorrect,
                                                consecutiveCorrect = if (isCorrect) {
                                                    challengeState.consecutiveCorrect + 1
                                                } else {
                                                    challengeState.consecutiveCorrect // Keep progress
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                
                // OK button for Easy mode
                if (difficulty == Difficulty.EASY && challengeState.showingAnswer) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
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
            }
        }
        
        // Confetti effect
        if (showConfetti) {
            ConfettiEffect()
        }
    }
}

@Composable
fun AnswerButton(
    answer: Int,
    isCorrect: Boolean,
    isSelected: Boolean,
    showingAnswer: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Shake animation for wrong answer
    var shouldShake by remember { mutableStateOf(false) }
    
    LaunchedEffect(showingAnswer, isSelected, isCorrect) {
        if (showingAnswer && isSelected && !isCorrect) {
            shouldShake = true
            delay(500)
            shouldShake = false
        }
    }
    
    val offsetX by animateFloatAsState(
        targetValue = if (shouldShake) {
            // Create shake effect
            when ((System.currentTimeMillis() / 50) % 4) {
                0L -> -8f
                1L -> 8f
                2L -> -8f
                else -> 8f
            }
        } else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessHigh
        )
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected && showingAnswer) {
            if (isCorrect) 1.1f else 0.95f
        } else if (isSelected) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    val backgroundColor = when {
        showingAnswer && isCorrect -> Color(0xFF4CAF50) // Green for correct
        showingAnswer && isSelected && !isCorrect -> Color(0xFFF44336) // Red for wrong selection
        else -> MaterialTheme.colorScheme.surface
    }
    
    val borderColor = when {
        showingAnswer && isCorrect -> Color(0xFF2E7D32) // Dark green
        showingAnswer && isSelected && !isCorrect -> Color(0xFFC62828) // Dark red
        else -> MaterialTheme.colorScheme.outline
    }
    
    Button(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale)
            .offset(x = offsetX.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
        enabled = !showingAnswer
    ) {
        Text(
            text = answer.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (showingAnswer && (isCorrect || (isSelected && !isCorrect))) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

