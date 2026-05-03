# ADR-0001: Adopt Kotlin Multiplatform for cross-platform game logic

## Status

Accepted

## Context

The project targets both Android and iOS. The game logic (state machine, scoring, level progression, color mixing, math challenge generation, leaderboard CRUD) is identical across platforms. We need to decide where this logic lives and how much UI code we share.

Options considered:

1. **Pure native, no sharing.** Write the same logic twice in Kotlin and Swift.
2. **Flutter or React Native.** Share both logic and UI rendering via a cross-platform framework.
3. **Kotlin Multiplatform (KMP) with native UI.** Share game logic in Kotlin; write UI separately in Compose and SwiftUI.
4. **Compose Multiplatform.** Share both logic and Compose UI; render Compose on iOS via Skia.

## Decision

Adopt **Kotlin Multiplatform with native UI** (option 3).

Game logic, models, and platform abstractions live in `shared/src/commonMain`. Platform-specific actuals (Android `SharedPreferences`, iOS `NSUserDefaults`, etc.) live in `androidMain` and `iosMain`. Each app module renders its own UI: Compose on Android, SwiftUI on iOS.

## Consequences

**Positive:**

- Game logic stays in sync without manual coordination — both platforms consume the same `GameController` and `GameState`.
- Each platform uses its native UI primitives (Compose animations, SwiftUI navigation, native haptics) without compromise.
- Test once: shared logic is unit-tested via the Android test runner; results apply to both platforms.

**Negative:**

- iOS observation of Kotlin `StateFlow` requires a bridge (see ADR-0003).
- Kotlin/Native compilation can be slow; CI builds take 2-3 minutes longer than a pure Android project would.
- Some IDE integration (debugging across the JVM/Native boundary) is rougher than pure native.
- UI duplication: ~4500 LOC of Compose UI on Android vs ~2000 LOC of SwiftUI on iOS. Only ~13-18% of cross-platform code is shared today. The opportunity to extract more UI-driving state into shared remains open.

**Rejected alternatives:**

- **Pure native.** Logic divergence over time is inevitable; tests would have to be duplicated; the project loses its cross-platform value proposition.
- **Flutter / React Native.** Both platforms render through a non-native UI layer. Kid-friendly UX benefits from native animation primitives and native haptics. Dependency on a separate runtime increases binary size and adds platform-specific debugging complexity.
- **Compose Multiplatform.** Compose-on-iOS via Skia is still maturing. Native SwiftUI ages better and gives the iOS app a properly native feel. Revisit when Compose Multiplatform is stable on iOS.
