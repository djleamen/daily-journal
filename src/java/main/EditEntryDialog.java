import java.awt.*;
import java.time.LocalDate;
import java.util.Collections;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * The {@code EditEntryDialog} class provides a graphical user interface for editing a journal entry.
 * Users can update the title, date, location, tags, and content of the entry.
 * The dialog also integrates with a global tags manager to allow adding new tags.
 */
public class EditEntryDialog extends JDialog {
    private boolean succeeded;
    private JTextField titleField;
    private JTextField dateField;
    private JTextField locationField;
    private JTextField tagField;
    private DefaultListModel<String> entryTagsModel;
    private JTextArea contentArea;
    private transient JournalEntry journalEntry;

    /**
     * Constructs an {@code EditEntryDialog} instance.
     *
     * @param parent      the parent frame of the dialog
     * @param entry       the journal entry to be edited
     * @param tagsManager the manager for handling global tags
     */
    public EditEntryDialog(Frame parent, JournalEntry entry, TagsManager tagsManager) {
        super(parent, "Edit Journal Entry", true);
        setSize(400, 800);
        setLocationRelativeTo(parent);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10,10,10,10));
        getContentPane().add(panel);

        // Pre-populated attributes panel.
        JPanel attributePanel = new JPanel(new GridLayout(0,1,5,5));

        // Title field
        titleField = new JTextField(entry.getTitle());
        attributePanel.add(new JLabel("Title:"));
        attributePanel.add(titleField);

        // Date field
        dateField = new JTextField();
        dateField.setText(entry.getDate().toString());
        attributePanel.add(new JLabel("Date (yyyy-MM-dd):"));
        attributePanel.add(dateField);

        // Location field
        locationField = new JTextField(entry.getLocation());
        attributePanel.add(new JLabel("Location:"));
        attributePanel.add(locationField);

        // New Tag input
        tagField = new JTextField();
        entryTagsModel = new DefaultListModel<>();
        for (String tag : entry.getTags()) {
            entryTagsModel.addElement(tag);
        }

        // Add Tag button
        JButton addTagButton = new JButton("Add Tag");
        addTagButton.addActionListener(e -> {
            String tag = tagField.getText().trim();
            if (!tag.isEmpty() && !entryTagsModel.contains(tag)) {
                entryTagsModel.addElement(tag);
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

        // Global Tags Panel
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

        // Combine attribute and global tags panels
        JPanel topPanel = new JPanel(new BorderLayout(10,10));
        topPanel.add(attributePanel, BorderLayout.CENTER);
        topPanel.add(globalTagsPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // Large content area pre-populated with entry content
        contentArea = new JTextArea(entry.getContent());
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setPreferredSize(new Dimension(350,300));
        panel.add(contentScroll, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(EditEntryDialog.this, "Title cannot be empty");
                return;
            }
            String title = titleField.getText().trim();
            String location = locationField.getText().trim();
            java.util.List<String> tags = Collections.list(entryTagsModel.elements());
            String content = contentArea.getText().trim();

            // Parse the date field
            LocalDate date;
            try {
                date = LocalDate.parse(dateField.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(EditEntryDialog.this, "Invalid date format. Use yyyy-MM-dd.");
                return;
            }

            // Retain the original date only if parsing fails
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
     * @return {@code true} if the dialog operation succeeded; {@code false} otherwise
     */
    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * Retrieves the updated journal entry after the dialog operation.
     *
     * @return the updated {@code JournalEntry} object
     */
    public JournalEntry getJournalEntry() {
        return journalEntry;
    }
}