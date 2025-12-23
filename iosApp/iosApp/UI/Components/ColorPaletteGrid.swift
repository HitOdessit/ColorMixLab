//
//  ColorPaletteGrid.swift
//  ColorMixLab iOS
//
//  Grid of unlocked color buttons
//

import SwiftUI
import shared

struct ColorPaletteGrid: View {
    let unlockedColors: [GameColor]
    let onColorTap: (GameColor) -> Void

    let columns = [
        GridItem(.flexible()),
        GridItem(.flexible()),
        GridItem(.flexible())
    ]

    var body: some View {
        VStack(spacing: 12) {
            Text("Available Colors")
                .font(.system(size: 18, weight: .semibold))
                .foregroundColor(.primary)

            LazyVGrid(columns: columns, spacing: 16) {
                ForEach(unlockedColors, id: \.name) { gameColor in
                    ColorButton(
                        gameColor: gameColor,
                        onTap: { onColorTap(gameColor) }
                    )
                }
            }
        }
        .padding(20)
        .background(Color.white)
        .cornerRadius(20)
        .shadow(color: .black.opacity(0.1), radius: 8, x: 0, y: 4)
    }
}

struct ColorButton: View {
    let gameColor: GameColor
    let onTap: () -> Void

    @State private var isPressed = false

    private var color: Color {
        let platformColor = gameColor.color
        return Color(
            red: Double(platformColor.redFloat),
            green: Double(platformColor.greenFloat),
            blue: Double(platformColor.blueFloat)
        )
    }

    var body: some View {
        Button(action: {
            // Haptic feedback
            let generator = UIImpactFeedbackGenerator(style: .light)
            generator.impactOccurred()

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
            VStack(spacing: 8) {
                // Color Circle
                Circle()
                    .fill(
                        RadialGradient(
                            colors: [color.opacity(0.9), color],
                            center: .center,
                            startRadius: 10,
                            endRadius: 35
                        )
                    )
                    .frame(width: 70, height: 70)
                    .overlay(
                        Circle()
                            .stroke(Color.white, lineWidth: 3)
                    )
                    .shadow(color: color.opacity(0.5), radius: isPressed ? 5 : 10, x: 0, y: isPressed ? 2 : 5)
                    .scaleEffect(isPressed ? 0.9 : 1.0)

                // Color Name
                Text(gameColor.name)
                    .font(.system(size: 13, weight: .medium))
                    .foregroundColor(.primary)
                    .lineLimit(1)
            }
        }
        .buttonStyle(PlainButtonStyle())
    }
}

#Preview {
    ColorPaletteGrid(
        unlockedColors: [],
        onColorTap: { _ in }
    )
    .padding()
}
