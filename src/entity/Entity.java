package entity;

import player.Inventory;
import player.weapon.AttackContainer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import world.ITile;
import world.Tile;

/**
 * @author Josip
 * @version 20.05.2014
 */
public class Entity implements IEntity {

	private Tile tile;
	//private Inventory inventory = new Inventory()
	@Override
	public int getTileX() {
		return tile.getGridX();
	}

	@Override
	public int getTileY() {
		return tile.getGridY();
	}

	@Override
	public ITile getLocation() {
		return tile;
	}

	@Override
	public Inventory getInventory() {
		throw new NotImplementedException();
	}
}
