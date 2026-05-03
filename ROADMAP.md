# Roadmap

This document covers what's deliberately scoped *out* of v1, what's intentionally on the back burner, and what's a credible next step. It is not a feature wishlist.

The repo is a portfolio piece demonstrating end-to-end AI-assisted mobile development. v1's bar is *"a working cross-platform game with credible engineering practices, no production polish."* Items below are sorted by signal-to-effort ratio for that bar.

## Deliberate scope decisions in v1

These are calls made on purpose, not gaps to fill.

### Native UI on each platform, not Compose Multiplatform UI

Game logic shipped to `commonMain` (~1,000 LOC). UI did not. The Android app is Jetpack Compose; the iOS app is SwiftUI; both observe the same `StateFlow<GameState>` from shared.

Why: the goal was native platform feel — Material 3 motion on Android, SwiftUI animation primitives on iOS — not a single shared rendering layer. Compose-on-iOS is maturing but still imposes cost on animation fidelity and platform-idiomatic gesture handling. KMP is the right tool when shared logic + native UI is the goal; if shared rendering were the goal, Flutter or Compose Multiplatform would have been chosen instead.

The result is that ~13–18% of total LOC is shared today. That's the intended ratio for this scope, not a missed target.

### iOS state observation via 100ms polling, not SKIE

Bridging Kotlin `StateFlow` to SwiftUI cleanly requires [SKIE](https://skie.touchlab.co/) or [KMP-NativeCoroutines](https://github.com/rickclephas/KMP-NativeCoroutines). Both add Gradle plugin and compiler-plugin complexity. For a turn-based color-mixing game, a `Timer.publish(every: 0.1)` polling `gameController.gameState.value` is well below the perception threshold and adds zero dependencies. Documented in [ADR-0003](docs/adr/0003-ios-stateflow-polling-bridge.md).

If the number of `StateFlow`s grows, or if real-time UI requirements emerge, switch to SKIE.

### Local leaderboard, no server, no accounts

No backend, no PII, no analytics, no sign-in. The leaderboard is `NSUserDefaults` / `SharedPreferences`. This is a feature, not a gap — it keeps the app fully offline-verifiable from the manifest.

### Single-language UI

All user-facing strings are English. Localization machinery (Android string resources, iOS string catalogs) is not exercised in v1. Adding one additional language is a credible next step for a real product; for a portfolio piece showcasing KMP and animation it would be ceremony.

## Acknowledged trade-offs (not done in v1)

These are real gaps a working engineer would close before shipping commercially. Listed honestly so reviewers don't have to find them.

### Sound effects

`SoundProvider` exists with `expect`/`actual` declarations. The actual implementations have `loadSound` calls commented out and the app falls back to system sounds. Closing this is mostly content (sourcing sound files) and uncommenting the wired calls.

### iOS test target

Shared module logic is covered by the Android JUnit runner (100+ tests). iOS-specific code — `GameStateExtensions.swift`, the polling bridge, `LeaderboardViewModel`, the custom `==` operator — has no test target. Adding an `iosAppTests` Xcode target is straightforward; it's been deferred because shared-logic coverage already addresses the highest-risk surface.

### R8 / ProGuard

Release builds have `isMinifyEnabled = false` and `proguard-rules.pro` is empty. The release APK is identical in size to the debug APK. Enabling R8 and adding rules for `kotlinx.serialization` (`LeaderboardEntry` is `@Serializable`) is a 1–2 hour task. Typical APK reduction: 20–40%.

### iPad-specific layouts on iOS

The iOS app has no `horizontalSizeClass` or `UIDevice.userInterfaceIdiom` checks. On iPad, the iPhone layout scales up — functional but not polished. Android already has `LandscapeGameLayout` / tablet adaptations; mirroring that on iOS is the work.

### Expand snapshot test coverage

`@Preview` annotations and Paparazzi snapshot tests now exist for `MixingBowl`, `ColorButton`, `TargetColor`, `MathAnswerButton`, and `ResultDialogContent` — see [docs/snapshot-tests.md](docs/snapshot-tests.md). The next step is screen-level snapshots (`GameScreen`, `IntroScreen`, `MathChallengeScreen`), which require a `ViewModel` test seam not yet exposed.

## Next-step extractions (when picking this up again)

### Continue moving logic to `commonMain`

The iOS `GameViewModel` re-implements behavior that already exists in the shared `GameController` (state extension methods, force-cast Kotlin collections, `==` overrides). Most of this could be deleted or pushed down. Concrete targets:

- **Math challenge orchestration.** `MathQuestionGenerator` is shared; the challenge flow (question sequencing, correct/wrong handling, auto-advance timing) lives partly in the platform view models. Consolidate into a `MathChallengeController` in shared.
- **Layout-agnostic view-state.** A common "view-state" projection in `commonMain` would let both platforms observe a UI-shaped struct rather than re-deriving it.

### Performance benchmarks

Macrobenchmark module measuring app startup, frame timing during gameplay, and the 10-second celebration animation. The project currently relies on observed visual smoothness on test devices rather than measured percentiles; a benchmark module would close that gap if the project ever needs verifiable perf numbers.

### Release distribution

A signed APK on GitHub Releases (the workflow exists; needs a keystore) and a TestFlight build for iOS. Currently a recruiter has no way to play the game without building it from source.

## Out of scope

- Online multiplayer
- In-app purchases
- Server-side leaderboard
- Compose Multiplatform Web (too immature today; revisit when stable)
