package world;

import comp.GUIUpdater;
import gui.Drawable;
import misc.metadata.OverrideMetaList;
import world.brush.BrushHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Josip
 * @version 20.05.2014
 * @deprecated Use {@link world.BaseTerrain} instead.
 */
@Deprecated
public class Terrain implements ITerrain, Drawable, GUIUpdater {

	private static final long serialVersionUID = -2440813561948973995L;
	private OverrideMetaList metalist = new OverrideMetaList(this);

	LinkedList<LinkedList<Tile>> tiles = new LinkedList<LinkedList<Tile>>();
	LinkedList<Field> fields = new LinkedList<Field>();

	/**
	 * Creates a new terrain with empty tiles.
	 * @param sizeX The width of the world in tiles.
	 * @param sizeY The height of the world in tiles.
	 */
	public Terrain(int sizeX, int sizeY) {
		//tiles.ensureCapacity(sizeX);
		for(int i = 0; i < sizeX; i++) {
			LinkedList<Tile> build = new LinkedList<Tile>();
			//build.ensureCapacity(sizeY);
			for(int y = 0; y < sizeY; y++) {
				Tile tile = new Tile();
				tile.gridX = i;
				tile.gridY = y;
				build.add(tile);
			}
			tiles.add(build);
		}
	}

	protected LinkedList<LinkedList<Tile>> getTiles() {
		return tiles;
	}

	@Override
	public Tile getTileAt(int x, int y) {
		return tiles.get(x).get(y);
	}

	@Override
	public Field getFieldAt(int x, int y) {
		return tiles.get(x).get(y).getField();
	}

	public World getWorld() {
		return tiles.get(0).get(0).getWorld();
	}

	@Override
	public int getSizeX() {
		return tiles.size();
	}

	@Override
	public int getSizeY() {
		return tiles.get(0).size();
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
		for(int x = 0; x < tiles.size(); x++) {
			for(int y = 0; y < tiles.get(x).size(); y++) {
				tiles.get(x).get(y).draw(g);
			}
		}

	}

	@Override
	public void updateGUI() {
		for(int x = 0; x < tiles.size(); x++) {
			for(int y = 0; y < tiles.get(x).size(); y++) {
				tiles.get(x).get(y).updateGUI();
			}
		}
	}
}
