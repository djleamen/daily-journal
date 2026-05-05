import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PasswordManagerTest {

    @Test
    void hashIsDeterministicAndNonEmpty() {
        String hash1 = PasswordManager.hashPassword("secret");
        String hash2 = PasswordManager.hashPassword("secret");
        assertEquals(hash1, hash2);
        assertEquals(64, hash1.length()); // SHA-256 hex
        assertNotEquals(hash1, PasswordManager.hashPassword("other"));
    }

    @Test
    void newManagerHasNoPassword(@TempDir Path tempDir) {
        File file = tempDir.resolve("password.json").toFile();
        PasswordManager mgr = new PasswordManager(file.getAbsolutePath());
        assertFalse(mgr.isPasswordSet());
    }

    @Test
    void setAndVerifyPassword(@TempDir Path tempDir) {
        File file = tempDir.resolve("password.json").toFile();
        PasswordManager mgr = new PasswordManager(file.getAbsolutePath());
        mgr.setPassword("hunter2");

        assertTrue(mgr.isPasswordSet());
        assertTrue(mgr.verifyPassword("hunter2"));
        assertFalse(mgr.verifyPassword("wrong"));
    }

    @Test
    void passwordPersistsAcrossInstances(@TempDir Path tempDir) {
        File file = tempDir.resolve("password.json").toFile();
        new PasswordManager(file.getAbsolutePath()).setPassword("pw");

        PasswordManager reloaded = new PasswordManager(file.getAbsolutePath());
        assertTrue(reloaded.isPasswordSet());
        assertTrue(reloaded.verifyPassword("pw"));
    }
}
