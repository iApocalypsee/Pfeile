package world;

import entity.Entity;
import misc.metadata.IMetadatable;

import java.awt.*;
import java.util.List;

/**
 * @author Josip
 */
public interface ITile extends IMetadatable {

	int getGridX();
	int getGridY();
	int getHeight();
	IField getField();
	IWorld getWorld();
	List<? extends Entity> getEntities();
	Color getColor();

}
