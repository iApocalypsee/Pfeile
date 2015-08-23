package general;

public final class Util {

	private Util() {
	}

	public static String stacktraceString(final Throwable throwable) {
		String res = "Stack trace from " + throwable + ":\n";
		for(StackTraceElement element : throwable.getStackTrace()) {
			res += "  -> " + element.getClassName() + "::" + element.getMethodName() + " (" + element.getFileName() + ":" + element.getLineNumber() + ")";
			if(element.isNativeMethod()) {
				res += " (native)";
			}
			res += "\n";
		}
		return res;
	}

}
