import java.awt.FlowLayout;
import javax.swing.*;

/**
 * Panel that contains all the action buttons for the journal application.
 */
public class JournalButtonPanel extends JPanel {
    private final transient JournalManager journalManager;
    private final transient TagsManager tagsManager;
    private final JournalEntryListPanel entryListPanel;
    private final JournalGraphPanel graphPanel;
    private final JFrame parentFrame;

    /**
     * Constructs a new JournalButtonPanel.
     *
     * @param parentFrame The parent frame
     * @param journalManager The journal manager
     * @param tagsManager The tags manager
     * @param entryListPanel The entry list panel
     * @param graphPanel The graph panel
     */
    public JournalButtonPanel(JFrame parentFrame, JournalManager journalManager, 
                              TagsManager tagsManager, JournalEntryListPanel entryListPanel, 
                              JournalGraphPanel graphPanel) {
        this.parentFrame = parentFrame;
        this.journalManager = journalManager;
        this.tagsManager = tagsManager;
        this.entryListPanel = entryListPanel;
        this.graphPanel = graphPanel;
        
        initializeButtons();
    }

    /**
     * Initializes all the buttons and their action listeners.
     */
    private void initializeButtons() {
        setLayout(new FlowLayout(FlowLayout.RIGHT));

        add(createFilterButton());
        add(createManageTagsButton());
        add(createViewEntryButton());
        add(createAddEntryButton());
        add(createEditEntryButton());
        add(createDeleteEntryButton());
    }

    /**
     * Creates the filter button.
     */
    private JButton createFilterButton() {
        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(e -> handleFilterAction());
        return filterButton;
    }

    /**
     * Creates the manage tags button.
     */
    private JButton createManageTagsButton() {
        JButton manageTagsButton = new JButton("Manage Global Tags");
        manageTagsButton.addActionListener(e -> handleManageTagsAction());
        return manageTagsButton;
    }

    /**
     * Creates the view entry button.
     */
    private JButton createViewEntryButton() {
        JButton viewEntryButton = new JButton("View Entry");
        viewEntryButton.addActionListener(e -> handleViewEntryAction());
        return viewEntryButton;
    }

    /**
     * Creates the add entry button.
     */
    private JButton createAddEntryButton() {
        JButton addEntryButton = new JButton("Add New Entry");
        addEntryButton.addActionListener(e -> handleAddEntryAction());
        return addEntryButton;
    }

    /**
     * Creates the edit entry button.
     */
    private JButton createEditEntryButton() {
        JButton editEntryButton = new JButton("Edit Selected Entry");
        editEntryButton.addActionListener(e -> handleEditEntryAction());
        return editEntryButton;
    }

    /**
     * Creates the delete entry button.
     */
    private JButton createDeleteEntryButton() {
        JButton deleteEntryButton = new JButton("Delete Selected Entry");
        deleteEntryButton.addActionListener(e -> handleDeleteEntryAction());
        return deleteEntryButton;
    }

    /**
     * Handles the filter button action.
     */
    private void handleFilterAction() {
        FilterDialog dialog = new FilterDialog(parentFrame, tagsManager, journalManager.getEntries());
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            applyFilter(dialog.getSelectedCategory(), dialog.getSelectedValue());
        }
    }

    /**
     * Applies the selected filter.
     */
    private void applyFilter(String category, String value) {
        if ("All".equals(value)) {
            entryListPanel.clearFilters();
        } else if ("Tag".equals(category)) {
            entryListPanel.setTagFilter(value);
            entryListPanel.setLocationFilter(null);
        } else if ("Location".equals(category)) {
            entryListPanel.setLocationFilter(value);
            entryListPanel.setTagFilter(null);
        }
        entryListPanel.updateEntries();
    }

    /**
     * Handles the manage tags button action.
     */
    private void handleManageTagsAction() {
        ManageGlobalTagsDialog dialog = new ManageGlobalTagsDialog(parentFrame, tagsManager, journalManager);
        dialog.setVisible(true);
    }

    /**
     * Handles the view entry button action.
     */
    private void handleViewEntryAction() {
        JournalEntry selectedEntry = entryListPanel.getSelectedEntry();
        if (selectedEntry == null) {
            JOptionPane.showMessageDialog(parentFrame, "Please select an entry to view.");
            return;
        }
        ViewEntryDialog dialog = new ViewEntryDialog(parentFrame, selectedEntry);
        dialog.setVisible(true);
    }

    /**
     * Handles the add entry button action.
     */
    private void handleAddEntryAction() {
        AddEntryDialog dialog = new AddEntryDialog(parentFrame, tagsManager);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            JournalEntry newEntry = dialog.getJournalEntry();
            journalManager.addEntry(newEntry);
            journalManager.saveEntries();
            refreshViews();
        }
    }

    /**
     * Handles the edit entry button action.
     */
    private void handleEditEntryAction() {
        JournalEntry selectedEntry = entryListPanel.getSelectedEntry();
        if (selectedEntry == null) {
            JOptionPane.showMessageDialog(parentFrame, "Please select an entry to edit.");
            return;
        }
        
        int index = journalManager.getEntries().indexOf(selectedEntry);
        if (index != -1) {
            EditEntryDialog dialog = new EditEntryDialog(parentFrame, selectedEntry, tagsManager);
            dialog.setVisible(true);
            if (dialog.isSucceeded()) {
                JournalEntry editedEntry = dialog.getJournalEntry();
                journalManager.getEntries().set(index, editedEntry);
                journalManager.saveEntries();
                refreshViews();
            }
        }
    }

    /**
     * Handles the delete entry button action.
     */
    private void handleDeleteEntryAction() {
        JournalEntry selectedEntry = entryListPanel.getSelectedEntry();
        if (selectedEntry == null) {
            JOptionPane.showMessageDialog(parentFrame, "Please select an entry to delete.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(parentFrame,
            "Are you sure you want to delete this entry?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            journalManager.getEntries().remove(selectedEntry);
            journalManager.saveEntries();
            refreshViews();
        }
    }

    /**
     * Refreshes both the entry list and graph panel.
     */
    private void refreshViews() {
        entryListPanel.updateEntries();
        graphPanel.repaint();
    }
}
