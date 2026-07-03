//
//  GameViewModel.swift
//  ColorMixLab iOS
//
//  iOS-specific ViewModel - thin wrapper around shared GameController
//  Only handles platform-specific concerns (timer, haptics, color conversions)
//

import Foundation
import SwiftUI
import Combine
import shared

class GameViewModel: ObservableObject {
    @Published var gameState: GameState

    // Shared game controller contains ALL game logic
    private let gameController: GameController

    private var timerCancellable: AnyCancellable?
    private var stateObserver: Cancellable?
    private let hapticProvider = HapticProvider()

    init(difficulty: Difficulty) {
        // Create shared controller
        gameController = GameController(initialDifficulty: difficulty)
        gameState = gameController.gameState.value as! GameState

        // Kotlin StateFlow observation from Swift is awkward, so we poll the
        // current value on a 100ms cadence and republish when it changes.
        // Change detection uses the shared data class's generated equality.
        stateObserver = Timer.publish(every: 0.1, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                guard let self = self else { return }
                guard let newState = self.gameController.gameState.value as? GameState else { return }
                if !newState.isEqual(self.gameState) {
                    self.gameState = newState

                    // Handle timer lifecycle based on state changes
                    if newState.isTimerActive && self.timerCancellable == nil {
                        self.startTimer()
                    } else if !newState.isTimerActive && self.timerCancellable != nil {
                        self.stopTimer()
                    }
                }
            }
    }

    // MARK: - Actions (delegated to shared controller)

    func addColorDrop(color: GameColor) {
        gameController.addColorDrop(color: color)
        hapticProvider.performHaptic(type: HapticType.lightTap)
    }

    func clearBowl() {
        gameController.clearBowl()
        hapticProvider.performHaptic(type: HapticType.lightTap)
    }

    func checkMatch() {
        gameController.checkMatch()
        let isSuccess = gameState.similarity >= GameConstants.shared.MATCH_SUCCESS_THRESHOLD
        hapticProvider.performHaptic(type: isSuccess ? HapticType.success : HapticType.error)
    }

    func nextLevel() {
        gameController.nextLevel()
    }

    func retryLevel() {
        gameController.retryLevel()
    }

    func resetGame() {
        stopTimer()
        gameController.resetGame()
    }

    func completeMathChallenge() {
        gameController.completeMathChallenge()
    }

    func getCurrentMathChallenge() -> (MathChallengeType, GameColor?)? {
        guard let result = gameController.getCurrentMathChallenge() else { return nil }
        return (result.first as! MathChallengeType, result.second as? GameColor)
    }

    func deductPointsForWrongAnswer() {
        gameController.deductPointsForWrongMathAnswer()
    }

    func exitGame() {
        stopTimer()
        stateObserver?.cancel()
    }

    func resultMessage(for similarity: Float) -> String {
        gameController.getResultMessage(similarity: similarity)
    }

    func getResultEmoji(similarity: Float) -> String {
        gameController.getResultEmoji(similarity: similarity)
    }

    // MARK: - Timer management
    // Timer runs on main thread and calls shared controller's tickTimer()

    private func startTimer() {
        timerCancellable?.cancel()

        timerCancellable = Timer.publish(every: 1, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                self?.gameController.tickTimer()
            }
    }

    private func stopTimer() {
        timerCancellable?.cancel()
        timerCancellable = nil
    }

    // MARK: - UI color conversions

    var targetColor: Color {
        gameState.targetColor.swiftUIColor
    }

    var mixedColor: Color {
        gameState.mixedColor.swiftUIColor
    }
}
