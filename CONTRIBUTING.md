# Contributing

This project was built entirely with AI tooling (Claude Code) as a demonstration of AI-assisted mobile development. It is primarily a personal portfolio project.

## How to contribute

- **Bug reports and suggestions** — Open an [issue](https://github.com/HitOdessit/ColorMixLab/issues) describing what you found or what you'd like to see improved.
- **Pull requests** — Welcome for bug fixes, documentation improvements, and small enhancements. For larger changes, please open an issue first to discuss the approach.

## Development setup

1. Clone the repo
2. Open in Android Studio (2023.1+) with JDK 17
3. Run `./gradlew test` to verify everything works
4. Make your changes
5. Run `./gradlew test` again to confirm tests pass
6. Submit a PR

## Code style

- Kotlin for all new Android/shared code
- Follow existing patterns in the codebase
- Tests for any new game logic (add to `app/src/test/`)
