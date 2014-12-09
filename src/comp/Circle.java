package comp;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Josip
 * @version 05.05.2014
 */
public class Circle implements Shape {

	/**
	 * The middle point of the circle in the x coordinate space.
	 */
	private double x;

	/**
	 * The middle point of the circle in the y coordinate space.
	 */
	private double y;

	/**
	 * The radius of the circle.
	 */
	private double radius;

	private double radiusSq;

	/**
	 * Constructs a new circle object.
	 */
	public Circle() {
		x = 0.0;
		y = 0.0;
		radius = 0.0;
	}

    /**
     * This constructs a new circle
     * @param x      the X coordinate of the middle of the circle
     * @param y      the y coordinate of the middle of the circle
     * @param radius the radius of the circle
     */
    public Circle (double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getRadius() {
		return radius;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setRadius(double radius) {
		this.radius = radius;
		radiusSq = radius * radius;
	}

	/**
	 * Returns an integer {@link java.awt.Rectangle} that completely encloses the
	 * <code>Shape</code>.  Note that there is no guarantee that the
	 * returned <code>Rectangle</code> is the smallest bounding box that
	 * encloses the <code>Shape</code>, only that the <code>Shape</code>
	 * lies entirely within the indicated  <code>Rectangle</code>.  The
	 * returned <code>Rectangle</code> might also fail to completely
	 * enclose the <code>Shape</code> if the <code>Shape</code> overflows
	 * the limited range of the integer data type.  The
	 * <code>getBounds2D</code> method generally returns a
	 * tighter bounding box due to its greater flexibility in
	 * representation.
	 * <p/>
	 * <p>
	 * Note that the <a href="{@docRoot}/java/awt/Shape.html#def_insideness">
	 * definition of insideness</a> can lead to situations where points
	 * on the defining outline of the {@code shape} may not be considered
	 * contained in the returned {@code bounds} object, but only in cases
	 * where those points are also not considered contained in the original
	 * {@code shape}.
	 * </p>
	 * <p>
	 * If a {@code point} is inside the {@code shape} according to the
	 * {@link #contains(double x, double y) contains(point)} method, then
	 * it must be inside the returned {@code Rectangle} bounds object
	 * according to the {@link #contains(double x, double y) contains(point)}
	 * method of the {@code bounds}. Specifically:
	 * </p>
	 * <p>
	 * {@code shape.contains(x, y)} requires {@code bounds.contains(x, y)}
	 * </p>
	 * <p>
	 * If a {@code point} is not inside the {@code shape}, then it might
	 * still be contained in the {@code bounds} object:
	 * </p>
	 * <p>
	 * {@code bounds.contains(x, y)} does not imply {@code shape.contains(x, y)}
	 * </p>
	 *
	 * @return an integer <code>Rectangle</code> that completely encloses
	 * the <code>Shape</code>.
	 * @see #getBounds2D
	 * @since 1.2
	 */
	@Override
	public Rectangle getBounds() {
        return new Rectangle((int) (x  - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2));
	}

	/**
	 * Returns a high precision and more accurate bounding box of
	 * the <code>Shape</code> than the <code>getBounds</code> method.
	 * Note that there is no guarantee that the returned
	 * {@link java.awt.geom.Rectangle2D} is the smallest bounding box that encloses
	 * the <code>Shape</code>, only that the <code>Shape</code> lies
	 * entirely within the indicated <code>Rectangle2D</code>.  The
	 * bounding box returned by this method is usually tighter than that
	 * returned by the <code>getBounds</code> method and never fails due
	 * to overflow problems since the return value can be an instance of
	 * the <code>Rectangle2D</code> that uses double precision values to
	 * store the dimensions.
	 * <p/>
	 * <p>
	 * Note that the <a href="{@docRoot}/java/awt/Shape.html#def_insideness">
	 * definition of insideness</a> can lead to situations where points
	 * on the defining outline of the {@code shape} may not be considered
	 * contained in the returned {@code bounds} object, but only in cases
	 * where those points are also not considered contained in the original
	 * {@code shape}.
	 * </p>
	 * <p>
	 * If a {@code point} is inside the {@code shape} according to the
	 * {@link #contains(Point2D p) contains(point)} method, then it must
	 * be inside the returned {@code Rectangle2D} bounds object according
	 * to the {@link #contains(Point2D p) contains(point)} method of the
	 * {@code bounds}. Specifically:
	 * </p>
	 * <p>
	 * {@code shape.contains(p)} requires {@code bounds.contains(p)}
	 * </p>
	 * <p>
	 * If a {@code point} is not inside the {@code shape}, then it might
	 * still be contained in the {@code bounds} object:
	 * </p>
	 * <p>
	 * {@code bounds.contains(p)} does not imply {@code shape.contains(p)}
	 * </p>
	 *
	 * @return an instance of <code>Rectangle2D</code> that is a
	 * high-precision bounding box of the <code>Shape</code>.
	 * @see #getBounds
	 * @since 1.2
	 */
	@Override
	public Rectangle2D getBounds2D() {
		Rectangle rect = new Rectangle((int) (x  - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2));
		return rect.getBounds2D();
	}

	/**
	 * Tests if the specified coordinates are inside the boundary of the
	 * <code>Shape</code>, as described by the
	 * <a href="{@docRoot}/java/awt/Shape.html#def_insideness">
	 * definition of insideness</a>.
	 *
	 * @param x the specified X coordinate to be tested
	 * @param y the specified Y coordinate to be tested
	 * @return <code>true</code> if the specified coordinates are inside
	 * the <code>Shape</code> boundary; <code>false</code>
	 * otherwise.
	 * @since 1.2
	 */
	@Override
	public boolean contains(double x, double y) {
		// get the relative length to the middle point of the circle
		double xlen = x - this.x;
        double ylen = y - this.y;
		// use the pythagorean theorem to calculate the triangle between xlen, ylen and the middle point
		double res = Math.pow(xlen, 2) + Math.pow(ylen, 2);
		// and return the check
		return res < radiusSq;
	}

	/**
	 * Tests if a specified {@link java.awt.geom.Point2D} is inside the boundary
	 * of the <code>Shape</code>, as described by the
	 * <a href="{@docRoot}/java/awt/Shape.html#def_insideness">
	 * definition of insideness</a>.
	 *
	 * @param p the specified <code>Point2D</code> to be tested
	 * @return <code>true</code> if the specified <code>Point2D</code> is
	 * inside the boundary of the <code>Shape</code>;
	 * <code>false</code> otherwise.
	 * @since 1.2
	 */
	@Override
	public boolean contains(Point2D p) {
		/*
		// get the relative length to the middle point of the circle
		//double xlen = p.getX() - this.x, ylen = p.getY() - this.y;
		double xlen = this.x - p.getX(), ylen = this.y - p.getY();
		// use the pythagorean theorem to calculate the triangle between xlen, ylen and the middle point
		double res = Math.sqrt(Math.pow(xlen, 2) + Math.pow(ylen, 2));
		// and return the check
		return res < radius;
		*/
		return Point2D.distanceSq(p.getX(), p.getY(), x, y) < radius * radius;
	}

	/**
	 * Tests if the interior of the <code>Shape</code> intersects the
	 * interior of a specified rectangular area.
	 * The rectangular area is considered to intersect the <code>Shape</code>
	 * if any point is contained in both the interior of the
	 * <code>Shape</code> and the specified rectangular area.
	 * <p/>
	 * The {@code Shape.intersects()} method allows a {@code Shape}
	 * implementation to conservatively return {@code true} when:
	 * <ul>
	 * <li>
	 * there is a high probability that the rectangular area and the
	 * <code>Shape</code> intersect, but
	 * <li>
	 * the calculations to accurately determine this intersection
	 * are prohibitively expensive.
	 * </ul>
	 * This means that for some {@code Shapes} this method might
	 * return {@code true} even though the rectangular area does not
	 * intersect the {@code Shape}.
	 * The {@link java.awt.geom.Area Area} class performs
	 * more accurate computations of geometric intersection than most
	 * {@code Shape} objects and therefore can be used if a more precise
	 * answer is required.
	 *
	 * @param x the X coordinate of the upper-left corner
	 *          of the specified rectangular area
	 * @param y the Y coordinate of the upper-left corner
	 *          of the specified rectangular area
	 * @param w the width of the specified rectangular area
	 * @param h the height of the specified rectangular area
	 * @return <code>true</code> if the interior of the <code>Shape</code> and
	 * the interior of the rectangular area intersect, or are
	 * both highly likely to intersect and intersection calculations
	 * would be too expensive to perform; <code>false</code> otherwise.
	 * @see java.awt.geom.Area
	 * @since 1.2
	 */
	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return contains(x, y) || contains(x + w, y) || contains(x + w, y + h) || contains(x, y + h);
	}

	/**
	 * Tests if the interior of the <code>Shape</code> intersects the
	 * interior of a specified <code>Rectangle2D</code>.
	 * The {@code Shape.intersects()} method allows a {@code Shape}
	 * implementation to conservatively return {@code true} when:
	 * <ul>
	 * <li>
	 * there is a high probability that the <code>Rectangle2D</code> and the
	 * <code>Shape</code> intersect, but
	 * <li>
	 * the calculations to accurately determine this intersection
	 * are prohibitively expensive.
	 * </ul>
	 * This means that for some {@code Shapes} this method might
	 * return {@code true} even though the {@code Rectangle2D} does not
	 * intersect the {@code Shape}.
	 * The {@link java.awt.geom.Area Area} class performs
	 * more accurate computations of geometric intersection than most
	 * {@code Shape} objects and therefore can be used if a more precise
	 * answer is required.
	 *
	 * @param r the specified <code>Rectangle2D</code>
	 * @return <code>true</code> if the interior of the <code>Shape</code> and
	 * the interior of the specified <code>Rectangle2D</code>
	 * intersect, or are both highly likely to intersect and intersection
	 * calculations would be too expensive to perform; <code>false</code>
	 * otherwise.
	 * @see #intersects(double, double, double, double)
	 * @since 1.2
	 */
	@Override
	public boolean intersects(Rectangle2D r) {
		return contains(r.getX(), r.getY()) || contains(r.getX() + r.getWidth(), r.getY()) ||
				contains(r.getX() + r.getWidth(), r.getY() + r.getHeight()) ||
				contains(r.getX(), r.getY() + r.getHeight());
	}

	/**
	 * Tests if the interior of the <code>Shape</code> entirely contains
	 * the specified rectangular area.  All coordinates that lie inside
	 * the rectangular area must lie within the <code>Shape</code> for the
	 * entire rectanglar area to be considered contained within the
	 * <code>Shape</code>.
	 * <p/>
	 * The {@code Shape.contains()} method allows a {@code Shape}
	 * implementation to conservatively return {@code false} when:
	 * <ul>
	 * <li>
	 * the <code>intersect</code> method returns <code>true</code> and
	 * <li>
	 * the calculations to determine whether or not the
	 * <code>Shape</code> entirely contains the rectangular area are
	 * prohibitively expensive.
	 * </ul>
	 * This means that for some {@code Shapes} this method might
	 * return {@code false} even though the {@code Shape} contains
	 * the rectangular area.
	 * The {@link java.awt.geom.Area Area} class performs
	 * more accurate geometric computations than most
	 * {@code Shape} objects and therefore can be used if a more precise
	 * answer is required.
	 *
	 * @param x the X coordinate of the upper-left corner
	 *          of the specified rectangular area
	 * @param y the Y coordinate of the upper-left corner
	 *          of the specified rectangular area
	 * @param w the width of the specified rectangular area
	 * @param h the height of the specified rectangular area
	 * @return <code>true</code> if the interior of the <code>Shape</code>
	 * entirely contains the specified rectangular area;
	 * <code>false</code> otherwise or, if the <code>Shape</code>
	 * contains the rectangular area and the
	 * <code>intersects</code> method returns <code>true</code>
	 * and the containment calculations would be too expensive to
	 * perform.
	 * @see java.awt.geom.Area
	 * @see #intersects
	 * @since 1.2
	 */
	@Override
	public boolean contains(double x, double y, double w, double h) {
		return contains(x, y) && contains(x + w, y) && contains(x + w, y + h) && contains(x, y + h);
	}

	/**
	 * Tests if the interior of the <code>Shape</code> entirely contains the
	 * specified <code>Rectangle2D</code>.
	 * The {@code Shape.contains()} method allows a {@code Shape}
	 * implementation to conservatively return {@code false} when:
	 * <ul>
	 * <li>
	 * the <code>intersect</code> method returns <code>true</code> and
	 * <li>
	 * the calculations to determine whether or not the
	 * <code>Shape</code> entirely contains the <code>Rectangle2D</code>
	 * are prohibitively expensive.
	 * </ul>
	 * This means that for some {@code Shapes} this method might
	 * return {@code false} even though the {@code Shape} contains
	 * the {@code Rectangle2D}.
	 * The {@link java.awt.geom.Area Area} class performs
	 * more accurate geometric computations than most
	 * {@code Shape} objects and therefore can be used if a more precise
	 * answer is required.
	 *
	 * @param r The specified <code>Rectangle2D</code>
	 * @return <code>true</code> if the interior of the <code>Shape</code>
	 * entirely contains the <code>Rectangle2D</code>;
	 * <code>false</code> otherwise or, if the <code>Shape</code>
	 * contains the <code>Rectangle2D</code> and the
	 * <code>intersects</code> method returns <code>true</code>
	 * and the containment calculations would be too expensive to
	 * perform.
	 * @see #contains(double, double, double, double)
	 * @since 1.2
	 */
	@Override
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY()) && contains(r.getX() + r.getWidth(), r.getY()) &&
				contains(r.getX() + r.getWidth(), r.getY() + r.getHeight()) &&
				contains(r.getX(), r.getY() + r.getHeight());
	}

	/**
	 * Always returns <code>null</code> in the case of a circle, since it has endless vertices.
	 * No geometry can and should be provided with the circle class.
	 */
	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return null;
	}

	/**
	 * Always returns <code>null</code> in the case of a circle, since it has endless vertices.
	 * No geometry can and should be provided with the circle class.
	 */
	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return null;
	}
}
