# Automatic Version Management

## How It Works

ColorMixLab uses an automatic version incrementing system that updates the app version after each commit.

### Version Format
- **versionName**: `1.X` (e.g., 1.5, 1.6, 1.7)
- **versionCode**: `X` (increments automatically)

Where `X` is the minor version number that increments with each commit.

### Automatic Increment

A git post-commit hook automatically:
1. Reads the current `versionCode` from `app/build.gradle.kts`
2. Increments it by 1
3. Updates the file
4. Amends the commit to include the version change

This happens automatically after every `git commit`.

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
.git/hooks/post-commit
```

### Disabling Auto-Increment

To temporarily disable:
```bash
chmod -x .git/hooks/post-commit
```

To re-enable:
```bash
chmod +x .git/hooks/post-commit
```

### Troubleshooting

If versions aren't incrementing:

1. **Check hook is executable:**
   ```bash
   ls -la .git/hooks/post-commit
   ```
   Should show `-rwxr-xr-x` (executable)

2. **Run hook manually to test:**
   ```bash
   .git/hooks/post-commit
   ```

3. **Check for errors:**
   The hook outputs status messages. If you don't see:
   ```
   ✅ Version incremented: X → Y
   ✅ Build file updated and commit amended with new version
   ```
   There may be an issue.

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

- The hook uses `--no-verify` to prevent infinite loops
- Version increments happen **after** the commit is created
- The commit is amended to include the version change
- This keeps version updates atomic with code changes

