package general;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/** Thread which prints the System Properties in the console */
public class SystemProperties {

    /**
     * Prints the system properties listed in {@link java.lang.System#getProperty(String)} and a separation line.
     */
    public static void printSystemProperties() {
        if (System.getProperty("os.name").equalsIgnoreCase("Linux")) {
            System.setProperty("java.awt.headless", "false");
        }
        Properties sys_props = System.getProperties();

        System.out.println("SYSTEM PROPERTIES");
        System.out.println("AWT headless? " + System.getProperty("java.awt.headless"));
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Java version: " + sys_props.getProperty("java.version"));
        System.out.println("OS: " + sys_props.getProperty("os.name") + "; (Version " + sys_props.getProperty("os.version") + ")");
        System.out.println("Maximum memory in JVM: " + Math.round(Runtime.getRuntime().maxMemory() / (1024 * 1024)) + " MB");
        System.out.println("Total memory in JVM: " + Math.round(Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " MB");
        System.out.println("Free memory  in JVM: " + Math.round(Runtime.getRuntime().freeMemory() / (1024 * 1024)) + " MB");
        System.out.println("-------------------------------\n");
    }

    /**
     * Returns the name of the computer, which is listed in the account setting of your computer. It is also the local
     * host name of the computer.
     *
     * @return the name of the computer (z.B. "Daniel-COM")
     */
    public static String getComputerName () {
        String hostName = "Unknown";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        return hostName;
    }
}
