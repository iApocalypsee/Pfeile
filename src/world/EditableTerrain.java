package world;

import world.brush.ColorBrush;
import world.brush.HeightBrush;
import world.brush.IBrush;
import world.brush.IRawBrush;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

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

		heightMap = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_ARGB);
		colorMap = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_RGB);

		/*
		// draw the whole image maps black for now
		Graphics2D hmg = heightMap.createGraphics(), cmg = colorMap.createGraphics();
		hmg.setColor(Color.black);
		cmg.setColor(Color.black);
		hmg.fillRect(0, 0, heightMap.getWidth(), heightMap.getTileHeight());
		cmg.fillRect(0, 0, colorMap.getWidth(), colorMap.getTileHeight());
		*/

	}

	@Override
	public void edit(IRawBrush brush, List<Point> points) {

		IBrush ibrush = (IBrush) brush;

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
			//throw new NotImplementedException();
			System.err.println("Height brush not implemented yet!");
		}

		List<IBaseTile> t = new LinkedList<IBaseTile>();
		LinkedList<Tile> oldt = collectSelectedTiles(points);

		for(Tile tile : oldt) {
			t.add((IBaseTile) tile);
		}

		// assign to the tiles
		ibrush.assign(t);
	}

	@Override
	public void set(int x, int y, IBaseTile tile) {
		if(!getWorld().isTileValid(x, y)) throw new NoSuchElementException();
		LinkedList<LinkedList<Tile>> t = getTiles();
		t.get(x).set(y, (Tile) tile);
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
