# ADR-0006: Disable R8 / ProGuard for hobby distribution

## Status

Accepted

## Context

Android's R8 (and the legacy ProGuard) shrinks, optimizes, and obfuscates the release APK. Enabling it (`isMinifyEnabled = true`) produces a smaller, harder-to-reverse APK at the cost of ongoing maintenance — every reflection-based dependency needs keep-rules, and stack traces require uploaded mapping files to remain readable.

The current configuration in `app/build.gradle.kts` has `isMinifyEnabled = false` and an empty `proguard-rules.pro`.

ColorMixLab is a public portfolio project, not a Play Store distribution. There is no signing key, no release channel, and no audience downloading the APK. Enabling R8 introduces three sources of ongoing maintenance:

1. **Reflection-based libraries.** Compose, Kotlinx serialization, Paparazzi, and other dependencies use reflection in ways the shrinker cannot statically see. Each Gradle / library bump can introduce a new reflection edge that requires a fresh keep-rule.
2. **Stack trace readability.** A minified APK's stack traces are unintelligible without the corresponding `mapping.txt`. Debugging crash reports requires hosting and matching mapping files per release.
3. **Test coverage gap.** Unit and Paparazzi tests run against the unminified test APK. R8 misconfigurations are not caught by the existing test suite — they manifest only in release builds, which this project does not regularly produce.

For a project whose goals are "buildable from source on a fresh checkout, looks professional, demonstrates AI-driven mobile development," the maintenance cost of keeping R8 keep-rules synchronized with library upgrades exceeds the value of a smaller APK that nobody downloads.

## Decision

Leave `isMinifyEnabled = false` in `app/build.gradle.kts`. Leave `proguard-rules.pro` empty.

If this project ever moves to public distribution (Play Store, signed APK on GitHub Releases, or alternative app store), enable R8 as part of that distribution work and write the keep-rules then.

## Consequences

**Positive:**

- Zero shrinker maintenance burden as Compose, Kotlinx, Paparazzi, and other reflection-using libraries evolve.
- Stack traces from any built APK are readable without symbol uploads.
- Slightly faster `assembleRelease` (~5–15 seconds saved on a typical build).
- Aligns with the project's documented "minimum-maintenance portfolio" posture.

**Negative:**

- Release APK is approximately 30–40% larger than it would be after minification (still well under 10 MB; not a meaningful concern at this scale).
- A reverse-engineered APK exposes fully readable Kotlin source. Since the project is open-source under MIT, this is a non-issue — the source is already public.
- The "shipped a fully-shrunk release pipeline" item that some hiring rubrics check is not on the engineering checklist for this project.

## Rejected alternatives

- **Enable R8 with a "fix-it-when-it-breaks" posture.** Catches no value (no distribution channel) while creating a class of CI failure that is annoying to debug, particularly on Dependabot bumps to Compose or Kotlin.
- **Enable R8, write keep-rules, never check release builds.** Worst of both worlds — maintenance cost incurred without verification. R8 misconfigurations would silently degrade builds that nobody runs.

## Related

- [ROADMAP.md](../../ROADMAP.md) lists this as an acknowledged scope choice, alongside other deliberate v1 trade-offs.
- [CONTRIBUTING.md](../../CONTRIBUTING.md) — this project is not actively developed; PRs that flip `isMinifyEnabled = true` would need a strong distribution-focused justification accompanied by a superseding ADR.
