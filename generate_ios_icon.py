#!/usr/bin/env python3
"""
Generate iOS app icon that matches the Android app icon design.
This creates a 1024x1024 PNG icon with colored circles and a mixing bowl.

The icon matches the design from ic_launcher_foreground.xml:
- Red circle (top-left)
- Blue circle (top-right)  
- Yellow circle (bottom-left)
- Purple mixing bowl (center) with dark outline
"""

try:
    from PIL import Image, ImageDraw
    import os
except ImportError:
    print("Error: PIL (Pillow) is required. Install it with:")
    print("  pip install Pillow")
    print("  or")
    print("  pip3 install Pillow")
    exit(1)

def generate_icon(size=1024):
    """Generate the ColorMixLab app icon."""
    
    # Create a new image with white background
    image = Image.new('RGB', (size, size), color='white')
    draw = ImageDraw.Draw(image)
    
    # Scale factor from the original 108x108 design space
    scale = size / 108.0
    
    # Color definitions (matching Android colors)
    RED = '#E74C3C'      # Red circle
    BLUE = '#3498DB'     # Blue circle
    YELLOW = '#F1C40F'   # Yellow circle
    PURPLE = '#9B59B6'   # Purple mixing bowl
    DARK = '#2C3E50'     # Dark outline
    
    # Helper function to draw a circle
    def draw_circle(x, y, radius, fill_color, outline_color=None, outline_width=0):
        x_scaled = x * scale
        y_scaled = y * scale
        r_scaled = radius * scale
        bbox = [
            x_scaled - r_scaled,
            y_scaled - r_scaled,
            x_scaled + r_scaled,
            y_scaled + r_scaled
        ]
        if outline_color:
            draw.ellipse(bbox, fill=fill_color, outline=outline_color, 
                        width=int(outline_width * scale))
        else:
            draw.ellipse(bbox, fill=fill_color)
    
    # Draw red circle (top-left)
    draw_circle(30, 30, 12, RED)
    
    # Draw blue circle (top-right)
    draw_circle(78, 30, 12, BLUE)
    
    # Draw yellow circle (bottom-left)
    draw_circle(30, 78, 12, YELLOW)
    
    # Draw purple mixing bowl (center) with outline
    draw_circle(54, 44, 18, PURPLE, DARK, 3)
    
    return image

def save_icon_with_info(image, filepath, description):
    """Save icon and print info."""
    image.save(filepath, 'PNG')
    file_size = os.path.getsize(filepath)
    print(f"✓ {description}")
    print(f"  Saved to: {filepath}")
    print(f"  Size: {image.width}x{image.height}, {file_size:,} bytes")

def main():
    print("=" * 70)
    print("ColorMixLab iOS App Icon Generator")
    print("=" * 70)
    print()
    
    # Determine the output directory
    script_dir = os.path.dirname(os.path.abspath(__file__))
    output_dir = os.path.join(script_dir, 'iosApp', 'ColorMixLab', 'ColorMixLab', 
                              'Assets.xcassets', 'AppIcon.appiconset')
    
    if not os.path.exists(output_dir):
        print(f"Warning: Output directory not found: {output_dir}")
        output_dir = script_dir
        print(f"Saving to script directory instead: {output_dir}")
    
    print(f"Output directory: {output_dir}")
    print()
    
    # Generate the main 1024x1024 icon
    print("Generating app icon...")
    icon_1024 = generate_icon(1024)
    output_path = os.path.join(output_dir, 'app-icon-1024.png')
    save_icon_with_info(icon_1024, output_path, "App Icon (1024x1024)")
    
    print()
    print("=" * 70)
    print("SUCCESS! Icon generated.")
    print("=" * 70)
    print()
    print("Next steps:")
    print("1. Open your Xcode project")
    print("2. Navigate to Assets.xcassets -> AppIcon")
    print("3. Drag and drop 'app-icon-1024.png' into the 1024x1024 slot")
    print("4. Xcode will automatically generate all required sizes")
    print()
    print("Note: You can use the same image for both light and dark modes,")
    print("      or generate separate versions if needed.")
    print()

if __name__ == '__main__':
    main()

