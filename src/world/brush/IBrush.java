package world.brush;

import world.IBaseTile;
import world.ITile;

import java.util.Collection;
import java.util.List;

/**
 * @author Josip
 * @version 21.05.2014
 */
public interface IBrush extends IRawBrush {

	/**
	 * Assigns data to given tiles.
	 * @param tileArray The tiles to be edited.
	 */
	void assign(List<IBaseTile> tileArray);

}
