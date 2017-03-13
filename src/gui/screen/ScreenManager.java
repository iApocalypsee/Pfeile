package gui.screen;

import gui.Drawable;

import java.awt.*;
import java.util.Hashtable;
import java.util.Optional;

/**
 * ScreenManager stores the screens in a hashtable, provides the reference to the active screen
 * {@link ScreenManager#getActiveScreen()} and handles screen changes with the methods
 * {@link ScreenManager#requestScreenChange(int)}, {@link ScreenManager#requestScreenChange(Screen)} and the deprecated
 * method {@link ScreenManager#setActiveScreen(Screen)}. It redirects the draw call to the active screen, but it
 * ScreenManager has nothing to do with drawing the screens or calling their listeners.
 */
public final class ScreenManager implements Drawable {

	private Screen activeScreen, transitioning = null;
	private Hashtable<Integer, Screen> screens = new Hashtable<>();
	private long lastScreenChange;

	/**
	 * Creates a new instance of ScreenManager.
	 */
	public ScreenManager() {}

	/**
	 * @return the activeScreen
	 */
	public Screen getActiveScreen() {
		return activeScreen;
	}

	/** The returned Optional<Screen> is the screen to be changed at the next {@link ScreenManager#screenCycle()}.
	 *  If there won't be any screen changes the Optional will contain null.*/
    public Optional<Screen> getTransitioning() {
        return Optional.ofNullable(transitioning);
    }

    public int getActiveScreenIndex() {
		return activeScreen.SCREEN_INDEX;
	}

	/**
	 * @param activeScreen the activeScreen to set
	 * @throws RuntimeException if the screen is not registered
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
	 * Returns the time at which the last screen change happened (via System.getCurrentTimeMillis())
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
