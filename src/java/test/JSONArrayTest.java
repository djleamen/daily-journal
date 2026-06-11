import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class JSONArrayTest {

    @Test
    void emptyArrayToString() {
        JSONArray arr = new JSONArray();
        assertEquals(0, arr.length());
        assertEquals("[]", arr.toString());
        assertEquals("[]", arr.toString(4));
    }

    @Test
    void putAndGetString() {
        JSONArray arr = new JSONArray();
        arr.put("hello");
        arr.put("world");
        assertEquals(2, arr.length());
        assertEquals("hello", arr.getString(0));
        assertEquals("world", arr.getString(1));
    }

    @Test
    void constructFromList() {
        JSONArray arr = new JSONArray(Arrays.asList("a", "b", "c"));
        assertEquals(3, arr.length());
        assertEquals("b", arr.getString(1));
    }

    @Test
    void parseFromJsonString() {
        JSONArray arr = new JSONArray("[\"x\",\"y\"]");
        assertEquals(2, arr.length());
        assertEquals("x", arr.getString(0));
    }

    @Test
    void parseHandlesInvalidInput() {
        assertEquals(0, new JSONArray((String) null).length());
        assertEquals(0, new JSONArray("").length());
        assertEquals(0, new JSONArray("not-array").length());
        assertEquals(0, new JSONArray("[]").length());
    }

    @Test
    void compactAndIndentedFormatDiffer() {
        JSONArray arr = new JSONArray(Arrays.asList("a", "b"));
        assertFalse(arr.toString(0).contains("\n"));
        assertTrue(arr.toString(2).contains("\n"));
    }

    @Test
    void getJsonObjectFromString() {
        JSONArray arr = new JSONArray();
        arr.put("{\"k\":\"v\"}");
        JSONObject obj = arr.getJSONObject(0);
        assertNotNull(obj);
        assertEquals("v", obj.getString("k"));
    }

    @Test
    void getJsonObjectFromActualObject() {
        JSONObject inner = new JSONObject();
        inner.put("k", "v");
        JSONArray arr = new JSONArray(Collections.singletonList(inner));
        assertEquals("v", arr.getJSONObject(0).getString("k"));
    }

    @Test
    void escapeRoundTripViaParse() {
        JSONArray arr = new JSONArray(Collections.singletonList("a\"b\nc"));
        JSONArray parsed = new JSONArray(arr.toString());
        assertEquals("a\"b\nc", parsed.getString(0));
    }

    @Test
    void nestedObjectsRoundTripThroughArray() {
        JSONObject obj = new JSONObject();
        obj.put("title", "Trip, part 2");
        obj.put("tags", java.util.Arrays.asList("travel", "fun"));
        JSONArray array = new JSONArray();
        array.put(obj);

        JSONArray reparsed = new JSONArray(array.toString());
        assertEquals(1, reparsed.length());
        JSONObject back = reparsed.getJSONObject(0);
        assertEquals("Trip, part 2", back.getString("title"));
        JSONArray tags = back.getJSONArray("tags");
        assertEquals(2, tags.length());
        assertEquals("travel", tags.getString(0));
    }

    @Test
    void nestedArraysParseRecursively() {
        JSONArray array = new JSONArray("[[\"a\",\"b\"],[\"c\"]]");
        assertEquals(2, array.length());
        assertEquals("[\"a\",\"b\"]", array.getString(0).replace(" ", ""));
    }

    @Test
    void escapedQuotesAndCommasSurviveRoundTrip() {
        JSONArray array = new JSONArray();
        array.put("say \"hi\", then leave");
        JSONArray reparsed = new JSONArray(array.toString());
        assertEquals("say \"hi\", then leave", reparsed.getString(0));
    }

    @Test
    void indentedNestedOutputRoundTrips() {
        JSONObject obj = new JSONObject();
        obj.put("k", "v");
        JSONArray array = new JSONArray();
        array.put(obj);
        JSONArray reparsed = new JSONArray(array.toString(2));
        assertEquals(1, reparsed.length());
        assertEquals("v", reparsed.getJSONObject(0).getString("k"));
    }
}
