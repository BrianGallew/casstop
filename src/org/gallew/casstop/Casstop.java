package org.gallew.casstop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Casstop {
    final static Logger logger = LoggerFactory.getLogger(Casstop.class);
    static CassandraNode node;
    public static void main(String[] args) {
        Config config = new Config(args);
        try {
            node = new CassandraNode(config);
            logger.debug("Got a {} for {}", node, config.nodename);
            MyGui display = new MyGui(node);
            display.loop();
        } catch (java.io.IOException the_exception) {
            logger.error("IO Error on node " + config.nodename + ": " + the_exception.toString());
	    System.exit(2);
        } catch (java.lang.NullPointerException the_exception) {
            // it's OK, we don't need to do anything.
            logger.error("NPE on node {}: {}", config.nodename, the_exception.toString());
            the_exception.printStackTrace(System.out);
	    System.exit(3);
        } catch (java.lang.InterruptedException the_exception) {
            logger.error("InterruptedException on node {}: {}", config.nodename, the_exception.toString());
            the_exception.printStackTrace(System.out);
	    System.exit(4);
        }
        System.exit(0);
    }

}
