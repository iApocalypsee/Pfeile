package world;

import misc.metadata.IMetadatable;
import player.BoardPositionable;
import world.tile.TileCage;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * @author Josip
 * @version 30.05.2014
 */
public interface IBaseTile extends IMetadatable, BoardPositionable {

	int getTileHeight();
	int getRequiredMovementPoints();

	IField getField();
	IWorld getWorld();
	Color getColor();

	IBaseTile north();
	IBaseTile northeast();
	IBaseTile east();
	IBaseTile southeast();
	IBaseTile south();
	IBaseTile southwest();
	IBaseTile west();
	IBaseTile northwest();

	Point2D gridCenter();

}
