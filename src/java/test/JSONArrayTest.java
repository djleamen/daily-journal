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
}
