package com.colormixlab.game.math

/**
 * Represents a multiplication question with one correct answer and 8 wrong answers
 */
data class MathQuestion(
    val multiplier1: Int,
    val multiplier2: Int,
    val correctAnswer: Int,
    val allOptions: List<Int> // Contains correct answer + 8 wrong answers, shuffled
) {
    init {
        require(allOptions.size == 9) { "Must have exactly 9 options" }
        require(correctAnswer in allOptions) { "Correct answer must be in options" }
    }
}

/**
 * Tracks the state of a math challenge session
 */
data class MathChallengeState(
    val questionsAnswered: Int = 0,
    val consecutiveCorrect: Int = 0,
    val currentQuestion: MathQuestion? = null,
    val showingAnswer: Boolean = false,
    val lastAnswerCorrect: Boolean = false,
    val selectedAnswer: Int? = null
)

