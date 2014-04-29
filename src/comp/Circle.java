package comp;

import java.awt.*;

/**
 * @author Josip
 * @version 22.03.14
 */
public class Circle {

	private int x;
	private int y;
	private int radius;

	public Circle() {
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * Returns <code>true</code> if the point is inside the bounds of the circle.
	 * @param point The point.
	 * @return <code>true</code>, else <code>false</code>
	 */
	public boolean contains(Point point) {
		double dist = Math.abs(Math.sqrt(Math.pow(point.getX() - this.x + this.radius, 2) + Math.pow(point.getY() - this.y + this.radius, 2)));
		return dist <= radius && dist >= 0;
	}
}
