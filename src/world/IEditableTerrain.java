package world;

import world.brush.IBrush;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

/**
 * @author Josip
 * @version 21.05.2014
 */
public interface IEditableTerrain extends ITerrain {

	void edit(IBrush brush, Collection<Point> points);
	BufferedImage getHeightMap();
	BufferedImage getColorMap();

}
