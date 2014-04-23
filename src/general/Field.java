package general;

import comp.Component;
import comp.GUIUpdater;
import gui.GameScreen;
import gui.Screen;
import player.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Represents a field.
 * <p>2.3.2014</p>
 * <ul>
 *     <li>See update <code>2.3.2014</code> in {@linkplain player.AttackContainer}</li>
 * </ul>
 * <p>30.3.2014</p>
 * <ul>
 *     <li>FIXME Shouldn't I move the class to package "player" and rename the package "player"?
 *     Because I need some methods from the vision system, which are not public.</li>
 * </ul>
 * @version 2.3.2014
 */
public abstract class Field extends Component implements AttackContainer, GUIUpdater {

	/**
	 * The position of the field on the grid in x-coordinate space.
	 */
	private int gridX;

	/**
	 * The position of the field on the grid in y-coordinate space.
	 */
	private int gridY;

	/**
	 * The world in which the field is generated in.
	 */
	private World world;

	/**
	 * Indicates whether the field is in the fog of war. Fog of war means that the
	 * field has not been discovered by the player yet.
	 */
	private boolean inFogOfWar = true;

	/**
	 * Indicates whether the player can currently see this field.
	 */
	private boolean fieldVisible = false;

	/**
	 * The entities that are standing on this field.
	 */
	private HashMap<String, Entity> entities = new HashMap<String, Entity>();

	/**
	 * The players that are on this field.
	 */
	private HashMap<String, Player> players = new HashMap<String, Player>();

	/**
	 * The attacks registered on this field.
	 */
	private LinkedList<AttackQueue> attackQueues = new LinkedList<AttackQueue>();



	private static BufferedImage DEFAULT_IMAGE = null;

	/**
	 * The info box displaying some informations about the field currently pointed at.
	 */
	static FieldInfoBox infoBox = new FieldInfoBox();

	static {
		try {
			DEFAULT_IMAGE = ImageIO.read(DesertField.class.getClassLoader().
					getResourceAsStream("resources/gfx/field textures/textureNotFound_Field.png"));
		} catch (FileNotFoundException e) {
			System.err.println("Default image for field could not be found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO exception during loading of default image.");
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new field instance
	 * @param x The x position on the grid.
	 * @param y The y position on the grid.
	 */
	public Field(int x, int y, World world) {
		//TODO has to be fixed in component
		//super(x * WorldViewport.STD_FIELD_DIMENSION, y * WorldViewport.STD_FIELD_DIMENSION, WorldViewport.STD_FIELD_DIMENSION, WorldViewport.STD_FIELD_DIMENSION, GameScreen.getInstance());
		super(0, 0, 0, 0, GameScreen.getInstance());
		gridX = x;
		gridY = y;
		this.world = world;
		//updateGUI();
		setBackingScreen(GameScreen.getInstance());

		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (Field.this.world.getActivePlayer().hasTurn()) {
					if (e.getButton() == 3) {
						Player player = Field.this.world.getTurnPlayer();
						player.move(getBoardX() - player.getBoardPosition().x,
								getBoardY() - player.getBoardPosition().y);
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				infoBox.retrieveData(Field.this);
			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});

		Class<VisionableArea> t = VisionableArea.class;
		try {
			Method m = t.getDeclaredMethod("addEntry", Field.class);
			//m = t.getMethod("addEntry", Field.class);
			m.setAccessible(true);
			m.invoke(null, this);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Draws the default field. This method should not be called by subclasses
	 * as long as they have a picture to represent themselves.
	 * @param g The graphics object.
	 */
	@Override
	public void draw(Graphics2D g) {
		drawHelper(g, DEFAULT_IMAGE);
	}

	/**
	 * Returns the entity mapped to the name, or <code>null</code> if no entity
	 * with this name is currently on the field.
	 * @param name The name with which the entity is marked
	 * @return The entity mapped to the name, or <code>null</code>, if no entity
	 * with this name is currently on this field.
	 */
	public Entity getEntityByName(String name) {
		if(entities.containsKey(name)) {
			return entities.get(name);
		} else {
			return null;
		}
	}

	/**
	 * Adds a new entity to the field. With this call, the entity is set on the field.
	 * @param entity The entity to register.
	 */
	public void addEntity(Entity entity) {
		entities.put(entity.getName(), entity);
	}

	/**
	 * Removes an entity from the field. With this call, the entity should not
	 * stand anymore on the field.
	 * @param entity The entity to unregister.
	 */
	public void removeEntity(Entity entity) {
		if(entities.containsValue(entity)) {
			entities.remove(entity.getName());
		}
	}

	/**
	 * Returns the world in which the field is generated in.
	 * @return The world in which the field is generated in.
	 */
	public World getWorld() {
		return world;
	}

	void setWorld(World world) {
		this.world = world;
	}

	/**
	 * Returns the x-position of the field on the world grid.
	 * @return The x-position of the field on the world grid.
	 */
	public int getBoardX() {
		return gridX;
	}

	/**
	 * Returns the y-position of the field on the world grid.
	 * @return The y-position of the field on the world grid.
	 */
	public int getBoardY() {
		return gridY;
	}

	Color background = new Color(0.30588236f, 0.53333336f, 0.7019608f, 0.5f);
	Color shoot_attempt = new Color(0.59607846f, 0.27450982f, 0.19607843f, 0.6f);
	Color invalid_shoot_field = new Color(0.4f, 0.4f, 0.4f, 0.5f);
	Color non_visible_field_color = new Color(0.0f, 0.0f, 0.0f, 0.35f);

	/**
	 * Lil' helper function.
	 * @param img The image to draw according to the field constraints.
	 */
	protected final void drawHelper(Graphics2D g, BufferedImage img) {

		if(world.getActivePlayer().getVision().getVisibility(this) == VisionableArea.VisionState.UNREVEALED) {
			g.setColor(Color.darkGray);
			g.fillRect(getX(), getY(), getWidth(), getHeight());
		} else {
			g.drawImage(img, getX(), getY(), getWidth(), getHeight(), null);

			if(world.getActivePlayer().getVision().getVisibility(this) == VisionableArea.VisionState.NONVISIBLE) {
				g.setColor(non_visible_field_color);
				g.fillRect(getX(), getY(), getWidth(), getHeight());
			} else {
				// just get the first entry in the entity collection and draw it
				for(Entity entity : entities.values()) {
					entity.draw(g);
					break;
				}
			}
		}

		// check for mouse position
		if(getBounds().contains(Screen.getMousePosition())) {

			if(world.getActivePlayer().hasTurn()) {

				if(world.getActivePlayer().isAttemptingShoot()) {

					// setzt die Farbe, je nachdem, ob der Spieler auf dem betreffenden Feld steht
					if(world.getActivePlayer().getStandingOn() != this) {
						g.setColor(shoot_attempt);
					} else {
						g.setColor(invalid_shoot_field);
					}



				} else {

					g.setColor(background);

				}

				g.fillRect(getX(), getY(), getWidth(), getHeight());

			}

			if(getBackingScreen().getManager().getActiveScreen() == GameScreen.getInstance()) {
				g.setColor(background);
				g.fillRect(getX(), getY(), getWidth(), getHeight());
			}

		}
	}

	@Override
	public void updateGUI() {
		setX((int) (gridX * WorldViewport.STD_FIELD_DIMENSION * world.getViewport().getZoomFactor() + world.getViewport().getShiftX()));
		setY((int) (gridY * WorldViewport.STD_FIELD_DIMENSION * world.getViewport().getZoomFactor() + world.getViewport().getShiftY()));
		setWidth((int) (WorldViewport.STD_FIELD_DIMENSION * world.getViewport().getZoomFactor()) - 1);
		setHeight((int) (WorldViewport.STD_FIELD_DIMENSION * world.getViewport().getZoomFactor()) - 1);
		getBounds().invalidate();

		/*
		// TODO Vielleicht einen Pixel Rand lassen???
		if(Main.getWindowDimensions().contains(getSimplifiedBounds())) {

			world.getViewport().shiftAbs(
					world.getViewport().getShiftX() - (world.getFieldAt(world.getSizeX() - 1, world.getSizeY() - 1).getX() + WorldViewport.STD_FIELD_DIMENSION),
					world.getViewport().getShiftY() - (world.getFieldAt(world.getSizeX() - 1, world.getSizeY() - 1).getY() + WorldViewport.STD_FIELD_DIMENSION)
			);

		}
		*/
	}

	/**
	 * Registers a new attack on the field.
	 * @param attackQueue The attack queue to register.
	 */
	public void registerAttack(AttackQueue attackQueue) {
		attackQueues.add(attackQueue);
	}

	/**
	 * Registers a new attack on the combatant.
	 *
	 * @param event The attack to register.
	 */
	@Override
	public void registerAttack(AttackEvent event) {
        AttackQueue queue = new AttackQueue(event);
        attackQueues.add(queue);
	}

	/**
	 * Unregisters an attack. Maybe it has bugs! TODO
	 * @param aWeapon The weapon.
	 */
	public void unregisterAttack(Class<? extends Weapon> aWeapon) {
		for (AttackQueue q : attackQueues) {
			if (q.getWeapon().getClass() == aWeapon) {
				attackQueues.remove(q);
				break;
			}
		}
	}

	public void unregisterAttack(AttackQueue queue) {
		if(attackQueues.contains(queue)) {
			attackQueues.remove(queue);
		}
	}

	/**
	 * Returns <code>true</code> if the combatant is being attacked.
	 *
	 * @return <code>true</code> if the combatant is being attacked.
	 */
	@Override
	public boolean isAttacked() {
		return false;
	}

	/**
	 * Returns <code>true</code> if the combatant is being attacked with.
	 *
	 * @param w The weapon to check.
	 * @return <code>true</code> if the combatant is being attacked with.
	 */
	@Override
	public boolean isAttackedBy(Class<? extends Weapon> w) {
		return false;
	}

	/**
	 * Returns <code>true</code> if the attack container is being attacked
	 * by a combatant.
	 *
	 * @param combatant The combatant from whom to check if he is attacking.
	 * @return <code>true</code> if the attack container is being attacked
	 * by the combatant.
	 */
	@Override
	public boolean isAttackedBy(Combatant combatant) {
		return false;
	}

	/**
	 * Returns all attack queues on this attack container which have specified
	 * aggressor. If no attack queue matches with specified combatant, <code>null</code>
	 * is returned.
	 *
	 * @param aggressor The combatant from whom to check.
	 * @return All attack queues matching with the combatant, or <code>null</code>
	 * if nothing matches.
	 */
	@Override
	public ArrayList<AttackQueue> getAttackQueuesBy(Combatant aggressor) {
		ArrayList<AttackQueue> matches = new ArrayList<AttackQueue>();
		matches.ensureCapacity(attackQueues.size());
		for(AttackQueue queue : attackQueues) {
			if(queue.getAggressor() == aggressor) {
				matches.add(queue);
			}
		}
		// return null if the array list is empty, to delete the not-needed memory immediately
		if(matches.isEmpty()) {
			return null;
		} else {
			return (ArrayList<AttackQueue>) Collections.unmodifiableCollection(matches);
		}
	}

	/**
	 * Returns all attack queues on this attack container which have specified
	 * weapon. If no attack queue matches with specified weapon, <code>null</code>
	 * is returned.
	 *
	 *
	 * @param aWeapon The weapon.
	 * @return All attack queues matching with the weapon, or <code>null</code>
	 * if nothing matches.
	 */
	@Override
	public ArrayList<AttackQueue> getAttackQueuesBy(Class<? extends Weapon> aWeapon) {
		// look for matches in queues
		ArrayList<AttackQueue> matches = new ArrayList<AttackQueue>();
		matches.ensureCapacity(attackQueues.size());
		for(AttackQueue queue : attackQueues) {
			if(queue.getWeapon().getClass() == aWeapon) {
				matches.add(queue);
			}
		}
		// return null if the array list is empty, to delete the not-needed memory immediately
		if(matches.isEmpty()) {
			return null;
		} else {
			return (ArrayList<AttackQueue>) Collections.unmodifiableCollection(matches);
		}
	}

	public boolean isInFogOfWar() {
		return inFogOfWar;
	}

	public void setInFogOfWar(boolean inFogOfWar) {
		this.inFogOfWar = inFogOfWar;
	}

	/**
	 * Returns <code>true</code> if the specified entity can observe the field
	 * (if field is inside the vision borders of the entity)
	 * @param entity The entity to check.
	 * @return <code>true</code> if the specified entity can observe the field.
	 */
	public VisionableArea.VisionState isObservable(Entity entity) {
		return entity.getVision().getVisibility(entity.getStandingOn());
	}

	public LinkedList<Entity> getEntities() {
		LinkedList<Entity> e = new LinkedList<Entity>();
		Collection<Entity> c = world.getEntities().values();
		for(Entity entity : c) {
			if(entity.getStandingOn() == this) {
				e.add(entity);
			}
		}
		return e;
	}

	/**
	 * Returns the field type name.
	 * @return The field type name.
	 */
	public abstract String getFieldType();
}
