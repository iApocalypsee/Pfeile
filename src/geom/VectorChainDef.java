package geom;

import geom.interfaces.Vector;
import geom.interfaces.VectorChain;

import java.util.LinkedList;

/**
 * @author Josip Palavra
 */
public class VectorChainDef extends VectorDef implements VectorChain {

	private LinkedList<Vector> vectors = new LinkedList<Vector>();

	/**
	 * Creates a new default vector instance.
	 *
	 * @param start The starting point.
	 * @param end   The ending point.
	 */
	public VectorChainDef(PointRef start, PointRef end) {
		super(start, end);
		vectors.add(new VectorDef(start, end));
	}

	/**
	 * Returns the collection of the vectors.
	 *
	 * @return The vectors.
	 */
	@Override
	public LinkedList<Vector> getVectors() {
		return (LinkedList<Vector>) vectors.clone();
	}

	/**
	 * Gets the starting vector of the vector chain. The starting vector
	 * does not have to be saved in an instance variable.
	 *
	 * @return The starting vector.
	 */
	@Override
	public Vector getStartVector() {
		return vectors.getFirst();
	}

	/**
	 * Gets the ending vector of the vector chain. The starting vector
	 * does not have to be saved in an instance variable.
	 *
	 * @return The ending vector.
	 */
	@Override
	public Vector getEndVector() {
		return vectors.getLast();
	}

	/**
	 * Returns the number of breaks in the vector chain.
	 *
	 * @return The number of breaks in the vector chain.
	 */
	@Override
	public int countBreaks() {
		return vectors.size() - 1;
	}

	@Override
	public void append(PointRef point) {
		vectors.add(new VectorDef(new PointRef(vectors.getLast().getEndX(), vectors.getLast().getEndY()), point));
	}

	@Override
	public double getStartX() {
		return vectors.getFirst().getStartX();
	}

	@Override
	public double getStartY() {
		return vectors.getFirst().getStartY();
	}

	@Override
	public void setStartX(double x) {
		vectors.getFirst().setStartX(x);
	}

	@Override
	public void setStartY(double y) {
		vectors.getFirst().setStartY(y);
	}

	@Override
	public double getEndX() {
		return vectors.getLast().getEndX();
	}

	@Override
	public double getEndY() {
		return vectors.getLast().getEndY();
	}

	@Override
	public void setEndX(double x) {
		vectors.getLast().setEndX(x);
	}

	@Override
	public void setEndY(double y) {
		vectors.getLast().setEndY(y);
	}

	@Override
	public double totalLength() {
		double res = 0.0;
		for(Vector v : vectors) res += v.straightLength();
		return res;
	}
}
