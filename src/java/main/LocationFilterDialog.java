/**
 * The LocationFilterDialog class is responsible for displaying a dialog
 * that allows users to filter journal entries by location. It provides
 * a dropdown menu populated with unique locations extracted from the
 * provided journal entries.
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;

/**
 * A dialog for filtering journal entries by location.
 * Displays a dropdown of unique locations extracted from the provided journal entries.
 */
public class LocationFilterDialog extends JDialog {
    private boolean succeeded;
    private JComboBox<String> locationComboBox;
    private String selectedLocation;

    /**
     * Constructs a LocationFilterDialog.
     *
     * @param parent  The parent frame of the dialog.
     * @param entries The list of journal entries to extract locations from.
     */
    public LocationFilterDialog(Frame parent, List<JournalEntry> entries) {
        super(parent, "Filter by Location", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        locationComboBox = new JComboBox<>();
        locationComboBox.addItem("All");  // "All" means no filtering

        // Use a set to gather unique, non-empty locations.
        Set<String> locations = new TreeSet<>();
        for (JournalEntry entry : entries) {
            String loc = entry.getLocation();
            if (loc != null && !loc.trim().isEmpty()) {
                locations.add(loc);
            }
        }
        for (String loc : locations) {
            locationComboBox.addItem(loc);
        }

        comboPanel.add(new JLabel("Select Location:"));
        comboPanel.add(locationComboBox);
        add(comboPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            selectedLocation = (String) locationComboBox.getSelectedItem();
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
     * Returns whether the dialog was closed successfully (OK button clicked).
     *
     * @return true if the dialog was closed successfully, false otherwise.
     */
    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * Returns the location selected by the user.
     *
     * @return The selected location, or null if no location was selected.
     */
    public String getSelectedLocation() {
        return selectedLocation;
    }
}