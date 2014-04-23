package general;

import gui.ArrowSelection;
import gui.GameScreen;
import gui.PreWindow;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

import player.Bot;
import player.EntityAttributes;
import player.Life;
import player.Player;

/**
 * Hauptklasse mit der Main-Methode und den abstraktesten Objekten unseres Spiels.
 * <p>3.3.2014</p>
 * <ul>
 *     <li>Logger hinzugefügt. Nachrichten sollten in den Logger geschrieben werden und nicht
 *     mehr wie gehabt auf die Konsole.</li>
 *     <li>SpawnEntityInstanceArgs rausgenommen. Ich weiß wirklich nicht, was mich da geritten hat
 *     bei dieser Klasse...</li>
 * </ul>
 * @version 3.3.2014
 *
 */
public class Main {

	/**
	 * Die derzeitige Anzahl an FPS, die das Spiel gerade erzielt.
	 */
	private volatile int fps;
	
	/**
	 * Die erwünschte FPS-Rate. Wenn <code>setFPS == 0</code>, ist die Framerate unbegrenzt.
	 * Unbegrenzte Framerate noch nicht implementiert!
	 */
	private volatile int setFPS = 35;
	
	/**
	 * Der Timeout, der in der <code>Thread.sleep()</code>-Methode als Argument verwendet wird.
	 */
	private int framerateTimeout = 1000 / setFPS;

	private static Rectangle windowDimensions = new Rectangle(0, 0, Main.getWindowWidth(), Main.getWindowHeight());

	public static Rectangle getWindowDimensions() {
		return windowDimensions;
	}

	private static Logger logger = Logger.getLogger("log");

	public static Logger getLogger() {
		return logger;
	}

	/**
	 * Is the game rendering images?
	 */
	private boolean rendering = true;
	private long lastFrame;

	/** GETTER: Fenstergröße */
	public static int getWindowWidth() {
		// Fensterbreite: HDready - Einstellung
		return 1366;
	}

	/** GETTER: Fensterhöhe */
	public static int getWindowHeight() {
		// Fensterhöhe: HDready - Einstellung
		return 768;
	}

	/**
	 * Returns the time in between two frames. The timeout is recalculated in every drawing process.
	 * @return The time in between two frames.
	 */
	public long getTimeSinceLastFrame() {
		return this.timeSinceLastFrame;
	}
	
	public Thread getWorldgenThread() {
		return worldgenThread;
	}

	public String[] input;

	private long timeSinceLastFrame = 0;

	private PreWindow settingWindow;
	private static ArrowSelection arrowSelectionWindow;
	private static GameWindow gameWindow;
	private GraphicsEnvironment environmentG;
	private GraphicsDevice graphicsDevice;

	/**
	 * Der Weltgenerierungsthread.
	 */
	private final Thread worldgenThread = new Thread(new Runnable() {

		/**
		 * Initialisiert die Felder.
		 */
		@Override
		public void run() {
			synchronized (this) {
				// Spawnprozess! Sollte später noch umgearbeitet werden
				Random r = new Random();
				GameScreen s = GameScreen.getInstance();

				if(s.getWorld() != null) {
					World newone = WorldFactory.generateDefault(Mechanics.worldSizeX, Mechanics.worldSizeY);
					s.setWorld(newone);
					s.getWorld().addPlayer(new Player(
							Mechanics.getUsername(),
							r.nextInt(s.getWorld().getSizeX()),
							r.nextInt(s.getWorld().getSizeY()),
							new EntityAttributes(
									100.0, 5.0, 3.0, 1)
					));

					s.getWorld().addPlayer(new Bot(
							"Dummie",
							r.nextInt(s.getWorld().getSizeX()),
							r.nextInt(s.getWorld().getSizeY()),
							new EntityAttributes(
									100.0, 5.0, 3.0, 1
							)
					));

				}

			}
		}

	});



	public static TimeClock timeObj;
	public Life lifePlayer;
	public Life lifeKI;

	private static Thread stopwatchThread;
	private static Main main;

	public static Thread getStopwatchThread() {
		return stopwatchThread;
	}

	/**
	 * Führt nicht die main-Funktion aus, sondern gibt eine Referenz auf das
	 * Main-Objekt zurück.
	 * 
	 * @return Referenz auf das Main-Objekt. Von hier aus kann auf fast alles
	 *         zugegriffen werden.
	 */
	public static Main getMain() {
		return main;
	}

	// KONSTRUKTOR ###############################################
	// HIER STEHEN DIE HAUPTAUFRUFE ##############################
	/**
	 * Der Konstruktor. Hier stehen keine Hauptaufrufe. Die Hauptaufrufe werden
	 * in <code>foo()</code> getätigt.
	 */
	private Main() {
		
		if(System.getProperty("os.name").equalsIgnoreCase("Linux")) {
			System.setProperty("java.awt.headless", "false");
		}
		System.out.println("AWT headless? " + System.getProperty("java.awt.headless"));
		
		Properties sys_props = System.getProperties();
		
		System.out.println("DEBUG INFORMATION");
		System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
		System.out.println("Total memory in JVM: " + Runtime.getRuntime().totalMemory() + " bytes");
		System.out.println("Java version: " + sys_props.getProperty("java.version"));
		System.out.println("OS: " + sys_props.getProperty("os.name") + "; version " + sys_props.getProperty("os.version"));
		System.out.println("OS architecture: " + sys_props.getProperty("os.arch") + "\n");

	}

	private void runGame() {

		timeObj.start();

		// assign the last frame time
		lastFrame = System.currentTimeMillis();
		
		while (rendering) {
			render();
		}

		/*
		while (true) {
			// ++++++++++++++++++++++++++++++++++
			// AUFRUF DER PFEILAUSWAHL

			// +++++++++++++++++++++++++++++++++++
			// RUNDE
			while (true) {
				render();
				if(lock) {
					lock = false;
					break;
				}
			}

			timeObj.stop();

			if (lifePlayer.getLife() <= 0 || lifeKI.getLife() <= 0) {
				break;
			}
			if (timeObj.getMilliDeath() < 0) {
				break;
			}
			if (turnsPerRound - currentTurn <= 0) {
				break;
			}
			if (isTurnEnd) {
				break;
			}

			// ++++++++++++++++++++++++++++++++
			// BELEOHNUNGSYSTEM

		}
		*/


	}

	/**
	 * ruft die eigentlichen Schleifen auf, je nachdem ob man gewonnen /
	 * verloren hat Gewonnen: doEndSequenceWonLoop Verloren:
	 * doEndSequenceDiedLoop
	 */
	private void endSequenceLoop() {
		if (lifePlayer.getLife() <= 0 || timeObj.getMilliDeath() < 0) {
			doEndSequenceDiedLoop();
		} else if (lifeKI.getLife() <= 0) {
			doEndSequenceWonLoop();
		}
	}

	/**
	 * Berechnung der Endsequence: Gewonnen Aufruf durch: endSequenceLoop()
	 * TODO While-Schleife kann nur durch Exception oder Terminieren des Programms verlassen werden.
	 */
	private void doEndSequenceWonLoop() {
		while (true) {
			gameWindow.endSequenceWon();

			Keys.updateKeys();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Berechnung der Endsequence: Verloren Aufruf durch: endSequenceLoop()
	 * // TODO While-Schleife kann nur durch Exception oder Terminieren des Programms verlassen werden.
	 */
	private void doEndSequenceDiedLoop() {
		while (true) {
			gameWindow.endSequenceDied();

			Keys.updateKeys();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Nachverarbeitung von ArrowSelection.
	 */
	private void doArrowSelectionLoop() {
		synchronized(getMain()) {
			try {
				Main.getMain().wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Spawnprozess! Sollte später noch umgearbeitet werden
		Random r = new Random();
		GameScreen s = GameScreen.getInstance();

		if(s.getWorld() == null) {
			World newone = WorldFactory.generateDefault(Mechanics.worldSizeX, Mechanics.worldSizeY);
			s.setWorld(newone);
			s.getWorld().addPlayer(new Player(
					Mechanics.getUsername(),
					r.nextInt(s.getWorld().getSizeX()),
					r.nextInt(s.getWorld().getSizeY()),
					new EntityAttributes(
							100.0, 5.0, 3.0, 1)
			));

			s.getWorld().addPlayer(new Bot(
					"Dummie",
					r.nextInt(s.getWorld().getSizeX()),
					r.nextInt(s.getWorld().getSizeY()),
					new EntityAttributes(
							100.0, 5.0, 3.0, 1
					)
			));

		}

		if(s.getWorld().getActivePlayer() == null) {
			System.out.println("Active player is null!");
		}

		for (String selectedArrow : arrowSelectionWindow.selectedArrows) {
		   s.getWorld().getPlayerByIndex(GameScreen.getInstance().getWorld().
				getActivePlayer().getIndex()).getInventory().addItem(
							getArrowSelection().
									checkString(
											selectedArrow));
		}

		arrowSelectionWindow.dispose();
	}

	/** GETTER: PreWindow */
	public PreWindow getPreWindow() {
		return this.settingWindow;
	}

	/** GETTER: GameWindow */
	public static GameWindow getGameWindow() {
		return gameWindow;
	}

	public static ArrowSelection getArrowSelection() {
		return arrowSelectionWindow;
	}

	/** GETTER: GraphicsEnviroment */
	public GraphicsEnvironment getGraphicsEnvironment() {
		return this.environmentG;
	}

	/** GETTER: GraphicsDevice */
	public GraphicsDevice getGraphicsDevice() {
		return this.graphicsDevice;
	}

	/** Main-Method öffnet eine neue Instanz von Main: main */
	public static void main(String[] arguments) {
		
		Mechanics.setUsername("Just a user");

		main = new Main();
		main.foo();

	}

	private void foo() {
		
		initGameWindow();

		// startet den Weltgenerierungsthread
		worldgenThread.setName("worldgen");
		worldgenThread.setDaemon(true);
		worldgenThread.start();

		int initHeight = 320;
		int initWidth = 580;

		// FIXME Daniel, bist du dir da ganz sicher, dass du width und height vertauschen willst?
		// Schau in die Parameter vom Konstruktor PreWindows....
		settingWindow = new PreWindow(initWidth, initHeight, 0, "Einstellungen");

		// Schleife nicht mehr nötig. Der wait()-Aufruf wird in PreWindows
		// ListenToButton durch notify() aufgelöst
		synchronized (getMain()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		arrowSelectionWindow = new ArrowSelection(initWidth, initHeight);
		doArrowSelectionLoop();
		environmentG = GraphicsEnvironment.getLocalGraphicsEnvironment();
		graphicsDevice = environmentG.getDefaultScreenDevice();

		// Initialisierungen
		initClasses();

		/*
		 * Da die Spielmechanik initialisiert wurde, kann auch der letzte Teil
		 * der Feldinitialisierung ausgeführt werden.
		 */
		synchronized (worldgenThread) {
			worldgenThread.notify();
		}
		
		Mechanics.worldSizeX++;
		Mechanics.worldSizeY--;
		
		stopwatchThread.start();

		runGame();

		endSequenceLoop();

	}

	/** Initialiert die Klassen */
	private void initClasses() {

		// Feldinitialisierung in einen separaten Thread verschoben...

		input = ArrowSelection.convert(arrowSelectionWindow.selectedArrows);

		/* Instanziert 'timeClock' */
		timeObj = new TimeClock();
		/* TimeClock wird zu Thread */
		stopwatchThread = new Thread(timeObj);
		stopwatchThread.setDaemon(true);

	}

	/** Initialisiert GameWindow */
	private void initGameWindow() {
		
		gameWindow = new GameWindow(graphicsDevice, timeObj);
		GameWindow.createWindow(Main.getWindowWidth(), Main.getWindowHeight(),
				gameWindow);
		gameWindow.initializeScreens();

		graphicsDevice.setFullScreenWindow(gameWindow);

		gameWindow.createBufferStrategy();

	}

	/** Hauptloop: hier läuft das eigentliche Spiel */
	private void render() {

		// calculate delta time
		long thisFrame = System.currentTimeMillis();
		timeSinceLastFrame = thisFrame - lastFrame;
		lastFrame = thisFrame;
		
		Keys.updateKeys();

		// Zeichnet die Objekte auf den Fullscreen
		gameWindow.update();

		try {
			Thread.sleep(framerateTimeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


	}
	
	/**
	 * Setzt die neue FPS-Rate.
	 * @param fps Die neu erwünschte FPS-Anzahl.
	 * @throws IllegalArgumentException wenn die angegebene Rate unter 0 liegt.
	 */
	public void setFPS(int fps) {
		if(fps < 0) {
			throw new IllegalArgumentException("Negative FPS not permitted.");
		}
		this.setFPS = fps;
		recalculateTimeout();
	}
	
	/**
	 * Gibt die vom Spiel gerade erzielten FPS zurück.
	 * @return Die vom Spiel gerade erzielten FPS.
	 */
	public int getFPS() {
		return fps;
	}
	
	/**
	 * Berechnet den {@link #framerateTimeout} neu.
	 */
	private void recalculateTimeout() {
		framerateTimeout = 1000 / setFPS;
	}


}
