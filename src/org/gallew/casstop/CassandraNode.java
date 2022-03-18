package org.gallew.casstop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;

public class CassandraNode {
    // May have to subclass this if we're unable to get the information we need from a different version
    final Logger logger = LoggerFactory.getLogger(CassandraNode.class);
    JMXConnection conn;
    String nodename;
    String port;
    boolean include_system;
    // Metrics
    String cassandra_version;
    String cluster_name;
    long update_delay_in_ms;
    long last_update;
    NodeData metrics;


    public CassandraNode(Config config) throws java.io.IOException {
        nodename = config.nodename;
        port = config.port;
        update_delay_in_ms = config.update_delay;
        include_system = config.system;
        last_update = new Date().getTime() - update_delay_in_ms;
        conn = new JMXConnection(nodename, port);
        if (!conn.alive) {
            logger.error("Unable to make JMX connection to {}:{}", config.nodename, config.port);
            System.exit(4);
        }
        cassandra_version = conn.getString("org.apache.cassandra.db:type=StorageService", "ReleaseVersion");
        cluster_name = conn.getString("org.apache.cassandra.db:type=StorageService", "ClusterName");
    }

    public boolean update() {
        long start_time = new Date().getTime();
        if ((start_time-last_update) < update_delay_in_ms) {
            return false;
        }
        logger.debug("attempting update");
        if (!conn.alive) {      // Ensure we didn't lose our connection while sleeping
           conn = new JMXConnection(nodename, port);
        }
        try {
            NodeData new_metrics = new NodeData(conn);
            metrics = new_metrics;
        } catch (java.io.IOException the_exception) {
            logger.warn("Unable to load metrics via JMX");
            return false;
        }
            
        if (conn.alive) {       // Can happen for many reasons
            logger.debug("Successfully loaded data for {}", nodename);
        } else {
            logger.warn("Failed to load data for {}", nodename);

        }
        last_update = start_time;
        return true;
    }
}
