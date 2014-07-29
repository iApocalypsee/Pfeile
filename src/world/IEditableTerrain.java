package world;

import world.brush.IBrush;
import world.brush.IRawBrush;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author Josip
 * @version 21.05.2014
 */
public interface IEditableTerrain extends ITerrain {

	void edit(IRawBrush brush, List<Point> points);
	void set(int x, int y, IBaseTile tile);
	BufferedImage getHeightMap();
	BufferedImage getColorMap();

}
