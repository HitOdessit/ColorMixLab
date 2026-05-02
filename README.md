# Color Mix Lab

[![Build](https://github.com/HitOdessit/ColorMixLab/actions/workflows/android.yml/badge.svg)](https://github.com/HitOdessit/ColorMixLab/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue.svg)](https://kotlinlang.org)
[![Swift](https://img.shields.io/badge/Swift-5.9-orange.svg)](https://swift.org)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> **Built entirely with AI.** Every line of code in this project was generated using AI tooling (Claude Code). No code was written by hand. See [CLAUDE.md](CLAUDE.md) for details.

A cross-platform educational color-mixing game for kids ages 7-11, built with Kotlin Multiplatform and native UI frameworks (Jetpack Compose for Android, SwiftUI for iOS).

## Features

- **30 progressive levels** with increasing complexity (2-5 color recipes)
- **Math challenges** as gating mechanics between color tiers (multiplication questions with plausible distractors)
- **Three difficulty modes** (Easy, Medium, Hard) with timed gameplay on Medium/Hard
- **Local leaderboard** with time-filtered views (Today, This Week, This Month, All Time)
- **Game completion celebration** with multi-phase particle animations
- **Haptic feedback** on interactions (color drops, match results, timer warnings)
- **Adaptive layouts** for portrait, landscape, phones, and tablets
- **No ads, no analytics, no tracking** -- pure offline gameplay
- **Cross-platform** -- shared game logic via Kotlin Multiplatform

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Shared Module (KMP)               │
│  ┌───────────┐  ┌────────────┐  ┌────────────────┐  │
│  │ GameState  │  │ GameColor  │  │ MathQuestion   │  │
│  │ Controller │  │ ColorMixer │  │ Generator      │  │
│  │ LevelMgr   │  │ Leaderboard│  │ Timer Config   │  │
│  └───────────┘  └────────────┘  └────────────────┘  │
│  ┌─────────────────────────────────────────────────┐ │
│  │ Platform Abstractions (expect/actual)           │ │
│  │ HapticProvider · PlatformStorage · SoundProvider│ │
│  └─────────────────────────────────────────────────┘ │
└────────────────┬──────────────────┬──────────────────┘
                 │                  │
    ┌────────────▼──────┐  ┌───────▼────────────┐
    │  Android App      │  │  iOS App           │
    │  Jetpack Compose  │  │  SwiftUI           │
    │  Material 3       │  │  NavigationStack   │
    │  ViewModel        │  │  ObservableObject  │
    └───────────────────┘  └────────────────────┘
```

## Project Structure

```
ColorMixLab/
├── app/                              # Android application
│   ├── src/main/java/com/colormixlab/
│   │   ├── MainActivity.kt
│   │   ├── game/                     # ViewModel (thin wrapper over shared GameController)
│   │   ├── ui/                       # Compose screens and dialogs
│   │   │   ├── components/           # Reusable UI (MixingBowl, ColorButton, animations)
│   │   │   ├── dialogs/              # Menu, Result, Nickname dialogs
│   │   │   └── theme/                # Material 3 theme
│   │   └── utils/                    # Platform utilities (haptics, sound, color conversion)
│   └── src/test/                     # Unit tests
├── shared/                           # Kotlin Multiplatform shared module
│   └── src/
│       ├── commonMain/               # Cross-platform game logic
│       │   └── kotlin/com/colormixlab/
│       │       ├── game/             # GameController, GameState, ColorMixer, LevelManager
│       │       ├── model/            # GameColor, LeaderboardEntry, PlatformColor
│       │       ├── data/             # LeaderboardManager
│       │       ├── platform/         # expect declarations (Storage, Haptics, Sound)
│       │       └── utils/            # MathChallengeTimer
│       ├── androidMain/              # Android actual implementations
│       └── iosMain/                  # iOS actual implementations
├── iosApp/ColorMixLab/               # iOS application (Xcode project)
│   └── ColorMixLab/
│       ├── UI/Screens/               # SwiftUI screens
│       ├── UI/Components/            # Reusable SwiftUI components
│       └── Utilities/                # iOS utilities
└── docs/
    ├── assets/                       # Design assets (app icon SVG)
    └── dev-notes/                    # Historical development notes
```

## Game Mechanics

### Color Progression

| Levels | Available Colors | Unlocked At |
|--------|-----------------|-------------|
| 1-3    | Red, Blue, Green | Start |
| 4-6    | + 1 from: Yellow, Cyan, Gray | Level 4 (after math challenge) |
| 7-9    | + 1 from: Orange, Magenta, Coral | Level 7 (after math challenge) |
| 10-12  | + 1 from: Purple, Lime, Turquoise | Level 10 (after math challenge) |
| 13-15  | + 1 from: Pink, Teal | Level 13 (after math challenge) |
| 16-18  | + 1 from tier 5 pool | Level 16 (after math challenge) |
| 19-30  | + 1 from tier 6 pool | Level 19 (after math challenge) |

Each game session randomly selects one color from each tier, so the palette varies between playthroughs.

### Difficulty Modes

| Mode | Timer | Math Challenge Timer | Target Complexity |
|------|-------|---------------------|-------------------|
| Easy | None | None | 2-5 colors |
| Medium | 40s per level | 20s per question | 2-5 colors |
| Hard | 20s per level | 10s per question | 2-5 colors |

### Scoring

- Base points scale with color similarity (80-100% match required)
- Difficulty multiplier: Easy x1, Medium x2, Hard x3
- Time bonus on Medium/Hard for quick matches

## Building

### Prerequisites

- **Android**: Android Studio (2023.1+), JDK 17+, Android SDK 35
- **iOS**: Xcode 15+, macOS

### Android

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Install on connected device
./gradlew installDebug
```

### iOS

1. Build the shared KMP framework:
   ```bash
   ./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
   ```
2. Open `iosApp/ColorMixLab/ColorMixLab.xcodeproj` in Xcode
3. Build and run on simulator or device

## Testing

Unit tests cover the shared game logic:

```bash
./gradlew test
```

Test coverage includes:
- **GameController** -- core game flow (add drops, check match, advance levels, scoring, timer)
- **ColorMixer** -- color mixing algorithm, similarity calculation
- **LevelManager** -- target generation, level complexity scaling
- **GameState** -- state defaults, helper methods
- **MathQuestionGenerator** -- question generation, distractor quality, difficulty scaling
- **LeaderboardEntry** -- sorting, serialization
- **MathChallengeTimer** -- timer configuration per difficulty

## Screenshots

> Screenshots coming soon. To capture your own, run the app on an emulator and take screenshots of the intro screen, gameplay, math challenge, and game completion.

## License

This project is licensed under the MIT License -- see [LICENSE](LICENSE) for details.
