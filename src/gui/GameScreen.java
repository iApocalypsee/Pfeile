package gui;

import comp.Button;
import general.Main;
import newent.Player;
import player.weapon.AttackDrawer;
import scala.runtime.AbstractFunction0;
import scala.runtime.BoxedUnit;
import world.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

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
    private final AttackDrawer attackDrawer = new AttackDrawer();

    public AttackDrawer getAttackDrawer () {
        return attackDrawer;
    }

    private LifeUI lifeUI = null;
	// I need lazy initialization of lifeUI, else it will throw a null pointer exception.
	private LifeUI getLifeUI() {
		if(lifeUI == null) {
			synchronized(this) {
				lifeUI = new LifeUI(Main.getWindowWidth() - 200, Main.getWindowHeight() - 150, Main.getContext().getActivePlayer().life());
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

        // spawning
        Point spawnPoint = null;
        Point spawnPointEnemy = null;
        java.util.Random randomGen = new Random();

        boolean isSpawnValid = false;
        do {
            TileLike tile = (TileLike) terrain.tileAt(randomGen.nextInt(terrain.width()), randomGen.nextInt(terrain.height()));
            if(spawnPoint == null && tile instanceof GrassTile) {
                spawnPoint = new Point(tile.latticeX(), tile.latticeY());
            }
            tile = (TileLike) terrain.tileAt(randomGen.nextInt(terrain.width()), randomGen.nextInt(terrain.height()));
            if(spawnPoint != null && tile instanceof GrassTile) {
                if ((spawnPoint.x > tile.latticeX() + 2 || spawnPoint.x < tile.latticeX() - 2) && (spawnPoint.y > tile.latticeY() + 2 || spawnPoint.y < tile.latticeY() - 2)) {
                    spawnPointEnemy = new Point(tile.latticeX(), tile.latticeY());
                    isSpawnValid = true;
                }
            }
        } while (!isSpawnValid);

		final Player act = new Player(Main.getContext().getWorld(), spawnPoint, Main.getUser().getUsername());
		final Player opponent = new Player(Main.getContext().getWorld(), spawnPointEnemy, "Opponent");

		act.onTurnGet().register(new AbstractFunction0<BoxedUnit>() {
			@Override
			public BoxedUnit apply() {
				Main.getContext().setActivePlayer(act);
				return BoxedUnit.UNIT;
			}
		});

		opponent.onTurnGet().register(new AbstractFunction0<BoxedUnit>() {
			@Override
			public BoxedUnit apply() {
				Main.getContext().setActivePlayer(opponent);
				return BoxedUnit.UNIT;
			}
		});

		Main.getContext().setActivePlayer(act);
		Main.getContext().playerList().setTurnPlayer(act);

		// The population of the world has to be performed AFTER the generation/loading of the world.
		Main.getContext().world().entities().register(Main.getContext().getActivePlayer());
		Main.getContext().world().entities().register(opponent);

		map = new VisualMap(Main.getContext().getWorld());
		map.moveMap(120, 470);

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

		shootButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				onLeavingScreen(this, ArrowSelectionScreen.SCREEN_INDEX);
			}
		});

		toggleStopwatch.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
//				if(toggleStopwatch.getBounds().contains(e.getPoint())) {
				if(Main.getContext().getTimeClock().isRunning()) {
					Main.getContext().getTimeClock().stop();
				} else {
					Main.getContext().getTimeClock().start();
				}
				System.out.println("Toggled stopwatch to " + Main.getContext().getTimeClock().isRunning());
//				}
			}
		});
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		map.draw(g);
		// I practically do not need the visual entity to let the entities draw on to the screen
		// BUT I still need the component objects, which reside in the visual entity object.
		// Drawing of the entities is done in VisualMap
		//visualEntity.draw(g);
        attackDrawer.draw(g);
		// Zeichnet die Welt und den UserInterface, der den Player darstellt
		endTurnButton.draw(g);
		shootButton.draw(g);
		toggleStopwatch.draw(g);

		getLifeUI().draw(g);
		Main.getContext().getTimeClock().draw(g);
	}

	@Override
	public void keyDown(KeyEvent e) {
		super.keyDown(e);
		switch(e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				onLeavingScreen(this, PauseScreen.SCREEN_INDEX);
				break;
			case KeyEvent.VK_S:
				onLeavingScreen(this, ArrowSelectionScreen.SCREEN_INDEX);
				break;
			case KeyEvent.VK_E:
				Main.getContext().onTurnEnd().call();
				break;
			case KeyEvent.VK_PAGE_UP:
				map.zoom(1.05f);
				break;
			case KeyEvent.VK_PAGE_DOWN:
				map.zoom(0.95f);
				break;
            // FIXME: remove this later
            default:
                if (lifeUI.life().getLife() - 20 < 0)
                    lifeUI.life().setLife(0);
                else
                    lifeUI.life().setLife(lifeUI.life().getLife() - 20);
		}
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
}
