//
//  PlatformColorExtensions.swift
//  ColorMixLab iOS
//
//  Single source of truth for converting the shared PlatformColor (ARGB)
//  into a SwiftUI Color.
//

import SwiftUI
import shared

extension PlatformColor {
    var swiftUIColor: Color {
        Color(
            red: Double(redFloat),
            green: Double(greenFloat),
            blue: Double(blueFloat),
            opacity: Double(alphaFloat)
        )
    }
}
