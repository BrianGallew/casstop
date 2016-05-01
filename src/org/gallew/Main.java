package org.gallew;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Main {
    final Logger logger = LogManager.getLogger(Main.class);
    static Cluster cluster;

    public static void main(String[] args) {
        if (args.length == 1) {
            cluster = new Cluster(args[0], 7100);
            System.out.println("Finished");

        } else if (args.length == 2) {

            Cluster cluster = new Cluster(args[0], Integer.decode(args[1]));
            System.out.println("Usage: casstop NODENAME");
        } else {
            System.out.println("Usage: casstop NODENAME");
        }
        cluster.update();
    }

}
