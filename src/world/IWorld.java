package world;

import entity.IEntity;

import java.io.Serializable;
import java.util.List;

/**
 * @author Josip Palavra
 */
public interface IWorld extends Serializable {

	int getSizeX();
	int getSizeY();
	ITile getTileAt(int x, int y);
	IField getFieldAt(int x, int y);
	ITerrain getTerrain();
	List<? extends IField> getFields();
	List<? extends IField> getNeighborFields();
	List<? extends IEntity> collectEntities();
	List<? extends IEntity> collectEntities(Class<? extends IEntity> clazz);

}
