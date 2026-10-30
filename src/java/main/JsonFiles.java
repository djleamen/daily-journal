import java.io.IOException;
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
     * journal written on macOS reads back correctly on Windows).
     */
    static String read(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
    }

    /**
     * Writes {@code content} as UTF-8 to a temp file next to the target and
     * renames it into place, so a crash or full disk mid-write leaves the
     * previous file intact instead of a truncated one.
     */
    static void writeAtomically(String filePath, String content) throws IOException {
        Path target = Paths.get(filePath).toAbsolutePath();
        Path tmp = target.resolveSibling(target.getFileName() + ".tmp");
        Files.write(tmp, content.getBytes(StandardCharsets.UTF_8));
        try {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Renames an unreadable file to {@code <name>.corrupt-<millis>}.
     *
     * @return the backup path, or {@code null} if it could not be moved
     */
    static Path quarantine(String filePath) {
        Path source = Paths.get(filePath);
        Path backup = source.resolveSibling(source.getFileName() + ".corrupt-" + System.currentTimeMillis());
        try {
            return Files.move(source, backup);
        } catch (IOException e) {
            return null;
        }
    }
}
