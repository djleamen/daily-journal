import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class JournalEntryTest {

    @Test
    void constructorStoresValues() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        List<String> tags = Arrays.asList("work", "ideas");
        JournalEntry entry = new JournalEntry("Title", date, "Toronto", tags, "content");

        assertEquals("Title", entry.getTitle());
        assertEquals(date, entry.getDate());
        assertEquals("Toronto", entry.getLocation());
        assertEquals(tags, entry.getTags());
        assertEquals("content", entry.getContent());
    }

    @Test
    void constructorRejectsNullDate() {
        assertThrows(IllegalArgumentException.class,
                () -> new JournalEntry("t", null, "loc", Collections.emptyList(), "c"));
    }

    @Test
    void constructorAcceptsNullTags() {
        JournalEntry entry = new JournalEntry("t", LocalDate.now(), "loc", null, "c");
        assertNotNull(entry.getTags());
        assertTrue(entry.getTags().isEmpty());
    }

    @Test
    void toJsonContainsAllFields() {
        JournalEntry entry = new JournalEntry("T", LocalDate.of(2024, 5, 1),
                "L", Arrays.asList("a"), "body");
        JSONObject json = entry.toJson();
        assertEquals("T", json.getString("title"));
        assertEquals("2024-05-01", json.getString("date"));
        assertEquals("L", json.getString("location"));
        assertEquals("body", json.getString("content"));
        assertNotNull(json.getJSONArray("tags"));
    }

    @Test
    void toJsonHandlesNullStringFields() {
        JournalEntry entry = new JournalEntry(null, LocalDate.of(2024, 5, 1), null, null, null);
        JSONObject json = entry.toJson();
        assertEquals("", json.getString("title"));
        assertEquals("", json.getString("location"));
        assertEquals("", json.getString("content"));
    }

    @Test
    void fromJsonRoundTrip() {
        JournalEntry original = new JournalEntry("Title", LocalDate.of(2024, 5, 1),
                "Toronto", Arrays.asList("a", "b"), "content");
        JournalEntry parsed = JournalEntry.fromJson(original.toJson());

        assertEquals(original.getTitle(), parsed.getTitle());
        assertEquals(original.getDate(), parsed.getDate());
        assertEquals(original.getLocation(), parsed.getLocation());
        assertEquals(original.getContent(), parsed.getContent());
        assertEquals(original.getTags(), parsed.getTags());
    }

    @Test
    void fromJsonRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> JournalEntry.fromJson(null));
    }

    @Test
    void fromJsonRequiresDate() {
        JSONObject json = new JSONObject();
        json.put("title", "T");
        assertThrows(IllegalArgumentException.class, () -> JournalEntry.fromJson(json));
    }
}
