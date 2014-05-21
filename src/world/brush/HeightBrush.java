package world.brush;

import comp.Circle;
import world.ITile;
import world.Tile;
import world.World;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Josip
 * @version 21.05.2014
 */
public class HeightBrush implements IBrush {

	private static final String height_check_meta_key = "pfeile.gen.heightbrushcheck";
	private static final int DEFAULT_HEIGHT_INCR = 3;

	int thickness;
	int heightIncrement = DEFAULT_HEIGHT_INCR;

	public HeightBrush() {
		thickness = DEFAULT_THICKNESS;
	}

	/**
	 * Returns the thickness of the brush.
	 * The thickness is the radius of the brush. Every tile in the radius
	 * gets "painted" by the brush (its metadata or anything else is going to
	 * be edited).
	 *
	 * @return The thickness of the brush.
	 */
	@Override
	public int getThickness() {
		return thickness;
	}

	/**
	 * Assigns data to given tiles and the surrounding tiles.
	 *
	 * @param tileArray The tiles to be edited.
	 */
	@Override
	public void assign(List<? extends ITile> tileArray) {
		// some helper vars
		List<Tile> tiles = (List<Tile>) tileArray;
		LinkedList<Tile> edits = new LinkedList<Tile>();

		// outer for loop, iterating over every tile that has to be changed
		for(Tile tile : tiles) {
			// determine every tile that have to be changed
			LinkedList<Tile> painted = determineTiles(tile);
			// iterate over the selections
			for(Tile paintTile : painted) {
				// check whether the metadata key is already set
				// if no check would be there, a bug would rise up
				// I don't want to edit the height multiple times in one
				// draw of the brush
				if(paintTile.getMetadata(height_check_meta_key) == null) {
					try {
						// access the 'height' field of the tile and increment/decrement it
						Field heightField = paintTile.getClass().getDeclaredField("height");
						heightField.setAccessible(true);
						heightField.setInt(paintTile, paintTile.getHeight() + heightIncrement);
						paintTile.setMetadata(height_check_meta_key, true);
						heightField.setAccessible(false);
						edits.add(paintTile);
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// iterate now over all the edits to delete the temporary metadata objects
		for(Tile tile : edits) {
			tile.removeMetadata(height_check_meta_key);
		}
	}

	private LinkedList<Tile> determineTiles(Tile t) {
		Circle rad = new Circle();
		LinkedList<Tile> edits = new LinkedList<Tile>();
		World w = t.getWorld();
		rad.setX(t.getGridX());
		rad.setY(t.getGridY());
		rad.setRadius(thickness);

		// define a rectangular array of tiles
		for(int x = t.getGridX() - thickness; x < t.getGridX() + thickness; x++) {
			for(int y = t.getGridY() - thickness; y < t.getGridY() + thickness; y++) {
				Tile checkTile = w.getTileAt(x, y);
				if(rad.contains(checkTile.gridCenter())) {
					edits.add(checkTile);
				}
			}
		}
		return edits;
	}

	/*
	public void assign(ArrayList<Tile> tileArray) {
		for(Tile tile : tileArray) {

		}
	}
	*/

	/**
	 * Sets the thickness of the height brush.
	 * @param thickness The thickness.
	 */
	public void setThickness(int thickness) {
		if(thickness <= 0) throw new IllegalArgumentException("Negative values not permitted.");
		this.thickness = thickness;
	}

	/**
	 * Returns the height increment of the brush.
	 * @return The height increment of the brush.
	 */
	public int getHeightIncrement() {
		return heightIncrement;
	}

	/**
	 * Sets the height increment of the height brush.
	 * @param heightIncrement The new height increment per painting.
	 */
	public void setHeightIncrement(int heightIncrement) {
		this.heightIncrement = heightIncrement;
	}

}
