/**
 * The PasswordDialog class provides a graphical user interface (GUI) dialog
 * for entering or setting a password. It can be used in two modes: to enter an
 * existing password or to set a new password.
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * The {@code PasswordDialog} class provides a graphical user interface (GUI) dialog
 * for entering or setting a password. It can be used in two modes: to enter an
 * existing password or to set a new password.
 * 
 * <p>This class extends {@link JDialog} and provides a simple interface for users to
 * input their password. It includes validation to ensure that the new password
 * and confirmation match when setting a new password. The dialog can be
 * displayed modally, meaning it will block input to other windows until it is
 * closed.</p>
 */
public class PasswordDialog extends JDialog {
    private boolean succeeded;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private boolean newPasswordMode;
    private String password;

    /**
     * Constructs a {@code PasswordDialog} instance.
     *
     * @param parent          the parent frame of the dialog
     * @param newPasswordMode {@code true} if the dialog is for setting a new password,
     *                        {@code false} if it is for entering an existing password
     */
    public PasswordDialog(Frame parent, boolean newPasswordMode) {
        super(parent, newPasswordMode ? "Set New Password" : "Enter Password", true);
        this.newPasswordMode = newPasswordMode;

        setSize(newPasswordMode ? 300 : 250, newPasswordMode ? 200 : 150);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10,10,10,10));
        getContentPane().add(panel);

        // Create OK button first so we can reference it.
        final JButton okButton = new JButton("OK");

        JPanel inputPanel;
        if(newPasswordMode) {
            inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            inputPanel.add(new JLabel("Password:"));
            passwordField = new JPasswordField();
            inputPanel.add(passwordField);
            inputPanel.add(new JLabel("Confirm:"));
            confirmPasswordField = new JPasswordField();
            inputPanel.add(confirmPasswordField);
            // Pressing Enter in either field triggers the OK button.
            passwordField.addActionListener(e -> okButton.doClick());
            confirmPasswordField.addActionListener(e -> okButton.doClick());
        } else {
            inputPanel = new JPanel(new GridLayout(1, 2, 5, 5));
            inputPanel.add(new JLabel("Password:"));
            passwordField = new JPasswordField();
            inputPanel.add(passwordField);
            // Pressing Enter in the field triggers the OK button.
            passwordField.addActionListener(e -> okButton.doClick());
        }
        panel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton.addActionListener(e -> {
            String pass = new String(passwordField.getPassword());
            if(newPasswordMode) {
                String confirm = new String(confirmPasswordField.getPassword());
                if(!pass.equals(confirm)) {
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
     * Returns whether the operation succeeded.
     *
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * Returns the entered or set password.
     *
     * @return the password as a {@code String}
     */
    public String getPassword() {
        return password;
    }
}