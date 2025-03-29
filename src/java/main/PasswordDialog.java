/**
 * The PasswordDialog class provides a graphical user interface (GUI) dialog
 * for either entering an existing password or setting a new password with confirmation.
 * It supports two modes: new password mode and existing password mode.
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A dialog for entering or setting a password. Supports two modes:
 * entering an existing password or setting a new password with confirmation.
 */
public class PasswordDialog extends JDialog {
    private boolean succeeded;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField; // used in new password mode
    private boolean newPasswordMode;
    private String password;

    /**
     * Constructs a PasswordDialog.
     *
     * @param parent          the parent frame of the dialog
     * @param newPasswordMode true if the dialog is for setting a new password, false for entering an existing password
     */
    public PasswordDialog(Frame parent, boolean newPasswordMode) {
        super(parent, newPasswordMode ? "Set New Password" : "Enter Password", true);
        this.newPasswordMode = newPasswordMode;

        setSize(newPasswordMode ? 300 : 250, newPasswordMode ? 200 : 150);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(panel);

        JPanel inputPanel;
        if (newPasswordMode) {
            inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            inputPanel.add(new JLabel("Password:"));
            passwordField = new JPasswordField();
            inputPanel.add(passwordField);
            inputPanel.add(new JLabel("Confirm:"));
            confirmPasswordField = new JPasswordField();
            inputPanel.add(confirmPasswordField);
        } else {
            inputPanel = new JPanel(new GridLayout(1, 2, 5, 5));
            inputPanel.add(new JLabel("Password:"));
            passwordField = new JPasswordField();
            inputPanel.add(passwordField);
        }
        panel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            String pass = new String(passwordField.getPassword());
            if (newPasswordMode) {
                String confirm = new String(confirmPasswordField.getPassword());
                if (!pass.equals(confirm)) {
                    JOptionPane.showMessageDialog(PasswordDialog.this, "Passwords do not match.");
                    return;
                }
            }
            password = pass;
            succeeded = true;
            dispose();
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            succeeded = false;
            dispose();
        });
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Returns whether the user successfully entered or set a password.
     *
     * @return true if the user clicked OK and entered a valid password, false otherwise
     */
    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * Returns the password entered or set by the user.
     *
     * @return the password as a String
     */
    public String getPassword() {
        return password;
    }
}