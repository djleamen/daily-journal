import java.awt.Component;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

/**
 * Custom cell renderer for displaying journal entries in a list.
 * Formats entries to show date and title together.
 */
public class JournalEntryRenderer extends DefaultListCellRenderer {
    private final transient DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        if (value instanceof JournalEntry) {
            JournalEntry entry = (JournalEntry) value;
            value = entry.getDate().format(formatter) + " - " + entry.getTitle();
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
