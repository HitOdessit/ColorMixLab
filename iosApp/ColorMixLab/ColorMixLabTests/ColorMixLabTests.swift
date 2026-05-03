//
//  ColorMixLabTests.swift
//  ColorMixLab iOS — smoke tests
//
//  These verify the Kotlin shared framework bridge is wired correctly and
//  that GameViewModel initializes consistently across difficulty modes.
//
//  Wiring this file into the Xcode project requires a one-time test target
//  setup — see README.md in this directory for steps.
//

import XCTest
@testable import ColorMixLab
import shared

final class GameViewModelTests: XCTestCase {

    func testEasyDifficultyInitializesWithoutTimer() throws {
        let viewModel = GameViewModel(difficulty: Difficulty.easy)

        XCTAssertEqual(viewModel.gameState.currentLevel, 1, "New game should start at level 1")
        XCTAssertEqual(viewModel.gameState.currentScore, 0, "New game should start at score 0")
        XCTAssertFalse(viewModel.gameState.isGameCompleted, "New game must not be marked completed")
        XCTAssertNil(
            viewModel.gameState.timeRemainingSeconds,
            "Easy mode has no per-level timer; timeRemainingSeconds must be nil"
        )
    }

    func testMediumDifficultyInitializesWithTimer() throws {
        let viewModel = GameViewModel(difficulty: Difficulty.medium)

        XCTAssertEqual(viewModel.gameState.currentLevel, 1)
        XCTAssertNotNil(
            viewModel.gameState.timeRemainingSeconds,
            "Medium mode must have a per-level timer initialized"
        )
    }

    func testHardDifficultyInitializesWithTimer() throws {
        let viewModel = GameViewModel(difficulty: Difficulty.hard)

        XCTAssertEqual(viewModel.gameState.currentLevel, 1)
        XCTAssertNotNil(
            viewModel.gameState.timeRemainingSeconds,
            "Hard mode must have a per-level timer initialized"
        )
    }
}
