//
//  IntroScreen.swift
//  ColorMixLab iOS
//
//  Intro screen with difficulty selection - matches Android version exactly
//

import SwiftUI
import shared

struct IntroScreen: View {
    @State private var selectedDifficulty: Difficulty = .medium
    @State private var navigateToGame = false
    @State private var showLeaderboard = false
    @State private var showCelebration = false
    @State private var versionClickCount = 0
    @State private var versionClickTimer: Timer?

    var body: some View {
        NavigationStack {
            ZStack {
                // Background
                Color(red: 0.95, green: 0.95, blue: 0.97)
                    .ignoresSafeArea()

                ScrollView {
                    VStack(spacing: 12) {
                        Spacer().frame(height: 20)

                        // Title
                        Text("🎨 ColorMixLab 🎨")
                            .font(.system(size: 28, weight: .bold))
                            .foregroundColor(.blue)

                        // Version (with double-click detection)
                        Text("v1.19")
                            .font(.system(size: 13))
                            .foregroundColor(.gray.opacity(0.6))
                            .offset(y: -8)
                            .onTapGesture {
                                versionClickCount += 1

                                if versionClickCount == 1 {
                                    versionClickTimer = Timer.scheduledTimer(withTimeInterval: 0.3, repeats: false) { _ in
                                        versionClickCount = 0
                                    }
                                } else if versionClickCount == 2 {
                                    versionClickTimer?.invalidate()
                                    versionClickCount = 0
                                    showCelebration = true
                                }
                            }

                        // Instructions Card
                        VStack(alignment: .leading, spacing: 4) {
                            Text("How to Play:")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.primary)

                            Text("• Match the target color by mixing")
                                .font(.system(size: 13))
                            Text("• Need 80+% similarity to advance")
                                .font(.system(size: 13))
                            Text("• Complete up to 30 levels")
                                .font(.system(size: 13))
                            Text("• Higher similarity = more points")
                                .font(.system(size: 13))
                            Text("• Harder difficulty = more points")
                                .font(.system(size: 13))
                        }
                        .padding(12)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color.blue.opacity(0.1))
                        .cornerRadius(12)
                        .padding(.horizontal, 16)

                        Spacer().frame(height: 4)

                        // Difficulty Selection
                        Text("Select Difficulty:")
                            .font(.system(size: 16, weight: .bold))
                            .padding(.horizontal, 16)
                            .frame(maxWidth: .infinity, alignment: .leading)

                        VStack(spacing: 8) {
                            DifficultyButton(
                                difficulty: .easy,
                                isSelected: selectedDifficulty == .easy,
                                icon: "🟢",
                                title: "Easy",
                                description: "No Timer • 75% Points",
                                onTap: { selectedDifficulty = .easy }
                            )

                            DifficultyButton(
                                difficulty: .medium,
                                isSelected: selectedDifficulty == .medium,
                                icon: "🟡",
                                title: "Medium",
                                description: "40s • 100% Points + Time Bonus",
                                onTap: { selectedDifficulty = .medium }
                            )

                            DifficultyButton(
                                difficulty: .hard,
                                isSelected: selectedDifficulty == .hard,
                                icon: "🔴",
                                title: "Hard",
                                description: "20s • 125% Points + Time Bonus",
                                onTap: { selectedDifficulty = .hard }
                            )
                        }
                        .padding(.horizontal, 16)

                        Spacer().frame(height: 4)

                        // Start Game Button
                        Button(action: {
                            let generator = UIImpactFeedbackGenerator(style: .medium)
                            generator.impactOccurred()
                            navigateToGame = true
                        }) {
                            Text("Start Game")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity)
                                .frame(height: 52)
                                .background(Color.blue)
                                .cornerRadius(12)
                        }
                        .padding(.horizontal, 16)

                        // Leaderboard Button
                        Button(action: {
                            showLeaderboard = true
                        }) {
                            HStack(spacing: 6) {
                                Image(systemName: "star.fill")
                                    .font(.system(size: 18))
                                Text("Leaderboard")
                                    .font(.system(size: 18, weight: .medium))
                            }
                            .foregroundColor(.blue)
                            .frame(maxWidth: .infinity)
                            .frame(height: 48)
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(Color.blue, lineWidth: 1)
                            )
                        }
                        .padding(.horizontal, 16)

                        Spacer().frame(height: 20)
                    }
                }
            }
            .navigationDestination(isPresented: $navigateToGame) {
                GameScreen(difficulty: selectedDifficulty)
            }
            .sheet(isPresented: $showLeaderboard) {
                LeaderboardView()
            }

            // Celebration overlay
            if showCelebration {
                GameCompletionCelebration(onAnimationComplete: {
                    showCelebration = false
                })
            }
        }
    }
}

struct DifficultyButton: View {
    let difficulty: Difficulty
    let isSelected: Bool
    let icon: String
    let title: String
    let description: String
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                // Icon
                Text(icon)
                    .font(.system(size: 24))

                // Content
                VStack(alignment: .leading, spacing: 2) {
                    Text(title)
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(.primary)

                    Text(description)
                        .font(.system(size: 13))
                        .foregroundColor(.secondary)
                }

                Spacer()

                // Selection indicator
                if isSelected {
                    Image(systemName: "checkmark.circle.fill")
                        .font(.system(size: 24))
                        .foregroundColor(.blue)
                } else {
                    Image(systemName: "circle")
                        .font(.system(size: 24))
                        .foregroundColor(.gray.opacity(0.3))
                }
            }
            .padding(12)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(isSelected ? Color.blue.opacity(0.1) : Color.white)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(isSelected ? Color.blue : Color.gray.opacity(0.2), lineWidth: isSelected ? 2 : 1)
            )
        }
        .buttonStyle(PlainButtonStyle())
    }
}

#Preview {
    IntroScreen()
}
