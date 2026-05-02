# ColorMixLab -- Project Guide

## AI-Built Project

This entire project was built using AI tooling (Claude Code by Anthropic). Every line of Kotlin, Swift, and configuration was generated through AI-assisted development, demonstrating what's possible with modern AI coding tools for cross-platform mobile development.

## Overview

ColorMixLab is a Kotlin Multiplatform (KMP) color-mixing game targeting Android (Jetpack Compose) and iOS (SwiftUI). All game logic lives in the `shared` module; platform apps are thin UI wrappers.

## Modules

| Module | Purpose | Language |
|--------|---------|----------|
| `shared/` | Game logic, models, platform abstractions | Kotlin (commonMain/androidMain/iosMain) |
| `app/` | Android UI, ViewModel, theme, utilities | Kotlin + Jetpack Compose |
| `iosApp/ColorMixLab/` | iOS UI, ViewModel, utilities | Swift + SwiftUI |

## Key Classes

- `GameController` -- central game state machine, all game actions
- `GameState` -- immutable data class, drives UI via StateFlow
- `GameColor` -- sealed class defining color palette with tier-based unlocking
- `ColorMixer` -- RGB averaging and similarity calculation
- `LevelManager` -- target color generation with complexity scaling
- `MathQuestionGenerator` -- multiplication questions with multi-strategy distractors
- `LeaderboardManager` -- CRUD for local leaderboard with time-windowed queries

## Build Commands

```bash
# Full build
./gradlew build

# Unit tests
./gradlew test

# Android install
./gradlew installDebug

# iOS shared framework
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

## Patterns

- **State management**: `MutableStateFlow<GameState>` in GameController, exposed as `StateFlow`
- **KMP platform abstractions**: `expect class` in commonMain with `actual class` per platform
- **Android ViewModel**: thin wrapper delegating to shared GameController
- **iOS observation**: Timer-based polling of Kotlin StateFlow (bridge limitation)
- **Color model**: `PlatformColor` (UInt ARGB) in shared code, converted to platform Color in UI

## Testing

Tests are in `app/src/test/` and cover shared module classes through the Android test runner. Run with `./gradlew test`.
