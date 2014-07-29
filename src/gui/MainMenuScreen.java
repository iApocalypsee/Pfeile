package gui;

import general.Main;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import comp.Button;

/**
 * <b>4.1.2014 (Josip):</b> Konstruktor braucht keine ScreenManager-Instanz mehr.
 * 
 * 
 * @version 4.1.2014
 * @see ScreenManager
 */
public class MainMenuScreen extends Screen {

	public static final String SCREEN_NAME = "Main menu";
	public static final int SCREEN_INDEX = 0;
	
	/**
	 * Der Singleplayer-Button. (Ach du Schande, Components werden jetzt vollautomatisch
	 * gezeichnet (so stolz auf das Component-System! =D ))
	 */
	private Button sp = new Button
			(Main.getWindowWidth() - 250, Main.getWindowHeight() - 175, this, "Singleplayer");
	
	public MainMenuScreen() {
		super(MainMenuScreen.SCREEN_NAME, MainMenuScreen.SCREEN_INDEX);
		sp.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				onLeavingScreen(this, ArrowSelectionScreen.SCREEN_INDEX);
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
			}
		});
		
		// TODO zusätzlicher init-code für das hauptmenü, falls nötig
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		sp.draw(g);
	}
	
	@Override
	public void keyDown(KeyEvent e) {
		super.keyDown(e);
//		if(Keys.isKeyPressed(KeyEvent.VK_RIGHT)) {
//			onLeavingScreen(this, GameScreen.SCREEN_INDEX);
//		}
	}
	
	
	
}
