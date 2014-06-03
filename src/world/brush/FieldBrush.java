package world.brush;

import comp.Circle;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import world.IBaseTile;
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
public class FieldBrush implements IBrush {

	private static final String field_check_meta_key = "pfeile.gen.fieldbrushcheck";

	private int thickness;
	private world.Field wField = null;

	public FieldBrush() {
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

	@Override
	public void setThickness(int thickness) {
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

	public world.Field getField() {
		return wField;
	}

	public void setField(world.Field field) {
		this.wField = field;
	}

	/**
	 * Assigns data to given tiles and the surrounding tiles.
	 *
	 * @param tileArray The tiles to be edited.
	 */
	@Override
	public void assign(List<IBaseTile> tileArray) {
		// some pre-asserts
		if(wField == null) throw new NullPointerException();

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
				if(paintTile.getMetadata(field_check_meta_key) == null) {
					try {
						// access the 'height' field of the tile and increment/decrement it
						Field fieldField = paintTile.getClass().getDeclaredField("field");
						fieldField.setAccessible(true);
						fieldField.set(paintTile, wField);
						paintTile.setMetadata(field_check_meta_key, true);
						fieldField.setAccessible(false);
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
		for(IBaseTile tile : edits) {
			tile.removeMetadata(field_check_meta_key);
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
