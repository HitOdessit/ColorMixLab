# Color Unlock Dialog Fix

## Issue
The level completion dialog was showing hardcoded color names (e.g., "Yellow Unlocked!" at level 3), which didn't match the randomly selected colors from the tier-based system.

## Root Cause
The dialog used a `when (level)` statement with hardcoded color names and RGB values:
```kotlin
when (level) {
    3 -> Text("🎨 Yellow Unlocked!", color = Color(0xFFF1C40F))
    6 -> Text("🧡 Orange Unlocked!", color = Color(0xFFE67E22))
    // etc...
}
```

This ignored the actual color selected from each tier, so if the game randomly chose "Cyan" for level 4, the dialog would still say "Yellow Unlocked!"

## Solution

### 1. Pass Unlocked Colors to Dialog
Added `unlockedColors: List<GameColor>` parameter to `ResultDialog`:

```kotlin
fun ResultDialog(
    // ... other params
    unlockedColors: List<GameColor>,  // NEW
    // ... other params
)
```

### 2. Dynamically Determine Newly Unlocked Color
Added logic to find the actual color that will unlock at the next level:

```kotlin
val newlyUnlockedColor = remember(level, unlockedColors) {
    if (isSuccess && level in listOf(3, 6, 9, 12, 15, 18)) {
        val nextLevel = level + 1
        // Find the color that will unlock at the next level
        GameColor.getAllColors().find { it.unlockLevel == nextLevel }
    } else {
        null
    }
}
```

**Logic Explanation:**
- When player completes level 3, the dialog checks for a color with `unlockLevel == 4`
- This will return whichever color was randomly selected for level 4 (Yellow, Cyan, or Gray)
- Same logic applies for all other unlock levels (7, 10, 13, 16, 19)

### 3. Dynamic Display
Replaced 6 hardcoded `when` branches with a single dynamic display:

```kotlin
if (isSuccess && newlyUnlockedColor != null) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = newlyUnlockedColor.rgb.copy(alpha = 0.15f)
        )
    ) {
        Column {
            // Color preview box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = newlyUnlockedColor.rgb,
                        shape = RoundedCornerShape(12.dp)
                    )
            )
            // Dynamic text
            Text(
                text = "🎨 ${newlyUnlockedColor.name} Unlocked!",
                color = newlyUnlockedColor.rgb
            )
        }
    }
}
```

## Benefits

✅ **Accurate**: Shows the actual randomly selected color  
✅ **Dynamic**: Works with any color from any tier  
✅ **Visual Preview**: Added a colored box showing the actual color  
✅ **Cleaner Code**: Reduced from ~130 lines to ~35 lines  
✅ **Maintainable**: Adding new colors requires no changes to UI code  

## Testing

### Before Fix
- Complete level 3 → Always showed "Yellow Unlocked!" (even if Cyan was selected)
- Complete level 6 → Always showed "Orange Unlocked!" (even if Magenta was selected)

### After Fix
- Complete level 3 → Shows actual level 4 color selected (Yellow, Cyan, or Gray)
- Complete level 6 → Shows actual level 7 color selected (Orange, Magenta, or Coral)
- Complete level 9 → Shows actual level 10 color selected (Purple, Lime, or Turquoise)
- Complete level 12 → Shows actual level 13 color selected (Pink or Teal)
- Complete level 15 → Shows actual level 16 color selected (White or Indigo)
- Complete level 18 → Shows actual level 19 color selected (Black or Brown)

## Visual Improvement

The new dialog also includes a **visual color preview** (48x48dp colored box) showing the actual color, making it even clearer what's being unlocked.

## Files Modified
1. `GameScreen.kt`:
   - Added `GameColor` import
   - Updated `ResultDialog` call to pass `unlockedColors`
   - Updated `ResultDialog` function signature
   - Replaced hardcoded unlock notifications with dynamic system
   - Added color preview box

