# `shared/` — Kotlin Multiplatform shared module

Cross-platform game logic. Consumed by the Android app (as a Gradle project dependency) and the iOS app (as a static framework named `shared`).

## Layout

```
src/
├── commonMain/kotlin/com/colormixlab/
│   ├── game/
│   │   ├── GameController.kt    # Central state machine
│   │   ├── GameState.kt          # Immutable state snapshot
│   │   ├── GameConstants.kt      # Tunable gameplay constants
│   │   ├── ColorMixer.kt         # RGB averaging + similarity
│   │   ├── LevelManager.kt       # Target color generation
│   │   └── math/                 # Math challenge generation
│   ├── model/                    # GameColor, LeaderboardEntry, PlatformColor
│   ├── data/                     # LeaderboardManager
│   ├── platform/                 # expect declarations: HapticProvider, PlatformStorage, SoundProvider
│   │                             # KeyValueStorage interface for testability
│   └── utils/                    # MathChallengeTimer
├── androidMain/                  # Android actuals (SharedPreferences, Vibrator, SoundPool)
└── iosMain/                      # iOS actuals (NSUserDefaults, UIImpactFeedbackGenerator, AVAudioPlayer)
```

## Key entry points

- [`GameController.kt`](src/commonMain/kotlin/com/colormixlab/game/GameController.kt) — All game actions. Source of truth for `GameState`.
- [`GameState.kt`](src/commonMain/kotlin/com/colormixlab/game/GameState.kt) — The state snapshot consumed by both UIs.
- [`LeaderboardManager.kt`](src/commonMain/kotlin/com/colormixlab/data/LeaderboardManager.kt) — Time-windowed leaderboard CRUD over `KeyValueStorage`.

## Building the iOS framework

```bash
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

The Xcode project consumes the framework from `shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework` via a `$(SRCROOT)`-relative path.
