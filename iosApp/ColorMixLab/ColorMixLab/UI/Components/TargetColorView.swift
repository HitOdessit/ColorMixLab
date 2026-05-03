//
//  TargetColorView.swift
//  ColorMixLab iOS
//
//  Simple target color display (matches Android)
//

import SwiftUI
import shared

struct TargetColorView: View {
    let targetColor: Color

    var body: some View {
        VStack(spacing: 4) {
            Text("Target")
                .font(.system(size: 13, weight: .bold))
                .foregroundColor(.primary)

            RoundedRectangle(cornerRadius: 10)
                .fill(targetColor)
                .frame(width: 75, height: 75)
                .overlay(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(Color.primary, lineWidth: 3)
                )
        }
    }
}

#Preview {
    TargetColorView(
        targetColor: Color(red: 0.8, green: 0.4, blue: 0.6)
    )
    .padding()
}
