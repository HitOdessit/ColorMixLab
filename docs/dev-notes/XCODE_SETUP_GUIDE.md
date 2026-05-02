# Xcode Project Setup Guide for ColorMixLab iOS

## Prerequisites

- macOS with Xcode 14.0 or later
- iOS 15.0+ deployment target
- Kotlin Multiplatform shared framework already built

## Step 1: Build the Shared Framework

Before setting up Xcode, ensure the shared framework is built:

```bash
cd /Users/valeryb/AndroidStudioProjects/ColorMixLab

# Build for iOS Simulator (M1 Mac)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# Build for iOS Device
./gradlew :shared:linkDebugFrameworkIosArm64

# Build for Intel Simulator
./gradlew :shared:linkDebugFrameworkIosX64
```

The frameworks will be generated at:
- Simulator (ARM64): `shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework`
- Device (ARM64): `shared/build/bin/iosArm64/debugFramework/shared.framework`
- Simulator (x64): `shared/build/bin/iosX64/debugFramework/shared.framework`

## Step 2: Create Xcode Project

1. **Open Xcode** and select **"Create a new Xcode project"**

2. **Select iOS App template**:
   - Platform: **iOS**
   - Application: **App**
   - Click **Next**

3. **Configure the project**:
   - Product Name: **ColorMixLab**
   - Team: *Select your development team*
   - Organization Identifier: **com.colormixlab**
   - Bundle Identifier: **com.colormixlab.ColorMixLab**
   - Interface: **SwiftUI**
   - Language: **Swift**
   - Storage: **None**
   - Click **Next**

4. **Save location**:
   - Navigate to: `/Users/valeryb/AndroidStudioProjects/ColorMixLab/`
   - Folder name: **iosApp**
   - Click **Create**

## Step 3: Link the Shared Framework

### 3.1 Add Framework to Project

1. In Xcode, select the **ColorMixLab** project in the navigator
2. Select the **ColorMixLab** target
3. Go to **General** tab
4. Scroll to **Frameworks, Libraries, and Embedded Content**
5. Click the **+** button
6. Click **Add Other...** → **Add Files...**
7. Navigate to: `shared/build/bin/iosSimulatorArm64/debugFramework/`
8. Select **shared.framework**
9. Click **Open**
10. Ensure **Embed & Sign** is selected

### 3.2 Configure Framework Search Paths

1. Select the **ColorMixLab** target
2. Go to **Build Settings** tab
3. Search for **Framework Search Paths**
4. Double-click the value and add:
   ```
   $(PROJECT_DIR)/../shared/build/bin/iosSimulatorArm64/debugFramework
   $(PROJECT_DIR)/../shared/build/bin/iosArm64/debugFramework
   $(PROJECT_DIR)/../shared/build/bin/iosX64/debugFramework
   ```

### 3.3 Add Build Phase to Rebuild Framework

This ensures the shared framework is rebuilt when you build the iOS app:

1. Select the **ColorMixLab** target
2. Go to **Build Phases** tab
3. Click **+** → **New Run Script Phase**
4. Rename it to **"Build Shared Framework"**
5. Drag it to be **before** "Compile Sources"
6. Add the following script:

```bash
cd "$SRCROOT/../"

# Detect if we're building for device or simulator
if [ "$PLATFORM_NAME" == "iphonesimulator" ]; then
    if [ "$(uname -m)" == "arm64" ]; then
        ./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
    else
        ./gradlew :shared:linkDebugFrameworkIosX64
    fi
else
    ./gradlew :shared:linkDebugFrameworkIosArm64
fi
```

7. Add Input Files:
   ```
   $(SRCROOT)/../shared/build.gradle.kts
   ```

8. Add Output Files:
   ```
   $(SRCROOT)/../shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework
   $(SRCROOT)/../shared/build/bin/iosArm64/debugFramework/shared.framework
   $(SRCROOT)/../shared/build/bin/iosX64/debugFramework/shared.framework
   ```

## Step 4: Add Swift Files to Project

### 4.1 Create Folder Structure

In Xcode, create the following folder structure (Right-click → New Group):

```
iosApp/
└── iosApp/
    ├── UI/
    │   ├── Components/
    │   ├── Dialogs/
    │   ├── IntroScreen.swift
    │   ├── GameScreen.swift
    │   └── MathChallengeScreen.swift
    ├── ViewModels/
    │   └── GameViewModel.swift
    ├── Assets.xcassets
    ├── ColorMixLabApp.swift
    └── Info.plist
```

### 4.2 Add Swift Files

1. Right-click on each folder
2. Select **Add Files to "ColorMixLab"...**
3. Navigate to the corresponding file in the file system
4. Select the file
5. Ensure **Copy items if needed** is checked
6. Click **Add**

Add these files:
- `UI/IntroScreen.swift`
- `UI/GameScreen.swift`
- `UI/MathChallengeScreen.swift`
- `UI/Components/GameHeader.swift`
- `UI/Components/TargetColorView.swift`
- `UI/Components/MixingBowlView.swift`
- `UI/Components/ColorPaletteGrid.swift`
- `UI/Components/ActionButtons.swift`
- `UI/Dialogs/ResultDialog.swift`
- `UI/Dialogs/MenuDialog.swift`
- `UI/Dialogs/LeaderboardView.swift`
- `UI/Dialogs/GameCompletedView.swift`
- `ViewModels/GameViewModel.swift`

### 4.3 Create App Entry Point

Replace the content of `ColorMixLabApp.swift` with:

```swift
import SwiftUI

@main
struct ColorMixLabApp: App {
    var body: some Scene {
        WindowGroup {
            IntroScreen()
        }
    }
}
```

## Step 5: Configure Bridging Header (if needed)

If Xcode prompts for a bridging header:

1. Click **Create Bridging Header**
2. In `ColorMixLab-Bridging-Header.h`, add:

```objc
#import <shared/shared.h>
```

## Step 6: Import Shared Module

In each Swift file that uses the shared module, add:

```swift
import shared
```

## Step 7: Configure Info.plist

Add the following keys if needed:

```xml
<key>UILaunchScreen</key>
<dict>
    <key>UIColorName</key>
    <string>LaunchScreenBackground</string>
</dict>
```

## Step 8: Configure Deployment Target

1. Select the **ColorMixLab** target
2. Go to **General** tab
3. Set **Minimum Deployments** to **iOS 15.0**

## Step 9: Build and Run

1. Select a simulator or device from the scheme dropdown
2. Press **⌘ + B** to build
3. Press **⌘ + R** to run

### Common Build Issues and Solutions

#### Issue: "Module 'shared' not found"
**Solution**:
- Ensure the shared framework is built (Step 1)
- Verify Framework Search Paths (Step 3.2)
- Clean build folder: **Product → Clean Build Folder** (⇧⌘K)

#### Issue: "Framework not found"
**Solution**:
- Check that the framework exists at the path specified in Framework Search Paths
- Rebuild the shared framework
- Verify the framework is added in "Frameworks, Libraries, and Embedded Content"

#### Issue: Gradle build fails
**Solution**:
- Ensure you have Android Studio installed (for Gradle wrapper)
- Make sure you've synced the Gradle project in Android Studio first
- Check Kotlin version is 2.0.0

#### Issue: Multiple frameworks conflict
**Solution**:
- Only embed the framework for the current architecture
- Use the build script in Step 3.3 to automatically select the correct framework

## Step 10: Create Universal Framework (Optional)

For distributing your app, you may want to create a universal (fat) framework:

```bash
cd /Users/valeryb/AndroidStudioProjects/ColorMixLab

# Build all variants
./gradlew :shared:linkDebugFrameworkIosX64
./gradlew :shared:linkDebugFrameworkIosArm64
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# Create universal framework directory
mkdir -p shared/build/XCFrameworks

# Create XCFramework
xcodebuild -create-xcframework \
  -framework shared/build/bin/iosX64/debugFramework/shared.framework \
  -framework shared/build/bin/iosArm64/debugFramework/shared.framework \
  -framework shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework \
  -output shared/build/XCFrameworks/shared.xcframework
```

Then link the XCFramework instead of individual frameworks.

## Additional Configuration

### Enable Haptic Feedback

Haptics should work out of the box on physical devices. On simulator:
- Go to **Hardware → Keyboard → Connect Hardware Keyboard** (uncheck)
- Haptics won't actually vibrate in simulator but code will execute

### Test on Device

1. Connect your iOS device
2. Select it from the scheme dropdown
3. If this is first time:
   - Go to **Settings → General → Device Management**
   - Trust your developer certificate
4. Build and run

### App Icons

✅ **App icon is already configured!** The iOS app icon matches the Android app icon design.

**Icon Design:**
- Red, Blue, Yellow circles representing primary colors
- Purple mixing bowl in the center
- Clean white background

**Location:** `Assets.xcassets/AppIcon.appiconset/app-icon-1024.png`

**To view:**
1. Open `Assets.xcassets` in Xcode
2. Select `AppIcon`
3. You'll see the colored circles icon (1024×1024)

**To regenerate (if needed):**
```bash
python3 generate_ios_icon_pure.py
```

**Documentation:**
- Full guide: `IOS_APP_ICON_SETUP.md`
- Quick reference: `IOS_APP_ICON_QUICK_REF.md`
- Android-iOS comparison: `APP_ICON_COMPARISON.md`

## Next Steps

- Test all screens and functionality
- Add app icons and splash screen
- Configure app metadata (display name, version, etc.)
- Test on physical device
- Submit to TestFlight/App Store (if desired)

## File Structure (Final)

```
ColorMixLab/
├── app/                          # Android app
├── shared/                       # KMP shared module
├── iosApp/                       # iOS app (Xcode project)
│   └── iosApp/
│       ├── UI/
│       │   ├── Components/
│       │   │   ├── GameHeader.swift
│       │   │   ├── TargetColorView.swift
│       │   │   ├── MixingBowlView.swift
│       │   │   ├── ColorPaletteGrid.swift
│       │   │   └── ActionButtons.swift
│       │   ├── Dialogs/
│       │   │   ├── ResultDialog.swift
│       │   │   ├── MenuDialog.swift
│       │   │   ├── LeaderboardView.swift
│       │   │   └── GameCompletedView.swift
│       │   ├── IntroScreen.swift
│       │   ├── GameScreen.swift
│       │   └── MathChallengeScreen.swift
│       ├── ViewModels/
│       │   └── GameViewModel.swift
│       ├── Assets.xcassets
│       ├── ColorMixLabApp.swift
│       └── Info.plist
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Troubleshooting

### Xcode Can't Find shared Module

1. Clean derived data:
   ```bash
   rm -rf ~/Library/Developer/Xcode/DerivedData
   ```

2. Restart Xcode

3. Rebuild shared framework

### Gradle Not Found

Ensure you have the Gradle wrapper:
```bash
cd /Users/valeryb/AndroidStudioProjects/ColorMixLab
./gradlew --version
```

If not found, sync the project in Android Studio first.

### Build Script Fails

Make sure the script has execute permissions:
```bash
chmod +x /Users/valeryb/AndroidStudioProjects/ColorMixLab/gradlew
```

## Resources

- [Kotlin Multiplatform Mobile Documentation](https://kotlinlang.org/docs/multiplatform-mobile-getting-started.html)
- [SwiftUI Documentation](https://developer.apple.com/documentation/swiftui)
- [Xcode Documentation](https://developer.apple.com/documentation/xcode)

---

**You're all set!** The iOS app should now build and run, sharing 80% of the codebase with the Android app through the Kotlin Multiplatform shared module.
