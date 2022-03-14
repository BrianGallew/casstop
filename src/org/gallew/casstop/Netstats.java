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
    
    Netstats(CassandraNode the_node, Integer columns) {
        super(the_node, columns);
        data = new ArrayList<String>();
    }


    public void update_data() {
        data.clear();
        if ((node.metrics.current_streams == null) || (node.metrics.current_streams.size() == 0)) {
            return;
        }
        
        Integer maxKeyLen = 0;
        Integer maxColLen = 0;
        Integer maxWidth = Math.max(getSize().getColumns() - 2, 20);
        for (Object entry : node.metrics.current_streams) {
            logger.info("update_data: {}", entry);
            data.add(entry.toString());
        }
        // ArrayList<CompactionHistoryDatum> history = new ArrayList<CompactionHistoryDatum>();
        // logger.info("update_data: maxWidth set to {}", maxWidth);
        // for (CompositeDataSupport entry : (Collection<CompositeDataSupport>)node.metrics.compaction_history.values()) {
        //     CompactionHistoryDatum datum = new CompactionHistoryDatum(entry);
        //     maxKeyLen = Math.max(datum.keyLength(), maxKeyLen);
        //     maxColLen = Math.max(datum.colLength(), maxColLen);
        //     if (!datum.keyspace_name.startsWith("system") || node.include_system)
        //         history.add(datum);
        // }
        // Collections.sort(history);
        // for (CompactionHistoryDatum datum : history)
        //     data.add(datum.format(maxKeyLen, maxColLen, maxWidth));
        // logger.info("update_data: produced {} rows of {}", data.size(), node.metrics.compaction_history.size());
    }
}
