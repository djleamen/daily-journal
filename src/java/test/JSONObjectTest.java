import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class JSONObjectTest {

    @Test
    void emptyConstructorProducesEmptyBraces() {
        JSONObject json = new JSONObject();
        assertEquals("{}", json.toString());
        assertEquals("{}", json.toString(4));
    }

    @Test
    void putAndGetString() {
        JSONObject json = new JSONObject();
        json.put("name", "Alice");
        assertEquals("Alice", json.getString("name"));
    }

    @Test
    void getStringReturnsNullForMissingKey() {
        JSONObject json = new JSONObject();
        assertNull(json.getString("missing"));
    }

    @Test
    void parseSimpleJsonString() {
        JSONObject json = new JSONObject("{\"key\":\"value\"}");
        assertEquals("value", json.getString("key"));
    }

    @Test
    void parseHandlesNullAndEmpty() {
        assertEquals("{}", new JSONObject((String) null).toString());
        assertEquals("{}", new JSONObject("").toString());
        assertEquals("{}", new JSONObject("not-json").toString());
        assertEquals("{}", new JSONObject("{}").toString());
    }

    @Test
    void escapeAndUnescapeRoundTripViaParse() {
        JSONObject original = new JSONObject();
        original.put("text", "line1\nline2\t\"quote\"");
        String serialized = original.toString();
        JSONObject parsed = new JSONObject(serialized);
        assertEquals("line1\nline2\t\"quote\"", parsed.getString("text"));
    }

    @Test
    void compactAndIndentedOutputDiffer() {
        JSONObject json = new JSONObject();
        json.put("a", "1");
        json.put("b", "2");
        String compact = json.toString(0);
        String indented = json.toString(4);
        assertTrue(compact.startsWith("{") && compact.endsWith("}"));
        assertTrue(indented.contains("\n"));
    }

    @Test
    void getJsonArrayReturnsArrayInstance() {
        JSONObject json = new JSONObject();
        json.put("tags", new JSONArray(Arrays.asList("a", "b")));
        JSONArray arr = json.getJSONArray("tags");
        assertNotNull(arr);
        assertEquals(2, arr.length());
    }

    @Test
    void getJsonArrayParsesStringRepresentation() {
        JSONObject json = new JSONObject();
        json.put("tags", "[\"a\",\"b\"]");
        JSONArray arr = json.getJSONArray("tags");
        assertNotNull(arr);
        assertEquals(2, arr.length());
    }

    @Test
    void getJsonArrayReturnsNullWhenAbsent() {
        JSONObject json = new JSONObject();
        assertNull(json.getJSONArray("nope"));
    }
}
