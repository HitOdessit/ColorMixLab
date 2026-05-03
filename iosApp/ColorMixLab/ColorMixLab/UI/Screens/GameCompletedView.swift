//
//  GameCompletedView.swift
//  ColorMixLab iOS
//
//  Full-screen celebration for completing all 30 levels
//

import SwiftUI
import shared

struct GameCompletedView: View {
    let finalScore: Int32
    let difficulty: Difficulty
    let onPlayAgain: () -> Void
    let onQuit: () -> Void

    @State private var showConfetti = false
    @State private var showNicknameDialog = false
    @State private var nickname = ""
    @State private var savedToLeaderboard = false

    var body: some View {
        ZStack {
            // Gradient Background
            LinearGradient(
                colors: [
                    Color(red: 0.5, green: 0.2, blue: 0.8),
                    Color(red: 0.2, green: 0.4, blue: 0.9),
                    Color(red: 0.1, green: 0.6, blue: 0.8)
                ],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .ignoresSafeArea()

            // Confetti Effect
            if showConfetti {
                ConfettiView()
            }

            VStack(spacing: 40) {
                Spacer()

                // Trophy Animation
                VStack(spacing: 20) {
                    Text("🏆")
                        .font(.system(size: 120))
                        .scaleEffect(showConfetti ? 1.0 : 0.5)
                        .animation(.spring(response: 0.6, dampingFraction: 0.5), value: showConfetti)

                    Text("Congratulations!")
                        .font(.system(size: 36, weight: .bold))
                        .foregroundColor(.white)

                    Text("You completed all 30 levels!")
                        .font(.system(size: 20))
                        .foregroundColor(.white.opacity(0.9))
                }

                // Stats
                VStack(spacing: 16) {
                    StatRow(label: "Final Score", value: "\(finalScore)", icon: "star.fill")
                    StatRow(label: "Difficulty", value: difficulty.name.capitalized, icon: "flag.fill")
                    StatRow(label: "Levels", value: "30/30", icon: "checkmark.circle.fill")
                }
                .padding(24)
                .background(Color.white.opacity(0.2))
                .cornerRadius(20)
                .padding(.horizontal, 32)

                Spacer()

                // Action Buttons
                VStack(spacing: 16) {
                    Button(action: {
                        showNicknameDialog = true
                    }) {
                        HStack {
                            Image(systemName: "trophy.fill")
                            Text(savedToLeaderboard ? "Saved to Leaderboard ✓" : "Save to Leaderboard")
                                .font(.system(size: 18, weight: .semibold))
                        }
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 56)
                        .background(
                            savedToLeaderboard ?
                            LinearGradient(
                                colors: [Color.green, Color.green],
                                startPoint: .leading,
                                endPoint: .trailing
                            ) :
                            LinearGradient(
                                colors: [Color.yellow.opacity(0.8), Color.orange],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .cornerRadius(16)
                    }
                    .disabled(savedToLeaderboard)

                    Button(action: onPlayAgain) {
                        Text("Play Again")
                            .font(.system(size: 18, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 56)
                            .background(Color.white.opacity(0.3))
                            .cornerRadius(16)
                    }

                    Button(action: onQuit) {
                        Text("Back to Menu")
                            .font(.system(size: 16, weight: .medium))
                            .foregroundColor(.white.opacity(0.8))
                    }
                }
                .padding(.horizontal, 32)
                .padding(.bottom, 40)
            }
        }
        .onAppear {
            withAnimation(.easeInOut(duration: 0.5).delay(0.3)) {
                showConfetti = true
            }
        }
        .sheet(isPresented: $showNicknameDialog) {
            NicknameDialog(
                score: finalScore,
                level: 30,
                difficulty: difficulty,
                onSave: { enteredNickname in
                    nickname = enteredNickname
                    saveToLeaderboard()
                    showNicknameDialog = false
                }
            )
        }
    }

    private func saveToLeaderboard() {
        let entry = LeaderboardEntry(
            nickname: nickname.isEmpty ? "Player" : nickname,
            score: finalScore,
            level: 30,
            difficulty: difficulty,
            timestamp: Int64(Date().timeIntervalSince1970 * 1000)
        )

        let storage = PlatformStorage()
        let leaderboardManager = LeaderboardManager(storage: storage)
        leaderboardManager.addEntry(entry: entry)

        savedToLeaderboard = true
    }
}

struct StatRow: View {
    let label: String
    let value: String
    let icon: String

    var body: some View {
        HStack {
            Image(systemName: icon)
                .font(.system(size: 20))
                .foregroundColor(.white)
                .frame(width: 32)

            Text(label)
                .font(.system(size: 16))
                .foregroundColor(.white.opacity(0.9))

            Spacer()

            Text(value)
                .font(.system(size: 20, weight: .bold))
                .foregroundColor(.white)
        }
    }
}

struct ConfettiView: View {
    @State private var animate = false

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                ForEach(0..<50, id: \.self) { index in
                    ConfettiPiece(geometry: geometry, index: index, animate: $animate)
                }
            }
        }
        .onAppear {
            animate = true
        }
        .ignoresSafeArea()
    }
}

struct ConfettiPiece: View {
    let geometry: GeometryProxy
    let index: Int
    @Binding var animate: Bool

    @State private var x: CGFloat = 0
    @State private var y: CGFloat = -50
    @State private var rotation: Double = 0

    private let colors: [Color] = [.red, .blue, .green, .yellow, .orange, .purple, .pink]

    var body: some View {
        Rectangle()
            .fill(colors[index % colors.count])
            .frame(width: 10, height: 10)
            .rotationEffect(.degrees(rotation))
            .position(x: x, y: y)
            .onAppear {
                x = CGFloat.random(in: 0...geometry.size.width)

                withAnimation(
                    .linear(duration: Double.random(in: 2...4))
                    .repeatForever(autoreverses: false)
                ) {
                    y = geometry.size.height + 50
                    rotation = Double.random(in: 0...720)
                }
            }
    }
}

struct NicknameDialog: View {
    let score: Int32
    let level: Int32
    let difficulty: Difficulty
    let onSave: (String) -> Void

    @State private var nickname = ""
    @Environment(\.dismiss) var dismiss

    var body: some View {
        NavigationView {
            VStack(spacing: 24) {
                Spacer()

                VStack(spacing: 16) {
                    Text("🏆")
                        .font(.system(size: 60))

                    Text("Save Your Score")
                        .font(.system(size: 24, weight: .bold))

                    Text("Score: \(score)")
                        .font(.system(size: 20, weight: .semibold))
                        .foregroundColor(.blue)
                }

                VStack(spacing: 12) {
                    Text("Enter your nickname")
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.secondary)

                    TextField("Nickname", text: $nickname)
                        .font(.system(size: 18))
                        .textFieldStyle(.roundedBorder)
                        .textInputAutocapitalization(.words)
                        .submitLabel(.done)
                        .onSubmit {
                            saveScore()
                        }
                        .padding(.horizontal, 20)
                }

                Button(action: saveScore) {
                    Text("Save")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 54)
                        .background(
                            LinearGradient(
                                colors: [Color.green, Color.green.opacity(0.8)],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .cornerRadius(16)
                }
                .padding(.horizontal, 32)
                .disabled(nickname.trimmingCharacters(in: .whitespaces).isEmpty)

                Spacer()
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
            }
        }
    }

    private func saveScore() {
        onSave(nickname.trimmingCharacters(in: .whitespaces))
        dismiss()
    }
}

#Preview {
    GameCompletedView(
        finalScore: 8500,
        difficulty: .medium,
        onPlayAgain: {},
        onQuit: {}
    )
}
