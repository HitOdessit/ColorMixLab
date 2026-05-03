# Project context for Claude Code

This file is auto-loaded by Claude Code when working on this repository. It is a *technical* context document — not the public retrospective. For the AI-build retrospective and lessons learned, see [RETROSPECTIVE.md](RETROSPECTIVE.md).

## What this project is

ColorMixLab is a Kotlin Multiplatform (KMP) color-mixing game targeting Android (Jetpack Compose) and iOS (SwiftUI). Game logic lives in the `shared` module; platform apps are thin UI wrappers over a single `GameController` exposed as `StateFlow<GameState>`.

## Modules

| Module | Purpose | Language |
|--------|---------|----------|
| `shared/` | Game logic, models, platform abstractions | Kotlin (commonMain / androidMain / iosMain) |
| `app/` | Android UI, ViewModel, theme, utilities | Kotlin + Jetpack Compose |
| `iosApp/ColorMixLab/` | iOS UI, ViewModel, utilities | Swift + SwiftUI |

## Key classes

- `GameController` — central state machine. All mutations go through `_gameState.update { }` for atomicity.
- `GameState` — immutable data class consumed by both platforms via `StateFlow`.
- `GameColor` — sealed class, 15+ colors organized into 6 unlock tiers.
- `ColorMixer` — RGB averaging + Euclidean similarity scoring.
- `LevelManager` — target color generation with complexity scaling (2–5 colors per recipe).
- `MathQuestionGenerator` — multiplication questions with 6 distractor strategies.
- `LeaderboardManager` — local leaderboard CRUD with time-windowed queries.

## Architecture conventions

- **State mutations** in `GameController` use `_gameState.update { state -> state.copy(...) }`, never `_gameState.value = _gameState.value.copy(...)`. The latter is a non-atomic read-modify-write under concurrent timer + UI mutations. See [ADR-0002](docs/adr/0002-atomic-stateflow-updates.md).
- **KMP platform services** use `expect class` in commonMain with `actual class` per platform: `HapticProvider`, `PlatformStorage`, `SoundProvider`. `KeyValueStorage` interface adapter exists so `LeaderboardManager` is testable with an in-memory fake.
- **Android ViewModel** is a thin wrapper that converts `StateFlow<GameState>` to Compose `State`.
- **iOS observation** uses 100ms polling of `gameController.gameState.value` republished via `@Published`. See [ADR-0003](docs/adr/0003-ios-stateflow-polling-bridge.md) for why polling instead of SKIE.
- **Color model** is `PlatformColor` (UInt ARGB) in shared code, converted to platform-native `Color` at the UI layer.

## Build commands

```bash
./gradlew build              # Full build
./gradlew test               # Unit tests
./gradlew installDebug       # Install on connected Android device
./gradlew detekt             # Static analysis
./gradlew spotlessCheck      # Formatting check
./gradlew spotlessApply      # Auto-fix formatting
./gradlew koverHtmlReport    # Coverage report

# iOS shared framework
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

## Tests

Tests live in `app/src/test/` and run via the Android JUnit runner. Coverage spans:

- `GameController` (27 tests) — game flow, scoring, timer, math gating
- `LeaderboardManager` (15 tests) — CRUD, ranking, time windows, corruption recovery
- `ColorMixer` (15 tests) — averaging, similarity properties
- `LevelManager` (18 tests) — complexity scaling, variety
- `MathQuestionGenerator` (17 tests) — invariants, distractor quality
- `GameState`, `LeaderboardEntry`, `MathChallengeTimer` (48 tests)
- `SnapshotTests` (11 Paparazzi snapshots)

Run with `./gradlew test`. Coverage report via `./gradlew koverHtmlReport`.

## Conventions for changes

- Kotlin for new shared / Android code, Swift for new iOS code.
- Keep `commonMain` free of platform imports — move platform-specific code behind `expect`/`actual`.
- Prefer immutability and `data class` for state types.
- Always use `_gameState.update { }` for state mutations; never read-then-assign.
- Tests for new shared logic go in `app/src/test/` (Android-runner JVM tests).

## Further reading inside this repo

- [README.md](README.md) — public overview, build instructions, FAQ
- [ARCHITECTURE.md](ARCHITECTURE.md) — module graph, state machine, engineering trade-offs
- [RETROSPECTIVE.md](RETROSPECTIVE.md) — public-facing AI-build retrospective
- [ROADMAP.md](ROADMAP.md) — scope decisions and acknowledged trade-offs
- [docs/adr/](docs/adr/) — Architecture Decision Records
- [docs/reproduce.md](docs/reproduce.md) — example prompts to rebuild from scratch
