import java.util.HashMap;
import java.util.Map;

/**
 * A simple JSONObject implementation for handling JSON object operations.
 */
public class JSONObject {
    private Map<String, Object> map;

    /**
     * Constructs an empty JSONObject.
     */
    public JSONObject() {
        this.map = new HashMap<>();
    }

    /**
     * Constructs a JSONObject from a JSON string representation.
     *
     * @param jsonString the JSON string to parse
     */
    public JSONObject(String jsonString) {
        this();
        parseFromString(jsonString);
    }

    /**
     * Gets the string value associated with the specified key.
     *
     * @param key the key whose associated value is to be returned
     * @return the string value associated with the key, or null if not found
     */
    public String getString(String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Gets the JSONArray associated with the specified key.
     *
     * @param key the key whose associated value is to be returned
     * @return the JSONArray associated with the key, or null if not found
     */
    public JSONArray getJSONArray(String key) {
        Object value = map.get(key);
        if (value instanceof JSONArray jsonArray) {
            return jsonArray;
        }
        // If it's a string representation, try to parse it
        if (value != null) {
            return new JSONArray(value.toString());
        }
        return null;
    }

    /**
     * Associates the specified value with the specified key in this JSONObject.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    public void put(String key, Object value) {
        map.put(key, value);
    }

    /**
     * Returns a string representation of this JSONObject.
     *
     * @return a JSON string representation
     */
    @Override
    public String toString() {
        return toString(0);
    }

    /**
     * Returns a string representation of this JSONObject with specified indentation.
     *
     * @param indentFactor the number of spaces to add to each level of indentation
     * @return a formatted JSON string representation
     */
    public String toString(int indentFactor) {
        if (map.isEmpty()) {
            return "{}";
        }

        return indentFactor > 0 ? formatWithIndentation(indentFactor) : formatCompact();
    }

    /**
     * Formats the JSON object with indentation.
     *
     * @param indentFactor the number of spaces for indentation
     * @return formatted JSON string with indentation
     */
    private String formatWithIndentation(int indentFactor) {
        StringBuilder sb = new StringBuilder("{\n");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                sb.append(",\n");
            }
            appendIndentation(sb, indentFactor);
            appendKeyValuePair(sb, entry, true);
            first = false;
        }
        sb.append("\n}");
        return sb.toString();
    }

    /**
     * Formats the JSON object in compact form.
     *
     * @return compact JSON string
     */
    private String formatCompact() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            appendKeyValuePair(sb, entry, false);
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Appends indentation spaces to the string builder.
     *
     * @param sb the string builder
     * @param indentFactor the number of spaces to add
     */
    private void appendIndentation(StringBuilder sb, int indentFactor) {
        for (int i = 0; i < indentFactor; i++) {
            sb.append(" ");
        }
    }

    /**
     * Appends a key-value pair to the string builder.
     *
     * @param sb the string builder
     * @param entry the map entry containing key and value
     * @param withSpaces whether to include spaces around the colon
     */
    private void appendKeyValuePair(StringBuilder sb, Map.Entry<String, Object> entry, boolean withSpaces) {
        sb.append("\"").append(escapeString(entry.getKey())).append("\"");
        sb.append(withSpaces ? ": " : ":");
        sb.append("\"").append(escapeString(entry.getValue().toString())).append("\"");
    }

    /**
     * Parses a JSON string and populates this JSONObject.
     *
     * @param jsonString the JSON string to parse
     */
    private void parseFromString(String jsonString) {
        String content = extractJsonContent(jsonString);
        if (content == null) {
            return;
        }

        String[] pairs = content.split(",");
        for (String pair : pairs) {
            parseKeyValuePair(pair);
        }
    }

    /**
     * Extracts the content from a JSON string, validating format.
     *
     * @param jsonString the JSON string to extract content from
     * @return the content between braces, or null if invalid
     */
    private String extractJsonContent(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }

        String trimmed = jsonString.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            return null;
        }

        String content = trimmed.substring(1, trimmed.length() - 1).trim();
        return content.isEmpty() ? null : content;
    }

    /**
     * Parses a single key-value pair and adds it to the map.
     *
     * @param pair the key-value pair string to parse
     */
    private void parseKeyValuePair(String pair) {
        String[] keyValue = pair.split(":", 2);
        if (keyValue.length != 2) {
            return;
        }

        String key = removeQuotes(keyValue[0].trim());
        String value = removeQuotes(keyValue[1].trim());
        
        key = unescapeString(key);
        value = unescapeString(value);
        map.put(key, value);
    }

    /**
     * Removes surrounding quotes from a string if present.
     *
     * @param str the string to process
     * @return the string without surrounding quotes
     */
    private String removeQuotes(String str) {
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    /**
     * Escapes special characters in a string for JSON representation.
     *
     * @param str the string to escape
     * @return the escaped string
     */
    private String escapeString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * Unescapes special characters in a string from JSON representation.
     *
     * @param str the string to unescape
     * @return the unescaped string
     */
    private String unescapeString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\\"", "\"")
                  .replace("\\\\", "\\")
                  .replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t");
    }
}
