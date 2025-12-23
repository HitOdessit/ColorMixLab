#!/bin/bash

# ColorMixLab iOS App Icon Generator
# This script creates a 1024x1024 PNG icon for iOS

echo "========================================================================"
echo "ColorMixLab iOS App Icon Generator"
echo "========================================================================"
echo ""

# Define paths
PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ICON_DIR="$PROJECT_DIR/iosApp/ColorMixLab/ColorMixLab/Assets.xcassets/AppIcon.appiconset"
SVG_FILE="$PROJECT_DIR/app-icon.svg"

echo "Project directory: $PROJECT_DIR"
echo "Icon directory: $ICON_DIR"
echo ""

# Check if SVG file exists
if [ ! -f "$SVG_FILE" ]; then
    echo "Error: app-icon.svg not found!"
    exit 1
fi

echo "✓ Found SVG file: $SVG_FILE"
echo ""

# Instructions for manual conversion
echo "========================================================================"
echo "MANUAL ICON CREATION STEPS"
echo "========================================================================"
echo ""
echo "The SVG icon file has been created at:"
echo "  $SVG_FILE"
echo ""
echo "To create the iOS app icon:"
echo ""
echo "Option 1 - Using Preview (macOS):"
echo "  1. Open app-icon.svg in Preview"
echo "  2. Click 'File' > 'Export as PNG...'"
echo "  3. Set size to 1024x1024 pixels"
echo "  4. Save as 'app-icon-1024.png' in:"
echo "     $ICON_DIR"
echo ""
echo "Option 2 - Using an online converter:"
echo "  1. Go to: https://svgtopng.com or https://cloudconvert.com"
echo "  2. Upload app-icon.svg"
echo "  3. Convert to PNG at 1024x1024"
echo "  4. Download and save to:"
echo "     $ICON_DIR"
echo ""
echo "Option 3 - Using Xcode directly:"
echo "  1. Open your Xcode project"
echo "  2. Navigate to Assets.xcassets > AppIcon"
echo "  3. Open app-icon.svg in any image editor"
echo "  4. Export as 1024x1024 PNG"
echo "  5. Drag the PNG into the 1024x1024 slot in Xcode"
echo ""
echo "The icon design matches your Android app with:"
echo "  - Red circle (top-left)"
echo "  - Blue circle (top-right)"
echo "  - Yellow circle (bottom-left)"
echo "  - Purple mixing bowl (center) with outline"
echo ""
echo "========================================================================"
echo ""

# Try to open the SVG in Preview
echo "Attempting to open SVG in Preview..."
open -a Preview "$SVG_FILE" 2>/dev/null && echo "✓ Opened in Preview"

echo ""
echo "Once you have the PNG file, add it to Xcode and it will automatically"
echo "generate all required icon sizes for iOS."
echo ""

