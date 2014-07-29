package geom.interfaces;

import geom.PointRef;

import java.awt.*;

/**
 * Represents a triangle.
 * TODO Implementations of this interface to be written.
 * @author Josip
 */
public interface Triangle extends Shape {

	/**
	 * Returns the first point of the triangle.
	 * @return The first point.
	 */
	PointRef getPoint1();

	/**
	 * Returns the second point of the triangle.
	 * @return The second point.
	 */
	PointRef getPoint2();

	/**
	 * Returns the third point of the triangle
	 * @return The third point of the triangle.
	 */
	PointRef getPoint3();

	/**
	 * Calculates the area value out of the three given points.
	 * @return The area value.
	 */
	double area();

}
