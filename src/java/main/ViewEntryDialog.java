/*
 * The ViewEntryDialog class provides a graphical user interface (GUI) dialog
 * for viewing a journal entry. It displays the entry's metadata (title, date, location, tags)
 * and content in a scrollable text area. The dialog includes a close button to dismiss it.
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * The {@code ViewEntryDialog} class provides a graphical user interface (GUI) dialog
 * for viewing a journal entry. It displays the entry's metadata (title, date, location, tags)
 * and content in a scrollable text area. The dialog includes a close button to dismiss it.
 */
public class ViewEntryDialog extends JDialog {

    /**
     * Constructs a {@code ViewEntryDialog} instance.
     *
     * @param parent the parent frame of the dialog
     * @param entry  the journal entry to be displayed
     */
    public ViewEntryDialog(Frame parent, JournalEntry entry) {
        super(parent, "View Entry", true);

        // Set a reasonable size (adjust as needed)
        setSize(500, 400);
        setLocationRelativeTo(parent);

        // Main panel with some padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(mainPanel);

        // Top panel for metadata (title, date, location, tags)
        JPanel metaPanel = new JPanel(new GridLayout(0, 1, 5, 5));

        // Add metadata labels to the panel
        metaPanel.add(new JLabel("<html><b>Title:</b> " + entry.getTitle() + "</html>"));
        metaPanel.add(new JLabel("<html><b>Date:</b> " + entry.getDate() + "</html>"));
        metaPanel.add(new JLabel("<html><b>Location:</b> " + entry.getLocation() + "</html>"));

        // Build a comma-separated string for tags
        String tagsString = String.join(", ", entry.getTags());
        metaPanel.add(new JLabel("<html><b>Tags:</b> " + tagsString + "</html>"));

        mainPanel.add(metaPanel, BorderLayout.NORTH);

        // Center panel: scrollable text area for the content
        JTextArea contentArea = new JTextArea(entry.getContent());
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        mainPanel.add(contentScroll, BorderLayout.CENTER);

        // Bottom panel: Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
}