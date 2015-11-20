package gui.screen;

import gui.Drawable;

import java.awt.*;
import java.util.Date;
import java.util.Hashtable;

/**
 * Der ScreenManager verwaltet die einzelnen Screens. Er ist daf�r zust�ndig,
 * dass die Screens gewechselt werden, <b>nicht daf�r, dass der aktuelle Screen
 * gezeichnet wird.</b> Der aktuelle Screen wird vom ScreenManager durch die
 * Methode {@link ScreenManager#getActiveScreen()} bereitgestellt und dessen
 * {@link Screen#draw(Graphics2D)}-Methode wird von GameWindow aus
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

	/**
	 * @param activeScreen the activeScreen to set
	 * @throws RuntimeException wenn kein Screen anhand des Index gefunden werden kann
	 */
	public void setActiveScreen(Screen activeScreen) {
		if(getScreens().containsValue(activeScreen)) {
            if (this.activeScreen != null)
                this.activeScreen.onScreenLeft.apply(new Screen.ScreenChangedEvent(activeScreen.SCREEN_INDEX));
            lastScreenChange = new Date();
            this.activeScreen = activeScreen;
            activeScreen.onScreenEnter.apply();
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
        // It's not necessary to check; every Screen is initialized
		getActiveScreen().drawChecked(g);
	}
}
