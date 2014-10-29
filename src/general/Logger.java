package general;

import java.util.Date;

/**
 * Class for logging operations, mainly to the console.
 * @author Josip
 */
public final class Logger {

	private Logger() {
	}

	public static void log(String msg) {
		log(msg, LoggingLevel.Info);
	}

	public static void log(String msg, LoggingLevel level) {
		String lstr = level.toString().toUpperCase();
		String timestamp = new Date().toString();

		if(level == LoggingLevel.Error) {
			System.err.printf("[%s] [%s]: %s\n", lstr, timestamp, msg);
		} else {
			System.out.printf("[%s] [%s]: %s\n", lstr, timestamp, msg);
		}
	}

	public static void logCurrentStackTrace() {
		StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
		String log = "Stack trace (most recent call first): \n";
		for(StackTraceElement e : stackTrace) {
			if(e.getLineNumber() >= 100) {
				log += "\tline " + e.getLineNumber() + "\t" + e.getClassName() + "::" + e.getMethodName();
			} else {
				log += "\tline " + e.getLineNumber() + "\t\t" + e.getClassName() + "::" + e.getMethodName();
			}

			if(e.isNativeMethod()) {
				log += " <<native>>";
			}
			log += "\n";
		}
		log(log);
	}

	public static enum LoggingLevel {
		Debug, Info, Warning, Error
	}

}
