package com.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Helper class to generate SVG-based icons for the application
 * This avoids the need for external icon files
 */
public class IconGenerator {
    
    /**
     * Get an icon based on its name
     * @param iconName The name of the icon to create
     * @param size The size of the icon (width and height)
     * @param color The color of the icon
     * @return A Node containing the icon
     */
    public static Node getIcon(String iconName, double size, Color color) {
        SVGPath path = new SVGPath();
        path.setFill(color);
        
        switch(iconName) {
            case "home":
                path.setContent("M10,20V14H14V20H19V12H22L12,3L2,12H5V20H10Z");
                break;
            case "moon":
                path.setContent("M17.75,4.09L15.22,6.03L16.13,9.09L13.5,7.28L10.87,9.09L11.78,6.03L9.25,4.09L12.44,4L13.5,1L14.56,4L17.75,4.09M21.25,11L19.61,12.25L20.2,14.23L18.5,13.06L16.8,14.23L17.39,12.25L15.75,11L17.81,10.95L18.5,9L19.19,10.95L21.25,11M18.97,15.95C19.8,15.87 20.69,17.05 20.16,17.8C19.84,18.25 19.5,18.67 19.08,19.07C15.17,23 8.84,23 4.94,19.07C1.03,15.17 1.03,8.83 4.94,4.93C5.34,4.53 5.76,4.17 6.21,3.85C6.96,3.32 8.14,4.21 8.06,5.04C7.79,7.9 8.75,10.87 10.95,13.06C13.14,15.26 16.1,16.22 18.97,15.95M17.33,17.97C14.5,17.81 11.7,16.64 9.53,14.5C7.36,12.31 6.2,9.5 6.04,6.68C3.23,9.82 3.34,14.64 6.35,17.66C9.37,20.67 14.19,20.78 17.33,17.97Z");
                break;
            case "sun":
                path.setContent("M3.55,18.54L4.96,19.95L6.76,18.16L5.34,16.74M11,22.45C11.32,22.45 13,22.45 13,22.45V19.5H11M12,5.5A6,6 0 0,0 6,11.5A6,6 0 0,0 12,17.5A6,6 0 0,0 18,11.5C18,8.18 15.31,5.5 12,5.5M20,12.5H23V10.5H20M17.24,18.16L19.04,19.95L20.45,18.54L18.66,16.74M20.45,4.46L19.04,3.05L17.24,4.84L18.66,6.26M13,0.55H11V3.5H13M4,10.5H1V12.5H4M6.76,4.84L4.96,3.05L3.55,4.46L5.34,6.26L6.76,4.84Z");
                break;
            case "search":
                path.setContent("M9.5,3A6.5,6.5 0 0,1 16,9.5C16,11.11 15.41,12.59 14.44,13.73L14.71,14H15.5L20.5,19L19,20.5L14,15.5V14.71L13.73,14.44C12.59,15.41 11.11,16 9.5,16A6.5,6.5 0 0,1 3,9.5A6.5,6.5 0 0,1 9.5,3M9.5,5C7.01,5 5,7.01 5,9.5C5,11.99 7.01,14 9.5,14C11.99,14 14,11.99 14,9.5C14,7.01 11.99,5 9.5,5Z");
                break;
            case "category":
                path.setContent("M10,4H4C2.89,4 2,4.89 2,6V18A2,2 0 0,0 4,20H20A2,2 0 0,0 22,18V8C22,6.89 21.1,6 20,6H12L10,4Z");
                break;
            case "add":
                path.setContent("M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z");
                break;
            case "edit":
                path.setContent("M20.71,7.04C21.1,6.65 21.1,6 20.71,5.63L18.37,3.29C18,2.9 17.35,2.9 16.96,3.29L15.12,5.12L18.87,8.87M3,17.25V21H6.75L17.81,9.93L14.06,6.18L3,17.25Z");
                break;
            case "delete":
                path.setContent("M19,4H15.5L14.5,3H9.5L8.5,4H5V6H19M6,19A2,2 0 0,0 8,21H16A2,2 0 0,0 18,19V7H6V19Z");
                break;
            case "code":
                path.setContent("M14.6,16.6L19.2,12L14.6,7.4L16,6L22,12L16,18L14.6,16.6M9.4,16.6L4.8,12L9.4,7.4L8,6L2,12L8,18L9.4,16.6Z");
                break;
            case "content_copy":
                path.setContent("M19,21H8V7H19M19,5H8A2,2 0 0,0 6,7V21A2,2 0 0,0 8,23H19A2,2 0 0,0 21,21V7A2,2 0 0,0 19,5M16,1H4A2,2 0 0,0 2,3V17H4V3H16V1Z");
                break;
            case "description":
                path.setContent("M14,17H7V15H14M17,13H7V11H17M17,9H7V7H17M19,3H5C3.89,3 3,3.89 3,5V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V5C21,3.89 20.1,3 19,3Z");
                break;
            case "list":
                path.setContent("M3,4H21V8H3V4M3,10H21V14H3V10M3,16H21V20H3V16Z");
                break;
            case "touch_app":
                path.setContent("M9,11.24V7.5C9,6.12 10.12,5 11.5,5S14,6.12 14,7.5V11.24C16.36,11.88 18,14.09 18,16.5V22H4V16.5C4,14.09 5.64,11.88 8,11.24M13,7.5C13,6.67 12.33,6 11.5,6S10,6.67 10,7.5V10.95C10.65,10.8 11.31,10.76 12,10.82V7.5H13M11,19A2,2 0 0,0 13,17A2,2 0 0,0 11,15A2,2 0 0,0 9,17A2,2 0 0,0 11,19Z");
                break;
            case "explore":
                path.setContent("M12,10.9C11.39,10.9 10.9,11.39 10.9,12C10.9,12.61 11.39,13.1 12,13.1C12.61,13.1 13.1,12.61 13.1,12C13.1,11.39 12.61,10.9 12,10.9M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M16.2,16.2L8,14.5L9.7,6.3L17.9,8L16.2,16.2Z");
                break;
            case "rocket":
                path.setContent("M2.81,14.12L5.64,11.29L8.17,10.79C11.39,6.41 17.55,4.22 19.78,4.22C19.78,6.45 17.59,12.61 13.21,15.83L12.71,18.36L9.88,21.19L9.17,17.66C7.76,17.66 7.76,17.66 7.05,16.95C6.34,16.24 6.34,16.24 6.34,14.83L2.81,14.12M5.64,16.95L7.05,18.36L4.39,21.03H2.97V19.61L5.64,16.95M4.22,15.54L5.46,15.71L3,18.16V16.74L4.22,15.54M8.29,18.54L8.46,19.78L7.26,21H5.84L8.29,18.54M13,9.5A1.5,1.5 0 0,0 11.5,11A1.5,1.5 0 0,0 13,12.5A1.5,1.5 0 0,0 14.5,11A1.5,1.5 0 0,0 13,9.5Z");
                break;
            case "help":
                path.setContent("M10,19H13V22H10V19M12,2C17.35,2.22 19.68,7.62 16.5,11.67C15.67,12.67 14.33,13.33 13.67,14.17C13,15 13,16 13,17H10C10,15.33 10,13.92 10.67,12.92C11.33,11.92 12.67,11.33 13.5,10.67C15.92,8.43 15.32,5.26 12,5A3,3 0 0,0 9,8H6A6,6 0 0,1 12,2Z");
                break;
            case "star":
                path.setContent("M12,17.27L18.18,21L16.54,13.97L22,9.24L14.81,8.62L12,2L9.19,8.62L2,9.24L7.45,13.97L5.82,21L12,17.27Z");
                break;
            case "create":
                path.setContent("M20.71,7.04C21.1,6.65 21.1,6 20.71,5.63L18.37,3.29C18,2.9 17.35,2.9 16.96,3.29L15.12,5.12L18.87,8.87M3,17.25V21H6.75L17.81,9.93L14.06,6.18L3,17.25Z");
                break;
            case "lightbulb":
                path.setContent("M12,2A7,7 0 0,0 5,9C5,11.38 6.19,13.47 8,14.74V17A1,1 0 0,0 9,18H15A1,1 0 0,0 16,17V14.74C17.81,13.47 19,11.38 19,9A7,7 0 0,0 12,2M9,21A1,1 0 0,0 10,22H14A1,1 0 0,0 15,21V20H9V21Z");
                break;
            case "navigation":
                path.setContent("M12,2L4.5,20.29L5.21,21L12,18L18.79,21L19.5,20.29L12,2Z");
                break;
            case "library_books":
                path.setContent("M19,7H9V5H19M15,15H9V13H15M19,11H9V9H19M20,3H8A2,2 0 0,0 6,5V17A2,2 0 0,0 8,19H20A2,2 0 0,0 22,17V5A2,2 0 0,0 20,3M4,3H2V19A2,2 0 0,0 4,21H20V19H4V3Z");
                break;
            case "bookmark_add":
                path.setContent("M17,3A2,2 0 0,1 19,5V21L12,18L5,21V5C5,3.89 5.9,3 7,3H17M11,7V9H9V11H11V13H13V11H15V9H13V7H11Z");
                break;
            case "text_format":
                path.setContent("M18.5,4L19.66,8.35L18.7,8.61C18.25,7.74 17.79,6.87 17.26,6.43C16.73,6 16.11,6 15.5,6H13V16.5C13,17 13,17.5 13.33,17.75C13.67,18 14.33,18 15,18V19H9V18C9.67,18 10.33,18 10.67,17.75C11,17.5 11,17 11,16.5V6H8.5C7.89,6 7.27,6 6.74,6.43C6.21,6.87 5.75,7.74 5.3,8.61L4.34,8.35L5.5,4H18.5Z");
                break;
            case "text_fields":
                path.setContent("M2.5,4V7H7.5V19H10.5V7H15.5V4H2.5M21.5,9H14.5V12H17.5V19H20.5V12H21.5V9Z");
                break;
            case "numbers":
                path.setContent("M4,17V9H2V7H6V17H4M22,15C22,16.11 21.1,17 20,17H16V15H20V13H18V11H20V9H16V7H20A2,2 0 0,1 22,9V10.5A1.5,1.5 0 0,1 20.5,12A1.5,1.5 0 0,1 22,13.5V15M14,15V17H8V13C8,11.89 8.9,11 10,11H12V9H8V7H12A2,2 0 0,1 14,9V11C14,12.11 13.1,13 12,13H10V15H14Z");
                break;
            case "calendar":
                path.setContent("M19,19H5V8H19M16,1V3H8V1H6V3H5C3.89,3 3,3.89 3,5V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V5C21,3.89 20.1,3 19,3H18V1M17,12H12V17H17V12Z");
                break;
            case "folder":
                path.setContent("M10,4H4C2.89,4 2,4.89 2,6V18A2,2 0 0,0 4,20H20A2,2 0 0,0 22,18V8C22,6.89 21.1,6 20,6H12L10,4Z");
                break;
            case "calculate":
                path.setContent("M7,2H17A2,2 0 0,1 19,4V20A2,2 0 0,1 17,22H7A2,2 0 0,1 5,20V4A2,2 0 0,1 7,2M7,4V20H17V4H7M16,12V10H8V12H16M16,16V14H8V16H16M11,8H13V6H15V8H13V10H11V8Z");
                break;
            case "stream":
                path.setContent("M7.5,2C5.71,3.15 4.5,5.18 4.5,7.5C4.5,9.82 5.71,11.85 7.5,13C9.29,11.85 10.5,9.82 10.5,7.5C10.5,5.18 9.29,3.15 7.5,2M7.5,4A3.5,3.5 0 0,1 11,7.5C11,9.5 9.72,11.23 7.92,11.96L7.5,12L7.07,11.96C5.26,11.23 4,9.5 4,7.5A3.5,3.5 0 0,1 7.5,4M16.5,2C14.71,3.15 13.5,5.18 13.5,7.5C13.5,9.82 14.71,11.85 16.5,13C18.29,11.85 19.5,9.82 19.5,7.5C19.5,5.18 18.29,3.15 16.5,2M16.5,4A3.5,3.5 0 0,1 20,7.5C20,9.5 18.72,11.23 16.92,11.96L16.5,12L16.07,11.96C14.26,11.23 13,9.5 13,7.5A3.5,3.5 0 0,1 16.5,4M7.5,14C4.46,14 2,15.79 2,18V20H13V18C13,15.79 10.54,14 7.5,14M7.5,16C9.33,16 11,16.67 11,18H4C4,16.67 5.67,16 7.5,16M16.5,14C13.46,14 11,15.79 11,18V20H22V18C22,15.79 19.54,14 16.5,14M16.5,16C18.33,16 20,16.67 20,18H13C13,16.67 14.67,16 16.5,16Z");
                break;
            case "format":
                path.setContent("M18.5,4L19.66,8.35L18.7,8.61C18.25,7.74 17.79,6.87 17.26,6.43C16.73,6 16.11,6 15.5,6H13V16.5C13,17 13,17.5 13.33,17.75C13.67,18 14.33,18 15,18V19H9V18C9.67,18 10.33,18 10.67,17.75C11,17.5 11,17 11,16.5V6H8.5C7.89,6 7.27,6 6.74,6.43C6.21,6.87 5.75,7.74 5.3,8.61L4.34,8.35L5.5,4H18.5Z");
                break;
            case "abc":
                path.setContent("M21,21H3V7H21M21,5H3A2,2 0 0,0 1,7V21A2,2 0 0,0 3,23H21A2,2 0 0,0 23,21V7A2,2 0 0,0 21,5M4,15.5A1.5,1.5 0 0,1 2.5,14A1.5,1.5 0 0,1 4,12.5H6V14H4V15.5M9,19A3,3 0 0,1 6,16A3,3 0 0,1 9,13H15V15H9V17H12V19M20,15.5V14H18V12.5H20A1.5,1.5 0 0,1 21.5,14A1.5,1.5 0 0,1 20,15.5Z");
                break;
            case "close":
                path.setContent("M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z");
                break;
            case "search_off":
                path.setContent("M15.5,14H14.71L14.43,13.73C15.41,12.59 16,11.11 16,9.5A6.5,6.5 0 0,0 9.5,3A6.5,6.5 0 0,0 3,9.5A6.5,6.5 0 0,0 9.5,16C11.11,16 12.59,15.41 13.73,14.43L14,14.71V15.5L19,20.5L20.5,19L15.5,14M9.5,14C7,14 5,12 5,9.5C5,7 7,5 9.5,5C12,5 14,7 14,9.5C14,12 12,14 9.5,14M7,2L9,4H11L13,2L11,0H9L7,2M12.4,4H14.4L16.4,2L14.4,0H12.4L10.4,2L12.4,4Z");
                break;
            default:
                // Create a text-based fallback icon with the first letter
                StackPane fallback = new StackPane();
                fallback.setMinSize(size, size);
                fallback.setMaxSize(size, size);
                fallback.setStyle("-fx-background-color: " + toHexString(color) + "; -fx-background-radius: 50%;");
                
                Text letter = new Text(iconName.substring(0, 1).toUpperCase());
                letter.setFont(Font.font("System", FontWeight.BOLD, size * 0.6));
                letter.setFill(Color.WHITE);
                
                fallback.getChildren().add(letter);
                return fallback;
        }
        
        // Scale the path to the requested size
        path.setScaleX(size / 24.0);
        path.setScaleY(size / 24.0);
        
        // Center the path in a StackPane
        StackPane pane = new StackPane(path);
        pane.setMinSize(size, size);
        pane.setMaxSize(size, size);
        
        return pane;
    }
    
    // Helper method to convert JavaFX Color to hex string
    private static String toHexString(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b);
    }
}
