# Roadmap

This document tracks high-value work that's not done yet. Items are not promises; they're known opportunities ranked by value.

## In scope

### Continue KMP extraction

Today, the `shared/` module contains ~1,000 LOC of game logic. The Android app has ~4,500 LOC of Compose UI and the iOS app has ~2,000 LOC of SwiftUI — meaning **only ~13-18% of cross-platform code is actually shared**. The KMP migration started but did not complete.

Specific extraction opportunities:
- **MathChallengeState orchestration.** Currently lives partly in shared (`MathQuestionGenerator`) and partly in platform view models. Move the full challenge flow (question sequencing, correct/wrong handling, auto-advance timing) into a shared `MathChallengeController`.
- **Layout-agnostic UI state.** The Compose `GameLayouts` and SwiftUI screen state could share a common "view-state" projection in `commonMain`, reducing duplication.
- **iOS view model duplication.** `iosApp/.../GameViewModel.swift` re-implements logic that already exists in shared `GameController` (state extension methods, force-cast Kotlin collections). Most of this could be deleted.

### Tablet support on iOS

The iOS app has **zero iPad-specific layouts**. There are no references to `UIDevice.userInterfaceIdiom`, `horizontalSizeClass`, or any other iPad-detection mechanism. On iPad, the app likely renders the iPhone layout scaled up — functional but not polished.

What's needed:
- Detect iPad via `@Environment(\.horizontalSizeClass)` in SwiftUI.
- Mirror the Android `LandscapeGameLayout` / tablet adaptations on iOS.
- Test on iPad simulator and verify the math challenge grid and color palette adapt.

### Sound effects

`SoundProvider` exists in the shared module with `expect` declarations and platform `actual` classes — but the actual implementations have all `loadSound` calls commented out. The Android `SoundManager` falls back to system sounds with a `// For now, we'll use system sounds as placeholders` comment. Result: the app is silent.

What's needed:
- Source or generate sound effect files (drop sound, match success, level complete, math correct/wrong, timer warning).
- Add files to `app/src/main/res/raw/` and bundle in iOS app resources.
- Uncomment and wire up the `loadSound` calls in `androidMain` and `iosMain` `SoundProvider` actuals.

### iOS test target

There are **zero iOS unit tests**. The shared module is tested via the Android test runner (which covers the shared logic), but iOS-specific code (`GameStateExtensions.swift`, the polling bridge, custom `==` operator, `LeaderboardViewModel`) is uncovered.

What's needed:
- Add an `iosAppTests` Xcode target.
- Test the Kotlin↔Swift bridge: confirm state transitions propagate, the `==` operator covers all state-affecting fields, `getCurrentMathChallenge` doesn't crash on null.
- Optionally: a `commonTest` source set in the shared module with multi-platform tests.

### ProGuard / R8 rules

Release builds have `isMinifyEnabled = false` and `proguard-rules.pro` is empty. The release APK is therefore identical in size to the debug APK and has no shrinking, optimization, or obfuscation.

What's needed:
- Enable `isMinifyEnabled = true` for release.
- Add ProGuard rules for `kotlinx.serialization` (the `LeaderboardEntry` `@Serializable` class has reflective access from the JSON encoder).
- Verify the release APK still works after R8 strips unused code.
- Measure APK size delta — typically 20-40% reduction.

## Stretch / nice-to-have

### Compose `@Preview` annotations

No Compose composables have `@Preview`. Adding previews to key composables (`MixingBowl`, `ColorButton`, `ResultDialog`, `MathChallengeLayout`) would speed up UI iteration.

### Replace iOS polling bridge with SKIE

[ADR-0003](docs/adr/0003-ios-stateflow-polling-bridge.md) documents the trade-off. Re-evaluate if the number of `StateFlow`s grows or real-time UI requirements emerge.

### Compose Multiplatform UI for the iPad layout

Compose-on-iOS is maturing. For the iPad layout specifically (which doesn't exist yet), we could write it once in Compose Multiplatform instead of writing SwiftUI.

### F-Droid / Play Store metadata

Add `fastlane/metadata/android/en-US/` with proper store descriptions, changelogs, and screenshots. Even without publishing, this signals production-readiness.

### Performance benchmarks

Add a Macrobenchmark module measuring app startup, frame timing during gameplay, and the celebration animation. Publish numbers in the README.

### Localization

All user-facing strings are English. Adding even one additional language demonstrates Android string-resource and iOS string-catalog discipline.

### Analytics / telemetry (opt-in)

For a portfolio piece, intentionally avoiding analytics is a feature. If this ever moved to a real product, opt-in telemetry with proper privacy controls would be added here.

## Out of scope

- **Online multiplayer.** This is an offline single-player game by design.
- **In-app purchases.** Same.
- **Server-side leaderboard.** The local leaderboard is intentional — no accounts, no servers, no PII.
- **Web (Compose Multiplatform Web).** Compose Multiplatform Web is too immature today; revisit when stable.
