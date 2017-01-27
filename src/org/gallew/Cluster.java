package org.gallew;

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
import sun.nio.ch.ThreadPool;

import static java.lang.Thread.sleep;

import java.util.Date;

public class Cluster {
    ArrayList<CassandraNode> node_list;
    ArrayList<String> nodes;
    String cassandra_version;
    Integer update_delay = 5;
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
                System.out.println("IO Error on node " + node + ": " + the_exception.toString());

            } catch (java.lang.NullPointerException the_exception) {
                // it's OK, we don't need to do anything.
                System.out.println(the_exception);
                System.out.println("NPE on node " + node + ": " + the_exception.toString());
                the_exception.printStackTrace(System.out);
            }
        }
        System.out.println(node_list);
        System.out.println(cassandra_version);
        update();
        logger.error("done with initial update");
    }

    public void update() {
        long start_time = new Date().getTime();
        // First, kick off all of the threads to get some data
        for (CassandraNode node : node_list) {
            threadPool.execute(node);
        }
        // Next, wait for them to complete
        while (threadPool.getActiveCount() > 0) {
            try {
                sleep(50);
            } catch (java.lang.InterruptedException the_exception) {
                // nothing to see here!
            }
        }
        // Finally, we need to sleep long enough to finish up our refresh cycle time.
        while (true) {
            long remainder = (new Date().getTime()) - start_time;
            if (remainder < (update_delay * 1000l)) {
                try {
                    long delay = (update_delay * 1000) - remainder;
                    logger.debug("about to sleep for {} millis: remainder={}", delay, remainder);
                    sleep(delay);
                } catch (java.lang.InterruptedException the_exception) {
                    // Still nothing to see!
                    logger.error("Interrupted");
                }
            } else {
                break;
            }
        }
    }
}
