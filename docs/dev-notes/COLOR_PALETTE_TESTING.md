# Color Palette Testing Guide

## What to Test

### 1. Game Start - Color Randomization
**Expected Behavior:**
- Every new game should have a different set of 6 unlockable colors
- Red, Blue, and Green should ALWAYS be available at level 1
- ONE color from each tier should unlock at the correct level:
  - Level 4: Yellow, Cyan, or Gray (randomly chosen)
  - Level 7: Orange, Magenta, or Coral (randomly chosen)
  - Level 10: Purple, Lime, or Turquoise (randomly chosen)
  - Level 13: Pink or Teal (randomly chosen)
  - Level 16: White or Indigo (randomly chosen)
  - Level 19: Black or Brown (randomly chosen)

**How to Test:**
1. Start a new game and progress to level 4
2. Note which color unlocks (should be Yellow, Cyan, or Gray)
3. Restart the game completely (Reset Game button)
4. Progress to level 4 again and check if a different color from that tier unlocks
5. Repeat for each tier to verify randomization works correctly

### 2. Color Consistency Within a Game
**Expected Behavior:**
- The same 6 unlockable colors should persist throughout the entire game session
- Even after restarting from level 1 during a session, colors should remain the same

**How to Test:**
1. Start a game and progress to level 4+ to see the first unlocked color
2. Use the "Restart Game" option
3. Progress again to level 4+ and verify the same color unlocks

### 3. Base Colors Always Available
**Expected Behavior:**
- Red, Blue, and Green should ALWAYS be the first 3 colors
- They should ALWAYS be available from level 1

**How to Test:**
1. Start any new game
2. Verify Red, Blue, and Green are available immediately
3. Repeat multiple times to ensure consistency

### 4. Color Mixing Works with All Colors
**Expected Behavior:**
- Any combination of colors should mix properly
- RGB averaging should produce expected results
- All new colors (Cyan, Magenta, Lime, Teal, Indigo, Brown, Gray, Coral, Turquoise) should mix correctly

**How to Test:**
1. Progress through levels to unlock different colors
2. Try mixing various combinations
3. Verify the mixed color appears reasonable
4. Check that you can successfully match target colors using any available colors

### 5. Level Progression
**Expected Behavior:**
- Colors should unlock at the correct levels: 4, 7, 10, 13, 16, 19
- Math challenges should still trigger before each color unlock
- Target colors should be generateable with any selected color set

**How to Test:**
1. Play through levels 1-19
2. Verify math challenges appear at levels 4, 7, 10, 13, 16, 19
3. Verify a new color unlocks after each successful math challenge

## Expected Color Pool (15 Total Unlockable Colors)

From this pool, 6 are randomly selected each game:

1. **Yellow** (0xFFF1C40F) - Bright yellow
2. **Orange** (0xFFE67E22) - Vibrant orange
3. **Purple** (0xFF9B59B6) - Medium purple
4. **Pink** (0xFFE91E63) - Hot pink
5. **White** (0xFFFFFFFF) - Pure white
6. **Black** (0xFF000000) - Pure black
7. **Cyan** (0xFF00BCD4) - Bright cyan/aqua
8. **Magenta** (0xFFFF00FF) - Bright magenta/fuchsia
9. **Lime** (0xFFCDDC39) - Lime green
10. **Teal** (0xFF009688) - Teal/dark cyan
11. **Indigo** (0xFF3F51B5) - Indigo blue
12. **Brown** (0xFF795548) - Medium brown
13. **Gray** (0xFF9E9E9E) - Medium gray
14. **Coral** (0xFFFF7F50) - Coral/salmon
15. **Turquoise** (0xFF40E0D0) - Turquoise

## Visual Test Checklist

- [ ] Red, Blue, Green appear at start
- [ ] Level 4 unlocks a random color
- [ ] Level 7 unlocks a different random color
- [ ] Level 10 unlocks a different random color
- [ ] Level 13 unlocks a different random color
- [ ] Level 16 unlocks a different random color
- [ ] Level 19 unlocks the 6th random color
- [ ] Math challenges appear before each color unlock
- [ ] Restarting the game gives different unlockable colors
- [ ] Colors mix properly (no crashes or visual glitches)
- [ ] All target colors can be achieved with available colors

## Known Behaviors

### Good Behaviors
- Each game session has fixed colors (for consistency during play)
- Starting a completely new game randomizes the colors again
- The selection is deterministic with a seed (same seed = same colors)

### Edge Cases to Test
1. **Quick Restart**: Rapidly restarting the game should always produce new color sets
2. **Long Play Session**: Playing through all 30 levels should maintain the same color set
3. **Multiple Games**: Playing 5+ games should show variety in color selections

## Debugging Tips

If colors don't seem to randomize:
1. Check that `GameColor.resetColors()` is called before `initializeGameColors()`
2. Verify the ViewModel is being recreated (not reused)
3. Look for any color caching that might prevent randomization

If colors are inconsistent within a game:
1. Check that `selectedUnlockableColors` is not being reset mid-game
2. Verify `getAllColors()` returns the same list throughout a session

## Testing Strategy

### Quick Test (5 minutes)
1. Start game → Note first unlocked color at level 4
2. Restart game → Verify different color unlocks at level 4
3. Repeat 3 times to confirm randomization

### Thorough Test (20 minutes)
1. Complete full game → Note all 6 unlocked colors
2. Restart and replay → Verify all 6 colors are different
3. Test color mixing with each new color
4. Verify no crashes or rendering issues

### Edge Case Test (10 minutes)
1. Restart game 10 times rapidly → Should see variety
2. Check all 15 colors appear across multiple games
3. Verify base colors never change

