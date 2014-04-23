package player;

import general.Main;
import general.World;
import gui.GameScreen;
import gui.Screen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * Represents a player on the map.
 * <p>2.3.2014</p>
 * <ul>
 *     <li>Slight changes in {@link Player#attack(AttackEvent)}.</li>
 * </ul>
 * <p>3.3.2014</p>
 * <ul>
 *     <li>Attack process of player is now threaded.</li>
 * </ul>
 * @version 3.3.2014
 */
public class Player extends Entity implements Combatant {

	/**
	 * Der Index des Spielers.
	 */
	private int index;

	/**
	 * Das Leben des Spielers.
	 */
	private Life life;

	/**
	 * Sagt aus, ob der Spieler im Begriff ist, Pfeile zu schießen.
	 */
	private boolean attemptingShoot;

	/**
	 * Das Bild des Spielers.
	 */
	private static BufferedImage stdImage;

	static {

		attackService = Executors.newFixedThreadPool(3);

		try {
			stdImage = ImageIO
					.read(Player.class
							.getClassLoader()
							.getResourceAsStream(
									"resources/gfx/player textures/player-pixelart-smooth.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Der Thread-Pool für die Angriffe.
	 */
	private static final ExecutorService attackService;

	public Player(String name, int spawnX, int spawnY, EntityAttributes attributes) {
		super(stdImage, spawnX, spawnY, attributes);
		setEntityImage(stdImage);
		setName(name);
	}
	
	@Override
	public void teleport(int x, int y) {
		super.teleport(x, y);
	}
	
	@Override
	public void move(int x, int y) {
		super.move(x, y);
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Beendet die Runde des Spielers, der gerade am Zug ist.
	 */
	public void endTurn() {

		if(!hasTurn()) {
			return;
		}

		getWorld().getTurnManager().endTurn();
		GameScreen.getInstance().lockUI();

		World w = GameScreen.getInstance().getWorld();

		if (w.getTurnPlayer() == this) {

			if (getIndex() + 1 == w.getPlayerCount()) {
				w.setTurnPlayer(w.getPlayerByIndex(0));
			} else {
				w.setTurnPlayer(w.getPlayerByIndex(index + 1));
			}
			
			GameScreen.getInstance().lockUI();
			
			if(w.getTurnPlayer() instanceof Bot) {
				Bot b = (Bot) w.getTurnPlayer();
				b.calculateTurn();
			} else {
				if(w.getTurnPlayer() == w.getActivePlayer()) {
					GameScreen.getInstance().releaseUI();
				}
			}

		}

	}

	/**
	 * @return the attemptingShoot
	 */
	public boolean isAttemptingShoot() {
		return attemptingShoot;
	}

	/**
	 * @param attemptingShoot the attemptingShoot to set
	 */
	public void setAttemptingShoot(boolean attemptingShoot) {
		this.attemptingShoot = attemptingShoot;
	}

	/**
	 * Zeichnet den Spieler an der Bildposition.
	 * 
	 * @param g Der Grafikkontext, der beim Bufferswapping hergestellt wird.
	 */
	public void draw(Graphics2D g) {
		g.drawImage(getEntityImage(), getX(), getY(),
				Math.round((getEntityImage().getWidth() * getWorld().getViewport().getZoomFactor())),
				Math.round(getEntityImage().getHeight() * getWorld().getViewport().getZoomFactor()),
				null);

		/*
		 * Nur wenn dieser Spieler der eingeloggte Spieler ist und der aktive
		 * Screen der GameScreen ist wird seine Lebensleiste gezeichnet
		 */
		if (GameScreen.getInstance().getWorld().getActivePlayer() == this
				&& Main.getGameWindow().getScreenManager().getActiveScreen() == Main
						.getGameWindow().getScreenManager().getScreens()
						.get(GameScreen.SCREEN_INDEX)) {
			drawLife(g);
		}
		
		if(GameScreen.getInstance().getWorld().getActivePlayer().attemptingShoot) {
			// nur wenn der Spieler nicht der aktive Spieler ist
			// soll er den Spieler markieren. Auf sich selbst schießen ist unlogisch? Oder
			// doch logisch?
			if(GameScreen.getInstance().getWorld().getActivePlayer() != this) {
				// es ist beabsichtigt, dass das Feld als Referenz benutzt wird
				if(getStandingOn().getBounds().contains(Screen.getMousePosition())) {
					mark(g, BLUE_MARK_COLOR);
				} else {
					mark(g, RED_MARK_COLOR);
				}
			}
		}
		
		if(getStandingOn().getBounds().contains(Screen.getMousePosition())) {
			drawLabel(g);
		}
	}

	/**
	 * Zeichnet die Lebensleiste des Spielers. <br></br>
	 * TODO Wird nach {@link comp.LifeBar} verlegt.
	 * 
	 * @param g Das Graphics-Objekt.
	 */
	private void drawLife(Graphics2D g) {
		/*
		// Lebensleiste
		// Rechteck + Hintergrund für Lebensleiste
		g.setColor(Color.RED);
		g.drawRect(life.getBoundingLife().x - 1, life.getBoundingLife().y - 1,
				125 + 1, life.getBoundingLife().height + 1);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(life.getBoundingLife().x, life.getBoundingLife().y, 125,
				life.getBoundingLife().height);

		// eigentliche Lebensleiste
		if (life.getLife() > 0) {
			Color currentLifeColor = new Color(250 - life.getRelativeLife(),
					Math.round(life.getRelativeLife() * 2.5f), 0);

			g.setColor(currentLifeColor);
			g.fillRect(life.getBoundingLife().x, life.getBoundingLife().y,
					life.getBoundingLife().width, life.getBoundingLife().height);
			g.draw(life.getBoundingLife());

		}

		// Prozentanzeige für das Leben
		int percentPosX = life.getBoundingLife().x + 127 - 36;
		int percentPosY = life.getBoundingLife().y + 40;

		// Hintergrund
		g.setColor(Color.BLACK);
		g.drawRect(percentPosX - 6, percentPosY - 14 - 1, 43 + 1, 18 + 1);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(percentPosX - 5, percentPosY - 14, 43, 18);

		// Prozent
//		Font font_lifePercent = new Font("Mangal", Font.ITALIC, 14);
		g.setColor(Color.WHITE);
		g.setFont(STD_FONT);
		if (life.getRelativeLife() >= 10)
			g.drawString(life.getRelativeLife() + "%", percentPosX, percentPosY);
		else
			g.drawString(life.getRelativeLife() + " %", percentPosX,
					percentPosY);

		// Leben / LebenMax - Anzeige
		// Variablen
		int lifePosX = life.getBoundingLife().x + 4;
		int lifePosY = life.getBoundingLife().y + 40;

		// Hintergrund
		g.setColor(Color.BLACK);
		g.drawRect(lifePosX - 4, lifePosY - 14 - 1, 80 + 1, 18 + 1);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(lifePosX - 3, lifePosY - 14, 80, 18);

		// Prozent
//		Font font_lifeMax = new Font("Mangal", Font.ITALIC, 14);
		g.setColor(Color.WHITE);
		g.setFont(STD_FONT);
		if (life.getLife() >= 100)
			g.drawString(life.getLife() + " / " + life.getMaxLife(), lifePosX,
					lifePosY);
		else if (life.getLife() >= 10)
			g.drawString(" " + life.getLife() + " / " + life.getMaxLife(),
					lifePosX, lifePosY);
		else
			g.drawString("  " + life.getLife() + " / " + life.getMaxLife(),
					lifePosX, lifePosY);
		*/
	}
	
	public Life getLife() {
		return life;
	}

	/**
	 * Lets the combatant attack.
	 *
	 * @param event The event to fire.
	 */
	@Override
	public void attack(final AttackEvent event) {
		attackService.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				// a row of checks and reports to the logger
				// if the player does not have the type of ammo to attack, the thread returns false
				if (!getInventory().contains(event.getWeapon().getClass())) {
					Main.getLogger().log(Level.INFO,
							"Player " + getName() + " does not have " + event.getWeapon().getName());
					return false;
				}


				// if the attack is not registered as authorized by the current player
				// the thread returns false
				if (event.getAggressor() != Player.this) {
					Main.getLogger().log(Level.WARNING, "Attack owner not matching to event.");
					return false;
				}

				// if the specified target is null (for whatsoever reason)
				// the thread (logs a severe problem and ?) throws exception TODO Log this one?
				if (event.getTarget() == null) {
					throw new NullPointerException("Target is null!");
				}

				AttackContainer target = event.getTarget();
				// register  the attack on the target
				target.registerAttack(event);
				// remove the item from the inventory
				try {
					getInventory().removeItem(event.getWeapon().getClass(), 1);
				} catch (Inventory.OutOfItemsException e) {
					e.printStackTrace();
				}
				// use it
				event.getWeapon().use();
				// kick back true as everything went as planned. The attack should now be registered
				// in the target
				return true;
			}
		});
	}

	/**
	 * Returns <code>true</code> if the player has permission to do game-relevant actions.
	 * @return <code>true</code> if the player has turn.
	 */
	public boolean hasTurn() {
		return getWorld().getTurnPlayer() == this;
	}
	
}
