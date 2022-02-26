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
public class SummaryPanel {
    final Logger logger = LoggerFactory.getLogger(SummaryPanel.class);
    Panel summary = new Panel();
    Label title = new Label("");
    Label rates = new Label("");
    CassandraNode node;
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    TerminalSize size;

    SummaryPanel(CassandraNode the_node) {
        node = the_node;
        // summary.setPreferredSize(new TerminalSize(80, 4));

        summary.setLayoutManager(new LinearLayout());
        summary.addComponent(title);
        summary.addComponent(rates);
        summary.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        summary.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        update();
    }

    public void update() {
        if (!node.update() || node.metrics == null) {
            return;
        }
        size = summary.getSize();
        update_title();
        update_rates();
    }

    private void optimize_label(Label label, String[] text) {
        Integer width = size.getColumns();
        Integer textWidthSummary = 0;
        for (String substring : text) {
            textWidthSummary += substring.length();
        }
        Integer separatorWidth = Math.max(3, (width - textWidthSummary) / (text.length - 1));
        String separator = String.format(String.format("%%%ds", separatorWidth), "");
        label.setText(String.join(separator, text));
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
            String.format("Reads %s/s latency %s",
                          Util.Humanize(node.metrics.readLatencyOneMinute),
                          Util.Humanize(node.metrics.readLatency)),
            String.format("Writes %s/s  latency %s",
                          Util.Humanize(node.metrics.writeLatencyOneMinute),
                          Util.Humanize(node.metrics.writeLatency)),
            String.format("Pending Tasks: %d", node.metrics.pendingTasks)
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
