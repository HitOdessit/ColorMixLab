#!/bin/bash

echo "============================================"
echo "Color Mix Lab - Project Structure Verification"
echo "============================================"
echo ""

echo "Checking project structure..."
echo ""

# Core files
echo "✓ Root configuration files:"
[ -f "settings.gradle.kts" ] && echo "  ✓ settings.gradle.kts" || echo "  ✗ settings.gradle.kts MISSING"
[ -f "build.gradle.kts" ] && echo "  ✓ build.gradle.kts" || echo "  ✗ build.gradle.kts MISSING"
[ -f "gradle.properties" ] && echo "  ✓ gradle.properties" || echo "  ✗ gradle.properties MISSING"
[ -f ".gitignore" ] && echo "  ✓ .gitignore" || echo "  ✗ .gitignore MISSING"
[ -f "README.md" ] && echo "  ✓ README.md" || echo "  ✗ README.md MISSING"

echo ""
echo "✓ App module files:"
[ -f "app/build.gradle.kts" ] && echo "  ✓ app/build.gradle.kts" || echo "  ✗ app/build.gradle.kts MISSING"
[ -f "app/proguard-rules.pro" ] && echo "  ✓ app/proguard-rules.pro" || echo "  ✗ app/proguard-rules.pro MISSING"
[ -f "app/src/main/AndroidManifest.xml" ] && echo "  ✓ AndroidManifest.xml" || echo "  ✗ AndroidManifest.xml MISSING"

echo ""
echo "✓ Source files:"
[ -f "app/src/main/java/com/colormixlab/MainActivity.kt" ] && echo "  ✓ MainActivity.kt" || echo "  ✗ MainActivity.kt MISSING"

echo ""
echo "✓ Game logic:"
[ -f "app/src/main/java/com/colormixlab/game/GameViewModel.kt" ] && echo "  ✓ GameViewModel.kt" || echo "  ✗ GameViewModel.kt MISSING"
[ -f "app/src/main/java/com/colormixlab/game/GameState.kt" ] && echo "  ✓ GameState.kt" || echo "  ✗ GameState.kt MISSING"
[ -f "app/src/main/java/com/colormixlab/game/ColorMixer.kt" ] && echo "  ✓ ColorMixer.kt" || echo "  ✗ ColorMixer.kt MISSING"
[ -f "app/src/main/java/com/colormixlab/game/LevelManager.kt" ] && echo "  ✓ LevelManager.kt" || echo "  ✗ LevelManager.kt MISSING"

echo ""
echo "✓ UI components:"
[ -f "app/src/main/java/com/colormixlab/ui/GameScreen.kt" ] && echo "  ✓ GameScreen.kt" || echo "  ✗ GameScreen.kt MISSING"
[ -f "app/src/main/java/com/colormixlab/ui/WelcomeScreen.kt" ] && echo "  ✓ WelcomeScreen.kt" || echo "  ✗ WelcomeScreen.kt MISSING"
[ -f "app/src/main/java/com/colormixlab/ui/components/ColorButton.kt" ] && echo "  ✓ ColorButton.kt" || echo "  ✗ ColorButton.kt MISSING"
[ -f "app/src/main/java/com/colormixlab/ui/components/MixingBowl.kt" ] && echo "  ✓ MixingBowl.kt" || echo "  ✗ MixingBowl.kt MISSING"
[ -f "app/src/main/java/com/colormixlab/ui/components/TargetColor.kt" ] && echo "  ✓ TargetColor.kt" || echo "  ✗ TargetColor.kt MISSING"
[ -f "app/src/main/java/com/colormixlab/ui/components/LevelDisplay.kt" ] && echo "  ✓ LevelDisplay.kt" || echo "  ✗ LevelDisplay.kt MISSING"

echo ""
echo "✓ Models:"
[ -f "app/src/main/java/com/colormixlab/model/GameColors.kt" ] && echo "  ✓ GameColors.kt" || echo "  ✗ GameColors.kt MISSING"

echo ""
echo "✓ Utils:"
[ -f "app/src/main/java/com/colormixlab/utils/SoundManager.kt" ] && echo "  ✓ SoundManager.kt" || echo "  ✗ SoundManager.kt MISSING"
[ -f "app/src/main/java/com/colormixlab/utils/HapticManager.kt" ] && echo "  ✓ HapticManager.kt" || echo "  ✗ HapticManager.kt MISSING"

echo ""
echo "✓ Theme:"
[ -f "app/src/main/java/com/colormixlab/ui/theme/Theme.kt" ] && echo "  ✓ Theme.kt" || echo "  ✗ Theme.kt MISSING"

echo ""
echo "✓ Resources:"
[ -f "app/src/main/res/values/strings.xml" ] && echo "  ✓ strings.xml" || echo "  ✗ strings.xml MISSING"
[ -f "app/src/main/res/values/colors.xml" ] && echo "  ✓ colors.xml" || echo "  ✗ colors.xml MISSING"
[ -f "app/src/main/res/values/themes.xml" ] && echo "  ✓ themes.xml" || echo "  ✗ themes.xml MISSING"

echo ""
echo "============================================"
echo "Project verification complete!"
echo "============================================"
echo ""
echo "Next steps:"
echo "1. Open Android Studio"
echo "2. Select 'Open an Existing Project'"
echo "3. Navigate to this directory"
echo "4. Wait for Gradle sync"
echo "5. Run on emulator or device"
echo ""

