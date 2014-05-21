package world.brush;

import comp.Circle;
import world.ITile;
import world.Tile;
import world.World;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Josip
 * @version 21.05.2014
 */
public class ColorBrush implements IBrush {

	private static final String color_check_meta_key = "pfeile.gen.colorbrushcheck";

	private int thickness = DEFAULT_THICKNESS;
	private Color color;

	public ColorBrush() {
		color = Color.black;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if(color == null) throw new NullPointerException();
		this.color = color;
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
	 * Sets the thickness of the brush. Should not be
	 * any value less than or equal to 0.
	 *
	 * @param thickness The new thickness.
	 */
	@Override
	public void setThickness(int thickness) {
		this.thickness = thickness;
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
				if(paintTile.getMetadata(color_check_meta_key) == null) {
					try {
						// access the 'height' field of the tile and increment/decrement it
						Field colorField = paintTile.getClass().getDeclaredField("color");
						colorField.setAccessible(true);
						colorField.set(paintTile, color);
						paintTile.setMetadata(color_check_meta_key, true);
						colorField.setAccessible(false);
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
			tile.removeMetadata(color_check_meta_key);
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
}
