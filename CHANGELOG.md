# Changelog

All notable changes to this project are documented in this file. The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to a `1.0.<commit-count>` versioning scheme.

## [Unreleased]

### Added
- `GameConstants` object centralizing all gameplay-tuning magic numbers
- `KeyValueStorage` interface for testable storage abstraction
- `LeaderboardManagerTest` (14 new tests) covering CRUD, ranking, time-window queries, and corruption recovery
- Sealed `Screen` hierarchy for type-safe navigation in `MainActivity`
- Comprehensive KDoc on `GameController`, `GameState`, `Difficulty`, `MathChallengeType`, and `LeaderboardManager` public APIs
- `detekt` static analysis with custom config in `config/detekt/detekt.yml`
- Spotless + ktlint formatting via `./gradlew spotlessCheck` / `spotlessApply`
- Kover code coverage with Codecov upload in CI
- `.editorconfig` for consistent formatting across editors
- `SECURITY.md`, `CODE_OF_CONDUCT.md`, PR template, and Issue template config
- Dependabot weekly Gradle + GitHub Actions updates
- CodeQL workflow for Kotlin/Java security scanning
- Release workflow that builds and publishes APKs on tag push
- `ARCHITECTURE.md` extracted from README with deeper diagrams and trade-off discussion
- `docs/reproduce.md` with example prompts to rebuild the project
- `docs/dev-notes/README.md` explaining the AI-generated dev notes archive

### Changed
- README rewritten with hook opening, Mermaid architecture diagram, concrete metrics, engineering trade-offs section, FAQ, and call-to-action
- `LeaderboardManager.getRank` now correctly returns `entries.size + 1` when score is below all entries (was returning 0)
- `LeaderboardManager` deduplicates the since-midnight calculation across today/week/month queries
- Stripped historical "// Reduced from X" / "// Old radius was Y" comments from animation files

## [1.0.32] — 2026-05-02

### Fixed
- Removed duplicate `MathChallengeTimer` class from app module that caused DEX merge collision on release builds
- Fixed deprecated `LinearProgressIndicator(progress: Float)` to use lambda overload
- Fixed deprecated `Icons.Filled.ArrowBack` to `Icons.AutoMirrored.Filled.ArrowBack`
- Fixed remaining deprecated `Divider()` in `MenuDialog`
- Removed personal absolute path from Xcode project, replaced with `$(SRCROOT)`-relative path
- Cleared exposed Apple Developer Team ID from Xcode project config
- Removed unused imports across `GameLayouts`, `IntroScreen`, `MathAnswerButton`

## [1.0.30] — 2026-05-02

### Added
- Initial GitHub Actions CI workflow
- `LICENSE` (MIT)
- `CLAUDE.md` documenting the AI build process
- `CONTRIBUTING.md`
- GitHub issue templates

### Changed
- Comprehensive README rewrite with badges, architecture, build instructions
- Cleaned up `.gitignore` (removed duplicates, added Xcode/Kotlin/Claude patterns)

## [1.0.27] — 2026-05-02

### Added
- Gradle version catalog (`gradle/libs.versions.toml`)

### Changed
- Bumped `compileSdk` and `targetSdk` from 34 to 35
- Removed unnecessary Jetifier
- Set `nonTransitiveRClass` to true (smaller APK, faster builds)

## [1.0.26] — 2026-05-02

### Added
- `GameControllerTest` with 25 tests covering core game flow, scoring, timer, math challenges
- `LeaderboardEntry` serialization roundtrip test

### Changed
- Fixed all 6 broken test files to compile against current KMP code (Compose `Color` → `PlatformColor`)
- Removed unused test dependencies (MockK, arch core-testing)

## [1.0.25] — 2026-05-02

### Removed
- Dead code: `LeaderboardManagerFactory`, `GameColor.Dynamic`, `RippleEffect`, debug logs
- Duplicate older iOS app directory (`iosApp/iosApp/`)

### Fixed
- Thread safety in `GameController`: replaced non-atomic `_gameState.value = .copy()` with `_gameState.update {}`
- Replaced deprecated `Divider()` with `HorizontalDivider()`

## [1.0.23] — 2026-05-02

### Added
- iOS app port (`iosApp/ColorMixLab/`) with SwiftUI UI layer

## [1.0.22] — 2026-04 to 2026-05

### Added
- Game completion celebration animation
- Math challenge layout optimization for landscape and tablets
- Initial unit tests
- Math times table challenge with timer
- New colors palette and randomized unlock progression
- Tablet support including landscape mode
- Today/Weekly/Monthly/All-time leaderboard tabs
- Difficulty levels (Easy/Medium/Hard) with intro screen
- Local leaderboard with end-of-game flow

## [1.0.0] — 2026-03

### Added
- Initial working version with color mixing and level progression
