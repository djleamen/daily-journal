import java.util.ArrayList;
import java.util.List;

/**
 * Shared text-level helpers for the simple {@link JSONObject} and
 * {@link JSONArray} implementations: top-level splitting, value
 * parsing/serialization, and string escaping.
 */
final class JsonText {

    private JsonText() {
        // utility class
    }

    /**
     * Splits JSON content on commas that are not inside nested objects,
     * arrays, or quoted strings.
     *
     * @param content the content between the outer braces or brackets
     * @return the top-level element strings
     */
    static List<String> splitTopLevel(String content) {
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
     * Parses a single JSON value: nested objects and arrays are parsed
     * recursively, quoted strings are unescaped, anything else is kept as-is.
     *
     * @param element the trimmed value text
     * @return the parsed value
     */
    static Object parseValue(String element) {
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
     * Converts a value to its JSON string representation. Nested
     * {@code JSONObject}, {@code JSONArray}, and {@code List} values are
     * emitted as JSON structures rather than quoted strings so they can be
     * parsed back; everything else is quoted and escaped.
     *
     * @param value the value to convert
     * @return the JSON representation of the value
     */
    static String valueToString(Object value) {
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
     * Escapes special characters in a string for JSON representation.
     *
     * @param str the string to escape
     * @return the escaped string
     */
    static String escapeString(String str) {
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
    static String unescapeString(String str) {
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
