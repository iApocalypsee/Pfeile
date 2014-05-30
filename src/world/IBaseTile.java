package world;

import misc.metadata.IMetadatable;
import world.tile.TileCage;

import java.awt.*;

/**
 * @author Josip
 * @version 30.05.2014
 */
public interface IBaseTile extends IMetadatable {

	int getGridX();
	int getGridY();
	int getHeight();
	IField getField();
	IWorld getWorld();
	Color getColor();

}
