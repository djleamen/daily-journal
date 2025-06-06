/**
 * The JournalGraphPanel class is responsible for rendering a heatmap-style calendar
 * inspired by GitHub's contribution graph. It visualizes journal entries as colored squares
 * for each day of the year. A color gradient is used to indicate the intensity of entries,
 * with lighter colors representing fewer entries and darker colors representing more entries.
 */

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code JournalGraphPanel} class creates a panel that visualizes journal entries 
 * as a heatmap-style calendar. Each cell represents a day, and the color intensity 
 * indicates the number of entries for that day.
 */
public class JournalGraphPanel extends JPanel {
    private JournalManager journalManager;
    private Map<LocalDate, Integer> dateEntryCount;
    private int cellSize = 15;
    private int cellPadding = 3;

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
            int drawX = x * (cellSize + cellPadding);
            int drawY = y * (cellSize + cellPadding);
            g2d.fillRect(drawX, drawY, cellSize, cellSize);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawRect(drawX, drawY, cellSize, cellSize);
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
        if (count == 0) {
            return Color.WHITE;
        } else if (count == 1) {
            return new Color(198, 228, 139);
        } else if (count == 2) {
            return new Color(123, 201, 111);
        } else {
            return new Color(35, 154, 59);
        }
    }
}