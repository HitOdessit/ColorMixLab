# Automatic Version Management

## How It Works

ColorMixLab uses an automatic version incrementing system that updates the app version with each commit.

### Version Format
- **versionName**: `1.X` (e.g., 1.5, 1.6, 1.7)
- **versionCode**: `X` (increments automatically)

Where `X` is the minor version number that increments with each commit.

### Automatic Increment

A git pre-commit hook automatically:
1. Runs **before** the commit is created
2. Reads the current `versionCode` from `app/build.gradle.kts`
3. Increments it by 1
4. Updates the file
5. Stages the change with your commit

This happens automatically with every `git commit` and works smoothly with Android Studio.

### Why Pre-Commit (Not Post-Commit)?

- ✅ Works seamlessly with Android Studio
- ✅ No commit amending (which causes IDE hangs)
- ✅ Version change is part of the commit
- ✅ Faster and more reliable

### Manual Override

If you need to manually set a version:

1. Edit `app/build.gradle.kts`
2. Change the `versionCode` value:
   ```kotlin
   versionCode = 10  // Set to desired version
   ```
3. Commit the change - the next commit will increment from this value

### Viewing Current Version

Check the current version in:
- `app/build.gradle.kts` - Look for `versionCode = X`
- The `versionName` is automatically set to `1.${versionCode}`

### Hook Location

The hook is located at:
```
.git/hooks/pre-commit
```

### Disabling Auto-Increment

To temporarily disable:
```bash
chmod -x .git/hooks/pre-commit
```

To re-enable:
```bash
chmod +x .git/hooks/pre-commit
```

### Troubleshooting

If versions aren't incrementing:

1. **Check hook is executable:**
   ```bash
   ls -la .git/hooks/pre-commit
   ```
   Should show `-rwxr-xr-x` (executable)

2. **Run hook manually to test:**
   ```bash
   .git/hooks/pre-commit
   ```

3. **Check for errors:**
   The hook outputs: `✅ Version auto-incremented: X → Y`

4. **Verify build.gradle.kts format:**
   The hook expects this format:
   ```kotlin
   versionCode = 5  // Comment is optional
   ```

## Version History

The version history is automatically tracked through git commits. To see version changes:

```bash
git log --oneline app/build.gradle.kts
```

## Notes

- The hook runs **before** commit creation (pre-commit)
- Version increments are included in the same commit
- Works seamlessly with Android Studio and command line
- Version updates happen automatically with code changes

