package org.gallew.casstop;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import java.lang.Math;
import java.lang.String;
import org.gallew.casstop.Util;

/**
 * Created by begallew on 5/4/16.
 */
public class SummaryPanel {
    Panel summary = new Panel();
    Label live_nodes = new Label("");
    Label dead_nodes = new Label("");
    Label compactions = new Label("");
    Label rrate = new Label("");
    Label rlatency = new Label("");
    Label wrate = new Label("");
    Label wlatency = new Label("");
    Cluster my_cluster;

    SummaryPanel(Cluster cluster) {
        my_cluster = cluster;
        summary.setPreferredSize(new TerminalSize(80, 4));

        summary.setLayoutManager(new GridLayout(8));

        summary.addComponent(new Label("Live Nodes"));
        summary.addComponent(live_nodes);

        summary.addComponent(new Label("Compactions"));
        summary.addComponent(compactions);

        summary.addComponent(new Label("Rrate"));
        summary.addComponent(rrate);

        summary.addComponent(new Label("Rlatency"));
        summary.addComponent(rlatency);


        summary.addComponent(new Label("Dead Nodes"));
        summary.addComponent(dead_nodes);

        summary.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        summary.addComponent(new EmptySpace(new TerminalSize(0, 0)));

        summary.addComponent(new Label("Wrate"));
        summary.addComponent(wrate);

        summary.addComponent(new Label("Wlatency"));
        summary.addComponent(wlatency);
        update();
    }

    public void update() {
        int live_count = 0;
        int dead_count = 0;
        int pending_compactions = 0;
        double read_rate = 0.0;
        double read_latency = 0.0;
        double write_rate = 0.0;
        double write_latency = 0.0;
        for (CassandraNode node : my_cluster.node_list) {
            if (node.conn.alive) {
                live_count += 1;
            } else {
                dead_count += 1;
            }
            pending_compactions += node.metrics.pendingTasks;
            read_rate += node.metrics.readLatencyOneMinute;
            read_latency = Math.max(read_latency, node.metrics.readLatency);
            write_rate += node.metrics.writeLatencyOneMinute;
            write_latency = Math.max(write_latency, node.metrics.writeLatency);
        }
        live_nodes.setText(String.valueOf(live_count));
        dead_nodes.setText(String.valueOf(dead_count));
        compactions.setText(String.valueOf(pending_compactions));
        rrate.setText(Util.Humanize(read_rate));
        rlatency.setText(Util.Humanize(read_latency, " us"));
        wrate.setText(Util.Humanize(write_rate));
        wlatency.setText(Util.Humanize(write_latency, "us"));
    }
}
