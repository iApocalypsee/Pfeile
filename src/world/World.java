package world;

import entity.Entity;
import gui.Drawable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Josip
 */
public class World implements IWorld, Drawable {

	private static final long serialVersionUID = 8474968677356694778L;
	Terrain terrain;
	WorldViewport viewport = new WorldViewport(this);

	public World(EditableTerrain terrain) {
		this.terrain = terrain;
		// reset the world reference, since it would
		// cause trouble with an old reference or even null
		ArrayList<ArrayList<Tile>> t = terrain.tiles;
		for (ArrayList<Tile> tiles : t) {
			for (Tile tile : tiles) {
				tile.world = this;
			}
		}

	}

	public World(int sizeX, int sizeY) {
		terrain = new EditableTerrain(sizeX, sizeY);
		ArrayList<ArrayList<Tile>> t = terrain.tiles;
		for (ArrayList<Tile> tiles : t) {
			for (Tile tile : tiles) {
				tile.world = this;
			}
		}
	}

	@Override
	public int getSizeX() {
		return terrain.getSizeX();
	}

	@Override
	public int getSizeY() {
		return terrain.getSizeY();
	}

	@Override
	public Tile getTileAt(int x, int y) {
		return terrain.getTileAt(x, y);
	}

	@Override
	public Field getFieldAt(int x, int y) {
		return terrain.getFieldAt(x, y);
	}

	@Override
	public Terrain getTerrain() {
		return terrain;
	}

	@Override
	public List<Field> getFields() {
		return Collections.unmodifiableList(terrain.fields);
	}

	@Override
	public WorldViewport getViewport() {
		return viewport;
	}

	@Override
	public List<Field> getNeighborFields() {
		throw new NotImplementedException();
	}

	@Override
	public List<Entity> collectEntities() {
		ArrayList<ArrayList<Tile>> tiles = terrain.tiles;
		LinkedList<Entity> entities = new LinkedList<Entity>();
		for (ArrayList<Tile> tile : tiles) {
			for (Tile t : tile) {
				entities.addAll(t.getEntities());
			}
		}
		return Collections.unmodifiableList(entities);
	}

	@Override
	public List<? extends Entity> collectEntities(Class<? extends Entity> clazz) {
		ArrayList<ArrayList<Tile>> tiles = terrain.tiles;
		LinkedList<Entity> entities = new LinkedList<Entity>();
		for (ArrayList<Tile> tile : tiles) {
			for (Tile t : tile) {
				for(Entity e : t.getEntities()) {
					if(e.getClass() == clazz) {
						entities.add(e);
					}
				}
			}
		}
		return Collections.unmodifiableList(entities);
	}

	public boolean isTileValid(int x, int y) {
		return x >= 0 && x < terrain.tiles.size() && y >= 0 &&  y < terrain.tiles.get(x).size();
	}

	@Override
	public void draw(Graphics2D g) {
		terrain.draw(g);
	}
}
