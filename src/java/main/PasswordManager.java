import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * The {@code PasswordManager} class is a utility class for managing 
 * password storage and verification. Passwords are hashed using SHA-256 
 * and stored in a JSON file.
 */
public class PasswordManager {
    private static final Logger logger = Logger.getLogger(PasswordManager.class.getName());
    private final String filePath;
    private String storedHash;

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
     * Loads the stored password hash from the file.
     * If the file does not exist, the stored hash is set to {@code null}.
     */
    private void loadPassword() {
        File file = new File(filePath);
        if (!file.exists()) {
            storedHash = null;
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonStr = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStr.append(line);
            }
            JSONObject json = new JSONObject(jsonStr.toString());
            storedHash = json.getString("passwordHash");
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
        return storedHash != null;
    }

    /**
     * Verifies if the provided password matches the stored password hash.
     *
     * @param password the password to verify
     * @return {@code true} if the password matches, {@code false} otherwise
     */
    public boolean verifyPassword(String password) {
        return hashPassword(password).equals(storedHash);
    }

    /**
     * Sets a new password by hashing it and storing the hash in the file.
     *
     * @param password the new password to set
     */
    public void setPassword(String password) {
        storedHash = hashPassword(password);
        JSONObject json = new JSONObject();
        json.put("passwordHash", storedHash);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.write(json.toString(4));
        } catch (Exception e) {
            logger.severe(String.format("Error saving password to file: %s - %s", filePath, e.getMessage()));
        }
    }

    /**
     * Hashes the provided password using SHA-256.
     *
     * @param password the password to hash
     * @return the hashed password as a hexadecimal string
     * @throws PasswordHashingException if the hashing process fails
     */
    public static String hashPassword(String password) {
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