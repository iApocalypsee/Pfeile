package general;

import gui.ArrowSelection;
import gui.ArrowSelectionScreen;
import gui.GameScreen;
import gui.PreWindow;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import player.Bot;
import player.Life;
import player.Player;
import player.SpawnEntityInstanceArgs;

/**
 * Hauptklasse mit der Main-Methode und den abstraktesten Objekten unseres Spiels.
 * <p>27.02.2014</p>
 * <ul>
 * <li>Updater hinzugefügt. Der Updater stellt sicher, dass die Daten von Objekten geupdated werden.</li>
 * </ul>
 * <p>1.3.2014</p>
 * <ul>
 * <li>Zeichenprozess entschlackt. Vorher waren einfach zu viele while(true)-Schleifen drin.</li>
 * </ul>
 *
 * @version 1.3.2014
 */
public class Main {

	// NUR INTIALISIERUNG - WIE WERTE UND VARIABLEN ###############

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

	private long lastFrame;

	// Zeichenvariablen.
	private boolean running = true;

	/**
	 * GETTER: Fenstergröße
	 */
	public static int getWindowWidth() {
		// Fensterbreite: HDready - Einstellung
		return 1366;
	}

	/**
	 * GETTER: Fensterhöhe
	 */
	public static int getWindowHeight() {
		// Fensterhöhe: HDready - Einstellung
		return 768;
	}

	/**
	 * Returns the time in between two frames. The timeout is recalculated in every drawing process.
	 *
	 * @return The time in between two frames.
	 */
	public long getTimeSinceLastFrame() {
		return this.timeSinceLastFrame;
	}

	public String[] input;

	private long timeSinceLastFrame = 0;

	private static PreWindow settingWindow;
	private static ArrowSelection arrowSelectionWindow;
	private static GameWindow gameWindow;
	private GraphicsEnvironment environmentG;
	private GraphicsDevice graphicsDevice;

	public static TimeClock timeObj;
	public static Life lifePlayer;
	public static Life lifeKI;

	private static Thread stopwatchThread;
	private static Main main;

	// threading vars
	private static ExecutorService exec = Executors.newCachedThreadPool();
	private static Set<Future<?>> futures = new HashSet<Future<?>>(5);

	public static Thread getStopwatchThread() {
		return stopwatchThread;
	}

	/**
	 * Führt nicht die main-Funktion aus, sondern gibt eine Referenz auf das
	 * Main-Objekt zurück.
	 *
	 * @return Referenz auf das Main-Objekt. Von hier aus kann auf fast alles
	 * zugegriffen werden.
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
	}

	/**
	 * Prints all system properties to console.
	 */
	private void printSystemProperties() {

		if (System.getProperty("os.name").equalsIgnoreCase("Linux")) {
			System.setProperty("java.awt.headless", "false");
		}

		Thread sys_props_out_thread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("SYSTEM PROPERTIES");

				System.out.println("AWT headless? " + System.getProperty("java.awt.headless"));

				Properties sys_props = System.getProperties();

				System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
				System.out.println("Total memory in JVM: " + Runtime.getRuntime().totalMemory() + " bytes");
				System.out.println("Free memory: " + Runtime.getRuntime().freeMemory() + " bytes");
				System.out.println("Java version: " + sys_props.getProperty("java.version"));
				System.out.println("OS: " + sys_props.getProperty("os.name") + "; version " + sys_props.getProperty("os.version"));
				System.out.println("OS architecture: " + sys_props.getProperty("os.arch"));
				System.out.println("----------------------------------\n");
			}
		});
		sys_props_out_thread.start();

	}

	// METHODEN ################################################################

	// Hier laeuft das Spiel nach allen Insizialisierungen 
	private void runGame() {

		// assign the last frame time
		lastFrame = System.currentTimeMillis();

		while (running) {
			render();
		}
		
		/*
		 * Mit einem Threadsystem ist es unmöglich, diese dreifache while(true)-Schleife
		 * aufrechtzuerhalten!!
		while (true) {
			// ++++++++++++++++++++++++++++++++++
			// AUFRUF DER PFEILAUSWAHL
			// TODO

			// +++++++++++++++++++++++++++++++++++
			// RUNDE
			while (true) {
				timeObj.start();
				
				// +++++++++++++++++++++++++++++++
				// ZUG
				while(true){
					render();
					
					// alle Bedingunen, bei denen ein Zug enden kann
					if (timeObj.getMilliDeath() < 0) 
						break;
					if (isTurnEnd == true) 
						break;
				}

				timeObj.stop();
				timeObj.reset();
				
				// ++++SCHADENSBERECHNUNGEN++++
				// TODO: alle Schadensberechungen, nach Endwe eines Zuges müssen hier rein
				lifePlayer.updateLife();
				
				// alle Bedingungen, mit denen eine Runde enden kann
				if (lifePlayer.getLife() <= 0 || lifeKI.getLife() <= 0) 
					break;
				if (timeObj.getMilliDeath() < 0) 
					break;
				if ((turnsPerRound - currentTurn) <= 0) 
					break;
			}
			// TODO Player und Leben richtig aufrufen
			
			// alle Bedingen, bei denen das Spiel endet
			if (lifePlayer.getLife() <= 0 || lifeKI.getLife() <= 0) 
				break;
			if (timeObj.getMilliDeath() < 0) 
				break;

			// ++++++++++++++++++++++++++++++++
			// BELEOHNUNGSYSTEM
			// TODO : Das kommplette Belohunungssystem
		}
		*/
	}

	/**
	 * ruft die eigentlichen Schleifen auf, je nachdem ob man gewonnen /
	 * Gewonnen: doEndSequenceWonLoop (Aktuell 1000 MilliSekunden lang);
	 * Verloren: doEndSequenceDiedLoop (Aktuell 1000 MilliSekunden lang)
	 */
	private void endSequenceLoop() {
		if (lifePlayer.getLife() <= 0 || timeObj.getMilliDeath() < 0) {
			// Hier einstellen, wie lange Die End-Sequenz Schleife laufen soll
			doEndSequenceDiedLoop(1000);
		} else if (lifeKI.getLife() <= 0) {
			// Hier einstellen, wie lange die End-Sequenz Schleife laufen soll
			doEndSequenceWonLoop(1000);
		}
	}

	/**
	 * Berechnung der Endsequence: Gewonnen Aufruf durch: endSequenceLoop()
	 * milliSec: Die Zeit in Millisekunden, bis er automaisch terminiert
	 */
	private void doEndSequenceWonLoop(long milliSec) {
		long endSequenceWonStartTime = System.currentTimeMillis();
		do {
			gameWindow.endSequenceWon();

			Keys.updateKeys();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (endSequenceWonStartTime <= System.currentTimeMillis() + milliSec);
	}

	/**
	 * Berechnung der Endsequence: Verloren Aufruf durch: endSequenceLoop()
	 */
	private void doEndSequenceDiedLoop(long milliSecDied) {

		long startTimeEndSequence = System.currentTimeMillis();

		do {
			gameWindow.endSequenceDied();

			Keys.updateKeys();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (startTimeEndSequence <= milliSecDied + System.currentTimeMillis());
	}

	// Fuegt die Pfeile in das Inventar des Spielers ein 
	private void doArrowSelectionAddingArrows() {
		GameScreen s = GameScreen.getInstance();

		for (String selectedArrow : arrowSelectionWindow.selectedArrows) {
			s.getWorld().getActivePlayer().getInventory().addItem(
					getArrowSelection().checkString(selectedArrow));
		}

		input = ArrowSelection.convert(arrowSelectionWindow.selectedArrows);

	}

	/**
	 * GETTER: PreWindow
	 */
	public PreWindow getPreWindow() {
		return this.settingWindow;
	}

	/**
	 * GETTER: GameWindow
	 */
	public static GameWindow getGameWindow() {
		return gameWindow;
	}

	public static ArrowSelection getArrowSelection() {
		return arrowSelectionWindow;
	}

	/**
	 * GETTER: GraphicsEnviroment
	 */
	public GraphicsEnvironment getGraphicsEnvironment() {
		return this.environmentG;
	}

	/**
	 * GETTER: GraphicsDevice
	 */
	public GraphicsDevice getGraphicsDevice() {
		return this.graphicsDevice;
	}

	/**
	 * Main-Method öffnet eine neue Instanz von Main: main
	 */
	public static void main(String[] arguments) {

		Mechanics.setUsername("Just a user");

		main = new Main();
		main.printSystemProperties();
		main.initPreWindow();
		main.initArrowSelectionWindow();
		main.initGameWindow();

		synchronized (main) {
			try {
				main.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		stopwatchThread.start();

		// Starten wir das Spiel
		main.runGame();

		/*
		 * TODO Hier kommt cleanup-code, z.B. noch offene Dateien schließen.
		 * Ich weis, noch nichts zum Aufräumen hinterher, aber wir werden es später
		 * 100% brauchen.
		 */

		// sanftes Schließen des GameWindows anstelle des harten System.exit(0)
		gameWindow.dispose();
		// und dann trotzdem HARTES BEENDEN!!! :D
		System.exit(0);
	}

	private void initArrowSelectionWindow() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				int width = 620, height = 370;
				arrowSelectionWindow = new ArrowSelection(width, height);
				arrowSelectionWindow.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {

						// add the arrows actually to the inventory
						doArrowSelectionAddingArrows();

						// window showing process
						gameWindow.setVisible(true);
						gameWindow.createBufferStrategy();

						timeObj.initNewPosition();

						synchronized (main) {
							main.notify();
						}
					}
				});
			}
		});
		thread.start();
	}

	private Callable<World> worldGen = new Callable<World>() {
		@Override
		public World call() throws Exception {
			return WorldFactory.generateDefault(Mechanics.worldSizeX, Mechanics.worldSizeY);
		}
	};

	/**
	 * Generates the world.
	 */
	private void generateWorld() {
		// a callable object has to be used, as the world cannot be saved instantly to
		// a GameScreen object
		futures.add(exec.submit(worldGen));
	}

	/**
	 * Initializes PreWindow.
	 */
	private void initPreWindow() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				int width = 620, height = 370;
				settingWindow = new PreWindow(width, height, 0, "Einstellungen");
				settingWindow.addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosed(WindowEvent e) {
						arrowSelectionWindow.setVisible(true);
						generateWorld();
						main.populateWorld();
						ArrowSelectionScreen.getInstance().init();
					}

				});
			}
		});
		thread.start();
	}

	/**
	 * Initialiert die Klassen
	 */
	private void initClasses() {

		// Feldinitialisierung in einen separaten Thread verschoben...

		lifePlayer = new Life(null);
		lifeKI = new Life(null);

		/* Instanziert 'timeClock' */
		timeObj = new TimeClock();
		
		/* TimeClock wird zu Thread */
		stopwatchThread = new Thread(timeObj);
		stopwatchThread.setDaemon(true);

	}

	/**
	 * Initialisiert GameWindow
	 */
	private void initGameWindow() {

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				environmentG = GraphicsEnvironment.getLocalGraphicsEnvironment();
				graphicsDevice = environmentG.getDefaultScreenDevice();

				gameWindow = new GameWindow();
				gameWindow.initializeScreens();
				// TODO dieser Methodenaufruf müsste in einen anderen Thread, glaube ich
				// so funktioniert es aber auch...
				main.postInitScreens();
				main.initClasses();

				GameWindow.adjustWindow(Main.getWindowWidth(), Main.getWindowHeight(),
						gameWindow);
				toggleFullscreen(true);
				gameWindow.setVisible(false);
			}
		});
		thread.start();

	}

	/**
	 * Neueste Änderungen an der Erstellung von Spielern unten bei den r.nextInt() Aufrufen.
	 * r.nextInt(s.getWorld().getSizeX() - 2) + 1 wirft die ganze Zeit Exceptions.
	 */
	private void populateWorld() {
		Future<?>[] threads = futures.toArray(new Future<?>[]{});
		World w = null;
		try {
			synchronized (this) {
				wait(1000);
			}
			w = (World) threads[0].get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		if (w != null) {

			Random r = new Random();
			SpawnEntityInstanceArgs e = new SpawnEntityInstanceArgs();
			GameScreen s = GameScreen.getInstance();
			s.setWorld(w);

			int x = 0, y = 0;
			// Kein Randfeld fuer x oder y
			do {
				x = r.nextInt(s.getWorld().getSizeX());
				y = r.nextInt(s.getWorld().getSizeY());
			} while (!WorldFactory.isSpawnPossible(x, y));
			e.setSpawnX(x);
			e.setSpawnY(y);
			e.setWorld(s.getWorld());
			Player p = new Player(Mechanics.getUsername(), e);
			s.getWorld().addPlayer(p);
			s.getWorld().setActivePlayer(p);
			s.getWorld().setTurnPlayer(p);

			SpawnEntityInstanceArgs bot_e = new SpawnEntityInstanceArgs();

			do {
				x = r.nextInt(s.getWorld().getSizeX() - 2) + 1;
				y = r.nextInt(s.getWorld().getSizeY() - 2) + 1;
			} while (!WorldFactory.isSpawnPossible(x, y));
			bot_e.setSpawnX(x);
			bot_e.setSpawnY(y);
			bot_e.setWorld(s.getWorld());
			s.getWorld().addPlayer(new Bot("Dummie", bot_e));

			GameScreen.getInstance().getWorld().updateWorldSizeAtBeginning();

		}
	}

	protected void postInitScreens() {

	}

	/**
	 * Hauptloop: hier läuft das eigentliche Spiel
	 */
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
	 *
	 * @param fps Die neu erwünschte FPS-Anzahl.
	 * @throws IllegalArgumentException wenn die angegebene Rate unter 0 liegt.
	 */
	public void setFPS(int fps) {
		if (fps <= 0) {
			throw new IllegalArgumentException("Negative FPS not permitted.");
		}
		this.setFPS = fps;
		recalculateTimeout();
	}

	/**
	 * Gibt die vom Spiel gerade erzielten FPS zurück.
	 *
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

	private static Rectangle windowDimensions = new Rectangle(0, 0, Main.getWindowWidth(), Main.getWindowHeight());

	public static Rectangle getWindowDimensions() {
		return windowDimensions;
	}

	public boolean isRunning() {
		return running;
	}

	void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * Toggles between fullscreen mode and windowed mode.
	 *
	 * @param fullscreen The fullscreen flag.
	 */
	void toggleFullscreen(boolean fullscreen) {
		if (fullscreen) {
			if (graphicsDevice.getFullScreenWindow() == gameWindow) {
				System.out.println("Already fullscreen.");
				return;
			}
			graphicsDevice.setFullScreenWindow(gameWindow);
		} else {
			if (graphicsDevice.getFullScreenWindow() != gameWindow) {
				System.out.println("Already windowed.");
				return;
			}
			graphicsDevice.setFullScreenWindow(null);
		}
	}
}
