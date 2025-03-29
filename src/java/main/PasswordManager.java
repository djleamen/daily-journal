/**
 * The PasswordManager class is responsible for securely managing passwords.
 * It provides functionality to hash passwords, store them in a file, and verify them.
 */

import java.io.*;
import org.json.JSONObject;
import java.security.MessageDigest;

/**
 * A utility class for managing password storage and verification.
 * Passwords are hashed using SHA-256 and stored in a JSON file.
 */
public class PasswordManager {
    private String filePath;
    private String storedHash;

    /**
     * Constructs a PasswordManager with the specified file path.
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
     * If the file does not exist, the stored hash is set to null.
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
            e.printStackTrace();
        }
    }

    /**
     * Checks if a password has been set.
     *
     * @return true if a password is set, false otherwise
     */
    public boolean isPasswordSet() {
        return storedHash != null;
    }

    /**
     * Verifies if the provided password matches the stored password hash.
     *
     * @param password the password to verify
     * @return true if the password matches, false otherwise
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
            e.printStackTrace();
        }
    }

    /**
     * Hashes the provided password using SHA-256.
     *
     * @param password the password to hash
     * @return the hashed password as a hexadecimal string
     * @throws RuntimeException if the hashing process fails
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            // Convert byte array to hex string.
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}