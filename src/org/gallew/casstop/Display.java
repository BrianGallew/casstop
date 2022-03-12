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

/**
 * Created by begallew on 5/3/16.
 */
public class Display {
    Screen screen;
    BasicWindow window;
    TerminalSize tsize;
    MultiWindowTextGUI gui;
    final Logger logger = LoggerFactory.getLogger(Display.class);

    Display(CassandraNode node) {
        try {

            screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();
            screen.clear();
            screen.refresh();
            screen.doResizeIfNecessary();
            // Create window to hold the panel
            window = new BasicWindow();
            window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN,
                                          Window.Hint.FIT_TERMINAL_WINDOW,
                                          Window.Hint.NO_DECORATIONS));

            // Create gui
            // gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
            gui = new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen, new DefaultWindowManager());
            gui.addWindow(window);
            ((AsynchronousTextGUIThread)gui.getGUIThread()).start();
            // Create panel to hold components
            Panel canvas = new Panel();
            canvas.setLayoutManager(new LinearLayout());
            window.setComponent(canvas);
            
            // Add the various sections
            node.update();
            SummaryPanel summary = new SummaryPanel(node);
            CompactionStats compactionstats = new CompactionStats(node);
            CompactionHistory compactionhistory = new CompactionHistory(node);
            canvas.addComponent(summary.withBorder(Borders.singleLine("Summary")));
            canvas.addComponent(compactionhistory.withBorder(Borders.singleLine("Compaction History")));
            canvas.addComponent(compactionstats.withBorder(Borders.singleLine("Compaction Stats")));
            // Start gui
            while (true) {
                gui.processInput();
                KeyStroke keyStroke = screen.pollInput();
                if (keyStroke != null) {
                    Character key = keyStroke.getCharacter();
                    if (key != null && key == 'q') {
                        ((AsynchronousTextGUIThread)gui.getGUIThread()).stop();
                        break;
                    }
                }
                summary.update();
                compactionstats.update();
                compactionhistory.update();
                sleep(10);
            }
            tsize = screen.getTerminalSize();
            screen.clear();
            screen.refresh();
            screen.stopScreen();
            ((AsynchronousTextGUIThread)gui.getGUIThread()).stop();
            sleep(10);
            summary.text_output(tsize.getRows(), tsize.getColumns());
        } catch (Exception the_exception)
        {
            logger.error("Exception building GUI: {}", the_exception);
            // nothing
        }
    }
}

