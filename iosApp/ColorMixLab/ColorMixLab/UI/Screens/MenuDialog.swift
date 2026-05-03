//
//  MenuDialog.swift
//  ColorMixLab iOS
//
//  Pause menu dialog
//

import SwiftUI

struct MenuDialog: View {
    let onResume: () -> Void
    let onRestart: () -> Void
    let onQuit: () -> Void

    @Environment(\.dismiss) var dismiss

    var body: some View {
        ZStack {
            Color.black.opacity(0.4)
                .ignoresSafeArea()
                .onTapGesture {
                    dismiss()
                    onResume()
                }

            VStack(spacing: 20) {
                // Title
                HStack {
                    Image(systemName: "line.3.horizontal")
                        .font(.system(size: 24))
                    Text("Menu")
                        .font(.system(size: 28, weight: .bold))
                }
                .foregroundColor(.primary)
                .padding(.top, 8)

                Divider()
                    .padding(.vertical, 8)

                // Menu Options
                VStack(spacing: 16) {
                    MenuButton(
                        icon: "play.fill",
                        title: "Resume",
                        color: .green
                    ) {
                        dismiss()
                        onResume()
                    }

                    MenuButton(
                        icon: "arrow.clockwise",
                        title: "Restart Game",
                        color: .orange
                    ) {
                        dismiss()
                        onRestart()
                    }

                    MenuButton(
                        icon: "xmark.circle.fill",
                        title: "Quit to Menu",
                        color: .red
                    ) {
                        dismiss()
                        onQuit()
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

struct MenuButton: View {
    let icon: String
    let title: String
    let color: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 16) {
                Image(systemName: icon)
                    .font(.system(size: 22))
                    .foregroundColor(color)
                    .frame(width: 32)

                Text(title)
                    .font(.system(size: 18, weight: .semibold))
                    .foregroundColor(.primary)

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(.gray)
            }
            .padding(16)
            .background(Color.gray.opacity(0.1))
            .cornerRadius(12)
        }
    }
}

#Preview {
    MenuDialog(
        onResume: {},
        onRestart: {},
        onQuit: {}
    )
}
