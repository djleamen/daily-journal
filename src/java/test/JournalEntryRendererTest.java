import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.time.LocalDate;
import java.util.Collections;
import javax.swing.JLabel;
import javax.swing.JList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;

@DisabledIf("isHeadless")
class JournalEntryRendererTest {

    static boolean isHeadless() {
        return GraphicsEnvironment.isHeadless();
    }

    @Test
    void rendersJournalEntryAsDateAndTitle() {
        JournalEntryRenderer renderer = new JournalEntryRenderer();
        JournalEntry entry = new JournalEntry("My Title",
                LocalDate.of(2024, 6, 15), "Loc",
                Collections.emptyList(), "body");
        JList<JournalEntry> list = new JList<>();

        Component c = renderer.getListCellRendererComponent(list, entry, 0, false, false);

        assertTrue(c instanceof JLabel);
        assertEquals("2024-06-15 - My Title", ((JLabel) c).getText());
    }

    @Test
    void rendersNonEntryValuesUnchanged() {
        JournalEntryRenderer renderer = new JournalEntryRenderer();
        JList<Object> list = new JList<>();

        Component c = renderer.getListCellRendererComponent(list, "raw text", 0, true, false);
        assertTrue(c instanceof JLabel);
        assertEquals("raw text", ((JLabel) c).getText());
    }
}
