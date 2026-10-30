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
            return new String(bytes, legacy);
        }
    }

    /**
     * Reads a file that must hold one complete JSON array. The hand-rolled
     * {@link JSONArray} parser returns an empty array for malformed input, so
     * a truncated file (e.g. {@code [{"title": ...}) is rejected here instead
     * of loading as "no entries" and being overwritten by the next save.
     */
    static JSONArray readArray(String filePath) throws IOException {
        String text = read(filePath).trim();
        if (!text.startsWith("[") || !text.endsWith("]")) {
            throw new IOException("Not a complete JSON array (truncated or corrupt)");
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
