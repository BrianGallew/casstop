package org.gallew.casstop;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import java.lang.Class;
import java.lang.Math;
import java.lang.String;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    ArrayList<String> data;
    Class borderClass;
    
    FullWidthPanel(CassandraNode the_node, Integer columns) {
        super();
        try {
            borderClass = Class.forName("com.googlecode.lanterna.gui2.Border");
        } catch (ClassNotFoundException the_exception) {
            logger.error("WTH: {}", the_exception);
            System.exit(-99);
        }
        node = the_node;
        setLayoutManager(new LinearLayout());
        size = new TerminalSize(columns, rows);
        setPreferredSize(size);
    }

    public Integer update(Integer maxrows) {
        if (data != null) {
            update_data();
            rows = display_data(maxrows-2);
        }
        size = getParent().getSize().withRows(rows);
        if (size.getColumns() == 0)
            return rows;
        size = size.withColumns(size.getColumns() -2);
        TerminalSize oldSize = getSize();
            logger.info("Setting size to: {}x{} from {}x{}",
                        size.getColumns(), size.getRows(),
                        oldSize.getColumns(), oldSize.getRows());
            setPreferredSize(size.withRows(rows));
        return rows+2;
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

    public Integer text_output(Integer rows, Integer columns) {
        Integer returnValue = 0;
        if (data != null) {
            System.out.println();
            returnValue = 1;
            for ( String str : data ) {
                System.out.println(str);
                returnValue += 1;
            }
        }
        return returnValue;
    }

    public void update_data() {}

    public Integer display_data(Integer maxrows) {
        Component target;
        Component parent = getParent();
        removeAllComponents();
        Integer count = 0;
        // Find the component to make invisible.  If we were wrapped with a Border, use the Border.
        if (borderClass.isInstance(parent)) {
            target = (Component)parent;
        } else {
            target = (Component)this;
        }
        // No data?  Don't display anything, including the bounding box
        if (data.size() == 0) {
            target.setVisible(false);
            return 0;
        } else {
            target.setVisible(true);
        }
        for (String line : data) {
            if (count < maxrows) {
                addComponent(new Label(line));
                count += 1;
            } else {
                break;
            }
        }
        return count;
    }
    
}
