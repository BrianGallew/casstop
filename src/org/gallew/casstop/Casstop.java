package org.gallew.casstop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Casstop {
    final static Logger logger = LoggerFactory.getLogger(Casstop.class);
    static Cluster cluster;
    static Integer port = 7199;
    static String host = "localhost";
    public static void main(String[] args) {
        logger.debug("classpath: {}",System.getProperty("java.class.path"));
        if (args.length == 2) {
            host = args[1];
            logger.debug("selected node {}:{}", host, port);
        } else if (args.length == 3) {
            host = args[1];
            port = Integer.decode(args[2]);
            logger.debug("Selected {}:{}", host, port);
        } else if (args.length > 1) {
            logger.error("args.length {}", args.length);
	    System.out.println("Usage: casstop NODENAME [PORT]");
	    System.exit(1);
        }
        cluster = new Cluster(host, port);
        if (cluster.node_list.size() == 0) {
            System.out.printf("Unable to connect to %s:%s\n", host, port);
            logger.error("Unable to connect to {}:{}", host, port);
            System.exit(1);
        }
        ClusterDisplay display = new ClusterDisplay(cluster);
        System.exit(0);
    }

}
