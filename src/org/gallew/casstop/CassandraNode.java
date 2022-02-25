package org.gallew.casstop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;

public class CassandraNode {
    // May have to subclass this if we're unable to get the information we need from a different version
    final Logger logger = LoggerFactory.getLogger(CassandraNode.class);
    JMXConnection conn;
    String nodename;
    Integer port;
    // Metrics
    Integer live_nodes;
    String cassandra_version;
    String cluster_name;
    long update_delay_in_ms = 5000; // Wire in a way to change this?
    long last_update = new Date().getTime() - 5000;
    NodeData metrics;

    public CassandraNode(String new_nodename, Integer new_port) throws java.io.IOException {
        nodename = new_nodename;
        port = new_port;
        conn = new JMXConnection(new_nodename, new_port);
        cassandra_version = conn.getString("org.apache.cassandra.db:type=StorageService", "ReleaseVersion");
        cluster_name = conn.getString("org.apache.cassandra.db:type=StorageService", "ClusterName");
    }

    public void update() {
        long start_time = new Date().getTime();
        if ((start_time-last_update) < update_delay_in_ms) {
            return;
        }
        logger.info("attempting update");
        if (!conn.alive) {      // Ensure we didn't lose our connection while sleeping
           conn = new JMXConnection(nodename, port);
        }
        try {
            metrics = new NodeData(conn);
        } catch (java.io.IOException the_exception) {
            return;
        }
            
        if (conn.alive) {       // Can happen for many reasons
            logger.debug("Successfully loaded data for {}", nodename);
        } else {
            logger.warn("Failed to load data for {}", nodename);

        }
        last_update = start_time;
    }
}
