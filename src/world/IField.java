package world;

import entity.Entity;

import java.awt.*;
import java.util.Collection;

/**
 * @author Josip
 * @version 15.05.2014
 */
public interface IField {

	Polygon getFieldBorders();
	int getEntityCount();
	Collection<Entity> getEntities();
	Collection<ITile> getTiles();
	ITile getCenterTile();

}
