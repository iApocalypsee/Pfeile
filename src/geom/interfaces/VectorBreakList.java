package geom.interfaces;

import java.util.List;

/**
 * @author Josip
 */
public interface VectorBreakList extends List<Vector> {

	/**
	 * Returns the vector chain to which this list is tied to.
	 * @return The vector chain.
	 */
	VectorChain getVectorChain();

	/**
	 * Removes the point from the break list, if it exists, and merges the two
	 * affected vectors.
	 * @param x The x coordinate.
	 * @param y The y coordinte.
	 * @return A boolean value indicating whether the point has been found and the point
	 * removed.
	 */
	boolean remove(double x, double y);

}
