package org.gallew.casstop;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import javax.management.openmbean.TabularData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts data via JMX.
 */
public class NodeData {
    final Logger logger = LoggerFactory.getLogger(SummaryPanel.class);
    public Long load = 0L;
    public Integer pendingTasks = 0;
    public Long totalHints = 0L;
    public Long totalHintsInProgress = 0L;
    public String status = "Unknown";
    public Double readLatency = 0.0;
    public Double readLatencyOneMinute = 0.0;
    public Double writeLatency = 0.0;
    public Double writeLatencyOneMinute = 0.0;
    public Set current_streams;
    public Integer nodesUp = 0;
    public Integer nodesDown = 0;
    public TabularData compaction_history;
    public ArrayList compactions;

    public NodeData(JMXConnection conn) throws java.io.IOException {
        Long startTime = Instant.now().toEpochMilli();
        status = conn.getString("org.apache.cassandra.db:type=StorageService", "OperationMode");
        load = conn.getLong("org.apache.cassandra.metrics:type=Storage,name=Load", "Count");
        totalHints = conn.getLong("org.apache.cassandra.metrics:type=Storage,name=TotalHints", "Count");
        totalHintsInProgress = conn.getLong("org.apache.cassandra.metrics:type=Storage,name=TotalHintsInProgress", "Count");
        pendingTasks = conn.getInteger("org.apache.cassandra.metrics:type=Compaction,name=PendingTasks", "Value");
        readLatency  = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency","75thPercentile");
        writeLatency = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Write,name=Latency","75thPercentile");
        readLatencyOneMinute  = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency","OneMinuteRate");
        writeLatencyOneMinute = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Write,name=Latency","OneMinuteRate");
        nodesUp = conn.getInteger("org.apache.cassandra.net:type=FailureDetector", "UpEndpointCount");
        nodesDown = conn.getInteger("org.apache.cassandra.net:type=FailureDetector", "DownEndpointCount");
        current_streams = conn.getSet("org.apache.cassandra.net:type=StreamManager","CurrentStreams");
        compactions = conn.getList("org.apache.cassandra.db:type=CompactionManager","Compactions");
        compaction_history = conn.getTabularData("org.apache.cassandra.db:type=CompactionManager","CompactionHistory");
        Long endTime = Instant.now().toEpochMilli();
        logger.info("Time to load data: {}ms", endTime-startTime);
    }
}
