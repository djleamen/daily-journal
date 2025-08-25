import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code TagsManager class is responsible for managing a list of tags.
 * It provides functionality to add, load, and save tags to a file.
 * Tags are stored in a JSON array format within the specified file.
 * <p>
 * This class is part of a larger application that manages journal entries
 * and allows users to tag their entries for better organization and retrieval.
 */

public class TagsManager {
    private static final Logger logger = Logger.getLogger(TagsManager.class.getName());
    private final List<String> tags;
    private final String filePath;

    /**
     * Constructs a {@code TagsManager} with the specified file path.
     *
     * @param filePath the path to the file where tags are stored
     */
    public TagsManager(String filePath) {
        this.filePath = filePath;
        this.tags = new ArrayList<>();
    }

    /**
     * Returns the list of tags.
     *
     * @return a {@code List<String>} containing the tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Adds a new tag to the list if it does not already exist and saves the tags to the file.
     *
     * @param tag the tag to be added
     */
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            saveTags();
        }
    }

    /**
     * Loads tags from the file into the list. If the file does not exist, the list remains empty.
     * <p>
     * This method clears the current list of tags before loading new ones from the file.
     */
    public void loadTags() {
        tags.clear();
        File file = new File(filePath);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonStr = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStr.append(line);
            }
            JSONArray jsonArr = new JSONArray(jsonStr.toString());
            for (int i = 0; i < jsonArr.length(); i++) {
                tags.add(jsonArr.getString(i));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, () -> "Error loading tags from file: " + filePath + " - " + e.getMessage());
        }
    }

    /**
     * Saves the current list of tags to the file in JSON array format.
     * <p>
     * If an error occurs during the save operation, the exception is logged.
     */
    public void saveTags() {
        JSONArray jsonArr = new JSONArray(tags);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.write(jsonArr.toString(4));
        } catch (Exception e) {
            logger.log(Level.SEVERE, () -> "Error saving tags to file: " + filePath + " - " + e.getMessage());
        }
    }
}