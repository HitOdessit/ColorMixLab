//
//  GameViewModel.swift
//  ColorMixLab iOS
//
//  ViewModel for game logic, wrapping shared module
//

import SwiftUI
import shared
import Combine

class GameViewModel: ObservableObject {
    @Published var gameState: GameState_

    private var timerCancellable: AnyCancellable?
    private let hapticProvider = HapticProvider()

    init(difficulty: Difficulty) {
        // Initialize with shared GameState
        self.gameState = GameState_()

        // Initialize game colors with random seed
        let seed = Int64(Date().timeIntervalSince1970 * 1000)
        GameColor.companion.initializeGameColors(seed: seed)

        // Set difficulty and start
        self.gameState = gameState.doCopy(difficulty: difficulty)
        startNewLevel()
    }

    func setDifficulty(difficulty: Difficulty) {
        gameState = gameState.doCopy(difficulty: difficulty)
    }

    func addColorDrop(color: GameColor) {
        var currentDrops = gameState.drops
        let currentCount = currentDrops[color] as? Int32 ?? 0
        currentDrops.updateValue(currentCount + 1, forKey: color)

        // Use shared ColorMixer
        let mixedPlatformColor = ColorMixer.shared.mixColors(drops: currentDrops)
        let similarity = ColorMixer.shared.calculateSimilarity(
            target: gameState.targetColor,
            mixed: mixedPlatformColor
        )

        gameState = gameState.doCopy(
            drops: currentDrops,
            mixedColor: mixedPlatformColor,
            similarity: similarity
        )

        hapticProvider.performHaptic(type: .lightTap)
    }

    func clearBowl() {
        gameState = gameState.doCopy(
            drops: [:],
            mixedColor: PlatformColor.companion.White,
            similarity: 0.0
        )
        hapticProvider.performHaptic(type: .lightTap)
    }

    func checkMatch() {
        guard !gameState.hasCheckedThisRound else { return }

        let similarity = gameState.similarity
        let points = calculatePoints(similarity: similarity)
        let timeBonus = calculateTimeBonus(similarity: similarity)
        let totalPoints = points + timeBonus
        let newScore = max(0, gameState.currentScore + totalPoints)

        let isSuccess = similarity >= 0.80

        gameState = gameState.doCopy(
            isMatched: isSuccess,
            showSuccessDialog: true,
            hasCheckedThisRound: true,
            currentScore: newScore,
            lastBasePoints: points,
            lastTimeBonus: timeBonus
        )

        hapticProvider.performHaptic(type: isSuccess ? .success : .error)
    }

    func calculatePoints(similarity: Float) -> Int32 {
        var basePoints: Int32

        switch similarity {
        case 1.0...:
            basePoints = 150
        case 0.95..<1.0:
            basePoints = 100
        case 0.90..<0.95:
            basePoints = 80
        case 0.85..<0.90:
            basePoints = 60
        case 0.80..<0.85:
            basePoints = 40
        case 0.75..<0.80:
            basePoints = -10
        case 0.70..<0.75:
            basePoints = -15
        case 0.65..<0.70:
            basePoints = -20
        case 0.60..<0.65:
            basePoints = -25
        case 0.50..<0.60:
            basePoints = -35
        default:
            basePoints = -50
        }

        let multiplier: Float = switch gameState.difficulty {
        case .easy: 0.75
        case .medium: 1.0
        case .hard: 1.25
        default: 1.0
        }

        return Int32(Float(basePoints) * multiplier)
    }

    private func calculateTimeBonus(similarity: Float) -> Int32 {
        guard similarity >= 0.80 else { return 0 }
        guard let timeRemaining = gameState.timeRemainingSeconds else { return 0 }
        guard let totalDuration = GameState_.companion.getTimerDuration(difficulty: gameState.difficulty) else { return 0 }

        let timePercent = Float(timeRemaining) / Float(totalDuration)
        return Int32(50 * timePercent)
    }

    func getResultMessage(similarity: Float) -> String {
        switch similarity {
        case 1.0...: return "Perfect Match!"
        case 0.95..<1.0: return "Excellent Mix!"
        case 0.90..<0.95: return "Great Job!"
        case 0.85..<0.90: return "Very Good!"
        case 0.80..<0.85: return "Nice Work!"
        case 0.75..<0.80: return "Almost There!"
        case 0.70..<0.75: return "Close!"
        case 0.65..<0.70: return "Getting Closer!"
        case 0.60..<0.65: return "Keep Trying!"
        case 0.50..<0.60: return "Not Quite!"
        default: return "Try Harder!"
        }
    }

    func getResultEmoji(similarity: Float) -> String {
        switch similarity {
        case 1.0...: return "🎉"
        case 0.95..<1.0: return "⭐"
        case 0.90..<0.95: return "👍"
        case 0.85..<0.90: return "😊"
        case 0.80..<0.85: return "👌"
        case 0.75..<0.80: return "🎨"
        case 0.70..<0.75: return "💪"
        case 0.65..<0.70: return "🤔"
        case 0.60..<0.65: return "🔄"
        case 0.50..<0.60: return "😕"
        default: return "😢"
        }
    }

    func nextLevel() {
        let currentLevel = gameState.currentLevel

        if currentLevel >= GameState_.MAX_LEVEL {
            cancelTimer()
            gameState = gameState.doCopy(
                showSuccessDialog: false,
                isGameCompleted: true
            )
            return
        }

        let newLevel = currentLevel + 1

        // Check for math challenges
        let colorUnlockLevels: [Int32] = [4, 7, 10, 13, 16, 19]
        let needsColorUnlock = colorUnlockLevels.contains(newLevel)
        let needsMilestone = newLevel > 19 && (newLevel - 19) % 3 == 0

        let needsChallenge = needsColorUnlock || needsMilestone
        let challengeType: MathChallengeType = needsColorUnlock ? .colorUnlock : (needsMilestone ? .milestone : .none)

        // Generate new target
        let (targetColor, recipe) = LevelManager.shared.generateTargetColor(
            level: newLevel,
            previousTarget: gameState.targetColor
        )

        cancelTimer()

        gameState = gameState.doCopy(
            currentLevel: newLevel,
            unlockedColors: needsChallenge ? gameState.unlockedColors : GameColor.companion.getAvailableColors(level: newLevel),
            targetColor: targetColor,
            targetRecipe: recipe,
            mixedColor: PlatformColor.companion.White,
            drops: [:],
            isMatched: false,
            showSuccessDialog: false,
            hasCheckedThisRound: false,
            similarity: 0.0,
            needsMathChallenge: needsChallenge,
            mathChallengeType: challengeType,
            mathChallengeCompleted: false
        )

        if !needsChallenge {
            startTimer()
        }
    }

    func retryLevel() {
        let (targetColor, recipe) = LevelManager.shared.generateTargetColor(
            level: gameState.currentLevel,
            previousTarget: gameState.targetColor
        )

        cancelTimer()

        gameState = gameState.doCopy(
            targetColor: targetColor,
            targetRecipe: recipe,
            mixedColor: PlatformColor.companion.White,
            drops: [:],
            showSuccessDialog: false,
            hasCheckedThisRound: false,
            similarity: 0.0
        )

        startTimer()
    }

    func completeMathChallenge() {
        gameState = gameState.doCopy(
            needsMathChallenge: false,
            mathChallengeCompleted: true,
            unlockedColors: GameColor.companion.getAvailableColors(level: gameState.currentLevel)
        )
        startTimer()
    }

    func getCurrentMathChallenge() -> (MathChallengeType, GameColor?)? {
        guard gameState.needsMathChallenge else { return nil }

        let challengeType = gameState.mathChallengeType
        let currentUnlocked = gameState.unlockedColors
        let nextLevel Colors = GameColor.companion.getAvailableColors(level: gameState.currentLevel)

        let nextColor = nextLevelColors.first { color in
            !currentUnlocked.contains(where: { $0.name == color.name })
        }

        return (challengeType, nextColor)
    }

    func deductPointsForWrongAnswer() {
        let newScore = max(0, gameState.currentScore - 75)
        gameState = gameState.doCopy(currentScore: newScore)
    }

    func resetGame() {
        cancelTimer()
        GameColor.companion.resetColors()
        let seed = Int64(Date().timeIntervalSince1970 * 1000)
        GameColor.companion.initializeGameColors(seed: seed)

        gameState = GameState_(difficulty: gameState.difficulty)
        startNewLevel()
    }

    func exitGame() {
        cancelTimer()
    }

    // Timer functions
    func startTimer() {
        guard let duration = GameState_.companion.getTimerDuration(difficulty: gameState.difficulty) else { return }

        cancelTimer()

        gameState = gameState.doCopy(
            timeRemainingSeconds: duration,
            isTimerActive: true,
            isTimerPaused: false
        )

        timerCancellable = Timer.publish(every: 1, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                self?.tickTimer()
            }
    }

    private func tickTimer() {
        guard !gameState.isTimerPaused,
              let currentTime = gameState.timeRemainingSeconds,
              currentTime > 0 else { return }

        let newTime = currentTime - 1
        gameState = gameState.doCopy(timeRemainingSeconds: newTime)

        if newTime == 0 {
            onTimerExpired()
        }
    }

    func cancelTimer() {
        timerCancellable?.cancel()
        gameState = gameState.doCopy(
            isTimerActive: false,
            timeRemainingSeconds: nil,
            isTimerPaused: false
        )
    }

    private func onTimerExpired() {
        let similarity = gameState.similarity
        let points = calculatePoints(similarity: similarity)
        let timeBonus = calculateTimeBonus(similarity: similarity)
        let totalPoints = points + timeBonus
        let newScore = max(0, gameState.currentScore + totalPoints)

        let isSuccess = similarity >= 0.80

        gameState = gameState.doCopy(
            isMatched: isSuccess,
            showSuccessDialog: true,
            hasCheckedThisRound: true,
            currentScore: newScore,
            isTimerActive: false,
            lastBasePoints: points,
            lastTimeBonus: timeBonus
        )
    }

    private func startNewLevel() {
        let previousTarget = gameState.currentLevel > 1 ? gameState.targetColor : nil
        let (targetColor, recipe) = LevelManager.shared.generateTargetColor(
            level: gameState.currentLevel,
            previousTarget: previousTarget
        )

        gameState = gameState.doCopy(
            targetColor: targetColor,
            targetRecipe: recipe,
            mixedColor: PlatformColor.companion.White,
            drops: [:],
            isMatched: false,
            showSuccessDialog: false,
            similarity: 0.0
        )

        startTimer()
    }

    // UI Color conversions
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

// Helper extension for GameState copy
extension GameState_ {
    func doCopy(
        currentLevel: Int32? = nil,
        targetColor: PlatformColor? = nil,
        targetRecipe: [GameColor: KotlinInt]? = nil,
        mixedColor: PlatformColor? = nil,
        drops: [GameColor: KotlinInt]? = nil,
        unlockedColors: [GameColor]? = nil,
        isMatched: Bool? = nil,
        showSuccessDialog: Bool? = nil,
        similarity: Float? = nil,
        currentScore: Int32? = nil,
        hasCheckedThisRound: Bool? = nil,
        isGameCompleted: Bool? = nil,
        difficulty: Difficulty? = nil,
        timeRemainingSeconds: KotlinInt? = nil,
        isTimerActive: Bool? = nil,
        isTimerPaused: Bool? = nil,
        lastBasePoints: Int32? = nil,
        lastTimeBonus: Int32? = nil,
        needsMathChallenge: Bool? = nil,
        mathChallengeType: MathChallengeType? = nil,
        mathChallengeCompleted: Bool? = nil
    ) -> GameState_ {
        return GameState_(
            currentLevel: currentLevel ?? self.currentLevel,
            targetColor: targetColor ?? self.targetColor,
            targetRecipe: targetRecipe ?? self.targetRecipe,
            mixedColor: mixedColor ?? self.mixedColor,
            drops: drops ?? self.drops,
            unlockedColors: unlockedColors ?? self.unlockedColors,
            isMatched: isMatched ?? self.isMatched,
            showSuccessDialog: showSuccessDialog ?? self.showSuccessDialog,
            similarity: similarity ?? self.similarity,
            currentScore: currentScore ?? self.currentScore,
            hasCheckedThisRound: hasCheckedThisRound ?? self.hasCheckedThisRound,
            isGameCompleted: isGameCompleted ?? self.isGameCompleted,
            difficulty: difficulty ?? self.difficulty,
            timeRemainingSeconds: timeRemainingSeconds ?? self.timeRemainingSeconds,
            isTimerActive: isTimerActive ?? self.isTimerActive,
            isTimerPaused: isTimerPaused ?? self.isTimerPaused,
            lastBasePoints: lastBasePoints ?? self.lastBasePoints,
            lastTimeBonus: lastTimeBonus ?? self.lastTimeBonus,
            needsMathChallenge: needsMathChallenge ?? self.needsMathChallenge,
            mathChallengeType: mathChallengeType ?? self.mathChallengeType,
            mathChallengeCompleted: mathChallengeCompleted ?? self.mathChallengeCompleted
        )
    }
}
