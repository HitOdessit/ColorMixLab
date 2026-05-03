//
//  GameStateExtensions.swift
//  ColorMixLab
//
//  Extension to make GameState copying easier in Swift
//

import Foundation
import shared

extension GameState {
    func copy(
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
    ) -> GameState {
        return self.doCopy(
            currentLevel: currentLevel ?? self.currentLevel,
            targetColor: targetColor ?? self.targetColor,
            targetRecipe: targetRecipe ?? (self.targetRecipe as! [GameColor: KotlinInt]),
            mixedColor: mixedColor ?? self.mixedColor,
            drops: drops ?? (self.drops as! [GameColor: KotlinInt]),
            unlockedColors: unlockedColors ?? (self.unlockedColors as! [GameColor]),
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
