import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

/**
 * The {@code JournalGraphPanel} class creates a panel that visualizes journal entries 
 * as a heatmap-style calendar. Each cell represents a day, and the color intensity 
 * indicates the number of entries for that day.
 */
public class JournalGraphPanel extends JPanel {
    private final transient JournalManager journalManager;
    private Map<LocalDate, Integer> dateEntryCount;
    private static final int CELL_SIZE = 15;
    private static final int CELL_PADDING = 3;

    /**
     * Constructs a {@code JournalGraphPanel} with the given {@code JournalManager}.
     *
     * @param manager the {@code JournalManager} that provides journal entries
     */
    public JournalGraphPanel(JournalManager manager) {
        this.journalManager = manager;
        computeEntryCounts();
    }

    /**
     * Computes the number of journal entries for each date and stores the results in a map.
     * This method iterates through all journal entries provided by the {@code JournalManager}
     * and counts the occurrences for each date.
     */
    private void computeEntryCounts() {
        dateEntryCount = new HashMap<>();
        for (JournalEntry entry : journalManager.getEntries()) {
            LocalDate date = entry.getDate();
            dateEntryCount.put(date, dateEntryCount.getOrDefault(date, 0) + 1);
        }
    }

    /**
     * Paints the heatmap-style calendar on the panel. Each cell represents a day of the year,
     * and its color intensity corresponds to the number of journal entries for that day.
     *
     * @param g the {@code Graphics} object used for painting
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        computeEntryCounts();
        Graphics2D g2d = (Graphics2D) g;
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        LocalDate startDate = LocalDate.of(year, 1, 1);

        int x = 0;
        int y = startDate.getDayOfWeek().getValue() % 7;
        LocalDate date = startDate;
        while (date.getYear() == year) {
            int count = dateEntryCount.getOrDefault(date, 0);
            Color color = getColorForCount(count);
            g2d.setColor(color);
            int drawX = x * (CELL_SIZE + CELL_PADDING);
            int drawY = y * (CELL_SIZE + CELL_PADDING);
            g2d.fillRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
            date = date.plusDays(1);
            y++;
            if (y > 6) {
                y = 0;
                x++;
            }
        }
    }

    /**
     * Determines the color to use for a cell based on the number of journal entries for that day.
     * The color intensity increases with the number of entries.
     *
     * @param count the number of journal entries for the day
     * @return the {@code Color} corresponding to the entry count
     */
    private Color getColorForCount(int count) {
        Color color;
        switch (count) {
            case 0:
                color = Color.WHITE;
                break;
            case 1:
                color = new Color(198, 228, 139);
                break;
            case 2:
                color = new Color(123, 201, 111);
                break;
            default:
                color = new Color(35, 154, 59);
                break;
        }
        return color;
    }
}