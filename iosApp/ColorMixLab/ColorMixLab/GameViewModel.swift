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

        // Observe shared game state using simple polling
        // This is a workaround since Kotlin StateFlow observation from Swift is complex
        stateObserver = Timer.publish(every: 0.1, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                guard let self = self else { return }
                guard let newState = self.gameController.gameState.value as? GameState else { return }
                if self.gameState != newState {
                    self.gameState = newState

                    // Handle timer lifecycle based on state changes
                    if newState.isTimerActive && self.timerCancellable == nil {
                        self.startTimerCoroutine()
                    } else if !newState.isTimerActive && self.timerCancellable != nil {
                        self.cancelTimerCoroutine()
                    }
                }
            }
    }

    // ========== DELEGATE ALL ACTIONS TO SHARED CONTROLLER ==========

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
        let isSuccess = gameState.similarity >= 0.80
        hapticProvider.performHaptic(type: isSuccess ? HapticType.success : HapticType.error)
    }

    func nextLevel() {
        gameController.nextLevel()
    }

    func retryLevel() {
        gameController.retryLevel()
    }

    func resetGame() {
        cancelTimerCoroutine()
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
        cancelTimerCoroutine()
        stateObserver?.cancel()
    }

    func getResultMessage(similarity: Float) -> String {
        gameController.getResultMessage(similarity: similarity)
    }

    func getResultEmoji(similarity: Float) -> String {
        gameController.getResultEmoji(similarity: similarity)
    }

    // ========== IOS-SPECIFIC TIMER MANAGEMENT ==========
    // Timer runs on main thread and calls shared controller's tickTimer()

    private func startTimerCoroutine() {
        timerCancellable?.cancel()

        timerCancellable = Timer.publish(every: 1, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                self?.gameController.tickTimer()
            }
    }

    private func cancelTimerCoroutine() {
        timerCancellable?.cancel()
        timerCancellable = nil
    }

    // ========== UI COLOR CONVERSIONS ==========

    func getTargetUIColor() -> Color {
        platformColorToUIColor(gameState.targetColor)
    }

    func getMixedUIColor() -> Color {
        platformColorToUIColor(gameState.mixedColor)
    }

    private func platformColorToUIColor(_ platformColor: PlatformColor) -> Color {
        Color(
            red: Double(platformColor.redFloat),
            green: Double(platformColor.greenFloat),
            blue: Double(platformColor.blueFloat),
            opacity: Double(platformColor.alphaFloat)
        )
    }

    func gameColorToUIColor(_ gameColor: GameColor) -> Color {
        platformColorToUIColor(gameColor.color)
    }
}

// Make GameState Equatable for change detection
extension GameState {
    static func != (lhs: GameState, rhs: GameState) -> Bool {
        return lhs.currentLevel != rhs.currentLevel ||
               lhs.currentScore != rhs.currentScore ||
               lhs.similarity != rhs.similarity ||
               lhs.isMatched != rhs.isMatched ||
               lhs.showSuccessDialog != rhs.showSuccessDialog ||
               lhs.hasCheckedThisRound != rhs.hasCheckedThisRound ||
               lhs.isGameCompleted != rhs.isGameCompleted ||
               lhs.completedAllLevels != rhs.completedAllLevels ||
               lhs.timeRemainingSeconds?.intValue != rhs.timeRemainingSeconds?.intValue ||
               lhs.isTimerActive != rhs.isTimerActive ||
               lhs.isTimerPaused != rhs.isTimerPaused ||
               lhs.needsMathChallenge != rhs.needsMathChallenge ||
               lhs.mathChallengeCompleted != rhs.mathChallengeCompleted ||
               lhs.drops.count != rhs.drops.count
    }
}
