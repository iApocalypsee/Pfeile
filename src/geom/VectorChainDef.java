package geom;

import geom.interfaces.VectorChain;

import java.util.LinkedList;

/**
 * @author Josip Palavra
 */
@Deprecated
public class VectorChainDef implements VectorChain {

	private LinkedList<Vector> vectors = new LinkedList<Vector>();
	private Point start;

	/**
	 * Creates a new default vector instance.
	 *
	 * @param start The starting point.
	 * @param end   The ending point.
	 */
	public VectorChainDef(Point start, Point end) {
		this.start = start;
		vectors.add(end.difference(start));
	}

	/**
	 * Returns the collection of the vectors.
	 *
	 * @return The vectors.
	 */
	@Override
	public LinkedList<Vector> getVectors() {
		return vectors;
	}

	/**
	 * Gets the starting vector of the vector chain. The starting vector
	 * does not have to be saved in an instance variable.
	 *
	 * @return The starting vector.
	 */
	@Override
	public Point getStartPoint() {
		return start;
	}

	/**
	 * Gets the ending vector of the vector chain. The starting vector
	 * does not have to be saved in an instance variable.
	 *
	 * @return The ending vector.
	 */
	@Override
	public Point getEndPoint() {
		return vectors.stream().reduce(start, (Point x, Vector y) -> x.add(y), null);
	}

	/**
	 * Returns the number of breaks in the vector chain.
	 *
	 * @return The number of breaks in the vector chain.
	 */
	@Override
	public int countBreaks() {
		return vectors.size();
	}

	@Override
	public void append(Point point) {
		vectors.add(point.difference(getEndPoint()));
	}

	@Override
	public double getStartX() {
		return start.getX();
	}

	@Override
	public double getStartY() {
		return start.getY();
	}

	@Override
	public void setStartX(double x) {
		start.setCoordinate(x, start.getY());
	}

	@Override
	public void setStartY(double y) {
		start.setCoordinate(start.getX(), y);
	}

	@Override
	public double getEndX() {
		return getEndPoint().getX();
	}

	@Override
	public double getEndY() {
		return getEndPoint().getY();
	}

	@Override
	public void setEndX(double x) {
		Point beforeLast = start;
		double y = vectors.getLast().getY();
		vectors.removeLast();
		vectors.forEach(beforeLast::add);
		vectors.addLast(new Vector(x, y));
	}

	@Override
	public void setEndY(double y) {
		Point beforeLast = start;
		double x = vectors.getLast().getX();
		vectors.removeLast();
		vectors.forEach(beforeLast::add);
		vectors.addLast(new Vector(x, y));
	}

	@Override
	public double totalLength() {
		return vectors.stream().map(Vector::length).reduce(0.0, Double::sum);
	}

	@Override
	public double straightLength() {
		return totalLength();
	}

	/**
	 * Removes the vector at the specified index.
	 *
	 * @param index The index.
	 */
	@Override
	public void remove(int index) {
		vectors.remove(index);
	}

	@Override
	public double diffX() {
		return vectors.stream().map(Vector::getX).reduce(0.0, Double::sum);
	}

	@Override
	public double diffY() {
		return vectors.stream().map(Vector::getY).reduce(0.0, Double::sum);
	}
}
