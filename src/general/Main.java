package general;

import static general.Mechanics.currentTurn;
import static general.Mechanics.isTurnEnd;
import static general.Mechanics.turnsPerRound;
import gui.ArrowSelection;
import gui.GameScreen;
import gui.PreWindow;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.Properties;
import java.util.Random;

import player.Bot;
import player.Life;
import player.Player;

/**
 * Hauptklasse mit der Main-Methode und den abstraktesten Objekten unseres Spiels.
 * <p>27.02.2014</p>
 * <ul>
 *     <li>Updater hinzugefügt. Der Updater stellt sicher, dass die Daten von Objekten geupdated werden.</li>
 * </ul>
 * <p>1.3.2014</p>
 * <ul>
 *     <li>Zeichenprozess entschlackt. Vorher waren einfach zu viele while(true)-Schleifen drin.</li>
 * </ul>
 * @version 1.3.2014
 *
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
	 * Spawnt die Spieler. 
	 */
	private final Thread worldgenThread = new Thread(new Runnable() {

		/**
		 * Initialisiert die Felder.
		 * 
		 * Spawnt den Spieler und den Bot
		 */
		@Override
		public void run() {
			synchronized (this) { 
				Random r = new Random(); 
				GameScreen s = GameScreen.getInstance();
				// Spawn 
				if(s.getWorld() == null) {
					World newone = WorldFactory.generateDefault(Mechanics.worldSizeX, Mechanics.worldSizeY);
					s.setWorld(newone);
					
					SpawnEntityInstanceArgs e = new SpawnEntityInstanceArgs();
					int x = 0, y = 0; 
					// Kein Randfeld fuer x oder y
					do {
						x = r.nextInt(s.getWorld().getSizeX() - 2) + 1; 
						y = r.nextInt(s.getWorld().getSizeY() - 2) + 1;
					} while (isSpawnPossible(x, y, s) == false);
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
					} while (isSpawnPossible(x, y, s) == false);
					bot_e.setSpawnX(x);
					bot_e.setSpawnY(y);
					bot_e.setWorld(s.getWorld());
					s.getWorld().addPlayer(new Bot("Dummie", bot_e));

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
		System.out.println("DEBUG INFORMATION" + "\n");
		
		if(System.getProperty("os.name").equalsIgnoreCase("Linux")) {
			System.setProperty("java.awt.headless", "false");
		}
		System.out.println("AWT headless? " + System.getProperty("java.awt.headless"));
		
		Properties sys_props = System.getProperties();
		
		System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
		System.out.println("Total memory in JVM: " + Runtime.getRuntime().totalMemory() + " bytes");
		System.out.println("Free memory: " + Runtime.getRuntime().freeMemory() + " bytes");
		System.out.println("Java version: " + sys_props.getProperty("java.version"));
		System.out.println("OS: " + sys_props.getProperty("os.name") + "; version " + sys_props.getProperty("os.version"));
		System.out.println("OS architecture: " + sys_props.getProperty("os.arch"));
		System.out.println("----------------------------------" + "\n");

	}

	// METHODEN ################################################################

	// Hier laeuft das Spiel nach allen Insizialisierungen 
	private void runGame() {

		// assign the last frame time
		lastFrame = System.currentTimeMillis();
		
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
				

				// alle Bedingungen, mit denen eine Runde enden kann
				if (lifePlayer.getLife() <= 0 || lifeKI.getLife() <= 0) 
					break;
				if (timeObj.getMilliDeath() < 0) 
					break;
				if ((turnsPerRound - currentTurn) <= 0) 
					break;
			}

			// alle Bedingen, bei denen das Spiel endet
			if (lifePlayer.getLife() <= 0 || lifeKI.getLife() <= 0) 
				break;
			if (timeObj.getMilliDeath() < 0) 
				break;

			// ++++++++++++++++++++++++++++++++
			// BELEOHNUNGSYSTEM
			// TODO : Das kommplette Belohunungssystem
		}
	}

	/**
	 * ruft die eigentlichen Schleifen auf, je nachdem ob man gewonnen /
	 * Gewonnen: doEndSequenceWonLoop (Aktuell 1000 MilliSekunden lang);
	 *  Verloren: doEndSequenceDiedLoop (Aktuell 1000 MilliSekunden lang)
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
	 * 
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

		arrowSelectionWindow.dispose();
	}
	
	// Fuegt die Pfeile in das Inventar des Spielers ein 
	private void doArrowSelectionAddingArrows() {
		GameScreen s = GameScreen.getInstance();

		for (String selectedArrow : arrowSelectionWindow.selectedArrows) {
		   s.getWorld().getPlayerByIndex(
				   GameScreen.getInstance().getWorld().getActivePlayer().getIndex())
				   .getInventory().addItem(
						getArrowSelection().checkString(selectedArrow));
		}
		
		input = ArrowSelection.convert(arrowSelectionWindow.selectedArrows);
	}

	/** Kontroliert, ob der Spieler / Bot auf jenem Feld spawnen kann */
	private boolean isSpawnPossible(int x, int y, GameScreen s) {
		// Kann das Feld auf dem der Spieler / Bot spawnen soll betretten werden? 
		if (s.getWorld().getFieldAt(x, y).isAccessible() == true) {
			// Kann der Spieler / Bot auf minderstens einem Feld weitergehen?
			if (s.getWorld().getFieldAt(x-1, y).isAccessible() == true 
					|| s.getWorld().getFieldAt(x, y-1).isAccessible() == true 
					|| s.getWorld().getFieldAt(x+1, y).isAccessible() == true 
					|| s.getWorld().getFieldAt(x, y+1).isAccessible() == true) {
				
				// Spieler und Bot dürfen nicht auf dem selben Feld stehen
				if (s.getWorld().getActivePlayer() != null) {
					if (s.getWorld().getActivePlayer().getBoardX() == x && 
							s.getWorld().getActivePlayer().getBoardY() == y) {
						return false;
					} else 
						return true;
				} else 
					// Wenn kein Spieler spieler da ist, dann muss es auch true sein
					return true; 
			} else 
				return false; 
		} else 
			return false;
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

	/** Leitet die Main: ruft sowohl die Init-Methoden auf, als auch die Schleifen, die das Spiel laufen lassen */
	private void foo() {
		
		gameWindow = new GameWindow(graphicsDevice, timeObj);
		gameWindow.initializeScreens();

		int initHeight = 370;
		int initWidth = 620;
		
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
		// startet den Weltgenerierungsthread
		worldgenThread.setName("worldgen");
		worldgenThread.setDaemon(true);
		worldgenThread.start();
		
		arrowSelectionWindow = new ArrowSelection(initWidth, initHeight);
		doArrowSelectionLoop();
		
		// Initialisierungen
		initClasses();
		
		/*
		 * Da die Spielmechanik initialisiert wurde, kann auch der letzte Teil
		 * der Feldinitialisierung ausgeführt werden.
		 */
		synchronized (worldgenThread) {
			worldgenThread.notify();
		}
		
		// Feldgrößenanpassung
		GameScreen.getInstance().getWorld().updateWorldSizeAtBeginning();		
		
		doArrowSelectionAddingArrows();
				
		environmentG = GraphicsEnvironment.getLocalGraphicsEnvironment();
		graphicsDevice = environmentG.getDefaultScreenDevice();
		
		initGameWindow();
		
		stopwatchThread.start();

		runGame();

		endSequenceLoop();

	}

	/** Initialiert die Klassen */
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

	/** Initialisiert GameWindow */
	private void initGameWindow() {

		// instanziert die Welt und damit den Player

		// Doppelte Instanzierung von GameWindow!
		// gameWindow = new GameWindow(graphicsDevice, timeObj);
		GameWindow.createWindow(Main.getWindowWidth(), Main.getWindowHeight(),
				gameWindow);

		graphicsDevice.setFullScreenWindow(gameWindow);

		gameWindow.createBufferStrategy();

	}

	/** Hauptloop: hier läuft das eigentliche Spiel */
	private void render() {

		// calculate delta time
		long thisFrame = System.currentTimeMillis();
		timeSinceLastFrame = thisFrame - lastFrame;
		lastFrame = thisFrame;

		lifePlayer.updateLife();
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

	private static Rectangle windowDimensions = new Rectangle (0, 0, Main.getWindowWidth(), Main.getWindowHeight()); 
	
	public static Rectangle getWindowDimensions () {
		return windowDimensions;
	}
}
