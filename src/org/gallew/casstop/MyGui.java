package org.gallew.casstop;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Arrays;
import static java.lang.Thread.sleep;

public class MyGui extends MultiWindowTextGUI {
    final Logger logger = LoggerFactory.getLogger(MultiWindowTextGUI.class);
    Screen screen;
    TerminalSize tsize;
    CassandraNode node;
    Panel canvas;
    Boolean stopping = false;

    MyGui(CassandraNode the_node) throws java.io.IOException {
        super(new SeparateTextGUIThread.Factory(), new DefaultTerminalFactory().createScreen(), new DefaultWindowManager());
        node = the_node;
        screen = getScreen();
        tsize = screen.getTerminalSize();
        logger.info("Initial screen size: {}x{}", tsize.getColumns(), tsize.getRows());
        screen.startScreen();
        screen.clear();
        screen.refresh();

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN,
                                      Window.Hint.FIT_TERMINAL_WINDOW,
                                      Window.Hint.NO_DECORATIONS));

        addWindow(window);
        ((AsynchronousTextGUIThread)getGUIThread()).start();

        // Create panel to hold components with the various sections
        canvas = Panels.vertical(new SummaryPanel(node, tsize.getColumns()).withBorder(Borders.singleLine("Summary")),
                                 new Netstats(node, tsize.getColumns()).withBorder(Borders.singleLine("Netstats History")),
                                 new CompactionStats(node, tsize.getColumns()).withBorder(Borders.singleLine("Compaction Stats")),
                                 new CompactionHistory(node, tsize.getColumns()).withBorder(Borders.singleLine("Compaction History"))
                                 );
        window.setComponent(canvas);
    }

    public void loop() throws java.io.IOException, java.lang.InterruptedException {
        // Run the update loop
        while (true) {
            if (stopping) {
                ((AsynchronousTextGUIThread)getGUIThread()).stop();
                break;
            }
            if (node.update()) {
                for ( Component panel : canvas.getChildren())
                    ((FullWidthPanel)(((Border)panel).getComponent())).update();
            }
            sleep(10);
        }
        tsize = screen.getTerminalSize();
        screen.clear();
        screen.refresh();
        screen.stopScreen();
        ((AsynchronousTextGUIThread)getGUIThread()).stop();
        sleep(10);
        for ( Component panel : canvas.getChildren())
            ((FullWidthPanel)(((Border)panel).getComponent())).text_output(tsize.getRows(), tsize.getColumns());

    }

    public boolean handleInput​(KeyStroke key) {
        logger.warn("handling keystroke {}", key);
        Character character = key.getCharacter();
        if (character != null && character == 'q') {
            logger.warn("Got a q, exiting");
            stopping = true;
            return true;
        }
        return false;
            
    }
}

