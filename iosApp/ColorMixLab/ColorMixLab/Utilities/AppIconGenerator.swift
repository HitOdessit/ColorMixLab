import SwiftUI
import UIKit

/// Utility to generate the ColorMixLab app icon programmatically
/// This matches the Android app icon design with colored circles and a mixing bowl
class AppIconGenerator {
    static func generateIcon(size: CGFloat = 1024) -> UIImage {
        let renderer = UIGraphicsImageRenderer(size: CGSize(width: size, height: size))
        
        return renderer.image { context in
            let ctx = context.cgContext
            
            // Background - White
            ctx.setFillColor(UIColor.white.cgColor)
            ctx.fill(CGRect(x: 0, y: 0, width: size, height: size))
            
            let scale = size / 108.0
            
            // Red circle (top-left)
            ctx.setFillColor(UIColor(red: 0.906, green: 0.298, blue: 0.235, alpha: 1.0).cgColor)
            ctx.fillEllipse(in: CGRect(x: (30-12)*scale, y: (30-12)*scale, 
                                      width: 24*scale, height: 24*scale))
            
            // Blue circle (top-right)
            ctx.setFillColor(UIColor(red: 0.204, green: 0.596, blue: 0.859, alpha: 1.0).cgColor)
            ctx.fillEllipse(in: CGRect(x: (78-12)*scale, y: (30-12)*scale, 
                                      width: 24*scale, height: 24*scale))
            
            // Yellow circle (bottom-left)
            ctx.setFillColor(UIColor(red: 0.945, green: 0.769, blue: 0.059, alpha: 1.0).cgColor)
            ctx.fillEllipse(in: CGRect(x: (30-12)*scale, y: (78-12)*scale, 
                                      width: 24*scale, height: 24*scale))
            
            // Purple mixing bowl (center)
            ctx.setFillColor(UIColor(red: 0.608, green: 0.349, blue: 0.714, alpha: 1.0).cgColor)
            ctx.fillEllipse(in: CGRect(x: (54-18)*scale, y: (44-18)*scale, 
                                      width: 36*scale, height: 36*scale))
            
            // Bowl outline
            ctx.setStrokeColor(UIColor(red: 0.173, green: 0.243, blue: 0.314, alpha: 1.0).cgColor)
            ctx.setLineWidth(3*scale)
            ctx.strokeEllipse(in: CGRect(x: (54-18)*scale, y: (44-18)*scale, 
                                        width: 36*scale, height: 36*scale))
        }
    }
    
    /// Save the icon to the AppIcon.appiconset folder
    static func saveIconToAssets() {
        let icon = generateIcon(size: 1024)
        
        // For development/testing - print the path where icons should be saved
        #if DEBUG
        if let data = icon.pngData() {
            print("Icon generated successfully. Size: \(data.count) bytes")
            print("Icon dimensions: \(icon.size.width) x \(icon.size.height)")
        }
        #endif
    }
}

/// SwiftUI Preview helper for the app icon
struct AppIconView: View {
    let size: CGFloat
    
    var body: some View {
        Canvas { context, size in
            let scale = size.width / 108.0
            
            // Background - White
            context.fill(
                Path(CGRect(x: 0, y: 0, width: size.width, height: size.height)),
                with: .color(.white)
            )
            
            // Red circle (top-left)
            context.fill(
                Path(ellipseIn: CGRect(x: (30-12)*scale, y: (30-12)*scale, 
                                      width: 24*scale, height: 24*scale)),
                with: .color(Color(red: 0.906, green: 0.298, blue: 0.235))
            )
            
            // Blue circle (top-right)
            context.fill(
                Path(ellipseIn: CGRect(x: (78-12)*scale, y: (30-12)*scale, 
                                      width: 24*scale, height: 24*scale)),
                with: .color(Color(red: 0.204, green: 0.596, blue: 0.859))
            )
            
            // Yellow circle (bottom-left)
            context.fill(
                Path(ellipseIn: CGRect(x: (30-12)*scale, y: (78-12)*scale, 
                                      width: 24*scale, height: 24*scale)),
                with: .color(Color(red: 0.945, green: 0.769, blue: 0.059))
            )
            
            // Purple mixing bowl (center)
            context.fill(
                Path(ellipseIn: CGRect(x: (54-18)*scale, y: (44-18)*scale, 
                                      width: 36*scale, height: 36*scale)),
                with: .color(Color(red: 0.608, green: 0.349, blue: 0.714))
            )
            
            // Bowl outline
            context.stroke(
                Path(ellipseIn: CGRect(x: (54-18)*scale, y: (44-18)*scale, 
                                      width: 36*scale, height: 36*scale)),
                with: .color(Color(red: 0.173, green: 0.243, blue: 0.314)),
                lineWidth: 3*scale
            )
        }
        .frame(width: size, height: size)
    }
}

#Preview("App Icon - Small") {
    AppIconView(size: 120)
        .previewLayout(.sizeThatFits)
}

#Preview("App Icon - Large") {
    AppIconView(size: 256)
        .previewLayout(.sizeThatFits)
}

