package gui;

// 443e1f
import general.Main;
import general.Mechanics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;

import player.Player;

/**
 * <br><br>
 * <b>11.1.2014:</b> Zugriff auf PlayerList restriktiver. <br> Hashtable f�r neues Feld
 * eingef�hrt, aber noch nicht benutzt.
 * <b>24.1.2014:</b> Felder von World nicht mehr static, um Polymorphie von World zu
 * erlauben. Unter Polymorphie von World stelle ich mir zum Beispiel Subklassen von World
 * vor, die andere Welttypen generieren. Der Generierungscode sollte, wenn in World
 * die Infrastruktur daf�r vorhanden ist, nach World verschoben werden.
 * @version 24.1.2014
 * @deprecated {@link general.World} anstelle von dieser World verwenden.
 */
@Deprecated
public class World implements Drawable {

	/**
	 * Der aktive Spieler. Von diesem Spieler wird die Lebensleiste gezeichnet.
	 */
	private Player activePlayer;

	/**
	 * Der Spieler, der gerade an der Reihe ist.
	 */
	private Player turnPlayer;

	/**
	 * The array of players playing in one world.
	 */
	private LinkedList<Player> playerList;

	/**
	 * Array zur leichteren Verwaltung der Fields erste Stelle des Arreys:
	 * X-Koordinate im Gitterfeld zweite Stelle des Arreys: Y-Koordinte im
	 * Gitterfeld
	 * */
	private Field[][] fields = new Field[Mechanics.worldSizeX + 1][Mechanics.worldSizeY - 1];
	
	/**
	 * Behilfsreferenz zum Gamescreen, um Tipperei zu sparen.
	 */
	@SuppressWarnings("unused")
	private static GameScreen ref_gameScreen = (GameScreen) Main.getGameWindow().getScreenManager().
			getScreens().get(GameScreen.SCREEN_INDEX);

	/**
	 * Erstellt ein neues Objekt von World. 
	 */
	public World() {
		setPlayerList(new LinkedList<Player>());

		// KI-Initialisierung (vorerst hier)
//		Bot d = new Bot("Dummie");
//		LinkedList<AbstractArrow> aiArrowList = new LinkedList<AbstractArrow>();
////		aiArrowList.add(new FireArrow());
////		aiArrowList.add(new WaterArrow());
////		aiArrowList.add(new ShadowArrow());
//		registerPlayer(d);
//		
//		for (AbstractArrow a : aiArrowList) {
//			d.getInventory().addArrow(a);
//		}
		
	}



	@Override
	public void draw(Graphics2D g) {
		for (int i = 0; i < Field.fieldsList.size(); i++) {
			Field.fieldsList.get(i).draw(g);
		}

		for (Player p : playerList) {
			p.draw(g);
//			if(ref_gameScreen.hasShootAttempt()) {
//				p.mark(g, MARK_COLOR);
//			}
		}
	}

	/**
	 * Zeichnet einen Pfeil ( --> , nicht den Munitionspfeil). Ein ganz
	 * stinknormaler Pfeil, der von A nach B zeigt.
	 * 
	 * @param from
	 *            Von welchem Feld
	 * @param to
	 *            Zu welchem Feld hin
	 * @deprecated Noch nicht fertig entwickelt.
	 */
	@Deprecated
	public void drawArrow(Field from, Field to, Graphics2D g) {
		
		int to_bounds_center_x = to.getBoundingField().x + to.getBoundingField().width / 2;
		int to_bounds_center_y = to.getBoundingField().y + to.getBoundingField().height / 2;
		
		g.setColor(Color.black);
		g.drawLine(from.getBoundingField().x + from.getBoundingField().width
				/ 2, from.getBoundingField().y + from.getBoundingField().height
				/ 2, to_bounds_center_x, to_bounds_center_y);
		
		g.drawLine(to_bounds_center_x, to_bounds_center_y, to_bounds_center_x + 10, to_bounds_center_y + 10);
		g.drawLine(to_bounds_center_x, to_bounds_center_y, to_bounds_center_x - 10, to_bounds_center_y - 10);		

	}
	
	@Deprecated
	public void drawArrow(Field from, Field to, Color arrowColor, Graphics2D g) {
		
		int to_bounds_center_x = to.getBoundingField().x + to.getBoundingField().width / 2;
		int to_bounds_center_y = to.getBoundingField().y + to.getBoundingField().height / 2;
		
		g.setColor(arrowColor);
		g.drawLine(from.getBoundingField().x + from.getBoundingField().width
				/ 2, from.getBoundingField().y + from.getBoundingField().height
				/ 2, to_bounds_center_x, to_bounds_center_y);
		
		g.drawLine(to_bounds_center_x, to_bounds_center_y, to_bounds_center_x + 10, to_bounds_center_y + 10);
		g.drawLine(to_bounds_center_x, to_bounds_center_y, to_bounds_center_x - 10, to_bounds_center_y - 10);	
		
	}

	/**
	 * @return the activePlayer
	 */
	public Player getActivePlayer() {
		return activePlayer;
	}

	/**
	 * @param activePlayer
	 *            the activePlayer to set
	 */
	public void setActivePlayer(Player activePlayer) {
		this.activePlayer = activePlayer;
	}

	/**
	 * @return the turnPlayer
	 */
	public  Player getTurnPlayer() {
		if (turnPlayer == null) {
			turnPlayer = this.getActivePlayer();
		}
		return turnPlayer;
	}

	/**
	 * @param turnPlayer
	 *            the turnPlayer to set
	 */
	public void setTurnPlayer(Player turnPlayer) {
		this.turnPlayer = turnPlayer;
	}

	protected void setPlayerList(LinkedList<Player> p) {
		this.playerList = p;
	}

	/**
	 * F�gt einen neuen Eintrag in der LinkedList hinzu und weist dem Player
	 * automatisch einen Index zu.
	 * 
	 * @param p
	 *            Der Spieler, der zur Welt hinzugef�gt werden soll.
	 */
	public void registerPlayer(Player p) {
		playerList.add(p);
		p.setIndex(playerList.indexOf(p));
		
		if(p.getName().equals(Mechanics.getUsername())) {
			setActivePlayer(p);
		}
	}

	/**
	 * @return the fields
	 */
	public Field[][] getFields() {
		return fields;
	}

	/**
	 * @param fields
	 *            the fields to set
	 */
	public void setFields(Field[][] fields) {
		this.fields = fields;
	}
	
	// FIXME Restriktiverer Zugriff auf die playerList
	
	/**
	 * Gibt die Anzahl der Spieler auf der Welt zur�ck.
	 * @return
	 * @since 11.1.2014
	 */
	public int getPlayerCount() {
		return playerList.size();
	}
	
	/**
	 * Gibt den Spieler anhand des Index zur�ck.
	 * @param index Der Index des Spielers.
	 * @return
	 * @since 11.1.2014
	 */
	public Player getPlayer(int index) {
		return playerList.get(index);
	}
	
	/**
	 * Gibt den Spieler (oder Bot) zur�ck, der als N�chster am Zug ist.
	 * @return
	 * @since 11.1.2014
	 */
	public Player nextPlayer() {
		if (turnPlayer.getIndex() + 1 == getPlayerCount()) {
			return getPlayer(0);
		} else {
			return getPlayer(turnPlayer.getIndex() + 1);
		}
	}

}
