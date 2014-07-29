package misc.metadata;

import java.io.NotSerializableException;
import java.io.Serializable;

/**
 * @author Josip
 * @version 20.05.2014
 */
public interface IMetaEntry extends Serializable {

	MetaKeyString getKey();
	Object getValue();
	Serializable serializableInstance() throws NotSerializableException;
	boolean isSerializable();

}
