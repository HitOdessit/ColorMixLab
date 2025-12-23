//
//  IntroScreen.swift
//  ColorMixLab iOS
//
//  Main introduction screen with difficulty selection and game start
//

import SwiftUI
import shared

struct IntroScreen: View {
    @State private var selectedDifficulty: Difficulty = .medium
    @State private var showGame = false
    @State private var showLeaderboard = false

    var body: some View {
        NavigationView {
            ZStack {
                // Background gradient
                LinearGradient(
                    colors: [Color(red: 0.2, green: 0.1, blue: 0.4), Color(red: 0.1, green: 0.3, blue: 0.5)],
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                .ignoresSafeArea()

                VStack(spacing: 40) {
                    Spacer()

                    // Title
                    VStack(spacing: 8) {
                        Text("🎨")
                            .font(.system(size: 80))

                        Text("Color Mix Lab")
                            .font(.system(size: 42, weight: .bold))
                            .foregroundColor(.white)

                        Text("Master the Art of Color Mixing")
                            .font(.system(size: 18))
                            .foregroundColor(.white.opacity(0.8))
                    }

                    Spacer()

                    // Difficulty Selection
                    VStack(spacing: 20) {
                        Text("Select Difficulty")
                            .font(.system(size: 24, weight: .semibold))
                            .foregroundColor(.white)

                        VStack(spacing: 12) {
                            DifficultyButton(
                                title: "Easy",
                                subtitle: "No timer • 0.75x points",
                                icon: "😌",
                                difficulty: .easy,
                                selectedDifficulty: $selectedDifficulty
                            )

                            DifficultyButton(
                                title: "Medium",
                                subtitle: "40s timer • 1.0x points",
                                icon: "🎯",
                                difficulty: .medium,
                                selectedDifficulty: $selectedDifficulty
                            )

                            DifficultyButton(
                                title: "Hard",
                                subtitle: "20s timer • 1.25x points",
                                icon: "🔥",
                                difficulty: .hard,
                                selectedDifficulty: $selectedDifficulty
                            )
                        }
                        .padding(.horizontal, 20)
                    }

                    Spacer()

                    // Buttons
                    VStack(spacing: 16) {
                        // Start Game Button
                        Button(action: {
                            showGame = true
                        }) {
                            HStack {
                                Text("Start Game")
                                    .font(.system(size: 20, weight: .semibold))
                                Image(systemName: "play.fill")
                            }
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 56)
                            .background(
                                LinearGradient(
                                    colors: [Color.green, Color.green.opacity(0.8)],
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                            )
                            .cornerRadius(16)
                        }

                        // Leaderboard Button
                        Button(action: {
                            showLeaderboard = true
                        }) {
                            HStack {
                                Text("Leaderboard")
                                    .font(.system(size: 18, weight: .medium))
                                Image(systemName: "trophy.fill")
                            }
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 48)
                            .background(Color.white.opacity(0.2))
                            .cornerRadius(12)
                        }
                    }
                    .padding(.horizontal, 32)
                    .padding(.bottom, 40)
                }
            }
            .navigationBarHidden(true)
            .fullScreenCover(isPresented: $showGame) {
                GameScreen(difficulty: selectedDifficulty)
            }
            .sheet(isPresented: $showLeaderboard) {
                LeaderboardView()
            }
        }
    }
}

struct DifficultyButton: View {
    let title: String
    let subtitle: String
    let icon: String
    let difficulty: Difficulty
    @Binding var selectedDifficulty: Difficulty

    var isSelected: Bool {
        selectedDifficulty == difficulty
    }

    var body: some View {
        Button(action: {
            selectedDifficulty = difficulty
        }) {
            HStack(spacing: 16) {
                Text(icon)
                    .font(.system(size: 32))

                VStack(alignment: .leading, spacing: 4) {
                    Text(title)
                        .font(.system(size: 20, weight: .semibold))
                        .foregroundColor(.white)

                    Text(subtitle)
                        .font(.system(size: 14))
                        .foregroundColor(.white.opacity(0.7))
                }

                Spacer()

                if isSelected {
                    Image(systemName: "checkmark.circle.fill")
                        .font(.system(size: 24))
                        .foregroundColor(.green)
                }
            }
            .padding(16)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(isSelected ? Color.white.opacity(0.25) : Color.white.opacity(0.1))
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(isSelected ? Color.green : Color.clear, lineWidth: 2)
                    )
            )
        }
    }
}

#Preview {
    IntroScreen()
}
