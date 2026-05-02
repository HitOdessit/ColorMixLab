# Reproducing ColorMixLab with AI

This document contains **example prompts** that would rebuild major parts of this project from scratch using [Claude Code](https://docs.anthropic.com/en/docs/claude-code) or any sufficiently capable AI coding assistant.

These are **not** the exact prompts originally used to build ColorMixLab — those are lost to history and would be too long, conversational, and personal to publish. Instead, this is a curated set of "what you could ask the AI to get something like this," organized by feature.

If you're curious whether AI-assisted mobile development is real or hype: clone an empty repo, run through these prompts, and see for yourself.

## Setup phase

### 1. Initialize the KMP project

```
Initialize a Kotlin Multiplatform project targeting Android (Jetpack Compose,
Material 3) and iOS (SwiftUI). Structure:
- app/ for the Android application module
- shared/ for the KMP shared module with iOS frameworks (x64, arm64, simulator arm64)
- iosApp/ for the iOS Xcode project consuming the shared framework

Use a Gradle version catalog at gradle/libs.versions.toml. Set compileSdk 35,
minSdk 24, JVM target 1.8. Include kotlinx-coroutines, kotlinx-serialization-json,
and kotlinx-datetime in the shared module.
```

## Game logic phase (shared module)

### 2. Core color mixing model

```
In the shared module commonMain, create:
- A `PlatformColor` data class wrapping a UInt ARGB value, with companion
  constants for White/Black/Transparent and accessors for red/green/blue/alpha
  as both Int (0-255) and Float (0.0-1.0).
- A `GameColor` sealed class organizing 15+ named colors into tiers, where
  each tier unlocks at a specific level (4, 7, 10, 13, 16, 19). The base
  tier (level 1) always contains Red, Blue, Green. Provide companion
  functions to initialize a game with one randomly selected color per tier
  (seeded for reproducibility) and to query available colors at a given level.
- A `ColorMixer` object with `mixColors(drops: Map<GameColor, Int>): PlatformColor`
  that averages the RGB components weighted by drop counts, plus
  `calculateSimilarity(target, mixed): Float` returning 0.0-1.0 based on
  Euclidean distance in RGB space.
```

### 3. Game state and controller

```
Create `GameState` as an immutable data class with all the fields the UI needs:
current level, score, target color, recipe, mixed color, drops map,
unlocked colors, similarity, timer state, math challenge flags, etc.
30 levels max.

Create `GameController` as the single source of truth, exposing
`gameState: StateFlow<GameState>`. Every mutation goes through
`MutableStateFlow.update { }` to be atomic. Provide methods:
- addColorDrop(color), clearBowl(), checkMatch()
- nextLevel() — gates levels 4, 7, 10, 13, 16, 19 behind a math challenge
- retryLevel(), resetGame(), forceFinishGame()
- pauseTimer(), resumeTimer(), tickTimer()
- calculatePoints(similarity), getResultMessage(similarity), getResultEmoji(similarity)

Difficulty multipliers: Easy 0.75x, Medium 1.0x, Hard 1.25x. Time bonus up to
50 points scales with remaining seconds. 75-point penalty for wrong math answers.
```

### 4. Math challenge generator

```
Create `MathQuestionGenerator` that produces multiplication questions with
plausible distractors. Each question has:
- Two factors (multiplier1 from a difficulty-specific times table, multiplier2 in 2-12)
- One correct answer
- 8 wrong answers using 6 generation strategies:
  1. Near misses (correct ± 1 to 5)
  2. Off-by-one factor errors (multiplier1 ± 1) × multiplier2
  3. Squared factor (multiplier1 × multiplier1)
  4. Nearby multiples of multiplier1 or multiplier2
  5. Common confusions (e.g., addition instead of multiplication)
  6. Random integers in [1, 150] as fallback
- All 9 options shuffled

Easy uses tables {2, 5, 10}; Medium uses {3, 4, 9, 11}; Hard uses {6, 8}.
After level 20, add tables {7, 12} to all difficulties.
```

### 5. Cross-platform leaderboard

```
Create a `KeyValueStorage` interface in commonMain (saveString/getString/remove/clear),
and `expect class PlatformStorage` with that interface.

Provide actuals: Android using SharedPreferences, iOS using NSUserDefaults.

Then implement `LeaderboardManager(storage: KeyValueStorage)` that persists
`LeaderboardEntry` objects (nickname, score, level, difficulty, timestamp) as
JSON. Cap at 100 entries. Provide time-windowed queries: today (since local
midnight), this week (since Monday midnight), this month (since 1st), all-time.
Implement Comparable on entries: score desc, level desc, timestamp asc.
```

## UI phase (Android)

### 6. Compose UI scaffolding

```
In the app module, create:
- MainActivity hosting Compose with Material 3 theme.
- A sealed Screen hierarchy (Intro, MathChallenge, Game) for type-safe navigation
  via a `when` block.
- IntroScreen with difficulty selector cards (Easy/Medium/Hard) and a Start button.
  Include a leaderboard preview accessed via a Star button. Add a hidden
  "completion celebration" preview triggered by double-tapping the version label.
- GameScreen with portrait and landscape layouts. Show: target color swatch,
  mixing bowl (animated pie-chart visualization), level/score header, timer,
  color buttons grid, Check Match and Clear Bowl buttons.
- MathChallengeScreen showing a 3x3 grid of answer options with shake-on-wrong
  and pulse-on-correct animations. Auto-advance after correct answers.
```

### 7. Game completion celebration

```
Create a `GameCompletionCelebration` Composable that renders a 10-second
multi-phase particle animation when the player completes level 30:

Phase 1 (0-2s): "appear" — particles spawn from bowl center.
Phase 2 (2-5s): "fly" — particles arc outward.
Phase 3 (5-8s): "spiral" — converge into rainbow ring patterns.
Phase 4 (8-10s): "explode" — final burst with sparkles.

Performance requirements:
- Single Animatable driving master clock, no per-particle animateFloatAsState
- Pre-allocated FloatArray buffers for particle positions
- All rendering inside a single Canvas { } using DrawScope, no recomposition per frame
- Target 60fps on mid-tier devices

Overlay a CelebrationText component that fades in "You completed all 30 levels!"
with a manual spring easing approximation animating via graphicsLayer.
```

## iOS phase

### 8. iOS app scaffolding

```
In iosApp/ColorMixLab/, create an Xcode project that consumes the shared
framework from ../../shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework
via a $(SRCROOT)-relative path.

Create a SwiftUI app with NavigationStack hosting Intro, Game, and MathChallenge
screens. Mirror the Android UX: difficulty selection, color mixing with pie-chart
bowl, math challenges, completion celebration, leaderboard with time tabs.

Bridge the Kotlin GameController's StateFlow to SwiftUI via a 100ms Timer
that polls `gameController.gameState.value` and republishes via @Published.
Document this trade-off explicitly — alternatives like SKIE add significant
build complexity for a small UX gain.
```

## Tooling phase

### 9. Quality and CI

```
Add to the project:
- Detekt static analysis with custom config (LongMethod 80, complex method 20,
  forbidden TODO/FIXME comments, formatting disabled).
- Spotless with ktlint for Kotlin formatting.
- Kover for code coverage with HTML and XML reports.
- A GitHub Actions workflow that runs on push/PR to master:
  - quality job: spotlessCheck, detekt
  - build job: full ./gradlew build (skipping iOS framework linking on Linux),
    Codecov upload of Kover XML, debug APK artifact upload
- A separate CodeQL workflow scanning Kotlin/Java weekly + on push.
- A release workflow triggered by v* tags that builds release+debug APKs and
  attaches them to a GitHub Release.
- Dependabot config for weekly Gradle and GitHub Actions updates.
```

### 10. Tests

```
Add unit tests in app/src/test/ for:
- GameController — full game flow including math challenge gating, scoring,
  timer expiry, force-finish.
- LeaderboardManager — using an in-memory KeyValueStorage fake. Cover CRUD,
  ranking edge cases, capacity, corruption recovery.
- ColorMixer — empty drops, single color, weighting, all base colors, large
  drop counts, similarity properties (range, symmetry, identity).
- LevelManager — recipe complexity per level tier, color availability,
  variety across generations.
- MathQuestionGenerator — invariants (9 options, correct answer present,
  all unique), difficulty-specific times tables, shuffling.
- LeaderboardEntry — Comparable contract, serialization roundtrip.

Use plain JUnit 4 + kotlin-test. No mocking framework needed.
```

---

## Notes on actually using these prompts

- These are starting points, not magic incantations. Expect to iterate, especially on UI layout and animation.
- The AI will sometimes invent APIs that don't exist. Read the generated code before running it.
- Keep prompts focused. "Build a color-mixing game for me" produces worse results than the targeted prompts above.
- Save the dev notes the AI generates along the way (see `docs/dev-notes/`) — they're useful for tracking what was actually built vs. what you asked for.
