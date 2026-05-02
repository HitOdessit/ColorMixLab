//
//  SimpleGameViewModel.swift
//  ColorMixLab iOS
//
//  Simplified ViewModel demonstrating shared module usage
//

import Foundation
import SwiftUI
import Combine
import shared

class SimpleGameViewModel: ObservableObject {
    @Published var currentLevel: Int = 1
    @Published var score: Int = 0

    init() {
        // Simple initialization
        print("SimpleGameViewModel initialized")
    }

    func testSharedModule() {
        // Example: Access PlatformColor constants
        let redColor = PlatformColor.companion.Red
        print("Red color RGB: \(redColor.red), \(redColor.green), \(redColor.blue)")

        let blueColor = PlatformColor.companion.Blue
        print("Blue color RGB: \(blueColor.red), \(blueColor.green), \(blueColor.blue)")
    }
}
