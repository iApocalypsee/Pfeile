package general;

public final class Util {
	
	/**
	 * Gibt den Logarithmus von Basis und Zahl zurück.
	 * @param a Die Basis.
	 * @param b Das Argument.
	 * @return Der Logarithmus der beiden Zahlen.
	 */
	public static double log(double a, double b) {
		return Math.log10(b) / Math.log10(a);
	}

}
