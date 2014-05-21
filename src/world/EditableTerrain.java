package world;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import world.brush.ColorBrush;
import world.brush.HeightBrush;
import world.brush.IBrush;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Josip
 * @version 21.05.2014
 */
public class EditableTerrain extends Terrain implements IEditableTerrain {
	private static final long serialVersionUID = -2179754351564297519L;

	public BufferedImage heightMap,
		colorMap;

	/**
	 * Creates a new terrain with empty tiles.
	 *
	 * @param sizeX The width of the world in tiles.
	 * @param sizeY The height of the world in tiles.
	 */
	public EditableTerrain(int sizeX, int sizeY) {
		super(sizeX, sizeY);

		/*
		// draw the whole image maps black for now
		Graphics2D hmg = heightMap.createGraphics(), cmg = colorMap.createGraphics();
		hmg.setColor(Color.black);
		cmg.setColor(Color.black);
		hmg.fillRect(0, 0, heightMap.getWidth(), heightMap.getHeight());
		cmg.fillRect(0, 0, colorMap.getWidth(), colorMap.getHeight());
		*/

	}

	@Override
	public void edit(IBrush brush, Collection<Point> points) {

		// here: handling of special types of brushes,
		// which need specific handling
		// if the brush is an instance of color brush, I need
		// to draw to the color map aswell
		if(brush instanceof ColorBrush) {
			ColorBrush cBrush = (ColorBrush) brush;

			Graphics2D g = colorMap.createGraphics();

			for(Point p : points) {
				g.setColor(cBrush.getColor());
				g.fillOval(p.x - brush.getThickness(), p.y - brush.getThickness(), brush.getThickness() * 2, brush.getThickness() * 2);
			}

		} else if(brush instanceof HeightBrush) {
			//HeightBrush hBrush = (HeightBrush) brush;
			throw new NotImplementedException();
		}

		// assign to the tiles
		brush.assign(collectSelectedTiles(points));
	}

	private LinkedList<Tile> collectSelectedTiles(Collection<Point> points) {
		LinkedList<Tile> selections = new LinkedList<Tile>();
		for(Point point : points) {
			selections.add(getWorld().getTileAt(point.x, point.y));
		}
		return selections;
	}

	@Override
	public BufferedImage getHeightMap() {
		return heightMap;
	}

	@Override
	public BufferedImage getColorMap() {
		return colorMap;
	}
}
