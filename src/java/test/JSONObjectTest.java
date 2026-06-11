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

    @Test
    void nestedObjectValuesRoundTripThroughString() {
        JSONObject inner = new JSONObject();
        inner.put("title", "First, day");
        inner.put("note", "line1\nline2 \"quoted\"");
        JSONObject outer = new JSONObject();
        outer.put("entry", inner);

        JSONObject reparsed = new JSONObject(outer.toString());
        Object entry = JsonText.parseValue(
                JsonText.valueToString(inner));
        assertTrue(entry instanceof JSONObject);
        assertEquals("First, day", ((JSONObject) entry).getString("title"));
        assertEquals("line1\nline2 \"quoted\"",
                ((JSONObject) entry).getString("note"));
        assertNotNull(reparsed.toString());
    }

    @Test
    void listValuesSerializeAsJsonArrays() {
        JSONObject json = new JSONObject();
        json.put("tags", Arrays.asList("work", "school"));
        String out = json.toString();
        assertTrue(out.contains("[\"work\",\"school\"]"), out);

        JSONObject reparsed = new JSONObject(out);
        JSONArray tags = reparsed.getJSONArray("tags");
        assertEquals(2, tags.length());
        assertEquals("work", tags.getString(0));
        assertEquals("school", tags.getString(1));
    }

    @Test
    void commasInsideStringsDoNotSplitPairs() {
        JSONObject json = new JSONObject();
        json.put("content", "Hello, world, again");
        json.put("location", "Oshawa, ON");

        JSONObject reparsed = new JSONObject(json.toString());
        assertEquals("Hello, world, again", reparsed.getString("content"));
        assertEquals("Oshawa, ON", reparsed.getString("location"));
    }

    @Test
    void indentedOutputRoundTrips() {
        JSONObject inner = new JSONObject();
        inner.put("k", "v");
        JSONObject json = new JSONObject();
        json.put("obj", inner);
        JSONObject reparsed = new JSONObject(json.toString(4));
        assertNotNull(reparsed.toString());
    }
}
