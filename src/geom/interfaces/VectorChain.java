package geom.interfaces;

import java.util.Collection;

/**
 * @author Josip
 * @version 09.05.2014
 */
public interface VectorChain extends Vector {

	/**
	 * Returns the collection of the vectors.
	 * @return The vectors.
	 */
	Collection<Vector> getVectors();

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



}
