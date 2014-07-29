package world.brush;

import comp.Circle;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import world.IBaseTile;
import world.ITile;
import world.Tile;
import world.World;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Josip
 * @version 21.05.2014
 */
@Deprecated
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
	 * Assigns data to the specified coordinates.
	 *
	 * @param pointList The point list.
	 */
	@Override
	public void assignPoints(List<Point> pointList) {
		throw new NotImplementedException();
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

						try {
							Method method = paintTile.getClass().getMethod("height_$eq", Integer.class);
							method.setAccessible(true);
							//method.invoke()
							paintTile.setMetadata(color_check_meta_key, true);
							//colorField.setAccessible(false);
							edits.add(paintTile);
						} catch (NoSuchMethodException e1) {
							e1.printStackTrace();
						}

					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// iterate now over all the edits to delete the temporary metadata objects
		for(IBaseTile tile : edits) {
			tile.removeMetadata(color_check_meta_key);
		}
	}
}
