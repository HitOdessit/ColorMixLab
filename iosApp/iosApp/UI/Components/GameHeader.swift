//
//  GameHeader.swift
//  ColorMixLab iOS
//
//  Header component showing level, score, and timer
//

import SwiftUI

struct GameHeader: View {
    let level: Int32
    let score: Int32
    let timeRemaining: KotlinInt?
    let onMenuTap: () -> Void

    var body: some View {
        HStack(spacing: 12) {
            // Menu Button
            Button(action: onMenuTap) {
                Image(systemName: "line.3.horizontal")
                    .font(.system(size: 24))
                    .foregroundColor(.primary)
                    .frame(width: 44, height: 44)
                    .background(Color.white)
                    .cornerRadius(12)
                    .shadow(color: .black.opacity(0.1), radius: 4, x: 0, y: 2)
            }

            Spacer()

            // Level Display
            HStack(spacing: 8) {
                Text("Level")
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(.secondary)

                Text("\(level)")
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(.primary)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
            .background(Color.white)
            .cornerRadius(12)
            .shadow(color: .black.opacity(0.1), radius: 4, x: 0, y: 2)

            Spacer()

            // Score Display
            HStack(spacing: 8) {
                Text("⭐")
                    .font(.system(size: 18))

                Text("\(score)")
                    .font(.system(size: 20, weight: .bold))
                    .foregroundColor(.primary)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
            .background(Color.white)
            .cornerRadius(12)
            .shadow(color: .black.opacity(0.1), radius: 4, x: 0, y: 2)

            // Timer (if active)
            if let time = timeRemaining {
                let seconds = time.intValue
                TimerDisplay(seconds: seconds)
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }
}

struct TimerDisplay: View {
    let seconds: Int

    var isWarning: Bool {
        seconds <= 10
    }

    var isCritical: Bool {
        seconds <= 5
    }

    var body: some View {
        HStack(spacing: 6) {
            Image(systemName: "timer")
                .font(.system(size: 16))

            Text("\(seconds)s")
                .font(.system(size: 18, weight: .bold))
        }
        .foregroundColor(isCritical ? .red : (isWarning ? .orange : .primary))
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color.white)
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(isCritical ? Color.red : (isWarning ? Color.orange : Color.clear), lineWidth: 2)
                )
        )
        .shadow(color: .black.opacity(0.1), radius: 4, x: 0, y: 2)
        .animation(.easeInOut(duration: 0.3), value: isWarning)
        .animation(.easeInOut(duration: 0.3), value: isCritical)
    }
}

#Preview {
    VStack(spacing: 20) {
        GameHeader(level: 5, score: 1250, timeRemaining: 25, onMenuTap: {})
        GameHeader(level: 10, score: 3500, timeRemaining: 8, onMenuTap: {})
        GameHeader(level: 15, score: 5000, timeRemaining: 3, onMenuTap: {})
        GameHeader(level: 20, score: 7500, timeRemaining: nil, onMenuTap: {})
    }
    .padding()
    .background(Color.gray.opacity(0.1))
}
