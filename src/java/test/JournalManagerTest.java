import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JournalManagerTest {

    @Test
    void addAndRetrieveEntry(@TempDir Path tempDir) {
        File file = tempDir.resolve("entries.json").toFile();
        JournalManager mgr = new JournalManager(file.getAbsolutePath());
        JournalEntry entry = new JournalEntry("Title", LocalDate.of(2024, 1, 1),
                "Toronto", Arrays.asList("a"), "content");
        mgr.addEntry(entry);

        assertEquals(1, mgr.getEntries().size());
        assertSame(entry, mgr.getEntries().get(0));
    }

    @Test
    void saveEntriesWritesFile(@TempDir Path tempDir) throws Exception {
        File file = tempDir.resolve("entries.json").toFile();
        JournalManager mgr = new JournalManager(file.getAbsolutePath());
        mgr.addEntry(new JournalEntry("First", LocalDate.of(2024, 1, 1),
                "L1", Arrays.asList("t1"), "c1"));
        mgr.saveEntries();

        assertTrue(file.exists());
        String contents = new String(Files.readAllBytes(file.toPath()));
        assertTrue(contents.contains("First"));
        assertTrue(contents.contains("2024-01-01"));
    }

    @Test
    void saveEntriesWritesEmptyArrayWhenNoEntries(@TempDir Path tempDir) throws Exception {
        File file = tempDir.resolve("entries.json").toFile();
        JournalManager mgr = new JournalManager(file.getAbsolutePath());
        mgr.saveEntries();

        assertTrue(file.exists());
        String contents = new String(Files.readAllBytes(file.toPath())).trim();
        assertEquals("[]", contents);
    }

    @Test
    void loadFromMissingFileLeavesEmpty(@TempDir Path tempDir) {
        File file = tempDir.resolve("missing.json").toFile();
        JournalManager mgr = new JournalManager(file.getAbsolutePath());
        mgr.loadEntries();
        assertTrue(mgr.getEntries().isEmpty());
    }

    @Test
    void loadEmptyArrayFileLeavesEmpty(@TempDir Path tempDir) throws Exception {
        File file = tempDir.resolve("entries.json").toFile();
        Files.write(file.toPath(), "[]".getBytes());

        JournalManager mgr = new JournalManager(file.getAbsolutePath());
        mgr.addEntry(new JournalEntry("Transient", LocalDate.of(2024, 2, 2),
                "L", Arrays.asList(), "c"));
        mgr.loadEntries();

        assertTrue(mgr.getEntries().isEmpty());
    }
}
