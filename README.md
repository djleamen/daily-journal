# Daily Journal
Daily Journal is a Java Swing application that allows users to create and manage daily journal entries. The application provides a user-friendly interface for writing, editing, and deleting journal entries. Users can also view their entries in a list format, making it easy to navigate through past entries.

## Features
Journal Entry Management:
- Create, edit, and delete daily journal entries.
- Each entry includes a title, editable date, location, tags, and content.
Persistent Storage:
- Entries are saved to and loaded from a JSON file (e.g. journal_entries.json).
Year Overview Graph:
- A visual grid (similar to GitHub’s commit graph) that displays daily entry activity throughout the year.
Recent Entries List:
- Displays the most recent entries (with date and title) for quick access.
Tag System and Global Tag Management:
- Add multiple tags per entry.
- Global tags are stored in a separate JSON file (tags.json).
- Manage global tags (including deletion, which removes the tag from all entries).
Unified Filtering:
- A single “Filter” button lets you filter entries by either tag or location using a popup dialog.
Password Protection:
- A password lock prompts the user on startup.
- The password is stored securely (hashed, e.g. in password.json), with an option to set a new password if needed.

## To run:

Use run.sh. Alternatively,

1. `javac -cp .:lib/json-20210307.jar -d bin src/java/main/*.java`

2. `java -cp .:lib/json-20210307.jar:bin JournalApp`