//
//  ActionButtons.swift
//  ColorMixLab iOS
//
//  Check and Clear action buttons
//

import SwiftUI

struct ActionButtons: View {
    let hasDrops: Bool
    let hasChecked: Bool
    let onCheck: () -> Void
    let onClear: () -> Void

    var body: some View {
        HStack(spacing: 16) {
            // Clear Button
            Button(action: onClear) {
                HStack(spacing: 8) {
                    Image(systemName: "trash.fill")
                        .font(.system(size: 18))
                    Text("Clear")
                        .font(.system(size: 18, weight: .semibold))
                }
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(
                    LinearGradient(
                        colors: hasDrops ? [Color.red.opacity(0.8), Color.red] : [Color.gray.opacity(0.5), Color.gray.opacity(0.4)],
                        startPoint: .leading,
                        endPoint: .trailing
                    )
                )
                .cornerRadius(16)
                .shadow(color: hasDrops ? Color.red.opacity(0.3) : Color.clear, radius: 8, x: 0, y: 4)
            }
            .disabled(!hasDrops)

            // Check Button
            Button(action: onCheck) {
                HStack(spacing: 8) {
                    Image(systemName: "checkmark.circle.fill")
                        .font(.system(size: 20))
                    Text("Check")
                        .font(.system(size: 18, weight: .bold))
                }
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(
                    LinearGradient(
                        colors: hasDrops && !hasChecked ? [Color.green.opacity(0.8), Color.green] : [Color.gray.opacity(0.5), Color.gray.opacity(0.4)],
                        startPoint: .leading,
                        endPoint: .trailing
                    )
                )
                .cornerRadius(16)
                .shadow(color: hasDrops && !hasChecked ? Color.green.opacity(0.3) : Color.clear, radius: 8, x: 0, y: 4)
            }
            .disabled(!hasDrops || hasChecked)
        }
        .padding(.horizontal, 16)
    }
}

#Preview {
    VStack(spacing: 20) {
        ActionButtons(hasDrops: false, hasChecked: false, onCheck: {}, onClear: {})
        ActionButtons(hasDrops: true, hasChecked: false, onCheck: {}, onClear: {})
        ActionButtons(hasDrops: true, hasChecked: true, onCheck: {}, onClear: {})
    }
    .padding()
    .background(Color.gray.opacity(0.1))
}
