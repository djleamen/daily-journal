import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.*;

/**
 * The {@code LocationFilterDialog} class creates a dialog for filtering journal entries 
 * by location. It displays a dropdown of unique locations extracted from the provided 
 * journal entries.
 */
public class LocationFilterDialog extends JDialog {
    private boolean succeeded;
    private JComboBox<String> locationComboBox;
    private String selectedLocation;

    /**
     * Constructs a {@code LocationFilterDialog}.
     *
     * @param parent  the parent frame of the dialog
     * @param entries the list of journal entries to extract locations from
     */
    public LocationFilterDialog(Frame parent, List<JournalEntry> entries) {
        super(parent, "Filter by Location", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        locationComboBox = new JComboBox<>();
        locationComboBox.addItem("All");  // "All" indicates no filtering

        // Use a set to gather unique, non-empty locations from journal entries.
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
     * @return {@code true} if the dialog was closed successfully, {@code false} otherwise
     */
    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * Returns the location selected by the user.
     *
     * @return the selected location, or {@code null} if no location was selected
     */
    public String getSelectedLocation() {
        return selectedLocation;
    }
}