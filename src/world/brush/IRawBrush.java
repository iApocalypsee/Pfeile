package world.brush;

import java.awt.*;
import java.util.List;

/**
 * @author Josip Palavra
 * @version 01.06.2014
 */
public interface IRawBrush {

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
	 * Assigns data to the specified coordinates.
	 * @param pointList The point list.
	 */
	void assignPoints(List<Point> pointList);

}
