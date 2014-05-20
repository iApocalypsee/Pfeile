package world;

import entity.IEntity;

import java.util.Collection;

/**
 * @author Josip Palavra
 */
public interface IWorld {

	int getSizeX();
	int getSizeY();
	ITile getTileAt(int x, int y);
	IField getFieldAt(int x, int y);
	ITerrain getTerrain();
	Collection<IField> getFields();
	Collection<IField> getNeighborFields();
	Collection<IEntity> collectEntities();
	Collection<? extends IEntity> collectEntities(Class<? extends IEntity> clazz);

}
