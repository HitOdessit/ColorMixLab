# iOS test target — wiring

This directory contains an iOS test scaffold (`ColorMixLabTests.swift`) that is **not yet wired into the Xcode project**. ROADMAP acknowledges no iOS tests; this is the first step toward closing that gap.

The Swift file is committed; one-time Xcode UI work is needed to register a test target.

## One-time setup (≈30 seconds in Xcode)

1. Open `iosApp/ColorMixLab/ColorMixLab.xcodeproj` in Xcode.
2. **File → New → Target…** → select **iOS → Unit Testing Bundle** → **Next**.
3. Set:
   - **Product Name:** `ColorMixLabTests`
   - **Target to be Tested:** `ColorMixLab`
   - **Language:** Swift
4. Xcode creates a new `ColorMixLabTests/` group with a stub `ColorMixLabTests.swift`. Delete the stub.
5. Right-click the `ColorMixLabTests` group → **Add Files to "ColorMixLab"…** → select the existing `ColorMixLabTests.swift` from this directory → ensure **Target Membership** has only `ColorMixLabTests` checked.
6. Run with `⌘U` or via the scheme's Test action.

After this, the tests run from the Xcode UI and from `xcodebuild test -project iosApp/ColorMixLab/ColorMixLab.xcodeproj -scheme ColorMixLab -destination 'platform=iOS Simulator,name=iPhone 15'`.

## What the tests cover

`GameViewModelTests` is a smoke-test suite — it doesn't aim for parity with the Android JVM tests (those cover shared logic, which iOS already inherits via the KMP framework). It verifies:

- The Kotlin `shared` framework imports and instantiates from Swift (the KMP bridge works at runtime).
- `GameViewModel` initializes consistently for each `Difficulty` enum value.
- Easy mode boots with no timer; Medium and Hard boot with a timer.

If these tests fail, the framework bridge or initialization logic regressed — actionable signal.

## Why this scaffold isn't already in CI

Adding it to CI requires a macOS runner (`runs-on: macos-latest`) and the Xcode project's test target to exist. The first is cheap; the second needs the Xcode UI step above. Once that's done, a follow-up PR can add an `ios.yml` workflow that runs `xcodebuild test`.
