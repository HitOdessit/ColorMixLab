//
//  HapticProvider.swift
//  ColorMixLab iOS
//
//  Provides haptic feedback for user interactions
//

import UIKit

enum HapticType {
    case lightTap
    case success
    case error
    case warning
}

class HapticProvider {
    func performHaptic(type: HapticType) {
        switch type {
        case .lightTap:
            let generator = UIImpactFeedbackGenerator(style: .light)
            generator.impactOccurred()

        case .success:
            let generator = UINotificationFeedbackGenerator()
            generator.notificationOccurred(.success)

        case .error:
            let generator = UINotificationFeedbackGenerator()
            generator.notificationOccurred(.error)

        case .warning:
            let generator = UINotificationFeedbackGenerator()
            generator.notificationOccurred(.warning)
        }
    }
}
