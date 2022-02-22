package org.gallew.casstop;

/**
 * Created by begallew on 3/2/16.
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.sleep;

import java.util.Date;

public class Cluster  {
    ArrayList<CassandraNode> node_list;
    ArrayList<String> nodes;
    String cassandra_version;
    long update_delay_in_ms = 5000;
    long last_update = 0;
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 60, 60, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    final Logger logger = LoggerFactory.getLogger(Cluster.class);

    Cluster(String start_host, Integer port) {
        /*
        Make a JMX connection to the cluster
        Create a CassandraNode for each cluster member
        */
        JMXConnection conn = new JMXConnection(start_host, port);
        //org.apache.cassandra.db:type=StorageService attribute LiveNodes
        nodes = conn.getList("org.apache.cassandra.db:type=StorageService", "LiveNodes");
        node_list = new ArrayList<CassandraNode>(nodes.size());
        cassandra_version = conn.getString("org.apache.cassandra.db:type=StorageService", "ReleaseVersion");
        conn = null;
        for (String node : nodes) {
            try {
                CassandraNode holder = new CassandraNode(node, port);
                logger.debug("Got a {} for {}", holder, node);
                node_list.add(holder);
            } catch (java.io.IOException the_exception) {
                // it's OK, we don't need to do anything.
                logger.debug("IO Error on node " + node + ": " + the_exception.toString());

            } catch (java.lang.NullPointerException the_exception) {
                // it's OK, we don't need to do anything.
                logger.warn("NPE on node {}: {}", node, the_exception.toString());
                the_exception.printStackTrace(System.out);
            }
        }
        logger.info("Discovered nodes: {}", node_list);
        logger.info("Cluster cassandra version: {}", cassandra_version);
        update();
        logger.error("done with initial update");
    }


    public void update() {
        long start_time = new Date().getTime();
        logger.info("last start was {}, current time is {}, difference is {}", last_update, start_time, last_update-start_time);
        if (last_update> (start_time + update_delay_in_ms)) {
            return;
        }
        // First, kick off all of the threads to get some data
        for (CassandraNode node : node_list) {
            threadPool.execute(node);
        }
        // Next, wait for them to complete
        while (threadPool.getActiveCount() > 0) {
            try {
                sleep(5);
            } catch (java.lang.InterruptedException the_exception) {
                // nothing to see here!
            }
        }
        last_update = start_time;
    }
}
