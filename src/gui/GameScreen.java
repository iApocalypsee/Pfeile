package gui;

import general.Keys;
import general.Main;
import general.World;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;

import comp.Button;

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
		}
		return instance;
	}

	public static final String SCREEN_NAME = "Game screen";
	public static final int SCREEN_INDEX = 1;

	private Button endTurnButton;
	private Button shootButton;
	private Button toggleStopwatch;

	/**
	 * Die Welt, die vom GameScreen gezeichnet wird.
	 */
	private volatile general.World loadedWorld = null; // FIXME FIXME FIXME VOLATILE!!!

	private GameScreen() {
		super(GameScreen.SCREEN_NAME, GameScreen.SCREEN_INDEX);
		
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
		
		
		endTurnButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
                World w = GameScreen.getInstance().getWorld();
				if(w.getTurnPlayer() == w.getActivePlayer()) {
					w.getActivePlayer().endTurn();
				}
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
		
		shootButton.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
                World w = GameScreen.getInstance().getWorld();
				if(w.getTurnPlayer() == w.getActivePlayer()) {
					// negiert den wert
					w.getTurnPlayer().setAttemptingShoot(!w.getTurnPlayer().isAttemptingShoot());
				}
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
		// Zeichnet die Welt und den UserInterface, der den Player darstellt
		loadedWorld.draw(g);
		endTurnButton.draw(g);
		shootButton.draw(g);
		toggleStopwatch.draw(g);
		Main.timeObj.draw(g);
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
        // getWheelRotation(): negative, if away; positive, if towards
        loadedWorld.getViewport().zoomRel((float) (1.0f * Math.pow(1.2f, -e.getPreciseWheelRotation())));
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

    public synchronized World getWorld() {
        return loadedWorld;
    }

    public synchronized void setWorld(World world) {
        this.loadedWorld = world;
    }
}
