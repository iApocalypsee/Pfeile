package misc.metadata;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Josip
 */
public interface IMetaManagement extends Serializable {

	void addMeta(String keystr, Object val);
	IMetaEntry deleteMeta(String key);
	Object get(String key);

	Collection<MetaKeyString> keys();
	Collection<Object> values();
	Collection<IMetaEntry> pairs();

	boolean hasKey(String key);
	boolean hasValue(Object val);

	IMetadatable getMetadatable();

}
