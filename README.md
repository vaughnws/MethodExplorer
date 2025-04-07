# 📚 Method Explorer
<img width="100" alt="app-icon" src="https://github.com/user-attachments/assets/cfafab60-6ba2-4c20-9c14-b0abad2f3f1b" />

A comprehensive JavaFX application that serves as an interactive reference guide for Java methods, designed to help developers quickly find, understand, and implement Java methods in their code.

![Method Explorer](https://github.com/user-attachments/assets/5a682b51-2ac6-406f-9fb4-26ab6ef5ec56)
## 🚀 TL;DR

**Method Explorer** is a JavaFX desktop application that provides an organized reference library of Java methods. Browse categorized methods, search by name or functionality, view detailed documentation with examples, and create your own custom method references. Features include syntax highlighting, dark mode, one-click copying, and persistent storage of your custom methods.

## 📋 Table of Contents

- [Features](#-features)
- [Application Overview](#-application-overview)
- [User Interface](#-user-interface)
- [Method Categories](#-method-categories)
- [Detailed Feature Explanation](#-detailed-feature-explanation)
- [Technical Implementation](#-technical-implementation)
- [Data Persistence](#-data-persistence)
- [Getting Started](#-getting-started)
- [Tips and Tricks](#-tips-and-tricks)
- [Customization Options](#-customization-options)
- [Development Insights](#-development-insights)

## ✨ Features

Method Explorer comes packed with features designed to enhance your Java development experience:

- 🔍 **Comprehensive Search**: Instantly find methods across all categories with smart filtering
- 📋 **One-Click Copy**: Easily copy method signatures, parameters, examples, and descriptions
- 🌈 **Syntax Highlighting**: Color-coded example code makes understanding code examples faster
- 📝 **Custom Methods Library**: Create, edit, and save your own method references
- 🌙 **Dark/Light Themes**: Toggle between themes to reduce eye strain during long coding sessions
- 📁 **Organized Method Categories**: Browse methods through logical, well-structured categories
- 📊 **Detailed Documentation**: Each method includes comprehensive information about usage and implementation
- 🔄 **Persistent User Data**: Your custom methods are automatically saved and loaded between sessions
- 💾 **Backup System**: Automatic backups of your custom methods before saving changes
- 🧠 **Smart Categorization**: Methods are organized by functional purpose for easier discovery
- 🔗 **Breadcrumb Navigation**: Easily track and navigate your exploration path

## 🖥 Application Overview

Method Explorer is designed to solve a common problem for Java developers: quickly finding and understanding the various methods available in the Java standard library and custom user libraries. Instead of constantly searching online documentation or IDE tooltips, Method Explorer provides a centralized, searchable, and categorized reference that's always at your fingertips.

The application serves as both a learning tool for those new to Java and a productivity booster for experienced developers who need quick access to method signatures, parameters, and example code. It bridges the gap between dry API documentation and practical implementation by providing real-world examples and use cases for each method.

## 🎨 User Interface

The Method Explorer interface is divided into several key sections, designed for efficient navigation and information access:

### 🔝 Top Bar

- **Home Button**: Returns to the welcome screen from any view
- **Application Title**: Displays "Method Explorer" for easy identification
- **Dark Mode Toggle**: Switch between light and dark themes
- **Search Bar**: Global search functionality across all method categories

### 📑 Left Sidebar

- **Method Categories**: Expandable/collapsible sections organized by functionality
- **User Methods Section**: Access to your custom method library
- **Add New Method Button**: Quick access to create custom methods

### 📄 Main Content Area

- **Welcome Screen**: Introduction and quick start guide when first launching
- **Method Details View**: Comprehensive information about selected methods
- **Search Results**: Organized display of search results with category grouping

### 📝 Method Detail Components

- **Breadcrumb Navigation**: Shows your current location in the application
- **Method Title**: Full method signature with clear formatting
- **Description Section**: Detailed explanation of the method's purpose
- **Parameters Section**: Information about each parameter the method accepts
- **Example Code Section**: Practical code examples with syntax highlighting
- **Use Cases Section**: Common scenarios where the method is useful
- **Action Buttons**: Copy, edit, and delete functionality

## 🧩 Method Categories

Method Explorer organizes Java methods into intuitive categories for easier discovery:

### 📝 String Methods
Comprehensive collection of methods for string manipulation and processing:
- **Basic Operations**: `length()`, `isEmpty()`, `charAt()`, `substring()`
- **Comparison**: `equals()`, `equalsIgnoreCase()`, `compareTo()`, `startsWith()`, `endsWith()`
- **Transformation**: `toLowerCase()`, `toUpperCase()`, `trim()`, `strip()`, `replace()`, `split()`
- **Formatting**: `format()`, `join()`, `concat()`
- **Searching**: `indexOf()`, `lastIndexOf()`, `contains()`

### 🔄 Collection Methods
Methods for working with Java's collection framework:
- **List Operations**: `add()`, `remove()`, `get()`, `subList()`
- **Set Operations**: `contains()`, `add()`, `remove()`
- **Map Operations**: `put()`, `get()`, `entrySet()`, `containsKey()`
- **Utility Methods**: `sort()`, `shuffle()`, `reverse()`, `binarySearch()`
- **Status Checks**: `isEmpty()`, `size()`, `containsAll()`

### 🔢 Math Methods
Mathematical operations and calculations:
- **Basic Math**: `abs()`, `min()`, `max()`, `floor()`, `ceil()`, `round()`
- **Advanced Math**: `pow()`, `sqrt()`, `log()`, `exp()`, `sin()`, `cos()`, `tan()`
- **Random Numbers**: `random()`, `nextInt()`, `nextDouble()`
- **Special Functions**: `hypot()`, `toDegrees()`, `toRadians()`, `IEEEremainder()`

### 📅 Date and Time Methods
Modern date and time manipulation with the Java 8+ API:
- **Creation**: `now()`, `of()`, `parse()`
- **Manipulation**: `plus()`, `minus()`, `with()`, `until()`
- **Formatting**: `format()`, `ofPattern()`, `ofLocalizedDate()`
- **Conversion**: `toEpochMilli()`, `toInstant()`, `toLocalDate()`
- **Duration/Period**: `between()`, `ofDays()`, `ofYears()`

### 💾 File I/O Methods
File system and I/O operations:
- **File Management**: `createDirectory()`, `delete()`, `move()`, `copy()`
- **Reading**: `readAllLines()`, `readAllBytes()`, `newBufferedReader()`
- **Writing**: `write()`, `newBufferedWriter()`, `createTempFile()`
- **Navigation**: `list()`, `walk()`, `find()`
- **Attributes**: `exists()`, `isDirectory()`, `getLastModifiedTime()`

### 🌊 Stream API
Functional-style operations for collections:
- **Creation**: `stream()`, `of()`, `generate()`, `iterate()`
- **Intermediate Operations**: `filter()`, `map()`, `flatMap()`, `sorted()`, `distinct()`
- **Terminal Operations**: `collect()`, `reduce()`, `forEach()`, `count()`, `findFirst()`
- **Short-circuit Operations**: `anyMatch()`, `allMatch()`, `noneMatch()`, `limit()`
- **Specialized Streams**: `IntStream`, `LongStream`, `DoubleStream`

### 💰 BigDecimal/BigInteger Methods
High-precision numeric operations:
- **Arithmetic**: `add()`, `subtract()`, `multiply()`, `divide()`
- **Rounding**: `setScale()`, `round()`, `stripTrailingZeros()`
- **Comparison**: `compareTo()`, `equals()`, `max()`, `min()`
- **Conversion**: `doubleValue()`, `intValue()`, `toString()`
- **Special Operations**: `movePointLeft()`, `movePointRight()`, `isProbablePrime()`

### 🔠 Character Methods
Character manipulation and testing:
- **Testing**: `isDigit()`, `isLetter()`, `isWhitespace()`, `isUpperCase()`, `isLowerCase()`
- **Conversion**: `toUpperCase()`, `toLowerCase()`, `getNumericValue()`
- **Unicode**: `isAlphabetic()`, `isDefined()`, `charCount()`, `codePointAt()`
- **Special Checks**: `isJavaIdentifierStart()`, `isJavaIdentifierPart()`, `isIdeographic()`

### 📊 Formatter Methods
Formatting numbers, dates, and text:
- **Number Formatting**: `getCurrencyInstance()`, `getPercentInstance()`, `setMaximumFractionDigits()`
- **Date Formatting**: `ofPattern()`, `ofLocalizedDate()`, `format()`
- **Custom Formats**: `DecimalFormat`, `MessageFormat`, `SimpleDateFormat`
- **Locale-Specific**: `getAvailableLocales()`, `getDefault()`, `forLanguageTag()`

### 👤 User Methods
Your custom method references, fully editable and persisted between sessions.

## 🔍 Detailed Feature Explanation

### 🔎 Search Functionality

The search system in Method Explorer is designed to be both powerful and intuitive:

1. **Real-time Filtering**: Results update as you type, with no need to press Enter
2. **Multi-field Search**: Searches across method names, descriptions, parameters, and examples
3. **Category Organization**: Search results are grouped by category for easier browsing
4. **Highlighting**: Matching text is visually highlighted in search results
5. **Smart Ranking**: More relevant results appear higher in the list based on match quality
6. **Result Count**: Shows the number of matches found for your search term
7. **No Results Handling**: Provides suggestions when no matches are found
8. **Search History**: Recent searches are accessible for quick re-use (not implemented in current version)

The search bar is always accessible from the top of the application, allowing you to find methods from anywhere in the interface.

### 📝 Custom Method Management

Method Explorer allows you to build your own method reference library:

1. **Creation**: Add new methods with the "Add New Method" button in the User Methods category
2. **Required Fields**:
   - **Method Name**: Full signature including class name and parameters
   - **Description**: Explanation of what the method does
   - **Parameters**: Details about the inputs the method accepts
   - **Example**: Practical code showing how to use the method
   - **Use Cases**: Common scenarios where the method is useful

3. **Editing**: Modify any of your custom methods using the Edit button
4. **Deletion**: Remove methods from your library with confirmation prompt
5. **Bulk Import**: Paste formatted method content to quickly add multiple methods
6. **Persistence**: All changes are automatically saved to disk
7. **Categorization**: Custom methods appear in the User Methods category

### 🎨 Theme System

Method Explorer features a comprehensive theming system:

1. **Light/Dark Modes**: Toggle between light and dark themes via the top-right button
2. **Persistent Preference**: Your theme choice is remembered between sessions
3. **System Integration**: Respects system-level theme preferences
4. **Color Palette**: Carefully designed color schemes for both themes:
   - **Light Theme**: Clean white backgrounds with blue accents
   - **Dark Theme**: Dark backgrounds with contrasting elements for reduced eye strain
5. **Icon Adaptation**: Icons automatically adjust to match the current theme
6. **Contrast Ratios**: All text meets accessibility standards for readability
7. **Custom Styling**: Each component has theme-specific styling

### 📋 Copy and Paste Functionality

Easy data transfer between Method Explorer and your IDE:

1. **Selective Copying**: Copy specific sections (name, description, example, etc.)
2. **One-Click Copy**: Copy buttons are provided for convenient access
3. **Visual Feedback**: Button text changes to "Copied!" temporarily after clicking
4. **Code Formatting Preservation**: Code formatting and indentation are maintained when copied
5. **Paste Support**: Special paste functionality for adding formatted method content

### 📊 Method Visualization

Methods are presented with clear visual organization:

1. **Section Dividers**: Clear separation between different information types
2. **Syntax Highlighting**: Code examples use color coding for different syntax elements
3. **Visual Hierarchy**: Important information is visually emphasized
4. **Icons**: Intuitive icons provide visual cues about information types
5. **Whitespace Management**: Proper spacing improves readability
6. **Font Choices**: Monospaced fonts for code, sans-serif for descriptions
7. **Card-Based Layout**: Each method is presented in a visually distinct card

## 🔧 Technical Implementation

Method Explorer is built with modern Java and JavaFX technologies:

### 🏗 Architecture

- **MVC Pattern**: Clear separation between data model, view components, and controller logic
- **Object-Oriented Design**: Encapsulated, reusable components with clear responsibilities
- **Event-Driven UI**: Responsive interface that reacts to user interactions
- **Responsive Layout**: UI adapts to different window sizes
- **Modular Construction**: Logically separated code for easier maintenance

### 📦 Key Classes

- **App.java**: Main application class that initializes the UI and handles navigation
- **MethodInfo.java**: Data model class representing individual method information
- **IconGenerator**: Utility class that creates vector icons for the interface
- **Styling System**: Custom CSS classes for theming and visual consistency

### 🖌 UI Components

- **JavaFX Controls**: Uses modern JavaFX controls like TitledPane, ScrollPane, SplitPane
- **Custom Components**: Enhanced standard components with additional functionality
- **Animation**: Smooth transitions between different states and views
- **Lazy Loading**: Optimizes performance by loading content only when needed
- **Event Handlers**: Comprehensive event handling for user interactions

### 🔄 Data Flow

1. **Loading**: Application loads built-in and user methods on startup
2. **User Interaction**: UI events trigger appropriate handlers
3. **Data Manipulation**: Changes to method data are processed in memory
4. **Persistence**: Modified data is serialized to disk for future sessions
5. **Search Processing**: Search queries filter the method collection for display

## 💾 Data Persistence

Method Explorer implements a robust system for preserving user data:

### 📁 Storage Format

- **Serialization**: Java's built-in serialization for storing MethodInfo objects
- **File Location**: Data stored in the user's home directory in a `.method-explorer` folder
- **File Naming**: Primary storage in `user_methods.dat`

### 🔄 Backup System

1. **Automatic Backups**: Creates `user_methods.dat.bak` before saving changes
2. **Failover Recovery**: Attempts to load from backup if primary file is corrupted
3. **Error Handling**: Graceful degradation if files cannot be accessed

### 🔒 Data Security

- **Local Storage**: All data stays on the user's machine
- **Standard Permissions**: Uses standard file system permissions
- **Error Logging**: Provides feedback for file operation failures

## 🚀 Getting Started

### 🌟 First Launch Experience

When you first open Method Explorer, you'll see the welcome screen which includes:

1. **Application Overview**: Brief introduction to the application
2. **Quick Start Guide**: Step-by-step guidance on basic features
3. **Feature Highlights**: Showcase of key functionality
4. **Navigation Help**: Instructions on using the interface

### 🧭 Basic Navigation Flow

1. **Browse Categories**: Expand categories in the left sidebar to see available methods
2. **Select Methods**: Click on any method name to view its details in the main area
3. **Search**: Use the search bar to find specific methods across all categories
4. **Add Custom Methods**: Create your own method references with the + button

### 🔧 Advanced Usage

1. **Custom Method Creation**: Add detailed custom methods to build your library
2. **Theming**: Toggle dark mode for different lighting conditions
3. **Keyboard Navigation**: Use Tab and arrow keys to navigate the interface
4. **Copy/Paste Workflow**: Efficiently move code examples to your development environment

## 💡 Tips and Tricks

### ⌨️ Keyboard Shortcuts

- **Search Focus**: Ctrl+F (or Cmd+F on Mac) to focus the search box
- **Navigation**: Tab and arrow keys for moving between elements
- **Copy**: Ctrl+C works on selected text in addition to copy buttons
- **Category Expansion**: Space or Enter to expand/collapse categories

### 🧠 Search Strategies

- **Method Names**: Search for specific method names like "substring" or "indexOf"
- **Functionality**: Try searching for what you want to do, like "sort" or "convert"
- **Parameter Types**: Search for data types like "String" or "int" to find methods that work with them
- **Combinations**: Combine terms to narrow results, like "string convert"

### 🛠 Custom Method Tips

- **Method Naming**: Use consistent naming for easier organization
- **Detailed Examples**: Include comprehensive examples for better understanding
- **Common Patterns**: Document recurring code patterns you use frequently
- **Library Specifics**: Add methods from third-party libraries you use often

## 🎛 Customization Options

### 🎯 UI Preferences

- **Theme Selection**: Choose between light and dark themes
- **Font Size**: System settings for accessibility affect the application (via OS settings)
- **Window Size**: Resize the application window to your preference

### 📚 Content Customization

- **Custom Methods**: Add, edit, and organize your personal method collection
- **Favorites**: Mark frequently used methods (future feature)
- **Notes**: Add personal notes to methods (future feature)

## 🧪 Development Insights

### 🏛 Design Principles

Method Explorer was designed with these core principles:

1. **Simplicity**: Clean, intuitive interface without unnecessary complexity
2. **Efficiency**: Fast access to method information with minimal clicks
3. **Completeness**: Comprehensive information about each method
4. **Customizability**: Ability to extend and personalize the reference library
5. **Accessibility**: Readable design with good contrast and visual hierarchy

### 🔍 Implementation Details

- **Categorization Logic**: Methods are grouped by function rather than by class
- **Search Algorithm**: Prioritizes matches in method names, then descriptions, then other fields
- **Icon System**: Vector-based icons that scale cleanly at different sizes
- **Error Handling**: Comprehensive error handling for file operations and UI events

### 🔮 Future Development

Potential future enhancements for Method Explorer:

1. **Method Favorites**: Ability to mark and filter favorite methods
2. **User Notes**: Add personal notes to any method
3. **Import/Export**: Share custom method collections with other users
4. **Filter System**: More advanced filtering options beyond search
5. **Code Generation**: Generate skeleton code based on method selection
6. **IDE Integration**: Plugins for popular IDEs to access Method Explorer directly
7. **External Documentation**: Links to official Java documentation
8. **Method Versioning**: Track changes between Java versions

## 🙏 Acknowledgments

Method Explorer was created to make Java development more efficient and accessible. It draws inspiration from:

- Official Java documentation
- Modern IDE features
- Developer reference tools
- Community feedback

---

*Method Explorer - Your comprehensive Java method reference library*
