//
//  ResultDialog.swift
//  ColorMixLab iOS
//
//  Dialog showing level completion result
//

import SwiftUI

struct ResultDialog: View {
    let isSuccess: Bool
    let similarity: Float
    let basePoints: Int32
    let timeBonus: Int32
    let message: String
    let emoji: String
    let onNext: () -> Void
    let onRetry: () -> Void

    @Environment(\.dismiss) var dismiss

    var totalPoints: Int32 {
        basePoints + timeBonus
    }

    var similarityPercentage: Int {
        Int(similarity * 100)
    }

    var body: some View {
        ZStack {
            Color.black.opacity(0.4)
                .ignoresSafeArea()
                .onTapGesture {
                    // Prevent dismiss on background tap
                }

            VStack(spacing: 24) {
                // Emoji
                Text(emoji)
                    .font(.system(size: 80))

                // Message
                Text(message)
                    .font(.system(size: 28, weight: .bold))
                    .foregroundColor(.primary)
                    .multilineTextAlignment(.center)

                // Similarity
                VStack(spacing: 8) {
                    Text("Similarity")
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.secondary)

                    Text("\(similarityPercentage)%")
                        .font(.system(size: 48, weight: .bold))
                        .foregroundColor(isSuccess ? .green : .orange)
                }

                // Points Breakdown
                VStack(spacing: 12) {
                    Divider()

                    HStack {
                        Text("Base Points")
                            .font(.system(size: 16))
                            .foregroundColor(.secondary)
                        Spacer()
                        Text("\(basePoints >= 0 ? "+" : "")\(basePoints)")
                            .font(.system(size: 18, weight: .semibold))
                            .foregroundColor(basePoints >= 0 ? .primary : .red)
                    }

                    if timeBonus > 0 {
                        HStack {
                            Text("Time Bonus")
                                .font(.system(size: 16))
                                .foregroundColor(.secondary)
                            Spacer()
                            Text("+\(timeBonus)")
                                .font(.system(size: 18, weight: .semibold))
                                .foregroundColor(.blue)
                        }
                    }

                    Divider()

                    HStack {
                        Text("Total")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(.primary)
                        Spacer()
                        Text("\(totalPoints >= 0 ? "+" : "")\(totalPoints)")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(totalPoints >= 0 ? .green : .red)
                    }
                }
                .padding(.horizontal, 20)

                // Action Buttons
                VStack(spacing: 12) {
                    if isSuccess {
                        Button(action: {
                            dismiss()
                            onNext()
                        }) {
                            Text("Next Level →")
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
                    } else {
                        Button(action: {
                            dismiss()
                            onRetry()
                        }) {
                            Text("Try Again")
                                .font(.system(size: 18, weight: .bold))
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity)
                                .frame(height: 54)
                                .background(
                                    LinearGradient(
                                        colors: [Color.orange, Color.orange.opacity(0.8)],
                                        startPoint: .leading,
                                        endPoint: .trailing
                                    )
                                )
                                .cornerRadius(16)
                        }

                        Button(action: {
                            dismiss()
                            onNext()
                        }) {
                            Text("Next Level Anyway")
                                .font(.system(size: 16, weight: .medium))
                                .foregroundColor(.blue)
                                .frame(maxWidth: .infinity)
                                .frame(height: 48)
                                .background(Color.blue.opacity(0.1))
                                .cornerRadius(12)
                        }
                    }
                }
            }
            .padding(32)
            .background(Color.white)
            .cornerRadius(24)
            .shadow(color: .black.opacity(0.3), radius: 20, x: 0, y: 10)
            .padding(.horizontal, 40)
        }
    }
}

#Preview {
    ResultDialog(
        isSuccess: true,
        similarity: 0.92,
        basePoints: 80,
        timeBonus: 25,
        message: "Great Job!",
        emoji: "👍",
        onNext: {},
        onRetry: {}
    )
}
