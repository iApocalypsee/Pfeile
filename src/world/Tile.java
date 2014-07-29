package world;

import comp.Component;
import entity.Entity;
import gui.AdjustableDrawing;
import gui.NewWorldTestScreen;
import misc.metadata.OverrideMetaList;
import world.tile.TileCage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Josip
 * @version 20.05.2014
 */
public class Tile extends Component implements ITile {

	private static final long serialVersionUID = 1921867867258539893L;
	/**
	 * The grid coordinates.
	 */
	int gridX, gridY;

	/**
	 * The height of the tile
	 */
	int height;

	/**
	 * The field attached to the tile.
	 */
	Field field;

	World world;

	/**
	 * The list of all entities currently on the tile.
	 */
	private LinkedList<Entity> entities = new LinkedList<Entity>();

	/**
	 * The display color of the tile.
	 */
	Color color;

	static final Color SelectColor = new Color(39, 38, 38, 161);

	/**
	 * The metalist, just like always.
	 */
	private OverrideMetaList metalist = new OverrideMetaList(this);

	private boolean hovered = false;

	TileCage cage = new TileCage(this);

	private boolean entered = false;

	public Tile() {
		// standard color for tiles is black
		color = Color.black;
		// add a basic mouse listener which makes checking mouse coordinates obsolete
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				entered = true;
			}
			@Override
			public void mouseExited(MouseEvent e) {
				entered = false;
			}
		});
		// don't forget to add the component to the screen, otherwise the listeners would not be triggered
		NewWorldTestScreen.add(this);
	}

	/**
	 * Adds an entity to the tile.
	 * @param entity The entity to add.
	 */
	public void addEntity(Entity entity) {
		entities.add(entity);
	}

	@Override
	public int getRequiredMovementPoints() {
		return Integer.MAX_VALUE;
	}

	/**
	 * Removes an entity. If the entity does not exist, a statement will be printed
	 * out to <code>stderr</code>.
	 * @param entity The entity.
	 */
	public void removeEntity(Entity entity) {
		if(!entities.contains(entity)) {
			System.err.println("Entity " + entity + " does not exist at (" + gridX + "|" + gridY + ").");
			return;
		}
		entities.remove(entity);
	}

	@Override
	public int getGridX() {
		return gridX;
	}

	@Override
	public int getGridY() {
		return gridY;
	}

	@Override
	public int getTileHeight() {
		return height;
	}

	@Override
	public Field getField() {
		return field;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public List<Entity> getEntities() {
		return Collections.unmodifiableList(entities);
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public IBaseTile north() {
		int x = gridX - 1, y = gridY + 1;
		if(world.isTileValid(x, y)) return world.getTileAt(x, y);
		else return null;
	}

	@Override
	public IBaseTile northeast() {
		int x = gridX, y = gridY + 1;
		if(world.isTileValid(x, y)) return world.getTileAt(x, y);
		else return null;
	}

	@Override
	public IBaseTile east() {
		int x = gridX + 1, y = gridY + 1;
		if(world.isTileValid(x, y)) return world.getTileAt(x, y);
		else return null;
	}

	@Override
	public IBaseTile southeast() {
		int x = gridX - 1, y = gridY;
		if(world.isTileValid(x, y)) return world.getTileAt(x, y);
		else return null;
	}

	@Override
	public IBaseTile south() {
		int x = gridX - 1, y = gridY - 1;
		if(world.isTileValid(x, y)) return world.getTileAt(x, y);
		else return null;
	}

	@Override
	public IBaseTile southwest() {
		int x = gridX, y = gridY - 1;
		if(world.isTileValid(x, y)) return world.getTileAt(x, y);
		else return null;
	}

	@Override
	public IBaseTile west() {
		int x = gridX - 1, y = gridY - 1;
		if(world.isTileValid(x, y)) return world.getTileAt(x, y);
		else return null;
	}

	@Override
	public IBaseTile northwest() {
		int x = gridX - 1, y = gridY;
		if(world.isTileValid(x, y)) return world.getTileAt(x, y);
		else return null;
	}

	/**
	 * Returns the metadata associated with the key, or <code>null</code>
	 * if none exists.
	 *
	 * @param key The key.
	 * @return The metadata.
	 */
	@Override
	public Object getMetadata(String key) {
		return metalist.get(key);
	}

	/**
	 * Sets a metadata.
	 *
	 * @param key The key.
	 * @param val The value.
	 */
	@Override
	public void setMetadata(String key, Object val) {
		metalist.addMeta(key, val);
	}

	/**
	 * Removes a metadata and returns the object that is being deleted,
	 * or null, if nothing has been deleted.
	 *
	 * @param key The key.
	 * @return The object that has been deleted, or null, if nothing has
	 * been deleted.
	 */
	@Override
	public Object removeMetadata(String key) {
		Object o = metalist.get(key);
		metalist.deleteMeta(key);
		return o;
	}

	private int[] determine_xPoints() {
		//return new int[]{((Double) cage.west()._1()).intValue(), ((Double) cage.south()._1()).intValue(), ((Double) cage.east()._1()).intValue(), ((Double) cage.north()._1()).intValue()};
		return new int[]{(int) cage.west().getX(), (int) cage.south().getX(), (int) cage.east().getX(), (int) cage.north().getX()};
	}

	private int[] determine_yPoints() {
		return new int[]{(int) cage.west().getY(), (int) cage.south().getY(), (int) cage.east().getY(), (int) cage.north().getY()};
	}

	@Override
	public void draw(Graphics2D g) {
		// draw the base tile
		g.setColor(color);
		g.fillPolygon(getBounds());



		if(entered) {
			g.setColor(SelectColor);
			g.fillPolygon(getBounds());
		}
	}

	public void updateGUI() {
		Polygon bounds = getBounds();
		bounds.xpoints = determine_xPoints();
		bounds.ypoints = determine_yPoints();
		bounds.npoints = 4;
		bounds.invalidate();
	}

	public Point2D gridCenter() {
		Point2D p = new Point2D() {
			double x, y;
			/**
			 * Returns the X coordinate of this <code>Point2D</code> in
			 * <code>double</code> precision.
			 *
			 * @return the X coordinate of this <code>Point2D</code>.
			 * @since 1.2
			 */
			@Override
			public double getX() {
				return x;
			}

			/**
			 * Returns the Y coordinate of this <code>Point2D</code> in
			 * <code>double</code> precision.
			 *
			 * @return the Y coordinate of this <code>Point2D</code>.
			 * @since 1.2
			 */
			@Override
			public double getY() {
				return y;
			}

			/**
			 * Sets the location of this <code>Point2D</code> to the
			 * specified <code>double</code> coordinates.
			 *
			 * @param x the new X coordinate of this {@code Point2D}
			 * @param y the new Y coordinate of this {@code Point2D}
			 * @since 1.2
			 */
			@Override
			public void setLocation(double x, double y) {
				this.x = x;
				this.y = y;
			}
		};
		p.setLocation(gridX + 0.5, gridY + 0.5);
		return p;
	}

	@Override
	public TileCage getCage() {
		return cage;
	}
}
