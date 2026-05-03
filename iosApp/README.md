# `iosApp/` — iOS application

iOS app module. Native SwiftUI app that consumes the `shared` Kotlin Multiplatform framework.

## Layout

```
ColorMixLab/
├── ColorMixLab.xcodeproj/        # Xcode project
└── ColorMixLab/
    ├── ColorMixLabApp.swift      # @main entry point
    ├── ContentView.swift         # NavigationStack root
    ├── GameViewModel.swift       # ObservableObject bridging StateFlow to @Published
    ├── GameStateExtensions.swift # Swift-friendly copy() and equality for Kotlin GameState
    ├── UI/
    │   ├── Screens/              # IntroScreen, GameScreen, MathChallengeScreen,
    │   │                         # MenuDialog, ResultDialog, LeaderboardView, GameCompletedView
    │   └── Components/           # MixingBowlView, TargetColorView, ActionButtons, etc.
    └── Utilities/
        ├── HapticProvider.swift
        └── AppIconGenerator.swift
```

## Building

1. Build the shared framework first:
   ```bash
   ./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
   ```
2. Open `ColorMixLab/ColorMixLab.xcodeproj` in Xcode 15+.
3. Select an iPhone simulator and Run.

## Key entry points

- [`ColorMixLabApp.swift`](ColorMixLab/ColorMixLabApp.swift) — App entry point.
- [`GameViewModel.swift`](ColorMixLab/GameViewModel.swift) — Bridge from Kotlin `StateFlow<GameState>` to SwiftUI `@Published` via 100ms polling. See [ADR-0003](../docs/adr/0003-ios-stateflow-polling-bridge.md) for the rationale.
- [`UI/Screens/GameScreen.swift`](ColorMixLab/UI/Screens/GameScreen.swift) — Main gameplay screen.

## Known gaps

- **No iPad layout.** The app currently renders the iPhone layout scaled up on iPad. See [ROADMAP.md](../ROADMAP.md).
- **No iOS unit tests.** Shared logic is tested via the Android test runner. iOS-specific code (the polling bridge, custom equality, view models) is uncovered.
- **Sound is silent.** The shared `SoundProvider` actual is stubbed.
