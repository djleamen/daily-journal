import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * File helpers shared by the managers that persist JSON: UTF-8 reads,
 * crash-safe writes, and setting aside files that can no longer be parsed so
 * the next save can't overwrite them.
 */
final class JsonFiles {

    private JsonFiles() {
        // utility class
    }

    /**
     * Reads the whole file as UTF-8 (independent of the platform charset, so a
     * journal written on macOS reads back correctly on Windows). Bytes that
     * aren't valid UTF-8 are never silently replaced: a file written by an
     * older version in a non-UTF-8 platform charset is decoded with that
     * charset, otherwise the read fails so the caller can preserve the file.
     */
    static String read(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes))
                    .toString();
        } catch (CharacterCodingException e) {
            Charset legacy = Charset.defaultCharset();
            if (legacy.equals(StandardCharsets.UTF_8)) {
                throw new IOException("File is not valid UTF-8", e);
            }
            // Decode the legacy charset with reporting too: a raw byte that is
            // invalid in both UTF-8 and the platform charset (e.g. 0x81 under
            // windows-1252) must reach the caller's preservation path, not be
            // silently turned into U+FFFD and then overwritten on the next save.
            try {
                return legacy.newDecoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT)
                        .decode(ByteBuffer.wrap(bytes))
                        .toString();
            } catch (CharacterCodingException legacyFailed) {
                throw new IOException("File is not valid UTF-8 or " + legacy, legacyFailed);
            }
        }
    }

    /**
     * Reads a file that must hold one complete, well-formed JSON array. The
     * hand-rolled {@link JSONArray} parser is lenient (it returns an empty
     * array for malformed input and silently drops members with a missing
     * colon or merges objects with a missing comma), so the text is first
     * validated strictly by {@link JsonText#validate}. A truncated or
     * otherwise corrupt file is rejected here instead of loading as partial
     * data and being overwritten by the next save.
     */
    static JSONArray readArray(String filePath) throws IOException {
        String text = read(filePath).trim();
        if (!text.startsWith("[")) {
            throw new IOException("Not a JSON array (truncated or corrupt)");
        }
        try {
            JsonText.validate(text);
        } catch (IllegalArgumentException e) {
            throw new IOException("Not a complete, well-formed JSON array (truncated or corrupt): "
                    + e.getMessage(), e);
        }
        return new JSONArray(text);
    }

    /**
     * Writes {@code content} as UTF-8 to a unique temp file next to the target
     * and renames it into place, so a crash or full disk mid-write leaves the
     * previous file intact instead of a truncated one. The temp file is
     * created owner-only (0600 on POSIX), so a private journal never becomes
     * readable by other local users.
     */
    static void writeAtomically(String filePath, String content) throws IOException {
        Path target = Paths.get(filePath).toAbsolutePath();
        Path tmp = Files.createTempFile(target.getParent(), target.getFileName() + ".", ".tmp");
        try {
            Files.write(tmp, content.getBytes(StandardCharsets.UTF_8));
            try {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    /**
     * Renames an unreadable file to {@code <name>.corrupt-<millis>}.
     *
     * @return the backup path
     * @throws IOException if the file could not be moved; callers must then
     *         stop saving over it
     */
    static Path quarantine(String filePath) throws IOException {
        Path source = Paths.get(filePath);
        Path backup = source.resolveSibling(source.getFileName() + ".corrupt-" + System.currentTimeMillis());
        return Files.move(source, backup);
    }
}
