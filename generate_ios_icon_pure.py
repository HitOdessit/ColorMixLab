#!/usr/bin/env python3
"""
Generate iOS app icon using pure Python (no external dependencies).
Creates a PPM file that can be converted to PNG using sips.
"""

import struct
import math
import subprocess
import os

def create_circle_mask(width, height, cx, cy, radius):
    """Create a circular mask."""
    mask = []
    for y in range(height):
        row = []
        for x in range(width):
            dx = x - cx
            dy = y - cy
            dist = math.sqrt(dx*dx + dy*dy)
            row.append(dist <= radius)
        mask.append(row)
    return mask

def hex_to_rgb(hex_color):
    """Convert hex color to RGB tuple."""
    hex_color = hex_color.lstrip('#')
    return tuple(int(hex_color[i:i+2], 16) for i in (0, 2, 4))

def generate_icon_ppm(size=1024):
    """Generate icon as PPM (Portable Pixmap) format."""
    
    # Create white background
    pixels = [[(255, 255, 255) for _ in range(size)] for _ in range(size)]
    
    scale = size / 108.0
    
    # Color definitions
    RED = hex_to_rgb('#E74C3C')
    BLUE = hex_to_rgb('#3498DB')
    YELLOW = hex_to_rgb('#F1C40F')
    PURPLE = hex_to_rgb('#9B59B6')
    DARK = hex_to_rgb('#2C3E50')
    
    def draw_filled_circle(cx, cy, radius, color):
        """Draw a filled circle."""
        cx_px = int(cx * scale)
        cy_px = int(cy * scale)
        r_px = int(radius * scale)
        
        for y in range(max(0, cy_px - r_px), min(size, cy_px + r_px + 1)):
            for x in range(max(0, cx_px - r_px), min(size, cx_px + r_px + 1)):
                dx = x - cx_px
                dy = y - cy_px
                if dx*dx + dy*dy <= r_px*r_px:
                    pixels[y][x] = color
    
    def draw_circle_outline(cx, cy, radius, color, width):
        """Draw a circle outline."""
        cx_px = int(cx * scale)
        cy_px = int(cy * scale)
        r_px = int(radius * scale)
        w_px = int(width * scale)
        
        outer_r = r_px
        inner_r = r_px - w_px
        
        for y in range(max(0, cy_px - outer_r), min(size, cy_px + outer_r + 1)):
            for x in range(max(0, cx_px - outer_r), min(size, cx_px + outer_r + 1)):
                dx = x - cx_px
                dy = y - cy_px
                dist_sq = dx*dx + dy*dy
                if inner_r*inner_r <= dist_sq <= outer_r*outer_r:
                    pixels[y][x] = color
    
    # Draw circles in order (back to front)
    print("Drawing red circle (top-left)...")
    draw_filled_circle(30, 30, 12, RED)
    
    print("Drawing blue circle (top-right)...")
    draw_filled_circle(78, 30, 12, BLUE)
    
    print("Drawing yellow circle (bottom-left)...")
    draw_filled_circle(30, 78, 12, YELLOW)
    
    print("Drawing purple mixing bowl (center)...")
    draw_filled_circle(54, 44, 18, PURPLE)
    
    print("Drawing bowl outline...")
    draw_circle_outline(54, 44, 18, DARK, 3)
    
    return pixels

def save_ppm(pixels, filename):
    """Save pixels as PPM file."""
    height = len(pixels)
    width = len(pixels[0]) if height > 0 else 0
    
    with open(filename, 'wb') as f:
        # PPM header
        header = f'P6\n{width} {height}\n255\n'
        f.write(header.encode('ascii'))
        
        # Write pixel data
        for row in pixels:
            for r, g, b in row:
                f.write(bytes([r, g, b]))

def convert_ppm_to_png(ppm_file, png_file):
    """Convert PPM to PNG using sips."""
    try:
        subprocess.run(['sips', '-s', 'format', 'png', ppm_file, '--out', png_file],
                      check=True, capture_output=True, text=True)
        return True
    except subprocess.CalledProcessError as e:
        print(f"Error converting to PNG: {e}")
        return False
    except FileNotFoundError:
        print("Error: 'sips' command not found")
        return False

def main():
    print("=" * 70)
    print("ColorMixLab iOS App Icon Generator")
    print("=" * 70)
    print()
    
    script_dir = os.path.dirname(os.path.abspath(__file__))
    output_dir = os.path.join(script_dir, 'iosApp', 'ColorMixLab', 'ColorMixLab',
                              'Assets.xcassets', 'AppIcon.appiconset')
    
    if not os.path.exists(output_dir):
        print(f"Warning: Output directory not found: {output_dir}")
        output_dir = script_dir
        print(f"Saving to script directory instead: {output_dir}")
    
    print(f"Output directory: {output_dir}")
    print()
    
    # Generate icon
    print("Generating 1024x1024 icon...")
    pixels = generate_icon_ppm(1024)
    
    # Save as PPM
    ppm_file = os.path.join(output_dir, 'app-icon-1024.ppm')
    print(f"Saving PPM file: {ppm_file}")
    save_ppm(pixels, ppm_file)
    print(f"✓ PPM file saved ({os.path.getsize(ppm_file):,} bytes)")
    print()
    
    # Convert to PNG
    png_file = os.path.join(output_dir, 'app-icon-1024.png')
    print(f"Converting to PNG: {png_file}")
    if convert_ppm_to_png(ppm_file, png_file):
        print(f"✓ PNG file created ({os.path.getsize(png_file):,} bytes)")
        
        # Clean up PPM file
        try:
            os.remove(ppm_file)
            print("✓ Temporary PPM file removed")
        except:
            pass
        
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
    else:
        print()
        print("Could not convert to PNG automatically.")
        print(f"PPM file saved at: {ppm_file}")
        print("You can manually convert it using an image editor.")
        print()

if __name__ == '__main__':
    main()

