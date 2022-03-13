package org.gallew.casstop;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.lang.Math;
import java.lang.String;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.gallew.casstop.Util;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import javax.management.openmbean.CompositeDataSupport;

public class CompactionHistory extends FullWidthPanel {
    final Logger logger = LoggerFactory.getLogger(CompactionHistory.class);

    CompactionHistory(CassandraNode the_node, Integer columns) {
        super(the_node, columns);
        data = new ArrayList<String>();
    }

    public void update_data() {
        // logger.info("Compaction history keySet: {}", node.metrics.compaction_history.keySet());
        // logger.info("Compaction history values: {}", node.metrics.compaction_history.values());
        data.clear();
        if (node.metrics.compaction_history == null) {
            return;
        }
        ArrayList<CompactionHistoryDatum> history = new ArrayList<CompactionHistoryDatum>();
        Integer maxKeyLen = 0;
        Integer maxColLen = 0;
        Integer maxWidth = Math.max(getSize().getColumns() - 2, 20);
        logger.info("update_data: maxWidth set to {}", maxWidth);
        for (CompositeDataSupport entry : (Collection<CompositeDataSupport>)node.metrics.compaction_history.values()) {
            CompactionHistoryDatum datum = new CompactionHistoryDatum(entry);
            maxKeyLen = Math.max(datum.keyLength(), maxKeyLen);
            maxColLen = Math.max(datum.colLength(), maxColLen);
            if (!datum.keyspace_name.startsWith("system") || node.include_system)
                history.add(datum);
        }
        Collections.sort(history);
        for (CompactionHistoryDatum datum : history)
            data.add(datum.format(maxKeyLen, maxColLen, maxWidth));
        logger.info("update_data: produced {} rows of {}", data.size(), node.metrics.compaction_history.size());
    }
}
