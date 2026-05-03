# Contributing

ColorMixLab is a personal portfolio project built end-to-end with AI tooling (Cursor + Claude Code) — see [RETROSPECTIVE.md](RETROSPECTIVE.md) for the build story. Contributions are welcome; the conventions below exist so changes land cleanly without churn.

## Ways to contribute

- **Bug reports and suggestions** — open an [issue](https://github.com/HitOdessit/ColorMixLab/issues). For bugs, include device/emulator, OS version, and reproduction steps.
- **Pull requests** — bug fixes, documentation improvements, and small enhancements are welcome. For larger changes, open an issue first to align on approach before writing code.
- **Reproduce experiments** — `docs/reproduce.md` has example prompts that rebuild parts of this project from scratch. If you try one and the output diverges from what's described, an issue documenting the divergence is genuinely useful.

## AI-generated PRs

PRs do not need to be human-written. If your PR was authored by an AI assistant, please disclose that in the PR description (which tool, roughly how much human edit/review). The disclosure is informational, not gatekeeping — AI-generated contributions are first-class. Reviewers focus on the same things either way: correctness, conventions, tests, scope.

## Development setup

1. Clone the repo
2. Open in Android Studio (2023.1+) with JDK 17
3. `./gradlew test` — verify tests pass on a clean checkout
4. Build the iOS shared framework once if you'll touch iOS: `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64`
5. Open `iosApp/ColorMixLab/ColorMixLab.xcodeproj` in Xcode for iOS work

## Codebase conventions

These are the conventions that catch the most review comments. Read them before writing code.

- **State mutations always atomic.** In `GameController`, mutate via `_gameState.update { state -> state.copy(...) }`. Never `_gameState.value = _gameState.value.copy(...)` — the latter is a non-atomic read-modify-write that loses updates under concurrent timer + UI mutations. See [ADR-0002](docs/adr/0002-atomic-stateflow-updates.md). This is the most common AI-generated bug pattern in the codebase; reviewers will flag it on sight.
- **`commonMain` is platform-free.** No `android.*`, no `ios.*`, no `java.*` imports in `shared/src/commonMain/`. Platform-specific behavior goes behind `expect class` / `actual class` (see `HapticProvider`, `PlatformStorage`, `SoundProvider`).
- **Kotlin for shared and Android, Swift for iOS.** Don't introduce Java in new code. Don't introduce Objective-C in new iOS code.
- **Always use braces** on `if`/`else` bodies. No braceless single-liners (`if (x) return null` → `if (x) { return null }`).
- **Immutability by default** for state types — `data class`, `val`, no `var` properties on `GameState` or its members.
- **Color model.** `PlatformColor` (UInt ARGB) lives in shared code. Convert to platform-native `Color` only at the UI layer.

## Testing

- New shared / Android logic gets a test in `app/src/test/java/com/colormixlab/...`. Tests run via the Android JUnit runner (JVM, not instrumented).
- New Compose UI components get a Paparazzi snapshot test in `app/src/test/java/com/colormixlab/ui/SnapshotTests.kt`. After adding, run `./gradlew :app:recordPaparazziDebug` to generate the golden, then commit the new PNG under `app/src/test/snapshots/`.
- Run all tests before pushing: `./gradlew test :app:verifyPaparazziDebug`.
- iOS test target wiring is documented in `iosApp/ColorMixLab/ColorMixLabTests/README.md`.

## Code quality

Run before opening a PR:

```bash
./gradlew spotlessApply  # auto-fix formatting
./gradlew detekt         # static analysis (Kotlin)
./gradlew test           # unit tests
```

CI runs the same checks. Spotless and Detekt are currently advisory in CI but will be moved to gating — keep your changes clean either way.

## Commit style

- Conventional-commits-ish prefixes used in this repo: `feat:`, `fix:`, `docs:`, `test:`, `refactor:`, `chore:`, `ci:`.
- One concern per commit. If your branch fixes a bug AND refactors a helper, that's two commits.
- The `pre-commit` hook auto-bumps the app version code in `app/build.gradle.kts` — don't manually edit that.

## Pull request expectations

- Small and atomic. A 30-line PR gets a review faster than a 300-line one.
- Tests for new game logic are required, not negotiable.
- Update `CHANGELOG.md` under `[Unreleased]` for user-visible changes.
- If your change has a non-obvious design decision, mention it in the PR description; a one-paragraph rationale beats a five-comment review thread.
- For changes that affect both Android and iOS (almost any change to `shared/`), update both platforms in the same PR — do not leave one half implemented.

## What this project is not interested in

- Online multiplayer, IAP, ads, or analytics SDKs. The "fully offline, no tracking" property is intentional and load-bearing for the privacy story in the README.
- Adopting Compose Multiplatform on the iOS side. The native-UI-on-each-platform decision is documented in [ADR-0001](docs/adr/0001-adopt-kotlin-multiplatform.md); reopening it would require a strong, specific argument.
- Adding SKIE or KMP-NativeCoroutines for `StateFlow` bridging. The 100ms polling decision is documented in [ADR-0003](docs/adr/0003-ios-stateflow-polling-bridge.md).

PRs that move the project in any of these directions will not be merged unless they come with a fresh ADR proposing the change.
