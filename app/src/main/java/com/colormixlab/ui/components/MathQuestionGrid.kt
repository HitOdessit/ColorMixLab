package com.colormixlab.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.colormixlab.game.math.MathQuestion

/**
 * Question display and answer grid component for math challenges.
 * Automatically adapts to available screen space.
 *
 * @param question The math question to display
 * @param selectedAnswer The currently selected answer (null if none selected)
 * @param showingAnswer Whether to show answer feedback
 * @param onAnswerClick Callback when an answer is clicked
 * @param modifier Modifier for customization
 */
@Composable
fun MathQuestionGrid(
    question: MathQuestion,
    selectedAnswer: Int?,
    showingAnswer: Boolean,
    onAnswerClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableHeight = maxHeight
        val availableWidth = maxWidth
        val isLandscape = availableWidth > availableHeight

        // Calculate sizes based on available space
        val questionHeight = availableHeight * 0.15f
        // Reduce grid height more in landscape to prevent overlap
        val gridHeight = if (isLandscape) {
            availableHeight * 0.60f  // 60% in landscape
        } else {
            availableHeight * 0.68f  // 68% in portrait
        }
        val spacing = availableHeight * 0.02f

        // Button spacing - more aggressive in landscape mode with explicit margins
        val buttonSpacing = if (isLandscape) {
            (availableHeight.value * 0.035f).dp.coerceIn(12.dp, 20.dp)  // Increased spacing
        } else {
            (minOf(availableWidth, availableHeight).value * 0.02f).dp.coerceIn(8.dp, 14.dp)
        }
        
        // Button padding - additional margin around each button
        val buttonPadding = if (isLandscape) {
            (availableHeight.value * 0.015f).dp.coerceIn(4.dp, 8.dp)
        } else {
            2.dp
        }

        // Font sizes scale with available space - further reduced for better fit
        val questionFontSize = androidx.compose.ui.unit.TextUnit(
            (availableWidth.value * 0.04f).coerceIn(18f, 42f),
            androidx.compose.ui.unit.TextUnitType.Sp
        )
        val answerFontSize = androidx.compose.ui.unit.TextUnit(
            (availableWidth.value * 0.028f).coerceIn(12f, 28f),  // Reduced from 0.03/14-32 to 0.028/12-28
            androidx.compose.ui.unit.TextUnitType.Sp
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            // Question card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(questionHeight),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${question.multiplier1} × ${question.multiplier2} = ?",
                        fontSize = questionFontSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Answer grid - 3x3 grid that fills remaining space
            AnswerGrid(
                question = question,
                selectedAnswer = selectedAnswer,
                showingAnswer = showingAnswer,
                onAnswerClick = onAnswerClick,
                gridHeight = gridHeight,
                answerFontSize = answerFontSize,
                buttonSpacing = buttonSpacing,
                buttonPadding = buttonPadding
            )
        }
    }
}

/**
 * 3x3 answer grid that adapts to available space
 */
@Composable
private fun AnswerGrid(
    question: MathQuestion,
    selectedAnswer: Int?,
    showingAnswer: Boolean,
    onAnswerClick: (Int) -> Unit,
    gridHeight: androidx.compose.ui.unit.Dp,
    answerFontSize: androidx.compose.ui.unit.TextUnit,
    buttonSpacing: androidx.compose.ui.unit.Dp,
    buttonPadding: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(gridHeight),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in 0..2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (col in 0..2) {
                    val index = row * 3 + col
                    val answer = question.allOptions[index]

                    MathAnswerButton(
                        answer = answer,
                        isCorrect = answer == question.correctAnswer,
                        isSelected = selectedAnswer == answer,
                        showingAnswer = showingAnswer,
                        fontSize = answerFontSize,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .padding(buttonPadding),
                        onClick = { onAnswerClick(answer) }
                    )
                }
            }
        }
    }
}
