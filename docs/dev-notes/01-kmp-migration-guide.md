# ColorMixLab - Kotlin Multiplatform Migration Guide

> _Preserved as-is from the AI build journey. Claims here are point-in-time; see [ROADMAP](../../ROADMAP.md) for currently measured status._

## Overview

This document outlines the KMP (Kotlin Multiplatform) migration for ColorMixLab, allowing the game to run on both Android and iOS with ~80% shared code.

## Current Status: Phase 1-3 Complete ✅

### Completed Work

#### 1. Project Structure ✅
- Created `:shared` KMP module with commonMain, androidMain, and iosMain source sets
- Configured build.gradle.kts files for multiplatform support
- Added iOS targets: iosX64, iosArm64, iosSimulatorArm64
- Updated Android app to depend on shared module

#### 2. Shared Game Logic (commonMain) ✅

All core game logic has been extracted to the shared module:

**Models:**
- `/Users/valeryb/AndroidStudioProjects/ColorMixLab/shared/src/commonMain/kotlin/com/colormixlab/model/`
  - `PlatformColor.kt` - Platform-independent ARGB color representation
  - `GameColor.kt` - 18 unlockable colors with tier-based progression
  - `LeaderboardEntry.kt` - Serializable leaderboard entry with difficulty tracking

**Game Logic:**
- `/Users/valeryb/AndroidStudioProjects/ColorMixLab/shared/src/commonMain/kotlin/com/colormixlab/game/`
  - `ColorMixer.kt` - RGB color mixing and similarity calculation
  - `LevelManager.kt` - Target color generation with progressive difficulty
  - `GameState.kt` - Complete game state with enums (Difficulty, MathChallengeType)
  - `math/MathQuestion.kt` - Math challenge question and state models
  - `math/MathQuestionGenerator.kt` - Dynamic question generation

**Data Layer:**
- `/Users/valeryb/AndroidStudioProjects/ColorMixLab/shared/src/commonMain/kotlin/com/colormixlab/data/`
  - `LeaderboardManager.kt` - Cross-platform leaderboard with time-based filtering (today, week, month, all-time)

**Utilities:**
- `/Users/valeryb/AndroidStudioProjects/ColorMixLab/shared/src/commonMain/kotlin/com/colormixlab/utils/`
  - `MathChallengeTimer.kt` - Timer configuration for math challenges

#### 3. Platform Abstraction (expect/actual) ✅

**Common Interfaces:**
- `/Users/valeryb/AndroidStudioProjects/ColorMixLab/shared/src/commonMain/kotlin/com/colormixlab/platform/`
  - `PlatformStorage.kt` - Key-value storage interface
  - `HapticProvider.kt` - Haptic feedback with 3 types (LIGHT_TAP, SUCCESS, ERROR)
  - `SoundProvider.kt` - Sound playback with 4 types (DROP_COLOR, SUCCESS, CLEAR, LEVEL_UP)

**Android Implementations:**
- `/Users/valeryb/AndroidStudioProjects/ColorMixLab/shared/src/androidMain/kotlin/com/colormixlab/platform/`
  - `PlatformStorage.kt` - Uses SharedPreferences
  - `HapticProvider.kt` - Uses Android Vibrator API with VibrationEffect
  - `SoundProvider.kt` - Uses SoundPool for low-latency playback

## Key Design Decisions

### 1. Color Abstraction
Instead of using Compose Multiplatform, we created a lightweight `PlatformColor` class using ARGB values. This keeps the shared module truly platform-independent.

### 2. Serialization
- Using `kotlinx-serialization` instead of platform-specific JSON libraries
- All data classes that need persistence are marked with `@Serializable`

### 3. Date/Time Handling
- Using `kotlinx-datetime` instead of `java.util.Calendar` or `System.currentTimeMillis()`
- All timestamps use `Clock.System.now().toEpochMilliseconds()`

### 4. Storage Pattern
- LeaderboardManager business logic is in commonMain
- Platform-specific storage (SharedPreferences/UserDefaults) handled via expect/actual

## Next Steps

### Phase 4: iOS Actual Implementations (PENDING)

Create iOS implementations for platform APIs in `shared/src/iosMain/kotlin/com/colormixlab/platform/`:

#### **PlatformStorage.kt** (iOS)
```swift
actual class PlatformStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun saveString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    actual fun getString(key: String): String? {
        return defaults.stringForKey(key)
    }

    actual fun remove(key: String) {
        defaults.removeObjectForKey(key)
    }

    actual fun clear() {
        // Clear all ColorMixLab keys
        let domain = NSBundle.mainBundle.bundleIdentifier!
        defaults.removePersistentDomainForName(domain)
    }
}
```

#### **HapticProvider.kt** (iOS)
```swift
import UIKit

actual class HapticProvider {
    actual fun performHaptic(type: HapticType) {
        when (type) {
            HapticType.LIGHT_TAP -> {
                let impact = UIImpactFeedbackGenerator(style: .light)
                impact.impactOccurred()
            }
            HapticType.SUCCESS -> {
                let notification = UINotificationFeedbackGenerator()
                notification.notificationOccurred(.success)
            }
            HapticType.ERROR -> {
                let notification = UINotificationFeedbackGenerator()
                notification.notificationOccurred(.error)
            }
        }
    }
}
```

#### **SoundProvider.kt** (iOS)
```swift
import AVFoundation

actual class SoundProvider {
    private val players = mutableMapOf<SoundType, AVAudioPlayer>()

    actual fun playSound(type: SoundType) {
        players[type]?.play()
    }

    actual fun release() {
        players.values.forEach { it.stop() }
        players.clear()
    }
}
```

### Phase 5: Android App Refactoring (IN PROGRESS)

Update Android app to use shared module:

1. **Update imports** in existing Android files to use shared module classes
2. **Remove duplicate files** from Android app that now exist in shared module
3. **Update GameViewModel** to use shared LeaderboardManager with PlatformStorage
4. **Test Android app** to ensure it works with shared module

### Phase 6: iOS SwiftUI Implementation (PENDING)

Create SwiftUI screens matching the Android Compose UI:

#### **IntroScreen.swift**
- Welcome message and game instructions
- Difficulty selection (Easy/Medium/Hard)
- Start game button
- Leaderboard access

#### **GameScreen.swift**
- Target color display
- Mixing bowl with current mixed color
- Color buttons grid (dynamically showing unlocked colors)
- Drop counter, level display, score
- Timer (for Medium/Hard difficulty)
- Check/Clear/Menu buttons

#### **MathChallengeScreen.swift**
- Math question display (e.g., "7 × 8 = ?")
- 3×3 grid of answer options
- Timer (for Medium/Hard difficulty)
- Progress display (X/3 questions)

#### **Dialogs:**
- Success dialog (level completion)
- Leaderboard dialog with tabs (Today/Week/Month/All-Time)
- Menu dialog (Resume/Restart/Quit)
- Nickname entry dialog

### Phase 7: Xcode Project Setup (PENDING)

1. Create iOS app target in Xcode
2. Link shared.framework (built from KMP module)
3. Configure build phases to rebuild framework when needed
4. Set up proper module imports

## Building the Project

### Android

```bash
# Sync project in Android Studio (this will download Gradle wrapper if needed)
# File > Sync Project with Gradle Files

# Build shared module
./gradlew :shared:build

# Build Android app
./gradlew :app:assembleDebug
```

### iOS (After Xcode setup)

```bash
# Build shared framework for iOS
./gradlew :shared:linkDebugFrameworkIosArm64

# Open Xcode project
open iosApp/iosApp.xcodeproj

# Build and run in Xcode
```

## Dependencies

### Shared Module
- kotlinx-coroutines-core: 1.7.3
- kotlinx-serialization-json: 1.6.0
- kotlinx-datetime: 0.5.0

### Android App
- Jetpack Compose BOM: 2024.02.00
- AndroidX Core KTX: 1.12.0
- AndroidX Lifecycle: 2.7.0

## File Structure

```
ColorMixLab/
├── shared/                          # KMP shared module
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/kotlin/       # Shared game logic (80%)
│       │   └── com/colormixlab/
│       │       ├── model/           # GameColor, PlatformColor, LeaderboardEntry
│       │       ├── game/            # ColorMixer, LevelManager, GameState, math/
│       │       ├── data/            # LeaderboardManager
│       │       ├── platform/        # expect declarations (Storage, Haptics, Sound)
│       │       └── utils/           # MathChallengeTimer
│       ├── androidMain/kotlin/      # Android-specific implementations
│       │   └── com/colormixlab/platform/
│       └── iosMain/kotlin/          # iOS-specific implementations (TO BE CREATED)
│           └── com/colormixlab/platform/
├── app/                             # Android app (UI layer)
│   ├── build.gradle.kts             # Updated with shared module dependency
│   └── src/main/java/com/colormixlab/
│       └── ui/                      # Compose UI components
└── iosApp/                          # iOS app (TO BE CREATED)
    └── iosApp/
        └── UI/                      # SwiftUI screens
```

## Architecture Summary

```
┌─────────────────────────────────────────────────────────┐
│                   Platform UI Layer                      │
│  ┌────────────────────────┐  ┌────────────────────────┐ │
│  │  Android (Compose)      │  │  iOS (SwiftUI)         │ │
│  │  - GameScreen           │  │  - GameScreen          │ │
│  │  - IntroScreen          │  │  - IntroScreen         │ │
│  │  - MathChallengeScreen  │  │  - MathChallengeScreen │ │
│  └────────────────────────┘  └────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│            Platform Abstraction (expect/actual)          │
│  ┌────────────────────────┐  ┌────────────────────────┐ │
│  │  Android               │  │  iOS                    │ │
│  │  - SharedPreferences   │  │  - UserDefaults        │ │
│  │  - Vibrator            │  │  - UIFeedbackGenerator │ │
│  │  - SoundPool           │  │  - AVAudioPlayer       │ │
│  └────────────────────────┘  └────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│              Shared Business Logic (~80%)                │
│  - ColorMixer: RGB mixing algorithm                     │
│  - LevelManager: Target color generation                │
│  - GameState: Game state management                     │
│  - LeaderboardManager: Score tracking & filtering       │
│  - MathQuestionGenerator: Dynamic question generation   │
│  - GameColor: 18 colors with unlock progression         │
└─────────────────────────────────────────────────────────┘
```

## Testing Strategy

### Unit Tests (in shared module)
- ColorMixer: Test color mixing accuracy
- LevelManager: Test target generation and difficulty progression
- MathQuestionGenerator: Test question generation logic
- LeaderboardManager: Test sorting, filtering, persistence

### Integration Tests
- Android: Test PlatformStorage with SharedPreferences
- iOS: Test PlatformStorage with UserDefaults
- Both: Test complete game flow from start to leaderboard entry

## Migration Checklist

- [x] Create KMP project structure
- [x] Extract game logic to commonMain
- [x] Create expect/actual declarations
- [x] Implement Android actual classes
- [x] Update Android app build.gradle
- [ ] Implement iOS actual classes
- [ ] Refactor Android app to use shared module
- [ ] Create iOS SwiftUI screens
- [ ] Create iOS dialogs and components
- [ ] Implement iOS animations
- [ ] Set up Xcode project
- [ ] End-to-end testing on both platforms

## Resources

- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
- [kotlinx.datetime](https://github.com/Kotlin/kotlinx-datetime)
- [SwiftUI Documentation](https://developer.apple.com/documentation/swiftui)

## Support

For questions or issues:
1. Check this guide first
2. Review the official KMP documentation
3. Check Stack Overflow for platform-specific questions
