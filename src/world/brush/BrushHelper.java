package world.brush;

import comp.Circle;
import world.Tile;
import world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Josip
 */
public final class BrushHelper {

	/**
	 * Determines all tiles from the middle point 'tile' in radius 'radius'
	 * @param tile The tile which represents the "middle" point of the circle.
	 * @param radius The radius to look for tiles.
	 * @return The tiles determined.
	 */
	public static List<Tile> determineTiles(Tile tile, int radius) {
		Circle rad = new Circle();
		LinkedList<Tile> edits = new LinkedList<Tile>();
		World w = tile.getWorld();
		rad.setX(tile.getGridX());
		rad.setY(tile.getGridY());
		rad.setRadius(radius);

		// define a rectangular array of tiles
		for(int x = tile.getGridX() - radius; x < tile.getGridX() + radius; x++) {
			for(int y = tile.getGridY() - radius; y < tile.getGridY() + radius; y++) {
				if(!w.isTileValid(x, y)) continue;
				Tile checkTile = w.getTileAt(x, y);
				if(rad.contains(checkTile.gridCenter())) {
					edits.add(checkTile);
				}
			}
		}
		return edits;
	}

}
