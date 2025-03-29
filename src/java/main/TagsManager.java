/**
 * The TagsManager class is responsible for managing a list of tags.
 * It provides functionality to add, load, and save tags to a file.
 * Tags are stored in a JSON array format within the specified file.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

public class TagsManager {
    private List<String> tags;
    private String filePath;
    
    public TagsManager(String filePath) {
        this.filePath = filePath;
        tags = new ArrayList<>();
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            saveTags();
        }
    }
    
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
            e.printStackTrace();
        }
    }
    
    public void saveTags() {
        JSONArray jsonArr = new JSONArray(tags);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.write(jsonArr.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}