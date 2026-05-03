# Snapshot tests (Paparazzi)

This project uses [Paparazzi](https://github.com/cashapp/paparazzi) for Compose snapshot tests. Paparazzi renders composables on the JVM (no emulator required) using a layoutlib that mirrors Android's rendering pipeline, then diffs the output against committed PNG goldens.

The goal is to catch unintended visual regressions in components whose appearance is the contract — color buttons, the mixing bowl, math answer buttons, and the result dialog content. Animation refactors are the highest-risk area; this is what would have caught the celebration animation regressions documented in [docs/dev-notes/04-celebration-animation-optimization.md](dev-notes/04-celebration-animation-optimization.md).

## Components covered

- `MixingBowl` — empty state, three-color state
- `ColorButton` — without drop count badge, with badge
- `TargetColor`
- `MathAnswerButton` — idle, correct selected, wrong selected
- `ResultDialogContent` — success, failure (the `Dialog` wrapper is excluded; see below)

Tests live in [`app/src/test/java/com/colormixlab/ui/SnapshotTests.kt`](../app/src/test/java/com/colormixlab/ui/SnapshotTests.kt).

## Running

```bash
# Record fresh goldens (run after intentional visual changes)
./gradlew :app:recordPaparazziDebug

# Verify against committed goldens (CI mode — fails on diff)
./gradlew :app:verifyPaparazziDebug
```

Goldens are written to `app/src/test/snapshots/` and committed alongside the test source.

## Why `ResultDialogContent` instead of `ResultDialog`

Paparazzi cannot render `androidx.compose.ui.window.Dialog` — it requires a `Window` host that the layoutlib stub does not provide. The fix is the standard Compose pattern: extract the dialog's content into a separate `@Composable` (`ResultDialogContent`) that returns a `Card` directly, and have `ResultDialog` wrap it in `Dialog { }`. Production behavior is unchanged; the snapshotted surface is the same UI minus the system dialog scrim.

## AGP / Paparazzi version compatibility

Paparazzi tracks AGP closely. If `./gradlew :app:recordPaparazziDebug` fails with a version-mismatch error:

| Symptom | Fix |
|---------|-----|
| "Paparazzi requires AGP X.Y+" | Bump `agp` in [`gradle/libs.versions.toml`](../gradle/libs.versions.toml) to the required minimum |
| Class-not-found at runtime, `LayoutlibRenderImpl` etc. | Bump `paparazzi` version in the catalog and re-run with `--refresh-dependencies` |
| Compose Compiler / Kotlin 2.0 errors | Confirm Paparazzi version supports Kotlin 2.0; recent (1.3.5+) does |

Current pinned versions: AGP 8.3.0, Kotlin 2.0.0, Paparazzi 1.3.5. If Paparazzi 1.3.5 requires a newer AGP than 8.3.0 in your local toolchain, bump AGP — the rest of the stack tolerates 8.5+.

## Adding a new snapshot

1. Add a new `@Test fun` to `SnapshotTests.kt` that calls `paparazzi.snapshot { … }`.
2. (Recommended) Add a parallel `@Preview` annotation to the underlying composable so it shows up in Android Studio's preview pane.
3. Run `./gradlew :app:recordPaparazziDebug` once to generate the golden, inspect the PNG, commit it.
4. Subsequent runs of `verifyPaparazziDebug` will fail if the rendered output drifts.

## What's intentionally not snapshotted

- **Animations across time.** Paparazzi captures a single frame; multi-frame animations would need a video-like test harness (out of scope).
- **`Dialog`-wrapped composables.** Snapshot the inner content directly.
- **Particle celebrations.** `GameCompletionCelebration` is animation-driven and time-dependent; snapshot value is low.
- **Full screens.** Screen-level layouts would need state injection that the current `GameViewModel` doesn't expose for tests.
