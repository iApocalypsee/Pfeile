package general;

import gui.screen.Screen;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keys implements KeyListener {

	private static boolean [] keys = new boolean [512];
	
	public static boolean isKeyPressed (int keyCode) {
        return keyCode >= 0 && keyCode <= keys.length && keys[keyCode];
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode >= 0 && keyCode <= keys.length)
			keys[keyCode] = true;
		Screen activeScreen = Main.getGameWindow().getScreenManager().getActiveScreen();
		activeScreen.keyDown(e);
		activeScreen.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode >= 0 && keyCode <= keys.length)
			keys[keyCode] = false;
		Main.getGameWindow().getScreenManager().getActiveScreen().keyReleased(e);
	}

	
	// UNUSED
	@Override
	public void keyTyped(KeyEvent e) {}
	
	static void updateKeys() {
		if(Keys.isKeyPressed(KeyEvent.VK_ESCAPE) && Keys.isKeyPressed(KeyEvent.VK_SHIFT)) {
			GameLoop.setRunFlag(false);
		}
	}
}
