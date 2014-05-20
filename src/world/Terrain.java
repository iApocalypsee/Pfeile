package world;

import misc.metadata.OverrideMetaList;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Josip
 * @version 20.05.2014
 */
public class Terrain implements ITerrain {

	private OverrideMetaList metalist = new OverrideMetaList(this);

	private ArrayList<ArrayList<Tile>> tiles = new ArrayList<ArrayList<Tile>>();
	private LinkedList<Field> fields = new LinkedList<Field>();

	@Override
	public ITile getTileAt(int x, int y) {
		return tiles.get(x).get(y);
	}

	@Override
	public IField getFieldAt(int x, int y) {
		return tiles.get(x).get(y).getField();
	}

	/**
	 * Returns the metadata associated with the key, or <code>null</code>
	 * if none exists.
	 *
	 * @param key The key.
	 * @return The metadata.
	 */
	@Override
	public Object getMetadata(String key) {
		return metalist.get(key);
	}

	/**
	 * Sets a metadata.
	 *
	 * @param key The key.
	 * @param val The value.
	 */
	@Override
	public void setMetadata(String key, Object val) {
		metalist.addMeta(key, val);
	}

	/**
	 * Removes a metadata and returns the object that is being deleted,
	 * or null, if nothing has been deleted.
	 *
	 * @param key The key.
	 * @return The object that has been deleted, or null, if nothing has
	 * been deleted.
	 */
	@Override
	public Object removeMetadata(String key) {
		Object o = metalist.get(key);
		metalist.deleteMeta(key);
		return o;
	}
}
