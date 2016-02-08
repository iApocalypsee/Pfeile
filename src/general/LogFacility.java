package general;

import java.util.concurrent.TimeUnit;

/**
 * Class for logging operations, mainly to the console.
 * @author Josip
 */
public final class LogFacility {

	private LogFacility() {
	}

	/**
	 * Logs a message to the console. The message is pushed with {@link LogFacility.LoggingLevel#Info} level.
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
		String timestamp = LogFacility.getTimestamp();
		String threadLabel = "[" + Thread.currentThread().getName() + "]";

		if(level == LoggingLevel.Error) {
			System.err.printf("[%s] [%s] [%s]: %s\n", timestamp, lstr, threadLabel, msg);
		} else {
			System.out.printf("[%s] [%s] [%s]: %s\n", timestamp, lstr, threadLabel, msg);
		}
	}

	public static void log(String msg, String level) {
		log(msg, LoggingLevel.valueOf(level));
	}

	public static void log(String msg, String level, String label) {
		log("[(" + label.toLowerCase() + ")] -> " + msg, level);
	}

	public static void log(Object any) {
		log(any.toString());
	}

	public static void log(Object any, LoggingLevel level) {
		log(any.toString(), level);
	}

	public static void log(Object any, String level) {
		log(any, LoggingLevel.valueOf(level));
	}

	public static void putSeparationLine() {
		System.out.println();
	}

	/**
	 * Logs the current stack trace to the console.
	 */
	public static void logCurrentStackTrace() {
		StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
		String log = "Stack trace (most recent call first): \n";
		for (int i = 1; i < stackTrace.length; i++) {
			StackTraceElement e = stackTrace[i];
			if (e.getLineNumber() >= 100) {
				log += "\tline " + e.getLineNumber() + "\t" + e.getClassName() + "::" + e.getMethodName();
			} else {
				log += "\tline " + e.getLineNumber() + "\t\t" + e.getClassName() + "::" + e.getMethodName();
			}

			if (e.isNativeMethod()) {
				log += " <<native>>";
			}
			log += "\n";
		}
		log(log);
	}

	public static void logCurrentStackTrace(String msg, LoggingLevel level) {
		StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
		String log = msg + ": \n" + getCutStackTrace(1);
		log(log, level);
	}

	public static void logCurrentStackTrace(String msg, String loggingLevel) {
		logCurrentStackTrace(msg, LoggingLevel.valueOf(loggingLevel));
	}

	/**
	 * Logs the stack trace element where execution is currently.
	 */
	public static void logCurrentMethodLocation() {
		log(getStackFrameCall(2));
	}

	public static String getCurrentMethodLocation() {
		// I want the previous stack to print out, because there is the line to the call to this method.
		return getStackFrameCall(2);
	}

	public static String getStackFrameCall(int index) {
		StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
		StackTraceElement ref = stackTrace[index];
		return "In " + ref.getClassName() + "::" + ref.getMethodName() + " @ line " + ref.getLineNumber();
	}

	public static void logMethodWithMessage(String msg) {
		logMethodWithMessage(msg, LoggingLevel.Info);
	}

	public static void logMethodWithMessage(String msg, LoggingLevel level) {
		String endmsg = getStackFrameCall(3) + "\n";
		String[] newLineSplits = msg.split("\\r?\\n");
		for(String str : newLineSplits) {
			endmsg += "::\t\t" + str + "\n";
		}
		log(endmsg, level);
	}

	/**
	 * The severity of the message printed to the console. <p>
	 * Will appear in square brackets in the printout.
	 */
	public enum LoggingLevel {
		Debug, Info, Warning, Error
	}

	private static String getTimestamp() {
		final long currentTime = System.currentTimeMillis();
		final long executionTime = currentTime - Main.getProgramStartTime();
		return String.format("%d.%d.%d.%d",
				TimeUnit.MILLISECONDS.toHours(executionTime),
				TimeUnit.MILLISECONDS.toMinutes(executionTime) % 60,
				TimeUnit.MILLISECONDS.toSeconds(executionTime) % 60,
				TimeUnit.MILLISECONDS.toMillis(executionTime) % 1000);
	}

	private static String getCutStackTrace(int cutIndices) {
		if(cutIndices < 0) throw new IllegalArgumentException("Negative values not allowed");
		final StackTraceElement[] stackTrace = new Exception().getStackTrace();
		String log = "";
		for (int i = 1 + cutIndices; i < stackTrace.length; i++) {
			StackTraceElement e = stackTrace[i];
			if (e.getLineNumber() >= 100) {
				log += "\tline " + e.getLineNumber() + "\t" + e.getClassName() + "::" + e.getMethodName();
			} else {
				log += "\tline " + e.getLineNumber() + "\t\t" + e.getClassName() + "::" + e.getMethodName();
			}

			if (e.isNativeMethod()) {
				log += " <<native>>";
			}
			log += "\n";
		}
		return log;
	}

}
