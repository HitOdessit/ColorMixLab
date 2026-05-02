# ADR-0005: Sealed `Screen` hierarchy for top-level navigation

## Status

Accepted

## Context

The Android app has three top-level screens: Intro, MathChallenge, Game. Initial implementation used raw strings (`"intro"`, `"mathChallenge"`, `"game"`) in a `var currentScreen: String` and routed via `when (currentScreen)`.

Problems with string-based routing:

- A typo silently breaks navigation at runtime with no compile-time warning.
- Adding a new screen doesn't force the `when` block to add a branch — unhandled values fall through.
- IDE refactoring tools cannot rename a "screen" because there is no symbol, just text.

Options considered:

1. **Continue with raw strings.**
2. **Use the Jetpack Navigation Compose library.**
3. **Use a sealed hierarchy.**

## Decision

Adopt **option 3** — a sealed hierarchy:

```kotlin
sealed interface Screen {
    data object Intro : Screen
    data object MathChallenge : Screen
    data object Game : Screen
}
```

Navigation state is `var currentScreen: Screen by mutableStateOf(Screen.Intro)`. The routing `when` block is exhaustive — adding a new screen is a compile error until handled.

## Consequences

**Positive:**

- Compile-time exhaustiveness on the `when` block.
- Refactor-safe: renaming `Screen.Game` updates every reference automatically.
- Low ceremony — no extra dependency, no `NavGraph` setup, no destination IDs.

**Negative:**

- Doesn't support back-stack semantics. If we add a fourth or fifth screen with non-trivial navigation flows (deep linking, conditional back behavior, transitions), we'll likely need Navigation Compose.
- Doesn't support deep links or system back-button handling beyond what we wire up manually.

**Rejected alternatives:**

- **Raw strings.** Already discussed above.
- **Navigation Compose.** Overkill for three screens with linear progression. Adds setup, terminology, and a dependency. Reconsider if the navigation graph grows.
