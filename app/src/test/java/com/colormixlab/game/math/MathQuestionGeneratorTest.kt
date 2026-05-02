package com.colormixlab.game.math

import com.colormixlab.game.Difficulty
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for MathQuestionGenerator.
 * Tests question generation logic for different difficulties and levels.
 */
class MathQuestionGeneratorTest {

    @Test
    fun `generateQuestion returns correct answer`() {
        val question = MathQuestionGenerator.generateQuestion(Difficulty.MEDIUM, 1)

        val expectedAnswer = question.multiplier1 * question.multiplier2
        assertEquals("Correct answer should equal multiplication result", expectedAnswer, question.correctAnswer)
    }

    @Test
    fun `generateQuestion returns 9 total options`() {
        val question = MathQuestionGenerator.generateQuestion(Difficulty.MEDIUM, 1)

        assertEquals("Should have 9 total options (8 wrong + 1 correct)", 9, question.allOptions.size)
    }

    @Test
    fun `generateQuestion includes correct answer in options`() {
        val question = MathQuestionGenerator.generateQuestion(Difficulty.MEDIUM, 1)

        assertTrue(
            "Options should include correct answer",
            question.allOptions.contains(question.correctAnswer)
        )
    }

    @Test
    fun `generateQuestion has all unique options`() {
        val question = MathQuestionGenerator.generateQuestion(Difficulty.MEDIUM, 1)

        val uniqueOptions = question.allOptions.distinct()
        assertEquals(
            "All options should be unique",
            question.allOptions.size,
            uniqueOptions.size
        )
    }

    @Test
    fun `generateQuestion for EASY uses simple times tables`() {
        // Generate multiple questions to check distribution
        val questions = List(20) { MathQuestionGenerator.generateQuestion(Difficulty.EASY, 1) }

        questions.forEach { question ->
            assertTrue(
                "EASY should use 2, 5, or 10 times tables",
                question.multiplier1 in listOf(2, 5, 10)
            )
        }
    }

    @Test
    fun `generateQuestion for MEDIUM uses moderate times tables`() {
        val questions = List(20) { MathQuestionGenerator.generateQuestion(Difficulty.MEDIUM, 1) }

        questions.forEach { question ->
            assertTrue(
                "MEDIUM should use 3, 4, 9, or 11 times tables",
                question.multiplier1 in listOf(3, 4, 9, 11)
            )
        }
    }

    @Test
    fun `generateQuestion for HARD uses challenging times tables`() {
        val questions = List(20) { MathQuestionGenerator.generateQuestion(Difficulty.HARD, 1) }

        questions.forEach { question ->
            assertTrue(
                "HARD should use 6 or 8 times tables",
                question.multiplier1 in listOf(6, 8)
            )
        }
    }

    @Test
    fun `generateQuestion multiplier2 is between 2 and 12`() {
        val questions = List(30) {
            MathQuestionGenerator.generateQuestion(Difficulty.entries.random(), 1)
        }

        questions.forEach { question ->
            assertTrue(
                "Multiplier2 should be >= 2",
                question.multiplier2 >= 2
            )
            assertTrue(
                "Multiplier2 should be <= 12",
                question.multiplier2 <= 12
            )
        }
    }

    @Test
    fun `generateQuestion wrong answers are positive`() {
        val question = MathQuestionGenerator.generateQuestion(Difficulty.MEDIUM, 1)

        question.allOptions.forEach { option ->
            assertTrue("All options should be positive", option > 0)
        }
    }

    @Test
    fun `generateQuestion wrong answers are plausible`() {
        val question = MathQuestionGenerator.generateQuestion(Difficulty.MEDIUM, 1)

        val wrongAnswers = question.allOptions.filter { it != question.correctAnswer }

        wrongAnswers.forEach { wrong ->
            // Wrong answers should be reasonably close or use common mistake patterns
            // They should be within a reasonable range (not 1 and not 1000 for 6*7)
            assertTrue(
                "Wrong answer should be in plausible range",
                wrong in 1..150
            )
        }
    }

    @Test
    fun `generateQuestion has variety of wrong answers`() {
        val question = MathQuestionGenerator.generateQuestion(Difficulty.MEDIUM, 1)

        val wrongAnswers = question.allOptions.filter { it != question.correctAnswer }.sorted()

        // Check that wrong answers span a range (not all clustered)
        val range = wrongAnswers.last() - wrongAnswers.first()
        assertTrue(
            "Wrong answers should have variety (span > 5)",
            range > 5
        )
    }

    @Test
    fun `generateQuestion options are shuffled`() {
        val questions = List(10) { MathQuestionGenerator.generateQuestion(Difficulty.MEDIUM, 5) }

        // Check that correct answer is not always at the same position
        val correctPositions = questions.map { question ->
            question.allOptions.indexOf(question.correctAnswer)
        }.toSet()

        assertTrue(
            "Correct answer should appear at different positions",
            correctPositions.size > 1
        )
    }

    @Test
    fun `generateQuestion for high level adds harder tables`() {
        // Level > 20 should include 7 and 12 in addition to base tables
        val questions = List(30) { MathQuestionGenerator.generateQuestion(Difficulty.EASY, 25) }

        val timesTablesUsed = questions.map { it.multiplier1 }.toSet()

        // Should include base tables (2, 5, 10) and potentially 7, 12
        assertTrue(
            "High level should use varied times tables",
            timesTablesUsed.size >= 2
        )
    }

    @Test
    fun `generateQuestion creates different questions each time`() {
        val questions = List(20) { MathQuestionGenerator.generateQuestion(Difficulty.MEDIUM, 5) }

        // Convert questions to string representation for comparison
        val uniqueQuestions = questions.map {
            "${it.multiplier1}*${it.multiplier2}=${it.correctAnswer}"
        }.toSet()

        // Should have variety (not all identical)
        assertTrue(
            "Should generate different questions",
            uniqueQuestions.size > 1
        )
    }

    @Test
    fun `MathQuestion data class stores values correctly`() {
        val question = MathQuestion(
            multiplier1 = 6,
            multiplier2 = 7,
            correctAnswer = 42,
            allOptions = listOf(40, 41, 42, 43, 44, 45, 46, 47, 48)
        )

        assertEquals(6, question.multiplier1)
        assertEquals(7, question.multiplier2)
        assertEquals(42, question.correctAnswer)
        assertEquals(9, question.allOptions.size)
        assertTrue(question.allOptions.contains(42))
    }

    @Test
    fun `generateQuestion handles all difficulty levels`() {
        val easyQuestion = MathQuestionGenerator.generateQuestion(Difficulty.EASY, 5)
        val mediumQuestion = MathQuestionGenerator.generateQuestion(Difficulty.MEDIUM, 10)
        val hardQuestion = MathQuestionGenerator.generateQuestion(Difficulty.HARD, 15)

        // All should generate valid questions
        assertNotNull(easyQuestion)
        assertNotNull(mediumQuestion)
        assertNotNull(hardQuestion)

        // All should have correct structure
        assertEquals(9, easyQuestion.allOptions.size)
        assertEquals(9, mediumQuestion.allOptions.size)
        assertEquals(9, hardQuestion.allOptions.size)
    }

    @Test
    fun `generateQuestion wrong answers do not include correct answer`() {
        val question = MathQuestionGenerator.generateQuestion(Difficulty.MEDIUM, 1)

        val wrongAnswers = question.allOptions.filter { it != question.correctAnswer }

        assertEquals("Should have exactly 8 wrong answers", 8, wrongAnswers.size)
        assertFalse(
            "Wrong answers should not include correct answer",
            wrongAnswers.contains(question.correctAnswer)
        )
    }
}
