package geom.interfaces;

import geom.PointRef;

import java.util.List;

/**
 * @author Josip
 * @version 09.05.2014
 */
public interface VectorChain extends Vector {

	/**
	 * Returns the collection of the vectors.
	 * @return The vectors.
	 */
	List<Vector> getVectors();

	/**
	 * Gets the starting vector of the vector chain. The starting vector
	 * does not have to be saved in an instance variable.
	 * @return The starting vector.
	 */
	Vector getStartVector();

	/**
	 * Gets the ending vector of the vector chain. The starting vector
	 * does not have to be saved in an instance variable.
	 * @return The ending vector.
	 */
	Vector getEndVector();

	/**
	 * Returns the number of breaks in the vector chain.
	 * @return The number of breaks in the vector chain.
	 */
	int countBreaks();

	/**
	 * Appends the point to the vector chain and links a new vector from the ending
	 * vector to it.
	 * @param point The point to append to the vector chain.
	 */
	void append(PointRef point);

	/**
	 * Removes the vector at the specified index.
	 * @param index The index.
	 */
	void remove(int index);

	/**
	 * Calculates and returns the total length of the vector chain.
	 * @return The total length.
	 */
	double totalLength();

}
