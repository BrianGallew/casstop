package org.gallew.casstop;

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

}
