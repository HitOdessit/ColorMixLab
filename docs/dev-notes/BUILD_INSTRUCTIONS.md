# Build Instructions - Color Mix Lab

## Project Location
```
/Users/valeryb/AndroidStudioProjects/ColorMixLab
```

## Prerequisites

### Required Software
- **Android Studio**: Hedgehog (2023.1.1) or newer
- **Java Development Kit (JDK)**: Version 8 or higher
- **Android SDK**: API Level 34 (automatically installed by Android Studio)

### System Requirements
- **macOS**: 10.14 (Mojave) or higher
- **RAM**: Minimum 8GB (16GB recommended)
- **Disk Space**: At least 4GB free space

## Step-by-Step Build Instructions

### 1. Open the Project

1. Launch **Android Studio**
2. Click **"Open"** (or File → Open)
3. Navigate to: `/Users/valeryb/AndroidStudioProjects/ColorMixLab`
4. Click **"Open"**

### 2. Wait for Gradle Sync

- Android Studio will automatically start syncing Gradle
- This may take 2-5 minutes on first load
- Watch the bottom status bar for progress
- If prompted to update Gradle or plugins, click **"Update"**

### 3. Configure SDK (if needed)

If you see SDK errors:
1. Go to **Android Studio → Settings → Appearance & Behavior → System Settings → Android SDK**
2. Ensure **Android 14.0 (API Level 34)** is installed
3. Click **"Apply"** if you make changes

### 4. Run on Emulator

#### Create an Emulator (if you don't have one):
1. Click the **Device Manager** icon (phone icon in toolbar)
2. Click **"Create Device"**
3. Select a device (recommended: **Pixel 5** or **Pixel 6**)
4. Select system image: **API Level 34** (download if needed)
5. Click **"Finish"**

#### Run the App:
1. Select your emulator from the device dropdown in the toolbar
2. Click the green **"Run"** button (▶️) or press **⌃R**
3. Wait for the emulator to boot (first time takes 1-2 minutes)
4. The app will install and launch automatically

### 5. Run on Physical Device

1. Enable **Developer Options** on your Android device:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
   - Go back to Settings → System → Developer Options
   
2. Enable **USB Debugging** in Developer Options

3. Connect device via USB

4. If prompted on device, allow USB debugging

5. In Android Studio, select your device from the dropdown

6. Click **"Run"** (▶️)

## Build Variants

### Debug Build (Default)
- Used for development and testing
- Includes debugging information
- No code optimization
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build
- Optimized for production
- Requires signing configuration
```bash
./gradlew assembleRelease
```

## Troubleshooting

### Gradle Sync Fails
**Solution 1**: Invalidate Caches
- File → Invalidate Caches → Invalidate and Restart

**Solution 2**: Check Internet Connection
- Gradle needs to download dependencies on first sync
- Ensure you have a stable internet connection

**Solution 3**: Clean Project
- Build → Clean Project
- Build → Rebuild Project

### App Won't Install on Device
**Solution 1**: Uninstall existing version
```bash
adb uninstall com.colormixlab
```

**Solution 2**: Check USB Debugging
- Ensure USB debugging is enabled on device
- Try revoking and re-granting USB debugging authorization

### Compilation Errors
**Solution 1**: Update Kotlin Plugin
- Tools → Kotlin → Configure Kotlin Plugin Updates

**Solution 2**: Sync Gradle Again
- File → Sync Project with Gradle Files

### Emulator Slow or Crashes
**Solution 1**: Increase Emulator RAM
- Device Manager → Edit device → Show Advanced Settings
- Increase RAM to 2048MB or more

**Solution 2**: Enable Hardware Acceleration
- Ensure Intel HAXM (macOS) is installed
- Android Studio → SDK Manager → SDK Tools → Intel HAXM

## Testing the Game

### Test Checklist

1. **Welcome Screen**
   - ✓ Shows on first launch
   - ✓ Instructions are clear
   - ✓ "Start Playing" button works

2. **Level 1-3 (Primary Colors Only)**
   - ✓ Only Red, Blue, Yellow buttons visible
   - ✓ Tapping buttons adds drops with haptic feedback
   - ✓ Bowl color changes smoothly
   - ✓ Similarity percentage updates
   - ✓ "Clear Bowl" resets state
   - ✓ "Check Match" validates correctly
   - ✓ Success dialog appears on match

3. **Level 4-6 (Orange Unlocked)**
   - ✓ Orange button appears
   - ✓ Unlock message shows on level 3 completion

4. **Level 7-9 (Purple Unlocked)**
   - ✓ Purple button appears
   - ✓ Unlock message shows on level 6 completion

5. **Level 10+ (Green Unlocked)**
   - ✓ Green button appears
   - ✓ Unlock message shows on level 9 completion

6. **Color Mixing**
   - ✓ Red + Blue ≈ Purple
   - ✓ Red + Yellow ≈ Orange
   - ✓ Blue + Yellow ≈ Green

7. **Animations & UX**
   - ✓ Bowl color transitions smoothly
   - ✓ Button press animations
   - ✓ Haptic feedback works
   - ✓ Success dialog animates nicely
   - ✓ App stays in landscape mode

8. **Edge Cases**
   - ✓ Can't check match with empty bowl
   - ✓ Multiple drops of same color work
   - ✓ Level progression maintains state
   - ✓ Rotation locked to landscape

## Project Structure Summary

```
ColorMixLab/
├── app/
│   ├── src/main/
│   │   ├── java/com/colormixlab/
│   │   │   ├── MainActivity.kt
│   │   │   ├── ui/
│   │   │   ├── game/
│   │   │   ├── model/
│   │   │   └── utils/
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Performance Optimization Tips

1. **Enable R8 (Proguard)** for release builds
2. Use **emulator with >= 2GB RAM**
3. Test on real device for accurate haptic feedback
4. Monitor frame rate (should be smooth 60fps)

## Support

For issues or questions:
1. Check Android Studio's **Logcat** for error messages
2. Review the **Build** tab for compilation errors
3. Use **Device File Explorer** to verify app installation

## Success Criteria

✅ Project builds without errors  
✅ App launches in landscape mode  
✅ Welcome screen appears on first launch  
✅ Color buttons respond with haptic feedback  
✅ Bowl mixes colors correctly  
✅ Level progression works  
✅ New colors unlock at appropriate levels  
✅ Animations are smooth  
✅ No crashes during gameplay  

---

**Built with Kotlin + Jetpack Compose**  
**Target: Kids Ages 7-11**

