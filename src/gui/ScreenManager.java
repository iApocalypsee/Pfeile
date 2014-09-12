package gui;

import java.awt.Graphics2D;
import java.util.Hashtable;
import java.util.Date;

/**
 * Der ScreenManager verwaltet die einzelnen Screens. Er ist daf�r zust�ndig,
 * dass die Screens gewechselt werden, <b>nicht daf�r, dass der aktuelle Screen
 * gezeichnet wird.</b> Der aktuelle Screen wird vom ScreenManager durch die
 * Methode {@link ScreenManager#getActiveScreen()} bereitgestellt und dessen
 * {@link Screen#drawScreen(Graphics2D)}-Methode wird von GameWindow aus
 * aufgerufen.
 * <br><br>
 * <b>10.1.2014 (Josip):</b> {@link #getLastScreenChange()} kann jetzt genutzt werden.
 * 
 * @version 10.1.2014
 * 
 */
public final class ScreenManager implements Drawable {

	private Screen activeScreen;
	private Hashtable<Integer, Screen> screens = new Hashtable<Integer, Screen>();
	private Date lastScreenChange;

	/**
	 * Erstellt eine neue Instanz des ScreenManagers.
	 */
	public ScreenManager() {}

	/**
	 * @return the activeScreen
	 */
	public Screen getActiveScreen() {
		return activeScreen;
	}
	
	public int getActiveScreenIndex() {
		return activeScreen.SCREEN_INDEX;
	}
	
	// sp�ter DAS HIER AUSKOMMENTIEREN
	static GameScreen ref_gameScreen;

	/**
	 * @param activeScreen the activeScreen to set
	 * @throws RuntimeException wenn kein Screen anhand des Index gefunden werden kann
	 */
	public void setActiveScreen(Screen activeScreen) {
		if(getScreens().containsValue(activeScreen)) {
			this.activeScreen = activeScreen;
			lastScreenChange = new Date();
		} else throw new IllegalArgumentException("Screen is not listed!");
	}
	
	/**
	 * Legt den aktiven Screen anhand des Index fest.
	 * @param index
	 * @throws RuntimeException wenn kein Screen anhand des Index gefunden werden kann
	 */
	public synchronized void setActiveScreen(int index) {
		if(getScreens().containsKey(index)) {
			setActiveScreen(screens.get(index));
		} else throw new IllegalArgumentException("No screen could be found at position " + index + ". " + 
			"Make sure the target screen is listed in the table.");
	}

	/**
	 * @return the screens
	 */
	public Hashtable<Integer, Screen> getScreens() {
		return screens;
	}

	/**
	 * @param screens the screens to set
	 */
	void setScreens(Hashtable<Integer, Screen> screens) {
		this.screens = screens;
		ref_gameScreen = (GameScreen) screens.get(GameScreen.SCREEN_INDEX);
	}

	/**
	 * Passt den letzten Zeitpunkt zur�ck, an dem ein Screen gewechselt wurde.
	 * @return the lastScreenChange
	 */
	public Date getLastScreenChange() {
		return lastScreenChange;
	}

	/**
	 * Setzt den Zeitpunkt, an dem ein Screen gewechselt wird.
	 * @param lastScreenChange the lastScreenChange to set
	 */
	void setLastScreenChange(Date lastScreenChange) {
		this.lastScreenChange = lastScreenChange;
	}

	@Override
	public void draw(Graphics2D g) {
		if (getActiveScreen() != null)
			getActiveScreen().draw(g);
	}
}
