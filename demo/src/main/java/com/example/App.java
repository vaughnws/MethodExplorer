package com.example;
import java.util.prefs.*;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.fxml.FXMLLoader;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.paint.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.scene.Node;

public class App extends Application {
    private static Scene scene;
    private VBox contentPane;
    private List<MethodInfo> userMethods = new ArrayList<>();
    private TitledPane userMethodsCategory;
    private static final String USER_METHODS_FILE = "user_methods.dat";
    private BooleanProperty darkMode = new SimpleBooleanProperty(false);
    private static final String DARK_MODE_PREF = "dark_mode";
    private static final String LIGHT_THEME_CSS = "/styles-light.css";
    private static final String DARK_THEME_CSS = "/styles-dark.css";
    private Button homeButton;
    private ToggleButton darkModeToggle;
    private TextField searchField;
    private List<MethodInfo> searchResults = new ArrayList<>();
    private VBox searchResultsContainer;
    private SplitPane splitPane;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load user methods from file
            loadUserMethods();
            
            // Create CSS file if needed
            createScrollbarStylesheet();

            createEnhancedUI(primaryStage);

            updateAllIcons();
            
            // Set up window close handler to save user methods
            primaryStage.setOnCloseRequest(e -> {
                saveUserMethods();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createScrollbarStylesheet() {
        String cssContent = 
            ".scroll-pane > .viewport {\n" +
            "    -fx-background-color: transparent;\n" +
            "}\n" +
            "\n" +
            ".scroll-pane {\n" +
            "    -fx-background-color: transparent;\n" +
            "}\n" +
            "\n" +
            ".scroll-bar:vertical {\n" +
            "    -fx-pref-width: 12;\n" +
            "    -fx-background-color: transparent;\n" +
            "}\n" +
            "\n" +
            ".scroll-bar:horizontal {\n" +
            "    -fx-pref-height: 12;\n" +
            "    -fx-background-color: transparent;\n" +
            "}\n" +
            "\n" +
            ".scroll-bar:vertical .thumb,\n" +
            ".scroll-bar:horizontal .thumb {\n" +
            "    -fx-background-color: #888;\n" +
            "    -fx-background-radius: 5;\n" +
            "}\n" +
            "\n" +
            ".scroll-bar .increment-button,\n" +
            ".scroll-bar .decrement-button {\n" +
            "    -fx-padding: 4;\n" +
            "}\n" +
            "\n" +
            ".edge-to-edge {\n" +
            "    -fx-background-color: transparent;\n" +
            "    -fx-padding: 0;\n" +
            "    -fx-background-insets: 0;\n" +
            "}";
        
        try {
            // Try to create a styles.css file in the resources directory
            File dir = new File("src/main/resources");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            File cssFile = new File(dir, "styles.css");
            if (!cssFile.exists()) {
                try (FileWriter writer = new FileWriter(cssFile)) {
                    writer.write(cssContent);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not create CSS file: " + e.getMessage());
        }
    }

        // Update your createEnhancedUI method to include the top bar
        private void createEnhancedUI(Stage primaryStage) {
            // Create the top navigation bar
            HBox topBar = createTopBar();

            // Main content area
            contentPane = new VBox();
            contentPane.setPadding(new Insets(0)); // Remove padding to maximize space
            contentPane.setStyle("-fx-background-color: #f8f8f8;");
            VBox.setVgrow(contentPane, Priority.ALWAYS);

            // Main layout with split pane
            splitPane = new SplitPane();
            splitPane.setOrientation(Orientation.HORIZONTAL);
            splitPane.setDividerPositions(0.25); // Set initial divider position

            // Sidebar for method navigation
            VBox sidebar = createSidebar();

            // Create a ScrollPane for the content area with improved scrolling
            ScrollPane contentScrollPane = new ScrollPane(contentPane);
            contentScrollPane.setFitToWidth(true);
            contentScrollPane.setFitToHeight(true);
            contentScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            contentScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            contentScrollPane.setPannable(true);
            contentScrollPane.getStyleClass().add("edge-to-edge");
            
            // Add components to split pane
            splitPane.getItems().addAll(sidebar, contentScrollPane);
            
            // Welcome content
            showWelcomeContent();
            
            // Create main layout with top bar and split pane
            VBox mainLayout = new VBox();
            mainLayout.getChildren().addAll(topBar, splitPane);
            VBox.setVgrow(splitPane, Priority.ALWAYS);
            
            // Create scene
            scene = new Scene(mainLayout, 1200, 800);
            
            // Initialize with the correct theme
            updateTheme(darkMode.get());
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("Method Explorer");
            primaryStage.show();
        }

    
        private VBox createSidebar() {
            VBox sidebarContent = new VBox(5);
            sidebarContent.setPadding(new Insets(15));
            sidebarContent.getStyleClass().add("sidebar");
            
            // Create title for sidebar with icon
            HBox titleContainer = new HBox(10);
            titleContainer.setAlignment(Pos.CENTER_LEFT);
            
            ImageView categoryIcon = createIcon("category");
            
            Label title = new Label("Method Categories");
            title.setFont(Font.font("System", FontWeight.BOLD, 16));
            title.getStyleClass().add("sidebar-title");
            
            titleContainer.getChildren().addAll(categoryIcon, title);
            titleContainer.setPadding(new Insets(0, 0, 15, 0));
            
            // Search box
            HBox searchBox = createSearchBox();
            
            // Create accordion for categorized methods with modern styling
            Accordion accordion = new Accordion();
            accordion.getStyleClass().add("method-accordion");
            
            // User methods category (place at the top for visibility)
            userMethodsCategory = createUserMethodsPane();
            userMethodsCategory.getStyleClass().add("user-methods-category");
            
            // Standard categories with icons
            TitledPane stringCategory = createCategoryPaneWithIcon("String Methods", getStringMethods(), "abc");
            TitledPane advStringCategory = createCategoryPaneWithIcon("Advanced String Methods", getAdvStringMethods(), "text_format");
            TitledPane collectionCategory = createCategoryPaneWithIcon("Collection Methods", getCollectionMethods(), "list");
            TitledPane fileIOCategory = createCategoryPaneWithIcon("File I/O Methods", getFileIOMethods(), "folder");
            TitledPane mathCategory = createCategoryPaneWithIcon("Math Methods", getMathMethods(), "calculate");
            TitledPane streamCategory = createCategoryPaneWithIcon("Stream API", getStreamMethods(), "stream");
            TitledPane decIntCategory = createCategoryPaneWithIcon("Big Decimal / Big Int Methods", getDecIntMethods(), "numbers");
            TitledPane formatterCategory = createCategoryPaneWithIcon("Formatter Methods", getFormatterMethods(), "format");
            TitledPane dateTimeCategory = createCategoryPaneWithIcon("Date and Time Methods", getDateMethods(), "calendar");
            TitledPane characterCategory = createCategoryPaneWithIcon("Character Methods", getCharacterMethods(), "text_fields");
            
            // Add all categories to accordion
            accordion.getPanes().addAll(
                userMethodsCategory,
                stringCategory, 
                advStringCategory,
                collectionCategory, 
                mathCategory, 
                fileIOCategory, 
                streamCategory,
                decIntCategory,
                formatterCategory,
                dateTimeCategory,
                characterCategory
            );
            
            // Add components to sidebar
            sidebarContent.getChildren().addAll(titleContainer, searchBox, accordion);
        
            // Create a ScrollPane with fixed size to prevent excessive scrolling
            ScrollPane scrollableSidebar = new ScrollPane(sidebarContent);
            scrollableSidebar.setFitToWidth(true);
            scrollableSidebar.setPrefWidth(280); // Fixed width
            scrollableSidebar.setMinWidth(200); // Minimum width
            scrollableSidebar.setMaxWidth(350); // Maximum width
            scrollableSidebar.setPannable(true);
            scrollableSidebar.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Prevent horizontal scrolling
            scrollableSidebar.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollableSidebar.getStyleClass().add("sidebar-scroll");
            
            // Create the main sidebar container with fixed width
            VBox sidebar = new VBox();
            sidebar.setPrefWidth(280);
            sidebar.setMinWidth(200);
            sidebar.setMaxWidth(350);
            sidebar.getChildren().add(scrollableSidebar);
            VBox.setVgrow(scrollableSidebar, Priority.ALWAYS);
            
            return sidebar;
        }

        private TitledPane createCategoryPaneWithIcon(String categoryName, List<MethodInfo> methods, String iconName) {
            VBox content = new VBox(5);
            content.setPadding(new Insets(5));
            content.getStyleClass().add("category-content");
            
            // Create a list view for the methods in this category
            for (MethodInfo method : methods) {
                HBox methodRow = new HBox(10);
                methodRow.setAlignment(Pos.CENTER_LEFT);
                
                Hyperlink link = new Hyperlink(method.getName().split("\\(")[0]);
                link.setOnAction(e -> displayMethodDetails(method));
                link.getStyleClass().add("method-link");
                HBox.setHgrow(link, Priority.ALWAYS);
                
                methodRow.getChildren().add(link);
                content.getChildren().add(methodRow);
            }
            
            // Make the content scrollable if it gets too large
            ScrollPane scrollContent = new ScrollPane(content);
            scrollContent.setFitToWidth(true);
            scrollContent.setPrefHeight(Math.min(methods.size() * 25, 300)); // Limit max height
            scrollContent.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollContent.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollContent.getStyleClass().add("category-scroll");
            
            // Create the title with an icon
            HBox titleWithIcon = new HBox(10);
            titleWithIcon.setAlignment(Pos.CENTER_LEFT);
            
            ImageView icon = createIcon(iconName);
            Label titleLabel = new Label(categoryName);
            
            titleWithIcon.getChildren().addAll(icon, titleLabel);
            
            TitledPane category = new TitledPane();
            category.setGraphic(titleWithIcon);
            category.setContent(scrollContent);
            category.getStyleClass().add("category-pane");
            
            return category;
        }

        private HBox createTopBar() {
            HBox topBar = new HBox(15);
            topBar.setPadding(new Insets(10, 15, 10, 15));
            topBar.setAlignment(Pos.CENTER_LEFT);
            topBar.getStyleClass().add("top-bar");
            
            // Home button
            homeButton = new Button();
            homeButton.setGraphic(createIcon("home"));
            homeButton.setTooltip(new Tooltip("Go to Home"));
            homeButton.getStyleClass().add("icon-button");
            homeButton.setOnAction(e -> showWelcomeContent());
            
            // App title
            Label title = new Label("Method Explorer");
            title.setFont(Font.font("System", FontWeight.BOLD, 18));
            title.getStyleClass().add("app-title");
            HBox.setHgrow(title, Priority.ALWAYS);
            
            // Dark mode toggle with different icons for light/dark mode
            darkModeToggle = new ToggleButton();
            updateDarkModeToggleIcon();
            darkModeToggle.setTooltip(new Tooltip("Toggle Dark Mode"));
            darkModeToggle.getStyleClass().add("icon-button");
            
            // Load dark mode preference
            Preferences prefs = Preferences.userNodeForPackage(App.class);
            boolean isDarkMode = prefs.getBoolean(DARK_MODE_PREF, false);
            darkMode.set(isDarkMode);
            darkModeToggle.setSelected(isDarkMode);
            
            darkModeToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
                darkMode.set(newVal);
                updateTheme(newVal);
                updateDarkModeToggleIcon();
                // Save preference
                prefs.putBoolean(DARK_MODE_PREF, newVal);
            });
            
            // Add components to the top bar
            topBar.getChildren().addAll(homeButton, title, darkModeToggle);
            
            return topBar;
        }
        
        // Helper method to update the dark mode toggle icon based on the current mode
        private void updateDarkModeToggleIcon() {
            if (darkModeToggle != null) {
                if (darkMode.get()) {
                    darkModeToggle.setGraphic(createIcon("sun"));
                    darkModeToggle.setTooltip(new Tooltip("Switch to Light Mode"));
                } else {
                    darkModeToggle.setGraphic(createIcon("moon"));
                    darkModeToggle.setTooltip(new Tooltip("Switch to Dark Mode"));
                }
            }
        }


        // Method to get appropriate color for icons based on icon type and theme
private Color getIconColor(String iconName) {
    if (darkMode.get()) {
        // In dark mode, most icons are white
        return Color.WHITE;
    } else {
        // In light mode, use colors based on icon type/category
        switch (iconName) {
            // Navigation icons - blue
            case "home":
            case "navigation":
            case "explore":
            case "search":
                return Color.web("#2a5885"); // Blue
                
            // Action icons - dark blue
            case "edit":
            case "add":
            case "content_copy":
            case "create":
                return Color.web("#1a4875"); // Dark blue
                
            // Danger/warning icons - red
            case "delete":
                return Color.web("#c62828"); // Red
                
            // Creative/idea icons - purple
            case "lightbulb":
            case "star":
                return Color.web("#7b1fa2"); // Purple
                
            // Information icons - teal
            case "description":
            case "help":
            case "list":
            case "library_books":
                return Color.web("#00796b"); // Teal
                
            // Code-related icons - dark orange
            case "code":
            case "format":
            case "text_format":
            case "text_fields":
                return Color.web("#e65100"); // Dark orange
                
            // Data/math icons - dark green
            case "calculate":
            case "stream":
            case "numbers":
                return Color.web("#2e7d32"); // Dark green
                
            // Time/date icons - brown
            case "calendar":
                return Color.web("#795548"); // Brown
                
            // File/folder icons - grey
            case "folder":
            case "category":
                return Color.web("#546e7a"); // Blue-grey
                
            // Toggle icons - special cases
            case "moon":
                return Color.web("#5c6bc0"); // Indigo
            case "sun":
                return Color.web("#fb8c00"); // Orange

            // search icons - NICE! 
            case "close":
                return Color.web("#616161"); // Grey
                
            case "search_off":
                return Color.web("#616161"); // Grey
                
            // Default fallback
            default:
                return Color.web("#2a5885"); // Default blue
        }
    }
}

// Updated createIcon method to use the color selection helper
private ImageView createIcon(String iconName) {
    // Get the appropriate color based on icon type and theme
    Color iconColor = getIconColor(iconName);
    
    // Get the icon node from the generator
    Node iconNode = IconGenerator.getIcon(iconName, 20, iconColor);
    
    // Convert to ImageView for consistency with existing code
    if (iconNode instanceof ImageView) {
        return (ImageView) iconNode;
    } else {
        // Take a snapshot of the node to create an image
        javafx.scene.SnapshotParameters parameters = new javafx.scene.SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        javafx.scene.image.WritableImage image = iconNode.snapshot(parameters, null);
        return new ImageView(image);
    }
}

// Updated overloaded version with custom size
private ImageView createIcon(String iconName, double size) {
    // Get the appropriate color based on icon type and theme
    Color iconColor = getIconColor(iconName);
    
    Node iconNode = IconGenerator.getIcon(iconName, size, iconColor);
    
    if (iconNode instanceof ImageView) {
        return (ImageView) iconNode;
    } else {
        javafx.scene.SnapshotParameters parameters = new javafx.scene.SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        javafx.scene.image.WritableImage image = iconNode.snapshot(parameters, null);
        return new ImageView(image);
    }
}

// Overloaded version with custom color
private ImageView createIcon(String iconName, Color color) {
    Node iconNode = IconGenerator.getIcon(iconName, 20, color);
    
    if (iconNode instanceof ImageView) {
        return (ImageView) iconNode;
    } else {
        javafx.scene.SnapshotParameters parameters = new javafx.scene.SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        javafx.scene.image.WritableImage image = iconNode.snapshot(parameters, null);
        return new ImageView(image);
    }
}

// Method to update the theme based on dark mode setting
private void updateTheme(boolean isDarkMode) {
    // Update CSS
    scene.getStylesheets().clear();
    if (isDarkMode) {
        scene.getStylesheets().add(getClass().getResource(DARK_THEME_CSS) != null ? 
            getClass().getResource(DARK_THEME_CSS).toExternalForm() : "");
    } else {
        scene.getStylesheets().add(getClass().getResource(LIGHT_THEME_CSS) != null ? 
            getClass().getResource(LIGHT_THEME_CSS).toExternalForm() : "");
    }
    
    // Update icons in the UI
    updateAllIcons();

    // Update sidebar colors
    updateSidebarColors();
}

// Method to update all icons in the UI based on current theme
private void updateAllIcons() {
    // Update home button icon
    if (homeButton != null) {
        homeButton.setGraphic(createIcon("home"));
    }
    
    // Update dark mode toggle icon
    updateDarkModeToggleIcon();
    
    // to ensure all icons are updated
    if (contentPane.getChildren().size() > 0) {
        Node currentContent = contentPane.getChildren().get(0);
        if (currentContent != null) {
            // Force a refresh by removing and re-adding the content
            contentPane.getChildren().clear();
            contentPane.getChildren().add(currentContent);
        }
    }
}
    
    private TitledPane createUserMethodsPane() {
        VBox content = new VBox(5);
        content.setPadding(new Insets(5));
        
        // Add button to create a new method
        Button addButton = new Button("+ Add A New Method");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> showAddMethodForm());
        
        content.getChildren().add(addButton);
        
        // Add separator between button and methods
        Separator separator = new Separator();
        separator.setPadding(new Insets(5, 0, 5, 0));
        content.getChildren().add(separator);
        
        // Add existing user methods if any
        updateUserMethodsList(content);
        
        // Make the content scrollable if it gets too large
        ScrollPane scrollContent = new ScrollPane(content);
        scrollContent.setFitToWidth(true);
        scrollContent.setPrefHeight(Math.min(userMethods.size() * 35 + 60, 300)); // Adjust for button & separator
        scrollContent.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollContent.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollContent.setStyle("-fx-background-color: transparent;");
        
        TitledPane category = new TitledPane("User Methods", scrollContent);
        return category;
    }
    
// 1. Replace your existing updateUserMethodsList method with this version
private void updateUserMethodsList(VBox content) {
    // Remove all items except the add button and separator (first two items)
    if (content.getChildren().size() > 2) {
        content.getChildren().remove(2, content.getChildren().size());
    }
    
    // Add user methods
    if (userMethods.isEmpty()) {
        // Add a label indicating no methods when the list is empty
        Label emptyLabel = new Label("No user methods available. Click '+' to add one.");
        emptyLabel.setStyle("-fx-text-fill: #888888; -fx-font-style: italic;");
        emptyLabel.setPadding(new Insets(10, 0, 0, 0));
        content.getChildren().add(emptyLabel);
    } else {
        for (MethodInfo method : userMethods) {
            HBox methodRow = new HBox(10);
            methodRow.setAlignment(Pos.CENTER_LEFT);
            
            // Hyperlink for viewing the method
            Hyperlink link = new Hyperlink(method.getName().split("\\(")[0]);
            link.setOnAction(e -> displayMethodDetails(method));
            HBox.setHgrow(link, Priority.ALWAYS);
            
            // Edit button
            Button editButton = new Button("Edit");
            editButton.setOnAction(e -> showEditMethodForm(method));
            
            // Delete button
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> deleteUserMethod(method));
            
            methodRow.getChildren().addAll(link, editButton, deleteButton);
            content.getChildren().add(methodRow);
        }
    }
    
    // Add a ScrollPane to make the content area scrollable if needed
    if (content.getParent() instanceof ScrollPane) {
        ScrollPane scrollPane = (ScrollPane) content.getParent();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
    }
    }
    
    private boolean confirmDelete(MethodInfo method) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirm Delete");
    alert.setHeaderText("Delete Custom Method");
    alert.setContentText("Are you sure you want to delete the method '" + 
                        method.getName() + "'?");
    
    return alert.showAndWait().get() == ButtonType.OK;
    }
   
    // Method to set up the search functionality in the sidebar
    private HBox createSearchBox() {
        HBox searchBox = new HBox(5);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.getStyleClass().add("search-container");
        searchBox.setPadding(new Insets(0, 0, 15, 0));
        searchBox.setPrefHeight(32); // Set a fixed height for the search box
        
        // Create search field with fixed height
        searchField = new TextField();
        searchField.setPromptText("Search methods...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefHeight(28); // Set height for the text field
        searchField.setMaxHeight(28);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        
        // Add listener for real-time search as user types
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 2) {
                performSearch(newValue);
            } else if (newValue.isEmpty()) {
                clearSearch();
            }
        });
        
        // Add key event handler for Enter key
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                performSearch(searchField.getText());
            } else if (event.getCode() == KeyCode.ESCAPE) {
                searchField.clear();
                clearSearch();
            }
        });
        
        // Create search button with fixed size
        Button searchButton = new Button();
        searchButton.setGraphic(createIcon("search"));
        searchButton.getStyleClass().add("icon-button");
        searchButton.setPrefSize(28, 28);
        searchButton.setMinSize(28, 28);
        searchButton.setMaxSize(28, 28);
        searchButton.setOnAction(e -> performSearch(searchField.getText()));
        
        // Create clear button (initially hidden) with fixed size
        Button clearButton = new Button();
        clearButton.setGraphic(createIcon("close"));
        clearButton.getStyleClass().add("icon-button");
        clearButton.setPrefSize(28, 28);
        clearButton.setMinSize(28, 28);
        clearButton.setMaxSize(28, 28);
        clearButton.setVisible(false);
        clearButton.setManaged(false);
        clearButton.setOnAction(e -> {
            searchField.clear();
            clearSearch();
        });
        
        // Show/hide clear button based on search field content
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean hasText = newValue != null && !newValue.isEmpty();
            clearButton.setVisible(hasText);
            clearButton.setManaged(hasText);
        });
        
        searchBox.getChildren().addAll(searchField, clearButton, searchButton);
        
        return searchBox;
    }


private void performSearch(String query) {
    if (query == null || query.trim().isEmpty()) {
        clearSearch();
        return;
    }
    
    query = query.toLowerCase().trim();
    
    // Clear previous results
    searchResults.clear();
    
    // Search in all method categories
    searchInMethodList(userMethods, query);
    searchInMethodList(getStringMethods(), query);
    searchInMethodList(getAdvStringMethods(), query);
    searchInMethodList(getCollectionMethods(), query);
    searchInMethodList(getFileIOMethods(), query);
    searchInMethodList(getMathMethods(), query);
    searchInMethodList(getStreamMethods(), query);
    searchInMethodList(getDecIntMethods(), query);
    searchInMethodList(getFormatterMethods(), query);
    searchInMethodList(getDateMethods(), query);
    searchInMethodList(getCharacterMethods(), query);
    
    // Display results
    displaySearchResults();
}

private void searchInMethodList(List<MethodInfo> methods, String query) {
    for (MethodInfo method : methods) {
        // Search in method name, description, and example code
        if (method.getName().toLowerCase().contains(query) ||
            method.getDescription().toLowerCase().contains(query) ||
            method.getParameters().toLowerCase().contains(query) ||
            method.getExample().toLowerCase().contains(query) ||
            method.getUseCases().toLowerCase().contains(query)) {
            
            // Add to results if not already added
            if (!searchResults.contains(method)) {
                searchResults.add(method);
            }
        }
    }
}


private void displaySearchResults() {
    // Create a screen to display search results
    contentPane.getChildren().clear();
    
    VBox resultsContainer = new VBox(15);
    resultsContainer.setPadding(new Insets(25));
    resultsContainer.getStyleClass().add("search-results-container");
    
    // Create header with result count and search query
    HBox header = new HBox(15);
    header.setAlignment(Pos.CENTER_LEFT);
    
    ImageView searchIcon = createIcon("search");
    
    String resultText = String.format("Found %d results for \"%s\"", 
                                  searchResults.size(), 
                                  searchField.getText().trim());
    Label resultsLabel = new Label(resultText);
    resultsLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
    resultsLabel.getStyleClass().add("results-header");
    
    Button clearButton = new Button("Clear Search");
    clearButton.setGraphic(createIcon("close"));
    clearButton.getStyleClass().add("action-button");
    clearButton.setOnAction(e -> {
        searchField.clear();
        clearSearch();
        showWelcomeContent();
    });
    
    header.getChildren().addAll(searchIcon, resultsLabel);
    HBox.setHgrow(resultsLabel, Priority.ALWAYS);
    
    HBox actionBar = new HBox(10);
    actionBar.setAlignment(Pos.CENTER_RIGHT);
    actionBar.getChildren().add(clearButton);
    
    // Create results list
    VBox resultsList = new VBox(10);
    resultsList.setPadding(new Insets(15, 0, 0, 0));
    
    if (searchResults.isEmpty()) {
        // Show no results message
        VBox noResultsBox = new VBox(15);
        noResultsBox.setAlignment(Pos.CENTER);
        noResultsBox.setPadding(new Insets(50, 20, 50, 20));
        noResultsBox.getStyleClass().add("no-results-box");
        
        ImageView noResultsIcon = createIcon("search_off", 48);
        
        Label noResultsLabel = new Label("No methods found");
        noResultsLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        Label suggestionLabel = new Label("Try different keywords or check your spelling");
        suggestionLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        
        noResultsBox.getChildren().addAll(noResultsIcon, noResultsLabel, suggestionLabel);
        resultsList.getChildren().add(noResultsBox);
    } else {
        // Group results by category
        Map<String, List<MethodInfo>> categorizedResults = new LinkedHashMap<>();
        
        // Detect which category each method belongs to
        for (MethodInfo method : searchResults) {
            String category = getCategoryForMethod(method);
            if (!categorizedResults.containsKey(category)) {
                categorizedResults.put(category, new ArrayList<>());
            }
            categorizedResults.get(category).add(method);
        }
        
        // Add category headers and methods
        for (Map.Entry<String, List<MethodInfo>> entry : categorizedResults.entrySet()) {
            String category = entry.getKey();
            List<MethodInfo> methods = entry.getValue();
            
            Label categoryLabel = new Label(category);
            categoryLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            categoryLabel.getStyleClass().add("category-header");
            categoryLabel.setPadding(new Insets(10, 0, 5, 0));
            
            resultsList.getChildren().add(categoryLabel);
            
            // Add result cards for each method
            for (MethodInfo method : methods) {
                resultsList.getChildren().add(createResultCard(method));
            }
            
            // Add separator between categories
            if (categorizedResults.size() > 1) {
                Separator separator = new Separator();
                separator.setPadding(new Insets(10, 0, 10, 0));
                resultsList.getChildren().add(separator);
            }
        }
    }
    
    // Create scrollable container
    ScrollPane scrollPane = new ScrollPane(resultsList);
    scrollPane.setFitToWidth(true);
    scrollPane.getStyleClass().add("results-scroll-pane");
    VBox.setVgrow(scrollPane, Priority.ALWAYS);
    
    // Add everything to the container
    resultsContainer.getChildren().addAll(header, actionBar, scrollPane);
    
    // Save reference to the container for updates
    searchResultsContainer = resultsContainer;
    
    // Add to content pane
    contentPane.getChildren().add(resultsContainer);
}


private VBox createResultCard(MethodInfo method) {
    VBox card = new VBox(8);
    card.setPadding(new Insets(15));
    card.getStyleClass().addAll("card", "result-card");
    
    // Method name with highlight
    Hyperlink nameLink = new Hyperlink(method.getName());
    nameLink.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
    nameLink.getStyleClass().add("result-method-name");
    nameLink.setOnAction(e -> displayMethodDetails(method));
    
    // Description preview (truncated)
    String description = method.getDescription();
    if (description.length() > 120) {
        description = description.substring(0, 120) + "...";
    }
    
    Label descLabel = new Label(description);
    descLabel.setWrapText(true);
    descLabel.getStyleClass().add("result-description");
    
    // View button
    Button viewButton = new Button("View Details");
    viewButton.getStyleClass().add("primary-button");
    viewButton.setOnAction(e -> displayMethodDetails(method));
    
    // Align button to the right
    HBox buttonBox = new HBox();
    buttonBox.setAlignment(Pos.CENTER_RIGHT);
    buttonBox.getChildren().add(viewButton);
    
    card.getChildren().addAll(nameLink, descLabel, buttonBox);
    
    return card;
}

// Method to get category for a method
private String getCategoryForMethod(MethodInfo method) {
    if (userMethods.contains(method)) {
        return "User Methods";
    } else if (getStringMethods().contains(method)) {
        return "String Methods";
    } else if (getAdvStringMethods().contains(method)) {
        return "Advanced String Methods";
    } else if (getCollectionMethods().contains(method)) {
        return "Collection Methods";
    } else if (getFileIOMethods().contains(method)) {
        return "File I/O Methods";
    } else if (getMathMethods().contains(method)) {
        return "Math Methods";
    } else if (getStreamMethods().contains(method)) {
        return "Stream API Methods";
    } else if (getDecIntMethods().contains(method)) {
        return "Big Decimal / Big Int Methods";
    } else if (getFormatterMethods().contains(method)) {
        return "Formatter Methods";
    } else if (getDateMethods().contains(method)) {
        return "Date and Time Methods";
    } else if (getCharacterMethods().contains(method)) {
        return "Character Methods";
    } else {
        return "Other Methods";
    }
}

// Method to clear search results and reset UI
private void clearSearch() {
    searchResults.clear();
    
    // Clear the search results view if it's currently showing
    if (contentPane.getChildren().size() == 1 && 
        contentPane.getChildren().get(0) == searchResultsContainer) {
        showWelcomeContent();
    }
}

    private void deleteUserMethod(MethodInfo method) {
        if (confirmDelete(method)) {
            // 1. Remove the method from the list
            boolean removed = userMethods.remove(method);
            
            // 2. Debug output to verify removal
            System.out.println("Method removed: " + removed + ", Remaining methods: " + userMethods.size());
            
            // 3. Save the updated list immediately
            saveUserMethods();
            
            // 4. Update the UI
            updateUserMethodsPane();
            
            // 5. Show welcome content
            showWelcomeContent();
        }
    }

    private void showAddMethodForm() {
        // Create a dialog for adding a new method
        Dialog<MethodInfo> dialog = new Dialog<>();
        dialog.setTitle("Add New Method");
        dialog.setHeaderText("Create Your Custom Method");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Method Name (e.g., MyClass.myMethod(int param))");
        
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Description of what the method does");
        descriptionField.setPrefRowCount(3);
        
        TextArea parametersField = new TextArea();
        parametersField.setPromptText("Details about parameters, one per line");
        parametersField.setPrefRowCount(3);
        
        TextArea exampleField = new TextArea();
        exampleField.setPromptText("Example code showing how to use the method");
        exampleField.setPrefRowCount(5);
        
        TextArea useCasesField = new TextArea();
        useCasesField.setPromptText("Common use cases, one per line");
        useCasesField.setPrefRowCount(3);

            // Add a paste all button for quick data entry
            Button pasteAllButton = new Button("Paste All Content");
            pasteAllButton.setOnAction(e -> {
                // Create a dialog with a text area for pasting
                Dialog<String> pasteDialog = new Dialog<>();
                pasteDialog.setTitle("Paste Method Content");
                pasteDialog.setHeaderText("Paste method details using the specified format");
                
                // Set the button types
                ButtonType pasteButtonType = new ButtonType("Parse Content", ButtonBar.ButtonData.OK_DONE);
                pasteDialog.getDialogPane().getButtonTypes().addAll(pasteButtonType, ButtonType.CANCEL);
                
                // Create text area for pasting
                TextArea pasteArea = new TextArea();
                pasteArea.setPrefRowCount(15);
                pasteArea.setPrefColumnCount(50);
                
                // Create format instructions with an example
                Label formatLabel = new Label("Required Format (include all section markers):");
                formatLabel.setStyle("-fx-font-weight: bold;");
                
                TextArea formatInstructions = new TextArea(
                    "NAME:\nYour method name here\n" +
                    "DESCRIPTION:\nYour description here\n" +
                    "PARAMETERS:\nYour parameters here\n" +
                    "EXAMPLE:\nYour code example here\n" +
                    "USECASES:\nYour use cases here"
                );
                formatInstructions.setEditable(false);
                formatInstructions.setPrefRowCount(7);
                formatInstructions.setStyle("-fx-control-inner-background: #f8f8f8;");
                
                VBox pasteContent = new VBox(10);
                pasteContent.getChildren().addAll(
                    formatLabel,
                    formatInstructions,
                    new Label("Paste your content here:"),
                    pasteArea
                );
                pasteContent.setPadding(new Insets(10));
                
                pasteDialog.getDialogPane().setContent(pasteContent);
                pasteDialog.getDialogPane().setPrefWidth(500);
                
                // Request focus on the paste area
                pasteArea.requestFocus();
                
                // Convert the result when the paste button is clicked
                pasteDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == pasteButtonType) {
                        return pasteArea.getText();
                    }
                    return null;
                });
                
                // Process the pasted content
                pasteDialog.showAndWait().ifPresent(content -> {
                    try {
                        // Parse the content using the specific format markers
                        String name = extractSection(content, "NAME:");
                        String description = extractSection(content, "DESCRIPTION:");
                        String parameters = extractSection(content, "PARAMETERS:");
                        String example = extractSection(content, "EXAMPLE:");
                        String useCases = extractSection(content, "USECASES:");
                        
                        // Update the form fields
                        if (name != null) nameField.setText(name);
                        if (description != null) descriptionField.setText(description);
                        if (parameters != null) parametersField.setText(parameters);
                        if (example != null) exampleField.setText(formatPastedCode(example));
                        if (useCases != null) useCasesField.setText(useCases);
                        
                        // Show confirmation
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Success");
                        success.setHeaderText("Content Parsed Successfully");
                        success.setContentText("All fields have been updated with the pasted content.");
                        success.showAndWait();
                    } catch (Exception ex) {
                        // Show error if parsing fails
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Parsing Error");
                        error.setHeaderText("Could not parse the content");
                        error.setContentText("Make sure your content follows the required format with all section markers (NAME:, DESCRIPTION:, etc.)");
                        error.showAndWait();
                    }
                });
            });
            
        
        grid.add(new Label("Method Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Parameters:"), 0, 2);
        grid.add(parametersField, 1, 2);
        grid.add(new Label("Example:"), 0, 3);
        grid.add(exampleField, 1, 3);
        grid.add(new Label("Use Cases:"), 0, 4);
        grid.add(useCasesField, 1, 4);
        grid.add(pasteAllButton, 1, 5);

        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the name field by default
        nameField.requestFocus();
        
        // Convert the result to a method when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (nameField.getText().trim().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Validation Error");
                    alert.setHeaderText("Method Name Required");
                    alert.setContentText("Please enter a name for your method.");
                    alert.showAndWait();
                    return null;
                }
                
                return new MethodInfo(
                    nameField.getText(),
                    descriptionField.getText(),
                    parametersField.getText(),
                    exampleField.getText(),
                    useCasesField.getText()
                );
            }
            return null;
        });
        
        // Show the dialog and process the result
        dialog.showAndWait().ifPresent(methodInfo -> {
            userMethods.add(methodInfo);
            updateUserMethodsPane();
            saveUserMethods();
            displayMethodDetails(methodInfo);
        });
    }

    // Helper method to format pasted code
     private String formatPastedCode(String code) {
            if (code == null || code.trim().isEmpty()) {
                return "";
            }
            
            // Clean up the code
            code = code.trim();
            
            // Check if code already has proper indentation
            boolean hasIndentation = false;
            String[] lines = code.split("\n");
            for (int i = 1; i < lines.length; i++) {
                if (lines[i].startsWith("    ") || lines[i].startsWith("\t")) {
                    hasIndentation = true;
                    break;
                }
            }
            
            // If the code doesn't have proper indentation, add it
            if (!hasIndentation && lines.length > 1) {
                StringBuilder formattedCode = new StringBuilder();
                
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i].trim();
                    
                    // Add the line with proper indentation
                    if (i == 0) {
                        formattedCode.append(line).append("\n");
                    } else {
                        formattedCode.append("    ").append(line).append("\n");
                    }
                }
                
                return formattedCode.toString().trim();
            }
            
            return code;
        }

    // Helper method to extract sections from the pasted content
private String extractSection(String content, String sectionMarker) {
    int markerIndex = content.indexOf(sectionMarker);
    if (markerIndex == -1) return null;
    
    // Find the start of the section content (after the marker)
    int contentStart = markerIndex + sectionMarker.length();
    
    // Find the end of the section (the next section marker or end of string)
    int contentEnd = content.length();
    String[] markers = {"NAME:", "DESCRIPTION:", "PARAMETERS:", "EXAMPLE:", "USECASES:"};
    for (String marker : markers) {
        int nextMarker = content.indexOf(marker, contentStart);
        if (nextMarker > markerIndex && (nextMarker < contentEnd)) {
            contentEnd = nextMarker;
        }
    }
    
    // Extract and trim the section content
    return content.substring(contentStart, contentEnd).trim();
}
    
    private void showEditMethodForm(MethodInfo methodToEdit) {
        // Create a dialog for editing an existing method
        Dialog<MethodInfo> dialog = new Dialog<>();
        dialog.setTitle("Edit Method");
        dialog.setHeaderText("Edit Your Custom Method");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(methodToEdit.getName());
        
        TextArea descriptionField = new TextArea(methodToEdit.getDescription());
        descriptionField.setPrefRowCount(3);
        
        TextArea parametersField = new TextArea(methodToEdit.getParameters());
        parametersField.setPrefRowCount(3);
        
        TextArea exampleField = new TextArea(methodToEdit.getExample());
        exampleField.setPrefRowCount(5);
        
        TextArea useCasesField = new TextArea(methodToEdit.getUseCases());
        useCasesField.setPrefRowCount(3);

            // Add a paste all button for quick data entry (same as in add form)
            Button pasteAllButton = new Button("Paste All Content");
            pasteAllButton.setOnAction(e -> {
                // Create a dialog with a text area for pasting
                Dialog<String> pasteDialog = new Dialog<>();
                pasteDialog.setTitle("Paste Method Content");
                pasteDialog.setHeaderText("Paste method details using the specified format");
                
                // Set the button types
                ButtonType pasteButtonType = new ButtonType("Parse Content", ButtonBar.ButtonData.OK_DONE);
                pasteDialog.getDialogPane().getButtonTypes().addAll(pasteButtonType, ButtonType.CANCEL);
                
                // Create text area for pasting
                TextArea pasteArea = new TextArea();
                pasteArea.setPrefRowCount(15);
                pasteArea.setPrefColumnCount(50);
                
                // Create format instructions with an example
                Label formatLabel = new Label("Required Format (include all section markers):");
                formatLabel.setStyle("-fx-font-weight: bold;");
                
                TextArea formatInstructions = new TextArea(
                    "NAME:\nYour method name here\n" +
                    "DESCRIPTION:\nYour description here\n" +
                    "PARAMETERS:\nYour parameters here\n" +
                    "EXAMPLE:\nYour code example here\n" +
                    "USECASES:\nYour use cases here"
                );
                formatInstructions.setEditable(false);
                formatInstructions.setPrefRowCount(7);
                formatInstructions.setStyle("-fx-control-inner-background: #f8f8f8;");
                
                VBox pasteContent = new VBox(10);
                pasteContent.getChildren().addAll(
                    formatLabel,
                    formatInstructions,
                    new Label("Paste your content here:"),
                    pasteArea
                );
                pasteContent.setPadding(new Insets(10));
                
                pasteDialog.getDialogPane().setContent(pasteContent);
                pasteDialog.getDialogPane().setPrefWidth(500);
                
                // Request focus on the paste area
                pasteArea.requestFocus();
                
                // Convert the result when the paste button is clicked
                pasteDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == pasteButtonType) {
                        return pasteArea.getText();
                    }
                    return null;
                });
                
                // Process the pasted content
                pasteDialog.showAndWait().ifPresent(content -> {
                    try {
                        // Parse the content using the specific format markers
                        String name = extractSection(content, "NAME:");
                        String description = extractSection(content, "DESCRIPTION:");
                        String parameters = extractSection(content, "PARAMETERS:");
                        String example = extractSection(content, "EXAMPLE:");
                        String useCases = extractSection(content, "USECASES:");
                        
                        // Update the form fields
                        if (name != null) nameField.setText(name);
                        if (description != null) descriptionField.setText(description);
                        if (parameters != null) parametersField.setText(parameters);
                        if (example != null) exampleField.setText(formatPastedCode(example));
                        if (useCases != null) useCasesField.setText(useCases);
                        
                        // Show confirmation
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Success");
                        success.setHeaderText("Content Parsed Successfully");
                        success.setContentText("All fields have been updated with the pasted content.");
                        success.showAndWait();
                    } catch (Exception ex) {
                        // Show error if parsing fails
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Parsing Error");
                        error.setHeaderText("Could not parse the content");
                        error.setContentText("Make sure your content follows the required format with all section markers (NAME:, DESCRIPTION:, etc.)");
                        error.showAndWait();
                    }
                });
            });
        
        grid.add(new Label("Method Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Parameters:"), 0, 2);
        grid.add(parametersField, 1, 2);
        grid.add(new Label("Example:"), 0, 3);
        grid.add(exampleField, 1, 3);
        grid.add(new Label("Use Cases:"), 0, 4);
        grid.add(useCasesField, 1, 4);
        grid.add(pasteAllButton, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        // Convert the result to a method when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (nameField.getText().trim().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Validation Error");
                    alert.setHeaderText("Method Name Required");
                    alert.setContentText("Please enter a name for your method.");
                    alert.showAndWait();
                    return null;
                }
                
                return new MethodInfo(
                    nameField.getText(),
                    descriptionField.getText(),
                    parametersField.getText(),
                    exampleField.getText(),
                    useCasesField.getText()
                );
            }
            return null;
        });
        
        // Show the dialog and process the result
        dialog.showAndWait().ifPresent(updatedMethod -> {
            // Replace the old method with the updated one
            int index = userMethods.indexOf(methodToEdit);
            userMethods.set(index, updatedMethod);
            updateUserMethodsPane();
            saveUserMethods();
            displayMethodDetails(updatedMethod);
        });
    }
    
    private void updateUserMethodsPane() {
        // Recreate the entire user methods category to ensure clean state
        int index = -1;
        
        // Find the current index of the user methods category in the accordion
        for (int i = 0; i < ((Accordion)userMethodsCategory.getParent()).getPanes().size(); i++) {
            if (((Accordion)userMethodsCategory.getParent()).getPanes().get(i) == userMethodsCategory) {
                index = i;
                break;
            }
        }
        
        if (index >= 0) {
            // Create a new user methods category
            TitledPane newUserMethodsCategory = createUserMethodsPane();
            
            // Replace the old one
            ((Accordion)userMethodsCategory.getParent()).getPanes().set(index, newUserMethodsCategory);
            
            // Update our reference
            userMethodsCategory = newUserMethodsCategory;
            
            // Make sure it's expanded
            userMethodsCategory.setExpanded(true);
        }
    }
    
    private TitledPane createCategoryPane(String categoryName, List<MethodInfo> methods) {
        VBox content = new VBox(5);
        content.setPadding(new Insets(5));
        
        // Create a list view for the methods in this category
        for (MethodInfo method : methods) {
            Hyperlink link = new Hyperlink(method.getName().split("\\(")[0]);
            link.setOnAction(e -> displayMethodDetails(method));
            content.getChildren().add(link);
        }
        
        // Make the content scrollable if it gets too large
        ScrollPane scrollContent = new ScrollPane(content);
        scrollContent.setFitToWidth(true);
        scrollContent.setPrefHeight(Math.min(methods.size() * 25, 300)); // Limit max height
        scrollContent.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollContent.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollContent.setStyle("-fx-background-color: transparent;");
        
        TitledPane category = new TitledPane(categoryName, scrollContent);
        return category;
    }
    

    private void saveUserMethods() {
        try {
            // Get user home directory for a location we can write to
            String userHome = System.getProperty("user.home");
            File appDir = new File(userHome, ".method-explorer");
            
            // Create the directory if it doesn't exist
            if (!appDir.exists()) {
                if (!appDir.mkdirs()) {
                    System.err.println("Failed to create application directory: " + appDir.getAbsolutePath());
                    showError("Error Saving Methods", "Could not create application directory.");
                    return;
                }
            }
            
            // Create the file in the user's home directory
            File file = new File(appDir, USER_METHODS_FILE);
            
            // Create a backup of the existing file if it exists
            if (file.exists()) {
                File backup = new File(appDir, USER_METHODS_FILE + ".bak");
                try {
                    Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.err.println("Failed to create backup: " + e.getMessage());
                    // Continue anyway, as this is just a backup
                }
            }
            
            // Save the current methods list
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(userMethods);
                System.out.println("Methods saved successfully. Count: " + userMethods.size());
                System.out.println("Saved to: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error Saving Methods", "Could not save your custom methods to file: " + e.getMessage());
        }
    }
    
// Improved load method with error recovery
@SuppressWarnings("unchecked")
private void loadUserMethods() {
    // Get user home directory
    String userHome = System.getProperty("user.home");
    File appDir = new File(userHome, ".method-explorer");
    File file = new File(appDir, USER_METHODS_FILE);
    
    if (!file.exists()) {
        userMethods = new ArrayList<>();
        System.out.println("No user methods file found. Starting with empty list.");
        return;
    }
    
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
        userMethods = (List<MethodInfo>) ois.readObject();
        System.out.println("Methods loaded successfully. Count: " + userMethods.size());
        System.out.println("Loaded from: " + file.getAbsolutePath());
    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
        
        // Try to recover from backup if available
        File backup = new File(appDir, USER_METHODS_FILE + ".bak");
        if (backup.exists()) {
            try (ObjectInputStream backupOis = new ObjectInputStream(new FileInputStream(backup))) {
                userMethods = (List<MethodInfo>) backupOis.readObject();
                System.out.println("Methods recovered from backup. Count: " + userMethods.size());
                return;
            } catch (Exception backupException) {
                backupException.printStackTrace();
            }
        }
        
        // If all else fails, start with empty list
        userMethods = new ArrayList<>();
        showError("Error Loading Methods", "Could not load your custom methods from file. Starting with empty list.");
    }
}
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // All existing methods would remain the same...
    
    private void displayMethodDetails(MethodInfo method) {
    // Clear current content
    contentPane.getChildren().clear();
    
    // Create main layout containers
    VBox mainContainer = new VBox(15);
    mainContainer.setPadding(new Insets(25));
    mainContainer.getStyleClass().add("method-details-container");
    
    // Method header with breadcrumb navigation
    HBox breadcrumbNav = new HBox(8);
    breadcrumbNav.setAlignment(Pos.CENTER_LEFT);
    breadcrumbNav.setPadding(new Insets(0, 0, 15, 0));
    
    Button homeButton = new Button();
    homeButton.setGraphic(createIcon("home"));
    homeButton.getStyleClass().add("breadcrumb-button");
    homeButton.setOnAction(e -> showWelcomeContent());
    
    Label separator = new Label("/");
    separator.getStyleClass().add("breadcrumb-separator");
    
    String categoryName = userMethods.contains(method) ? "User Methods" : "Java Methods";
    Label categoryLabel = new Label(categoryName);
    categoryLabel.getStyleClass().add("breadcrumb-category");
    
    breadcrumbNav.getChildren().addAll(homeButton, separator, categoryLabel);
    
    // For user methods, add edit/delete buttons in a top bar
    HBox topButtonBar = new HBox(10);
    topButtonBar.setAlignment(Pos.CENTER_RIGHT);
    topButtonBar.setPadding(new Insets(0, 0, 10, 0));
    
    if (userMethods.contains(method)) {
        Button editButton = new Button("Edit Method");
        editButton.setGraphic(createIcon("edit"));
        editButton.getStyleClass().add("action-button");
        editButton.setOnAction(e -> showEditMethodForm(method));
        
        Button deleteButton = new Button("Delete Method");
        deleteButton.setGraphic(createIcon("delete"));
        deleteButton.getStyleClass().add("action-button");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> deleteUserMethod(method));
        
        topButtonBar.getChildren().addAll(editButton, deleteButton);
    }
    
    // Create a card container for the method content
    VBox methodCard = new VBox(20);
    methodCard.setPadding(new Insets(25));
    methodCard.getStyleClass().addAll("card", "method-card");
    
    // Method title in a header section
    HBox titleHeader = new HBox();
    titleHeader.setAlignment(Pos.CENTER_LEFT);
    titleHeader.setPadding(new Insets(0, 0, 10, 0));
    
    Label title = new Label(method.getName());
    title.setFont(Font.font("Monospace", FontWeight.BOLD, 18));
    title.setWrapText(true);
    title.getStyleClass().add("method-title");
    
    Button copyNameButton = new Button();
    copyNameButton.setGraphic(createIcon("content_copy"));
    copyNameButton.getStyleClass().add("icon-button");
    copyNameButton.setTooltip(new Tooltip("Copy method name"));
    copyNameButton.setOnAction(e -> {
        ClipboardContent content = new ClipboardContent();
        content.putString(method.getName());
        Clipboard.getSystemClipboard().setContent(content);
        
        // Show feedback
        showCopyFeedback(copyNameButton, "Copied!");
    });
    
    titleHeader.getChildren().addAll(title, copyNameButton);
    HBox.setHgrow(title, Priority.ALWAYS);
    
    // Divider
    Separator divider = new Separator();
    divider.getStyleClass().add("section-divider");
    
    // Description section with card
    VBox descriptionSection = createMethodSection("Description", method.getDescription(), "description");
    
    // Parameters section with card
    VBox parametersSection = createMethodSection("Parameters", method.getParameters(), "list");
    
    // Example section with syntax highlighting and code card
    VBox exampleSection = new VBox(15);
    exampleSection.getStyleClass().add("method-section");
    
    HBox exampleHeader = new HBox(10);
    exampleHeader.setAlignment(Pos.CENTER_LEFT);
    
    ImageView exampleIcon = createIcon("code");
    Label exampleTitle = new Label("Example");
    exampleTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
    exampleTitle.getStyleClass().add("section-title");
    
    exampleHeader.getChildren().addAll(exampleIcon, exampleTitle);
    
    // Create syntax-highlighted code container
    VBox codeContainer = new VBox(10);
    codeContainer.setPadding(new Insets(20));
    codeContainer.getStyleClass().add("code-container");
    
    // Add the pretty syntax-highlighted view (non-editable)
    TextFlow highlightedCode = createSyntaxHighlightedExample(method.getExample());
    highlightedCode.getStyleClass().add("highlighted-code");
    
    // Add the plain text version for copying (initially hidden)
    TextArea plainCode = new TextArea(method.getExample());
    plainCode.setEditable(false);
    plainCode.setVisible(false);
    plainCode.setManaged(false);
    plainCode.setPrefRowCount(Math.min(10, countLines(method.getExample())));
    plainCode.getStyleClass().add("plain-code");
    
    // Add Copy button with icon
    HBox codeButtons = new HBox(10);
    codeButtons.setAlignment(Pos.CENTER_RIGHT);
    
    Button copyCodeButton = new Button("Copy Code");
    copyCodeButton.setGraphic(createIcon("content_copy"));
    copyCodeButton.getStyleClass().add("copy-button");
    copyCodeButton.setOnAction(e -> {
        plainCode.selectAll();
        plainCode.copy();
        // Show feedback
        showCopyFeedback(copyCodeButton, "Code Copied!");
    });
    
    codeButtons.getChildren().add(copyCodeButton);
    
    codeContainer.getChildren().addAll(highlightedCode, plainCode, codeButtons);
    exampleSection.getChildren().addAll(exampleHeader, codeContainer);
    
    // Use cases section with card
    VBox useCasesSection = createMethodSection("Common Use Cases", method.getUseCases(), "lightbulb");

    // Add everything to the method card
    methodCard.getChildren().addAll(
        titleHeader, 
        divider,
        descriptionSection,
        parametersSection,
        exampleSection,
        useCasesSection
    );
    
    // Create footer with action buttons (for user methods)
    HBox footerButtons = new HBox(15);
    footerButtons.setAlignment(Pos.CENTER_RIGHT);
    footerButtons.setPadding(new Insets(15, 0, 0, 0));
    
    if (userMethods.contains(method)) {
        Button editButtonFooter = new Button("Edit Method");
        editButtonFooter.setGraphic(createIcon("edit"));
        editButtonFooter.getStyleClass().add("primary-button");
        editButtonFooter.setOnAction(e -> showEditMethodForm(method));
        
        Button deleteButtonFooter = new Button("Delete Method");
        deleteButtonFooter.setGraphic(createIcon("delete"));
        deleteButtonFooter.getStyleClass().addAll("danger-button");
        deleteButtonFooter.setOnAction(e -> deleteUserMethod(method));
        
        footerButtons.getChildren().addAll(editButtonFooter, deleteButtonFooter);
    }
    
    // Add components to the main container
    mainContainer.getChildren().addAll(breadcrumbNav, topButtonBar, methodCard);
    
    if (!footerButtons.getChildren().isEmpty()) {
        mainContainer.getChildren().add(footerButtons);
    }
    
    // Create scrollable view
    ScrollPane scrollPane = new ScrollPane(mainContainer);
    scrollPane.setFitToWidth(true);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.getStyleClass().add("details-scroll-pane");
    
    // Make the main ScrollPane take all available space
    VBox.setVgrow(scrollPane, Priority.ALWAYS);
    
    contentPane.getChildren().add(scrollPane);
}

// Helper method to create a section for method details
private VBox createMethodSection(String title, String content, String iconName) {
    VBox section = new VBox(15);
    section.getStyleClass().add("method-section");
    
    HBox header = new HBox(10);
    header.setAlignment(Pos.CENTER_LEFT);
    
    ImageView icon = createIcon(iconName);
    Label sectionTitle = new Label(title);
    sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
    sectionTitle.getStyleClass().add("section-title");
    
    header.getChildren().addAll(icon, sectionTitle);
    
    // Content with copy button
    HBox contentBox = new HBox();
    contentBox.setAlignment(Pos.CENTER_LEFT);
    VBox.setMargin(contentBox, new Insets(0, 0, 0, 30)); // Indent content
    
    TextArea textArea = new TextArea(content);
    textArea.setEditable(false);
    textArea.setWrapText(true);
    textArea.setPrefRowCount(Math.min(10, countLines(content)));
    textArea.getStyleClass().add("section-content");
    HBox.setHgrow(textArea, Priority.ALWAYS);
    
    Button copyButton = new Button();
    copyButton.setGraphic(createIcon("content_copy"));
    copyButton.getStyleClass().add("icon-button");
    copyButton.setTooltip(new Tooltip("Copy to clipboard"));
    copyButton.setOnAction(e -> {
        ClipboardContent clipContent = new ClipboardContent();
        clipContent.putString(content);
        Clipboard.getSystemClipboard().setContent(clipContent);
        
        // Show feedback
        showCopyFeedback(copyButton, "Copied!");
    });
    
    contentBox.getChildren().addAll(textArea, copyButton);
    
    section.getChildren().addAll(header, contentBox);
    return section;
}

// Fixed method to show copy feedback
private void showCopyFeedback(Button button, String message) {
    // Capture the original state in final variables
    final String originalText = button.getText();
    final Node originalGraphic = button.getGraphic();
    
    // Update the button to show feedback
    if (originalText != null && !originalText.isEmpty()) {
        button.setText(message);
    } else {
        button.setGraphic(null);
        button.setText(message);
    }
    
    // Reset after 1.5 seconds
    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
    pause.setOnFinished(evt -> {
        if (originalText != null && !originalText.isEmpty()) {
            button.setText(originalText);
        } else {
            button.setText("");
            button.setGraphic(originalGraphic);
        }
    });
    pause.play();
}

    // Update the showWelcomeContent method to ensure icons use proper colors
private void showWelcomeContent() {
    // Clear current content
    contentPane.getChildren().clear();
    
    // Main scroll pane for all content
    ScrollPane mainScroll = new ScrollPane();
    mainScroll.setFitToWidth(true);
    mainScroll.getStyleClass().add("main-scroll-pane");
    
    // Master container
    VBox masterContainer = new VBox(30);
    masterContainer.setPadding(new Insets(40, 60, 60, 60));
    masterContainer.getStyleClass().add("welcome-container");
    
    // Welcome header section with gradient background
    StackPane headerPane = new StackPane();
    headerPane.getStyleClass().add("welcome-header");
    headerPane.setPadding(new Insets(40, 30, 40, 30));
    headerPane.setMinHeight(180);
    
    VBox headerContent = new VBox(15);
    headerContent.setAlignment(Pos.CENTER);
    
    Label welcomeHeader = new Label("Welcome to Method Explorer");
    welcomeHeader.setFont(Font.font("System", FontWeight.BOLD, 32));
    welcomeHeader.getStyleClass().add("welcome-title");
    
    Label welcomeSubtitle = new Label("Your guide to Java methods");
    welcomeSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 18));
    welcomeSubtitle.getStyleClass().add("welcome-subtitle");

    Label creatorTitle = new Label("made by vaughn-ws and claude");
    creatorTitle.setFont(Font.font("System", FontWeight.NORMAL, 10));
    creatorTitle.getStyleClass().add("welcome-creator");
    
    headerContent.getChildren().addAll(welcomeHeader, welcomeSubtitle, creatorTitle);
    headerPane.getChildren().add(headerContent);
    
    // Quick start guide section - with modern cards
    VBox quickStartSection = createSection("Quick Start Guide", 
        "Get started with Method Explorer in three simple steps:", "rocket");
    
    // Step cards with numbers and shadow effect
    HBox stepCards = new HBox(20);
    stepCards.setAlignment(Pos.CENTER);
    stepCards.setPadding(new Insets(20, 0, 30, 0));
    
    stepCards.getChildren().addAll(
        createStepCard("1", "Browse", "Expand categories in the sidebar to see available methods", "category"),
        createStepCard("2", "Select", "Click on any method name to view its details", "touch_app"),
        createStepCard("3", "Explore", "Read descriptions, parameters, examples, and use cases", "explore")
    );
    
    quickStartSection.getChildren().add(stepCards);
    
    // How to use section with tabs - modern styling
    VBox howToSection = createSection("How To Guide", 
        "Learn how to make the most of Method Explorer with these helpful guides:", "help");
    
    TabPane howToTabs = new TabPane();
    howToTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    howToTabs.getStyleClass().add("how-to-tabs");
    
    // Navigate tab
    Tab navigateTab = new Tab("Navigate the App");
    navigateTab.setGraphic(createIcon("navigation", 16));
    navigateTab.setContent(createHowToContent(
        "Navigating the Application",
        new String[] {
            "Use the sidebar categories to browse methods by type",
            "Click on a category to expand or collapse it",
            "Click on any method name to see its full details",
            "The main content area will display comprehensive information about the selected method",
            "All text fields are copyable - just select the text you want and use Ctrl+C",
            "For code examples, use the 'Copy Code to Clipboard' button for easy copying",
            "Click the Home button in the top bar to return to this welcome screen at any time",
            "Toggle between light and dark modes using the icon in the top-right corner",
            "Use the search bar to quickly find methods across all categories"
        }
    ));
    
    // User methods tab
    Tab userMethodsTab = new Tab("Create User Methods");
    userMethodsTab.setGraphic(createIcon("create", 16));
    userMethodsTab.setContent(createHowToContent(
        "Creating Your Own Custom Methods",
        new String[] {
            "Click on the '+ Add New Method' button in the User Methods category",
            "Fill in the details for your custom method:",
            "   • Method Name: Enter the full method signature (e.g., MyClass.myMethod(String param))",
            "   • Description: Provide a clear explanation of what the method does",
            "   • Parameters: List all parameters with their types and descriptions",
            "   • Example Code: Show a practical example of how to use the method",
            "   • Use Cases: Describe common scenarios where this method is useful",
            "Click 'Save' to add your method to the User Methods category",
            "Your custom methods will be saved automatically and available when you restart the app"
        }
    ));
    
    // Tips tab
    Tab tipsTab = new Tab("Pro Tips");
    tipsTab.setGraphic(createIcon("lightbulb", 16));
    tipsTab.setContent(createHowToContent(
        "Pro Tips for Maximum Productivity",
        new String[] {
            "Use the search function to quickly find specific methods",
            "Organize your custom methods with clear naming conventions",
            "Examples with real-world scenarios are more useful than abstract ones",
            "Add code comments in your examples to explain complex parts",
            "Custom methods are saved locally - back them up if needed",
            "Edit or delete custom methods using the buttons in the detail view",
            "Switch to dark mode for late-night coding sessions to reduce eye strain"
        }
    ));
    
    howToTabs.getTabs().addAll(navigateTab, userMethodsTab, tipsTab);
    howToSection.getChildren().add(howToTabs);
    
    // Features highlight section with modern cards
    VBox featuresSection = createSection("Key Features", 
        "Method Explorer offers powerful features to enhance your Java development:", "star");
    
    GridPane featuresGrid = new GridPane();
    featuresGrid.setHgap(20);
    featuresGrid.setVgap(20);
    featuresGrid.setPadding(new Insets(20, 0, 30, 0));
    
    featuresGrid.add(createFeatureBox("Comprehensive Reference", 
        "Detailed documentation for common Java methods", "library_books"), 0, 0);
    featuresGrid.add(createFeatureBox("Syntax Highlighting", 
        "Color-coded example code for better readability", "code"), 1, 0);
    featuresGrid.add(createFeatureBox("Custom Methods", 
        "Add and save your own method references", "bookmark_add"), 0, 1);
    featuresGrid.add(createFeatureBox("Copyable Content", 
        "Easily copy code and descriptions to your projects", "content_copy"), 1, 1);
    
    // Add a footer with version information
    HBox footer = new HBox();
    footer.setAlignment(Pos.CENTER);
    footer.setPadding(new Insets(30, 0, 0, 0));
    
    Label versionLabel = new Label("Method Explorer v1.0");
    versionLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
    versionLabel.setStyle("-fx-text-fill: #888888;");
    
    footer.getChildren().add(versionLabel);
    
    featuresSection.getChildren().addAll(featuresGrid, footer);
    
    // Add all sections to the master container
    masterContainer.getChildren().addAll(
        headerPane,
        quickStartSection,
        howToSection,
        featuresSection
    );
    
    // Set the content and add to the main pane
    mainScroll.setContent(masterContainer);
    contentPane.getChildren().add(mainScroll);
}
    
    
    // Helper method to create a section with title and description
    // Updated helper method to create a section with an icon
    private VBox createSection(String title, String description, String iconName) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20, 0, 10, 0));
        section.getStyleClass().add("welcome-section");
        
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        
        // Create icon with explicit color based on current theme
        ImageView icon = createIcon(iconName);
        
        Label sectionTitle = new Label(title);
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 24));
        sectionTitle.getStyleClass().add("section-title");
        
        titleRow.getChildren().addAll(icon, sectionTitle);
        
        Label sectionDesc = new Label(description);
        sectionDesc.setFont(Font.font("System", FontWeight.NORMAL, 16));
        sectionDesc.setWrapText(true);
        sectionDesc.setPadding(new Insets(0, 0, 10, 0));
        sectionDesc.getStyleClass().add("section-description");
        
        section.getChildren().addAll(titleRow, sectionDesc);
        return section;
    }
    
// Updated createStepCard method with proper icon coloring
private VBox createStepCard(String number, String title, String description, String iconName) {
    VBox card = new VBox(15);
    card.setAlignment(Pos.TOP_CENTER);
    card.setPadding(new Insets(25));
    card.setPrefWidth(250);
    card.setMinHeight(200);
    card.getStyleClass().addAll("card", "step-card");
    
    // Number circle
    StackPane numberCircle = new StackPane();
    numberCircle.setMinSize(45, 45);
    numberCircle.setMaxSize(45, 45);
    numberCircle.getStyleClass().add("number-circle");
    
    Label numberLabel = new Label(number);
    numberLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
    numberLabel.getStyleClass().add("number-label");
    
    numberCircle.getChildren().add(numberLabel);
    
    // Icon - ensure proper color
    ImageView icon = createIcon(iconName, 24);
    
    // Title and description
    Label titleLabel = new Label(title);
    titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
    titleLabel.setPadding(new Insets(5, 0, 5, 0));
    titleLabel.getStyleClass().add("step-title");
    
    Label descLabel = new Label(description);
    descLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
    descLabel.setWrapText(true);
    descLabel.setAlignment(Pos.CENTER);
    descLabel.getStyleClass().add("step-description");
    
    card.getChildren().addAll(numberCircle, icon, titleLabel, descLabel);
    return card;
}
    
    // Helper method to create content for how-to tabs
    private VBox createHowToContent(String title, String[] steps) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.getStyleClass().add("how-to-content");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setPadding(new Insets(0, 0, 15, 0));
        titleLabel.getStyleClass().add("how-to-title");
        
        VBox stepsList = new VBox(12);
        
        for (int i = 0; i < steps.length; i++) {
            HBox step = new HBox(15);
            step.setAlignment(Pos.CENTER_LEFT);
            
            // Create a rounded bullet or number
            StackPane bulletCircle = new StackPane();
            bulletCircle.setMinSize(26, 26);
            bulletCircle.setMaxSize(26, 26);
            bulletCircle.getStyleClass().add("bullet-circle");
            
            Label bulletLabel = new Label((i + 1) + "");
            bulletLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
            bulletLabel.getStyleClass().add("bullet-label");
            
            bulletCircle.getChildren().add(bulletLabel);
            
            // Step text
            Label stepText = new Label(steps[i]);
            stepText.setFont(Font.font("System", FontWeight.NORMAL, 14));
            stepText.setWrapText(true);
            stepText.getStyleClass().add("step-text");
            HBox.setHgrow(stepText, Priority.ALWAYS);
            
            step.getChildren().addAll(bulletCircle, stepText);
            stepsList.getChildren().add(step);
        }
        
        content.getChildren().addAll(titleLabel, stepsList);
        return content;
    }
    
// Updated helper method to create a feature highlight box
private VBox createFeatureBox(String title, String description, String iconName) {
    VBox featureBox = new VBox(15);
    featureBox.setPadding(new Insets(25));
    featureBox.setMinHeight(150);
    featureBox.getStyleClass().add("feature-box");
    
    // Icon at the top - ensure proper color
    ImageView icon = createIcon(iconName, 32);
    
    Label titleLabel = new Label(title);
    titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
    titleLabel.getStyleClass().add("feature-title");
    
    Label descLabel = new Label(description);
    descLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
    descLabel.setWrapText(true);
    descLabel.getStyleClass().add("feature-description");
    
    featureBox.getChildren().addAll(icon, titleLabel, descLabel);
    return featureBox;
}

private void updateSidebarColors() {
    // Get the sidebar from the splitPane (assuming it's the first item)
    if (splitPane != null && splitPane.getItems().size() > 0) {
        VBox sidebar = (VBox) splitPane.getItems().get(0);
        
        // Force refresh the sidebar
        VBox newSidebar = createSidebar();
        splitPane.getItems().set(0, newSidebar);
    }
}


    // Data methods for different categories
    private List<MethodInfo> getStringMethods() {
        List<MethodInfo> methods = new ArrayList<>();
        
        methods.add(new MethodInfo(
            "String.substring(int beginIndex, int endIndex)",
            "Extracts a portion of a string between the specified indices.",
            "beginIndex: the start position (inclusive)\nendIndex: the end position (exclusive)",
            "String text = \"Hello, World!\";\n" +
            "String extracted = text.substring(0, 5); // Returns \"Hello\"",
            "• Extracting substrings from user input\n" +
            "• Parsing fixed-format data\n" +
            "• Removing prefixes or suffixes from strings"
        ));
        
        methods.add(new MethodInfo(
            "String.split(String regex)",
            "Splits a string into an array of substrings based on the provided regular expression.",
            "regex: the delimiting regular expression",
            "String csvData = \"apple,banana,cherry\";\n" +
            "String[] fruits = csvData.split(\",\"); // Returns [\"apple\", \"banana\", \"cherry\"]",
            "• Parsing CSV or other delimited data\n" +
            "• Breaking text into words or tokens\n" +
            "• Processing multi-line text"
        ));
        
        methods.add(new MethodInfo(
            "String.format(String format, Object... args)",
            "Returns a formatted string using the specified format string and arguments.",
            "format: a format string\nargs: arguments referenced by the format specifiers in the format string",
            "String formatted = String.format(\"%.2f%%\", 75.8); // Returns \"75.80%\"\n" +
            "String message = String.format(\"Hello, %s! You are %d years old.\", \"Alice\", 30);",
            "• Creating user-friendly messages with dynamic values\n" +
            "• Formatting numbers with specific precision\n" +
            "• Building SQL queries with placeholders"
        ));
        
        methods.add(new MethodInfo(
            "String.toLowerCase()",
            "Converts all characters in a string to lowercase.",
            "No parameters",
            "String text = \"Hello, WORLD!\";\n" +
            "String lowerCase = text.toLowerCase(); // Returns \"hello, world!\"",
            "• Case-insensitive comparisons\n" +
            "• Normalizing user input\n" +
            "• Building search functionality"
        ));
        
        methods.add(new MethodInfo(
            "String.trim()",
            "Removes leading and trailing whitespace from a string.",
            "No parameters",
            "String text = \"   Hello, World!   \";\n" +
            "String trimmed = text.trim(); // Returns \"Hello, World!\"",
            "• Cleaning user input\n" +
            "• Processing data from external sources\n" +
            "• Validating input fields"
        ));

        methods.add(new MethodInfo(
            "String.replace(CharSequence target, CharSequence replacement)",
            "Replaces all occurrences of a specified target sequence with a replacement sequence.",
            "target: the sequence to be replaced\nreplacement: the replacement sequence",
            "String text = \"Hello, World!\";\n" +
            "String replaced = text.replace(\"Hello\", \"Hi\"); // Returns \"Hi, World!\"",
            "• Sanitizing user input\n" +
            "• Text preprocessing\n" +
            "• Implementing search and replace functionality"
        ));

        methods.add(new MethodInfo(
            "String.contains(CharSequence sequence)",
            "Checks if a string contains the specified character sequence.",
            "sequence: the character sequence to search for",
            "String text = \"Hello, World!\";\n" +
            "boolean contains = text.contains(\"World\"); // Returns true",
            "• Validating user input\n" +
            "• Implementing search functionality\n" +
            "• Conditional text processing"
        ));

        methods.add(new MethodInfo(
            "String.startsWith(String prefix)",
            "Tests if a string starts with the specified prefix.",
            "prefix: the prefix to be tested",
            "String text = \"Hello, World!\";\n" +
            "boolean starts = text.startsWith(\"Hello\"); // Returns true",
            "• Validating file extensions\n" +
            "• Processing command-line inputs\n" +
            "• Implementing autocomplete features"
        ));

        methods.add(new MethodInfo(
            "String.endsWith(String suffix)",
            "Tests if a string ends with the specified suffix.",
            "suffix: the suffix to be tested",
            "String filename = \"document.pdf\";\n" +
            "boolean isPdf = filename.endsWith(\".pdf\"); // Returns true",
            "• File type validation\n" +
            "• Domain name validation\n" +
            "• Parsing file extensions"
        ));

        methods.add(new MethodInfo(
            "String.toCharArray()",
            "Converts a string to a new character array.",
            "No parameters",
            "String text = \"Hello\";\n" +
            "char[] chars = text.toCharArray(); // Returns ['H', 'e', 'l', 'l', 'o']",
            "• Character-by-character processing\n" +
            "• Implementing encryption algorithms\n" +
            "• Creating custom string manipulation functions"
        ));

        methods.add(new MethodInfo(
            "String.valueOf(Object obj)",
            "Returns the string representation of the specified object.",
            "obj: the object to be converted to a string",
            "int number = 42;\n" +
            "String text = String.valueOf(number); // Returns \"42\"",
            "• Converting primitive values to strings\n" +
            "• Building string representations of complex objects\n" +
            "• Safe conversion from null values"
        ));

        methods.add(new MethodInfo(
            "String.isEmpty()",
            "Checks if a string is empty (length is 0).",
            "No parameters",
            "String text = \"\";\n" +
            "boolean empty = text.isEmpty(); // Returns true",
            "• Input validation\n" +
            "• Preventing operations on empty strings\n" +
            "• Conditional processing based on string content"
        ));

        methods.add(new MethodInfo(
            "String.charAt(int index)",
            "Returns the character at the specified index in a string.",
            "index: the index of the character to return",
            "String text = \"Hello\";\n" +
            "char c = text.charAt(0); // Returns 'H'",
            "• Accessing individual characters\n" +
            "• Implementing character-by-character algorithms\n" +
            "• Building character frequency analysis"
        ));

        methods.add(new MethodInfo(
            "String.replaceAll(String regex, String replacement)",
            "Replaces all substrings matching the given regular expression with the replacement string.",
            "regex: the regular expression to match\nreplacement: the string to replace matched substrings",
            "String text = \"Hello 123 World 456\";\n" +
            "String noDigits = text.replaceAll(\"\\\\d+\", \"\"); // Returns \"Hello  World \"",
            "• Advanced text cleaning\n" +
            "• Implementing regex-based search and replace\n" +
            "• Data normalization"
        ));

        methods.add(new MethodInfo(
            "String.join(CharSequence delimiter, CharSequence... elements)",
            "Joins multiple strings with a specified delimiter between each string.",
            "delimiter: the delimiter to be used between each element\nelements: the elements to join together",
            "String[] fruits = {\"apple\", \"banana\", \"cherry\"};\n" +
            "String joined = String.join(\", \", fruits); // Returns \"apple, banana, cherry\"",
            "• Creating CSV or delimited data\n" +
            "• Building readable lists from array elements\n" +
            "• Constructing SQL IN clauses"
        ));

        methods.add(new MethodInfo(
            "String.toUpperCase()",
            "Converts all characters in a string to uppercase.",
            "No parameters",
            "String text = \"Hello, world!\";\n" +
            "String upperCase = text.toUpperCase(); // Returns \"HELLO, WORLD!\"",
            "• Case normalization\n" +
            "• Creating display text\n" +
            "• Implementing case-insensitive comparisons"
        ));

        methods.add(new MethodInfo(
            "String.equals(Object anObject)",
            "Compares this string to the specified object for equality.",
            "anObject: the object to compare this String against",
            "String str1 = \"Hello\";\n" +
            "String str2 = \"Hello\";\n" +
            "boolean isEqual = str1.equals(str2); // Returns true",
            "• Comparing string values\n" +
            "• Authentication checks\n" +
            "• Data validation"
        ));

        methods.add(new MethodInfo(
            "String.equalsIgnoreCase(String anotherString)",
            "Compares this String to another String, ignoring case considerations.",
            "anotherString: the String to compare this String against",
            "String str1 = \"Hello\";\n" +
            "String str2 = \"hello\";\n" +
            "boolean isEqual = str1.equalsIgnoreCase(str2); // Returns true",
            "• Case-insensitive comparisons\n" +
            "• User input validation\n" +
            "• Search functionality"
        ));

        methods.add(new MethodInfo(
            "String.indexOf(String str)",
            "Returns the index within this string of the first occurrence of the specified substring.",
            "str: the substring to search for",
            "String text = \"Hello, World!\";\n" +
            "int index = text.indexOf(\"World\"); // Returns 7",
            "• Searching for substrings\n" +
            "• Text parsing\n" +
            "• Input validation"
        ));

        methods.add(new MethodInfo(
            "String.lastIndexOf(String str)",
            "Returns the index within this string of the last occurrence of the specified substring.",
            "str: the substring to search for",
            "String filePath = \"C:\\\\Documents\\\\Files\\\\document.pdf\";\n" +
            "int lastSlash = filePath.lastIndexOf(\"\\\\\"); // Returns 22",
            "• File path parsing\n" +
            "• Finding the last delimiter\n" +
            "• Text analysis"
        ));

        methods.add(new MethodInfo(
            "String.matches(String regex)",
            "Tells whether or not this string matches the given regular expression.",
            "regex: the regular expression to which this string is to be matched",
            "String email = \"user@example.com\";\n" +
            "boolean isValid = email.matches(\"^[\\\\w.-]+@([\\\\w-]+\\\\.)+[\\\\w-]{2,4}$\"); // Validates email format",
            "• Pattern validation\n" +
            "• Input format checking\n" +
            "• Text filtering"
        ));

        methods.add(new MethodInfo(
            "String.replaceFirst(String regex, String replacement)",
            "Replaces the first substring of this string that matches the given regular expression with the given replacement.",
            "regex: the regular expression to which this string is to be matched\nreplacement: the string to be substituted for the first match",
            "String text = \"This is a test. This is only a test.\";\n" +
            "String result = text.replaceFirst(\"is\", \"was\"); // Returns \"Thwas is a test. This is only a test.\"",
            "• Text editing\n" +
            "• Search and replace operations\n" +
            "• Content transformation"
        ));

        methods.add(new MethodInfo(
            "String.isBlank()",
            "Returns true if the string is empty or contains only white space characters.",
            "No parameters",
            "String text = \"   \";\n" +
            "boolean isBlank = text.isBlank(); // Returns true",
            "• Validating meaningful user input\n" +
            "• Checking for empty content\n" +
            "• Form validation"
        ));

        methods.add(new MethodInfo(
            "String.strip()",
            "Returns a string with all leading and trailing whitespace removed.",
            "No parameters",
            "String text = \"  Hello, World!  \";\n" +
            "String stripped = text.strip(); // Returns \"Hello, World!\"",
            "• Cleaning user input\n" +
            "• Text normalization\n" +
            "• Preparing data for processing"
        ));

        methods.add(new MethodInfo(
            "String.stripLeading()",
            "Returns a string with all leading whitespace removed.",
            "No parameters",
            "String text = \"  Hello, World!\";\n" +
            "String stripped = text.stripLeading(); // Returns \"Hello, World!\"",
            "• Formatting text alignment\n" +
            "• Cleaning prefix whitespace\n" +
            "• Text presentation"
        ));

        methods.add(new MethodInfo(
            "String.stripTrailing()",
            "Returns a string with all trailing whitespace removed.",
            "No parameters",
            "String text = \"Hello, World!  \";\n" +
            "String stripped = text.stripTrailing(); // Returns \"Hello, World!\"",
            "• Cleaning text for display\n" +
            "• Removing unnecessary spaces\n" +
            "• Data preparation"
        ));

        methods.add(new MethodInfo(
            "String.repeat(int count)",
            "Returns a string whose value is the concatenation of this string repeated count times.",
            "count: number of times to repeat",
            "String star = \"*\";\n" +
            "String line = star.repeat(10); // Returns \"**********\"",
            "• Creating separator lines\n" +
            "• Building padding or indentation\n" +
            "• Generating test data"
        ));

        methods.add(new MethodInfo(
            "String.compareToIgnoreCase(String str)",
            "Compares two strings lexicographically, ignoring case differences.",
            "str: the String to be compared",
            "String str1 = \"apple\";\n" +
            "String str2 = \"BANANA\";\n" +
            "int result = str1.compareToIgnoreCase(str2); // Returns negative value as \"apple\" comes before \"banana\"",
            "• Case-insensitive sorting\n" +
            "• Implementing custom comparators\n" +
            "• Alphabetical ordering"
        ));
        
        return methods;
    }
    
    private List<MethodInfo> getCollectionMethods() {
        List<MethodInfo> methods = new ArrayList<>();
        
        methods.add(new MethodInfo(
            "Collections.sort(List<T> list)",
            "Sorts the specified list into ascending order.",
            "list: the list to be sorted",
            "List<Integer> numbers = new ArrayList<>(Arrays.asList(5, 2, 8, 1));\n" +
            "Collections.sort(numbers); // numbers becomes [1, 2, 5, 8]",
            "• Sorting data for display\n" +
            "• Preparing data for binary search\n" +
            "• Implementing leaderboards or rankings"
        ));
        
        methods.add(new MethodInfo(
            "List.add(E element)",
            "Appends the specified element to the end of the list.",
            "element: element to be appended to the list",
            "List<String> fruits = new ArrayList<>();\n" +
            "fruits.add(\"apple\"); // fruits becomes [\"apple\"]\n" +
            "fruits.add(\"banana\"); // fruits becomes [\"apple\", \"banana\"]",
            "• Building dynamic collections of items\n" +
            "• Accumulating results during processing\n" +
            "• Managing user selections"
        ));
        
        methods.add(new MethodInfo(
            "Map.put(K key, V value)",
            "Associates the specified value with the specified key in the map.",
            "key: key with which the specified value is to be associated\nvalue: value to be associated with the specified key",
            "Map<String, Integer> scores = new HashMap<>();\n" +
            "scores.put(\"Alice\", 95); // Adds mapping Alice -> 95\n" +
            "scores.put(\"Bob\", 87); // Adds mapping Bob -> 87",
            "• Building lookup tables\n" +
            "• Implementing caches\n" +
            "• Counting occurrences of items"
        ));
        
        methods.add(new MethodInfo(
            "Set.contains(Object o)",
            "Returns true if this set contains the specified element.",
            "o: element whose presence in this set is to be tested",
            "Set<String> uniqueWords = new HashSet<>();\n" +
            "uniqueWords.add(\"hello\");\n" +
            "boolean contains = uniqueWords.contains(\"hello\"); // Returns true",
            "• Checking for duplicates\n" +
            "• Implementing filters\n" +
            "• Tracking visited items"
        ));
        
        methods.add(new MethodInfo(
            "Queue.poll()",
            "Retrieves and removes the head of the queue, or returns null if this queue is empty.",
            "No parameters",
            "Queue<String> messageQueue = new LinkedList<>();\n" +
            "messageQueue.add(\"First message\");\n" +
            "String message = messageQueue.poll(); // Returns \"First message\" and removes it",
            "• Implementing FIFO data structures\n" +
            "• Processing tasks in order\n" +
            "• Building job schedulers"
        ));

        methods.add(new MethodInfo(
            "Collections.reverse(List<?> list)",
            "Reverses the order of elements in the specified list.",
            "list: the list to be reversed",
            "List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4));\n" +
            "Collections.reverse(numbers); // numbers becomes [4, 3, 2, 1]",
            "• Displaying data in reverse chronological order\n" +
            "• Implementing undo functionality\n" +
            "• Reversing computational results"
        ));

        methods.add(new MethodInfo(
            "Collections.shuffle(List<?> list)",
            "Randomly permutes the specified list using a default source of randomness.",
            "list: the list to be shuffled",
            "List<String> cards = new ArrayList<>(Arrays.asList(\"Ace\", \"King\", \"Queen\", \"Jack\"));\n" +
            "Collections.shuffle(cards); // Randomizes the order of elements",
            "• Implementing card games\n" +
            "• Creating random quizzes or surveys\n" +
            "• Randomizing test data"
        ));

        methods.add(new MethodInfo(
            "List.remove(int index)",
            "Removes the element at the specified position in the list.",
            "index: the index of the element to be removed",
            "List<String> fruits = new ArrayList<>(Arrays.asList(\"apple\", \"banana\", \"cherry\"));\n" +
            "String removed = fruits.remove(1); // Returns \"banana\" and list becomes [\"apple\", \"cherry\"]",
            "• Implementing deletion functionality\n" +
            "• Managing dynamic lists\n" +
            "• Filtering elements during iteration"
        ));

        methods.add(new MethodInfo(
            "Map.get(Object key)",
            "Returns the value to which the specified key is mapped, or null if the map contains no mapping for the key.",
            "key: the key whose associated value is to be returned",
            "Map<String, Integer> scores = new HashMap<>();\n" +
            "scores.put(\"Alice\", 95);\n" +
            "Integer score = scores.get(\"Alice\"); // Returns 95",
            "• Lookup operations in dictionaries\n" +
            "• Accessing cached data\n" +
            "• Implementing configuration systems"
        ));

        methods.add(new MethodInfo(
            "Collection.isEmpty()",
            "Returns true if this collection contains no elements.",
            "No parameters",
            "List<String> messages = new ArrayList<>();\n" +
            "boolean empty = messages.isEmpty(); // Returns true if no elements",
            "• Validating input before processing\n" +
            "• Checking availability of resources\n" +
            "• Conditional UI rendering based on data presence"
        ));

        methods.add(new MethodInfo(
            "Collection.stream()",
            "Returns a sequential Stream with this collection as its source.",
            "No parameters",
            "List<String> names = Arrays.asList(\"Alice\", \"Bob\", \"Charlie\");\n" +
            "List<String> upperCaseNames = names.stream()\n" +
            "                                   .map(String::toUpperCase)\n" +
            "                                   .collect(Collectors.toList());",
            "• Functional-style operations on collections\n" +
            "• Implementing complex filtering and mapping\n" +
            "• Building data processing pipelines"
        ));

        methods.add(new MethodInfo(
            "Map.entrySet()",
            "Returns a Set view of the mappings contained in this map.",
            "No parameters",
            "Map<String, Integer> scores = new HashMap<>();\n" +
            "scores.put(\"Alice\", 95);\n" +
            "scores.put(\"Bob\", 87);\n" +
            "for (Map.Entry<String, Integer> entry : scores.entrySet()) {\n" +
            "    System.out.println(entry.getKey() + \": \" + entry.getValue());\n" +
            "}",
            "• Iterating through key-value pairs\n" +
            "• Accessing both keys and values simultaneously\n" +
            "• Implementing map transformations"
        ));

        methods.add(new MethodInfo(
            "Collections.binarySearch(List<? extends Comparable<? super T>> list, T key)",
            "Searches the specified list for the specified object using the binary search algorithm.",
            "list: the sorted list to be searched\nkey: the key to be searched for",
            "List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 3, 5, 7, 9));\n" +
            "int index = Collections.binarySearch(numbers, 5); // Returns 2",
            "• Efficiently finding elements in sorted lists\n" +
            "• Implementing search functionality\n" +
            "• Checking existence of elements in large datasets"
        ));

        methods.add(new MethodInfo(
            "List.subList(int fromIndex, int toIndex)",
            "Returns a view of the portion of this list between the specified fromIndex (inclusive) and toIndex (exclusive).",
            "fromIndex: low endpoint (inclusive) of the subList\ntoIndex: high endpoint (exclusive) of the subList",
            "List<String> words = new ArrayList<>(Arrays.asList(\"apple\", \"banana\", \"cherry\", \"date\"));\n" +
            "List<String> sublist = words.subList(1, 3); // Returns [\"banana\", \"cherry\"]",
            "• Implementing pagination\n" +
            "• Processing data in chunks\n" +
            "• Creating views of larger collections"
        ));

        methods.add(new MethodInfo(
            "Collections.max(Collection<? extends T> coll)",
            "Returns the maximum element of the given collection, according to the natural ordering of its elements.",
            "coll: the collection whose maximum element is to be determined",
            "List<Integer> numbers = Arrays.asList(3, 7, 1, 9, 4);\n" +
            "Integer max = Collections.max(numbers); // Returns 9",
            "• Finding highest scores or values\n" +
            "• Determining maximum ranges\n" +
            "• Implementing statistical functions"
        ));
        
        return methods;
    }
    
    private List<MethodInfo> getFileIOMethods() {
        List<MethodInfo> methods = new ArrayList<>();
        
        methods.add(new MethodInfo(
            "Files.readAllLines(Path path)",
            "Reads all lines from a file as a List of Strings.",
            "path: the path to the file",
            "Path filePath = Paths.get(\"data.txt\");\n" +
            "List<String> lines = Files.readAllLines(filePath);\n" +
            "for (String line : lines) {\n" +
            "    System.out.println(line);\n" +
            "}",
            "• Reading configuration files\n" +
            "• Processing log files\n" +
            "• Loading data for analysis"
        ));
        
        methods.add(new MethodInfo(
            "Files.write(Path path, Iterable<? extends CharSequence> lines)",
            "Writes lines of text to a file.",
            "path: the path to the file\nlines: the lines of text to write",
            "Path filePath = Paths.get(\"output.txt\");\n" +
            "List<String> lines = Arrays.asList(\"Line 1\", \"Line 2\", \"Line 3\");\n" +
            "Files.write(filePath, lines);",
            "• Saving application data\n" +
            "• Creating log files\n" +
            "• Exporting results"
        ));
        
        methods.add(new MethodInfo(
            "BufferedReader.readLine()",
            "Reads a line of text from the input stream.",
            "No parameters",
            "try (BufferedReader reader = new BufferedReader(new FileReader(\"data.txt\"))) {\n" +
            "    String line;\n" +
            "    while ((line = reader.readLine()) != null) {\n" +
            "        System.out.println(line);\n" +
            "    }\n" +
            "}",
            "• Processing files line by line\n" +
            "• Reading network responses\n" +
            "• Parsing structured text"
        ));
        
        methods.add(new MethodInfo(
            "File.listFiles()",
            "Returns an array of abstract pathnames denoting the files in the directory.",
            "No parameters",
            "File directory = new File(\"/path/to/directory\");\n" +
            "File[] files = directory.listFiles();\n" +
            "for (File file : files) {\n" +
            "    System.out.println(file.getName());\n" +
            "}",
            "• File system navigation\n" +
            "• Batch processing files\n" +
            "• Building file explorers"
        ));
        
        methods.add(new MethodInfo(
            "InputStream.read(byte[] b)",
            "Reads up to b.length bytes of data from this input stream into an array of bytes.",
            "b: the buffer into which the data is read",
            "try (FileInputStream fis = new FileInputStream(\"data.bin\")) {\n" +
            "    byte[] buffer = new byte[1024];\n" +
            "    int bytesRead;\n" +
            "    while ((bytesRead = fis.read(buffer)) != -1) {\n" +
            "        // Process buffer data\n" +
            "    }\n" +
            "}",
            "• Reading binary files\n" +
            "• Processing images or media\n" +
            "• Network communications"
        ));

        methods.add(new MethodInfo(
            "Files.createDirectory(Path dir)",
            "Creates a new directory at the specified path.",
            "dir: the directory to create",
            "Path dirPath = Paths.get(\"new_directory\");\n" +
            "Files.createDirectory(dirPath);",
            "• Setting up application data folders\n" +
            "• Creating organizational structures\n" +
            "• Preparing output directories for batch processing"
        ));

        methods.add(new MethodInfo(
            "Files.copy(Path source, Path target, CopyOption... options)",
            "Copies a file or directory from the source path to the target path.",
            "source: the path to the file to copy\ntarget: the path to the target file\noptions: options specifying how the copy should be done",
            "Path sourceFile = Paths.get(\"source.txt\");\n" +
            "Path targetFile = Paths.get(\"target.txt\");\n" +
            "Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);",
            "• Creating file backups\n" +
            "• Implementing file operations in applications\n" +
            "• Duplicating templates or configurations"
        ));

        methods.add(new MethodInfo(
            "Files.move(Path source, Path target, CopyOption... options)",
            "Moves or renames a file to a target file.",
            "source: the path to the file to move\ntarget: the path to the target file\noptions: options specifying how the move should be done",
            "Path sourceFile = Paths.get(\"old_name.txt\");\n" +
            "Path targetFile = Paths.get(\"new_name.txt\");\n" +
            "Files.move(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);",
            "• Renaming files\n" +
            "• Moving processed files to archive folders\n" +
            "• Implementing file organization features"
        ));

        methods.add(new MethodInfo(
            "Files.delete(Path path)",
            "Deletes a file or an empty directory at the specified path.",
            "path: the path to the file or directory to delete",
            "Path filePath = Paths.get(\"temporary.txt\");\n" +
            "Files.delete(filePath);",
            "• Cleaning up temporary files\n" +
            "• Implementing file deletion features\n" +
            "• Removing processed data"
        ));

        methods.add(new MethodInfo(
            "Files.exists(Path path, LinkOption... options)",
            "Tests whether a file exists at the specified path.",
            "path: the path to the file\noptions: options indicating how symbolic links are handled",
            "Path filePath = Paths.get(\"config.properties\");\n" +
            "boolean exists = Files.exists(filePath);\n" +
            "if (exists) {\n" +
            "    // Process the file\n" +
            "}",
            "• Validating file existence before operations\n" +
            "• Checking for configuration files\n" +
            "• Implementing file verification steps"
        ));

        methods.add(new MethodInfo(
            "Files.readAllBytes(Path path)",
            "Reads all the bytes from a file.",
            "path: the path to the file",
            "Path filePath = Paths.get(\"image.jpg\");\n" +
            "byte[] imageData = Files.readAllBytes(filePath);\n" +
            "// Process binary data",
            "• Loading binary files into memory\n" +
            "• Processing image or document data\n" +
            "• Working with encrypted content"
        ));

        methods.add(new MethodInfo(
            "FileWriter.write(String str)",
            "Writes a string to the file.",
            "str: String to be written",
            "try (FileWriter writer = new FileWriter(\"output.txt\")) {\n" +
            "    writer.write(\"Hello, World!\");\n" +
            "}",
            "• Creating text files\n" +
            "• Generating reports\n" +
            "• Implementing logging functionality"
        ));

        methods.add(new MethodInfo(
            "Files.createTempFile(Path dir, String prefix, String suffix, FileAttribute<?>... attrs)",
            "Creates a new empty temporary file in the specified directory.",
            "dir: the directory in which to create the file\nprefix: the prefix string to be used in generating the file's name\nsuffix: the suffix string to be used in generating the file's name",
            "Path tempDir = Paths.get(\"temp\");\n" +
            "Path tempFile = Files.createTempFile(tempDir, \"data_\", \".tmp\");\n" +
            "// Use the temporary file",
            "• Creating scratch files for processing\n" +
            "• Implementing file caching mechanisms\n" +
            "• Storing intermediate computation results"
        ));

        methods.add(new MethodInfo(
            "Files.walkFileTree(Path start, FileVisitor<? super Path> visitor)",
            "Walks a file tree rooted at a given starting file.",
            "start: the starting file\nvisitor: the file visitor to invoke for each file",
            "Path rootDir = Paths.get(\"/path/to/root\");\n" +
            "Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {\n" +
            "    @Override\n" +
            "    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {\n" +
            "        System.out.println(file);\n" +
            "        return FileVisitResult.CONTINUE;\n" +
            "    }\n" +
            "});",
            "• Recursive directory scanning\n" +
            "• Building file indexes\n" +
            "• Implementing search functionality"
        ));

        methods.add(new MethodInfo(
            "Scanner.nextLine()",
            "Advances this scanner past the current line and returns the input that was skipped.",
            "No parameters",
            "try (Scanner scanner = new Scanner(new File(\"data.txt\"))) {\n" +
            "    while (scanner.hasNextLine()) {\n" +
            "        String line = scanner.nextLine();\n" +
            "        System.out.println(line);\n" +
            "    }\n" +
            "}",
            "• Reading formatted text files\n" +
            "• Processing user input\n" +
            "• Parsing structured data"
        ));

        methods.add(new MethodInfo(
            "PrintWriter.println(String x)",
            "Prints a String and then terminates the line.",
            "x: The String to be printed",
            "try (PrintWriter writer = new PrintWriter(\"output.txt\")) {\n" +
            "    writer.println(\"Line 1\");\n" +
            "    writer.println(\"Line 2\");\n" +
            "}",
            "• Creating formatted output files\n" +
            "• Generating reports with line breaks\n" +
            "• Logging with proper formatting"
        ));

        methods.add(new MethodInfo(
            "ObjectOutputStream.writeObject(Object obj)",
            "Writes the specified object to the ObjectOutputStream.",
            "obj: the object to be written",
            "try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(\"object.ser\"))) {\n" +
            "    Person person = new Person(\"John\", 30);\n" +
            "    out.writeObject(person);\n" +
            "}",
            "• Serializing Java objects\n" +
            "• Implementing data persistence\n" +
            "• Saving application state"
        ));

        methods.add(new MethodInfo(
            "ObjectInputStream.readObject()",
            "Reads an object from the ObjectInputStream.",
            "No parameters",
            "try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(\"object.ser\"))) {\n" +
            "    Person person = (Person) in.readObject();\n" +
            "    System.out.println(person.getName());\n" +
            "}",
            "• Deserializing Java objects\n" +
            "• Loading saved application state\n" +
            "• Restoring persisted data"
        ));

        methods.add(new MethodInfo(
            "Files.newBufferedReader(Path path, Charset charset)",
            "Opens a file for reading, returning a BufferedReader.",
            "path: the path to the file\ncharset: the charset to use for decoding",
            "Path filePath = Paths.get(\"data.txt\");\n" +
            "try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {\n" +
            "    String line;\n" +
            "    while ((line = reader.readLine()) != null) {\n" +
            "        System.out.println(line);\n" +
            "    }\n" +
            "}",
            "• Reading text files with specific encodings\n" +
            "• Processing international text data\n" +
            "• Building text parsers"
        ));

        methods.add(new MethodInfo(
            "Files.newBufferedWriter(Path path, Charset charset, OpenOption... options)",
            "Opens or creates a file for writing, returning a BufferedWriter.",
            "path: the path to the file\ncharset: the charset to use for encoding\noptions: options specifying how the file is opened",
            "Path filePath = Paths.get(\"output.txt\");\n" +
            "try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {\n" +
            "    writer.write(\"Hello, World!\");\n" +
            "    writer.newLine();\n" +
            "    writer.write(\"Another line\");\n" +
            "}",
            "• Creating text files with specific encodings\n" +
            "• Generating international text content\n" +
            "• Building text exporters"
        ));
        
        return methods;
    }
    
    private List<MethodInfo> getMathMethods() {
        List<MethodInfo> methods = new ArrayList<>();
        
        methods.add(new MethodInfo(
            "Math.sqrt(double x)",
            "Returns the square root of a number.",
            "x: the number to find the square root of",
            "double root = Math.sqrt(25.0); // Returns 5.0",
            "• Calculating distances (Pythagorean theorem)\n" +
            "• Statistical operations\n" +
            "• Graphics and game development"
        ));
        
        methods.add(new MethodInfo(
            "Math.pow(double a, double b)",
            "Returns the value of the first argument raised to the power of the second argument.",
            "a: the base\nb: the exponent",
            "double result = Math.pow(2.0, 3.0); // Returns 8.0",
            "• Exponential growth calculations\n" +
            "• Financial calculations (compound interest)\n" +
            "• Scientific computations"
        ));
        
        methods.add(new MethodInfo(
            "Math.random()",
            "Returns a double value with a positive sign, greater than or equal to 0.0 and less than 1.0.",
            "No parameters",
            "double randomValue = Math.random(); // Returns a value between 0.0 and 1.0\n" +
            "int randomInt = (int)(Math.random() * 100); // Returns a value between 0 and 99",
            "• Generating random numbers\n" +
            "• Simulations and games\n" +
            "• Statistical sampling"
        ));
        
        methods.add(new MethodInfo(
            "Math.round(double a)",
            "Returns the closest long or int to the argument.",
            "a: a floating-point value to be rounded",
            "long rounded = Math.round(3.75); // Returns 4",
            "• Rounding calculations for display\n" +
            "• Financial calculations\n" +
            "• User interface positioning"
        ));
        
        methods.add(new MethodInfo(
            "Math.max(double a, double b)",
            "Returns the greater of two values.",
            "a: the first value\nb: the second value",
            "double maximum = Math.max(5.7, 9.2); // Returns 9.2",
            "• Finding extreme values\n" +
            "• Constraining values within bounds\n" +
            "• Comparing measurements"
        ));
                
        methods.add(new MethodInfo(
            "Math.min(double a, double b)",
            "Returns the smaller of two values.",
            "a: the first value\nb: the second value",
            "double minimum = Math.min(5.7, 9.2); // Returns 5.7",
            "• Constraining values to upper limits\n" +
            "• Finding the lowest score or measurement\n" +
            "• Implementing clipping algorithms"
        ));

        methods.add(new MethodInfo(
            "Math.abs(double a)",
            "Returns the absolute value of a number.",
            "a: the argument whose absolute value is to be determined",
            "double absolute = Math.abs(-10.5); // Returns 10.5",
            "• Calculating distances or differences\n" +
            "• Error measurement\n" +
            "• Signal processing"
        ));

        methods.add(new MethodInfo(
            "Math.sin(double a)",
            "Returns the trigonometric sine of an angle in radians.",
            "a: an angle, in radians",
            "double result = Math.sin(Math.PI / 2); // Returns 1.0",
            "• Wave simulations\n" +
            "• Graphics and animations\n" +
            "• Physics calculations"
        ));

        methods.add(new MethodInfo(
            "Math.cos(double a)",
            "Returns the trigonometric cosine of an angle in radians.",
            "a: an angle, in radians",
            "double result = Math.cos(Math.PI); // Returns -1.0",
            "• Circular motion calculations\n" +
            "• Game development (rotations)\n" +
            "• Coordinate transformations"
        ));

        methods.add(new MethodInfo(
            "Math.tan(double a)",
            "Returns the trigonometric tangent of an angle in radians.",
            "a: an angle, in radians",
            "double result = Math.tan(Math.PI / 4); // Returns approximately the value 1.0",
            "• Slope calculations\n" +
            "• Perspective projections\n" +
            "• Engineering applications"
        ));

        methods.add(new MethodInfo(
            "Math.floor(double a)",
            "Returns the largest integer less than or equal to the argument.",
            "a: a value",
            "double result = Math.floor(3.7); // Returns 3.0",
            "• Integer division\n" +
            "• Pagination calculations\n" +
            "• Rounding down for financial calculations"
        ));

        methods.add(new MethodInfo(
            "Math.ceil(double a)",
            "Returns the smallest integer greater than or equal to the argument.",
            "a: a value",
            "double result = Math.ceil(3.1); // Returns 4.0",
            "• Rounding up for resource allocation\n" +
            "• Calculating minimum containers needed\n" +
            "• Implementing ceiling functions"
        ));

        methods.add(new MethodInfo(
            "Math.log(double a)",
            "Returns the natural logarithm (base e) of a double value.",
            "a: a value",
            "double result = Math.log(Math.E); // Returns 1.0",
            "• Exponential growth/decay analysis\n" +
            "• Information theory calculations\n" +
            "• Scientific computations"
        ));

        methods.add(new MethodInfo(
            "Math.log10(double a)",
            "Returns the base 10 logarithm of a double value.",
            "a: a value",
            "double result = Math.log10(100.0); // Returns 2.0",
            "• Engineering calculations\n" +
            "• Decibel calculations\n" +
            "• Scale conversions"
        ));

        methods.add(new MethodInfo(
            "Math.toDegrees(double angrad)",
            "Converts an angle measured in radians to degrees.",
            "angrad: an angle, in radians",
            "double degrees = Math.toDegrees(Math.PI); // Returns 180.0",
            "• Converting between angle units\n" +
            "• User interface for angular inputs\n" +
            "• Geographical calculations"
        ));

        methods.add(new MethodInfo(
            "Math.toRadians(double angdeg)",
            "Converts an angle measured in degrees to radians.",
            "angdeg: an angle, in degrees",
            "double radians = Math.toRadians(90.0); // Returns approximately the value of PI/2",
            "• Preparing angles for trigonometric functions\n" +
            "• Physics simulations\n" +
            "• 3D graphics computations"
        ));

        methods.add(new MethodInfo(
            "Math.atan2(double y, double x)",
            "Returns the angle in radians between the positive x-axis and the point (x, y).",
            "y: the ordinate coordinate\nx: the abscissa coordinate",
            "double angle = Math.atan2(1.0, 1.0); // Returns approximately the value of PI/4",
            "• Computing angles between points\n" +
            "• Navigation and bearing calculations\n" +
            "• Game AI and movement"
        ));

        methods.add(new MethodInfo(
            "Math.hypot(double x, double y)",
            "Returns sqrt(x^2 + y^2) without intermediate overflow or underflow.",
            "x: the first value\ny: the second value",
            "double distance = Math.hypot(3.0, 4.0); // Returns 5.0",
            "• Calculating Euclidean distances\n" +
            "• Vector magnitude computation\n" +
            "• Pythagorean theorem applications"
        ));

        methods.add(new MethodInfo(
            "Math.exp(double a)",
            "Returns Euler's number e raised to the power of a double value.",
            "a: the exponent to raise e to",
            "double result = Math.exp(1.0); // Returns the value of e",
            "• Exponential growth modeling\n" +
            "• Scientific and financial calculations\n" +
            "• Statistical analysis"
        ));

        methods.add(new MethodInfo(
            "Math.cbrt(double a)",
            "Returns the cube root of a double value.",
            "a: a value",
            "double result = Math.cbrt(27.0); // Returns 3.0",
            "• Volume calculations\n" +
            "• 3D scaling operations\n" +
            "• Scientific computations"
        ));

        methods.add(new MethodInfo(
            "Math.signum(double d)",
            "Returns the signum function of the argument: zero if the argument is zero, 1.0 if the argument is greater than zero, -1.0 if the argument is less than zero.",
            "d: the floating-point value whose signum is to be returned",
            "double sign = Math.signum(-42.0); // Returns -1.0",
            "• Determining direction of movement\n" +
            "• Normalizing values\n" +
            "• Implementing comparison functions"
        ));

        methods.add(new MethodInfo(
            "StrictMath.scalb(double d, int scaleFactor)",
            "Returns d × 2^scaleFactor rounded as if performed by a single correctly rounded floating-point multiply.",
            "d: number to be scaled\nscaleFactor: scale factor",
            "double result = StrictMath.scalb(1.0, 3); // Returns 8.0 (1.0 × 2^3)",
            "• Bit manipulation\n" +
            "• Implementing floating-point algorithms\n" +
            "• Scientific calculations requiring precise scaling"
        ));

        methods.add(new MethodInfo(
            "Math.IEEEremainder(double f1, double f2)",
            "Computes the remainder operation on two arguments as prescribed by the IEEE 754 standard.",
            "f1: the dividend\nf2: the divisor",
            "double remainder = Math.IEEEremainder(5.0, 2.0); // Returns 1.0",
            "• Precise modular arithmetic\n" +
            "• Angle normalization\n" +
            "• Implementing numeric algorithms"
        ));

        methods.add(new MethodInfo(
            "Math.addExact(int x, int y)",
            "Returns the sum of its arguments, throwing an exception if the result overflows an int.",
            "x: the first value\ny: the second value",
            "int sum = Math.addExact(Integer.MAX_VALUE - 1, 1); // Returns Integer.MAX_VALUE\n" +
            "// Math.addExact(Integer.MAX_VALUE, 1); // Would throw ArithmeticException",
            "• Safe integer arithmetic\n" +
            "• Financial calculations requiring overflow protection\n" +
            "• Security-critical numeric operations"
        ));

        methods.add(new MethodInfo(
            "Math.multiplyExact(long x, long y)",
            "Returns the product of the arguments, throwing an exception if the result overflows a long.",
            "x: the first value\ny: the second value",
            "long product = Math.multiplyExact(10L, 20L); // Returns 200L\n" +
            "// Math.multiplyExact(Long.MAX_VALUE, 2L); // Would throw ArithmeticException",
            "• Secure multiplication operations\n" +
            "• Financial calculations with large numbers\n" +
            "• Safe scaling operations"
        ));

        return methods;
    }
    
    private List<MethodInfo> getStreamMethods() {
        List<MethodInfo> methods = new ArrayList<>();
        
        methods.add(new MethodInfo(
            "Stream.filter(Predicate<T> predicate)",
            "Returns a stream consisting of elements that match the given predicate.",
            "predicate: a non-interfering, stateless predicate to apply to each element",
            "List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);\n" +
            "List<Integer> evens = numbers.stream()\n" +
            "    .filter(n -> n % 2 == 0)\n" +
            "    .collect(Collectors.toList()); // Returns [2, 4, 6]",
            "• Filtering collections based on criteria\n" +
            "• Implementing search functionality\n" +
            "• Data validation"
        ));
        
        methods.add(new MethodInfo(
            "Stream.map(Function<T, R> mapper)",
            "Returns a stream consisting of the results of applying the given function to the elements of this stream.",
            "mapper: a non-interfering, stateless function to apply to each element",
            "List<String> names = Arrays.asList(\"Alice\", \"Bob\", \"Charlie\");\n" +
            "List<Integer> nameLengths = names.stream()\n" +
            "    .map(String::length)\n" +
            "    .collect(Collectors.toList()); // Returns [5, 3, 7]",
            "• Transforming data\n" +
            "• Extracting properties from objects\n" +
            "• Format conversion"
        ));
        
        methods.add(new MethodInfo(
            "Stream.collect(Collector<T, A, R> collector)",
            "Performs a mutable reduction operation on the elements of this stream using a Collector.",
            "collector: the Collector describing the reduction",
            "List<String> fruits = Arrays.asList(\"apple\", \"banana\", \"cherry\");\n" +
            "String result = fruits.stream()\n" +
            "    .collect(Collectors.joining(\", \")); // Returns \"apple, banana, cherry\"",
            "• Converting streams back to collections\n" +
            "• Aggregating results\n" +
            "• Building complex data structures"
        ));
        
        methods.add(new MethodInfo(
            "Stream.reduce(T identity, BinaryOperator<T> accumulator)",
            "Performs a reduction on the elements of this stream, using the provided identity value and an associative accumulation function.",
            "identity: the identity value for the accumulating function\naccumulator: an associative, non-interfering, stateless function for combining two values",
            "List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);\n" +
            "int sum = numbers.stream()\n" +
            "    .reduce(0, (a, b) -> a + b); // Returns 15",
            "• Summing or multiplying values\n" +
            "• Combining text\n" +
            "• Building composite objects"
        ));
        
        methods.add(new MethodInfo(
            "Stream.sorted(Comparator<T> comparator)",
            "Returns a stream consisting of the elements of this stream, sorted according to the provided Comparator.",
            "comparator: a non-interfering, stateless Comparator to be used to compare stream elements",
            "List<String> names = Arrays.asList(\"Charlie\", \"Alice\", \"Bob\");\n" +
            "List<String> sorted = names.stream()\n" +
            "    .sorted()\n" +
            "    .collect(Collectors.toList()); // Returns [\"Alice\", \"Bob\", \"Charlie\"]",
            "• Arranging data in a specific order\n" +
            "• Preparing data for presentation\n" +
            "• Finding min/max values"
        ));

        methods.add(new MethodInfo(
            "Stream.forEach(Consumer<T> action)",
            "Performs an action for each element of this stream.",
            "action: a non-interfering action to perform on the elements",
            "List<String> names = Arrays.asList(\"Alice\", \"Bob\", \"Charlie\");\n" +
            "names.stream()\n" +
            "    .forEach(name -> System.out.println(\"Hello, \" + name)); // Prints greeting for each name",
            "• Processing each element in a collection\n" +
            "• Applying side effects to stream elements\n" +
            "• Debugging stream operations"
        ));

        methods.add(new MethodInfo(
            "Stream.distinct()",
            "Returns a stream consisting of the distinct elements of this stream.",
            "No parameters",
            "List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 5, 5);\n" +
            "List<Integer> distinct = numbers.stream()\n" +
            "    .distinct()\n" +
            "    .collect(Collectors.toList()); // Returns [1, 2, 3, 4, 5]",
            "• Removing duplicates from collections\n" +
            "• Ensuring uniqueness in data processing\n" +
            "• Cleaning input data"
        ));

        methods.add(new MethodInfo(
            "Stream.flatMap(Function<T, Stream<R>> mapper)",
            "Returns a stream consisting of the results of replacing each element of this stream with the contents of a mapped stream.",
            "mapper: a function that maps an element to a stream of new elements",
            "List<List<Integer>> nestedList = Arrays.asList(\n" +
            "    Arrays.asList(1, 2), Arrays.asList(3, 4, 5), Arrays.asList(6));\n" +
            "List<Integer> flatList = nestedList.stream()\n" +
            "    .flatMap(Collection::stream)\n" +
            "    .collect(Collectors.toList()); // Returns [1, 2, 3, 4, 5, 6]",
            "• Flattening nested collections\n" +
            "• Processing multiple related streams\n" +
            "• One-to-many transformations"
        ));

        methods.add(new MethodInfo(
            "Stream.limit(long maxSize)",
            "Returns a stream consisting of the elements of this stream, truncated to be no longer than maxSize in length.",
            "maxSize: the number of elements the stream should be limited to",
            "List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);\n" +
            "List<Integer> limited = numbers.stream()\n" +
            "    .limit(5)\n" +
            "    .collect(Collectors.toList()); // Returns [1, 2, 3, 4, 5]",
            "• Implementing pagination\n" +
            "• Creating data samples\n" +
            "• Limiting resource consumption"
        ));

        methods.add(new MethodInfo(
            "Stream.skip(long n)",
            "Returns a stream consisting of the remaining elements of this stream after discarding the first n elements.",
            "n: the number of leading elements to skip",
            "List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);\n" +
            "List<Integer> skipped = numbers.stream()\n" +
            "    .skip(5)\n" +
            "    .collect(Collectors.toList()); // Returns [6, 7, 8, 9, 10]",
            "• Implementing offset in pagination\n" +
            "• Skipping header rows in data processing\n" +
            "• Splitting data into chunks"
        ));

        methods.add(new MethodInfo(
            "Stream.anyMatch(Predicate<T> predicate)",
            "Returns whether any elements of this stream match the provided predicate.",
            "predicate: a non-interfering, stateless predicate to apply to elements of this stream",
            "List<String> names = Arrays.asList(\"Alice\", \"Bob\", \"Charlie\");\n" +
            "boolean hasNameWithA = names.stream()\n" +
            "    .anyMatch(name -> name.startsWith(\"A\")); // Returns true",
            "• Checking if at least one element meets a condition\n" +
            "• Implementing search functionality\n" +
            "• Validating collection contents"
        ));

        methods.add(new MethodInfo(
            "Stream.allMatch(Predicate<T> predicate)",
            "Returns whether all elements of this stream match the provided predicate.",
            "predicate: a non-interfering, stateless predicate to apply to elements of this stream",
            "List<Integer> numbers = Arrays.asList(2, 4, 6, 8, 10);\n" +
            "boolean allEven = numbers.stream()\n" +
            "    .allMatch(n -> n % 2 == 0); // Returns true",
            "• Validating all elements meet a condition\n" +
            "• Checking batch data consistency\n" +
            "• Implementing filters for collections"
        ));

        methods.add(new MethodInfo(
            "Stream.findFirst()",
            "Returns an Optional describing the first element of this stream, or an empty Optional if the stream is empty.",
            "No parameters",
            "List<String> names = Arrays.asList(\"Alice\", \"Bob\", \"Charlie\");\n" +
            "Optional<String> firstName = names.stream()\n" +
            "    .filter(name -> name.length() > 4)\n" +
            "    .findFirst(); // Returns Optional[\"Alice\"]",
            "• Retrieving the first matching element\n" +
            "• Implementing deterministic selection\n" +
            "• Working with potentially empty results"
        ));

        methods.add(new MethodInfo(
            "Stream.count()",
            "Returns the count of elements in this stream.",
            "No parameters",
            "List<String> names = Arrays.asList(\"Alice\", \"Bob\", \"Charlie\");\n" +
            "long count = names.stream()\n" +
            "    .filter(name -> name.length() > 3)\n" +
            "    .count(); // Returns 3",
            "• Counting elements that meet criteria\n" +
            "• Measuring collection size after filtering\n" +
            "• Implementing statistics functionality"
        ));

        methods.add(new MethodInfo(
            "Collectors.groupingBy(Function<T, K> classifier)",
            "Returns a Collector implementing a group-by operation on input elements, grouping elements according to a classification function.",
            "classifier: the function that maps input elements to a key",
            "List<String> names = Arrays.asList(\"Alice\", \"Bob\", \"Charlie\", \"Andrew\", \"Beth\");\n" +
            "Map<Character, List<String>> namesByFirstLetter = names.stream()\n" +
            "    .collect(Collectors.groupingBy(name -> name.charAt(0)));\n" +
            "// Returns {A=[Alice, Andrew], B=[Bob, Beth], C=[Charlie]}",
            "• Grouping data by common properties\n" +
            "• Building hierarchical data structures\n" +
            "• Implementing data aggregation"
        ));

        return methods;
    }

    private List<MethodInfo> getDecIntMethods() {
        List<MethodInfo> methods = new ArrayList<>();

        methods.add(new MethodInfo(
            "BigDecimal.add(BigDecimal augend)",
            "Returns a BigDecimal whose value is (this + augend).",
            "augend: the value to be added to this BigDecimal",
            "BigDecimal price = new BigDecimal(\"99.95\");\n" +
            "BigDecimal tax = new BigDecimal(\"8.25\");\n" +
            "BigDecimal total = price.add(tax); // Returns 108.20",
            "• Financial calculations\n" +
            "• Currency operations\n" +
            "• Precise addition without floating-point errors"
        ));
        
        methods.add(new MethodInfo(
            "BigDecimal.setScale(int newScale, RoundingMode roundingMode)",
            "Returns a BigDecimal with the specified scale and rounding mode.",
            "newScale: the scale of the result\nroundingMode: the rounding mode to apply when scaling",
            "BigDecimal value = new BigDecimal(\"10.5678\");\n" +
            "BigDecimal rounded = value.setScale(2, RoundingMode.HALF_UP); // Returns 10.57",
            "• Rounding monetary values\n" +
            "• Formatting decimal numbers for display\n" +
            "• Meeting precision requirements in financial operations"
        ));
        
        methods.add(new MethodInfo(
            "BigDecimal.compareTo(BigDecimal val)",
            "Compares this BigDecimal with the specified BigDecimal.",
            "val: BigDecimal to which this BigDecimal is to be compared",
            "BigDecimal value1 = new BigDecimal(\"100.00\");\n" +
            "BigDecimal value2 = new BigDecimal(\"100.0\");\n" +
            "int result = value1.compareTo(value2); // Returns 0 (equal in value)",
            "• Comparing monetary values\n" +
            "• Implementing sorting for precise numbers\n" +
            "• Threshold checking in financial applications"
        ));
        
        methods.add(new MethodInfo(
            "BigDecimal.multiply(BigDecimal multiplicand)",
            "Returns a BigDecimal whose value is (this × multiplicand).",
            "multiplicand: the value to be multiplied by this BigDecimal",
            "BigDecimal price = new BigDecimal(\"19.99\");\n" +
            "BigDecimal quantity = new BigDecimal(\"3\");\n" +
            "BigDecimal subtotal = price.multiply(quantity); // Returns 59.97",
            "• Calculating product totals\n" +
            "• Interest calculations\n" +
            "• Scaling values with precise multiplication"
        ));
        
        methods.add(new MethodInfo(
            "BigDecimal.divide(BigDecimal divisor, int scale, RoundingMode roundingMode)",
            "Returns a BigDecimal whose value is (this / divisor), with rounding.",
            "divisor: value by which this BigDecimal is to be divided\nscale: scale of the result\nroundingMode: rounding mode to apply",
            "BigDecimal amount = new BigDecimal(\"100.00\");\n" +
            "BigDecimal people = new BigDecimal(\"3\");\n" +
            "BigDecimal share = amount.divide(people, 2, RoundingMode.HALF_UP); // Returns 33.33",
            "• Splitting bills or payments\n" +
            "• Calculating rates or ratios\n" +
            "• Performing division with controlled precision"
        ));
        
        methods.add(new MethodInfo(
            "BigInteger.isProbablePrime(int certainty)",
            "Returns true if this BigInteger is probably prime, false if it's definitely composite.",
            "certainty: a measure of the uncertainty that the caller is willing to tolerate",
            "BigInteger number = new BigInteger(\"997\");\n" +
            "boolean isPrime = number.isProbablePrime(100); // High probability check for primality",
            "• Cryptographic key generation\n" +
            "• Number theory applications\n" +
            "• Generating large prime numbers"
        ));
        
        methods.add(new MethodInfo(
            "BigDecimal.movePointLeft(int n)",
            "Returns a BigDecimal whose value is equivalent to this one with the decimal point moved left n positions.",
            "n: number of positions to move the decimal point",
            "BigDecimal value = new BigDecimal(\"123.45\");\n" +
            "BigDecimal scaled = value.movePointLeft(2); // Returns 1.2345",
            "• Converting between different units\n" +
            "• Applying percentage calculations\n" +
            "• Scaling values by powers of 10"
        ));
        
        methods.add(new MethodInfo(
            "BigInteger.gcd(BigInteger val)",
            "Returns a BigInteger whose value is the greatest common divisor of abs(this) and abs(val).",
            "val: value with which the GCD is to be computed",
            "BigInteger num1 = new BigInteger(\"54\");\n" +
            "BigInteger num2 = new BigInteger(\"24\");\n" +
            "BigInteger result = num1.gcd(num2); // Returns 6",
            "• Number theory calculations\n" +
            "• Fraction simplification\n" +
            "• Cryptographic algorithms"
        ));
        
        methods.add(new MethodInfo(
            "BigDecimal.stripTrailingZeros()",
            "Returns a BigDecimal which is numerically equal to this one but with trailing zeros removed.",
            "No parameters",
            "BigDecimal value = new BigDecimal(\"100.000\");\n" +
            "BigDecimal stripped = value.stripTrailingZeros(); // Returns 1E+2 (100)",
            "• Normalizing decimal representations\n" +
            "• Simplifying numeric display\n" +
            "• Comparing values with different scales"
        ));
        
        methods.add(new MethodInfo(
            "BigInteger.modPow(BigInteger exponent, BigInteger m)",
            "Returns a BigInteger whose value is (this^exponent mod m).",
            "exponent: the exponent\nm: the modulus",
            "BigInteger base = new BigInteger(\"7\");\n" +
            "BigInteger exp = new BigInteger(\"3\");\n" +
            "BigInteger mod = new BigInteger(\"13\");\n" +
            "BigInteger result = base.modPow(exp, mod); // Returns 5 (7^3 mod 13 = 343 mod 13 = 5)",
            "• Cryptographic calculations\n" +
            "• RSA encryption implementation\n" +
            "• Efficient large number operations"
        ));


        return methods;

    }

    private List<MethodInfo> getFormatterMethods() {
        List<MethodInfo> methods = new ArrayList<>();

        methods.add(new MethodInfo(
            "DateTimeFormatter.ofPattern(String pattern)",
            "Creates a formatter using the specified pattern.",
            "pattern: the pattern to use, not null",
            "DateTimeFormatter formatter = DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\");\n" +
            "LocalDateTime now = LocalDateTime.now();\n" +
            "String formatted = formatter.format(now); // Returns something like \"2023-05-15 14:30:25\"",
            "• Formatting dates and times for display\n" +
            "• Converting dates to specific string formats\n" +
            "• Creating standardized timestamp representations"
        ));
        
        methods.add(new MethodInfo(
            "NumberFormat.getCurrencyInstance(Locale locale)",
            "Returns a currency format for the specified locale.",
            "locale: the locale for which a currency format is desired",
            "NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);\n" +
            "String formatted = currencyFormatter.format(1234.56); // Returns \"$1,234.56\"",
            "• Formatting monetary values\n" +
            "• Displaying prices with appropriate currency symbols\n" +
            "• Creating localized financial reports"
        ));
        
        methods.add(new MethodInfo(
            "DecimalFormat.applyPattern(String pattern)",
            "Apply the given pattern to this Format object.",
            "pattern: a new pattern to be applied",
            "DecimalFormat df = new DecimalFormat();\n" +
            "df.applyPattern(\"#,###.##\");\n" +
            "String formatted = df.format(12345.67); // Returns \"12,345.67\"",
            "• Customizing number display formats\n" +
            "• Creating different numeric representations dynamically\n" +
            "• Implementing user-configurable number formatting"
        ));
        
        methods.add(new MethodInfo(
            "DateTimeFormatter.format(TemporalAccessor temporal)",
            "Formats a date-time object using this formatter.",
            "temporal: the temporal object to format, not null",
            "DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;\n" +
            "LocalDate date = LocalDate.of(2023, 5, 15);\n" +
            "String formatted = formatter.format(date); // Returns \"2023-05-15\"",
            "• Converting date objects to strings\n" +
            "• Generating date representations for APIs\n" +
            "• Creating standardized date outputs"
        ));
        
        methods.add(new MethodInfo(
            "NumberFormat.setMaximumFractionDigits(int newValue)",
            "Sets the maximum number of digits allowed in the fraction portion of a number.",
            "newValue: the maximum number of fraction digits to be shown",
            "NumberFormat nf = NumberFormat.getInstance();\n" +
            "nf.setMaximumFractionDigits(2);\n" +
            "String formatted = nf.format(123.4567); // Returns \"123.46\"",
            "• Controlling decimal precision in output\n" +
            "• Rounding numbers for display\n" +
            "• Creating consistent numeric presentations"
        ));
        
        methods.add(new MethodInfo(
            "MessageFormat.format(String pattern, Object... arguments)",
            "Creates a MessageFormat with the given pattern and formats the given arguments.",
            "pattern: the pattern for this message format\narguments: the objects to format",
            "String pattern = \"At {1,time} on {1,date}, there was {2} on planet {0}.\";\n" +
            "String formatted = MessageFormat.format(pattern, \"Earth\", new Date(), \"a disturbance in the force\");\n" +
            "// Returns something like \"At 12:30:00 PM on May 15, 2023, there was a disturbance in the force on planet Earth.\"",
            "• Creating complex internationalized messages\n" +
            "• Building dynamic user notifications\n" +
            "• Formatting messages with multiple variables"
        ));
        
        methods.add(new MethodInfo(
            "DateTimeFormatter.parse(CharSequence text)",
            "Parses the text using this formatter.",
            "text: the text to parse, not null",
            "DateTimeFormatter formatter = DateTimeFormatter.ofPattern(\"yyyy-MM-dd\");\n" +
            "TemporalAccessor temporal = formatter.parse(\"2023-05-15\");\n" +
            "LocalDate date = LocalDate.from(temporal); // Converts the parsed result to a LocalDate",
            "• Converting string inputs to date objects\n" +
            "• Parsing user-entered dates\n" +
            "• Processing date information from external sources"
        ));
        
        methods.add(new MethodInfo(
            "String.format(String format, Object... args)",
            "Returns a formatted string using the specified format string and arguments.",
            "format: a format string\nargs: arguments referenced by the format specifiers in the format string",
            "String formatted = String.format(\"Hello, %s! Your balance is $%.2f\", \"John\", 125.8675);\n" +
            "// Returns \"Hello, John! Your balance is $125.87\"",
            "• Creating formatted messages\n" +
            "• Building strings with multiple variables\n" +
            "• Generating reports with consistent formatting"
        ));
        
        methods.add(new MethodInfo(
            "DateTimeFormatter.ofLocalizedDate(FormatStyle dateStyle)",
            "Creates a formatter that will format or parse a date.",
            "dateStyle: the formatter style to use, not null",
            "DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);\n" +
            "LocalDate date = LocalDate.of(2023, 5, 15);\n" +
            "String formatted = formatter.format(date); // Returns something like \"May 15, 2023\"",
            "• Creating localized date representations\n" +
            "• Displaying dates in user's preferred format\n" +
            "• Building internationalized applications"
        ));
        
        methods.add(new MethodInfo(
            "System.out.printf(String format, Object... args)",
            "A convenience method to write a formatted string to this output stream using the specified format string and arguments.",
            "format: a format string\nargs: arguments referenced by the format specifiers in the format string",
            "int id = 5;\n" +
            "String name = \"Widget\";\n" +
            "double price = 29.99;\n" +
            "System.out.printf(\"Item #%d: %s - $%.2f%n\", id, name, price);\n" +
            "// Prints \"Item #5: Widget - $29.99\" followed by a line separator",
            "• Displaying formatted output to console\n" +
            "• Debugging with formatted values\n" +
            "• Creating aligned tabular output"
        ));

        return methods;

    }

    private List<MethodInfo> getDateMethods() {
        List<MethodInfo> methods = new ArrayList<>();

        methods.add(new MethodInfo(
            "LocalDate.now()",
            "Returns the current date from the system clock in the default time zone.",
            "No parameters",
            "LocalDate today = LocalDate.now();\n" +
            "// Returns the current date like 2023-05-15",
            "• Getting the current date\n" +
            "• Creating timestamps\n" +
            "• Initializing date fields in applications"
        ));
        
        methods.add(new MethodInfo(
            "LocalDate.parse(CharSequence text)",
            "Obtains an instance of LocalDate from a text string such as '2007-12-03'.",
            "text: the text to parse such as '2007-12-03', not null",
            "LocalDate date = LocalDate.parse(\"2023-05-15\");\n" +
            "// Parses the string into a LocalDate representing May 15, 2023",
            "• Converting date strings to LocalDate objects\n" +
            "• Processing user input\n" +
            "• Parsing dates from external data sources"
        ));
        
        methods.add(new MethodInfo(
            "LocalDateTime.plusDays(long days)",
            "Returns a copy of this LocalDateTime with the specified number of days added.",
            "days: the days to add, may be negative",
            "LocalDateTime dateTime = LocalDateTime.of(2023, 5, 15, 10, 30);\n" +
            "LocalDateTime future = dateTime.plusDays(7);\n" +
            "// Returns 2023-05-22T10:30:00",
            "• Calculating future dates\n" +
            "• Managing deadlines\n" +
            "• Scheduling appointments"
        ));
        
        methods.add(new MethodInfo(
            "Duration.between(Temporal startInclusive, Temporal endExclusive)",
            "Obtains a Duration representing the duration between two temporal objects.",
            "startInclusive: the start instant, inclusive, not null\nendExclusive: the end instant, exclusive, not null",
            "LocalTime start = LocalTime.of(9, 0);\n" +
            "LocalTime end = LocalTime.of(17, 30);\n" +
            "Duration duration = Duration.between(start, end);\n" +
            "long hours = duration.toHours(); // Returns 8\n" +
            "long minutes = duration.toMinutesPart(); // Returns 30",
            "• Calculating time differences\n" +
            "• Measuring elapsed time\n" +
            "• Computing work hours or durations"
        ));
        
        methods.add(new MethodInfo(
            "Period.between(LocalDate startDateInclusive, LocalDate endDateExclusive)",
            "Obtains a Period consisting of the number of years, months, and days between two dates.",
            "startDateInclusive: the start date, inclusive, not null\nendDateExclusive: the end date, exclusive, not null",
            "LocalDate birthDate = LocalDate.of(1990, 5, 15);\n" +
            "LocalDate currentDate = LocalDate.of(2023, 5, 15);\n" +
            "Period period = Period.between(birthDate, currentDate);\n" +
            "int years = period.getYears(); // Returns 33",
            "• Calculating age\n" +
            "• Determining time spans between dates\n" +
            "• Measuring anniversary periods"
        ));
        
        methods.add(new MethodInfo(
            "ZonedDateTime.withZoneSameInstant(ZoneId zone)",
            "Returns a copy of this ZonedDateTime with a different time zone, retaining the instant.",
            "zone: the time zone to change to, not null",
            "ZonedDateTime nyTime = ZonedDateTime.now(ZoneId.of(\"America/New_York\"));\n" +
            "ZonedDateTime laTime = nyTime.withZoneSameInstant(ZoneId.of(\"America/Los_Angeles\"));\n" +
            "// Converts time from New York to Los Angeles time zone",
            "• Converting between time zones\n" +
            "• Managing international scheduling\n" +
            "• Displaying localized times for users in different regions"
        ));
        
        methods.add(new MethodInfo(
            "LocalDate.until(ChronoLocalDate endDateExclusive)",
            "Calculates the period between this date and another date as a Period.",
            "endDateExclusive: the end date, exclusive, not null",
            "LocalDate startDate = LocalDate.of(2023, 1, 1);\n" +
            "LocalDate endDate = LocalDate.of(2023, 12, 31);\n" +
            "Period period = startDate.until(endDate);\n" +
            "int months = period.getMonths(); // Part of the period in months",
            "• Calculating remaining time until a deadline\n" +
            "• Determining contract or subscription durations\n" +
            "• Planning based on time intervals"
        ));
        
        methods.add(new MethodInfo(
            "LocalDateTime.of(int year, int month, int dayOfMonth, int hour, int minute)",
            "Obtains an instance of LocalDateTime from year, month, day, hour and minute.",
            "year: the year to represent\nmonth: the month-of-year to represent\ndayOfMonth: the day-of-month to represent\nhour: the hour-of-day to represent\nminute: the minute-of-hour to represent",
            "LocalDateTime dateTime = LocalDateTime.of(2023, 5, 15, 14, 30);\n" +
            "// Creates a LocalDateTime for May 15, 2023, 2:30 PM",
            "• Creating specific date-time instances\n" +
            "• Setting appointment times\n" +
            "• Building scheduled events"
        ));
        
        methods.add(new MethodInfo(
            "LocalDate.datesUntil(LocalDate endExclusive)",
            "Returns a sequential stream of dates from this date (inclusive) to the given end date (exclusive).",
            "endExclusive: the end date, exclusive, not null",
            "LocalDate start = LocalDate.of(2023, 5, 1);\n" +
            "LocalDate end = LocalDate.of(2023, 5, 6);\n" +
            "List<LocalDate> dates = start.datesUntil(end).collect(Collectors.toList());\n" +
            "// Returns a list containing 2023-05-01, 2023-05-02, 2023-05-03, 2023-05-04, 2023-05-05",
            "• Generating date ranges\n" +
            "• Creating calendar views\n" +
            "• Building scheduling systems"
        ));
        
        methods.add(new MethodInfo(
            "Instant.toEpochMilli()",
            "Converts this instant to the number of milliseconds from the epoch of 1970-01-01T00:00:00Z.",
            "No parameters",
            "Instant now = Instant.now();\n" +
            "long epochMilli = now.toEpochMilli();\n" +
            "// Returns the current time as milliseconds since Unix epoch",
            "• Converting to legacy timestamp formats\n" +
            "• Working with APIs that require epoch time\n" +
            "• Creating unique time-based identifiers"
        ));
        
        return methods;

    }

    private List<MethodInfo> getCharacterMethods() {
        List<MethodInfo> methods = new ArrayList<>();

        methods.add(new MethodInfo(
            "Character.isDigit(char ch)",
            "Determines if the specified character is a digit.",
            "ch: the character to be tested",
            "char c = '5';\n" +
            "boolean isDigit = Character.isDigit(c); // Returns true",
            "• Validating numeric input\n" +
            "• Parsing numbers from text\n" +
            "• Implementing input filters"
        ));
        
        methods.add(new MethodInfo(
            "Character.isLetter(char ch)",
            "Determines if the specified character is a letter.",
            "ch: the character to be tested",
            "char c = 'A';\n" +
            "boolean isLetter = Character.isLetter(c); // Returns true",
            "• Validating text input\n" +
            "• Implementing search algorithms\n" +
            "• Text processing"
        ));
        
        methods.add(new MethodInfo(
            "Character.isLetterOrDigit(char ch)",
            "Determines if the specified character is a letter or digit.",
            "ch: the character to be tested",
            "char c = 'a';\n" +
            "boolean isLetterOrDigit = Character.isLetterOrDigit(c); // Returns true",
            "• Validating alphanumeric input\n" +
            "• Parsing identifiers\n" +
            "• Implementing username validation"
        ));
        
        methods.add(new MethodInfo(
            "Character.isWhitespace(char ch)",
            "Determines if the specified character is white space.",
            "ch: the character to be tested",
            "char c = ' ';\n" +
            "boolean isWhitespace = Character.isWhitespace(c); // Returns true",
            "• Tokenizing text\n" +
            "• Trimming strings\n" +
            "• Processing formatted input"
        ));
        
        methods.add(new MethodInfo(
            "Character.isUpperCase(char ch)",
            "Determines if the specified character is an uppercase character.",
            "ch: the character to be tested",
            "char c = 'A';\n" +
            "boolean isUpperCase = Character.isUpperCase(c); // Returns true",
            "• Validating password complexity\n" +
            "• Implementing case-sensitive operations\n" +
            "• Analyzing text case"
        ));
        
        methods.add(new MethodInfo(
            "Character.isLowerCase(char ch)",
            "Determines if the specified character is a lowercase character.",
            "ch: the character to be tested",
            "char c = 'a';\n" +
            "boolean isLowerCase = Character.isLowerCase(c); // Returns true",
            "• Case validation\n" +
            "• Text analysis\n" +
            "• Implementing formatting rules"
        ));
        
        methods.add(new MethodInfo(
            "Character.toUpperCase(char ch)",
            "Converts the character argument to uppercase.",
            "ch: the character to be converted",
            "char c = 'a';\n" +
            "char upperCase = Character.toUpperCase(c); // Returns 'A'",
            "• Case conversion\n" +
            "• Normalizing text\n" +
            "• Implementing case-insensitive matching"
        ));
        
        methods.add(new MethodInfo(
            "Character.toLowerCase(char ch)",
            "Converts the character argument to lowercase.",
            "ch: the character to be converted",
            "char c = 'A';\n" +
            "char lowerCase = Character.toLowerCase(c); // Returns 'a'",
            "• Case normalization\n" +
            "• Text processing\n" +
            "• Implementing search algorithms"
        ));
        
        methods.add(new MethodInfo(
            "Character.getNumericValue(char ch)",
            "Returns the int value that the specified character represents.",
            "ch: the character to be converted",
            "char c = '7';\n" +
            "int value = Character.getNumericValue(c); // Returns 7\n" +
            "char hex = 'A';\n" +
            "int hexValue = Character.getNumericValue(hex); // Returns 10",
            "• Converting character digits to numbers\n" +
            "• Parsing hexadecimal values\n" +
            "• Implementing custom number parsers"
        ));
        
        methods.add(new MethodInfo(
            "Character.isAlphabetic(int codePoint)",
            "Determines if the specified character (Unicode code point) is an alphabet.",
            "codePoint: the character (Unicode code point) to be tested",
            "int codePoint = 'A';\n" +
            "boolean isAlphabetic = Character.isAlphabetic(codePoint); // Returns true",
            "• Unicode-aware text processing\n" +
            "• Supporting international character sets\n" +
            "• Implementing multilingual text analysis"
        ));
        
        methods.add(new MethodInfo(
            "Character.isDefined(char ch)",
            "Determines if the specified character is defined in Unicode.",
            "ch: the character to be tested",
            "char c = 'A';\n" +
            "boolean isDefined = Character.isDefined(c); // Returns true",
            "• Validating Unicode characters\n" +
            "• Processing international text\n" +
            "• Character set validation"
        ));
        
        methods.add(new MethodInfo(
            "Character.isJavaIdentifierStart(char ch)",
            "Determines if the specified character may be the first character in a Java identifier.",
            "ch: the character to be tested",
            "char c = 'a';\n" +
            "boolean isJavaIdentifierStart = Character.isJavaIdentifierStart(c); // Returns true\n" +
            "char digit = '9';\n" +
            "boolean digitStart = Character.isJavaIdentifierStart(digit); // Returns false",
            "• Implementing code editors\n" +
            "• Parsing Java source code\n" +
            "• Validating variable names"
        ));
        
        methods.add(new MethodInfo(
            "Character.isJavaIdentifierPart(char ch)",
            "Determines if the specified character may be part of a Java identifier after the first character.",
            "ch: the character to be tested",
            "char c = '2';\n" +
            "boolean isJavaIdentifierPart = Character.isJavaIdentifierPart(c); // Returns true",
            "• Validating variable names\n" +
            "• Implementing syntax highlighting\n" +
            "• Parsing Java identifiers"
        ));
        
        methods.add(new MethodInfo(
            "Character.isIdeographic(int codePoint)",
            "Determines if the specified character (Unicode code point) is an ideographic character.",
            "codePoint: the character (Unicode code point) to be tested",
            "int codePoint = '你'; // Chinese character\n" +
            "boolean isIdeographic = Character.isIdeographic(codePoint); // Check if it's an ideographic character",
            "• Processing East Asian text\n" +
            "• Supporting multilingual applications\n" +
            "• Implementing specialized text analysis"
        ));
        
        methods.add(new MethodInfo(
            "Character.charCount(int codePoint)",
            "Returns the number of char values needed to represent the specified character (Unicode code point).",
            "codePoint: the character (Unicode code point) to be tested",
            "int emoji = 0x1F600; // Smiling face emoji code point\n" +
            "int count = Character.charCount(emoji); // Returns 2 for surrogate pairs",
            "• Processing emoji and supplementary characters\n" +
            "• Implementing Unicode-aware string operations\n" +
            "• Building advanced text editors with proper surrogate pair handling"
        ));
        
        return methods;

    }

    private List<MethodInfo> getAdvStringMethods() {
        List<MethodInfo> methods = new ArrayList<>();


        methods.add(new MethodInfo(
            "String.concat(String str)",
            "Concatenates the specified string to the end of this string.",
            "str: the String that is concatenated to the end of this String",
            "String first = \"Hello, \";\n" +
            "String result = first.concat(\"World!\"); // Returns \"Hello, World!\"",
            "• Building strings dynamically\n" +
            "• Combining text fragments\n" +
            "• Creating messages from parts"
        ));

        methods.add(new MethodInfo(
            "String.compareTo(String anotherString)",
            "Compares two strings lexicographically.",
            "anotherString: the String to be compared",
            "String str1 = \"apple\";\n" +
            "String str2 = \"banana\";\n" +
            "int result = str1.compareTo(str2); // Returns negative value as \"apple\" comes before \"banana\"",
            "• Sorting strings\n" +
            "• Implementing comparators\n" +
            "• Building ordered structures"
        ));

        methods.add(new MethodInfo(
            "String.regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len)",
            "Tests if two string regions are equal.",
            "ignoreCase: if true, ignore case when comparing characters\ntoffset: the starting offset of the subregion in this string\nother: the string argument\nooffset: the starting offset of the subregion in the string argument\nlen: the number of characters to compare",
            "String str1 = \"Hello World\";\n" +
            "String str2 = \"ELLO WO\";\n" +
            "boolean matches = str1.regionMatches(true, 1, str2, 0, 7); // Returns true",
            "• Partial string comparison\n" +
            "• Implementing fuzzy search\n" +
            "• Text analysis algorithms"
        ));

        methods.add(new MethodInfo(
            "String.intern()",
            "Returns a canonical representation for the string object.",
            "No parameters",
            "String s1 = new String(\"Hello\");\n" +
            "String s2 = \"Hello\";\n" +
            "String s3 = s1.intern();\n" +
            "boolean result = (s2 == s3); // Returns true",
            "• Memory optimization\n" +
            "• String pooling\n" +
            "• Ensuring string identity"
        ));

        methods.add(new MethodInfo(
            "String.subSequence(int beginIndex, int endIndex)",
            "Returns a character sequence that is a subsequence of this sequence.",
            "beginIndex: the beginning index, inclusive\nendIndex: the ending index, exclusive",
            "String text = \"Hello, World!\";\n" +
            "CharSequence seq = text.subSequence(0, 5); // Returns \"Hello\"",
            "• Implementing CharSequence interface\n" +
            "• Text processing\n" +
            "• Working with character sequences"
        ));

        methods.add(new MethodInfo(
            "String.offsetByCodePoints(int index, int codePointOffset)",
            "Returns the index within this String that is offset from the given index by codePointOffset code points.",
            "index: the index to be offset\ncodePointOffset: the offset in code points",
            "String str = \"Hello😊World\";\n" +
            "int index = str.offsetByCodePoints(5, 1); // Returns 7 (skips the emoji which is 2 code units)",
            "• Unicode-aware string manipulation\n" +
            "• Handling international text\n" +
            "• Emoji and special character processing"
        ));

        methods.add(new MethodInfo(
            "String.codePointAt(int index)",
            "Returns the Unicode code point at the specified index.",
            "index: the index to the char values",
            "String str = \"Hello😊\";\n" +
            "int codePoint = str.codePointAt(5); // Returns the code point for 😊",
            "• Unicode processing\n" +
            "• Character analysis\n" +
            "• International text handling"
        ));

        methods.add(new MethodInfo(
            "String.codePointCount(int beginIndex, int endIndex)",
            "Returns the number of Unicode code points in the specified text range.",
            "beginIndex: the index to the first char of the text range\nendIndex: the index after the last char of the text range",
            "String str = \"Hello😊World\";\n" +
            "int count = str.codePointCount(0, str.length()); // Returns 11 (not 12, as the emoji is one code point)",
            "• Unicode-aware string length calculation\n" +
            "• Text analysis for international content\n" +
            "• Character counting in mixed-script text"
        ));

        methods.add(new MethodInfo(
            "String.contentEquals(CharSequence cs)",
            "Compares this string to the specified CharSequence.",
            "cs: the sequence to compare this String against",
            "String str = \"Hello\";\n" +
            "StringBuilder sb = new StringBuilder(\"Hello\");\n" +
            "boolean equals = str.contentEquals(sb); // Returns true",
            "• Comparing strings with other character sequences\n" +
            "• Working with different text representations\n" +
            "• Flexibility in text comparison"
        ));

        methods.add(new MethodInfo(
            "String.getBytes(Charset charset)",
            "Encodes this String into a sequence of bytes using the specified charset.",
            "charset: the charset to be used to encode the String",
            "String str = \"Hello, World!\";\n" +
            "byte[] bytes = str.getBytes(StandardCharsets.UTF_8);\n" +
            "// Returns the UTF-8 encoded bytes",
            "• Data serialization\n" +
            "• Network communication\n" +
            "• File I/O operations"
        ));

        methods.add(new MethodInfo(
            "String.lines()",
            "Returns a stream of lines extracted from this string, separated by line terminators.",
            "No parameters",
            "String multiline = \"Line 1\\nLine 2\\nLine 3\";\n" +
            "List<String> lines = multiline.lines().collect(Collectors.toList());\n" +
            "// Returns [\"Line 1\", \"Line 2\", \"Line 3\"]",
            "• Processing multiline text\n" +
            "• Parsing line-based formats\n" +
            "• Text analysis by line"
        ));

        methods.add(new MethodInfo(
            "String.formatted(Object... args)",
            "Returns a formatted string using this string as the format string and the supplied arguments.",
            "args: arguments referenced by the format specifiers in the format string",
            "String template = \"Hello, %s! You are %d years old.\";\n" +
            "String result = template.formatted(\"Alice\", 30);\n" +
            "// Returns \"Hello, Alice! You are 30 years old.\"",
            "• Creating dynamic messages\n" +
            "• Templating\n" +
            "• Localized formatting"
        ));

        methods.add(new MethodInfo(
            "String.translateEscapes()",
            "Returns a string whose value is this string, with escape sequences translated as if in a string literal.",
            "No parameters",
            "String escaped = \"Hello\\\\nWorld\\\\t!\";\n" +
            "String translated = escaped.translateEscapes();\n" +
            "// Returns a string with actual newline and tab characters",
            "• Processing escaped strings\n" +
            "• Handling user input with escape sequences\n" +
            "• Parsing configuration files"
        ));

        methods.add(new MethodInfo(
            "String.stripIndent()",
            "Returns a string whose value is this string, with incidental white space removed from the beginning and end of every line.",
            "No parameters",
            "String multiline = \"\"\"\n" +
            "        Line 1\n" +
            "        Line 2\n" +
            "        Line 3\n" +
            "        \"\"\";\n" +
            "String result = multiline.stripIndent();\n" +
            "// Returns text with consistent indentation removed",
            "• Processing multi-line text blocks\n" +
            "• Cleaning indented content\n" +
            "• Normalizing text formatting"
        ));

        methods.add(new MethodInfo(
            "String.transform(Function<? super String, ? extends R> f)",
            "Applies a function to this string.",
            "f: a function to apply to this string",
            "String input = \"42\";\n" +
            "Integer value = input.transform(Integer::parseInt);\n" +
            "// Returns 42 as an Integer",
            "• Chaining string operations\n" +
            "• Functional-style string processing\n" +
            "• Converting strings to other types"
        ));
        
        return methods;

    }


    
    private TextFlow createSyntaxHighlightedExample(String example) {
        TextFlow textFlow = new TextFlow();
        
        // Arrays of Java keywords and types to highlight
        String[] keywords = {"new", "return", "if", "else", "for", "while", "try", "catch", "public", "private", 
                         "protected", "class", "interface", "static", "final", "void", "null", "true", "false"};
        String[] types = {"String", "int", "double", "boolean", "char", "long", "byte", "short", "float", 
                     "Integer", "Double", "Boolean", "List", "Map", "Set", "Queue", "ArrayList", 
                     "HashMap", "Arrays", "Collections"};
        
        // Split the code into lines
        String[] lines = example.split("\n");
        for (String line : lines) {
            TextFlow lineFlow = new TextFlow();
            
            // Split by spaces but preserve spaces
            String[] parts = line.split("(?<=\\s)|(?=\\s)");
            
            for (String part : parts) {
                Text text = new Text(part);
                
                // Check and apply syntax highlighting
                if (isInArray(part.trim(), keywords)) {
                    text.setFill(Color.PURPLE);
                    text.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
                } else if (isInArray(part.trim(), types)) {
                    text.setFill(Color.BLUE);
                    text.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
                } else if (part.trim().startsWith("\"") && part.trim().endsWith("\"")) {
                    // String literals
                    text.setFill(Color.GREEN);
                    text.setFont(Font.font("Monospace", 12));
                } else if (part.trim().matches("\\d+(\\.\\d+)?")) {
                    // Number literals
                    text.setFill(Color.ORANGE);
                    text.setFont(Font.font("Monospace", 12));
                } else if (part.contains("//")) {
                    // Comments
                    text.setFill(Color.GRAY);
                    text.setFont(Font.font("Monospace", FontWeight.LIGHT, 12));
                } else {
                    // Regular text
                    text.setFont(Font.font("Monospace", 12));
                }
                
                lineFlow.getChildren().add(text);
            }
            
            // Add the completed line and a new line character
            Text newLine = new Text("\n");
            textFlow.getChildren().addAll(lineFlow.getChildren());
            textFlow.getChildren().add(newLine);
        }
        
        return textFlow;
    }
    
    // Helper method to check if a string is in an array
    private boolean isInArray(String item, String[] array) {
        for (String s : array) {
            if (s.equals(item)) {
                return true;
            }
        }
        return false;
    }

    // Helper method to count lines in a string
    private int countLines(String text) {
        if (text == null || text.isEmpty()) {
            return 1;
        }
        
        String[] lines = text.split("\n");
        int lineCount = lines.length;
        
        // Add extra lines for wrapped text
        for (String line : lines) {
            // Estimate additional lines needed for wrapping (assuming ~60 chars per line)
            int wrapLines = (int) Math.ceil(line.length() / 60.0) - 1;
            if (wrapLines > 0) {
                lineCount += wrapLines;
            }
        }
        
        return lineCount;
    }
    
    // All other existing methods...

    public static void main(String[] args) {
        launch(args);
    }
}