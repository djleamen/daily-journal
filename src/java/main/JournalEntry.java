import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code JournalEntry} class represents a single entry in the journal.
 * It includes details such as the title, date, location, tags, and content
 * of the journal entry, and provides methods for JSON serialization and deserialization.
 */
public class JournalEntry {
    private final String title;
    private final LocalDate date;
    private final String location;
    private final List<String> tags;
    private final String content;

    /**
     * Constructs a new {@code JournalEntry}.
     *
     * @param title    the title of the journal entry
     * @param date     the date of the journal entry
     * @param location the location associated with the journal entry
     * @param tags     a list of tags for the journal entry
     * @param content  the content of the journal entry
     * @throws IllegalArgumentException if date is null
     */
    public JournalEntry(String title, LocalDate date, String location, List<String> tags, String content) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.title = title;
        this.date = date;
        this.location = location;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
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
        json.put("title", title != null ? title : "");
        json.put("date", date != null ? date.toString() : "");
        json.put("location", location != null ? location : "");
        json.put("tags", tags != null ? tags : new ArrayList<>());
        json.put("content", content != null ? content : "");
        return json;
    }

    /**
     * Creates a {@code JournalEntry} object from a JSON object.
     *
     * @param json the {@code JSONObject} representing a journal entry
     * @return a {@code JournalEntry} object created from the JSON data
     * @throws IllegalArgumentException if json is null or missing required fields
     */
    public static JournalEntry fromJson(JSONObject json) {
        if (json == null) {
            throw new IllegalArgumentException("JSON object cannot be null");
        }
        
        String title = json.getString("title");
        String dateString = json.getString("date");
        if (dateString == null) {
            throw new IllegalArgumentException("Date field is required");
        }
        LocalDate date = LocalDate.parse(dateString);
        String location = json.getString("location");
        List<String> tags = new ArrayList<>();
        JSONArray tagsArray = json.getJSONArray("tags");
        if (tagsArray != null) {
            for (int i = 0; i < tagsArray.length(); i++) {
                String tag = tagsArray.getString(i);
                if (tag != null) {
                    tags.add(tag);
                }
            }
        }
        String content = json.getString("content");
        return new JournalEntry(title, date, location, tags, content);
    }
}