//
//  TargetColorView.swift
//  ColorMixLab iOS
//
//  Display target color and recipe
//

import SwiftUI
import shared

struct TargetColorView: View {
    let targetColor: Color
    let targetRecipe: [GameColor: KotlinInt]

    var body: some View {
        VStack(spacing: 16) {
            Text("Target Color")
                .font(.system(size: 18, weight: .semibold))
                .foregroundColor(.primary)

            // Target Color Circle
            Circle()
                .fill(targetColor)
                .frame(width: 120, height: 120)
                .overlay(
                    Circle()
                        .stroke(Color.gray.opacity(0.3), lineWidth: 3)
                )
                .shadow(color: targetColor.opacity(0.5), radius: 20, x: 0, y: 10)

            // Recipe Display
            if !targetRecipe.isEmpty {
                VStack(spacing: 8) {
                    Text("Recipe Hint")
                        .font(.system(size: 14, weight: .medium))
                        .foregroundColor(.secondary)

                    HStack(spacing: 12) {
                        ForEach(Array(targetRecipe.sorted(by: { $0.value.intValue > $1.value.intValue })), id: \.key.name) { color, count in
                            RecipeItem(colorName: color.name, count: count.intValue)
                        }
                    }
                }
            }
        }
        .padding(20)
        .background(Color.white)
        .cornerRadius(20)
        .shadow(color: .black.opacity(0.1), radius: 8, x: 0, y: 4)
    }
}

struct RecipeItem: View {
    let colorName: String
    let count: Int

    var body: some View {
        HStack(spacing: 4) {
            Text(colorName)
                .font(.system(size: 12, weight: .medium))
                .foregroundColor(.primary)

            Text("×\(count)")
                .font(.system(size: 12, weight: .bold))
                .foregroundColor(.secondary)
        }
        .padding(.horizontal, 10)
        .padding(.vertical, 6)
        .background(Color.gray.opacity(0.1))
        .cornerRadius(8)
    }
}

#Preview {
    TargetColorView(
        targetColor: Color(red: 0.8, green: 0.4, blue: 0.6),
        targetRecipe: [:]
    )
    .padding()
}
