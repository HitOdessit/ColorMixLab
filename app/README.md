# `app/` — Android application

Android app module. Hosts the Jetpack Compose UI and a thin `GameViewModel` that delegates to the shared `GameController`.

## Layout

```
src/main/java/com/colormixlab/
├── MainActivity.kt           # Sealed Screen routing (Intro / MathChallenge / Game)
├── game/                     # GameViewModel — thin wrapper around shared.GameController
├── ui/
│   ├── GameScreen.kt         # Main gameplay screen
│   ├── IntroScreen.kt        # Difficulty selection + leaderboard preview
│   ├── MathChallengeScreen.kt
│   ├── LeaderboardDialog.kt
│   ├── components/           # Reusable composables
│   ├── dialogs/              # Menu, Result, Nickname dialogs
│   └── theme/                # Material 3 theme
└── utils/                    # HapticManager, color extensions
src/test/                     # JUnit unit tests for shared logic
```

## Key entry points

- [`MainActivity.kt`](src/main/java/com/colormixlab/MainActivity.kt) — Single Activity. Holds Screen routing state.
- [`GameScreen.kt`](src/main/java/com/colormixlab/ui/GameScreen.kt) — Main gameplay loop.
- [`GameViewModel.kt`](src/main/java/com/colormixlab/game/GameViewModel.kt) — Bridge from `StateFlow<GameState>` to Compose `State`.

## Tests

Tests live in `src/test/`. Run with `./gradlew :app:test` or `./gradlew test` from the repo root.
