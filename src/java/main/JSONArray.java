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
        if (obj instanceof JSONObject jsonObject) {
            return jsonObject;
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
        sb.append("\"").append(escapeString(list.get(index).toString())).append("\"");
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
        sb.append("\"").append(escapeString(list.get(index).toString())).append("\"");
        if (index < list.size() - 1) {
            sb.append(",");
        }
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

        // Simple parsing for string arrays
        String[] elements = content.split(",");
        for (String element : elements) {
            String trimmedElement = element.trim();
            if (trimmedElement.startsWith("\"") && trimmedElement.endsWith("\"")) {
                // Remove quotes and unescape
                String value = trimmedElement.substring(1, trimmedElement.length() - 1);
                value = unescapeString(value);
                list.add(value);
            } else {
                list.add(trimmedElement);
            }
        }
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
