# GitHub Copilot Instructions for Daily Journal

## Project Overview

Daily Journal is a Java Swing desktop application for creating and managing daily journal entries. The application provides a user-friendly GUI for writing, editing, and viewing journal entries with features like tagging, location tracking, password protection, and a year-overview graph visualization.

## Technology Stack

- **Language**: Java 8+
- **UI Framework**: Java Swing
- **Data Storage**: JSON files
- **JSON Handling**: Custom `JSONObject` and `JSONArray` implementations (not external libraries)
- **External Libraries**: 
  - `json-20210307.jar` is present in `lib/` directory but not currently used (the project uses custom JSON implementations instead)

## Project Structure

```
daily-journal/
├── src/java/main/           # All Java source files
│   ├── JournalApp.java      # Main application entry point
│   ├── JournalManager.java  # Manages journal entries
│   ├── TagsManager.java     # Manages global tags
│   ├── PasswordManager.java # Handles password hashing/verification
│   ├── *Dialog.java         # Various dialog windows
│   └── *Panel.java          # UI panel components
├── lib/                     # External JAR dependencies
├── bin/                     # Compiled .class files (gitignored)
└── run.sh                   # Build and run script
```

## Build and Run Instructions

### Compile and Run
```bash
./run.sh
```

Or manually:
```bash
# Compile
javac -cp .:lib/json-20210307.jar -d bin src/java/main/*.java

# Run
java -cp .:lib/json-20210307.jar:bin JournalApp
```

### IDE Setup
The project can be opened in IDEs like VSCode or IntelliJ IDEA. Run `JournalApp.java` directly from the IDE.

## Coding Standards and Conventions

### General Guidelines
- **Java Version**: Code should be compatible with Java 8+
- **Naming Conventions**: Follow standard Java naming conventions
  - Classes: PascalCase (e.g., `JournalManager`)
  - Methods: camelCase (e.g., `addEntry()`)
  - Constants: UPPER_SNAKE_CASE (e.g., `FILE_PATH`)
- **Code Documentation**: Use Javadoc comments for all public classes and methods
- **Formatting**: Use 4-space indentation

### Architecture Patterns

1. **Separation of Concerns**:
   - Manager classes (`JournalManager`, `TagsManager`, `PasswordManager`) handle data and business logic
   - Dialog classes handle user interactions
   - Panel classes manage UI layout and display
   - Main `JournalApp` class orchestrates the application

2. **Data Persistence**:
   - All data is stored in JSON format
   - Use `JournalManager` for entry operations
   - Use `TagsManager` for tag operations
   - Use `PasswordManager` for authentication

3. **UI Components**:
   - Dialogs extend `JDialog` for modal windows
   - Panels extend `JPanel` for reusable UI components
   - Use `JournalApp` as the main frame (extends `JFrame`)

### Common Patterns

1. **File I/O**:
   - Always check if file exists before reading
   - Handle `IOException` appropriately
   - Save data immediately after modifications

2. **Swing UI**:
   - Initialize UI components in EDT (Event Dispatch Thread)
   - Use `SwingUtilities.invokeLater()` for thread-safe UI updates
   - Dispose dialogs properly after use

3. **JSON Handling**:
   - Use the project's custom `JSONObject` and `JSONArray` implementations
   - These classes are located in `src/java/main/JSONObject.java` and `src/java/main/JSONArray.java`
   - These are NOT the standard org.json library classes - they are custom, project-specific implementations
   - Validate JSON data before parsing
   - Handle parsing errors gracefully

### Security Considerations

- Passwords are hashed before storage (see `PasswordManager.java`)
- Never store passwords in plain text
- Validate user input in dialogs to prevent injection issues

## Testing

Currently, this project does not have automated tests. When adding tests:
- Place test files in a `src/test/java/` directory
- Use JUnit for unit testing
- Test manager classes independently of UI components
- Mock file I/O operations for testing

## Common Tasks

### Adding a New Dialog
1. Create a new class extending `JDialog`
2. Initialize UI components in the constructor
3. Add event listeners for buttons
4. Return user input or action result
5. Dispose the dialog when done

### Adding a New Feature
1. Update relevant manager class for business logic
2. Create or modify UI components (dialogs/panels)
3. Update `JournalApp` if needed to integrate the feature
4. Test the feature manually with the GUI

### Modifying Data Structure
1. Update the `JournalEntry` or relevant model class
2. Update JSON serialization/deserialization in manager classes
3. Update UI components that display or edit the data
4. Consider backward compatibility with existing data files

## File Naming Conventions

- Main application: `JournalApp.java`
- Managers: `*Manager.java` (e.g., `JournalManager.java`)
- Dialogs: `*Dialog.java` (e.g., `AddEntryDialog.java`)
- Panels: `*Panel.java` (e.g., `JournalGraphPanel.java`)
- Model classes: Descriptive names (e.g., `JournalEntry.java`)

## Dependencies

When adding new dependencies:
1. Place JAR files in the `lib/` directory
2. Update `run.sh` to include the new JAR in the classpath
3. Update README.md to document the new dependency

## Additional Notes

- The application uses a password lock on startup
- Journal entries are stored in `journal_entries.json`
- Global tags are stored in `tags.json`
- Password hash is stored in `password.json`
- All data files are created in the application's working directory
