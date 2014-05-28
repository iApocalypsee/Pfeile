package world;

import entity.Entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author Josip Palavra
 */
public interface IWorld extends Serializable {

	int getSizeX();
	int getSizeY();
	boolean isTileValid(int x, int y);
	ITile getTileAt(int x, int y);
	IField getFieldAt(int x, int y);
	ITerrain getTerrain();
	List<? extends IField> getFields();
	List<? extends IField> getNeighborFields();
	List<? extends Entity> collectEntities();
	List<? extends Entity> collectEntities(Class<? extends Entity> clazz);
	WorldViewport getViewport();

}
