package general;

import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class SystemProperties {

    /**
     * Prints the system properties listed in {@link java.lang.System#getProperty(String)} and a separation line.
     */
    public static void printSystemProperties() {
        Properties sys_props = System.getProperties();

        if (sys_props.getProperty("os.name").equalsIgnoreCase("Linux")) {
            System.setProperty("java.awt.headless", "false");
        }

        DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();

        System.out.println("\nSYSTEM PROPERTIES");
        System.out.println("AWT headless? " + sys_props.getProperty("java.awt.headless"));
        System.out.println("Java version: " + sys_props.getProperty("java.version"));
        System.out.println("Operating System: " + sys_props.getProperty("os.name") + "; (Version " + sys_props.getProperty("os.version") + ")");
        System.out.println("Maximum memory in JVM: " + Math.round(Runtime.getRuntime().maxMemory() / (1024f * 1024f)) + " MB");
        System.out.println("Total memory in JVM: " + Math.round(Runtime.getRuntime().totalMemory() / (1024f * 1024f)) + " MB");
        System.out.println("Free memory  in JVM: " + Math.round(Runtime.getRuntime().freeMemory() / (1024f * 1024f)) + " MB");
        System.out.println("Display: Size (" + displayMode.getWidth() + " px|" + displayMode.getHeight() + " px) ; " +
                        "Resolution " + Toolkit.getDefaultToolkit().getScreenResolution() + " px/inch ; Refresh rate " +
                        displayMode.getRefreshRate() + " Hz ; Bit depth " + displayMode.getBitDepth() + " bits/px");
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
        LogFacility.putSeparationLine();
    }

    /**
     * Returns the name of the computer, which is listed in the account setting of your computer. It is also the local
     * host name of the computer.
     *
     * @return the name of the computer (e.g. "Daniel-COM")
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
