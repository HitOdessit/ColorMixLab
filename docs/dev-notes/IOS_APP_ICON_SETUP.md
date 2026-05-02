# iOS App Icon Setup

## Summary

The iOS app icon has been successfully configured to match the Android app icon design.

## Icon Design

The app icon features the ColorMixLab branding with:
- **Red circle** (top-left) - Primary color
- **Blue circle** (top-right) - Primary color  
- **Yellow circle** (bottom-left) - Primary color
- **Purple mixing bowl** (center) - Represents color mixing
- **Dark outline** around the bowl - For definition

## Files Created

### 1. App Icon PNG
- **Location**: `iosApp/ColorMixLab/ColorMixLab/Assets.xcassets/AppIcon.appiconset/app-icon-1024.png`
- **Size**: 1024x1024 pixels (24 KB)
- **Format**: PNG, 8-bit RGB
- **Purpose**: Main iOS app icon (universal for all iOS devices)

### 2. Icon Generators
Three generator files were created for future updates:

#### `generate_ios_icon_pure.py` (Recommended)
- Pure Python implementation (no external dependencies)
- Generates PPM format and converts to PNG using macOS `sips`
- Usage: `python3 generate_ios_icon_pure.py`

#### `generate_ios_icon.py`
- Python with Pillow library
- Requires: `pip3 install Pillow`
- More flexible for future enhancements

#### `app-icon.svg`
- SVG source file
- Can be edited in vector graphics editors
- Can be converted to PNG at any size

### 3. Swift Utility (Optional)
- **Location**: `iosApp/ColorMixLab/ColorMixLab/Utilities/AppIconGenerator.swift`
- Programmatic icon generation in Swift
- Includes SwiftUI preview for testing
- Useful for dynamic icon generation (if needed in future)

## Asset Configuration

The `AppIcon.appiconset/Contents.json` has been updated to reference the icon for:
- **Light mode**: Standard appearance
- **Dark mode**: Uses same icon (looks good on dark backgrounds)
- **Tinted mode**: For iOS 18+ app icon tinting

## Verification

To verify the icon is properly set up:

1. Open the project in Xcode:
   ```bash
   open iosApp/ColorMixLab/ColorMixLab.xcodeproj
   ```

2. Navigate to:
   - ColorMixLab (project) → ColorMixLab (target) → General tab
   - Check "App Icon" shows the colored circles icon
   
3. Or view in Assets:
   - Project Navigator → Assets.xcassets → AppIcon
   - You should see the icon in all three slots

## Building and Testing

The icon will appear:
- On the iOS home screen
- In the App Store (if published)
- In search results
- In Settings

To test:
1. Build and run on simulator or device
2. Press Home button (or swipe up)
3. Check the app icon on the home screen

## Regenerating the Icon

If you need to modify the icon design:

### Option 1: Edit SVG and Regenerate
```bash
# 1. Edit app-icon.svg with any vector editor
# 2. Run the pure Python generator
python3 generate_ios_icon_pure.py
```

### Option 2: Edit Colors in Python Script
```python
# Edit colors in generate_ios_icon_pure.py:
RED = hex_to_rgb('#E74C3C')      # Change hex values
BLUE = hex_to_rgb('#3498DB')
YELLOW = hex_to_rgb('#F1C40F')
PURPLE = hex_to_rgb('#9B59B6')

# Then run:
python3 generate_ios_icon_pure.py
```

### Option 3: Edit Android Icon and Sync
If you update the Android icon (`app/src/main/res/drawable/ic_launcher_foreground.xml`):
1. Update the color values in `generate_ios_icon_pure.py` to match
2. Regenerate the iOS icon

## Icon Specifications

iOS requires a single 1024x1024 icon, and Xcode automatically generates:
- 20x20pt (2x, 3x)
- 29x29pt (2x, 3x)
- 40x40pt (2x, 3x)
- 60x60pt (2x, 3x)
- 76x76pt (1x, 2x) - iPad
- 83.5x83.5pt (2x) - iPad Pro
- 1024x1024pt - App Store

Our 1024x1024 PNG serves as the universal source.

## Design Notes

- **Background**: White (#FFFFFF) - Clean and professional
- **Color palette matches Android**: Ensures brand consistency across platforms
- **Simple geometric design**: Scales well at all sizes
- **No text**: Better for international markets
- **Clear at small sizes**: Recognizable even at 20x20pt

## Troubleshooting

### Icon not appearing in Xcode
1. Clean build folder: Product → Clean Build Folder
2. Restart Xcode
3. Verify file is in: `AppIcon.appiconset/app-icon-1024.png`

### Icon not appearing on device
1. Delete the app from device/simulator
2. Clean build folder
3. Rebuild and reinstall

### Want different icon for dark mode
1. Generate a second icon variant (e.g., `app-icon-1024-dark.png`)
2. Update `Contents.json` dark mode entry to reference it
3. Consider lighter colors or inverted design

## Resources

- [Apple Human Interface Guidelines - App Icons](https://developer.apple.com/design/human-interface-guidelines/app-icons)
- [SF Symbols](https://developer.apple.com/sf-symbols/) - For system icons
- [Asset Catalog Format](https://developer.apple.com/library/archive/documentation/Xcode/Reference/xcode_ref-Asset_Catalog_Format/)

## Maintenance

- The icon generators are in the project root for easy access
- Keep `app-icon.svg` as the source of truth for the design
- When updating colors, update both Android XML and iOS generators
- Consider creating a color constants file shared between platforms

---

**Created**: December 23, 2025  
**Version**: 1.0  
**Platform**: iOS (Universal)

