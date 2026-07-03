//
//  GameScreen.swift
//  ColorMixLab iOS
//
//  Main game screen - matches Android layout exactly with proper spacing
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
    @State private var showCelebration = false

    init(difficulty: Difficulty) {
        self.difficulty = difficulty
        _viewModel = StateObject(wrappedValue: GameViewModel(difficulty: difficulty))
    }

    var body: some View {
        ZStack {
            Color(red: 0.95, green: 0.95, blue: 0.97)
                .ignoresSafeArea()

            VStack(spacing: 0) {
                // Top section: Menu, Level, Score, Target
                HStack(alignment: .center, spacing: 12) {
                    Button(action: { showMenu = true }) {
                        Image(systemName: "line.3.horizontal")
                            .font(.system(size: 24))
                            .foregroundColor(.blue)
                            .frame(width: 40, height: 40)
                    }

                    Text("Lvl \(viewModel.gameState.currentLevel)")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.blue)

                    Text("Score: \(viewModel.gameState.currentScore)")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.blue)
                        .animation(.spring(response: 0.5), value: viewModel.gameState.currentScore)

                    Spacer()

                    TargetColorView(targetColor: viewModel.targetColor)
                }
                .padding(.horizontal, 12)
                .padding(.top, 8)
                .frame(height: 60)

                // Timer
                TimerView(
                    timeRemaining: viewModel.gameState.timeRemainingSeconds,
                    difficulty: difficulty
                )
                .frame(height: 30)

                // Mixing Bowl (centered, takes available space)
                Spacer()

                MixingBowlView(
                    mixedColor: viewModel.mixedColor,
                    drops: viewModel.gameState.drops,
                    similarity: viewModel.gameState.similarity
                )
                .padding(.horizontal, 12)

                Spacer()

                LazyVGrid(
                    columns: [
                        GridItem(.flexible(), spacing: 8),
                        GridItem(.flexible(), spacing: 8),
                        GridItem(.flexible(), spacing: 8)
                    ],
                    spacing: 8
                ) {
                    ForEach(viewModel.gameState.unlockedColors, id: \.name) { gameColor in
                        ColorButton(
                            gameColor: gameColor,
                            onTap: {
                                viewModel.addColorDrop(color: gameColor)
                            }
                        )
                    }
                }
                .padding(.horizontal, 12)
                .frame(height: 180) // Fixed height for color grid

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
                .padding(.horizontal, 12)
                .padding(.top, 12)
                .padding(.bottom, 12)
                .frame(height: 70)
            }

            if showCelebration {
                GameCompletionCelebration(onAnimationComplete: {
                    showCelebration = false
                    showGameCompleted = true
                })
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
            if let challengeInfo = viewModel.getCurrentMathChallenge() {
                MathChallengeScreen(
                    difficulty: difficulty,
                    challengeInfo: challengeInfo,
                    onComplete: {
                        viewModel.completeMathChallenge()
                        showMathChallenge = false
                    },
                    onWrongAnswer: {
                        viewModel.deductPointsForWrongAnswer()
                    }
                )
            }
        }
        .sheet(isPresented: $showSuccessDialog) {
            ResultDialog(
                isSuccess: viewModel.gameState.isMatched,
                similarity: viewModel.gameState.similarity,
                basePoints: viewModel.gameState.lastBasePoints,
                timeBonus: viewModel.gameState.lastTimeBonus,
                message: viewModel.resultMessage(for: viewModel.gameState.similarity),
                emoji: viewModel.getResultEmoji(similarity: viewModel.gameState.similarity),
                onNext: {
                    viewModel.nextLevel()
                    showSuccessDialog = false

                    // Check if ALL levels were completed (show celebration)
                    if viewModel.gameState.completedAllLevels {
                        showCelebration = true
                    }
                    // Check for game over (any completion, show results screen)
                    else if viewModel.gameState.isGameCompleted {
                        showGameCompleted = true
                    }
                    // Then check for math challenge
                    else if viewModel.gameState.needsMathChallenge {
                        showMathChallenge = true
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
        .onDisappear {
            viewModel.exitGame()
        }
    }
}

// Simple timer view matching Android
struct TimerView: View {
    let timeRemaining: KotlinInt?
    let difficulty: Difficulty

    var body: some View {
        HStack(spacing: 6) {
            if difficulty == .easy {
                // Easy mode shows infinity (no timer)
                Text("∞")
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(.gray.opacity(0.5))
            } else if let time = timeRemaining {
                let seconds = time.intValue
                let isWarning = seconds <= 5 && seconds > 0

                Text("⏱")
                    .font(.system(size: 20))
                Text("\(seconds)s")
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(isWarning ? .red : .blue)
                    .opacity(isWarning && (seconds % 2 == 0) ? 0.3 : 1.0)
                    .animation(.easeInOut(duration: 0.5), value: seconds)
            }
        }
        .frame(maxWidth: .infinity)
    }
}

// Compact color button (matches Android)
struct ColorButton: View {
    let gameColor: GameColor
    let onTap: () -> Void

    @State private var isPressed = false

    private let hapticProvider = HapticProvider()

    private var color: Color {
        gameColor.color.swiftUIColor
    }

    var body: some View {
        Button(action: {
            hapticProvider.performHaptic(type: HapticType.lightTap)

            withAnimation(.spring(response: 0.3, dampingFraction: 0.6)) {
                isPressed = true
            }

            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                withAnimation(.spring(response: 0.3, dampingFraction: 0.6)) {
                    isPressed = false
                }
            }

            onTap()
        }) {
            VStack(spacing: 4) {
                // Color Circle
                Circle()
                    .fill(color)
                    .frame(width: 60, height: 60)
                    .overlay(
                        Circle()
                            .stroke(Color.primary, lineWidth: 3)
                    )
                    .scaleEffect(isPressed ? 1.15 : 1.0)

                // Color Name
                Text(gameColor.name)
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(.primary)
                    .lineLimit(1)
            }
        }
        .buttonStyle(PlainButtonStyle())
    }
}

#Preview {
    GameScreen(difficulty: .medium)
}
