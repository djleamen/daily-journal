import javax.swing.JOptionPane;

/**
 * Handles password setup and authentication for the journal application.
 */
public class JournalAuthentication {
    private static final int MAX_ATTEMPTS = 3;

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private JournalAuthentication() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Handles the complete authentication process including setup and login.
     *
     * @param passwordManager The password manager instance
     * @return true if authentication was successful, false otherwise
     */
    public static boolean authenticate(PasswordManager passwordManager) {
        return handlePasswordSetup(passwordManager) && authenticateUser(passwordManager);
    }

    /**
     * Handles password setup if no password is currently set.
     *
     * @param passwordManager The password manager instance
     * @return true if password setup was successful or not needed, false if user cancelled
     */
    private static boolean handlePasswordSetup(PasswordManager passwordManager) {
        if (!passwordManager.isPasswordSet()) {
            PasswordDialog setDialog = new PasswordDialog(null, true);
            setDialog.setVisible(true);
            if (setDialog.isSucceeded()) {
                passwordManager.setPassword(setDialog.getPassword());
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Authenticates the user with up to 3 password attempts.
     *
     * @param passwordManager The password manager instance
     * @return true if authentication was successful, false otherwise
     */
    private static boolean authenticateUser(PasswordManager passwordManager) {
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {
            PasswordDialog loginDialog = new PasswordDialog(null, false);
            loginDialog.setVisible(true);
            if (loginDialog.isSucceeded() && passwordManager.verifyPassword(loginDialog.getPassword())) {
                return true;
            }
            attempts++;
            if (attempts < MAX_ATTEMPTS) {
                JOptionPane.showMessageDialog(null, 
                    "Incorrect password. Attempts remaining: " + (MAX_ATTEMPTS - attempts));
            }
        }
        
        JOptionPane.showMessageDialog(null, "Too many incorrect attempts. Exiting.");
        return false;
    }
}
