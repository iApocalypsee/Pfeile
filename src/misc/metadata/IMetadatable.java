package misc.metadata;

import java.io.Serializable;

/**
 * @author Josip
 * @version 17.05.2014
 */
public interface IMetadatable extends Serializable {

	/**
	 * Returns the metadata associated with the key, or <code>null</code>
	 * if none exists.
	 * @param key The key.
	 * @return The metadata.
	 */
	Object getMetadata(String key);

	/**
	 * Sets a metadata.
	 * @param key The key.
	 * @param val The value.
	 */
	void setMetadata(String key, Object val);

	/**
	 * Removes a metadata and returns the object that is being deleted,
	 * or null, if nothing has been deleted.
	 * @param key The key.
	 * @return The object that has been deleted, or null, if nothing has
	 * been deleted.
	 */
	Object removeMetadata(String key);

}
