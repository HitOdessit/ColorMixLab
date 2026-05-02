# Intro Screen & Timer System Implementation Plan (Updated)

## Overview
Transform ColorMixLab into a more complete game experience with a proper intro screen, difficulty selection, and countdown timer mechanics. This adds strategic depth and replayability.

## Key Changes from Original Plan
- **Medium timer**: 40 seconds (changed from 60s)
- **Hard timer**: 20 seconds (changed from 30s)
- **Timer format**: Seconds only (e.g., "45") instead of MM:SS
- **Leaderboard**: Must show both difficulty AND level achieved
- **Intro screen**: Includes leaderboard button

## Key Features to Implement

### 1. Difficulty System
Create three game modes with different mechanics:

**Easy Mode:**
- No timer (unlimited time per level)
- All points multiplied by 0.5x (halved)
- Good for learning and young kids

**Medium Mode (Default):**
- **40 seconds per level**
- Normal points (1.0x multiplier)
- Balanced challenge

**Hard Mode:**
- **20 seconds per level**
- All points multiplied by 1.5x
- For experienced players

### 2. Intro/Welcome Screen
**Components:**
- App title/logo
- Brief instructions on how to play
- Difficulty selector (3 buttons)
- "Start Game" button
- **"Leaderboard" button** (opens leaderboard dialog)

**Instructions to show:**
- Match the target color by mixing colors
- Need 80% similarity to advance
- Complete all 30 levels
- Higher similarity = more points
- Higher difficulty = more points

**Layout:**
```
┌─────────────────────┐
│   ColorMixLab 🎨    │
├─────────────────────┤
│  Instructions:      │
│  • Match colors     │
│  • 80% to advance   │
│  • 30 levels total  │
│  • More time = less │
│    points!          │
├─────────────────────┤
│  [ Easy - No Timer ]│
│  [Medium - 40s]     │ ← Radio/Toggle style
│  [ Hard - 20s ]     │
├─────────────────────┤
│   [ Start Game ]    │
│   [Leaderboard]     │
└─────────────────────┘
```

### 3. Countdown Timer System
**Display:**
- Position: Top center of game screen (between level/score and target)
- **Shows: Seconds only** (e.g., "45" or "3")
- **Format: Just the number**, no MM:SS, with "s" suffix optional
- Visual states:
  - Normal (>5s): White/theme color
  - Warning (≤5s): **Blinking red**

**Behavior:**
- Starts when level begins
- Pauses when dialogs are open
- Resumes when dialogs close
- Resets on level advance/retry
- **Time out = -50 points + auto-advance to next level**

**Technical:**
- Use Compose `LaunchedEffect` with coroutines
- Timer state in `GameState`
- Blink animation using `animateFloatAsState`
- Blink frequency: ~2 times per second

### 4. Updated Game State
Add to `GameState.kt`:
```kotlin
enum class Difficulty {
    EASY,    // No timer, 0.75x points
    MEDIUM,  // 40s, 1.0x points + time bonus
    HARD     // 20s, 1.25x points + time bonus
}

data class GameState(
    // ... existing fields
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val timeRemainingSeconds: Int? = null,  // null for Easy mode
    val isTimerActive: Boolean = false,
    val isTimerPaused: Boolean = false
)

companion object {
    const val MAX_LEVEL = 30
    
    fun getTimerDuration(difficulty: Difficulty): Int? {
        return when (difficulty) {
            Difficulty.EASY -> null  // No timer
            Difficulty.MEDIUM -> 40
            Difficulty.HARD -> 20
        }
    }
}
```

### 5. Point Calculation Update
Modify `calculatePoints()` in `GameViewModel.kt`:
```kotlin
fun calculatePoints(similarity: Float): Int {
    val basePoints = when {
        similarity >= 1.0f -> 150  // Perfect
        similarity >= 0.95f -> 100
        similarity >= 0.90f -> 80
        similarity >= 0.85f -> 60
        similarity >= 0.80f -> 40   // Pass threshold
        similarity >= 0.75f -> -10
        similarity >= 0.70f -> -15
        similarity >= 0.65f -> -20
        similarity >= 0.60f -> -25
        similarity >= 0.50f -> -35
        else -> -50
    }
    
    val multiplier = when (_gameState.value.difficulty) {
        Difficulty.EASY -> 0.5f
        Difficulty.MEDIUM -> 1.0f
        Difficulty.HARD -> 1.5f
    }
    
    return (basePoints * multiplier).toInt()
}
```

### 6. Timer Management
Add to `GameViewModel.kt`:
```kotlin
private var timerJob: Job? = null

fun startTimer() {
    val duration = GameState.getTimerDuration(_gameState.value.difficulty) ?: return
    
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
        _gameState.value = _gameState.value.copy(
            timeRemainingSeconds = duration,
            isTimerActive = true,
            isTimerPaused = false
        )
        
        while (_gameState.value.timeRemainingSeconds!! > 0) {
            delay(1000)
            if (!_gameState.value.isTimerPaused) {
                val newTime = _gameState.value.timeRemainingSeconds!! - 1
                _gameState.value = _gameState.value.copy(
                    timeRemainingSeconds = newTime
                )
                
                if (newTime == 0) {
                    onTimerExpired()
                }
            }
        }
    }
}

fun pauseTimer() {
    _gameState.value = _gameState.value.copy(isTimerPaused = true)
}

fun resumeTimer() {
    _gameState.value = _gameState.value.copy(isTimerPaused = false)
}

fun cancelTimer() {
    timerJob?.cancel()
    _gameState.value = _gameState.value.copy(
        isTimerActive = false,
        timeRemainingSeconds = null
    )
}

fun onTimerExpired() {
    // Deduct 50 points
    val newScore = (_gameState.value.currentScore - 50).coerceAtLeast(0)
    _gameState.value = _gameState.value.copy(currentScore = newScore)
    
    // Auto-advance to next level
    nextLevel()
}
```

---

## Implementation Steps

### Step 1: Create Difficulty Enum and Update GameState
**File:** [`app/src/main/java/com/colormixlab/game/GameState.kt`](app/src/main/java/com/colormixlab/game/GameState.kt)
- Add `Difficulty` enum with EASY, MEDIUM, HARD
- Add `difficulty`, `timeRemainingSeconds`, `isTimerActive`, `isTimerPaused` fields
- Add `getTimerDuration()` helper function
- Default difficulty: MEDIUM

### Step 2: Create Intro Screen
**File:** `app/src/main/java/com/colormixlab/ui/IntroScreen.kt` (NEW)
- Title: "ColorMixLab 🎨" (large, centered)
- Instructions card with bullet points
- Three difficulty buttons (toggle/radio style):
  - Easy: Green background, "🟢 Easy - No Timer (75% Points)"
  - Medium: Yellow background, "🟡 Medium - 40s (100% Points + Time Bonus)" [default selected]
  - Hard: Red background, "🔴 Hard - 20s (125% Points + Time Bonus)"
- Large "Start Game" button
- "Leaderboard" button (opens leaderboard dialog)

### Step 3: Update MainActivity Navigation
**File:** [`app/src/main/java/com/colormixlab/MainActivity.kt`](app/src/main/java/com/colormixlab/MainActivity.kt)
- Add navigation state: `var currentScreen by remember { mutableStateOf<Screen>(Screen.Intro) }`
- Add difficulty state: `var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIUM) }`
- Show IntroScreen or GameScreen based on currentScreen
- Pass difficulty to GameScreen/ViewModel

### Step 4: Implement Timer in GameViewModel
**File:** [`app/src/main/java/com/colormixlab/game/GameViewModel.kt`](app/src/main/java/com/colormixlab/game/GameViewModel.kt)
- Add timer coroutine with countdown logic
- Start timer on level start (if not Easy mode)
- Pause when dialogs open
- Resume when dialogs close
- Cancel and reset on level change
- Handle expiration: -50 points + auto-advance

### Step 5: Add Timer UI to GameScreen
**File:** [`app/src/main/java/com/colormixlab/ui/GameScreen.kt`](app/src/main/java/com/colormixlab/ui/GameScreen.kt)
- Add timer display at top center (between top row elements)
- **Format: Just seconds** (e.g., "45" with small icon)
- Blink animation when ≤5 seconds (alpha fade in/out)
- Red color when ≤5 seconds
- For Easy mode: Show "∞" or hide timer
- Pause timer when showSuccessDialog or showMenu is true

### Step 6: Update Point Calculations
**File:** [`app/src/main/java/com/colormixlab/game/GameViewModel.kt`](app/src/main/java/com/colormixlab/game/GameViewModel.kt)
- Apply difficulty multipliers to all point calculations
- Easy: 0.5x, Medium: 1.0x, Hard: 1.5x
- Update score display to show applied points
- Ensure negative points work correctly with multipliers

### Step 7: Update Menu Restart Logic
**File:** [`app/src/main/java/com/colormixlab/ui/GameScreen.kt`](app/src/main/java/com/colormixlab/ui/GameScreen.kt)
- **Change restart behavior**: Return to intro screen (not just level 1)
- Cancel any active timer
- Clear all game state
- Allow player to reselect difficulty

### Step 8: Update Leaderboard System
**Files:**
- [`app/src/main/java/com/colormixlab/model/LeaderboardEntry.kt`](app/src/main/java/com/colormixlab/model/LeaderboardEntry.kt) - Add difficulty field
- [`app/src/main/java/com/colormixlab/data/LeaderboardManager.kt`](app/src/main/java/com/colormixlab/data/LeaderboardManager.kt) - Store/load difficulty
- [`app/src/main/java/com/colormixlab/ui/LeaderboardDialog.kt`](app/src/main/java/com/colormixlab/ui/LeaderboardDialog.kt) - Display difficulty badge and level

**Leaderboard Entry Display:**
```
#1  PlayerName        2500 pts
    Hard • Level 30   🔴

#2  PlayerTwo         2200 pts
    Medium • Level 30 🟡
```

---

## Visual Design Specs

### Timer Display
```
Normal (>5s):  ⏱ 45  (white/theme color, bold)
Warning (≤5s): ⏱ 3   (blinking red, pulsing)
Easy mode:     ∞     (gray, smaller) or hidden
```

### Difficulty Buttons (Intro Screen)
```
┌──────────────────────────┐
│ 🟢 Easy                   │
│ No Timer • 75% Points    │
└──────────────────────────┘

┌──────────────────────────┐
│ 🟡 Medium ✓              │ ← Selected (checkmark)
│ 40s • 100% Pts + Bonus   │
└──────────────────────────┘

┌──────────────────────────┐
│ 🔴 Hard                   │
│ 20s • 125% Pts + Bonus   │
└──────────────────────────┘
```

### Intro Screen Style
- Clean, centered layout
- Large readable text (instructions: 16-18sp)
- Friendly colors matching difficulty
- Difficulty buttons: Full width, 64dp height
- Start button: Primary color, prominent
- Leaderboard button: Outlined, secondary

---

## Testing Scenarios

### Timer Functionality
- [ ] Timer counts down correctly (40s Medium, 20s Hard)
- [ ] Timer shows ∞ or hidden on Easy mode
- [ ] Timer pauses when success dialog opens
- [ ] Timer pauses when menu opens
- [ ] Timer resumes when dialogs close
- [ ] Timer blinks red at ≤5 seconds (2 blinks/sec)
- [ ] Time expiration deducts exactly 50 points
- [ ] Time expiration auto-advances to next level
- [ ] Timer resets correctly on new level
- [ ] Timer cancels on game restart

### Difficulty System
- [ ] Easy: No timer, all points × 0.5
- [ ] Medium: 40s timer, all points × 1.0
- [ ] Hard: 20s timer, all points × 1.5
- [ ] Point multipliers apply to positive points
- [ ] Point multipliers apply to negative points
- [ ] Leaderboard shows difficulty badge
- [ ] **Leaderboard shows level achieved**

### Intro Screen
- [ ] Shows on app first launch
- [ ] Shows after restart from menu
- [ ] Difficulty selection works (toggle behavior)
- [ ] Default is Medium selected
- [ ] Start button launches game with selected difficulty
- [ ] **Leaderboard button opens leaderboard from intro**
- [ ] Instructions are clear and visible
- [ ] Buttons are readable and sized properly

### Game Flow
- [ ] Intro → Select difficulty → Start → Play → Complete → Nickname → Leaderboard
- [ ] **Restart returns to intro screen** (not directly to game)
- [ ] Can view leaderboard from intro screen
- [ ] Can change difficulty on restart
- [ ] Timer behavior correct for selected difficulty

---

## Files to Create
1. **`IntroScreen.kt`** - New welcome/setup screen with difficulty selection

## Files to Modify
1. **`GameState.kt`** - Add Difficulty enum, timer fields
2. **`GameViewModel.kt`** - Timer logic, point multipliers, lifecycle management
3. **`GameScreen.kt`** - Timer display, pause/resume logic
4. **`MainActivity.kt`** - Navigation between intro and game screens
5. **`LeaderboardEntry.kt`** - Add difficulty field (already has level)
6. **`LeaderboardManager.kt`** - Store/load difficulty field
7. **`LeaderboardDialog.kt`** - Display difficulty badge AND level prominently

---

## Important Notes

### Timer Format
**Display seconds only**: "45", "20", "5"
- No MM:SS format
- Optional: Add "s" suffix for clarity ("45s")
- Keep it simple and readable

### Leaderboard Requirements
**Must show BOTH**:
1. Difficulty (Easy/Medium/Hard) - with colored badge
2. Level achieved (already in data model, ensure UI shows it)

Example entry:
```
#1  ChampionKid     2800 pts
    🔴 Hard • Lvl 30
```

### Restart Behavior
**Critical**: Restart must return to intro screen, NOT directly start a new game
- Allows difficulty reselection
- Better UX for trying different modes
- Consistent with "new game" concept

---

## Success Criteria
✅ Intro screen displays with clear instructions
✅ Three difficulty buttons work correctly
✅ Leaderboard accessible from intro screen
✅ Timer shows **seconds only** (e.g., "45")
✅ Medium: 40s timer, Hard: 20s timer
✅ Timer blinks red at ≤5 seconds
✅ Time expiration: -50 pts + auto-advance
✅ Restart returns to intro screen
✅ Point multipliers: Easy 0.75x, Medium 1.0x, Hard 1.25x
✅ Time bonus for Medium and Hard (up to 50 pts)
✅ Leaderboard shows difficulty AND level
✅ Timer pauses/resumes with dialogs

This updated plan addresses all requested changes and provides a complete implementation roadmap!

