//
//  GameScreen.swift
//  ColorMixLab iOS
//
//  Main game screen with color mixing gameplay
//

import SwiftUI
import shared

struct GameScreen: View {
    let difficulty: Difficulty
    @Environment(\.dismiss) var dismiss
    @StateObject private var viewModel: GameViewModel

    @State private var showMenu = false
    @State private var showMathChallenge = false
    @State private var showSuccessDialog = false
    @State private var showGameCompleted = false

    init(difficulty: Difficulty) {
        self.difficulty = difficulty
        _viewModel = StateObject(wrappedValue: GameViewModel(difficulty: difficulty))
    }

    var body: some View {
        ZStack {
            // Background
            Color(red: 0.95, green: 0.95, blue: 0.97)
                .ignoresSafeArea()

            VStack(spacing: 16) {
                // Header
                GameHeader(
                    level: viewModel.gameState.currentLevel,
                    score: viewModel.gameState.currentScore,
                    timeRemaining: viewModel.gameState.timeRemainingSeconds,
                    onMenuTap: { showMenu = true }
                )

                ScrollView {
                    VStack(spacing: 24) {
                        // Target Color Display
                        TargetColorView(
                            targetColor: viewModel.getTargetUIColor(),
                            targetRecipe: viewModel.gameState.targetRecipe
                        )

                        // Mixing Bowl
                        MixingBowlView(
                            mixedColor: viewModel.getMixedUIColor(),
                            drops: viewModel.gameState.drops,
                            similarity: viewModel.gameState.similarity
                        )

                        // Color Palette Grid
                        ColorPaletteGrid(
                            unlockedColors: viewModel.gameState.unlockedColors,
                            onColorTap: { color in
                                viewModel.addColorDrop(color: color)
                            }
                        )

                        // Action Buttons
                        ActionButtons(
                            hasDrops: !viewModel.gameState.drops.isEmpty,
                            hasChecked: viewModel.gameState.hasCheckedThisRound,
                            onCheck: {
                                viewModel.checkMatch()
                                showSuccessDialog = true
                            },
                            onClear: {
                                viewModel.clearBowl()
                            }
                        )
                    }
                    .padding(.horizontal, 16)
                    .padding(.bottom, 20)
                }
            }
        }
        .navigationBarHidden(true)
        .sheet(isPresented: $showMenu) {
            MenuDialog(
                onResume: { showMenu = false },
                onRestart: {
                    viewModel.resetGame()
                    showMenu = false
                },
                onQuit: {
                    dismiss()
                }
            )
        }
        .sheet(isPresented: $showMathChallenge) {
            MathChallengeScreen(
                difficulty: difficulty,
                challengeInfo: viewModel.getCurrentMathChallenge(),
                onComplete: {
                    viewModel.completeMathChallenge()
                    showMathChallenge = false
                },
                onWrongAnswer: {
                    viewModel.deductPointsForWrongAnswer()
                }
            )
        }
        .sheet(isPresented: $showSuccessDialog) {
            ResultDialog(
                isSuccess: viewModel.gameState.isMatched,
                similarity: viewModel.gameState.similarity,
                basePoints: viewModel.gameState.lastBasePoints,
                timeBonus: viewModel.gameState.lastTimeBonus,
                message: viewModel.getResultMessage(similarity: viewModel.gameState.similarity),
                emoji: viewModel.getResultEmoji(similarity: viewModel.gameState.similarity),
                onNext: {
                    viewModel.nextLevel()
                    showSuccessDialog = false
                    if viewModel.gameState.needsMathChallenge {
                        showMathChallenge = true
                    }
                    if viewModel.gameState.isGameCompleted {
                        showGameCompleted = true
                    }
                },
                onRetry: {
                    viewModel.retryLevel()
                    showSuccessDialog = false
                }
            )
        }
        .fullScreenCover(isPresented: $showGameCompleted) {
            GameCompletedView(
                finalScore: viewModel.gameState.currentScore,
                difficulty: difficulty,
                onPlayAgain: {
                    viewModel.resetGame()
                    showGameCompleted = false
                },
                onQuit: {
                    dismiss()
                }
            )
        }
        .onAppear {
            viewModel.startTimer()
        }
        .onDisappear {
            viewModel.exitGame()
        }
    }
}

#Preview {
    GameScreen(difficulty: .medium)
}
