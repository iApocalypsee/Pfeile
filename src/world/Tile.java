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

	private static final long serialVersionUID = 1921867867258539893L;
	/**
	 * The grid coordinates.
	 */
	int gridX, gridY;

	/**
	 * The height of the tile
	 */
	int height;

	/**
	 * The field attached to the tile.
	 */
	IField field;

	/**
	 * The list of all entities currently on the tile.
	 */
	private LinkedList<Entity> entities = new LinkedList<Entity>();

	/**
	 * The display color of the tile.
	 */
	Color color;

	/**
	 * The metalist, just like always.
	 */
	private OverrideMetaList metalist = new OverrideMetaList(this);

	/**
	 * Adds an entity to the tile.
	 * @param entity The entity to add.
	 */
	public void addEntity(Entity entity) {
		entities.add(entity);
	}

	/**
	 * Removes an entity. If the entity does not exist, a statement will be printed
	 * out to <code>stderr</code>.
	 * @param entity The entity.
	 */
	public void removeEntity(Entity entity) {
		if(!entities.contains(entity)) {
			System.err.println("Entity " + entity + " does not exist at (" + gridX + "|" + gridY + ").");
			return;
		}
		entities.remove(entity);
	}

	@Override
	public int getGridX() {
		return gridX;
	}

	@Override
	public int getGridY() {
		return gridY;
	}

	@Override
	public int getHeight() {
		return height;
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
