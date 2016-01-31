package geom.interfaces;

/**
 * @author Josip
 * @version 09.05.2014
 */
@Deprecated
public interface Vector {

	/**
	 * Returns the starting point of the vector in x coordinate space.
	 * @return The x starting point.
	 */
	double getStartX();

	/**
	 * Returns the starting point of the vector in y coordinate space.
	 * @return The y starting point.
	 */
	double getStartY();

	/**
	 * Sets the starting point of the vector in x coordinate space.
	 * @param x The x coordinate.
	 */
	void setStartX(double x);

	/**
	 * Sets the starting point of the vector in y coordinate space.
	 * @param x The y coordinate.
	 */
	void setStartY(double y);

	/**
	 * Returns the ending point of the vector in x coordinate space.
	 * @return The x starting point.
	 */
	double getEndX();

	/**
	 * Returns the ending point of the vector in y coordinate space.
	 * @return The y starting point.
	 */
	double getEndY();

	/**
	 * Sets the ending point of the vector in x coordinate space.
	 * @param x The x coordinate.
	 */
	void setEndX(double x);

	/**
	 * Sets the ending point of the vector in y coordinate space.
	 * @param y The y coordinate.
	 */
	void setEndY(double y);

	/**
	 * Calculates the width of the vector mapped to the x axis.
	 * @return The width of the vector mapped to the x axis.
	 */
	double diffX();

	/**
	 * Calculates the height of the vector mapped to the y axis.
	 * @return The height of the vector mapped to the y axis.
	 */
	double diffY();

	/**
	 * Calculates the length of the vector.
	 * @return The length.
	 */
	double straightLength();


}
