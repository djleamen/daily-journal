/**
 * The JournalApp.java class serves as the main entry point for the Daily Journal application.
 * It initializes the graphical user interface (GUI) and manages user interactions with journal entries.
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The main application class for the Daily Journal application.
 * This class initializes the GUI and manages interactions between the user and the journal data.
 */
public class JournalApp extends JFrame {
    private JournalManager journalManager;
    private TagsManager tagsManager;
    private JournalGraphPanel graphPanel;
    private DefaultListModel<JournalEntry> recentEntriesModel;
    private JList<JournalEntry> recentEntriesList;

    private String currentTagFilter = null;
    private String currentLocationFilter = null;

    /**
     * Constructs the JournalApp and initializes the GUI components.
     */
    public JournalApp() {
        journalManager = new JournalManager("journal_entries.json");
        journalManager.loadEntries();

        tagsManager = new TagsManager("tags.json");
        tagsManager.loadTags();

        setTitle("Daily Journal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 600);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // Top panel: Year overview graph
        graphPanel = new JournalGraphPanel(journalManager);
        graphPanel.setPreferredSize(new Dimension(800, 200));
        mainPanel.add(graphPanel, BorderLayout.NORTH);

        // Center panel: Recent entries list
        recentEntriesModel = new DefaultListModel<>();
        updateRecentEntries();  // will show all initially
        recentEntriesList = new JList<>(recentEntriesModel);
        recentEntriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom cell renderer for date + title
        recentEntriesList.setCellRenderer(new DefaultListCellRenderer() {
            private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                if (value instanceof JournalEntry) {
                    JournalEntry entry = (JournalEntry) value;
                    value = entry.getDate().format(formatter) + " - " + entry.getTitle();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        JScrollPane scrollPane = new JScrollPane(recentEntriesList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel: Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Button: View Entry
        JButton viewEntryButton = new JButton("View Entry");
        viewEntryButton.addActionListener(e -> {
            JournalEntry selectedEntry = recentEntriesList.getSelectedValue();
            if (selectedEntry == null) {
                JOptionPane.showMessageDialog(JournalApp.this, "Please select an entry to view.");
                return;
            }
            String message = "Title: " + selectedEntry.getTitle() + "\n" +
                             "Date: " + selectedEntry.getDate() + "\n" +
                             "Location: " + selectedEntry.getLocation() + "\n" +
                             "Tags: " + String.join(", ", selectedEntry.getTags()) + "\n\n" +
                             "Content:\n" + selectedEntry.getContent();
            JOptionPane.showMessageDialog(JournalApp.this, message, "View Entry", JOptionPane.INFORMATION_MESSAGE);
        });

        // Button: Delete Selected Entry
        JButton deleteEntryButton = new JButton("Delete Selected Entry");
        deleteEntryButton.addActionListener(e -> {
            JournalEntry selectedEntry = recentEntriesList.getSelectedValue();
            if (selectedEntry == null) {
                JOptionPane.showMessageDialog(JournalApp.this, "Please select an entry to delete.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(JournalApp.this,
                "Are you sure you want to delete this entry?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                journalManager.getEntries().remove(selectedEntry);
                journalManager.saveEntries();
                updateRecentEntries();
                graphPanel.repaint();
            }
        });

        // Button: Edit Selected Entry
        JButton editEntryButton = new JButton("Edit Selected Entry");
        editEntryButton.addActionListener(e -> {
            JournalEntry selectedEntry = recentEntriesList.getSelectedValue();
            if (selectedEntry == null) {
                JOptionPane.showMessageDialog(JournalApp.this, "Please select an entry to edit.");
                return;
            }
            int index = journalManager.getEntries().indexOf(selectedEntry);
            if (index != -1) {
                EditEntryDialog dialog = new EditEntryDialog(JournalApp.this, selectedEntry, tagsManager);
                dialog.setVisible(true);
                if (dialog.isSucceeded()) {
                    JournalEntry editedEntry = dialog.getJournalEntry();
                    journalManager.getEntries().set(index, editedEntry);
                    journalManager.saveEntries();
                    updateRecentEntries();
                    graphPanel.repaint();
                }
            }
        });

        // Button: Add New Entry
        JButton addEntryButton = new JButton("Add New Entry");
        addEntryButton.addActionListener(e -> {
            AddEntryDialog dialog = new AddEntryDialog(JournalApp.this, tagsManager);
            dialog.setVisible(true);
            if (dialog.isSucceeded()) {
                JournalEntry newEntry = dialog.getJournalEntry();
                journalManager.addEntry(newEntry);
                journalManager.saveEntries();
                updateRecentEntries();
                graphPanel.repaint();
            }
        });

        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(e -> {
            // Open the unified filter dialog.
            FilterDialog dialog = new FilterDialog(JournalApp.this, tagsManager, journalManager.getEntries());
            dialog.setVisible(true);
            if (dialog.isSucceeded()) {
                String category = dialog.getSelectedCategory();
                String value = dialog.getSelectedValue();
                if ("All".equals(value)) {
                    // Clear both filters.
                    currentTagFilter = null;
                    currentLocationFilter = null;
                } else if ("Tag".equals(category)) {
                    currentTagFilter = value;
                    currentLocationFilter = null;
                } else if ("Location".equals(category)) {
                    currentLocationFilter = value;
                    currentTagFilter = null;
                }
            updateRecentEntries();
            }
        });

        // NEW: Button to manage global tags.
        JButton manageTagsButton = new JButton("Manage Global Tags");
        manageTagsButton.addActionListener(e -> {
            ManageGlobalTagsDialog dialog = new ManageGlobalTagsDialog(JournalApp.this, tagsManager, journalManager);
            dialog.setVisible(true);
            // Optionally, refresh filter dialogs or UI components if needed.
        });

        // Add buttons to the bottom panel
        bottomPanel.add(filterButton);
        bottomPanel.add(manageTagsButton);
        bottomPanel.add(viewEntryButton);
        bottomPanel.add(addEntryButton);
        bottomPanel.add(editEntryButton);
        bottomPanel.add(deleteEntryButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Updates the entries list according to the current filter.
     * If currentTagFilter is null, show all. Otherwise, show only entries
     * whose tags contain currentTagFilter.
     */
    private void updateRecentEntries() {
        recentEntriesModel.clear();
        List<JournalEntry> sortedEntries = new ArrayList<>(journalManager.getEntries());
        // Sort entries by date descending.
        sortedEntries.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        int count = 0;
        for (JournalEntry entry : sortedEntries) {
            boolean tagOk = (currentTagFilter == null || entry.getTags().contains(currentTagFilter));
            boolean locationOk = (currentLocationFilter == null || entry.getLocation().equals(currentLocationFilter));
            if (tagOk && locationOk) {
                recentEntriesModel.addElement(entry);
                count++;
            }
        }
    }

    /**
     * The main method to launch the application.
     * It initializes the password manager and prompts the user for authentication.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Initialize the PasswordManager with the password file.
            PasswordManager pm = new PasswordManager("password.json");

            // If no password is set, prompt the user to create one.
            if (!pm.isPasswordSet()) {
                PasswordDialog setDialog = new PasswordDialog(null, true);
                setDialog.setVisible(true);
                if (setDialog.isSucceeded()) {
                    pm.setPassword(setDialog.getPassword());
                } else {
                    System.exit(0);
                }
            }

            // Now prompt for password entry.
            PasswordDialog loginDialog = new PasswordDialog(null, false);
            loginDialog.setVisible(true);
            if (loginDialog.isSucceeded() && pm.verifyPassword(loginDialog.getPassword())) {
                JournalApp app = new JournalApp();
                app.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect password. Exiting.");
                System.exit(0);
            }
        });
    }
}