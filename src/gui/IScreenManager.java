package gui;

import gui.screen.Screen;

import java.util.Date;

/**
 * @author Josip Palavra
 * @version 27.07.2014
 */
public interface IScreenManager extends Drawable {

	Screen getActiveScreen();
	void setActiveScreen(Screen screen);
	void setActiveScreen(int index);
	void put(Screen screen);
	Date getLastScreenChange();

}
