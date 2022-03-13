package org.gallew.casstop;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import java.lang.Math;
import java.lang.String;
import java.time.LocalTime;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import org.gallew.casstop.Util;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Netstats extends FullWidthPanel {
    final Logger logger = LoggerFactory.getLogger(Netstats.class);
    Integer toggle = 0;
    
    Netstats(CassandraNode the_node, Integer columns) {
        super(the_node, columns);
        data = new ArrayList<String>();
    }

    public void update_data() {
        toggle = toggle + 1;
        logger.info("update_data: toggle={}", toggle);
        if (toggle < 3) {
            data.add("more data");
            return;
        }
        data.clear();
        toggle = 0;
    }

    public void text_output(Integer rows, Integer columns) {
        for ( String str : data ) 
            System.out.println(str);
    }
}
