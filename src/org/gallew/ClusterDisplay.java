package org.gallew;

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
public class ClusterDisplay {
    SummaryPanel summary;
    Terminal terminal;
    Screen screen;
    BasicWindow window;
    MultiWindowTextGUI gui;
    final Logger logger = LoggerFactory.getLogger(ClusterDisplay.class);

    ClusterDisplay(Cluster cluster) {
        try {
            terminal = new DefaultTerminalFactory().createTerminal();

            screen = new TerminalScreen(terminal);
            screen.startScreen();
            // Create panel to hold components
            summary = new SummaryPanel(cluster);

            // Create window to hold the panel
            window = new BasicWindow();
            window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
            window.setComponent(summary.summary);
            // Create gui and start gui
            gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
            gui.addWindow(window);
            gui.updateScreen();
            while (true) {
                KeyStroke keyStroke = screen.pollInput();
                if (keyStroke != null) {
                    Character key = keyStroke.getCharacter();
                    if (key != null && key == 'q') break;
                }
                gui.updateScreen();
                sleep(50);
            }
        } catch (Exception the_exception)
        {
            logger.error("Exception building GUI: {}", the_exception);
            // nothing
        }
    }
}

