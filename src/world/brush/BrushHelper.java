package world.brush;

import comp.Circle;
import world.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Josip
 */
public final class BrushHelper {

	/**
	 * Determines all tiles from the middle point 'tile' in radius 'radius'
	 * @param tile The tile which represents the "middle" point of the circle.
	 * @param radius The radius to look for tiles.
	 * @return The tiles determined.
	 */
	public static LinkedList<IBaseTile> determineTiles(IBaseTile tile, int radius) {
		Circle rad = new Circle();
		LinkedList<IBaseTile> edits = new LinkedList<IBaseTile>();
		IWorld w = tile.getWorld();
		rad.setX(tile.getGridX() + 0.5);
		rad.setY(tile.getGridY() + 0.5);
		rad.setRadius(radius);

		// define a rectangular array of tiles
		for(int x = tile.getGridX() - radius; x < tile.getGridX() + radius; x++) {
			if(!w.isTileValid(x, 0)) continue;
			for(int y = tile.getGridY() - radius; y < tile.getGridY() + radius; y++) {
				if(!w.isTileValid(x, y)) continue;
				IBaseTile checkTile = w.getTileAt(x, y);
				if(rad.contains(checkTile.gridCenter())) {
					edits.add(checkTile);
				}
			}
		}
		return edits;
	}

	public static LinkedList<GridElement> determineGridElements(GridElement elem, int radius) {
		ScaleWorld w = elem.world();
		Circle rad = new Circle();
		LinkedList<GridElement> matches = new LinkedList<GridElement>();
		rad.setX(elem.gridX() + 0.5);
		rad.setY(elem.gridY() + 0.5);
		rad.setRadius(radius);

		// define a rectangular array of tiles
		for(int x = elem.gridX() - radius, xit = 0; x < elem.gridX() + radius; x++, xit++) {
			if(x < 0) continue;
			if(x >= w.getSizeX()) break;
			for(int y = elem.gridY() - radius, yit = 0; y < elem.gridY() + radius; y++, yit++) {
				if(y < 0) continue;
				if(y >= w.getSizeY()) break;
				GridElement checkTile = w.getGridElementAt(x, y);
				if(rad.contains(checkTile.gridCenter())) {
					matches.add(checkTile);
				}
			}
		}
		return matches;
	}

	public static LinkedList<Point> determinePoints(Point middle, int radius) {
		LinkedList<LinkedList<BrushHelper$Point2D>> points = new LinkedList<LinkedList<BrushHelper$Point2D>>();
		LinkedList<Point> matches = new LinkedList<Point>();
		// fill the double filled linked list
		for(int x = middle.x - radius; x < middle.x + radius; x++) {
			LinkedList<BrushHelper$Point2D> build = new LinkedList<BrushHelper$Point2D>();
			for(int y = middle.y - radius; y < middle.y + radius; y++) {
				build.add(new BrushHelper$Point2D(x/* + 0.5*/, y/* + 0.5*/));
			}
			points.add(build);
		}

		Circle rad = new Circle();
		rad.setX(middle.x/* + 0.5*/);
		rad.setY(middle.y/* + 0.5*/);
		rad.setRadius(radius);

		for(int x = middle.x - radius, xit = 0; x < middle.x + radius - 1; x++, xit++) {
			if(x < 0) continue;
			for(int y = middle.y - radius, yit = 0; y < middle.y + radius; y++, yit++) {
				if(y < 0) continue;
				if(rad.contains(points.get(xit).get(yit))) {
					matches.add(points.get(xit).get(yit).toPoint());
				}
			}
		}
		return matches;
	}

	static class BrushHelper$Point2D extends Point2D {

		double x;
		double y;

		public BrushHelper$Point2D(double x, double y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * Returns the X coordinate of this <code>Point2D</code> in
		 * <code>double</code> precision.
		 *
		 * @return the X coordinate of this <code>Point2D</code>.
		 * @since 1.2
		 */
		@Override
		public double getX() {
			return x;
		}

		/**
		 * Returns the Y coordinate of this <code>Point2D</code> in
		 * <code>double</code> precision.
		 *
		 * @return the Y coordinate of this <code>Point2D</code>.
		 * @since 1.2
		 */
		@Override
		public double getY() {
			return y;
		}

		/**
		 * Sets the location of this <code>Point2D</code> to the
		 * specified <code>double</code> coordinates.
		 *
		 * @param x the new X coordinate of this {@code Point2D}
		 * @param y the new Y coordinate of this {@code Point2D}
		 * @since 1.2
		 */
		@Override
		public void setLocation(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public void move(double x, double y) {
			this.x += x;
			this.y += y;
		}

		public Point toPoint() {
			return new Point((int) x, (int) y);
		}
	}

}
