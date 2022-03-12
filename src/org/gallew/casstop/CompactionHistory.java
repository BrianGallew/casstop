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

public class CompactionHistory extends FullWidthPanel {
    final Logger logger = LoggerFactory.getLogger(CompactionHistory.class);
    Label title = new Label("");
    Label rates = new Label("");

    CompactionHistory(CassandraNode the_node) {
        super(the_node);
        addComponent(title);
        addComponent(rates);
    }

    public void update() {
        super.update();
        update_title();
        update_rates();
    }

    private void update_title() {
        Integer nodesUp = 0;
        Integer nodesDown = 0;
        if (node.metrics != null) {
            nodesUp = node.metrics.nodesUp;
            nodesDown = node.metrics.nodesDown;
        }
        String[] parts = {
            String.format("%s:%s(%s)", node.nodename, node.cluster_name, node.cassandra_version),
            node.metrics.status,
            String.format("Up: %d  Down: %d",nodesUp, nodesDown),
            String.format("Last Update: %s", LocalTime.now().format(timeFormatter))};
        optimize_label(title, parts);
        logger.debug("Updated time to {}", LocalTime.now().format(timeFormatter));
    }

    private void update_rates() {
        String[] parts = {
            String.format("Reads %3s/s  latency %3s usec",
                          Util.Humanize(node.metrics.readLatencyOneMinute),
                          Util.Humanize(node.metrics.readLatency)),
            String.format("Writes %3s/s  latency %3s usec",
                          Util.Humanize(node.metrics.writeLatencyOneMinute),
                          Util.Humanize(node.metrics.writeLatency)),
            String.format("Pending Tasks: %3d", node.metrics.pendingTasks)
        };
        optimize_label(rates, parts);
    }

    public void text_output(Integer rows, Integer columns) {
        System.out.println(title.getText());
        System.out.println(rates.getText());
        System.out.println("Rows: " + size.getRows());
        System.out.println("Columns: " + size.getColumns());
    }
}
