package com.colormixlab.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
    onChallengeComplete: () -> Unit,
    onBack: () -> Unit = {}
) {
    // Helper function to get timer duration for math challenge
    fun getTimerDuration(difficulty: Difficulty): Int? {
        return when (difficulty) {
            Difficulty.EASY -> null // No timer
            Difficulty.MEDIUM -> 20
            Difficulty.HARD -> 10
        }
    }

    var challengeState by remember {
        mutableStateOf(
            MathChallengeState(
                currentQuestion = MathQuestionGenerator.generateQuestion(difficulty, 1),
                timeRemaining = getTimerDuration(difficulty),
                isTimerActive = getTimerDuration(difficulty) != null
            )
        )
    }
    
    var showConfetti by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val hapticManager = remember { HapticManager(context) }
    val scope = rememberCoroutineScope()
    
    // Timer countdown
    LaunchedEffect(challengeState.isTimerActive, challengeState.timeRemaining, challengeState.showingAnswer) {
        if (challengeState.isTimerActive && !challengeState.showingAnswer) {
            val timeRemaining = challengeState.timeRemaining ?: return@LaunchedEffect
            if (timeRemaining > 0) {
                delay(1000)
                
                // Haptic feedback when timer is 5 seconds or less
                if (timeRemaining <= 5) {
                    hapticManager.performHaptic(HapticManager.HapticType.LIGHT_TAP)
                }
                
                challengeState = challengeState.copy(timeRemaining = timeRemaining - 1)
            } else {
                // Time's up - treat as wrong answer
                hapticManager.performHaptic(HapticManager.HapticType.ERROR)
                challengeState = challengeState.copy(
                    showingAnswer = true,
                    lastAnswerCorrect = false,
                    isTimerActive = false
                    // Keep consecutive correct count (don't reset on initial screen)
                )
            }
        }
    }
    
    // Handle answer feedback timing
    LaunchedEffect(challengeState.showingAnswer) {
        if (challengeState.showingAnswer) {
            when (difficulty) {
                Difficulty.EASY -> {
                    // Wait for user to click OK button (handled in UI)
                }
                Difficulty.MEDIUM -> {
                    delay(1500)  // Faster animation: 1.5 seconds
                    // Move to next question
                    if (challengeState.consecutiveCorrect >= 5) {
                        showConfetti = true
                        delay(2000)
                        onChallengeComplete()
                    } else {
                        challengeState = challengeState.copy(
                            currentQuestion = MathQuestionGenerator.generateQuestion(difficulty, 1),
                            showingAnswer = false,
                            selectedAnswer = null,
                            timeRemaining = getTimerDuration(difficulty),
                            isTimerActive = getTimerDuration(difficulty) != null
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
                            selectedAnswer = null,
                            timeRemaining = getTimerDuration(difficulty),
                            isTimerActive = getTimerDuration(difficulty) != null
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
            .padding(16.dp)
    ) {
        // Back button with visible background
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
                onClick = onBack,
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

          Column(
              modifier = Modifier
                  .fillMaxSize()
                  .align(Alignment.Center),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Top
          ) {
              Spacer(modifier = Modifier.height(64.dp))

              // Header
              Text(
                  text = "🧮 Math Challenge!",
                  fontSize = 28.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
              )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Answer 5 questions correctly to start!",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

              Spacer(modifier = Modifier.height(16.dp))

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
                          .padding(12.dp),
                      horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                      Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                      ) {
                          Text(
                              text = "Correct: ${challengeState.consecutiveCorrect}/5",
                              fontSize = 20.sp,
                              fontWeight = FontWeight.Bold,
                              color = MaterialTheme.colorScheme.onPrimaryContainer
                          )
                          
                          // Timer display
                          challengeState.timeRemaining?.let { time ->
                              val timerColor = when {
                                  time <= 5 -> Color.Red
                                  time <= 10 -> Color(0xFFFF9800) // Orange
                                  else -> MaterialTheme.colorScheme.primary
                              }
                              
                              Row(
                                  verticalAlignment = Alignment.CenterVertically,
                                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                              ) {
                                  Text(
                                      text = "⏱",
                                      fontSize = 18.sp
                                  )
                                  Text(
                                      text = "$time",
                                      fontSize = 20.sp,
                                      fontWeight = FontWeight.Bold,
                                      color = timerColor
                                  )
                                  Text(
                                      text = "s",
                                      fontSize = 14.sp,
                                      color = timerColor.copy(alpha = 0.7f)
                                  )
                              }
                          }
                      }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = challengeState.consecutiveCorrect / 5f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            
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
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Answer grid (3x3)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (row in 0..2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
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

                                            // Single haptic feedback for better performance
                                            if (isCorrect) {
                                                hapticManager.performHaptic(HapticManager.HapticType.SUCCESS)
                                            } else {
                                                hapticManager.performHaptic(HapticManager.HapticType.ERROR)
                                            }
                                            
                                            challengeState = challengeState.copy(
                                                selectedAnswer = answer,
                                                showingAnswer = true,
                                                lastAnswerCorrect = isCorrect,
                                                isTimerActive = false, // Stop timer
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
                    Spacer(modifier = Modifier.height(16.dp))

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
                                    selectedAnswer = null,
                                    timeRemaining = getTimerDuration(difficulty),
                                    isTimerActive = getTimerDuration(difficulty) != null
                                )
                            }
                        },
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

                    Spacer(modifier = Modifier.height(8.dp))
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
    // Shake animation for wrong answer using Animatable
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(showingAnswer, isSelected, isCorrect) {
        if (showingAnswer && isSelected && !isCorrect) {
            // Quick shake animation
            repeat(3) {
                offsetX.animateTo(8f, animationSpec = tween(50))
                offsetX.animateTo(-8f, animationSpec = tween(50))
            }
            offsetX.animateTo(0f, animationSpec = tween(50))
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected && showingAnswer) {
            if (isCorrect) 1.15f else 0.95f
        } else if (isSelected) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    // Simplified pulse animation for correct answer
    val pulse by animateFloatAsState(
        targetValue = if (showingAnswer && isSelected && isCorrect) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "pulse"
    )
    
    val backgroundColor = when {
        showingAnswer && isCorrect -> Color(0xFF4CAF50) // Bright green for correct
        showingAnswer && isSelected && !isCorrect -> Color(0xFFF44336) // Red for wrong selection
        else -> MaterialTheme.colorScheme.surface
    }
    
    val borderColor = when {
        showingAnswer && isCorrect -> Color(0xFF2E7D32) // Dark green
        showingAnswer && isSelected && !isCorrect -> Color(0xFFC62828) // Dark red
        else -> MaterialTheme.colorScheme.outline
    }
    
    val borderWidth = if (showingAnswer && isCorrect) 4.dp else 2.dp

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale)
            .offset(x = offsetX.value.dp),
        contentAlignment = Alignment.Center
    ) {
        // Simplified glow effect for correct answer
        if (showingAnswer && isSelected && isCorrect && pulse > 0.1f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.15f + pulse * 0.15f)
                    .background(
                        color = Color(0xFF4CAF50).copy(alpha = 0.25f * pulse),
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }
        
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(borderWidth, borderColor),
            enabled = !showingAnswer,
            contentPadding = PaddingValues(4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Show number
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
                
                // Show icon overlay for feedback
                if (showingAnswer) {
                    if (isSelected && isCorrect) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Correct",
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(20.dp)
                        )
                    } else if (isSelected && !isCorrect) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Wrong",
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(20.dp)
                        )
                    }
                    
                    // Highlight correct answer if it wasn't selected
                    if (isCorrect && !isSelected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Correct Answer",
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

