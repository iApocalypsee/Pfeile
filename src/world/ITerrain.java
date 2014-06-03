package world;

import misc.metadata.IMetadatable;

/**
 * @author Josip
 * @version 20.05.2014
 */
public interface ITerrain extends IMetadatable {

	IBaseTile getTileAt(int x, int y);
	IField getFieldAt(int x, int y);
	IWorld getWorld();
	int getSizeX();
	int getSizeY();

}
