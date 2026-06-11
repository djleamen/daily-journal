import java.util.ArrayList;
import java.util.List;

/**
 * A simple JSONArray implementation for handling JSON array operations.
 */
public class JSONArray {
    private List<Object> list;

    /**
     * Constructs an empty JSONArray.
     */
    public JSONArray() {
        this.list = new ArrayList<>();
    }

    /**
     * Constructs a JSONArray from a JSON string representation.
     *
     * @param jsonString the JSON string to parse
     */
    public JSONArray(String jsonString) {
        this();
        parseFromString(jsonString);
    }

    /**
     * Constructs a JSONArray from a List of objects.
     *
     * @param list the list to convert to JSONArray
     */
    public JSONArray(List<?> list) {
        this.list = new ArrayList<>(list);
    }

    /**
     * Returns the number of elements in this JSONArray.
     *
     * @return the number of elements
     */
    public int length() {
        return list.size();
    }

    /**
     * Gets the string value at the specified index.
     *
     * @param index the index of the element
     * @return the string value at the specified index
     */
    public String getString(int index) {
        Object obj = list.get(index);
        return obj != null ? obj.toString() : null;
    }

    /**
     * Gets the JSONObject at the specified index.
     *
     * @param index the index of the element
     * @return the JSONObject at the specified index
     * @throws ClassCastException if the element at the index is not a JSONObject
     */
    public JSONObject getJSONObject(int index) {
        Object obj = list.get(index);
        if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        }
        // If it's a string representation, try to parse it
        if (obj != null) {
            return new JSONObject(obj.toString());
        }
        return null;
    }

    /**
     * Adds an object to the end of this JSONArray.
     *
     * @param value the object to add
     */
    public void put(Object value) {
        list.add(value);
    }

    /**
     * Returns a string representation of this JSONArray.
     *
     * @return a JSON string representation
     */
    @Override
    public String toString() {
        return toString(0);
    }

    /**
     * Returns a string representation of this JSONArray with specified indentation.
     *
     * @param indentFactor the number of spaces to add to each level of indentation
     * @return a formatted JSON string representation
     */
    public String toString(int indentFactor) {
        if (list.isEmpty()) {
            return "[]";
        }

        return indentFactor > 0 ? formatWithIndentation(indentFactor) : formatCompact();
    }

    /**
     * Formats the JSONArray with indentation.
     *
     * @param indentFactor the number of spaces for indentation
     * @return formatted string with indentation
     */
    private String formatWithIndentation(int indentFactor) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < list.size(); i++) {
            appendIndentedElement(sb, indentFactor, i);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Formats the JSONArray in compact format.
     *
     * @return compact formatted string
     */
    private String formatCompact() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            appendCompactElement(sb, i);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Appends an element with indentation.
     *
     * @param sb the StringBuilder
     * @param indentFactor the indentation factor
     * @param index the element index
     */
    private void appendIndentedElement(StringBuilder sb, int indentFactor, int index) {
        for (int j = 0; j < indentFactor; j++) {
            sb.append(" ");
        }
        sb.append(valueToString(list.get(index)));
        if (index < list.size() - 1) {
            sb.append(",");
        }
        sb.append("\n");
    }

    /**
     * Appends an element in compact format.
     *
     * @param sb the StringBuilder
     * @param index the element index
     */
    private void appendCompactElement(StringBuilder sb, int index) {
        sb.append(valueToString(list.get(index)));
        if (index < list.size() - 1) {
            sb.append(",");
        }
    }

    /**
     * Converts a value to its JSON string representation. Nested
     * {@code JSONObject}, {@code JSONArray}, and {@code List} values are
     * emitted as JSON structures rather than quoted strings so they can be
     * parsed back; everything else is quoted and escaped.
     *
     * @param value the value to convert
     * @return the JSON representation of the value
     */
    private String valueToString(Object value) {
        if (value instanceof JSONObject) {
            return ((JSONObject) value).toString();
        }
        if (value instanceof JSONArray) {
            return ((JSONArray) value).toString();
        }
        if (value instanceof List) {
            return new JSONArray((List<?>) value).toString();
        }
        return "\"" + escapeString(value != null ? value.toString() : "") + "\"";
    }

    /**
     * Parses a JSON string and populates this JSONArray.
     *
     * @param jsonString the JSON string to parse
     */
    private void parseFromString(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return;
        }

        String trimmed = jsonString.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            return;
        }

        String content = trimmed.substring(1, trimmed.length() - 1).trim();
        if (content.isEmpty()) {
            return;
        }

        for (String element : splitTopLevel(content)) {
            list.add(parseValue(element.trim()));
        }
    }

    /**
     * Parses a single JSON value: nested objects and arrays are parsed
     * recursively, quoted strings are unescaped, anything else is kept as-is.
     *
     * @param element the trimmed value text
     * @return the parsed value
     */
    private Object parseValue(String element) {
        if (element.startsWith("{")) {
            return new JSONObject(element);
        }
        if (element.startsWith("[")) {
            return new JSONArray(element);
        }
        if (element.length() >= 2 && element.startsWith("\"") && element.endsWith("\"")) {
            return unescapeString(element.substring(1, element.length() - 1));
        }
        return element;
    }

    /**
     * Splits JSON content on commas that are not inside nested objects,
     * arrays, or quoted strings.
     *
     * @param content the content between the outer brackets
     * @return the top-level elements
     */
    private List<String> splitTopLevel(String content) {
        List<String> elements = new ArrayList<>();
        int depth = 0;
        boolean inString = false;
        int start = 0;
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (inString) {
                if (c == '\\') {
                    i++;
                } else if (c == '"') {
                    inString = false;
                }
            } else if (c == '"') {
                inString = true;
            } else if (c == '{' || c == '[') {
                depth++;
            } else if (c == '}' || c == ']') {
                depth--;
            } else if (c == ',' && depth == 0) {
                elements.add(content.substring(start, i));
                start = i + 1;
            }
        }
        elements.add(content.substring(start));
        return elements;
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
