package world.brush;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import world.IBaseTile;
import world.tile.SeaTile;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Josip
 * @version 21.05.2014
 */
public class HeightBrush implements IBrush {

	private static final String height_check_meta_key = "pfeile.gen.heightbrushcheck";
	public static final String meta_key = "pfeile.tileheight";
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
	public void assign(List<IBaseTile> tileArray) {
		LinkedList<IBaseTile> edits = new LinkedList<IBaseTile>();

		// outer for loop, iterating over every tile that has to be changed
		for(IBaseTile tile : tileArray) {
			// determine every tile that have to be changed
			LinkedList<IBaseTile> painted = BrushHelper.determineTiles(tile, thickness);
			// iterate over the selections
			for(IBaseTile paintTile : painted) {
				// check whether the metadata key is already set
				// if no check would be there, a bug would rise up
				// I don't want to edit the height multiple times in one
				// draw of the brush
				if(paintTile.getMetadata(height_check_meta_key) == null) {
					/*
					try {
						// access the 'height' field of the tile and increment/decrement it
						Field heightField = paintTile.getClass().getDeclaredField("height");
						heightField.setAccessible(true);
						// RELATIVE
						//heightField.setInt(paintTile, paintTile.getTileHeight() + heightIncrement);
						// ABSOLUTE
						heightField.setInt(paintTile, heightIncrement);
						paintTile.setMetadata(height_check_meta_key, true);
						heightField.setAccessible(false);
						edits.add(paintTile);
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					*/

					if(paintTile instanceof SeaTile) {
						if((Integer) paintTile.getMetadata(meta_key) != 0) {
							paintTile.setMetadata(meta_key, 0);
						}
						continue;
					}

					Object meta = paintTile.getTileHeight();
					if(meta == null) paintTile.setMetadata(meta_key, heightIncrement);
					else paintTile.setMetadata(meta_key, (Integer) meta + heightIncrement);
					paintTile.setMetadata(height_check_meta_key, true);
					edits.add(paintTile);
				}
			}
		}

		// iterate now over all the edits to delete the temporary metadata objects
		for(IBaseTile tile : edits) {
			tile.removeMetadata(height_check_meta_key);
		}
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
	 * Assigns data to the specified coordinates.
	 *
	 * @param pointList The point list.
	 */
	@Override
	public void assignPoints(List<Point> pointList) {
		throw new NotImplementedException();
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
