package org.gallew.casstop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Casstop {
    final static Logger logger = LoggerFactory.getLogger(Casstop.class);
    static CassandraNode node;
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
        try {
            node = new CassandraNode(host, port);
            logger.debug("Got a {} for {}", node, host);
        } catch (java.io.IOException the_exception) {
            logger.error("IO Error on node " + node + ": " + the_exception.toString());
	    System.exit(2);
        } catch (java.lang.NullPointerException the_exception) {
            // it's OK, we don't need to do anything.
            logger.error("NPE on node {}: {}", host, the_exception.toString());
            the_exception.printStackTrace(System.out);
	    System.exit(3);
            }
        Display display = new Display(node);
        System.exit(0);
    }

}
