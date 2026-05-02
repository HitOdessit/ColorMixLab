# Version Tracking System

## Format: Major.Minor

- **Major Version**: Updated manually when significant features are added
  - Current: `1`
  - Location: `app/build.gradle.kts` (line 23)

- **Minor Version**: Auto-incremented with each commit
  - Current: `5`
  - Location: `app/build.gradle.kts` (versionCode, line 14)

## How to Update

### For Each Commit:
Increment `versionCode` in `app/build.gradle.kts`:
```kotlin
versionCode = 6  // Increment by 1
```

### For Major Version Updates:
Update the major version number in two places in `app/build.gradle.kts`:
1. Line 15: `versionName = "2.${versionCode}"`
2. Line 23: `buildConfigField("int", "MAJOR_VERSION", "2")`

## Display

Version is displayed on the intro screen below the app name as "v1.5" (small gray text).
