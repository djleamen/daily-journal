import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PasswordManagerTest {

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

    @Test
    void storedFormatIsSaltedKdfNotBareSha256(@TempDir Path tempDir) throws Exception {
        File file = tempDir.resolve("password.json").toFile();
        new PasswordManager(file.getAbsolutePath()).setPassword("secret");

        String stored = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        assertTrue(stored.contains("salt"));
        assertTrue(stored.contains("iterations"));
        assertTrue(stored.contains("hash"));
        // The bare unsalted SHA-256 digest must never be written.
        assertFalse(stored.contains("passwordHash"));
        assertFalse(stored.contains(sha256Hex("secret")));
    }

    @Test
    void saltMakesIdenticalPasswordsStoreDifferently(@TempDir Path tempDir) throws Exception {
        File fileA = tempDir.resolve("a.json").toFile();
        File fileB = tempDir.resolve("b.json").toFile();
        new PasswordManager(fileA.getAbsolutePath()).setPassword("samepass");
        new PasswordManager(fileB.getAbsolutePath()).setPassword("samepass");

        String storedA = new String(Files.readAllBytes(fileA.toPath()), StandardCharsets.UTF_8);
        String storedB = new String(Files.readAllBytes(fileB.toPath()), StandardCharsets.UTF_8);
        assertNotEquals(storedA, storedB);

        // Both must still verify the same password despite different salts.
        assertTrue(new PasswordManager(fileA.getAbsolutePath()).verifyPassword("samepass"));
        assertTrue(new PasswordManager(fileB.getAbsolutePath()).verifyPassword("samepass"));
    }

    @Test
    void legacyHashVerifiesAndIsUpgraded(@TempDir Path tempDir) throws Exception {
        File file = tempDir.resolve("password.json").toFile();
        writeLegacyHash(file, "legacypw");

        PasswordManager mgr = new PasswordManager(file.getAbsolutePath());
        assertTrue(mgr.isPasswordSet());
        // Wrong password must not verify against the legacy hash.
        assertFalse(mgr.verifyPassword("nope"));
        // Correct password verifies and triggers transparent upgrade.
        assertTrue(mgr.verifyPassword("legacypw"));

        String upgraded = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        assertFalse(upgraded.contains("passwordHash"));
        assertTrue(upgraded.contains("salt"));

        // After upgrade the password still verifies via the salted scheme.
        assertTrue(new PasswordManager(file.getAbsolutePath()).verifyPassword("legacypw"));
    }

    private static void writeLegacyHash(File file, String password) throws Exception {
        JSONObject json = new JSONObject();
        json.put("passwordHash", sha256Hex(password));
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            writer.write(json.toString(4));
        }
    }

    private static String sha256Hex(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
