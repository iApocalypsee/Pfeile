package world;

import entity.IEntity;
import misc.metadata.IMetadatable;

import java.awt.*;
import java.util.List;

/**
 * @author Josip
 */
public interface ITile extends IMetadatable {

	int getGridX();
	int getGridY();
	IField getField();
	List<entity.Entity> getEntities();
	Color getColor();

}
