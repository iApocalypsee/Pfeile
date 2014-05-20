package world;

import entity.Entity;
import gui.Drawable;
import misc.metadata.OverrideMetaList;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Josip
 * @version 20.05.2014
 */
public class Tile implements ITile, Drawable {

	private int gridX, gridY;
	private IField field;
	private LinkedList<Entity> entities = new LinkedList<Entity>();
	private Color color;
	private OverrideMetaList metalist = new OverrideMetaList(this);
	@Override
	public int getGridX() {
		return gridX;
	}

	@Override
	public int getGridY() {
		return gridY;
	}

	@Override
	public IField getField() {
		return field;
	}

	@Override
	public List<Entity> getEntities() {
		return Collections.unmodifiableList(entities);
	}

	@Override
	public Color getColor() {
		return color;
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

	@Override
	public void draw(Graphics2D g) {
		throw new NotImplementedException();
	}
}
