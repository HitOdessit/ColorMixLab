package com.colormixlab.game.math

import com.colormixlab.game.Difficulty
import kotlin.random.Random

object MathQuestionGenerator {
    
    /**
     * Generate a multiplication question based on difficulty and level
     */
    fun generateQuestion(difficulty: Difficulty, level: Int): MathQuestion {
        val timesTable = selectTimesTable(difficulty, level)
        val multiplier1 = timesTable
        val multiplier2 = Random.nextInt(2, 13) // 2 to 12, avoiding 1
        
        val correctAnswer = multiplier1 * multiplier2
        val wrongAnswers = generateWrongAnswers(correctAnswer, multiplier1, multiplier2)
        
        // Combine and shuffle all options
        val allOptions = (wrongAnswers + correctAnswer).shuffled()
        
        return MathQuestion(
            multiplier1 = multiplier1,
            multiplier2 = multiplier2,
            correctAnswer = correctAnswer,
            allOptions = allOptions
        )
    }
    
    /**
     * Select which times table to use based on difficulty and level
     */
    private fun selectTimesTable(difficulty: Difficulty, level: Int): Int {
        val baseTables = when (difficulty) {
            Difficulty.EASY -> listOf(2, 5, 10)
            Difficulty.MEDIUM -> listOf(3, 4, 9, 11)
            Difficulty.HARD -> listOf(6, 8)
        }
        
        // After level 20, add harder tables
        val tables = if (level > 20) {
            baseTables + listOf(7, 12)
        } else {
            baseTables
        }
        
        return tables.random()
    }
    
    /**
     * Generate 8 plausible wrong answers
     */
    private fun generateWrongAnswers(correctAnswer: Int, multiplier1: Int, multiplier2: Int): List<Int> {
        val wrongAnswers = mutableSetOf<Int>()
        
        // Strategy 1: Correct answer ± small amount (1-5)
        wrongAnswers.addAll(
            listOf(
                correctAnswer - Random.nextInt(1, 6),
                correctAnswer + Random.nextInt(1, 6)
            ).filter { it > 0 && it != correctAnswer }
        )
        
        // Strategy 2: Correct answer ± larger amount (10-20)
        wrongAnswers.addAll(
            listOf(
                correctAnswer - Random.nextInt(10, 21),
                correctAnswer + Random.nextInt(10, 21)
            ).filter { it > 0 && it != correctAnswer }
        )
        
        // Strategy 3: One of the factors squared
        wrongAnswers.addAll(
            listOf(
                multiplier1 * multiplier1,
                multiplier2 * multiplier2
            ).filter { it != correctAnswer }
        )
        
        // Strategy 4: Product of (factor ± 1)
        wrongAnswers.addAll(
            listOf(
                (multiplier1 - 1) * multiplier2,
                (multiplier1 + 1) * multiplier2,
                multiplier1 * (multiplier2 - 1),
                multiplier1 * (multiplier2 + 1)
            ).filter { it > 0 && it != correctAnswer }
        )
        
        // Strategy 5: Random nearby multiples
        val nearbyMultiples = (1..12).map { it * multiplier1 }.filter { it != correctAnswer }
        wrongAnswers.addAll(nearbyMultiples.shuffled().take(2))
        
        // Strategy 6: Some random values in plausible range
        while (wrongAnswers.size < 20) {
            val randomWrong = Random.nextInt(
                maxOf(1, correctAnswer - 30),
                minOf(150, correctAnswer + 30)
            )
            if (randomWrong != correctAnswer) {
                wrongAnswers.add(randomWrong)
            }
        }
        
        // Take 8 unique wrong answers, ensuring they're all valid and different from correct
        return wrongAnswers
            .filter { it in 1..150 && it != correctAnswer }
            .distinct()
            .shuffled()
            .take(8)
    }
}

