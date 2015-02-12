package gui.screen;

import comp.Button;
import general.JavaInterop;
import general.Main;
import gui.FrameContainer;
import gui.FrameContainerObject;
import player.weapon.AttackDrawer;
import player.weapon.arrow.ImpactDrawerHandler;
import scala.runtime.BoxedUnit;
import world.VisualMap;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
public class GameScreen extends Screen implements FrameContainer {
	
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

	private VisualMap map = null;
    private final AttackDrawer attackDrawer = new AttackDrawer();

	private final FrameContainerObject frameContainerObject = new FrameContainerObject();

    public AttackDrawer getAttackDrawer () {
        return attackDrawer;
    }

	/**
	 * Die Welt, die vom GameScreen gezeichnet wird.
	 */
	private GameScreen() {
		super(GameScreen.SCREEN_NAME, GameScreen.SCREEN_INDEX);

		onScreenEnter.register(JavaInterop.asScalaFunctionSupplier(() -> {
			if(map == null) {
				map = new VisualMap(Main.getContext().getWorld());
				map.moveMap(120, 470);
			}
			return BoxedUnit.UNIT;
		}));
	}

	/** This method must be called just once!
	 *
	 * With postInit() I am avoiding problems that the GameScreen.getInstance() reference is still null.
	 * I know, complicated to explain, but every time I call GameScreen.getInstance() in the GameScreen constructor,
	 * getInstance() returns null. Why? Because the GameScreen instance has not been fully constructed yet.
	 * So construct it first, then do all the initialization afterwards.
	 */
	private void postInit() {

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
				Main.getContext().turnSystem().increment();
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
		if(map != null) {
			map.draw(g);
		}
		// I practically do not need the visual entity to let the entities draw on to the screen
		// BUT I still need the component objects, which reside in the visual entity object.
		// Drawing of the entities is done in VisualMap
		//visualEntity.draw(g);
        attackDrawer.draw(g);
        ImpactDrawerHandler.draw(g);
		// Zeichnet die Welt und den UserInterface, der den Player dargestellt
		endTurnButton.draw(g);
		shootButton.draw(g);
		toggleStopwatch.draw(g);
		Main.getContext().getActivePlayer().drawLifeUI(g);

		frameContainerObject.drawFrames(g);

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
				Main.getContext().turnSystem().increment();
				break;
			case KeyEvent.VK_PAGE_UP:
				map.zoom(1.05f);
				break;
			case KeyEvent.VK_PAGE_DOWN:
				map.zoom(0.95f);
				break;
            // FIXME: remove this later
            case KeyEvent.VK_SPACE:
                if (Main.getContext().getActivePlayer().life().getLife() - 20 < 0)
                    Main.getContext().getActivePlayer().life().setLife(0);
                else
                    Main.getContext().getActivePlayer().life().setLife(Main.getContext().getActivePlayer().life().getLife() - 20);
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

	/**
	 * The object managing the internal frames for the screen.
	 */
	@Override
	public FrameContainerObject frameContainer() {
		return frameContainerObject;
	}

	@Override
	public FrameContainerObject getFrameContainer() {
		return frameContainer();
	}
}
