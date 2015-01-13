package general;

import java.util.Properties;

/** Thread which prints the System Properties in the console */
public class SystemProperties {

    public void printSystemProperties() {
        if (System.getProperty("os.name").equalsIgnoreCase("Linux")) {
            System.setProperty("java.awt.headless", "false");
        }

        System.out.println("SYSTEM PROPERTIES");
        System.out.println("AWT headless? " + System.getProperty("java.awt.headless"));
        Properties sys_props = System.getProperties();
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Java version: " + sys_props.getProperty("java.version"));
        System.out.println("OS: " + sys_props.getProperty("os.name") + "; (Version " + sys_props.getProperty("os.version") + ")");
        System.out.println("Maximum memory in JVM: " + Math.round(Runtime.getRuntime().maxMemory() / (1024 * 1024)) + " MB");
        System.out.println("Total memory in JVM: " + Math.round(Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " MB");
        System.out.println("Free memory  in JVM: " + Math.round(Runtime.getRuntime().freeMemory() / (1024 * 1024)) + " MB");
        System.out.println("----------------------------------\n");
    }

}
