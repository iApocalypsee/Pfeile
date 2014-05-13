package geom;

import java.awt.geom.Point2D;

/**
 * Represents a point implying the use of the internal {@link com.github.pfeile.geom.PointRef.DoubleRef} class.
 * @author Josip
 */
public class PointRef extends Point2D {

	/**
	 * A class that wraps a double value similarly to the {@link java.lang.Double} class,
	 * except that it allows for changing the value inside of the object itself,
	 * allowing for refence passing of this object. Hence the name <code>DoubleRef</code>.
	 * @author Josip
	 */
	public class DoubleRef extends Number implements Comparable<DoubleRef> {

		/**
		 * The double value.
		 */
		private double val;

		@Override
		public int compareTo(DoubleRef o) {
			if(val < o.val) return -1;
			else if(val == o.val) return 0;
			else return 1;
		}

		@Override
		public int intValue() {
			return (int) val;
		}

		@Override
		public long longValue() {
			return (long) val;
		}

		@Override
		public float floatValue() {
			return (float) val;
		}

		@Override
		public double doubleValue() {
			return val;
		}

		public void setValue(double val) {
			this.val = val;
		}
	}

	/**
	 * The reference to the double value. It <b>NEEDS</b> to be a reference.
	 */
	private DoubleRef x;

	/**
	 * The reference to the double value. It <b>NEEDS</b> to be a reference.
	 */
	private DoubleRef y;

	/**
	 * Constructs a point ref object.
	 * @param x The x position.
	 * @param y The y position.
	 */
	public PointRef(double x, double y) {
		this.x.setValue(x);
		this.y.setValue(y);
	}

	@Override
	public double getX() {
		return x.doubleValue();
	}

	@Override
	public double getY() {
		return y.doubleValue();
	}

	/**
	 * Returns the reference double.
	 * @return The reference double.
	 * @see #getX()
	 */
	public DoubleRef getRefX() {
		return x;
	}

	/**
	 * Returns the reference double.
	 * @return The reference double.
	 * @see #getY()
	 */
	public DoubleRef getRefY() {
		return y;
	}

	@Override
	public void setLocation(double x, double y) {
		this.x.setValue(x);
		this.y.setValue(y);
	}

	/**
	 * Adds the specified point to this point.
	 * @param p The point.
	 */
	public PointRef add(PointRef p) {
		x.setValue(getX() + p.getX());
		y.setValue(getY() + p.getY());
		return this;
	}

	/**
	 * Subtracts the specified point to this point.
	 * @param p The point.
	 */
	public PointRef subtract(PointRef p) {
		x.setValue(getX() - p.getX());
		y.setValue(getY() - p.getY());
		return this;
	}

	/**
	 * Creates a deep copy of this object.
	 * @return A deep copy of this object.
	 */
	public PointRef deepCopy() {
		return new PointRef(x.doubleValue(), y.doubleValue());
	}
}
