package world;

import entity.Entity;

import java.awt.*;
import java.util.Collection;

/**
 * @author Josip
 * @version 20.05.2014
 */
public class Field implements IField {
	@Override
	public Polygon getFieldBorders() {
		return null;
	}

	@Override
	public int getEntityCount() {
		return 0;
	}

	@Override
	public Collection<Entity> getEntities() {
		return null;
	}

	@Override
	public Collection<ITile> getTiles() {
		return null;
	}

	@Override
	public ITile getCenterTile() {
		return null;
	}
}
