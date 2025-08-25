import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * The {@code FilterDialog} class provides a dialog for filtering journal entries 
 * based on categories such as "Tag" or "Location". Users can select a category 
 * and a corresponding value to apply the filter.
 */
public class FilterDialog extends JDialog {
    private boolean succeeded;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> valueComboBox;
    private String selectedCategory;
    private String selectedValue;

    /**
     * Constructs a {@code FilterDialog} instance for filtering journal entries 
     * by category and value.
     *
     * @param parent      the parent frame that owns this dialog
     * @param tagsManager the manager providing the list of global tags for filtering by "Tag"
     * @param entries     the list of journal entries used to derive distinct locations for filtering by "Location"
     */
    public FilterDialog(Frame parent, TagsManager tagsManager, List<JournalEntry> entries) {
        super(parent, "Filter Entries", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        // Create a panel with two rows for the filter type and value.
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        mainPanel.add(new JLabel("Filter Category:"));
        categoryComboBox = new JComboBox<>(new String[] {"Tag", "Location"});
        mainPanel.add(categoryComboBox);
        
        mainPanel.add(new JLabel("Filter Value:"));
        valueComboBox = new JComboBox<>();
        mainPanel.add(valueComboBox);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Populate the value combo box based on the selected category.
        updateValueComboBox(tagsManager, entries);
        
        // Update the value list when the category changes.
        categoryComboBox.addActionListener(e -> updateValueComboBox(tagsManager, entries));
        
        // Buttons panel.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            selectedCategory = (String) categoryComboBox.getSelectedItem();
            selectedValue = (String) valueComboBox.getSelectedItem();
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
     * Updates the value combo box based on the selected filter category.
     * <p>
     * If "Tag" is selected, the combo box is populated with available tags.
     * If "Location" is selected, the combo box is populated with distinct, non-empty locations.
     * </p>
     *
     * @param tagsManager the manager providing the list of global tags
     * @param entries     the list of journal entries used to derive distinct locations
     */
    private void updateValueComboBox(TagsManager tagsManager, List<JournalEntry> entries) {
        String category = (String) categoryComboBox.getSelectedItem();
        valueComboBox.removeAllItems();
        valueComboBox.addItem("All");
        if ("Tag".equals(category)) {
            for (String tag : tagsManager.getTags()) {
                valueComboBox.addItem(tag);
            }
        } else if ("Location".equals(category)) {
            // Use a set to gather distinct, non-empty locations.
            Set<String> locations = new TreeSet<>();
            for (JournalEntry entry : entries) {
                String loc = entry.getLocation();
                if (loc != null && !loc.trim().isEmpty()) {
                    locations.add(loc);
                }
            }
            for (String loc : locations) {
                valueComboBox.addItem(loc);
            }
        }
    }

    /**
     * Returns whether the dialog was successfully completed.
     *
     * @return {@code true} if the user clicked "OK", {@code false} otherwise
     */
    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * Returns the selected filter category.
     *
     * @return the selected category, either "Tag" or "Location"
     */
    public String getSelectedCategory() {
        return selectedCategory;
    }

    /**
     * Returns the selected filter value.
     *
     * @return the selected value for the chosen category, or "All" if no specific value was selected
     */
    public String getSelectedValue() {
        return selectedValue;
    }
}