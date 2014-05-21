package world;

import entity.Entity;
import entity.IEntity;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Josip
 */
public class World implements IWorld {

	private static final long serialVersionUID = 8474968677356694778L;
	Terrain terrain;

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
		terrain = new Terrain(sizeX, sizeY);
		ArrayList<ArrayList<Tile>> t = terrain.tiles;
		for (ArrayList<Tile> tiles : t) {
			for (Tile tile : tiles) {
				tile.world = this;
			}
		}
	}

	@Override
	public int getSizeX() {
		return terrain.tiles.size();
	}

	@Override
	public int getSizeY() {
		return terrain.tiles.get(0).size();
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
	public List<? extends IEntity> collectEntities(Class<? extends IEntity> clazz) {
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
}
