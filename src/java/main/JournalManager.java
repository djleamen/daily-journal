/**
 * The JournalManager class is responsible for managing a collection of journal entries.
 * It provides functionality to add, load, and save entries to a specified file path.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The {@code JournalManager} class manages a collection of journal entries, allowing for 
 * adding, loading, and saving entries. Entries are stored in a JSON format within a 
 * specified file. 
 */
public class JournalManager {
    private List<JournalEntry> entries;
    private String filePath;

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
     * Loads journal entries from the file specified by the file path.
     * Clears the current entries before loading.
     * If the file does not exist, no action is taken.
     */
    public void loadEntries() {
        entries.clear();
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonStr = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStr.append(line);
            }
            JSONArray jsonArr = new JSONArray(jsonStr.toString());
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                JournalEntry entry = JournalEntry.fromJson(obj);
                entries.add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current journal entries to the file specified by the file path.
     * Entries are saved in JSON format.
     */
    public void saveEntries() {
        JSONArray jsonArr = new JSONArray();
        for (JournalEntry entry : entries) {
            jsonArr.put(entry.toJson());
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.write(jsonArr.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}