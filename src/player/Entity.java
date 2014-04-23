package player;

import comp.GUIUpdater;
import general.*;
import gui.ArrowSelectionScreen;
import gui.GameScreen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import comp.Component;

import javax.imageio.ImageIO;

/**
 * Represents an entity moving and "living" on the world. <p></p>
 * <p>2.3.2014</p>
 * <ul>
 *     <li>See update 2.3.2014 in {@link player.AttackContainer}</li>
 * </ul>
 * @version 2.3.2014
 *
 */
public abstract class Entity extends Component implements AttackContainer, GUIUpdater {

	/**
	 * The standard image for entity.
	 */
	private static BufferedImage stdImage;

	/**
	 * Das Inventar des Spielers an Pfeilen.
	 */
	private Inventory inventory;

	/**
	 * Die auf die Entity bezogene Attack-Queue.
	 */
	protected LinkedList<AttackQueue> queue = new LinkedList<AttackQueue>();

	private EntityAttributes attributes;

	private VisionableArea vision = new VisionableArea(this);

	/**
	 * Die x Koordinate der Entity.
	 */
	private int boardX = 0;
	
	/**
	 * Die y Koordinate der Entity.
	 */
	private int boardY = 0;

	/**
	 * The world in which the entity "lives" in.
	 */
	private World world;
	
	private BufferedImage image;
	
	protected static final Color LABEL_COLOR = new Color(0.2f, 0.4f, 0.7f, 0.6f),
			BLUE_MARK_COLOR = new Color(0.0f, 0.0f, 1.0f, 0.2f),
			RED_MARK_COLOR = new Color(1.0f, 0.0f, 0.0f, 0.2f);
	protected static final Font DEBUG_FONT = new Font("Consolas", Font.PLAIN, 11);

	static {
		try {
			stdImage = ImageIO.read(Entity.class.getClassLoader().getResourceAsStream("resources/gfx/player textures/player-pixelart-smooth.png"));
		} catch (IOException e) {
			System.out.println("An IO exception occurred during loading the standard entity image.");
			e.printStackTrace();
			System.exit(1);
		}
	}


	public Entity(final BufferedImage img, int spawnX, int spawnY, EntityAttributes attribs) {
		super(0, 0, 0, 0, GameScreen.getInstance());
		// writing to instance attributes
		boardX = spawnX;
		boardY = spawnY;
		try {
			java.lang.reflect.Field f = GameScreen.class.getDeclaredField("loadedWorld");
			f.setAccessible(true);
			this.world = (World) f.get(GameScreen.getInstance());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			this.world = null;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			this.world = null;
		}
		getStandingOn().addEntity(this);
		image = img;
		inventory = new Inventory(this);
		this.attributes = attribs;
		// updates the gui as the last step
		updateGUI();
		// updates width and height
		setWidth(image.getWidth());
		setHeight(image.getHeight());

		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(world.getActivePlayer().hasTurn() && world.getActivePlayer().isAttemptingShoot()) {
					getBackingScreen().onLeavingScreen(this, ArrowSelectionScreen.SCREEN_INDEX);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});
	}
	
	public void mark(Graphics2D g, Color markColor) {
		
		g.setColor(markColor);
		g.fillRoundRect(getX(), getY(), getWidth(), getHeight(), 5, 5);
		
	}
	
	public void drawLabel(Graphics2D g) {
		g.setColor(LABEL_COLOR);
		g.fillRoundRect(getX(), getY() + getHeight() + 5, Component.getTextBounds(getName(), DEBUG_FONT).width + 30, 25, 5, 5);
		g.setColor(Color.black);
		g.drawString(getName(), getX() + 10, getY() + getHeight() + 20);
	}
	
	/**
	 * Bewegt die Entity relativ zu ihrer aktuellen Position.
	 * @param x Die relative x-Bewegungsrichtung.
	 * @param y Die relative y-Bewegungsrichtung.
	 */
	public void move(int x, int y) {
		// Entity braucht keinen Dekrement mit den neuen Field-Klassen mehr
//		x--;
//		y--;
		Field field = world.getFieldAt(boardX, boardY);
		field.removeEntity(this);

		if(boardX + x >= world.getSizeX()) {
			boardX = world.getSizeX() - 1;
		} else if(boardX + x < 0) {
			boardX = 0;
		} else {
			boardX += x;
		}

		if(boardY + y >= world.getSizeY()) {
			boardY = world.getSizeY() - 1;
		} else if(boardY + y < 0) {
			boardY = 0;
		} else {
			boardY += y;
		}

		field = world.getFieldAt(boardX, boardY);
		field.addEntity(this);

		vision.updateVision();
		/*
		Field f = GameScreen.getWorld().getFields()[boardX][boardY];
		if(f.getStandingEntities().contains(this)) {
			GameScreen.getWorld().getFields()[boardX][boardY].getStandingEntities().remove(this);
		}
		
		// sicherstellen, dass die Entity sich nicht außerhalb der Kartengrenzen bewegt
		if(boardX + x >= GameScreen.getWorld().getFields().length) {
			boardX = GameScreen.getWorld().getFields().length - 1;
		} else if(boardX + x < 0) {
			boardX = 0;
		} else {
			boardX += x;
		}
		
		if(boardY + y >= GameScreen.getWorld().getFields()[boardX].length) {
			boardY = GameScreen.getWorld().getFields()[boardX].length - 1;
		} else if(boardY + y < 0) {
			boardY = 0;
		} else {
			boardY += y;
		}

		GameScreen.getWorld().getFields()[boardX][boardY].getStandingEntities().add(this);
		standingOn = GameScreen.getWorld().getFields()[boardX][boardY];
		*/
		
		System.out.println("Moved " + getName() + " to x=" + boardX + "; y=" + boardY);
		
		updateGUI();
	}
	
	/**
	 * Teleportiert die Entity zur absoluten Position auf der Welt.
	 * @param world_x Die absolute x-Koordinate.
	 * @param world_y Die absolute x-Koordinate.
	 * @throws IllegalArgumentException wenn die Position nicht gültig ist
	 */
	public void teleport(int world_x, int world_y) {
		// Entity braucht keinen Dekrement mit den neuen Field-Klassen mehr
//		world_x--;
//		world_y--;
		// check if the position is valid
		if(!world.isPositionValid(world_x, world_y)) {
			throw new IllegalArgumentException("Invalid coordinates: " + world_x + "; " + world_y);
		}
		// updates data
		world.getFieldAt(boardX, boardY).removeEntity(this);
		boardX = world_x;
		boardY = world_y;
		world.getFieldAt(boardX, boardY).addEntity(this);
		vision.updateVision();
		// updates the screen
		updateGUI();
		/*
		if(GameScreen.getWorld().getFields()[boardX][boardY].getStandingEntities().contains(this)) {
			GameScreen.getWorld().getFields()[boardX][boardY].getStandingEntities().remove(this);
		}
		boardX = world_x;
		boardY = world_y;

		GameScreen.getWorld().getFields()[boardX][boardY].getStandingEntities().add(this);
		standingOn = GameScreen.getWorld().getFields()[boardX][boardY];
		*/
		System.out.println("Teleported "  + getName() + " to x=" + boardX + "; y=" + boardY);

	}
	
	/**
	 * Updated die Position des Spielers auf dem Bildschirms [aufrufen, wenn
	 * sich die X-/Y- Position des Spielers auf dem Feldern geändert hat]
	 */
	@Override
	public void updateGUI() {
		setX((int) (boardX * world.getViewport().getZoomFactor()
						* WorldViewport.STD_FIELD_DIMENSION
						+ world.getViewport().getShiftX()
						+ WorldViewport.STD_FIELD_DIMENSION
						/ 2 - image.getWidth() / 2));

		setY((int) (boardY * world.getViewport().getZoomFactor()
						* WorldViewport.STD_FIELD_DIMENSION
						+ world.getViewport().getShiftY()
						+ (WorldViewport.STD_FIELD_DIMENSION
						/ 2) - (image.getHeight() / 2)));


	}
	
	/**
	 * Erstellt eine neue Instanz von {@link Point} und speichert darin die
	 * derzeitige Position auf der Welt
	 * @return Die Position auf der Welt
	 */
	public Point getBoardPosition() {
		return new Point(boardX, boardY);
	}
	
	/**
	 * Gibt das Bild zurück, das die Entity benutzt, um sich darzustellen.
	 * @return Das Bild.
	 */
	public BufferedImage getEntityImage() {
		return image;
	}
	
	/**
	 * Setzt das Bild neu, mit dem die Entity dargestellt wird.
	 * @param img Das Bild, mit welchem die Entity dargestellt werden soll.
	 * @throws IllegalArgumentException wenn der Parameter null ist.
	 */
	protected void setEntityImage(BufferedImage img) {
		if(img == null) {
			throw new IllegalArgumentException();
		}
		image = img;
	}
	
	/**
	 * Gibt das Feld zurück, auf dem die Entity steht.
	 * @return Das Feld, auf dem die Entity steht.
	 */
	public Field getStandingOn() {
		return world.getFieldAt(boardX, boardY);
	}

	/**
	 * Annulliert <b>einen</b> Angriff mit der angegebenen Waffe, sofern ein
	 * Angriff auf den Spieler mit dieser Waffe registriert ist.
	 * @param w Die Waffe, dessen Angriff annulliert werden soll.
	 */
	@Override
	public void unregisterAttack(Class<? extends Weapon> w) {
		for (int i = 0; i < queue.size(); i++) {
			AttackQueue q = queue.get(i);
			if(w == q.getWeapon().getClass()) {
				queue.remove(i);
				break;
			}
		}
	}

	/**
	 * Gibt <code>true</code> zurück, wenn der Spieler angegriffen wird.
	 * @return <code>true</code>, wenn der Spieler angegriffen wird.
	 */
	@Override
	public boolean isAttacked() {
		return !queue.isEmpty();
	}

	/**
	 * Gibt <code>true</code> zurück, wenn der Spieler mit der angegebenen Waffe angegriffen wird.
	 * @param w Die Waffe, die überprüft werden soll.
	 * @return <code>true</code>, wenn der Spieler mit der angegebenen Waffe angegriffen wird.
	 */
	@Override
	public boolean isAttackedBy(Class<? extends Weapon> w) {
		for (AttackQueue q : queue) {
			if (w == q.getWeapon().getClass()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the inventory of the entity.
	 * @return The inventory of the entity.
	 */
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public void registerAttack(AttackEvent e) {
		AttackQueue q = new AttackQueue(e);
		queue.add(q);
	}

	/**
	 * Registers an already instanciated attack queue.
	 *
	 * @param queue The queue to register.
	 */
	@Override
	public void registerAttack(AttackQueue queue) {
		this.queue.add(queue);
	}

	@Override
	public boolean isAttackedBy(Combatant combatant) {
		for (AttackQueue q : queue) {
			if(q.getAggressor() == combatant) {
				return true;
			}
		}
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
		// look for matches
		ArrayList<AttackQueue> matches = new ArrayList<AttackQueue>();
		matches.ensureCapacity(queue.size());
		for(AttackQueue q : queue) {
			if(q.getAggressor() == aggressor) {
				matches.add(q);
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
		// look for matches
		ArrayList<AttackQueue> matches = new ArrayList<AttackQueue>();
		matches.ensureCapacity(queue.size());
		for(AttackQueue q : queue) {
			if(q.getWeapon().getClass() == aWeapon) {
				matches.add(q);
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
	 * Unregisters a defined instance of attack-queue.
	 *
	 * @param queue The queue object to remove from the combatant's incoming
	 *              attacks.
	 */
	@Override
	public void unregisterAttack(AttackQueue queue) {
		if(this.queue.contains(queue)) {
			this.queue.remove(queue);
		}
	}

	public int getBoardX() {
		return boardX;
	}

	public int getBoardY() {
		return boardY;
	}

	public World getWorld() {
		return world;
	}

	/**
	 * Returns the attributes of the entity.
	 * @return The attributes.
	 */
	public EntityAttributes getAttributes() {
		return attributes;
	}

	public VisionableArea getVision() {
		return vision;
	}
}
