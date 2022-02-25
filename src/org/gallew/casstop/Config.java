package org.gallew.casstop;

import org.apache.commons.cli.*;

public class Config {
    String nodename = "localhost";
    String port = "7199";
    Integer update_delay = 5000; // 5 seconds (in milliseconds)

    public Config(String[] args) {
        
        Options options = new Options();
        options.addOption("h", "help", false, "Help");
        options.addOption("H", "host", true, "Cassandra node name/address");
        options.addOption("p", "port", true, "JMX port");
        options.addOption("u", "update", true, "Delay between updates in seconds");

        CommandLineParser parser = new DefaultParser();
        CommandLine line;
        try {
            // parse the command line arguments
            line = parser.parse(options, args);
            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("casstop", options);
                System.exit(0);
            }
            parse_hostname(System.getenv("CQLSH_HOST")); // If an environment value is set, use it
            // CLI overrides environment
            if (line.hasOption("update")) {
                update_delay = Integer.parseInt(line.getOptionValue("update")) * 1000; // Need value in msec. 
            }
            if (line.hasOption("host")) {
                parse_hostname(line.getOptionValue("host"));
            }
            if (line.hasOption("port")) {
                port = line.getOptionValue("port");
            }
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("casstop", options);
            System.exit(1);
        }
    }

    private void parse_hostname(String name) {
        if (name == null) {
            return;
        }
        
        String[] parts = name.split(":"); // In case someone wanted to be clever with host:port
        if (parts.length == 2) {
            nodename = parts[0];
            port = parts[1];
        } else {
            nodename = name;
        }
    }
}
