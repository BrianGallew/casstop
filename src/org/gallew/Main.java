package org.gallew;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    final static Logger logger = LoggerFactory.getLogger(Main.class);
    static Cluster cluster;
    static Integer port = 7199;
    static String host;
    public static void main(String[] args) {
        logger.error("classpath: {}",System.getProperty("java.class.path"));
        if (args.length == 1) {
            host = args[0];
        } else if (args.length == 2) {
            host = args[0];
            port = Integer.decode(args[1]);
        } else {
            logger.error("Usage: casstop NODENAME [PORT]");
        }
        cluster = new Cluster(host, port);
        if (cluster.node_list.size() == 0) {
            logger.error("Unable to connect to {}:{}", host, port);
            System.exit(1);
        }
        ClusterDisplay display = new ClusterDisplay(cluster);
        System.exit(0);
    }

}
