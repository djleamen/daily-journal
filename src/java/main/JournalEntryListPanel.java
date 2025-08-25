import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Panel that manages the display and filtering of journal entries in a list.
 */
public class JournalEntryListPanel extends JPanel {
    private final transient JournalManager journalManager;
    private final DefaultListModel<JournalEntry> entriesModel;
    private final JList<JournalEntry> entriesList;
    
    private String currentTagFilter = null;
    private String currentLocationFilter = null;

    /**
     * Constructs a new JournalEntryListPanel.
     *
     * @param journalManager The journal manager to use for data access
     */
    public JournalEntryListPanel(JournalManager journalManager) {
        this.journalManager = journalManager;
        this.entriesModel = new DefaultListModel<>();
        this.entriesList = new JList<>(entriesModel);
        
        initializeComponents();
        refreshEntries();
    }

    /**
     * Initializes the panel components.
     */
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        entriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entriesList.setCellRenderer(new JournalEntryRenderer());
        
        JScrollPane scrollPane = new JScrollPane(entriesList);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Gets the currently selected journal entry.
     *
     * @return The selected entry or null if none is selected
     */
    public JournalEntry getSelectedEntry() {
        return entriesList.getSelectedValue();
    }

    /**
     * Updates the list of journal entries based on current filters.
     */
    public void updateEntries() {
        refreshEntries();
    }

    /**
     * Internal method to refresh entries (safe to call from constructor).
     */
    private void refreshEntries() {
        entriesModel.clear();
        List<JournalEntry> sortedEntries = new ArrayList<>(journalManager.getEntries());
        sortedEntries.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        for (JournalEntry entry : sortedEntries) {
            if (matchesFilters(entry)) {
                entriesModel.addElement(entry);
            }
        }
    }

    /**
     * Checks if an entry matches the current filters.
     *
     * @param entry The entry to check
     * @return true if the entry matches all active filters
     */
    private boolean matchesFilters(JournalEntry entry) {
        boolean tagOk = (currentTagFilter == null || entry.getTags().contains(currentTagFilter));
        boolean locationOk = (currentLocationFilter == null || entry.getLocation().equals(currentLocationFilter));
        return tagOk && locationOk;
    }

    /**
     * Sets the current tag filter.
     *
     * @param tagFilter The tag to filter by, or null to clear the filter
     */
    public void setTagFilter(String tagFilter) {
        this.currentTagFilter = tagFilter;
    }

    /**
     * Sets the current location filter.
     *
     * @param locationFilter The location to filter by, or null to clear the filter
     */
    public void setLocationFilter(String locationFilter) {
        this.currentLocationFilter = locationFilter;
    }

    /**
     * Clears all filters.
     */
    public void clearFilters() {
        this.currentTagFilter = null;
        this.currentLocationFilter = null;
    }
}
