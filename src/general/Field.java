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
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Feld akzeptiert jetzt Angriffe.
 * @author Josip
 * @version 16.2.2014
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

	/** Is the field accesible? 
	 * sea, jungle, dessert and snow are not accessible
	 */
	protected boolean isAccessible;
	
	

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

				if(Field.this.world.getActivePlayer().hasTurn()) {
					if(e.getButton() == 3) {
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

	/**
	 * Lil' helper function.
	 * @param img The image to draw according to the field constraints.
	 */
	protected final void drawHelper(Graphics2D g, BufferedImage img) {
//		if(!inFogOfWar) {
			g.drawImage(img, getX(), getY(), getWidth(), getHeight(), null);
//		} else {
//			g.setColor(Color.DARK_GRAY);
//			g.fillRect(getX(), getY(), getWidth(), getHeight());
//		}

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

	/**
	 * updated die neue GUI-Position, wenn an 'Mechanics.heightStreching' oder
	 * 'Mechanics.widthStreching' etwas geï¿½ndert wurde
	 */
	@Override
	public void updateGUI() {
		
		// die Positionen der Felder am Bildschirm werden gesetzt

		setX((Math.round(world.getViewport().getShiftX()
				+ getBoardX()
				* WorldViewport.STD_FIELD_DIMENSION * world.getViewport().getZoomFactor())));
		setY(Math.round(world.getViewport().getShiftY() + getBoardY() * WorldViewport.STD_FIELD_DIMENSION * world.getViewport().getZoomFactor()));


		setWidth(Math.round(WorldViewport.STD_FIELD_DIMENSION * world.getViewport().getZoomFactor()) - 1);
		setHeight(Math.round(WorldViewport.STD_FIELD_DIMENSION * world.getViewport().getZoomFactor()) - 1);
		
		// wenn die Karte insegsammt zu groß ist, muss sie  verkleinert werden: 
		// zuerst der Randeinschub
		if(world.getFieldAt(world.getSizeX() - 1, world.getSizeY() - 1).getX() + WorldViewport.STD_FIELD_DIMENSION > Main.getWindowWidth() - 1 || 
			world.getFieldAt(world.getSizeX() - 1, world.getSizeY() - 1).getY() + WorldViewport.STD_FIELD_DIMENSION > Main.getWindowHeight() - 1) {
				
//			System.out.println(world.getViewport().getShiftX() + " " + world.getViewport().getShiftY());
			world.getViewport().shiftAbs(world.getViewport().getShiftX() - ((world.getFieldAt(world.getSizeX() - 1, world.getSizeY() - 1).getX() + Math.round(WorldViewport.STD_FIELD_DIMENSION * world.getViewport().getZoomFactor())) - Main.getWindowWidth()), 
									world.getViewport().getShiftY() - ((world.getFieldAt(world.getSizeX() - 1, world.getSizeY() - 1).getY() + Math.round(WorldViewport.STD_FIELD_DIMENSION * world.getViewport().getZoomFactor())) - Main.getWindowHeight()));
//			System.out.println(world.getViewport().getShiftX() + " " + world.getViewport().getShiftY());
			System.err.println("Map doesn't fit into the size of the screen. It gets smaller.");
		}
		// wenn der Randeinschub zu klein (unter 0 --> außerhalb des Bildschirms ist) ist, 
		// wird er wieder reingeschoben inset... = 0 und dann die Vergrößerungsfakorern angeglichen 
			
		if (world.getViewport().getShiftX() < 1 || world.getViewport().getShiftY() < 1) {
			loop_inset: while (true) {
				if (world.getViewport().getShiftX() >= 1 && world.getViewport().getShiftY() >= 1) {
					while (true) {
						if (world.getFieldAt(world.getSizeX() - 1, world.getSizeY() - 1).getX() + WorldViewport.STD_FIELD_DIMENSION > Main.getWindowWidth() || 
								world.getFieldAt(world.getSizeX() - 1, world.getSizeY() - 1).getY() + WorldViewport.STD_FIELD_DIMENSION > Main.getWindowHeight()) {

							world.getViewport().zoomRel(-0.02f);
						} else {
							// die Positionen der Felder am Bildschirm werden neu (angelichen) gesetzt
							setX(Math.round(world.getViewport().getShiftX()
									+ getBoardX()
									* WorldViewport.STD_FIELD_DIMENSION * world.getViewport().getZoomFactor()));
							setY(Math.round(world.getViewport().getShiftY() + getBoardY() * WorldViewport.STD_FIELD_DIMENSION * world.getViewport().getZoomFactor()));

							setWidth(Math.round(WorldViewport.STD_FIELD_DIMENSION * world.getViewport().getZoomFactor()) - 1);
							setHeight(Math.round(WorldViewport.STD_FIELD_DIMENSION * world.getViewport().getZoomFactor()) - 1);

							// beide Schleifen verlassen 
							break loop_inset;
						}
						
					}
				}
				if (world.getViewport().getShiftX() < 1) {
					world.getViewport().shiftRel(1, 0);
				}
				if (world.getViewport().getShiftY() < 0) {
					world.getViewport().shiftRel(0, 1);
				}
			}
		}
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
	 * weapon. If no attack queue matches with specified weapon, <code>null</code>
	 * is returned.
	 *
	 * @param aWeapon The weapon.
	 * @return All attack queues matching with the weapon, or <code>null</code>
	 * if nothing matches.
	 */
	@Override
	public AttackQueue[] getAttackQueuesBy(Class<? extends Weapon> aWeapon) {
		return new AttackQueue[0];
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
	public AttackQueue[] getAttackQueuesBy(Combatant aggressor) {
		return new AttackQueue[0];
	}


	public boolean isInFogOfWar() {
		return inFogOfWar;
	}

	public void setInFogOfWar(boolean inFogOfWar) {
		this.inFogOfWar = inFogOfWar;
	}

	/**
	 * Returns <code>true</code>, if the active player can watch currently this field.
	 * For convenience with {@link comp.Component}, the method has to be named
	 * something else than isVisible().
	 * @return <code>true</code>, if the active player can watch currently this field.
	 */
	public boolean isFieldVisible() {
		return fieldVisible;
	}

	public void setFieldVisible(boolean fieldVisible) {
		this.fieldVisible = fieldVisible;
	}

	/**
	 * Returns the field type name.
	 * @return The field type name.
	 */
	public abstract String getFieldType();
	
	
	public int getGridX() {
		return gridX;
	}
	public void setGridX(int gridX) {
		this.gridX = gridX;
	}
	public int getGridY() {
		return gridY;
	}
	public void setGridY(int gridY) {
		this.gridY = gridY;
	}

	/** @return isAccessible - Ist das Feld zugaenglich?*/
	public boolean isAccessible() {
		return isAccessible;
	}

	/** sets 'isAccessible', wheather the field is or not */
	public void setAccessible(boolean isAccessible) {
		this.isAccessible = isAccessible;
	}
}
