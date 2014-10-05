package gui;

import comp.Button;
import general.Keys;
import general.Main;
import newent.*;
import world.*;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/**
 * <b>4.1.2014 (Josip):</b> Konstruktor braucht keine ScreenManager-Instanz mehr. <br><br>
 * <b>20.1.2014:</b> GameScreen wurde an {@link Screen}-Update 20.1.2014 angepasst.
 * <b>25.1.2014:</b> Singleton pattern an die Subklassen von Screen angewendet -> Einfachere Anwendung
 * von Screen-Klassen. Um auf das GameScreen-Objekt zuzugreifen, muss {@link #getInstance()} aufgerufen
 * werden.
 * 
 * 
 * @version 20.1.2014
 * @see ScreenManager
 */
public class GameScreen extends Screen {
	
	private static GameScreen instance = null;
	
	/**
	 * Singleton-Methode.
	 * @return The object.
	 */
	public static GameScreen getInstance() {
		if(instance == null) {
			instance = new GameScreen();
			instance.postInit();
		}
		return instance;
	}

	public static final String SCREEN_NAME = "Game screen";
	public static final int SCREEN_INDEX = 1;

	private Button endTurnButton;
	private Button shootButton;
	private Button toggleStopwatch;

	private VisualMap map;
	private VisualEntity visualEntity;

	private LifeUI lifeUI = null;
	// I need lazy initialization of lifeUI, else it will throw a null pointer exception.
	private LifeUI getLifeUI() {
		if(lifeUI == null) {
			synchronized(this) {
				if(lifeUI == null) {
					lifeUI = new LifeUI(Main.getWindowWidth() - 200, Main.getWindowHeight() - 150, Main.getContext().getActivePlayer().life());
				}
			}
		}
		return lifeUI;
	}

	/**
	 * Die Welt, die vom GameScreen gezeichnet wird.
	 */
	private GameScreen() {
		super(GameScreen.SCREEN_NAME, GameScreen.SCREEN_INDEX);
	}

	/** This method must be called just once!
	 *
	 * With postInit() I am avoiding problems that the GameScreen.getInstance() reference is still null.
	 * I know, complicated to explain, but every time I call GameScreen.getInstance() in the GameScreen constructor,
	 * getInstance() returns null. Why? Because the GameScreen instance has not been fully constructed yet.
	 * So construct it first, then do all the initialization afterwards.
	 */
	private void postInit() {
		Main.getContext().setWorld(new DefaultWorld());
		TerrainLike terrain = Main.getContext().getWorld().terrain();

		// Comment that out later.
		// Generating Component objects for every tile...
		List<TileComponentWrapper> compWrappers = new ArrayList<TileComponentWrapper>(terrain.width() * terrain.height());
		for (int x = 0; x < terrain.width(); x++) {
			for (int y = 0; y < terrain.height(); y++) {
				compWrappers.add(TileComponentWrapper.tile2ComponentTile((TileLike) terrain.tileAt(x, y)));
			}
		}

		visualEntity = new VisualEntity(new LinkedList<EntityComponentWrapper>());

		Main.getContext().setActivePlayer(new Player(Main.getContext().getWorld(), new Point(0, 0), Main.getUser().getUsername()));

		// The population of the world has to be performed AFTER the generation/loading of the world.
		Main.getContext().world().entities().register(Main.getContext().getActivePlayer());

		map = new VisualMap(compWrappers);
		map.moveMap(100, 300);

		// später DAS HIER auskommentieren
		ScreenManager.ref_gameScreen = this;

		// Initialisierung der Buttons
		endTurnButton = new Button(30, Main.getWindowHeight() - 50, this,
				"End turn");
		shootButton = new Button(endTurnButton.getX()
				+ endTurnButton.getWidth() + 20, Main.getWindowHeight() - 50,
				this, "Shoot");
		toggleStopwatch = new Button(endTurnButton.getX() + endTurnButton.getWidth()
				+ shootButton.getX() + shootButton.getWidth() + 20,
				Main.getWindowHeight() - 50, this, "Stopwatch");

		endTurnButton.setName("End turn button");
		shootButton.setName("Shoot button");
		toggleStopwatch.setName("Stopwatch button");


		endTurnButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				Main.getContext().onTurnEnd().call();
			}

		});

		shootButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				onLeavingScreen(this, ArrowSelectionScreen.SCREEN_INDEX);
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

		toggleStopwatch.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
//				if(toggleStopwatch.getBounds().contains(e.getPoint())) {
				if(Main.timeObj.isRunning()) {
					Main.timeObj.stop();
				} else {
					Main.timeObj.start();
				}
				System.out.println("Toggled stopwatch to " + Main.timeObj.isRunning());
//				}
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
		map.draw(g);
		visualEntity.draw(g);
		// Zeichnet die Welt und den UserInterface, der den Player darstellt
		endTurnButton.draw(g);
		shootButton.draw(g);
		toggleStopwatch.draw(g);

		getLifeUI().draw(g);
	}

	@Override
	public void keyDown(KeyEvent e) {
		super.keyDown(e);
		if (Keys.isKeyPressed(KeyEvent.VK_LEFT)) {
			onLeavingScreen(this, MainMenuScreen.SCREEN_INDEX);
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			onLeavingScreen(this, PauseScreen.SCREEN_INDEX);
		}
        /*
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
//			onLeavingScreen(this, ArrowSelectionScreen.SCREEN_INDEX);
		}
		*/
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
    public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);
    }

    public void lockUI() {
		endTurnButton.declineInput();
		shootButton.declineInput();
		toggleStopwatch.declineInput();
	}
	
	public void releaseUI() {
		endTurnButton.acceptInput();
		shootButton.acceptInput();
		toggleStopwatch.acceptInput();
	}

	public VisualMap getMap() {
		return map;
	}

    public VisualEntity getVisualEntity() {
        return visualEntity;
    }
}
