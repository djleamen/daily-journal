import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * The {@code JournalApp} class is the main application class for the Daily Journal 
 * application. This class initializes the GUI and manages the overall application structure.
 */
public class JournalApp extends JFrame {
    private transient JournalManager journalManager;
    private transient TagsManager tagsManager;
    private JournalGraphPanel graphPanel;
    private JournalEntryListPanel entryListPanel;

    /**
     * Constructs the {@code JournalApp} and initializes the GUI components.
     */
    public JournalApp() {
        initializeManagers();
        initializeFrame();
        initializeComponents();
    }

    /**
     * Initializes the data managers.
     */
    private void initializeManagers() {
        journalManager = new JournalManager("journal_entries.json");
        journalManager.loadEntries();

        tagsManager = new TagsManager("tags.json");
        tagsManager.loadTags();
    }

    /**
     * Initializes the main frame properties.
     */
    private void initializeFrame() {
        setTitle("Daily Journal");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(980, 600);
        setLocationRelativeTo(null);
    }

    /**
     * Initializes and arranges the main GUI components.
     */
    private void initializeComponents() {
        JPanel mainPanel = createMainPanel();
        setContentPane(mainPanel);

        // Add components to main panel
        mainPanel.add(createGraphPanel(), BorderLayout.NORTH);
        mainPanel.add(createEntryListPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
    }

    /**
     * Creates the main panel with border layout.
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        return mainPanel;
    }

    /**
     * Creates the graph panel for year overview.
     */
    private JournalGraphPanel createGraphPanel() {
        graphPanel = new JournalGraphPanel(journalManager);
        graphPanel.setPreferredSize(new Dimension(800, 200));
        return graphPanel;
    }

    /**
     * Creates the entry list panel.
     */
    private JournalEntryListPanel createEntryListPanel() {
        entryListPanel = new JournalEntryListPanel(journalManager);
        return entryListPanel;
    }

    /**
     * Creates the button panel with all action buttons.
     */
    private JournalButtonPanel createButtonPanel() {
        return new JournalButtonPanel(this, journalManager, tagsManager, entryListPanel, graphPanel);
    }

    /**
     * The main method to launch the application.
     * It initializes the password manager and prompts the user for authentication.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PasswordManager passwordManager = new PasswordManager("password.json");
            
            if (JournalAuthentication.authenticate(passwordManager)) {
                JournalApp app = new JournalApp();
                app.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}