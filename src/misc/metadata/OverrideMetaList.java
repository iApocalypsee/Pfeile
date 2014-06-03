package misc.metadata;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

/**
 * Represents a metadata override list.
 * This metadata list overrides existing entries if
 * there is an attempt to set an already existing key.
 * <br><br>
 * Note that the serialization of this list will fail if
 * any object given to the meta list does not support
 * the {@link java.io.Serializable} interface.
 * @author Josip
 */
public class OverrideMetaList implements IMetaManagement {

	private static final long serialVersionUID = -7120344130293558296L;
	private final IMetadatable metadatable;

	/**
	 * The entries.
	 */
	private LinkedList<OverrideMetaEntry> entries = new LinkedList<OverrideMetaEntry>();

	/**
	 * Creates a new meta list.
	 * @param metadatable The metadatable. May not be null.
	 */
	public OverrideMetaList(IMetadatable metadatable) {
		if(metadatable == null) throw new NullPointerException();
		this.metadatable = metadatable;
	}

	/*
	public OverrideMetaList(Metadatable scalaMetadatable) {

	}
	*/

	@Override
	public void addMeta(String str, Object val) {
		OverrideMetaEntry entry = null;
		MetaKeyString keystr = new MetaKeyString(str);
		if(!hasKey(str)) {
			entry = new OverrideMetaEntry(keystr, val);
			entries.add(entry);
		} else {
			entry = getEntry(keystr);
			entry.value = val;
		}
	}

	@Override
	public IMetaEntry deleteMeta(String str) {
		MetaKeyString key = new MetaKeyString(str);
		if(!hasKey(str)) throw new NoSuchElementException();
		IMetaEntry entry = getEntry(key);
		entries.remove(entry);
		return entry;
	}

	@Override
	public Object get(String str) {
		MetaKeyString key = new MetaKeyString(str);
		if(!hasKey(str)) {
			return null;
		}

		for(OverrideMetaEntry entry : entries) {
			if(entry.getKey().equals(key)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public List<MetaKeyString> keys() {
		LinkedList<MetaKeyString> keys = new LinkedList<MetaKeyString>();
		for(OverrideMetaEntry entry : entries) {
			keys.add(entry.getKey());
		}
		return Collections.unmodifiableList(keys);
	}

	@Override
	public boolean hasKey(String str) {
		MetaKeyString key = new MetaKeyString(str);
		for(OverrideMetaEntry entry : entries) {
			if(entry.getKey().equals(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasValue(Object val) {
		for(OverrideMetaEntry entry : entries) {
			if(entry.getValue() == val) {
				return true;
			}
		}
		return false;
	}

	@Override
	public IMetadatable getMetadatable() {
		return metadatable;
	}

	private OverrideMetaEntry getEntry(MetaKeyString key) {
		for(OverrideMetaEntry entry : entries) {
			if(entry.getKey().equals(key)) {
				return entry;
			}
		}
		return null;
	}

	@Override
	public List<Object> values() {
		LinkedList<Object> values = new LinkedList<Object>();
		for(OverrideMetaEntry entry : entries) {
			values.add(entry.getValue());
		}
		return Collections.unmodifiableList(values);
	}

	@Override
	public List<IMetaEntry> pairs() {
		LinkedList<IMetaEntry> pairs = new LinkedList<IMetaEntry>();
		for(OverrideMetaEntry entry : entries) {
			pairs.add(entry);
		}
		return Collections.unmodifiableList(pairs);
	}

	/**
	 * Checks the serializability of all objects in the metadata list.
	 * @throws NotSerializableException if any object is not serializable.
	 */
	private void checkSerializability() throws NotSerializableException {
		for(OverrideMetaEntry entry : entries) {
			if(!entry.isSerializable()) {
				throw new NotSerializableException();
			}
		}
	}

	static class OverrideMetaEntry implements IMetaEntry {

		private static final long serialVersionUID = 3219544002843830198L;

		private MetaKeyString key;
		private Object value;

		/**
		 * Creates a new entry.
		 * @param key A key.
		 * @param value A value.
		 * @throws java.lang.NullPointerException if key or value are null.
		 */
		public OverrideMetaEntry(MetaKeyString key, Object value) {
			if(value == null || key == null) throw new NullPointerException();
			this.key = key;
			this.value = value;
		}

		@Override
		public MetaKeyString getKey() {
			return key;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public Serializable serializableInstance() throws NotSerializableException {
			if(isSerializable()) return (Serializable) value;
			else {
				exceptSerializable();
				return null;
			}
		}

		@Override
		public boolean isSerializable() {
			return value instanceof Serializable;
		}

		private void exceptSerializable() throws NotSerializableException {
			if(!isSerializable()) {
				throw new NotSerializableException(value.getClass().getName());
			}
		}
	}

}
