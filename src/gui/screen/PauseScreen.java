package gui.screen;

import comp.Button;
import comp.List;
import comp.TextBox;
import general.Main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

/**
 * <b>4.1.2014 (Josip):</b> Konstruktor braucht keine ScreenManager-Instanz mehr.
 * 
 * 
 * @version 4.1.2014
 * @see ScreenManager
 */
public class PauseScreen extends Screen {

	public static final Integer SCREEN_INDEX = 2;

	/**
	 * Die Hintergrundfarbe des Pausemenüs.
	 */
	private Color backgroundColor = new Color(0.0f, 0.0f, 0.0f, 0.25f);

	private Button toGame;
	private Button toMainMenu;

	private TextBox box;
	
	/**
	 * Diese List ist nötig! Nicht löschen!
	 */
	private List list;
	
	public PauseScreen() {
		super("Pause screen", SCREEN_INDEX);
		toGame = new Button(Main.getWindowWidth() - 200,
				Main.getWindowHeight() - 100, this, "to game");
		toMainMenu = new Button(Main.getWindowWidth() - 200,
				Main.getWindowHeight() - 50, this, "to main menu");
		box = new TextBox(Main.getWindowWidth() - 200,
				Main.getWindowHeight() - 200, "<enter something>", this);
		LinkedList<String> items = new LinkedList<String>();
		items.add("foo");
		items.add("bar");
		items.add("fooiey");
		items.add("hello");
		items.add("bye");
		items.add("hello again");
		items.add("good bye");
		list = new List(Main.getWindowWidth() - 200, 50, 150, 200, this, items);
		
		toGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
//				if(toGame.getBounds().contains(e.getPoint())) {
					onLeavingScreen(this, GameScreen.SCREEN_INDEX);
//				}
			}
		});
		
		toMainMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
//				if(toMainMenu.getBounds().contains(e.getPoint())) {
					onLeavingScreen(this, MainMenuScreen.SCREEN_INDEX);
//				}
			}
		});
		
		
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		g.setColor(backgroundColor);
		g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
		
		box.draw(g);
		list.draw(g);
		toGame.draw(g);
		toMainMenu.draw(g);
	}

	@Override
	public void mouseMoved(MouseEvent e) {super.mouseMoved(e);}
	@Override
	public void mousePressed(MouseEvent e) {super.mousePressed(e);}
	@Override
	public void mouseReleased(MouseEvent e) {super.mouseReleased(e);}

	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		if(box.getBounds().contains(Screen.getMousePosition())) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				box.declineInput();
			} else {
				box.enterText(e);
			}
		}
	}
}
