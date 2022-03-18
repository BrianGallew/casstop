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
        data.clear();
        if (node.metrics.compaction_history == null) {
            return;
        }
        ArrayList<CompactionHistoryDatum> history = new ArrayList<CompactionHistoryDatum>();
        Integer maxWidth = Math.max(getSize().getColumns() - 2, 20);
        logger.debug("update_data: maxWidth set to {}", maxWidth);
        CompactionHistoryDatum datum = new CompactionHistoryDatum();
        Integer maxKeyLen = datum.keyLength();
        Integer maxColLen = datum.colLength();
        history.add(datum);
        for (CompositeDataSupport entry : (Collection<CompositeDataSupport>)node.metrics.compaction_history.values()) {
            datum = new CompactionHistoryDatum(entry);
            maxKeyLen = Math.max(datum.keyLength(), maxKeyLen);
            maxColLen = Math.max(datum.colLength(), maxColLen);
            if (!datum.keyspace_name.startsWith("system") || node.include_system)
                history.add(datum);
        }
        Collections.sort(history);
        for (CompactionHistoryDatum datum2 : history)
            data.add(datum2.format(maxKeyLen, maxColLen, maxWidth));
        logger.debug("update_data: produced {} rows of {}", data.size(), node.metrics.compaction_history.size());
    }
}
