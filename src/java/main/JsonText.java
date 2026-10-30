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
     * Strictly validates that {@code text} is a single, complete, well-formed
     * JSON document. The lenient {@link JSONObject}/{@link JSONArray} parsers
     * silently drop a member with a missing colon and merge objects with a
     * missing comma, so malformed input would otherwise load as partial data
     * and be overwritten on the next save. This rejects such input so the
     * caller can preserve the original instead.
     * <p>
     * It is deliberately lenient <em>inside</em> strings — raw control
     * characters and unrecognised escapes are accepted — so a journal written
     * by this app's own serializer is never falsely rejected; structure
     * (colons, commas, brackets, string termination, literals, numbers) is
     * validated strictly.
     *
     * @param text the trimmed JSON text
     * @throws IllegalArgumentException if the text is not well-formed JSON
     */
    static void validate(String text) {
        Validator v = new Validator(text);
        v.skipWhitespace();
        v.value();
        v.skipWhitespace();
        if (!v.atEnd()) {
            throw new IllegalArgumentException("Unexpected trailing content at offset " + v.pos());
        }
    }

    /**
     * Recursive-descent JSON structure validator. See {@link #validate(String)}.
     */
    private static final class Validator {
        private final String s;
        private int i;

        Validator(String s) {
            this.s = s;
        }

        int pos() {
            return i;
        }

        boolean atEnd() {
            return i >= s.length();
        }

        void skipWhitespace() {
            while (i < s.length()) {
                char c = s.charAt(i);
                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    i++;
                } else {
                    break;
                }
            }
        }

        private char peek() {
            if (i >= s.length()) {
                throw new IllegalArgumentException("Unexpected end of JSON");
            }
            return s.charAt(i);
        }

        private void expect(char c) {
            if (i >= s.length() || s.charAt(i) != c) {
                throw new IllegalArgumentException("Expected '" + c + "' at offset " + i);
            }
            i++;
        }

        void value() {
            skipWhitespace();
            char c = peek();
            switch (c) {
                case '{': object(); break;
                case '[': array(); break;
                case '"': string(); break;
                case 't': literal("true"); break;
                case 'f': literal("false"); break;
                case 'n': literal("null"); break;
                default:
                    if (c == '-' || (c >= '0' && c <= '9')) {
                        number();
                    } else {
                        throw new IllegalArgumentException("Unexpected character '" + c + "' at offset " + i);
                    }
            }
        }

        private void object() {
            expect('{');
            skipWhitespace();
            if (peek() == '}') {
                i++;
                return;
            }
            while (true) {
                skipWhitespace();
                if (peek() != '"') {
                    throw new IllegalArgumentException("Expected string key at offset " + i);
                }
                string();
                skipWhitespace();
                expect(':');
                value();
                skipWhitespace();
                char c = peek();
                if (c == ',') {
                    i++;
                } else if (c == '}') {
                    i++;
                    return;
                } else {
                    throw new IllegalArgumentException("Expected ',' or '}' at offset " + i);
                }
            }
        }

        private void array() {
            expect('[');
            skipWhitespace();
            if (peek() == ']') {
                i++;
                return;
            }
            while (true) {
                value();
                skipWhitespace();
                char c = peek();
                if (c == ',') {
                    i++;
                } else if (c == ']') {
                    i++;
                    return;
                } else {
                    throw new IllegalArgumentException("Expected ',' or ']' at offset " + i);
                }
            }
        }

        private void string() {
            expect('"');
            while (true) {
                if (i >= s.length()) {
                    throw new IllegalArgumentException("Unterminated string");
                }
                char c = s.charAt(i++);
                if (c == '"') {
                    return;
                }
                if (c == '\\') {
                    if (i >= s.length()) {
                        throw new IllegalArgumentException("Unterminated escape in string");
                    }
                    // Accept any escaped character (lenient, matching unescapeString).
                    i++;
                }
                // Raw control characters are accepted on purpose (lenient).
            }
        }

        private void literal(String lit) {
            if (!s.startsWith(lit, i)) {
                throw new IllegalArgumentException("Invalid literal at offset " + i);
            }
            i += lit.length();
        }

        private void number() {
            int start = i;
            if (i < s.length() && s.charAt(i) == '-') {
                i++;
            }
            // Integer part: a single 0, or a non-zero digit followed by digits
            // (no leading zeros). At least one digit is required.
            if (i >= s.length() || !isDigit(s.charAt(i))) {
                throw new IllegalArgumentException("Invalid number at offset " + start);
            }
            if (s.charAt(i) == '0') {
                i++;
            } else {
                while (i < s.length() && isDigit(s.charAt(i))) {
                    i++;
                }
            }
            // Fraction: '.' must be followed by at least one digit.
            if (i < s.length() && s.charAt(i) == '.') {
                i++;
                if (i >= s.length() || !isDigit(s.charAt(i))) {
                    throw new IllegalArgumentException("Invalid number (fraction) at offset " + start);
                }
                while (i < s.length() && isDigit(s.charAt(i))) {
                    i++;
                }
            }
            // Exponent: optional sign, then at least one digit.
            if (i < s.length() && (s.charAt(i) == 'e' || s.charAt(i) == 'E')) {
                i++;
                if (i < s.length() && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
                    i++;
                }
                if (i >= s.length() || !isDigit(s.charAt(i))) {
                    throw new IllegalArgumentException("Invalid number (exponent) at offset " + start);
                }
                while (i < s.length() && isDigit(s.charAt(i))) {
                    i++;
                }
            }
        }

        private static boolean isDigit(char c) {
            return c >= '0' && c <= '9';
        }
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
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\\' && i + 1 < str.length()) {
                char next = str.charAt(++i);
                switch (next) {
                    case '"':  sb.append('"');  break;
                    case '\\': sb.append('\\'); break;
                    case 'n':  sb.append('\n'); break;
                    case 'r':  sb.append('\r'); break;
                    case 't':  sb.append('\t'); break;
                    default:   sb.append('\\').append(next); break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
