//
//  MixingBowlView.swift
//  ColorMixLab iOS
//
//  Display the mixed color and similarity indicator
//

import SwiftUI
import shared

struct MixingBowlView: View {
    let mixedColor: Color
    let drops: [GameColor: KotlinInt]
    let similarity: Float

    var totalDrops: Int {
        drops.values.reduce(0) { $0 + $1.intValue }
    }

    var similarityPercentage: Int {
        Int(similarity * 100)
    }

    var similarityColor: Color {
        switch similarity {
        case 0.95...: return .green
        case 0.85..<0.95: return .blue
        case 0.75..<0.85: return .orange
        default: return .red
        }
    }

    var body: some View {
        VStack(spacing: 16) {
            Text("Your Mix")
                .font(.system(size: 18, weight: .semibold))
                .foregroundColor(.primary)

            ZStack {
                // Mixing Bowl
                Circle()
                    .fill(
                        RadialGradient(
                            colors: [mixedColor.opacity(0.9), mixedColor],
                            center: .center,
                            startRadius: 20,
                            endRadius: 100
                        )
                    )
                    .frame(width: 160, height: 160)
                    .overlay(
                        Circle()
                            .stroke(Color.gray.opacity(0.4), lineWidth: 4)
                    )
                    .shadow(color: mixedColor.opacity(0.6), radius: 25, x: 0, y: 12)

                // Drop count indicator
                if totalDrops > 0 {
                    VStack(spacing: 4) {
                        Text("\(totalDrops)")
                            .font(.system(size: 32, weight: .bold))
                            .foregroundColor(.white)
                            .shadow(color: .black.opacity(0.3), radius: 2, x: 0, y: 1)

                        Text("drops")
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(.white.opacity(0.9))
                            .shadow(color: .black.opacity(0.3), radius: 2, x: 0, y: 1)
                    }
                }
            }

            // Similarity Indicator
            if totalDrops > 0 {
                VStack(spacing: 8) {
                    Text("Similarity")
                        .font(.system(size: 14, weight: .medium))
                        .foregroundColor(.secondary)

                    HStack(spacing: 12) {
                        // Progress Bar
                        GeometryReader { geometry in
                            ZStack(alignment: .leading) {
                                // Background
                                RoundedRectangle(cornerRadius: 8)
                                    .fill(Color.gray.opacity(0.2))
                                    .frame(height: 24)

                                // Progress
                                RoundedRectangle(cornerRadius: 8)
                                    .fill(
                                        LinearGradient(
                                            colors: [similarityColor.opacity(0.8), similarityColor],
                                            startPoint: .leading,
                                            endPoint: .trailing
                                        )
                                    )
                                    .frame(width: geometry.size.width * CGFloat(similarity), height: 24)
                                    .animation(.spring(response: 0.5), value: similarity)
                            }
                        }
                        .frame(height: 24)

                        // Percentage
                        Text("\(similarityPercentage)%")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(similarityColor)
                            .frame(width: 50)
                    }
                }
            }

            // Drop details
            if !drops.isEmpty {
                VStack(spacing: 8) {
                    Text("Drops Added")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(.secondary)

                    FlowLayout(spacing: 8) {
                        ForEach(Array(drops.sorted(by: { $0.key.name < $1.key.name })), id: \.key.name) { color, count in
                            DropBadge(colorName: color.name, count: count.intValue)
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

struct DropBadge: View {
    let colorName: String
    let count: Int

    var body: some View {
        HStack(spacing: 6) {
            Circle()
                .fill(getColorForName(colorName))
                .frame(width: 12, height: 12)

            Text(colorName)
                .font(.system(size: 12, weight: .medium))

            Text("×\(count)")
                .font(.system(size: 12, weight: .bold))
                .foregroundColor(.secondary)
        }
        .padding(.horizontal, 10)
        .padding(.vertical, 6)
        .background(Color.gray.opacity(0.1))
        .cornerRadius(12)
    }

    private func getColorForName(_ name: String) -> Color {
        switch name.lowercased() {
        case "red": return .red
        case "blue": return .blue
        case "green": return .green
        case "yellow": return .yellow
        case "orange": return .orange
        case "purple": return .purple
        case "pink": return .pink
        case "cyan": return .cyan
        case "magenta": return Color(red: 1, green: 0, blue: 1)
        case "lime": return Color(red: 0.8, green: 1, blue: 0)
        case "brown": return .brown
        case "gray": return .gray
        default: return .gray
        }
    }
}

// Simple FlowLayout for wrapping items
struct FlowLayout: Layout {
    var spacing: CGFloat = 8

    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let result = FlowLayoutResult(
            in: proposal.replacingUnspecifiedDimensions().width,
            subviews: subviews,
            spacing: spacing
        )
        return result.size
    }

    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        let result = FlowLayoutResult(
            in: bounds.width,
            subviews: subviews,
            spacing: spacing
        )
        for (index, subview) in subviews.enumerated() {
            subview.place(at: result.positions[index], proposal: .unspecified)
        }
    }

    struct FlowLayoutResult {
        var size: CGSize = .zero
        var positions: [CGPoint] = []

        init(in maxWidth: CGFloat, subviews: Subviews, spacing: CGFloat) {
            var x: CGFloat = 0
            var y: CGFloat = 0
            var lineHeight: CGFloat = 0

            for subview in subviews {
                let size = subview.sizeThatFits(.unspecified)

                if x + size.width > maxWidth && x > 0 {
                    x = 0
                    y += lineHeight + spacing
                    lineHeight = 0
                }

                positions.append(CGPoint(x: x, y: y))
                lineHeight = max(lineHeight, size.height)
                x += size.width + spacing
            }

            self.size = CGSize(width: maxWidth, height: y + lineHeight)
        }
    }
}

#Preview {
    MixingBowlView(
        mixedColor: Color(red: 0.7, green: 0.3, blue: 0.5),
        drops: [:],
        similarity: 0.85
    )
    .padding()
}
