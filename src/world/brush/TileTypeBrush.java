package world.brush;

import world.BaseTile;
import world.GridElement;
import world.IEditableTerrain;
import world.IWorld;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Josip Palavra
 * @version 01.06.2014
 */
public class TileTypeBrush implements IRawBrush {

	private int thickness = IBrush.DEFAULT_THICKNESS;
	private IEditableTerrain terrain = null;
	private Class<? extends BaseTile> clazz;

	public TileTypeBrush(IWorld world) {
		if(world == null) throw new NullPointerException();
		this.terrain = (IEditableTerrain) world.getTerrain();
	}

	public Class<? extends BaseTile> getTileClass() {
		return clazz;
	}

	public void setTileClass(Class<? extends BaseTile> clazz) {
		if(clazz == null) throw new NullPointerException();
		this.clazz = clazz;
	}

	/**
	 * Returns the thickness of the brush.
	 * The thickness is the radius of the brush. Every tile in the radius
	 * gets "painted" by the brush (its metadata or anything else is going to
	 * be edited).
	 *
	 * @return The thickness of the brush.
	 */
	@Override
	public int getThickness() {
		return thickness;
	}

	/**
	 * Sets the thickness of the brush. Should not be
	 * any value less than or equal to 0.
	 *
	 * @param thickness The new thickness.
	 */
	@Override
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	/**
	 * Assigns data to the specified coordinates.
	 *
	 * @param pointList The point list.
	 */
	@Override
	public void assignPoints(List<Point> pointList) {
		// outer for loop, iterating over every tile that has to be changed
		for(Point p : pointList) {
			LinkedList<Point> affected = BrushHelper.determinePoints(p, thickness);
			try {
				Constructor<? extends BaseTile> construct = clazz.getConstructor(GridElement.class);

				for(Point affect : affected) {
					if(!terrain.getWorld().isTileValid(affect.x, affect.y)) continue;
					BaseTile t = construct.newInstance(new GridElement(affect.x, affect.y));
					terrain.set(affect.x, affect.y, t);
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}


}
