package com.colormixlab.game

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colormixlab.model.GameColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Android-specific ViewModel - thin wrapper around shared GameController
 * Only handles platform-specific concerns (timer coroutines, Compose State conversion)
 */
class GameViewModel : ViewModel() {
    // Shared game controller contains ALL game logic
    private val gameController = GameController()

    // Convert StateFlow to Compose State for UI observation
    private val _gameState = mutableStateOf(gameController.gameState.value)
    val gameState: State<GameState> = _gameState

    private var timerJob: Job? = null

    init {
        // Observe shared game state and convert to Compose State
        gameController.gameState.onEach { newState ->
            _gameState.value = newState

            // Handle timer lifecycle based on state changes
            if (newState.isTimerActive && timerJob == null) {
                startTimerCoroutine()
            } else if (!newState.isTimerActive && timerJob != null) {
                cancelTimerCoroutine()
            }
        }.launchIn(viewModelScope)
    }

    // ========== DELEGATE ALL ACTIONS TO SHARED CONTROLLER ==========

    fun setDifficulty(difficulty: Difficulty) {
        gameController.setDifficulty(difficulty)
    }

    fun addColorDrop(color: GameColor) {
        gameController.addColorDrop(color)
    }

    fun clearBowl() {
        gameController.clearBowl()
    }

    fun checkMatch() {
        gameController.checkMatch()
    }

    fun calculatePoints(similarity: Float): Int {
        return gameController.calculatePoints(similarity)
    }

    fun getResultMessage(similarity: Float): String {
        return gameController.getResultMessage(similarity)
    }

    fun getResultEmoji(similarity: Float): String {
        return gameController.getResultEmoji(similarity)
    }

    fun nextLevel() {
        gameController.nextLevel()
    }

    fun retryLevel() {
        gameController.retryLevel()
    }

    fun dismissSuccessDialog() {
        _gameState.value = _gameState.value.copy(showSuccessDialog = false)
    }

    fun completeMathChallenge() {
        gameController.completeMathChallenge()
    }

    fun getCurrentMathChallenge(): Pair<MathChallengeType, GameColor?>? {
        return gameController.getCurrentMathChallenge()
    }

    fun deductPointsForWrongMathAnswer() {
        gameController.deductPointsForWrongMathAnswer()
    }

    fun exitGame() {
        cancelTimerCoroutine()
    }

    fun resetGame() {
        cancelTimerCoroutine()
        gameController.resetGame()
    }

    fun completeGame() {
        _gameState.value = _gameState.value.copy(isGameCompleted = false)
    }

    fun forceFinishGame() {
        cancelTimerCoroutine()
        gameController.forceFinishGame()
    }

    // ========== ANDROID-SPECIFIC TIMER MANAGEMENT ==========
    // Timer runs in viewModelScope coroutine and calls shared controller's tickTimer()

    private fun startTimerCoroutine() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                gameController.tickTimer()

                // Check if timer is still active
                if (!gameController.gameState.value.isTimerActive) {
                    break
                }
            }
        }
    }

    fun pauseTimer() {
        gameController.pauseTimer()
    }

    fun resumeTimer() {
        gameController.resumeTimer()
    }

    private fun cancelTimerCoroutine() {
        timerJob?.cancel()
        timerJob = null
    }
}
