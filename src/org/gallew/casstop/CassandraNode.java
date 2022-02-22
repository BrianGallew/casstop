package org.gallew.casstop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by begallew on 3/2/16.
 * <p>
 *     Load up data from a Cassandra node.
 */
public class CassandraNode implements Runnable {
    // May have to subclass this if we're unable to get the information we need from a different version
    final Logger logger = LoggerFactory.getLogger(CassandraNode.class);
    JMXConnection conn;
    String nodename;
    Integer port;
    // Metrics
    NodeData metrics = new NodeData();

    public CassandraNode(String new_nodename, Integer new_port) throws java.io.IOException {
        nodename = new_nodename;
        port = new_port;
        conn = new JMXConnection(new_nodename, new_port);
    }

    public void run() {
        NodeData new_metrics = new NodeData();

        new_metrics.status = conn.getString("org.apache.cassandra.db:type=StorageService", "OperationMode");
        new_metrics.load = conn.getLong("org.apache.cassandra.metrics:type=Storage,name=Load", "Count");
        new_metrics.totalHints = conn.getLong("org.apache.cassandra.metrics:type=Storage,name=TotalHints", "Count");
        new_metrics.totalHintsInProgress = conn.getLong("org.apache.cassandra.metrics:type=Storage,name=TotalHintsInProgress", "Count");
        new_metrics.pendingTasks = conn.getInteger("org.apache.cassandra.metrics:type=Compaction,name=PendingTasks", "Value");
        new_metrics.readLatency = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency","75thPercentile");
        new_metrics.readLatencyOneMinute = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency","OneMinuteRate");
        new_metrics.writeLatency = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Write,name=Latency","75thPercentile");
        new_metrics.writeLatencyOneMinute = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Write,name=Latency","OneMinuteRate");
        metrics = new_metrics;
        if (conn.alive) {
            logger.debug("Successfully loaded data for {}", nodename);
        } else {
            logger.warn("Failed to load data for {}", nodename);

        }

    }
}
