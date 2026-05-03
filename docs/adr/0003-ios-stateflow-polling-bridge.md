# ADR-0003: Bridge Kotlin StateFlow to SwiftUI via 100ms polling

## Status

Accepted

## Context

The shared module exposes game state as a Kotlin `StateFlow<GameState>`. SwiftUI consumes state via `@Published` properties on an `ObservableObject`. There is no built-in interop between these two abstractions: Kotlin `Flow` collected from Swift is technically possible but ergonomically clunky, and `Flow.collect` from a Swift context requires either suspending semantics or callback-based wrappers.

Options considered:

1. **SKIE (Touchlab).** Kotlin compiler plugin that auto-generates Swift-native APIs for Kotlin classes, including `AsyncSequence` adapters for `StateFlow`.
2. **KMP-NativeCoroutines.** Compiler plugin that generates `@Published`-compatible publishers from `StateFlow`.
3. **Manual callback wrapper.** Have the shared module accept a Swift callback and invoke it on every state change.
4. **Polling.** Read `gameController.gameState.value` on a Swift `Timer.publish(...)`, republish via `@Published`.

## Decision

Use **option 4: 100ms polling** in the iOS `GameViewModel`:

```swift
stateObserver = Timer.publish(every: 0.1, on: .main, in: .common)
    .autoconnect()
    .sink { [weak self] _ in
        guard let self = self else { return }
        let newState = self.gameController.gameState.value
        if newState != self.gameState {
            self.gameState = newState
        }
    }
```

## Consequences

**Positive:**

- Zero additional dependencies. No compiler plugins, no extra Gradle setup, no Kotlin version lock-in.
- Trivial to reason about. Anyone reading the iOS view model immediately understands what's happening.
- Works on any KMP version.

**Negative:**

- Up to 100ms of UI lag between a state change and the SwiftUI render. For a turn-based color-mixing game where the smallest meaningful interaction is "add a drop," this is below the perception threshold (humans typically perceive UI lag above ~150ms).
- Wakes the main runloop 10 times per second, even when nothing is happening. CPU cost is negligible but non-zero.
- A custom `==` operator on `GameState` is required (Kotlin data classes don't auto-bridge equality to Swift). Currently this checks a subset of fields and is a known fragility — if a field that affects the UI is added but not added to the equality check, the UI won't update.

**Rejected alternatives:**

- **SKIE.** Adds a build-time dependency on Kotlin compiler internals. Locks the project to specific Kotlin versions. Significant Gradle plugin setup. Worth it for a complex multi-flow app; overkill for one `StateFlow`.
- **KMP-NativeCoroutines.** Same complexity concerns as SKIE. The plugin is high-quality but the cost-benefit doesn't favor it for a single `StateFlow`.
- **Manual callback wrapper.** Would push state-management complexity into the shared module just to support iOS, polluting the cross-platform abstraction.

## Re-evaluation criteria

Switch to SKIE or KMP-NativeCoroutines if:

- The number of `StateFlow`s (or other reactive streams) in the shared module grows beyond ~3.
- Real-time UI requirements emerge (e.g., a multiplayer mode where 100ms lag would be noticeable).
- The custom `==` operator on `GameState` becomes a recurring source of bugs.
