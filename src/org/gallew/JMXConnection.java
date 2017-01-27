package org.gallew;

/**
 * Created by begallew on 4/20/16.
 */

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMXConnection {
    final Logger logger = LoggerFactory.getLogger(JMXConnection.class);
    int retries = 2;
    int trial = 0;
    JMXServiceURL url;
    MBeanServerConnection mbsc;
    boolean alive = true;

    JMXConnection(String host, Integer port) {
        try {
            url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port.toString() + "/jmxrmi");
        } catch (java.net.MalformedURLException the_exception) {
            System.out.println("Improperly formed URL: " + the_exception.toString());
            System.exit(1);
        } catch (java.io.IOException the_exception) {
            alive = false;
        }
        connect();
    }

    private void connect() {
        //Make a JMX connection to a host
        try {
            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
            mbsc = jmxc.getMBeanServerConnection();
            alive = true;
        } catch (java.io.IOException the_exception) {
            alive = false;
        }
    }

    @SuppressWarnings("unchecked")
    public String getString(String key, String attribute) {
        // Extract/return a String attribute.
        if (!alive) return "";
        try {
            String retval = (String) mbsc.getAttribute(new ObjectName(key), attribute);
            trial = 0;
            return retval;
        } catch (Exception the_exception) {
            trial = trial + 1;
            if (trial < retries) {
                connect();
                return getString(key, attribute);
            }
            logger.error("Error reading bean {}: {}", key, the_exception);
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    public ArrayList getList(String key, String attribute) {
        // Extract/return a List attribute.
        if (!alive) return new ArrayList(0);

        try {
            ArrayList retval = (ArrayList) mbsc.getAttribute(new ObjectName(key), attribute);
            trial = 0;
            return retval;
        } catch (Exception the_exception) {
            trial = trial + 1;
            if (trial < retries) {
                connect();
                return getList(key, attribute);
            }
            logger.error("Error reading bean {}: {}", key, the_exception);
        }
        return new ArrayList();
    }

    @SuppressWarnings("unchecked")
    public Integer getInteger(String key, String attribute) {
        // Extract/return an Integer attribute.
        if (!alive) return 0;

        try {
            Integer retval = (Integer) mbsc.getAttribute(new ObjectName(key), attribute);
            trial = 0;
            return retval;
        } catch (Exception the_exception) {
            trial = trial + 1;
            if (trial < retries) {
                connect();
                return getInteger(key, attribute);
            }
            logger.error("Error reading bean {}: {}", key, the_exception);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public Long getLong(String key, String attribute) {
        // Extract/return a Long attribute.
        if (!alive) return 0L;

        try {
            Long retval = (Long) mbsc.getAttribute(new ObjectName(key), attribute);
            trial = 0;
            return retval;
        } catch (Exception the_exception) {
            trial = trial + 1;
            if (trial < retries) {
                connect();
                return getLong(key, attribute);
            }
            logger.error("Error reading bean {}: {}", key, the_exception);
        }
        return 0L;
    }

    @SuppressWarnings("unchecked")
    public Double getDouble(String key, String attribute) {
        // Extract/return a Double attribute.
        if (!alive) return 0.0;

        try {
            Double retval = (Double) mbsc.getAttribute(new ObjectName(key), attribute);
            trial = 0;
            return retval;
        } catch (Exception the_exception) {
            trial = trial + 1;
            if (trial < retries) {
                connect();
                return getDouble(key, attribute);
            }
            logger.error("Error reading bean {}: {}", key, the_exception);
        }
        return 0.0;
    }

}
