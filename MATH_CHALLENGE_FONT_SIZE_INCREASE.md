# Math Challenge Font Size Optimization

## Summary

Optimized the font sizes in math challenge dialogs for better balance:
- **Question font**: Increased by 1.5x (50% more than original)
- **Answer button font**: Increased by 3x (for maximum readability)

## Changes Made

### Android

**File**: `app/src/main/java/com/colormixlab/ui/components/MathQuestionGrid.kt`

- **Question font size** (e.g., "2 × 5 = ?"): 
  - Original: `(availableWidth * 0.04f).coerceIn(18f, 42f)` sp
  - After: `(availableWidth * 0.06f).coerceIn(27f, 63f)` sp (1.5x increase)

- **Answer button font size**: 
  - Original: `(availableWidth * 0.028f).coerceIn(12f, 28f)` sp
  - After: `(availableWidth * 0.084f).coerceIn(36f, 84f)` sp (3x increase)

### iOS

**Files**: 
- `iosApp/ColorMixLab/ColorMixLab/UI/Screens/MathChallengeScreen.swift`
- `iosApp/iosApp/UI/MathChallengeScreen.swift`

- **Question numbers** (e.g., 2, 5, ?): 
  - Original: 64 pt
  - After: 96 pt (1.5x increase)

- **Question operators** (× and =): 
  - Original: 48 pt
  - After: 72 pt (1.5x increase)

- **Answer button numbers**: 
  - Original: 28 pt
  - After: 84 pt (3x increase - unchanged)

## Visual Impact

### Question Display (1.5x)
- Moderately larger than original
- Easy to read without overwhelming the screen
- Balanced with answer buttons

### Answer Buttons (3x)
- Significantly larger for quick recognition
- Most important for user interaction
- Easier to tap and read during timed challenges

## Design Rationale

The different scaling creates a visual hierarchy:
1. **Answer buttons** (3x) - Primary interaction elements, need maximum visibility
2. **Question** (1.5x) - Moderately enlarged, readable but not dominant
3. This creates better screen balance and focuses attention on the interactive elements

## Testing

To test the changes:

1. **Android**: Build and run the app
   - Trigger a math challenge (after 3 color mixes or milestone)
   - Observe the balanced question and large answer buttons

2. **iOS**: Build and run the iOS app
   - Trigger a math challenge
   - Verify the optimized font sizes

## Adaptive Sizing

The fonts are still responsive and adapt to:
- Screen size (phone vs tablet)
- Orientation (portrait vs landscape)
- Available space

The scaling factors ensure optimal readability while maintaining good layout proportions.

---

**Date**: December 23, 2025  
**Issue**: Initial 3x increase for question was too large  
**Solution**: Reduced question to 1.5x, kept answer buttons at 3x for optimal balance

