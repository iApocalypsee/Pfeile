package world;

import entity.IEntity;

import java.awt.*;
import java.util.Collection;

/**
 * @author Josip
 * @version 15.05.2014
 */
public interface IField {

	Polygon getFieldBorders();
	int getEntityCount();
	Collection<IEntity> getEntities();
	Collection<ITile> getTiles();
	ITile getCenterTile();

}
