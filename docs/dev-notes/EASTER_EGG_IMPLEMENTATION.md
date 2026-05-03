# Easter Egg: Double-Click Version Number

## Overview
Added a hidden easter egg feature that triggers the game completion celebration animation when the user double-clicks on the app version number on the intro screen.

## Implementation Details

### Feature Description
- **Trigger**: Double-click on the version number text (e.g., "v1.5")
- **Action**: Shows the full-screen `GameCompletionCelebration` animation
- **Duration**: ~8.8 seconds of colorful celebration animation
- **Location**: Available on both portrait and landscape intro screens

### Technical Implementation

#### Files Modified
- `/app/src/main/java/com/colormixlab/ui/IntroScreen.kt`

#### Key Changes

1. **Added Imports**:
   ```kotlin
   import androidx.compose.foundation.gestures.detectTapGestures
   import androidx.compose.ui.input.pointer.pointerInput
   import com.colormixlab.ui.components.GameCompletionCelebration
   import kotlinx.coroutines.delay
   ```

2. **IntroScreen Composable**:
   - Added `showCelebration` state to track when to show animation
   - Animation displays as full-screen overlay when triggered
   - Returns to normal intro screen after animation completes (~8.8 seconds)
   - Passes `onVersionDoubleClick` callback to both portrait and landscape screens

3. **IntroScreenPortrait**:
   - Added `onVersionDoubleClick: () -> Unit` parameter
   - Added `lastClickTime` state variable to track double-click timing
   - Modified version Text modifier with `pointerInput` gesture detection:
     - Detects tap gestures on version text
     - Compares current tap time with last tap time
     - If < 500ms apart, triggers the celebration animation
     - Otherwise, updates last click time for next comparison

4. **IntroScreenLandscape**:
   - Same implementation as portrait
   - Double-click detection on the version text in subtitle line
   - Version appears as: "v1.5 • Match colors • 30 levels • Beat the timer"

### Double-Click Detection Logic

```kotlin
var lastClickTime by remember { mutableStateOf(0L) }

modifier = Modifier.pointerInput(Unit) {
    detectTapGestures(
        onTap = {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < 500) { // 500ms threshold
                onVersionDoubleClick()
            }
            lastClickTime = currentTime
        }
    )
}
```

**Threshold**: 500 milliseconds between taps to register as double-click

### Animation Details

The triggered animation is the same `GameCompletionCelebration` used when completing all 30 levels:

- **Phase 1** (1.5s): Circles appear with bouncy entrance
- **Phase 2** (2s): Circles bounce and dance around screen
- **Phase 3** (2.5s): Circles merge toward center
- **Phase 4** (2s): Final burst with rings, sparkles, and confetti
- **Text**: "🎉 Amazing! You completed all levels!"

### User Experience

1. User opens the app and sees the intro screen
2. User notices version number below title (subtle, gray text)
3. User double-clicks/double-taps on version number
4. Full-screen celebration animation plays
5. After ~8.8 seconds, animation completes and returns to intro screen
6. User can still start game normally

### Easter Egg Benefits

- **Fun Discovery**: Rewards curious users who interact with UI elements
- **Quality Polish**: Shows attention to detail and playful design
- **No Impact**: Doesn't interfere with normal game flow
- **Replayable**: Can be triggered multiple times
- **Universal**: Works on all devices (phones, tablets) and orientations

## Testing

To test the feature:

1. Launch the app (lands on intro screen)
2. Quickly double-tap the version number text (e.g., "v1.5")
3. Observe the full-screen celebration animation
4. Wait for animation to complete
5. Verify return to intro screen
6. Test in both portrait and landscape orientations

## Code Quality

- ✅ No linter errors
- ✅ Follows existing code style
- ✅ Uses Compose best practices
- ✅ Proper state management with `remember`
- ✅ Gesture detection with standard Compose APIs
- ✅ Works in both orientations
- ✅ No performance impact (gesture handler is lightweight)

## Future Enhancements (Optional)

- Add haptic feedback on double-click detection
- Track usage analytics (how many users discover the easter egg)
- Add more easter eggs with different tap patterns (triple-click, long-press, etc.)
- Show a different animation variant each time
- Add a secret counter that shows how many times user found the easter egg

