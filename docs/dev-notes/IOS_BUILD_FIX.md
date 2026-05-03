# iOS Build Fix - Quick Steps

## What I Did
1. Created corrected `GameViewModel.swift` with proper Swift type names
2. The file is at: `iosApp/ColorMixLab/ColorMixLab/GameViewModel.swift`

## What You Need to Do in Xcode

### Step 1: Remove Old File References
1. Select your **ColorMixLab target**
2. Go to **Build Phases** tab
3. Expand **"Compile Sources"**
4. Remove ALL files with path `iosApp/iosApp/...` (click "-" button)
   - GameViewModel.swift (the old one)
   - MathChallengeScreen.swift
   - All the UI component files
   - All the dialog files

### Step 2: Add New GameViewModel
1. Right-click "ColorMixLab" folder in Project Navigator
2. **Add Files to ColorMixLab...**
3. Select `/Users/valeryb/AndroidStudioProjects/ColorMixLab/iosApp/ColorMixLab/ColorMixLab/GameViewModel.swift`
4. **UNCHECK** "Copy items if needed"
5. Click "Add"

### Step 3: Build
1. **Clean Build Folder** (⇧⌘K)
2. **Build** (⌘B)
3. **Run** (⌘R)

## What You Should See

The app will show the simple test interface with:
- ✅ Shared module working!
- Red and Blue color values from Kotlin code
- Proves the shared framework is linked correctly

## Next Steps

Once this works, we can:
1. Create proper game UI screens
2. Or test the Android app first
3. Whatever you prefer!

## Files to Keep in Build
- ColorMixLabApp.swift
- ContentView.swift
- SimpleGameViewModel.swift
- GameViewModel.swift (new one)
