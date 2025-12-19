# Color Mix Lab

A simple, educational color-mixing game for kids ages 7-11.

## Overview

Color Mix Lab is a kid-friendly Android game where players mix colors by tapping buttons to add color drops to a mixing bowl. The goal is to match a target color by combining different colors.

## Features

- **Progressive Difficulty**: Starts with simple primary colors and unlocks more complex colors as you advance
- **Educational**: Teaches color mixing concepts (Red + Blue = Purple, etc.)
- **Kid-Friendly UI**: Large buttons, clear visuals, haptic feedback
- **No Ads or Analytics**: Pure offline gameplay
- **Landscape Mode**: Optimized for comfortable play

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: Lightweight MVVM with ViewModel
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34

## Project Structure

```
app/src/main/java/com/colormixlab/
├── MainActivity.kt                 # Main activity
├── ui/
│   ├── GameScreen.kt              # Main game screen
│   ├── components/                # Reusable UI components
│   │   ├── ColorButton.kt
│   │   ├── MixingBowl.kt
│   │   ├── TargetColor.kt
│   │   └── LevelDisplay.kt
│   └── theme/
│       └── Theme.kt               # Material 3 theme
├── game/
│   ├── GameViewModel.kt           # Game state management
│   ├── GameState.kt               # Game state data class
│   ├── ColorMixer.kt              # Color mixing logic
│   └── LevelManager.kt            # Level generation
├── model/
│   └── GameColors.kt              # Color definitions
└── utils/
    ├── SoundManager.kt            # Sound effects (placeholder)
    └── HapticManager.kt           # Haptic feedback
```

## Building the Project

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 8 or higher
- Android SDK with API level 34

### Steps

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to this project directory
4. Wait for Gradle sync to complete
5. Run on an emulator or physical device

### Running

1. Connect an Android device or start an emulator
2. Click the "Run" button in Android Studio
3. Select your device/emulator
4. The app will install and launch automatically

## Game Mechanics

### Color Unlocking

- **Levels 1-3**: Red, Blue, Yellow (primary colors)
- **Levels 4-6**: Orange unlocked
- **Levels 7-9**: Purple unlocked
- **Levels 10+**: Green unlocked

### Gameplay

1. See the target color in the top-right
2. Tap color buttons to add drops to the mixing bowl
3. Watch the bowl color change in real-time
4. Use "Clear Bowl" to start over
5. Tap "Check Match!" when ready
6. If colors match (within tolerance), advance to next level

### Difficulty

- Earlier levels are more forgiving with color matching
- Later levels require more precision
- Tolerance decreases as you progress

## Future Enhancements

- Add actual sound effect files (currently placeholder)
- Add colorblind accessibility mode
- Add hints/tutorial for first-time players
- Save progress across sessions
- Add achievements system
- Parent/teacher dashboard

## License

This is a demonstration project. Feel free to use and modify as needed.

## Credits

Built with ❤️ for kids learning color theory.

