package entity;

import player.Inventory;
import world.ITile;

/**
 * @author Josip
 * @version 15.05.2014
 */
public interface IEntity {

	int getTileX();
	int getTileY();
	ITile getLocation();
	Inventory getInventory();

}
