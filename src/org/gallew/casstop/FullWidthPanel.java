package org.gallew.casstop;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import java.lang.Math;
import java.lang.String;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.gallew.casstop.Util;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by begallew on 5/4/16.
 */
public class FullWidthPanel extends Panel {
    final Logger logger = LoggerFactory.getLogger(FullWidthPanel.class);
    CassandraNode node;
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    TerminalSize size;
    Integer rows = 2;
    String title = "FullWidthPanel";
    
    FullWidthPanel(CassandraNode the_node, Integer columns) {
        super();
        node = the_node;
        setLayoutManager(new LinearLayout());
        size = new TerminalSize(columns, rows);
        setPreferredSize(size);
    }

    public void update() {
        size = getParent().getSize().withRows(rows);
        if (size.getColumns() == 0)
            return;
        size = size.withColumns(size.getColumns() -2);
        TerminalSize oldSize = getSize();
        if (size.getColumns() != oldSize.getColumns()) {
            logger.info("Setting size to: {}x{} from {}x{}",
                        size.getColumns(), size.getRows(),
                        oldSize.getColumns(), oldSize.getRows());
            setPreferredSize(size.withRows(rows));
        }
        return;
    }

    public void optimize_label(Label label, String[] text) {
        Integer width = size.getColumns();
        Integer textWidthSummary = 0;
        for (String substring : text) {
            textWidthSummary += substring.length();
        }
        Integer separatorWidth = Math.max(3, (width - textWidthSummary) / (text.length - 1));
        String separator = String.format(String.format(" %%%ds", separatorWidth), "");
        label.setText(String.join(separator, text));
    }

    public void text_output(Integer rows, Integer columns) {

    }
    
}
