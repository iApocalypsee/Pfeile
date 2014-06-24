package player;

import com.sun.istack.internal.Nullable;

import comp.GUIUpdater;
import general.*;
import general.field.Field;
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
import java.util.LinkedList;

import comp.Component;

import javax.imageio.ImageIO;

import player.weapon.AttackContainer;
import player.weapon.AttackEvent;
import player.weapon.AttackQueue;
import player.weapon.Weapon;

/**
 * Represents an entity moving and "living" on the world. <p></p>
 * <b>10.2.2014:</b>
 * <ul>
 *     <li>Entity now relies on new abstract field classes instead of old one.</li>
 * </ul>
 * @version 10.2.2014
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

	public Entity(SpawnEntityInstanceArgs instanceArgs) {
		this(stdImage, instanceArgs);
	}

	public Entity(final BufferedImage img, SpawnEntityInstanceArgs instanceArgs) {
		super(0, 0, 0, 0, GameScreen.getInstance());
		// writing to instance attributes
		boardX = instanceArgs.getSpawnX();
		boardY = instanceArgs.getSpawnY();
		world = instanceArgs.getWorld();
		image = img;
		inventory = new Inventory(this);
		// updates the com.github.pfeile.gui as the last step
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
	
	/**TODO: make move by using a button 
	 * Bewegt die Entity relativ zu ihrer aktuellen Position. Wenn das Zielfeld nicht unzug�nglich ist, dann bewegt er sich nicht
	 * @param x Die relative x-Bewegungsrichtung.
	 * @param y Die relative y-Bewegungsrichtung.
	 */
	public void move(int x, int y) {
		
		Field field = world.getFieldAt(boardX, boardY);
		
		int posX = boardX, posY = boardY;
		
		if(posX + x >= world.getSizeX()) {
			posX = world.getSizeX() - 1;
		} else if(posX + x < 0) {
			posX = 0;
		} else {
			posX += x;
		}

		if(posY + y >= world.getSizeY()) {
			posY = world.getSizeY() - 1;
		} else if(posY + y < 0) {
			posY = 0;
		} else {
			posY += y;
		}

		Field newField = world.getFieldAt(posX, posY);
		
		if (field.isAccessable()) {
			field.removeEntity(this);
			newField.addEntity(this);
			boardX = posX;
			boardY = posY;
			updateGUI();
		}
	}
	
	/**
	 * Teleportiert die Entity zur absoluten Position auf der Welt. Wenn das Zielfeld unzug�glich ist, dann bleibt die aktuelle Position erhalten.
	 * @param world_x Die absolute x-Koordinate. (0 bis worldSizeX-1)
	 * @param world_y Die absolute x-Koordinate. (0 bis worldSizeY-1)
	 * @throws IllegalArgumentException wenn die Position nicht g�ltig ist
	 */
	public void teleport(int world_x, int world_y) {
		
		// check if the position is valid
		if(world.isPositionValid(world_x, world_y) == false) {
			throw new IllegalArgumentException("Invalid coordinates: " + world_x + "; " + world_y);
		}
		if (world.getFieldAt(world_x, world_y).isAccessable() == false) {
			System.out.println("Field (" + boardX + "|" + boardY + ") is not accessable.");
			return;
		}
		// updates data
		world.getFieldAt(boardX, boardY).removeEntity(this);
		boardX = world_x;
		boardY = world_y;
		world.getFieldAt(boardX, boardY).addEntity(this);
		// updates the screen
		updateGUI();
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
	@Nullable
	public AttackQueue[] getAttackQueuesBy(Combatant aggressor) {
		LinkedList<AttackQueue> matching = new LinkedList<AttackQueue>();
		for(AttackQueue q : queue) {
			if(q.getAggressor() == aggressor) {
				matching.add(q);
			}
		}
		if(matching.isEmpty()) {
			return null;
		} else {
			return (AttackQueue[]) matching.toArray();
		}
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
	@Nullable
	public AttackQueue[] getAttackQueuesBy(Class<? extends Weapon> aWeapon) {
		LinkedList<AttackQueue> matching = new LinkedList<AttackQueue>();
		for(AttackQueue q : queue) {
			if(q.getWeapon().getClass() == aWeapon) {
				matching.add(q);
			}
		}
		if(matching.isEmpty()) {
			return null;
		} else {
			return (AttackQueue[]) matching.toArray();
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

	public int getGridX() {
		return boardX;
	}

	public int getGridY() {
		return boardY;
	}

	public World getWorld() {
		return world;
	}

}
