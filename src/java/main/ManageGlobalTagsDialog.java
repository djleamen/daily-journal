/**
 * The ManageGlobalTagsDialog class provides a user interface for managing
 * global tags in the journal application. Users can view, delete, and manage
 * tags that are applied globally across all journal entries.
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * A dialog for managing global tags in the journal application.
 * Allows users to view, delete, and manage tags that are applied globally.
 */
public class ManageGlobalTagsDialog extends JDialog {
    private TagsManager tagsManager;
    private JournalManager journalManager;
    private DefaultListModel<String> tagsListModel;
    private JList<String> tagsList;

    /**
     * Constructs a new ManageGlobalTagsDialog.
     *
     * @param parent         The parent frame of this dialog.
     * @param tagsManager    The TagsManager instance for managing global tags.
     * @param journalManager The JournalManager instance for managing journal entries.
     */
    public ManageGlobalTagsDialog(Frame parent, TagsManager tagsManager, JournalManager journalManager) {
        super(parent, "Manage Global Tags", true);
        this.tagsManager = tagsManager;
        this.journalManager = journalManager;
        setSize(300, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Create list model and list for global tags.
        tagsListModel = new DefaultListModel<>();
        for (String tag : tagsManager.getTags()) {
            tagsListModel.addElement(tag);
        }
        tagsList = new JList<>(tagsListModel);
        tagsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tagsList);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons panel: Delete and Close.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteButton = new JButton("Delete Tag");
        deleteButton.addActionListener(e -> handleDeleteTag());
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Handles the deletion of a selected tag.
     * Prompts the user for confirmation, removes the tag from global tags,
     * removes it from all journal entries, and updates the UI.
     */
    private void handleDeleteTag() {
        String selectedTag = tagsList.getSelectedValue();
        if (selectedTag == null) {
            JOptionPane.showMessageDialog(this, "Please select a tag to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete tag '" + selectedTag + "'?\nThis will remove it from all entries.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Remove tag from global tags.
            tagsManager.getTags().remove(selectedTag);
            tagsManager.saveTags();

            // Remove tag from all journal entries.
            for (JournalEntry entry : journalManager.getEntries()) {
                entry.getTags().remove(selectedTag);
            }
            journalManager.saveEntries();

            // Update the list model.
            tagsListModel.removeElement(selectedTag);
            JOptionPane.showMessageDialog(this, "Tag deleted.");
        }
    }
}