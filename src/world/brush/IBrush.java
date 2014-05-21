package world.brush;

import world.ITile;

import java.util.Collection;
import java.util.List;

/**
 * @author Josip
 * @version 21.05.2014
 */
public interface IBrush {

	int DEFAULT_THICKNESS = 6;

	/**
	 * Returns the thickness of the brush.
	 * The thickness is the radius of the brush. Every tile in the radius
	 * gets "painted" by the brush (its metadata or anything else is going to
	 * be edited).
	 * @return The thickness of the brush.
	 */
	int getThickness();

	/**
	 * Sets the thickness of the brush. Should not be
	 * any value less than or equal to 0.
	 * @param thickness The new thickness.
	 */
	void setThickness(int thickness);

	/**
	 * Assigns data to given tiles.
	 * @param tileArray The tiles to be edited.
	 */
	void assign(List<? extends ITile> tileArray);

}
