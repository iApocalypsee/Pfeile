package world;

import comp.GUIUpdater;
import entity.Entity;
import entity.Player;
import gui.Drawable;

import java.io.Serializable;
import java.util.List;

/**
 * @author Josip Palavra
 */
public interface IWorld extends Serializable, GUIUpdater, Drawable {

	int getSizeX();
	int getSizeY();
	boolean isTileValid(int x, int y);
	IBaseTile getTileAt(int x, int y);
	IField getFieldAt(int x, int y);
	ITerrain getTerrain();
	Iterable<? extends IField> getFields();
	Iterable<? extends IField> getNeighborFields();

	Iterable<? extends Entity> collectEntities();
	Iterable<? extends Entity> collectEntities(Class<? extends Entity> clazz);

	Iterable<Player> getPlayers();

	void registerEntity(Entity e);

	WorldViewport getViewport();

}
