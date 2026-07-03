//
//  MixingBowlView.swift
//  ColorMixLab iOS
//
//  Animated mixing bowl with bouncing color sectors (matches Android exactly)
//

import SwiftUI
import shared

struct AnimatedSlice: Identifiable {
    let id = UUID()
    let color: GameColor
    var angle: CGFloat
}

struct MixingBowlView: View {
    let mixedColor: Color
    let drops: [GameColor: KotlinInt]
    let similarity: Float

    @State private var animatedSlices: [AnimatedSlice] = []

    var body: some View {
        let isEmpty = drops.isEmpty

        ZStack {
            // Outer border
            Circle()
                .stroke(Color.primary, lineWidth: 6)
                .frame(width: 203, height: 203)

            // Main bowl canvas
            Canvas { context, size in
                let radius = size.width / 2
                let center = CGPoint(x: size.width / 2, y: size.height / 2)
                let innerRadius: CGFloat = 69.3

                // Draw mixed color in center
                context.fill(
                    Path(ellipseIn: CGRect(
                        x: center.x - innerRadius,
                        y: center.y - innerRadius,
                        width: innerRadius * 2,
                        height: innerRadius * 2
                    )),
                    with: .color(mixedColor)
                )

                // Draw color slices in outer ring
                if !isEmpty {
                    var currentAngle: CGFloat = -90 // Start from top

                    for slice in animatedSlices {
                        let sliceColor = slice.color.color.swiftUIColor
                        let sweepAngle = slice.angle

                        // Draw outer arc
                        var path = Path()
                        path.addArc(
                            center: center,
                            radius: radius,
                            startAngle: Angle(degrees: Double(currentAngle)),
                            endAngle: Angle(degrees: Double(currentAngle + sweepAngle)),
                            clockwise: false
                        )
                        path.addLine(to: center)
                        path.closeSubpath()

                        context.fill(path, with: .color(sliceColor))

                        // Cover inner part with mixed color
                        var innerPath = Path()
                        innerPath.addArc(
                            center: center,
                            radius: innerRadius,
                            startAngle: Angle(degrees: Double(currentAngle)),
                            endAngle: Angle(degrees: Double(currentAngle + sweepAngle)),
                            clockwise: false
                        )
                        innerPath.addLine(to: center)
                        innerPath.closeSubpath()

                        context.fill(innerPath, with: .color(mixedColor))

                        currentAngle += sweepAngle
                    }
                }
            }
            .frame(width: 186, height: 186)

            // Empty text
            if isEmpty {
                Text("Empty")
                    .font(.system(size: 18, weight: .medium))
                    .foregroundColor(.gray)
            }

            // Similarity percentage when not empty
            if !isEmpty {
                VStack(spacing: 4) {
                    Text("\(Int(similarity * 100))%")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(.white)
                        .shadow(color: .black.opacity(0.3), radius: 2, x: 0, y: 1)

                    Text("Match")
                        .font(.system(size: 12))
                        .foregroundColor(.white.opacity(0.8))
                        .shadow(color: .black.opacity(0.3), radius: 2, x: 0, y: 1)
                }
            }
        }
        .frame(width: 203, height: 203)
        .onChange(of: drops) { _ in
            updateSlices()
        }
        .onAppear {
            updateSlices()
        }
    }

    private func updateSlices() {
        let newSliceCount = drops.values.reduce(0) { $0 + $1.intValue }
        let currentSliceCount = animatedSlices.count
        let targetAngle: CGFloat = newSliceCount > 0 ? 360.0 / CGFloat(newSliceCount) : 0

        if newSliceCount == 0 {
            // Clear all slices
            withAnimation {
                animatedSlices = []
            }
        } else if newSliceCount > currentSliceCount {
            // Add new slices with animation
            var newSlices: [AnimatedSlice] = []
            for (color, count) in drops {
                for _ in 0..<count.intValue {
                    newSlices.append(AnimatedSlice(color: color, angle: 0))
                }
            }

            // Animate slices growing
            withAnimation(.spring(response: 0.6, dampingFraction: 0.7)) {
                animatedSlices = newSlices.map { slice in
                    AnimatedSlice(color: slice.color, angle: targetAngle)
                }
            }
        } else {
            // Shrink slices
            var newSlices: [AnimatedSlice] = []
            for (color, count) in drops {
                for _ in 0..<count.intValue {
                    newSlices.append(AnimatedSlice(color: color, angle: targetAngle))
                }
            }

            withAnimation(.spring(response: 0.6, dampingFraction: 0.7)) {
                animatedSlices = newSlices
            }
        }
    }
}

#Preview {
    MixingBowlView(
        mixedColor: Color.purple,
        drops: [:],
        similarity: 0.75
    )
}
