import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TagsManagerTest {

    @Test
    void addTagAvoidsDuplicates(@TempDir Path tempDir) {
        File file = tempDir.resolve("tags.json").toFile();
        TagsManager mgr = new TagsManager(file.getAbsolutePath());

        mgr.addTag("work");
        mgr.addTag("work");
        mgr.addTag("ideas");

        assertEquals(2, mgr.getTags().size());
        assertTrue(mgr.getTags().contains("work"));
        assertTrue(mgr.getTags().contains("ideas"));
    }

    @Test
    void saveAndLoadRoundTrip(@TempDir Path tempDir) {
        File file = tempDir.resolve("tags.json").toFile();
        TagsManager mgr = new TagsManager(file.getAbsolutePath());
        mgr.addTag("alpha");
        mgr.addTag("beta");

        TagsManager reload = new TagsManager(file.getAbsolutePath());
        reload.loadTags();

        assertEquals(2, reload.getTags().size());
        assertTrue(reload.getTags().contains("alpha"));
        assertTrue(reload.getTags().contains("beta"));
    }

    @Test
    void loadFromMissingFileLeavesEmpty(@TempDir Path tempDir) {
        File file = tempDir.resolve("missing.json").toFile();
        TagsManager mgr = new TagsManager(file.getAbsolutePath());
        mgr.loadTags();
        assertTrue(mgr.getTags().isEmpty());
    }

    @Test
    void loadClearsExistingTagsBeforeReading(@TempDir Path tempDir) throws Exception {
        File file = tempDir.resolve("tags.json").toFile();
        TagsManager seeder = new TagsManager(file.getAbsolutePath());
        seeder.addTag("kept");

        TagsManager mgr2 = new TagsManager(file.getAbsolutePath());
        // Seed in-memory tags directly via getTags() to avoid overwriting the file.
        mgr2.getTags().add("transient1");
        mgr2.getTags().add("transient2");
        mgr2.loadTags();

        assertEquals(1, mgr2.getTags().size());
        assertTrue(mgr2.getTags().contains("kept"));
        assertFalse(mgr2.getTags().contains("transient1"));
    }
}
