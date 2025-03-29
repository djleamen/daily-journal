/**
 * The JournalEntry class represents a single entry in the journal.
 * It includes details such as the title, date, location, tags, and content
 * of the journal entry, and provides methods for JSON serialization and deserialization.
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The {@code JournalEntry} class represents a single entry in the journal.
 * It includes details such as the title, date, location, tags, and content
 * of the journal entry, and provides methods for JSON serialization and deserialization.
 */
public class JournalEntry {
    private String title;
    private LocalDate date;
    private String location;
    private List<String> tags;
    private String content;

    /**
     * Constructs a new {@code JournalEntry}.
     *
     * @param title    the title of the journal entry
     * @param date     the date of the journal entry
     * @param location the location associated with the journal entry
     * @param tags     a list of tags for the journal entry
     * @param content  the content of the journal entry
     */
    public JournalEntry(String title, LocalDate date, String location, List<String> tags, String content) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.tags = tags;
        this.content = content;
    }

    /**
     * Gets the title of the journal entry.
     *
     * @return the title of the journal entry
     */
    public String getTitle() { return title; }

    /**
     * Gets the date of the journal entry.
     *
     * @return the date of the journal entry
     */
    public LocalDate getDate() { return date; }

    /**
     * Gets the location associated with the journal entry.
     *
     * @return the location of the journal entry
     */
    public String getLocation() { return location; }

    /**
     * Gets the tags associated with the journal entry.
     *
     * @return a list of tags for the journal entry
     */
    public List<String> getTags() { return tags; }

    /**
     * Gets the content of the journal entry.
     *
     * @return the content of the journal entry
     */
    public String getContent() { return content; }

    /**
     * Converts the journal entry to a JSON object.
     *
     * @return a {@code JSONObject} representing the journal entry
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("title", title);
        json.put("date", date.toString());
        json.put("location", location);
        json.put("tags", tags);
        json.put("content", content);
        return json;
    }

    /**
     * Creates a {@code JournalEntry} object from a JSON object.
     *
     * @param json the {@code JSONObject} representing a journal entry
     * @return a {@code JournalEntry} object created from the JSON data
     */
    public static JournalEntry fromJson(JSONObject json) {
        String title = json.getString("title");
        LocalDate date = LocalDate.parse(json.getString("date"));
        String location = json.getString("location");
        List<String> tags = new ArrayList<>();
        JSONArray tagsArray = json.getJSONArray("tags");
        for (int i = 0; i < tagsArray.length(); i++) {
            tags.add(tagsArray.getString(i));
        }
        String content = json.getString("content");
        return new JournalEntry(title, date, location, tags, content);
    }
}