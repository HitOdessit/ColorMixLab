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
 * Shows the multiplication question and a 3x3 grid of answer options.
 *
 * @param question The math question to display
 * @param selectedAnswer The currently selected answer (null if none selected)
 * @param showingAnswer Whether to show answer feedback
 * @param onAnswerClick Callback when an answer is clicked
 */
@Composable
fun MathQuestionGrid(
    question: MathQuestion,
    selectedAnswer: Int?,
    showingAnswer: Boolean,
    onAnswerClick: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Question card
        Card(
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

        // Answer grid (3x3)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0..2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        val answer = question.allOptions[index]

                        MathAnswerButton(
                            answer = answer,
                            isCorrect = answer == question.correctAnswer,
                            isSelected = selectedAnswer == answer,
                            showingAnswer = showingAnswer,
                            modifier = Modifier.weight(1f),
                            onClick = { onAnswerClick(answer) }
                        )
                    }
                }
            }
        }
    }
}
