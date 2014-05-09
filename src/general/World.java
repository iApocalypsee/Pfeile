package general;

import comp.GUIUpdater;
import general.field.DesertField;
import general.field.Field;
import general.field.ForestField;
import general.field.GrassField;
import general.field.HighlandField;
import general.field.JungleField;
import general.field.MountainField;
import general.field.PlainsField;
import general.field.SeaField;
import general.field.SnowField;
import general.field.WastelandField;
import gui.Drawable;
import player.Entity;
import player.Player;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * 9.2.2014: TODO ClassLoader für Fields muss noch erstellt werden!
 *
 * @author Josip
 * @version 2/9/14
 */
public class World implements Drawable, GUIUpdater {

	/**
	 * The width of the world.
	 */
	private int sizeX;

	/**
	 * The height of the world.
	 */
	private int sizeY;

	/**
	 * The world viewport.
	 */
	private WorldViewport viewport = new WorldViewport(this);

	private TurnManager turnManager = null;

	/**
	 * Box für Lebensleiste und Zeit
	 */
	public static TimeLifeBox timeLifeBox;

	/**
	 * Box für Lebensleiste und Zeit
	 */
	public TimeLifeBox getTimeLifeBox() {
		return timeLifeBox;
	}

	/**
	 * The field data.
	 */
	private Field[][] fields = null;

	private HashMap<String, Entity> entities = new HashMap<String, Entity>();

	/**
	 * Creates a new world object with defined size of the world.
	 *
	 * @param sizeX The width of the world.
	 * @param sizeY The height of the world. Measured downwards.
	 */
	World(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		fields = new Field[sizeX][sizeY];
		// set up the args object for the turn manager
		TurnManagerInstanceArgs args = new TurnManagerInstanceArgs();
		args.setMaxPlayers(5);
		// instanciating the turn manager
		turnManager = new TurnManager(args);

		generateDefault();

		timeLifeBox = new TimeLifeBox();
	}

	void generateDefault() {
		Random r = new Random();

		// generate the fields' array content
		for (int x = 0; x < fields.length; x++) {
			for (int y = 0; y < fields[x].length; y++) {

				// generate the field type first
				int freq = r.nextInt(100) + 1;

				Field resField = null;

				while (resField == null) {

					if (freq >= 1 && freq <= 12) { // 12%
						resField = new GrassField(x, y, this);
					}
					if (freq >= 13 && freq <= 27) { // 15%
						resField = new ForestField(x, y, this);
					}
					if (freq >= 28 && freq <= 39) { // 12%
						resField = new PlainsField(x, y, this);
					}
					if (freq >= 40 && freq <= 50) { // 11%
						resField = new HighlandField(x, y, this);
					}
					if (freq >= 51 && freq <= 60) { // 10%
						resField = new MountainField(x, y, this);
					}
					if (freq >= 61 && freq <= 72) { // 12%
						resField = new DesertField(x, y, this);
					}
					if (freq >= 73 && freq <= 84) { // 12%
						resField = new SnowField(x, y, this);
					}
					if (freq >= 85 && freq <= 89) { // 5%
						resField = new SeaField(x, y, this);
					}
					if (freq >= 90 && freq <= 97) { // 8%
						resField = new JungleField(x, y, this);
					}
					if (freq >= 98 && freq <= 100) { // 3%
						resField = new WastelandField(x, y, this);
					}

				}

				fields[x][y] = resField;

			}
		}

		System.out.println("Done generating.");


	}

	void generateDefault(long seed) {
		Random r = new Random(seed);

		// generate the fields' array content
		for (int x = 0; x < fields.length; x++) {
			for (int y = 0; y < fields[x].length; y++) {

				// generate the field type first
				int freq = r.nextInt(100) + 1;
				Field resField = null;

				while (resField == null) {

					if (freq >= 1 && freq <= 12) { // 12%
						resField = new GrassField(x, y, this);
					}
					if (freq >= 13 && freq <= 27) { // 15%
						resField = new ForestField(x, y, this);
					}
					if (freq >= 28 && freq <= 39) { // 12%
						resField = new PlainsField(x, y, this);
					}
					if (freq >= 40 && freq <= 50) { // 11%
						resField = new HighlandField(x, y, this);
					}
					if (freq >= 51 && freq <= 60) { // 10%
						resField = new MountainField(x, y, this);
					}
					if (freq >= 61 && freq <= 72) { // 12%
						resField = new DesertField(x, y, this);
					}
					if (freq >= 73 && freq <= 84) { // 12%
						resField = new SnowField(x, y, this);
					}
					if (freq >= 85 && freq <= 89) { // 5%
						resField = new SeaField(x, y, this);
					}
					if (freq >= 90 && freq <= 97) { // 8%
						resField = new JungleField(x, y, this);
					}
					if (freq >= 98 && freq <= 100) { // 3%
						resField = new WastelandField(x, y, this);
					}

				}

				fields[x][y] = resField;

			}
		}
	}

	/**
	 * Creates a world with specified dimensions and specified seed.
	 *
	 * @param sizeX The width of the world.
	 * @param sizeY The height of the world. Measured downwards.
	 * @param seed  The seed.
	 */
	World(int sizeX, int sizeY, long seed) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		fields = new Field[sizeX][sizeY];
		// set up the args object for the turn manager
		TurnManagerInstanceArgs args = new TurnManagerInstanceArgs();
		args.setMaxPlayers(5);
		// instanciating the turn manager
		turnManager = new TurnManager(args);
		generateDefault(seed);

		timeLifeBox = new TimeLifeBox();
	}

	/**
	 * Returns the size of the world in x-direction. (Nicht 0-basiert)
	 *
	 * @return The size of the world in x-direction.
	 */
	public int getSizeX() {
		return sizeX;
	}

	/**
	 * Returns the size of the world in y-direction. (Nicht 0-basiert)
	 *
	 * @return The size of the world in y-direction.
	 */
	public int getSizeY() {
		return sizeY;
	}

	public Field getFieldAt(int x, int y) {
		if (isPositionValid(x, y)) {
			return fields[x][y];
		} else {
			return null;
		}
	}

	/**
	 * Returns <code>true</code> if the specified position can be mapped to a field.
	 * Note that the method returns true if the coordinates are valid array coordinates.
	 * So the coordinates <code>(0|0)</code> are always valid.
	 * This method is not subject to change anymore. It would cause a lot of confusion.
	 *
	 * @param x The x coordinate to check.
	 * @param y The y coordinate to check.
	 * @return <code>true</code> if the specified position can be mapped to a field
	 */
	public boolean isPositionValid(int x, int y) {
		return x >= 0 && x < sizeX && y >= 0 && y < sizeY;
	}


	/**
	 * Returns the viewport on the world.
	 *
	 * @return The viewport on the world.
	 */
	public WorldViewport getViewport() {
		return viewport;
	}

	public Player getActivePlayer() {
		return turnManager.activePlayer;
	}

	public void setActivePlayer(Player player) {
		turnManager.activePlayer = player;
	}

	public Player getTurnPlayer() {
		return turnManager.turnPlayer;
	}

	public void setTurnPlayer(Player player) {
		turnManager.turnPlayer = player;
	}

	public int getMaxPlayers() {
		return turnManager.maxPlayers;
	}

	public Player getPlayerByIndex(int index) {
		return turnManager.getPlayerByIndex(index);
	}

	/**
	 * Adds an entity. If the entity is a com.github.pfeile.player object, this method redirects request
	 * to {@link World#addPlayer(com.github.pfeile.player.Player)}.
	 *
	 * @param entity The entity to add.
	 */
	public void addEntity(Entity entity) {
		if (entity instanceof Player) {
			addPlayer((Player) entity);
		} else {
			entities.put(entity.getName(), entity);
		}
	}

	public Entity getEntityByName(String name) {
		return entities.get(name);
	}

	/**
	 * Removes an entity. If the entity is a com.github.pfeile.player object, this method redirects request
	 * to {@link World#removePlayer(com.github.pfeile.player.Player)}.
	 *
	 * @param entity The entity to remove.
	 */
	public void removeEntity(Entity entity) {
		if (entity instanceof Player) {
			removePlayer((Player) entity);
		} else {
			entities.remove(entity.getName());
		}
	}

	/**
	 * Adds a com.github.pfeile.player to the com.github.pfeile.player list.
	 *
	 * @param player The com.github.pfeile.player to add.
	 */
	public void addPlayer(Player player) {
		turnManager.addPlayer(player);
		if (player.getName().equals(Mechanics.getUsername())) {
			turnManager.activePlayer = player;
			turnManager.turnPlayer = player;
		}
	}

	/**
	 * Removes a com.github.pfeile.player from the com.github.pfeile.player list.
	 *
	 * @param player The com.github.pfeile.player to remove.
	 */
	public void removePlayer(Player player) {
		turnManager.removePlayer(player);
	}

	/**
	 * Returns the amount of players in the world.
	 *
	 * @return The amount of players in the world.
	 */
	public int getPlayerCount() {
		return turnManager.players.size();
	}

	public TurnManager getTurnManager() {
		return turnManager;
	}

	/**
	 * Zeichnet die Welt: 
	 * Felder, Spieler, Lebensleiste (mit Box), FieldInfoBox und TimeClock
	 */
	@Override
	public void draw(Graphics2D g) {
		for (Field[] fields1 : fields) {
			for (Field f : fields1) {
				f.draw(g);
			}
		}

		World.timeLifeBox.draw(g);

		for (Player p : turnManager.players) {
			p.draw(g);
		}

		Field.infoBox.draw(g);
		
		Main.timeObj.draw(g);
	}
	
	/** Draws the World: only Fields and Players will be drawn. 
	 * @see #draw(java.awt.Graphics2D)
	 */
	public void drawReduced(Graphics2D g) {
		for (Field[] fields1 : fields) {
			for (Field f : fields1) {
				f.draw(g);
			}
			for (Player p : turnManager.players) {
				p.draw(g);
			}
		}
	}

	@Override
	public void updateGUI() {
		for (int x = 0; x < fields.length; x++) {
			for (int y = 0; y < fields[x].length; y++) {
				fields[x][y].updateGUI();
			}
		}
		for (Player p : turnManager.players) {
			p.updateGUI();
		}
	}

	/**
	 * @author Josip
	 * @version 10.2.2014
	 */
	public static class TurnManager {

		private int maxPlayers;
		private Player activePlayer = null;
		private Player turnPlayer = null;
		private LinkedList<Player> players = new LinkedList<Player>();

		private TurnManager(TurnManagerInstanceArgs instanceArgs) {
			maxPlayers = instanceArgs.getMaxPlayers();
		}

		public int getMaxPlayers() {
			return maxPlayers;
		}

		public Player getActivePlayer() {
			return activePlayer;
		}

		/**
		 * Adds a com.github.pfeile.player to the turn manager.
		 *
		 * @param p The com.github.pfeile.player to add.
		 */
		public void addPlayer(Player p) {
			players.add(p);
			p.setIndex(players.indexOf(p));
		}

		/**
		 * Removes a com.github.pfeile.player from the turn manager.
		 *
		 * @param p The com.github.pfeile.player to remove.
		 */
		public void removePlayer(Player p) {
			players.remove(p);
			p.setIndex(-1);
		}

		/**
		 * Returns the com.github.pfeile.player mapped to the specified index.
		 *
		 * @param index The index of the com.github.pfeile.player, retrieved by {@link com.github.pfeile.player.Player#getIndex()}
		 * @return The com.github.pfeile.player mapped to the index.
		 * @throws IndexOutOfBoundsException if the index is out of range.
		 */
		public Player getPlayerByIndex(int index) {
			return players.get(index);
		}
	}

	/**
	 * Used for instanciating a {@link com.github.pfeile.general.World.TurnManager}
	 *
	 * @author Josip
	 * @version 2/10/14
	 */
	static class TurnManagerInstanceArgs {

		private int maxPlayers;

		public TurnManagerInstanceArgs() {
		}

		public int getMaxPlayers() {
			return maxPlayers;
		}

		public void setMaxPlayers(int maxPlayers) {
			this.maxPlayers = maxPlayers;
		}
	}

	/**
	 * udated die Weltgroesse bei Beginn des Spiels
	 */
	public void updateWorldSizeAtBeginning() {

		getViewport().zoomAbs(12f / (Mechanics.worldSizeX + 1));

		for (Field[] fields1 : fields) {
			for (Field f : fields1) {
				f.updateGUI();
			}
		}
	}
}
