//
//  GameCompletionCelebration.swift
//  ColorMixLab iOS
//
//  Celebration animation when game is completed (matches Android)
//

import SwiftUI

struct GameCompletionCelebration: View {
    let onAnimationComplete: () -> Void

    @State private var animationProgress: CGFloat = 0
    @State private var showText = false

    private let circles: [CGFloat] = [0, 60, 120, 180, 240, 300]
    private let colors: [Color] = [
        Color(red: 0.91, green: 0.30, blue: 0.24),
        Color(red: 0.20, green: 0.60, blue: 0.86),
        Color(red: 0.18, green: 0.80, blue: 0.44),
        Color(red: 0.95, green: 0.77, blue: 0.06),
        Color(red: 0.61, green: 0.35, blue: 0.71),
        Color(red: 1.00, green: 0.00, blue: 1.00)
    ]

    var body: some View {
        ZStack {
            // Background gradient
            LinearGradient(
                colors: [
                    Color(red: 0.10, green: 0.10, blue: 0.18),
                    Color(red: 0.09, green: 0.13, blue: 0.24)
                ],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .ignoresSafeArea()

            // Animated circles
            GeometryReader { geometry in
                let centerX = geometry.size.width / 2
                let centerY = geometry.size.height / 2

                ForEach(0..<circles.count, id: \.self) { index in
                    let angle = circles[index] + (animationProgress * 360)
                    let x = centerX + cos(angle * .pi / 180) * (80 + animationProgress * 120)
                    let y = centerY + sin(angle * .pi / 180) * (80 + animationProgress * 120)

                    Circle()
                        .fill(colors[index])
                        .frame(width: 45, height: 45)
                        .opacity(Double(1.0 - animationProgress * 0.5))
                        .position(x: x, y: y)
                }
            }

            // Celebration text
            if showText {
                VStack(spacing: 20) {
                    Text("🎉")
                        .font(.system(size: 80))
                        .scaleEffect(showText ? 1.0 : 0.1)
                        .animation(.spring(response: 0.6, dampingFraction: 0.6), value: showText)

                    Text("Congratulations!")
                        .font(.system(size: 36, weight: .bold))
                        .foregroundColor(.white)
                        .scaleEffect(showText ? 1.0 : 0.1)
                        .animation(.spring(response: 0.6, dampingFraction: 0.6).delay(0.2), value: showText)

                    Text("Game Completed!")
                        .font(.system(size: 24, weight: .medium))
                        .foregroundColor(.white.opacity(0.9))
                        .scaleEffect(showText ? 1.0 : 0.1)
                        .animation(.spring(response: 0.6, dampingFraction: 0.6).delay(0.4), value: showText)
                }
            }
        }
        .onAppear {
            withAnimation(.linear(duration: 10.0)) {
                animationProgress = 1.0
            }

            DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
                showText = true
            }

            DispatchQueue.main.asyncAfter(deadline: .now() + 10.0) {
                onAnimationComplete()
            }
        }
    }
}

#Preview {
    GameCompletionCelebration(onAnimationComplete: {})
}
