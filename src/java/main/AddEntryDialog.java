/**
 * The AddEntryDialog class provides a dialog for adding new journal entries.
 * It includes fields for title, date, location, tags, and content.
 * Users can add new tags, select from existing global tags, and save their entries.
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.Collections;


/**
 * The {@code AddEntryDialog} class provides a dialog for adding new journal entries.
 * It includes fields for title, date, location, tags, and content.
 * Users can add new tags, select from existing global tags, and save their entries.
 */
public class AddEntryDialog extends JDialog {
    private boolean succeeded;
    private JTextField titleField;
    private JTextField dateField;
    private JTextField locationField;
    private JTextField tagField;
    private DefaultListModel<String> entryTagsModel;
    private JTextArea contentArea;
    private JournalEntry journalEntry;
    private TagsManager tagsManager;
    
    /**
     * Constructs a new {@code AddEntryDialog}.
     *
     * @param parent      the parent frame of the dialog
     * @param tagsManager the {@code TagsManager} instance for managing global tags
     */
    public AddEntryDialog(Frame parent, TagsManager tagsManager) {
        super(parent, "Add New Journal Entry", true);
        this.tagsManager = tagsManager;
        setSize(400, 800);
        setLocationRelativeTo(parent);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10,10,10,10));
        getContentPane().add(panel);
        
        // Attributes panel: title, date, location, new tag, and entry tags list.
        JPanel attributePanel = new JPanel(new GridLayout(0,1,5,5));
        
        // Title field
        titleField = new JTextField();
        attributePanel.add(new JLabel("Title:"));
        attributePanel.add(titleField);
        
        // Date field
        dateField = new JTextField();
        dateField.setText(LocalDate.now().toString());
        attributePanel.add(new JLabel("Date (yyyy-MM-dd):"));
        attributePanel.add(dateField);
        
        // Location field
        locationField = new JTextField();
        attributePanel.add(new JLabel("Location:"));
        attributePanel.add(locationField);
        
        // New Tag input
        tagField = new JTextField();
        entryTagsModel = new DefaultListModel<>();
        JButton addTagButton = new JButton("Add Tag");
        addTagButton.addActionListener(e -> {
            String tag = tagField.getText().trim();
            if (!tag.isEmpty() && !entryTagsModel.contains(tag)) {
                entryTagsModel.addElement(tag);
                // Also add tag to global tags if new.
                tagsManager.addTag(tag);
                tagField.setText("");
            }
        });
        attributePanel.add(new JLabel("New Tag:"));
        JPanel tagInputPanel = new JPanel(new BorderLayout(5,5));
        tagInputPanel.add(tagField, BorderLayout.CENTER);
        tagInputPanel.add(addTagButton, BorderLayout.EAST);
        attributePanel.add(tagInputPanel);
        
        // Entry Tags list
        attributePanel.add(new JLabel("Entry Tags:"));
        JList<String> entryTagsList = new JList<>(entryTagsModel);
        entryTagsList.setVisibleRowCount(3);
        JScrollPane entryTagsScroll = new JScrollPane(entryTagsList);
        attributePanel.add(entryTagsScroll);
        
        // Global Tags Panel for filtering/selection.
        DefaultListModel<String> globalTagsModel = new DefaultListModel<>();
        for (String tag : tagsManager.getTags()) {
            globalTagsModel.addElement(tag);
        }
        JList<String> globalTagsList = new JList<>(globalTagsModel);
        globalTagsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane globalTagsScroll = new JScrollPane(globalTagsList);
        globalTagsScroll.setPreferredSize(new Dimension(150, 100));
        JButton addGlobalTagButton = new JButton("Add Selected Global Tag");
        addGlobalTagButton.addActionListener(e -> {
            String selectedGlobalTag = globalTagsList.getSelectedValue();
            if (selectedGlobalTag != null && !entryTagsModel.contains(selectedGlobalTag)) {
                entryTagsModel.addElement(selectedGlobalTag);
            }
        });
        JPanel globalTagsPanel = new JPanel(new BorderLayout(5,5));
        globalTagsPanel.add(new JLabel("Global Tags:"), BorderLayout.NORTH);
        globalTagsPanel.add(globalTagsScroll, BorderLayout.CENTER);
        globalTagsPanel.add(addGlobalTagButton, BorderLayout.SOUTH);
        
        // Combine attribute and global tags panels.
        JPanel topPanel = new JPanel(new BorderLayout(10,10));
        topPanel.add(attributePanel, BorderLayout.CENTER);
        topPanel.add(globalTagsPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Large content area for the journal entry.
        contentArea = new JTextArea();
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setPreferredSize(new Dimension(350,300));
        panel.add(contentScroll, BorderLayout.CENTER);
        
        // Buttons panel.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(AddEntryDialog.this, "Title cannot be empty");
                return;
            }
            String title = titleField.getText().trim();
            String location = locationField.getText().trim();
            java.util.List<String> tags = Collections.list(entryTagsModel.elements());
            String content = contentArea.getText().trim();
            
            // Parse the date from the dateField.
            LocalDate date;
            try {
                date = LocalDate.parse(dateField.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(AddEntryDialog.this, "Invalid date format. Use yyyy-MM-dd.");
                return;
            }
            
            journalEntry = new JournalEntry(title, date, location, tags, content);
            succeeded = true;
            dispose();
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            succeeded = false;
            dispose();
        });
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Checks if the dialog operation was successful.
     *
     * @return {@code true} if the entry was successfully saved, {@code false} otherwise
     */
    public boolean isSucceeded() {
        return succeeded;
    }
    
    /**
     * Retrieves the journal entry created in the dialog.
     *
     * @return the {@code JournalEntry} object containing the entered data
     */
    public JournalEntry getJournalEntry() {
        return journalEntry;
    }
}