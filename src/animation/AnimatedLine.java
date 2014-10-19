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

    /** The width of the line */
    private float width = 1.0f;

	/** The maximum offset. For fluent offsetting, set it to 20. */
	private double maximumOffset = 20.0;

	private volatile BasicStroke dashStroke = new BasicStroke(width,
			BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER,
			10.0f, new float[]{10f}, 0.0f);

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
		dashStroke = new BasicStroke(width,
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

    /** the end point where the line ends */
    public Point getEnd () {
        return end;
    }

    /** this where the line starts */
    public Point getStart () {
        return start;
    }

    /** set the Point where the line ends */
    public void setEnd (Point end) {
        this.end = end;
    }

    /** set the point where the points starts */
    public void setStart (Point start) {
        this.start = start;
    }

    /** the width of the animated line */
    public float getWidth () {
        return width;
    }

    /** sets the width of the line. The standard is 1.0f.*/
    public void setWidth (float width) {
        this.width = width;
        dashStroke = new BasicStroke(width,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f, new float[]{10f}, (float) offset);
    }

    @Override
	public void draw(Graphics2D g) {
		g.setStroke(dashStroke);
		g.setColor(color);
		g.drawLine(start.x, start.y, end.x, end.y);
	}
}
