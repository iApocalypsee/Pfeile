package general;

import gui.GameScreen;
import player.Player;

/**
 * @author Josip
 * @version 2/9/14
 */
public class WorldFactory {

	private WorldFactory() {
	}

	public static synchronized World generateDefault(int sizeX, int sizeY) {
		return new World(sizeX, sizeY);
	}

	public static synchronized World generateDefault(int sizeX, int sizeY, long seed) {
		return new World(sizeX, sizeY, seed);
	}

	/**
	 * Checks if on that field a com.github.pfeile.player can spawn.
	 *
	 * @param x The x position.
	 * @param y The y position.
	 * @return A boolean value indicating whether a com.github.pfeile.player can spawn on this position.
	 */
	public static boolean isSpawnPossible(int x, int y) {
		World ref = GameScreen.getInstance().getWorld();
		
		// checking all failure conditions first
		if (ref.isPositionValid(x, y) == false) {
			throw new IllegalArgumentException("Invalid coordinates at 'isSpwanPossible': (" + x + "|" + y + ")");
		}
		
		if (ref.getFieldAt(x, y).isAccessable() == false) {
			return false;
		}
		
		// FIXME There is no use in checking it again
		// It's already checked by isPositionValid(x, y) at the beginning and (x, y) cannot be 0 or sizeX/sizeY because Of r.nextInt(...) in Main
		// --> go to Main: populateWorld()
		// check if the coordinates are not making the edge of the map
		// if at the edge, I would work with null objects
//		if (!(ref.isPositionValid(x - 1, y) && ref.isPositionValid(x, y - 1) && ref.isPositionValid(x + 1, y) && ref.isPositionValid(x, y + 1))) {
//			return false;
//		}
		
		// this part is only useful for the second (or third) player
		if (ref.getPlayerCount() > 0) {
			// check if no com.github.pfeile.player is standing on the tile / field and on no tile in the direct neighborhood 
			if(ref.getFieldAt(x, y).getEntities(Player.class).isEmpty() == false && 
					ref.getFieldAt(x + 1, y).getEntities(Player.class).isEmpty() == false &&
					ref.getFieldAt(x, y + 1).getEntities(Player.class).isEmpty() == false && 
					ref.getFieldAt(x - 1, y).getEntities(Player.class).isEmpty() == false && 
					ref.getFieldAt(x, y - 1).getEntities(Player.class).isEmpty() == false && 
					ref.getFieldAt(x + 1, y + 1).getEntities(Player.class).isEmpty() == false && 
					ref.getFieldAt(x - 1, y - 1).getEntities(Player.class).isEmpty() == false && 
					ref.getFieldAt(x + 1, y - 1).getEntities(Player.class).isEmpty() == false && 
					ref.getFieldAt(x - 1, y + 1).getEntities(Player.class).isEmpty() == false){
				return false;
			}
		}

		// check if at least one com.github.pfeile.player's neighbor tile is accessable aswell
		if(!(ref.getFieldAt(x - 1, y).isAccessable() || 
				ref.getFieldAt(x, y - 1).isAccessable() || 
				ref.getFieldAt(x + 1, y).isAccessable() || 
				ref.getFieldAt(x, y + 1).isAccessable())) {
			return false;
		}
		return true;
	}

}
