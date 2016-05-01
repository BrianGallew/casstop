package org.gallew;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created by begallew on 3/2/16.
 * <p>
 *     Load up data from a Cassandra node.
 */
public class CassandraNode implements Runnable {
    // May have to subclass this if we're unable to get the information we need from a different version
    final Logger logger = LogManager.getLogger(CassandraNode.class);
    JMXConnection conn;
    String nodename;
    Integer port;
    // Metrics
    Long load = 0L;
    Integer pendingTasks = 0;
    Long totalHints = 0L;
    Long totalHintsInProgress = 0L;
    String status = "Unknown";
    Double readLatency = 0.0;
    Double readLatencyOneMinute = 0.0;
    Double writeLatency = 0.0;
    Double writeLatencyOneMinute = 0.0;

    public CassandraNode(String new_nodename, Integer new_port) throws java.io.IOException {
        nodename = new_nodename;
        port = new_port;
        conn = new JMXConnection(new_nodename, new_port);
    }

    public void run() {
        status = conn.getString("org.apache.cassandra.db:type=StorageService", "OperationMode");
        load = conn.getLong("org.apache.cassandra.metrics:type=Storage,name=Load", "Count");
        totalHints = conn.getLong("org.apache.cassandra.metrics:type=Storage,name=TotalHints", "Count");
        totalHintsInProgress = conn.getLong("org.apache.cassandra.metrics:type=Storage,name=TotalHintsInProgress", "Count");
        pendingTasks = conn.getInteger("org.apache.cassandra.metrics:type=Compaction,name=PendingTasks", "Value");
        readLatency = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency","75thPercentile");
        readLatencyOneMinute = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency","OneMinuteRate");
        writeLatency = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Write,name=Latency","75thPercentile");
        writeLatencyOneMinute = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Write,name=Latency","OneMinuteRate");
        logger.error("Successfully loaded data for {}", nodename);

    }
}
