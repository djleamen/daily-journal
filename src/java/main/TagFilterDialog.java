/**
 * The TagFilterDialog class provides a user interface for filtering items
 * based on tags. It displays a combo box populated with available tags
 * retrieved from the TagsManager and allows the user to confirm or cancel
 * their selection.
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A dialog for filtering items by tags. Provides a combo box to select a tag
 * and buttons to confirm or cancel the selection.
 */
public class TagFilterDialog extends JDialog {
    private boolean succeeded;
    private JComboBox<String> tagComboBox;
    private String selectedTag;
    private TagsManager tagsManager;

    /**
     * Constructs a TagFilterDialog.
     *
     * @param parent      The parent frame of the dialog.
     * @param tagsManager The TagsManager instance to retrieve available tags.
     */
    public TagFilterDialog(Frame parent, TagsManager tagsManager) {
        super(parent, "Filter by Tag", true);
        this.tagsManager = tagsManager;

        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Center panel with a combo box of tags
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tagComboBox = new JComboBox<>();
        // "All" means no filter
        tagComboBox.addItem("All");

        // Add each global tag
        for (String tag : tagsManager.getTags()) {
            tagComboBox.addItem(tag);
        }

        comboPanel.add(new JLabel("Select Tag:"));
        comboPanel.add(tagComboBox);
        add(comboPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            selectedTag = (String) tagComboBox.getSelectedItem();
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
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Checks if the dialog was closed with a successful selection.
     *
     * @return true if the user clicked "OK", false otherwise.
     */
    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * Gets the tag selected by the user.
     *
     * @return The selected tag, or null if no tag was selected.
     */
    public String getSelectedTag() {
        return selectedTag;
    }
}