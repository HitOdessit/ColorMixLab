//
//  MathChallengeScreen.swift
//  ColorMixLab iOS
//
//  Math challenge screen with multiplication questions
//

import SwiftUI
import Combine
import shared

struct MathChallengeScreen: View {
    let difficulty: Difficulty
    let challengeInfo: (MathChallengeType, GameColor?)?
    let onComplete: () -> Void
    let onWrongAnswer: () -> Void

    @StateObject private var viewModel: MathChallengeViewModel
    @Environment(\.dismiss) var dismiss

    init(difficulty: Difficulty, challengeInfo: (MathChallengeType, GameColor?)?, onComplete: @escaping () -> Void, onWrongAnswer: @escaping () -> Void) {
        self.difficulty = difficulty
        self.challengeInfo = challengeInfo
        self.onComplete = onComplete
        self.onWrongAnswer = onWrongAnswer
        _viewModel = StateObject(wrappedValue: MathChallengeViewModel(difficulty: difficulty))
    }

    var body: some View {
        ZStack {
            // Background
            LinearGradient(
                colors: [Color(red: 0.1, green: 0.2, blue: 0.4), Color(red: 0.2, green: 0.3, blue: 0.5)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .ignoresSafeArea()

            VStack(spacing: 24) {
                // Header
                MathChallengeHeader(
                    questionsAnswered: viewModel.questionsAnswered,
                    consecutiveCorrect: viewModel.consecutiveCorrect,
                    timeRemaining: viewModel.timeRemaining,
                    unlockingColor: challengeInfo?.1
                )

                Spacer()

                if let question = viewModel.currentQuestion {
                    // Question Display
                    VStack(spacing: 32) {
                        // Question - 1.5x larger font for readability (reduced from 3x)
                        HStack(spacing: 16) {
                            Text("\(question.multiplier1)")
                                .font(.system(size: 96, weight: .bold))
                                .foregroundColor(.white)

                            Text("×")
                                .font(.system(size: 72, weight: .light))
                                .foregroundColor(.white.opacity(0.7))

                            Text("\(question.multiplier2)")
                                .font(.system(size: 96, weight: .bold))
                                .foregroundColor(.white)

                            Text("=")
                                .font(.system(size: 72, weight: .light))
                                .foregroundColor(.white.opacity(0.7))

                            Text("?")
                                .font(.system(size: 96, weight: .bold))
                                .foregroundColor(.yellow)
                        }
                        .padding(.horizontal, 20)
                        .padding(.vertical, 32)
                        .background(Color.white.opacity(0.1))
                        .cornerRadius(20)

                        // Answer Grid
                        AnswerGrid(
                            options: question.allOptions,
                            selectedAnswer: viewModel.selectedAnswer,
                            correctAnswer: viewModel.showingAnswer ? Int(question.correctAnswer) : nil,
                            onSelectAnswer: { answer in
                                viewModel.selectAnswer(
                                    answer: answer,
                                    onCorrect: {
                                        if viewModel.consecutiveCorrect >= 3 {
                                            DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                                                dismiss()
                                                onComplete()
                                            }
                                        }
                                    },
                                    onWrong: {
                                        onWrongAnswer()
                                    }
                                )
                            }
                        )
                        .disabled(viewModel.showingAnswer)
                    }
                }

                Spacer()
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 24)
        }
        .navigationBarHidden(true)
        .onAppear {
            viewModel.startChallenge()
        }
    }
}

struct MathChallengeHeader: View {
    let questionsAnswered: Int
    let consecutiveCorrect: Int
    let timeRemaining: Int?
    let unlockingColor: GameColor?

    var body: some View {
        VStack(spacing: 16) {
            // Title
            if let color = unlockingColor {
                VStack(spacing: 8) {
                    Text("🎯 Math Challenge")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(.white)

                    Text("Unlocking: \(color.name)")
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundColor(.yellow)
                }
            } else {
                Text("🎯 Milestone Challenge")
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(.white)
            }

            // Progress and Timer
            HStack(spacing: 20) {
                // Progress
                HStack(spacing: 8) {
                    ForEach(0..<3, id: \.self) { index in
                        Circle()
                            .fill(index < consecutiveCorrect ? Color.green : Color.white.opacity(0.3))
                            .frame(width: 16, height: 16)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
                .background(Color.white.opacity(0.2))
                .cornerRadius(12)

                // Timer
                if let time = timeRemaining {
                    HStack(spacing: 6) {
                        Image(systemName: "timer")
                            .font(.system(size: 16))
                        Text("\(time)s")
                            .font(.system(size: 18, weight: .bold))
                    }
                    .foregroundColor(time <= 5 ? .red : .white)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 10)
                    .background(Color.white.opacity(0.2))
                    .cornerRadius(12)
                }
            }
        }
    }
}

struct AnswerGrid: View {
    let options: [KotlinInt]
    let selectedAnswer: Int?
    let correctAnswer: Int?
    let onSelectAnswer: (Int) -> Void

    let columns = [
        GridItem(.flexible()),
        GridItem(.flexible()),
        GridItem(.flexible())
    ]

    var body: some View {
        LazyVGrid(columns: columns, spacing: 16) {
            ForEach(options, id: \.self) { option in
                let value = option.intValue
                AnswerButton(
                    value: value,
                    isSelected: selectedAnswer == value,
                    isCorrect: correctAnswer == value,
                    isWrong: selectedAnswer == value && correctAnswer != nil && correctAnswer != value,
                    onTap: { onSelectAnswer(value) }
                )
            }
        }
        .padding(.horizontal, 16)
    }
}

struct AnswerButton: View {
    let value: Int
    let isSelected: Bool
    let isCorrect: Bool
    let isWrong: Bool
    let onTap: () -> Void

    var backgroundColor: Color {
        if isCorrect {
            return .green
        } else if isWrong {
            return .red
        } else if isSelected {
            return .blue
        } else {
            return .white.opacity(0.2)
        }
    }

    var body: some View {
        Button(action: onTap) {
            Text("\(value)")
                .font(.system(size: 84, weight: .bold))  // 3x larger: 28 → 84
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 80)
                .background(backgroundColor)
                .cornerRadius(16)
                .overlay(
                    RoundedRectangle(cornerRadius: 16)
                        .stroke(isSelected ? Color.white : Color.clear, lineWidth: 3)
                )
        }
        .shadow(color: .black.opacity(0.2), radius: 8, x: 0, y: 4)
        .scaleEffect(isSelected ? 1.05 : 1.0)
        .animation(.spring(response: 0.3), value: isSelected)
    }
}

class MathChallengeViewModel: ObservableObject {
    @Published var currentQuestion: MathQuestion?
    @Published var selectedAnswer: Int?
    @Published var showingAnswer = false
    @Published var questionsAnswered = 0
    @Published var consecutiveCorrect = 0
    @Published var timeRemaining: Int?

    private let difficulty: Difficulty
    private var timer: Timer?
    private let hapticProvider = HapticProvider()

    init(difficulty: Difficulty) {
        self.difficulty = difficulty
    }

    func startChallenge() {
        generateNewQuestion()
        startTimer()
    }

    private func generateNewQuestion() {
        currentQuestion = MathQuestionGenerator.shared.generateQuestion(
            difficulty: difficulty,
            level: 1 // Can be adjusted based on game level
        )
        selectedAnswer = nil
        showingAnswer = false
    }

    func selectAnswer(answer: Int, onCorrect: @escaping () -> Void, onWrong: @escaping () -> Void) {
        guard let question = currentQuestion, !showingAnswer else { return }

        selectedAnswer = answer
        showingAnswer = true

        let isCorrect = answer == question.correctAnswer

        if isCorrect {
            consecutiveCorrect += 1
            questionsAnswered += 1

            hapticProvider.performHaptic(type: HapticType.success)

            if consecutiveCorrect >= 3 {
                // Challenge completed!
                timer?.invalidate()
                onCorrect()
            } else {
                // Next question after delay
                DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                    self.generateNewQuestion()
                }
            }
        } else {
            consecutiveCorrect = 0
            questionsAnswered += 1

            hapticProvider.performHaptic(type: HapticType.error)

            onWrong()

            // Next question after delay
            DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                self.generateNewQuestion()
            }
        }
    }

    private func startTimer() {
        guard let duration = MathChallengeTimer.shared.getTimerDuration(difficulty: difficulty) else { return }

        timeRemaining = duration.intValue
        timer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { [weak self] _ in
            guard let self = self else { return }
            if let current = self.timeRemaining, current > 0 {
                self.timeRemaining = current - 1
            } else {
                self.timer?.invalidate()
                self.handleTimeout()
            }
        }
    }

    private func handleTimeout() {
        // Auto-fail on timeout
        consecutiveCorrect = 0
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
            self.generateNewQuestion()
            self.startTimer()
        }
    }

    deinit {
        timer?.invalidate()
    }
}

#Preview {
    MathChallengeScreen(
        difficulty: .medium,
        challengeInfo: nil,
        onComplete: {},
        onWrongAnswer: {}
    )
}
