package gui.screen;

import gui.Drawable;

import java.awt.*;
import java.util.Hashtable;
import java.util.Optional;

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

	private Screen activeScreen, transitioning = null;
	private Hashtable<Integer, Screen> screens = new Hashtable<Integer, Screen>();
	private long lastScreenChange;

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

    public Optional<Screen> getTransitioning() {
        return Optional.ofNullable(transitioning);
    }

    public int getActiveScreenIndex() {
		return activeScreen.SCREEN_INDEX;
	}

	/**
	 * @param activeScreen the activeScreen to set
	 * @throws RuntimeException wenn kein Screen anhand des Index gefunden werden kann
     * @deprecated Use requestScreenChange() instead.
     * Changing the active screen in parallel incurs consequences.
     * A better solution is to request a screen change; this request is then processed at the beginning of
     * another update cycle.
	 */
    @Deprecated
	public void setActiveScreen(Screen activeScreen) {
		if(getScreens().containsValue(activeScreen)) {
            if (this.activeScreen != null)
                this.activeScreen.onScreenLeft.apply(new Screen.ScreenChangedEvent(activeScreen.SCREEN_INDEX));
            lastScreenChange = System.currentTimeMillis();
            this.activeScreen = activeScreen;
            activeScreen.onScreenEnter.apply();
		} else throw new IllegalArgumentException("Screen is not listed!");
	}

    /**
     * Like 'setActiveScreen()', only that given screen is becoming active screen when the next update cycle begins.
     * This setup attempts to avoid threading problems that might occur with AWT mouse events and the main thread.
     * @param to The screen to change to.
     */
    public void requestScreenChange(Screen to) {
        if(to == null) throw new NullPointerException();
        if(transitioning == to) return;
        if(transitioning == null && screens.containsValue(to)) {
            transitioning = to;
        } else {
            throw new IllegalArgumentException("ScreenManager already promising to Screen '" + transitioning + "'");
        }
    }

    /**
     * Same as 'requestScreenChange(Screen)', only with plain integer indexing.
     * @param to The screen to change to, referenced by integer index.
     */
    public void requestScreenChange(int to) {
        requestScreenChange(screens.get(to));
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
	public long getLastScreenChange() {
		return lastScreenChange;
	}

    /**
     * Processes the request for a screen change, if any exists.
     */
    public void screenCycle() {
        if(transitioning != null) {
            if(activeScreen != null) {
                activeScreen.onScreenLeft.apply(new Screen.ScreenChangedEvent(transitioning.SCREEN_INDEX));
                activeScreen = transitioning;
                lastScreenChange = System.currentTimeMillis();
                activeScreen.onScreenEnter.apply();
                transitioning = null;
            } else {
                throw new IllegalStateException("No active screen paired with no screen to transition into");
            }
        }
    }

	@Override
	public void draw(Graphics2D g) {
        // It's not necessary to check; every Screen is initialized
		activeScreen.drawChecked(g);
	}
}
