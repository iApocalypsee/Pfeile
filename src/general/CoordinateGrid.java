package general;

import gui.Drawable;

import java.awt.*;

public class CoordinateGrid implements Drawable {

	/**
	 * If active, draws a grid which displays where what coordinate is.
	 * Should only apply if Main.isDebug() is true.
	 */
	private boolean activated = false;

	/**
	 * How dense the coordinate grid is going to be drawn.
	 * Applies only when activated is true.
	 */
	private int density = 100;
	private int offsetX = 0;
	private int offsetY = 0;
	private Font gridFont = new Font(Font.MONOSPACED, Font.PLAIN, 11);
	private Stroke gridStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 4, new float[] {4}, 0);
	private Color gridColor = new Color(255, 255, 255, 70);

	private int canvasWidth, canvasHeight;

	public CoordinateGrid(int canvasHeight, int canvasWidth) {
		this.canvasHeight = canvasHeight;
		this.canvasWidth = canvasWidth;
	}

	@Override
	public void draw(Graphics2D g) {
		g.setStroke(gridStroke);
		g.setFont(gridFont);
		g.setColor(gridColor);

		int offsetXMod = offsetX % density;
		int offsetYMod = offsetY % density;

		for(int y = offsetYMod; y < canvasHeight; y += density) {
			for(int x = offsetXMod; x < canvasWidth; x += density) {
				int normalX = x - offsetX;
				int normalY = y - offsetY;
				if(normalX == 0 || normalY == 0) g.setColor(Color.white);
				g.drawString(normalX + "|" + normalY, x, y);
				if(normalX == 0 || normalY == 0) g.setColor(gridColor);
			}
		}

		for(int x = offsetXMod; x < canvasWidth + offsetXMod; x += density) {
			int normalX = x - offsetX;
			if(normalX == 0) g.setColor(Color.white);
			g.drawLine(x, 0, x, canvasHeight);
			if(normalX == 0) g.setColor(gridColor);
		}

		for(int y = offsetYMod; y < canvasHeight + offsetYMod; y += density) {
			int normalY = y - offsetY;
			if(normalY == 0) g.setColor(Color.white);
			g.drawLine(0, y, canvasWidth, y);
			if(normalY == 0) g.setColor(gridColor);
		}
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public int getDensity() {
		return density;
	}

	public void setDensity(int density) {
		if(density < 0) throw new IllegalArgumentException("Coordinate grid density must not be below 0");
		this.density = density;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public int getCanvasWidth() {
		return canvasWidth;
	}

	public int getCanvasHeight() {
		return canvasHeight;
	}

	public void setCanvasWidth(int canvasWidth) {
		this.canvasWidth = canvasWidth;
	}

	public void setCanvasHeight(int canvasHeight) {
		this.canvasHeight = canvasHeight;
	}
}
