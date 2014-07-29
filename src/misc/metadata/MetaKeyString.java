package misc.metadata;

import java.io.Serializable;

/**
 * A key for the metadata system.
 * TODO Key conventions
 * @author Josip
 */
public class MetaKeyString implements Serializable {

	private static final long serialVersionUID = 398745569026650651L;

	/**
	 * The raw string object representing the key.
	 */
	private String keystr;

	/**
	 * Creates a key string for meta data.
	 * @param keystr The key string.
	 */
	public MetaKeyString(String keystr) {
		checkConventions(keystr);
		this.keystr = keystr;
	}

	private void checkConventions(String str) {
		if(str.indexOf('.') == 0) throw new ConventionDisregardException();
	}

	/**
	 * Returns the raw string.
	 * @return The raw key string.
	 */
	public String str() {
		return keystr;
	}

	public boolean equals(String str) {
		return keystr.equals(str);
	}

	public boolean equals(MetaKeyString str) {
		return keystr.equals(str.keystr);
	}

	public String[] splits() {
		return keystr.split(".");
	}

	public static final class ConventionDisregardException extends RuntimeException {
		private static final long serialVersionUID = -5439898306239150768L;
	}

}
