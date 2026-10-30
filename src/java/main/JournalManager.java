import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
/**
 * The {@code JournalManager} class manages a collection of journal entries, allowing for 
 * adding, loading, and saving entries. Entries are stored in a JSON format within a 
 * specified file. 
 */
public class JournalManager {
    private static final Logger logger = Logger.getLogger(JournalManager.class.getName());
    private final List<JournalEntry> entries;
    private final String filePath;

    /**
     * Constructs a {@code JournalManager} with the specified file path.
     *
     * @param filePath the path to the file where journal entries are stored
     */
    public JournalManager(String filePath) {
        this.filePath = filePath;
        entries = new ArrayList<>();
    }

    /**
     * Retrieves the list of journal entries.
     *
     * @return a {@code List} of {@code JournalEntry} objects
     */
    public List<JournalEntry> getEntries() {
        return entries;
    }

    /**
     * Adds a new journal entry to the collection.
     *
     * @param entry the {@code JournalEntry} to add
     */
    public void addEntry(JournalEntry entry) {
        entries.add(entry);
    }

    /**
     * Loads journal entries from the file specified by the file path,
     * replacing the current entries. If the file does not exist no action is
     * taken. Entries are only replaced once the whole file parsed; a file that
     * can't be parsed is renamed to {@code *.corrupt-<millis>} so the next
     * save can't overwrite the user's journal with an empty list.
     *
     * @return the path the unreadable file was moved to, or {@code null} if
     *         the load succeeded (or there was nothing to load)
     */
    public java.nio.file.Path loadEntries() {
        entries.clear();
        if (!new File(filePath).exists()) {
            return null;
        }
        try {
            JSONArray jsonArr = new JSONArray(JsonFiles.read(filePath));
            List<JournalEntry> loaded = new ArrayList<>();
            for (int i = 0; i < jsonArr.length(); i++) {
                loaded.add(JournalEntry.fromJson(jsonArr.getJSONObject(i)));
            }
            entries.addAll(loaded);
            return null;
        } catch (Exception e) {
            java.nio.file.Path backup = JsonFiles.quarantine(filePath);
            logger.severe(String.format("Error loading journal entries from file: %s - %s (kept as %s)",
                    filePath, e.getMessage(), backup));
            return backup;
        }
    }

    /**
     * Saves the current journal entries to the file specified by the file path.
     * Entries are saved in JSON format, atomically.
     */
    public void saveEntries() {
        JSONArray jsonArr = new JSONArray();
        for (JournalEntry entry : entries) {
            jsonArr.put(entry.toJson());
        }
        try {
            JsonFiles.writeAtomically(filePath, jsonArr.toString(4));
        } catch (Exception e) {
            logger.severe(String.format("Error saving journal entries to file: %s - %s", filePath, e.getMessage()));
        }
    }
}