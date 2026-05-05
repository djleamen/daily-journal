import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class JournalGraphPanelTest {

    @Test
    void constructsWithEmptyManager() {
        JournalManager mgr = new JournalManager("ignored.json");
        JournalGraphPanel panel = new JournalGraphPanel(mgr);
        assertNotNull(panel);
    }

    @Test
    void paintComponentRunsWithEntries() {
        JournalManager mgr = new JournalManager("ignored.json");
        LocalDate today = LocalDate.now();
        mgr.addEntry(new JournalEntry("a", today, "L", Arrays.asList(), "c"));
        mgr.addEntry(new JournalEntry("b", today, "L", Arrays.asList(), "c"));
        mgr.addEntry(new JournalEntry("c", today.minusDays(1), "L", Arrays.asList(), "c"));
        mgr.addEntry(new JournalEntry("d", today.minusDays(2), "L", Arrays.asList(), "c"));
        mgr.addEntry(new JournalEntry("e", today.minusDays(2), "L", Arrays.asList(), "c"));
        mgr.addEntry(new JournalEntry("f", today.minusDays(2), "L", Arrays.asList(), "c"));

        JournalGraphPanel panel = new JournalGraphPanel(mgr);
        panel.setSize(800, 200);

        BufferedImage img = new BufferedImage(800, 200, BufferedImage.TYPE_INT_ARGB);
        // Should not throw while iterating through every day of the year.
        assertDoesNotThrow(() -> panel.paint(img.getGraphics()));
    }
}
