# ColorMixLab — Built Entirely with AI

## The Story

This project was built from scratch using [Claude Code](https://docs.anthropic.com/en/docs/claude-code) by Anthropic. **Every line of code** — Kotlin, Swift, Gradle configuration, CI workflows, and even this document — was generated through AI-assisted development.

### What made this interesting

- **Cross-platform from day one.** The project started as a simple Android color-mixing game and evolved into a full Kotlin Multiplatform (KMP) app with an iOS port. The AI handled the KMP migration, including `expect`/`actual` platform abstractions, shared game logic, and native UI on both platforms.

- **The iOS port was the hardest part.** Bridging Kotlin `StateFlow` to SwiftUI required a pragmatic workaround — timer-based polling at 100ms intervals — because clean `StateFlow`-to-`AsyncSequence` bridging (via SKIE or KMP-NativeCoroutines) would have added significant dependency complexity. The AI identified this trade-off and chose the simpler path.

- **Math challenge distractors required iteration.** Generating plausible wrong answers for multiplication questions (not just random numbers, but near-misses, off-by-one factor errors, and squared-factor traps) took multiple prompt cycles to get pedagogically sound.

- **Animation performance was a recurring challenge.** The game completion celebration (multi-phase particle explosions with 50+ animated elements) went through several iterations to avoid frame drops — moving from Compose recomposition to direct `DrawScope` rendering with pre-allocated arrays.

- **The project restructuring was done by AI too.** The cleanup of 54 development markdown files, removal of dead code, thread-safety fixes (`StateFlow.update {}` over non-atomic read-modify-write), and build modernization (Gradle version catalog, SDK bumps) were all AI-driven.

### What the AI got right on the first try

- MVVM architecture with shared `GameController` as single source of truth
- Color mixing algorithm (RGB averaging with similarity scoring)
- Level progression with tier-based color unlocking
- Leaderboard with time-windowed queries (today/week/month/all-time)
- Haptic feedback integration on both platforms

### Lessons learned

1. **AI excels at cross-platform consistency.** Having the same AI generate both the Android and iOS implementations meant the game logic, scoring, and UX stayed in sync without manual coordination.
2. **Code review is still essential.** The AI produced working code quickly, but left behind dead code, debug logs, and thread-safety issues that required a dedicated cleanup pass.
3. **AI-generated development notes pile up fast.** The initial build produced 54 markdown files documenting every change — useful during development, but clutter for a public repo. These were archived to `docs/dev-notes/`.

---

## Technical Overview

ColorMixLab is a Kotlin Multiplatform (KMP) color-mixing game targeting Android (Jetpack Compose) and iOS (SwiftUI). All game logic lives in the `shared` module; platform apps are thin UI wrappers.

### Modules

| Module | Purpose | Language |
|--------|---------|----------|
| `shared/` | Game logic, models, platform abstractions | Kotlin (commonMain/androidMain/iosMain) |
| `app/` | Android UI, ViewModel, theme, utilities | Kotlin + Jetpack Compose |
| `iosApp/ColorMixLab/` | iOS UI, ViewModel, utilities | Swift + SwiftUI |

### Key Classes

- `GameController` — central game state machine, all game actions (atomic state updates via `StateFlow.update {}`)
- `GameState` — immutable data class with 21 properties, drives UI via `StateFlow`
- `GameColor` — sealed class defining 15+ colors organized into 6 unlock tiers
- `ColorMixer` — RGB averaging and Euclidean similarity calculation
- `LevelManager` — target color generation with complexity scaling (2-5 colors per recipe)
- `MathQuestionGenerator` — multiplication questions with 6 distractor strategies (near-misses, off-by-one factors, squared factors, nearby multiples, random fallback)
- `LeaderboardManager` — CRUD for local leaderboard with time-windowed queries

### Architecture Patterns

- **State management**: `MutableStateFlow<GameState>` with atomic `update {}` in GameController
- **KMP platform abstractions**: `expect class` in commonMain with `actual class` per platform (HapticProvider, PlatformStorage, SoundProvider)
- **Android ViewModel**: thin wrapper converting `StateFlow` to Compose `State`
- **iOS observation**: Timer-based polling of Kotlin StateFlow at 100ms (pragmatic bridge for KMP)
- **Color model**: `PlatformColor` (UInt ARGB) in shared code, converted to platform-native Color in UI layer

### Build Commands

```bash
./gradlew build           # Full build
./gradlew test            # Unit tests
./gradlew installDebug    # Install on Android device

# iOS shared framework
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

### Testing

Tests are in `app/src/test/` and cover shared module classes through the Android test runner. Coverage includes GameController (25 tests), ColorMixer, LevelManager, GameState, MathQuestionGenerator, LeaderboardEntry, and MathChallengeTimer. Run with `./gradlew test`.
