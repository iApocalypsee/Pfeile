package geom;

import geom.interfaces.Triangle;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Josip
 * @version 13.05.2014
 */
public class VectorDef implements geom.interfaces.Vector, Comparable<VectorDef> {

	/**
	 * The starting point of the vector.
	 */
	private PointRef start;

	/**
	 * The ending point of the vector.
	 */
	private PointRef end;

	/**
	 * Creates a new default vector instance.
	 * @param start The starting point.
	 * @param end The ending point.
	 */
	public VectorDef(PointRef start, PointRef end) {
		this.start = start;
		this.end = end;
	}

	public PointRef getStart() {
		return start;
	}

	public PointRef getEnd() {
		return end;
	}

	/**
	 * Returns the starting point of the vector in x coordinate space.
	 *
	 * @return The x starting point.
	 */
	@Override
	public double getStartX() {
		return start.getX();
	}

	/**
	 * Returns the starting point of the vector in y coordinate space.
	 *
	 * @return The y starting point.
	 */
	@Override
	public double getStartY() {
		return start.getY();
	}

	/**
	 * Sets the starting point of the vector in x coordinate space.
	 *
	 * @param x The x coordinate.
	 */
	@Override
	public void setStartX(double x) {
		start.setLocation(x, start.getY());
	}

	/**
	 * Sets the starting point of the vector in y coordinate space.
	 *
	 * @param y
	 */
	@Override
	public void setStartY(double y) {
		start.setLocation(start.getX(), y);
	}

	/**
	 * Returns the ending point of the vector in x coordinate space.
	 *
	 * @return The x starting point.
	 */
	@Override
	public double getEndX() {
		return end.getX();
	}

	/**
	 * Returns the ending point of the vector in y coordinate space.
	 *
	 * @return The y starting point.
	 */
	@Override
	public double getEndY() {
		return end.getY();
	}

	/**
	 * Sets the ending point of the vector in x coordinate space.
	 *
	 * @param x The x coordinate.
	 */
	@Override
	public void setEndX(double x) {
		end.setLocation(x, end.getY());
	}

	/**
	 * Sets the ending point of the vector in y coordinate space.
	 *
	 * @param y The y coordinate.
	 */
	@Override
	public void setEndY(double y) {
		end.setLocation(end.getY(), y);
	}

	/**
	 * Calculates the width of the vector mapped to the x axis.
	 *
	 * @return The width of the vector mapped to the x axis.
	 */
	@Override
	public double diffX() {
		if(start.getX() > end.getX()) {
			return start.getX() - end.getX();
		} else {
			return end.getX() - start.getX();
		}
	}

	/**
	 * Calculates the height of the vector mapped to the y axis.
	 *
	 * @return The height of the vector mapped to the y axis.
	 */
	@Override
	public double diffY() {
		if(start.getY() > end.getY()) {
			return start.getY() - end.getY();
		} else {
			return end.getY() - start.getY();
		}
	}

	/**
	 * Calculates the length of the vector.
	 *
	 * @return The length.
	 */
	@Override
	public double straightLength() {
		return Math.sqrt(Math.pow(diffX(), 2) + Math.pow(diffY(), 2));
	}

	/**
	 * Triangulates the vector.
	 *
	 * @return The triangle representation of the vector.
	 */
	@Override
	public Triangle triangulate() {
		throw new NotImplementedException();
	}

	/**
	 * Compares this object with the specified object for order.  Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 * <p/>
	 * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
	 * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
	 * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
	 * <tt>y.compareTo(x)</tt> throws an exception.)
	 * <p/>
	 * <p>The implementor must also ensure that the relation is transitive:
	 * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
	 * <tt>x.compareTo(z)&gt;0</tt>.
	 * <p/>
	 * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
	 * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
	 * all <tt>z</tt>.
	 * <p/>
	 * <p>It is strongly recommended, but <i>not</i> strictly required that
	 * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
	 * class that implements the <tt>Comparable</tt> interface and violates
	 * this condition should clearly indicate this fact.  The recommended
	 * language is "Note: this class has a natural ordering that is
	 * inconsistent with equals."
	 * <p/>
	 * <p>In the foregoing description, the notation
	 * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
	 * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
	 * <tt>0</tt>, or <tt>1</tt> according to whether the value of
	 * <i>expression</i> is negative, zero or positive.
	 *
	 * @param o the object to be compared.
	 * @return a negative integer, zero, or a positive integer as this object
	 * is less than, equal to, or greater than the specified object.
	 * @throws NullPointerException if the specified object is null
	 * @throws ClassCastException   if the specified object's type prevents it
	 *                              from being compared to this object.
	 */
	@Override
	public int compareTo(VectorDef o) {
		double tsl = straightLength(), osl = o.straightLength();
		if(tsl > osl) {
			return 1;
		} else if(tsl == osl) {
			return 0;
		} else {
			return -1;
		}
	}
}
