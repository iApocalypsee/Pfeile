package animation;

import gui.Drawable;

import java.awt.*;

/**
 * @author Josip Palavra
 */
public class AnimatedLine implements Drawable {

	private Point start;
	private Point end;

	/** The color with which the line is drawn. */
	private Color color;

	/** The current offset of the animated (dashed) line, */
	private double offset = 0.0;

	/** The maximum offset. For fluent offsetting, set it to 20. */
	private double maximumOffset = 20.0;

	private volatile BasicStroke dashStroke = new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER,
			10.0f, new float[]{10f}, 0.0f);

	private int guiBoundsThickness = 30;

	private Polygon bounds;

	/**
	 * Creates an animated line
	 *
	 * @param sx The start x position.
	 * @param sy The start y position.
	 * @param ex The end x position.
	 * @param ey The end y position.
	 */
	public AnimatedLine(int sx, int sy, int ex, int ey, Color color) {
		start = new Point(sx, sy);
		end = new Point(ex, ey);
		this.color = color;
	}

	public void updateOffset(double delta) {
		offset += delta;
		if (offset > maximumOffset) offset = 0.0;
		dashStroke = new BasicStroke(1.0f,
				BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER,
				10.0f, new float[]{10f}, (float) offset);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double getMaximumOffset() {
		return maximumOffset;
	}

	public void setMaximumOffset(double maximumOffset) {
		this.maximumOffset = maximumOffset;
	}

	public void setStartX(int x) {
		start.x = x;
		recalculateBounds();
	}

	public void setStartY(int y) {
		start.y = y;
		recalculateBounds();
	}

	public void setEndX(int x) {
		end.x = x;
		recalculateBounds();
	}

	public void setEndY(int y) {
		end.y = y;
		recalculateBounds();
	}

	public int getStartX() {
		return start.x;
	}

	public int getStartY() {
		return start.y;
	}

	public int getEndX() {
		return end.x;
	}

	public int getEndY() {
		return end.y;
	}

	private void recalculateBounds() {
		// Preparation for bounds calculation

		double halfThickness = guiBoundsThickness / 2.0;
		double verticalCathete = end.y - start.y;
		double horizontalCathete = end.x - start.x;
		double hypot = Math.sqrt(Math.pow(horizontalCathete, 2) + Math.pow(verticalCathete, 2));

		double alpha = horizontalCathete / hypot;
		double beta = verticalCathete / hypot;

		// Bounds calculation is here.
		double x_off = halfThickness * Math.cos(Math.toRadians(180 - 90 - alpha));
		double y_off = halfThickness * Math.sin(Math.toRadians(180 - 90 - alpha));

		final Point p1 = new Point(end.x + (int) x_off, end.y + (int) -y_off);
		final Point p2 = new Point(end.x + (int) -x_off, end.y + (int) y_off);

		x_off = halfThickness * Math.sin(Math.toRadians(180 - 90 - beta));
		y_off = halfThickness * Math.cos(Math.toRadians(180 - 90 - beta));

		final Point p3 = new Point(start.x + (int) -x_off, start.y + (int) y_off);
		final Point p4 = new Point(start.x + (int) x_off, start.y + (int) -y_off);

		bounds = comp.Component.createRectPolygon(p1, p2, p3, p4);
	}

	@Override
	public void draw(Graphics2D g) {
		g.setStroke(dashStroke);
		g.setColor(color);
		g.drawLine(start.x, start.y, end.x, end.y);
	}
}
