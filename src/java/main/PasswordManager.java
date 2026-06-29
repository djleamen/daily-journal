import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * The {@code PasswordManager} class is a utility class for managing
 * password storage and verification. Passwords are stretched with a salted,
 * deliberately-slow key derivation function (PBKDF2 with HMAC-SHA256) and the
 * salt, iteration count, and derived hash are stored together in a JSON file.
 *
 * <p>Older installs that stored a single, unsalted SHA-256 hex digest under
 * {@code passwordHash} are still accepted: such a legacy password verifies
 * once against the old scheme and is then transparently re-hashed and rewritten
 * in the salted format, so existing users are not locked out.
 */
public class PasswordManager {
    private static final Logger logger = Logger.getLogger(PasswordManager.class.getName());

    /** Key derivation function used for newly stored passwords. */
    private static final String KDF_ALGORITHM = "PBKDF2WithHmacSHA256";
    /** Work factor for the KDF; high enough to make brute force expensive. */
    private static final int ITERATIONS = 210_000;
    /** Length of the per-password random salt, in bytes. */
    private static final int SALT_LENGTH = 16;
    /** Length of the derived hash, in bits. */
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final String filePath;
    // Salted-KDF credential; null when no password is set or only a legacy hash was loaded.
    private byte[] salt;
    private int iterations;
    private byte[] hash;
    // Legacy bare SHA-256 hex digest; null unless an old-format password.json was loaded.
    private String legacyHash;

    /**
     * Constructs a {@code PasswordManager} with the specified file path.
     * The file is used to store the hashed password.
     *
     * @param filePath the path to the file where the password hash is stored
     */
    public PasswordManager(String filePath) {
        this.filePath = filePath;
        loadPassword();
    }

    /**
     * Loads the stored credential from the file. Both the salted-KDF format
     * ({@code salt}/{@code iterations}/{@code hash}) and the legacy bare-hex
     * format ({@code passwordHash}) are recognised. If the file does not exist
     * no credential is loaded.
     */
    private void loadPassword() {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonStr = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStr.append(line);
            }
            JSONObject json = new JSONObject(jsonStr.toString());
            String encodedHash = json.getString("hash");
            if (encodedHash != null) {
                this.salt = Base64.getDecoder().decode(json.getString("salt"));
                this.iterations = Integer.parseInt(json.getString("iterations"));
                this.hash = Base64.getDecoder().decode(encodedHash);
            } else {
                // Fall back to the legacy unsalted SHA-256 digest, if present.
                this.legacyHash = json.getString("passwordHash");
            }
        } catch (Exception e) {
            logger.severe(String.format("Error loading password from file: %s - %s", filePath, e.getMessage()));
        }
    }

    /**
     * Checks if a password has been set.
     *
     * @return {@code true} if a password is set, {@code false} otherwise
     */
    public boolean isPasswordSet() {
        return hash != null || legacyHash != null;
    }

    /**
     * Verifies if the provided password matches the stored credential, using a
     * constant-time comparison. A password stored in the legacy unsalted format
     * that verifies successfully is transparently upgraded to the salted-KDF
     * format and rewritten to disk.
     *
     * @param password the password to verify
     * @return {@code true} if the password matches, {@code false} otherwise
     */
    public boolean verifyPassword(String password) {
        if (hash != null) {
            byte[] candidate = pbkdf2(password, salt, iterations, hash.length * 8);
            return MessageDigest.isEqual(candidate, hash);
        }
        if (legacyHash != null) {
            boolean matches = MessageDigest.isEqual(
                    legacyHash.getBytes(StandardCharsets.UTF_8),
                    legacySha256Hex(password).getBytes(StandardCharsets.UTF_8));
            if (matches) {
                // Migrate the verified legacy password to the salted-KDF format.
                setPassword(password);
            }
            return matches;
        }
        return false;
    }

    /**
     * Sets a new password by deriving a salted hash and storing the salt,
     * iteration count, and hash in the file.
     *
     * @param password the new password to set
     */
    public void setPassword(String password) {
        byte[] newSalt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(newSalt);
        byte[] newHash = pbkdf2(password, newSalt, ITERATIONS, KEY_LENGTH);
        this.salt = newSalt;
        this.iterations = ITERATIONS;
        this.hash = newHash;
        this.legacyHash = null;

        JSONObject json = new JSONObject();
        json.put("algo", KDF_ALGORITHM);
        json.put("salt", Base64.getEncoder().encodeToString(newSalt));
        json.put("iterations", ITERATIONS);
        json.put("hash", Base64.getEncoder().encodeToString(newHash));
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.write(json.toString(4));
        } catch (Exception e) {
            logger.severe(String.format("Error saving password to file: %s - %s", filePath, e.getMessage()));
        }
    }

    /**
     * Derives a hash from the password using PBKDF2 with HMAC-SHA256.
     *
     * @param password      the password to stretch
     * @param salt          the per-password salt
     * @param iterations    the KDF work factor
     * @param keyLengthBits the desired hash length, in bits
     * @return the derived hash bytes
     * @throws PasswordHashingException if the derivation fails
     */
    private static byte[] pbkdf2(String password, byte[] salt, int iterations, int keyLengthBits) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLengthBits);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KDF_ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new PasswordHashingException("Failed to hash password: " + KDF_ALGORITHM + " not available", ex);
        } finally {
            spec.clearPassword();
        }
    }

    /**
     * Hashes the provided password using the legacy unsalted SHA-256 scheme.
     * Retained only to verify (and then upgrade) passwords stored before the
     * migration to a salted KDF.
     *
     * @param password the password to hash
     * @return the hashed password as a hexadecimal string
     * @throws PasswordHashingException if the hashing process fails
     */
    private static String legacySha256Hex(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            // Convert byte array to hex string.
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new PasswordHashingException("Failed to hash password: SHA-256 algorithm not available", ex);
        }
    }

    /**
     * Custom exception for password hashing failures.
     */
    public static class PasswordHashingException extends RuntimeException {
        public PasswordHashingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
