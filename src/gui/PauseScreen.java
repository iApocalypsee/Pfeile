package gui;

import general.Main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import comp.Button;
import comp.List;
import comp.TextBox;

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
		list = new List(Main.getWindowWidth() - 200, 20, 200, this, items);
		
		box.makeChildrenOf(toGame);
		
		toGame.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				onLeavingScreen(this, GameScreen.SCREEN_INDEX);
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
		toMainMenu.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				onLeavingScreen(this, MainMenuScreen.SCREEN_INDEX);
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
		
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		GameScreen.getInstance().getWorld().draw(g);
		g.setColor(backgroundColor);
		g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
		
		box.draw(g);
		list.draw(g);
		toGame.draw(g);
		toMainMenu.draw(g);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		
	}

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
