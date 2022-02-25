package org.gallew.casstop;

import java.util.Set;
import javax.management.openmbean.TabularData;

/**
 * Created by begallew on 5/4/16.
 */
public class NodeData {
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
    public TabularData compaction_history;

    public NodeData(JMXConnection conn) throws java.io.IOException {

        status = conn.getString("org.apache.cassandra.db:type=StorageService", "OperationMode");
        load = conn.getLong("org.apache.cassandra.metrics:type=Storage,name=Load", "Count");
        totalHints = conn.getLong("org.apache.cassandra.metrics:type=Storage,name=TotalHints", "Count");
        totalHintsInProgress = conn.getLong("org.apache.cassandra.metrics:type=Storage,name=TotalHintsInProgress", "Count");
        pendingTasks = conn.getInteger("org.apache.cassandra.metrics:type=Compaction,name=PendingTasks", "Value");
        readLatency = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency","75thPercentile");
        readLatencyOneMinute = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency","OneMinuteRate");
        writeLatency = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Write,name=Latency","75thPercentile");
        writeLatencyOneMinute = conn.getDouble("org.apache.cassandra.metrics:type=ClientRequest,scope=Write,name=Latency","OneMinuteRate");
        current_streams = conn.getSet("org.apache.cassandra.net:type=StreamManager","CurrentStreams");
        compaction_history = conn.getTabularData("org.apache.cassandra.db:type=CompactionManager","CompactionHistory");
    }
}
