package general;

import java.util.Date;

/**
 * Class for logging operations, mainly to the console.
 * @author Josip
 */
public final class Logger {

	private Logger() {
	}

	/**
	 * Logs a message to the console. The message is pushed with {@link general.Logger.LoggingLevel#Info} level.
	 * @param msg The message to log.
	 */
	public static void log(String msg) {
		log(msg, LoggingLevel.Info);
	}

	/**
	 * Logs a message to the console with given severity.
	 * @param msg The message to log.
	 * @param level The message's level of severity.
	 */
	public static void log(String msg, LoggingLevel level) {
		String lstr = level.toString().toUpperCase();
		String timestamp = new Date().toString();

		if(level == LoggingLevel.Error) {
			System.err.printf("[%s] [%s]: %s\n", lstr, timestamp, msg);
		} else {
			System.out.printf("[%s] [%s]: %s\n", lstr, timestamp, msg);
		}
	}

	/**
	 * Logs the current stack trace to the console.
	 */
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

	/**
	 * Logs the stack trace element where execution is currently.
	 */
	public static void logCurrentMethodLocation() {
		StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
		// I want the previous stack to print out, because there is the line to the call to this method.
		StackTraceElement ref = stackTrace[1];
		log("In " + ref.getClassName() + "::" + ref.getMethodName() + " @ line " + ref.getLineNumber());
	}

	/**
	 * The severity of the message printed to the console. <p>
	 * Will appear in square brackets in the printout.
	 */
	public static enum LoggingLevel {
		Debug, Info, Warning, Error
	}

}
