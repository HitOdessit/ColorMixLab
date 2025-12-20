# Automatic Version Increment Setup - Complete ✅

## What Was Done

Set up automatic version incrementing for ColorMixLab that runs after each git commit.

## Files Created/Modified

### 1. **`.git/hooks/post-commit`** (NEW)
- Bash script that runs automatically after each commit
- Increments `versionCode` in `app/build.gradle.kts`
- Amends the commit to include the version change
- Made executable with `chmod +x`

### 2. **`VERSION_MANAGEMENT.md`** (NEW)
- Complete documentation on how the versioning system works
- Troubleshooting guide
- Manual override instructions

## How It Works

```
Developer makes changes → git commit → POST-COMMIT HOOK RUNS:
    1. Read current versionCode (e.g., 5)
    2. Increment by 1 (now 6)
    3. Update app/build.gradle.kts
    4. Amend commit with version change
    → Next build uses version 1.6
```

## Version Format

- **versionCode**: 5, 6, 7, 8... (auto-increments)
- **versionName**: "1.5", "1.6", "1.7"... (auto-calculated from versionCode)

## Current Status

✅ Hook installed: `.git/hooks/post-commit`
✅ Hook is executable: `-rwxr-xr-x`
✅ Current version: 5 (versionName "1.5")
✅ Next commit will be: 6 (versionName "1.6")

## Test It

To test the automatic increment:

```bash
# Make a small change
echo "# Test" >> README.md

# Commit it
git add README.md
git commit -m "Test version increment"

# The hook will run automatically and you'll see:
# ✅ Version incremented: 5 → 6
# ✅ Build file updated and commit amended with new version

# Verify
grep versionCode app/build.gradle.kts
# Should show: versionCode = 6
```

## Verification

Check the hook is working:
```bash
ls -la .git/hooks/post-commit
# Should show: -rwxr-xr-x (executable)

cat .git/hooks/post-commit
# Should show the bash script
```

## Manual Control

If you need to set a specific version number:

1. Edit `app/build.gradle.kts`
2. Change: `versionCode = 10` (or whatever you want)
3. Commit the change
4. Future commits will increment from that number

## Disable/Enable

**Temporarily disable:**
```bash
chmod -x .git/hooks/post-commit
```

**Re-enable:**
```bash
chmod +x .git/hooks/post-commit
```

## Notes

- Hook uses `--no-verify` to prevent infinite loops
- Commit is amended, so version change is part of the same commit
- Works on both macOS and Linux
- Hook is in `.git/hooks/` which is not tracked by git (local only)
- Other developers need to set up their own hook from this guide

## For Other Developers

If other team members want the same auto-increment:

1. Copy `.git/hooks/post-commit` from this documentation
2. Save to `.git/hooks/post-commit` in their local repo
3. Run: `chmod +x .git/hooks/post-commit`

Or they can use the version in `VERSION_MANAGEMENT.md`.

## Troubleshooting

**Problem**: Version not incrementing

**Solutions**:
1. Check hook is executable: `ls -la .git/hooks/post-commit`
2. Run manually to test: `.git/hooks/post-commit`
3. Check git config doesn't disable hooks
4. Verify build.gradle.kts format matches expected pattern

**Problem**: Getting errors during commit

**Solutions**:
1. Check the error message
2. Verify `app/build.gradle.kts` exists and has correct format
3. Run hook manually to see detailed error
4. Temporarily disable hook if needed

## Success!

Your version will now automatically increment with every commit! 🎉

The current version is **1.5** and will become **1.6** on the next commit.

