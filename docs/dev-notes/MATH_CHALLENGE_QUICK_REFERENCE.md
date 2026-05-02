# Math Times Tables Challenge - Quick Reference

## How It Works

### Game Flow
```
Intro Screen (Select Difficulty)
        ↓
Math Challenge Screen (5 Questions)
        ↓
Game Screen (Color Mixing)
        ↓
Math Challenge Dialog (When Unlocking Colors or Milestones)
```

## Challenge Rules

### Initial Challenge (Before Game Starts)
- **Questions Required**: 5 consecutive correct answers
- **Wrong Answer Behavior**: Progress is KEPT (doesn't reset)
- **Purpose**: Gate to start the game

### In-Game Challenges (During Gameplay)
- **Questions Required**: 3 consecutive correct answers
- **Wrong Answer Behavior**: Progress RESETS to 0
- **When They Appear**:
  - Level 4: Unlock Yellow
  - Level 7: Unlock Orange
  - Level 10: Unlock Purple
  - Level 13: Unlock Pink
  - Level 16: Unlock White
  - Level 19: Unlock Black
  - Levels 22, 25, 28: Milestones

## Times Tables by Difficulty and Level

| Difficulty | Levels 1-20 | Levels 21-30 |
|-----------|------------|--------------|
| Easy | 2×, 5×, 10× | 2×, 5×, 10×, 7×, 12× |
| Medium | 3×, 4×, 9×, 11× | 3×, 4×, 9×, 11×, 7×, 12× |
| Hard | 6×, 8× | 6×, 8×, 7×, 12× |

**Note**: 1× is never used

## Answer Feedback by Difficulty

| Difficulty | When Wrong Answer Selected |
|-----------|---------------------------|
| Easy | Show correct answer + wait for OK button click |
| Medium | Show correct answer for 3 seconds, then auto-continue |
| Hard | No answer shown, continue immediately |

## Question Format
- **Display**: "What is X × Y?"
- **Options**: 9 answers in a 3×3 grid
  - 1 correct answer
  - 8 wrong (but plausible) answers
- **Range**: All answers are between 1-150

## Visual Feedback
- ✅ Correct answer: Green button with scale-up animation
- ❌ Wrong answer: Red button with shake animation
- Progress bar shows X/5 or X/3 completion
- Haptic feedback on every answer
- Confetti when completing initial 5 questions

## Timer Behavior
- Timer pauses automatically when math challenges appear
- Timer resumes when challenge is completed
- No time penalty for math challenges

## Testing the Feature

### Quick Test Path
1. Launch app → Select difficulty
2. Answer 5 math questions (can get some wrong)
3. Start playing color mixing game
4. Progress to level 4 → Math challenge appears
5. Answer 3 questions correctly to unlock Yellow
6. Continue to level 7, 10, 13, 16, 19 for more challenges
7. After level 19, challenges appear every 3 levels (22, 25, 28)

### What to Look For
- [ ] Math screen appears after selecting difficulty
- [ ] Progress counter works correctly
- [ ] Wrong answers don't reset counter on initial screen
- [ ] Wrong answers DO reset counter in-game dialog
- [ ] Correct answer feedback shows based on difficulty
- [ ] Colors unlock after completing challenge
- [ ] Timer pauses during challenges
- [ ] Animations are smooth
- [ ] Haptic feedback works
- [ ] Questions use correct times tables for difficulty

## Code Structure

```
app/src/main/java/com/colormixlab/
├── game/
│   ├── math/
│   │   ├── MathQuestion.kt          (Data models)
│   │   └── MathQuestionGenerator.kt (Question generation)
│   ├── GameState.kt                 (Added math challenge fields)
│   └── GameViewModel.kt             (Challenge triggers & completion)
├── ui/
│   ├── MathChallengeScreen.kt       (Initial 5-question screen)
│   ├── GameScreen.kt                (Shows dialog when needed)
│   └── components/
│       └── MathChallengeDialog.kt   (In-game 3-question dialog)
└── MainActivity.kt                  (Navigation flow)
```

## Troubleshooting

**Challenge doesn't appear at level 4/7/etc:**
- Check that `needsMathChallenge` is being set in `GameViewModel.nextLevel()`
- Verify color unlock levels match: 4, 7, 10, 13, 16, 19

**Timer doesn't pause:**
- Check `LaunchedEffect` in `GameScreen.kt` includes `state.needsMathChallenge`

**Wrong times tables appear:**
- Verify `MathQuestionGenerator.selectTimesTable()` logic
- Check difficulty and level parameters

**Progress doesn't reset/keep:**
- Initial screen: Check it increments but doesn't reset
- Dialog: Check it resets `consecutiveCorrect` to 0 on wrong answer

