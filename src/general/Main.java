package general;

import entity.path.Direction;
import gui.ArrowSelection;
import gui.ArrowSelectionScreen;
import gui.GameScreen;
import gui.NewWorldTestScreen;
import gui.PreWindow;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import player.Bot;
import player.Player;
import player.SpawnEntityInstanceArgs;
import scala.Tuple2;
import scala.collection.JavaConversions;
import world.BaseTile;
import world.EditableBaseTerrain;
import world.IBaseTile;
import world.IWorld;
import world.ScaleWorld;
import world.brush.HeightBrush;
import world.brush.SmoothHeightBrush;
import world.brush.TileTypeBrush;
import world.tile.GrassTile;
import world.tile.SeaTile;
import world.tile.package$;

/**
 * Hauptklasse mit der Main-Methode und den abstraktesten Objekten unseres Spiels.
 * <p>27.02.2014</p>
 * <ul>
 * <li>Updater hinzugefügt. Der Updater stellt sicher, dass die Daten von Objekten geupdated werden.</li>
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
	public int getTimeSinceLastFrame() {
		return this.timeSinceLastFrame;
	}

	public static int delta() {
		return main.timeSinceLastFrame;
	}

	private int timeSinceLastFrame = 0;

	private static PreWindow settingWindow;
	private static ArrowSelection arrowSelectionWindow;
	private static GameWindow gameWindow;
	private GraphicsEnvironment environmentG;
	private GraphicsDevice graphicsDevice;

	public static TimeClock timeObj;

	private static Thread stopwatchThread;
	private static Main main;

	// threading vars
	private static ExecutorService exec = Executors.newCachedThreadPool();
	private static Set<Future<?>> futures = new HashSet<Future<?>>(5);

	
	// DONE WITH ALL VARIABELS;
	// MOST IMPORTANT METHODS ####################################
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
	/**
	 * Der Konstruktor. Hier stehen keine Hauptaufrufe. Die Hauptaufrufe werden
	 * in <code>foo()</code> getätigt.
	 */
	private Main() {}
	
	// ###########################################################
	// HIER STEHEN DIE HAUPTAUFRUFE ##############################
	// ###########################################################
	
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

		main.disposeInitialResources();

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

	private void disposeInitialResources() {
		settingWindow = null;
		arrowSelectionWindow = null;
	}
	
	// ############ RUN GAME 
	/** Hier laeuft das Spiel nach allen Insizialisierungen */
	private void runGame() {

		// start TimeClock
		stopwatchThread.start();
		timeObj.start();

		GameLoop.run(1 / 60.0);
		
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
	
	// #########################################################
	// METHODEN: v. a. alle Threads ############################
	// #########################################################
	
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
						main.initTimeClock();
						main.newWorldTest();
						new player.weapon.ArrowHelper();
						ArrowSelectionScreen.getInstance().init();
					}
				});
			}
		});
		thread.start();
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

						synchronized (main) {
							main.notify();
						}
					}
				});
			}
		});
		thread.start();
	}
	
	/** Fuegt die Pfeile in das Inventar des Spielers ein */
	private void doArrowSelectionAddingArrows() {
		
		for (String selectedArrow : arrowSelectionWindow.selectedArrows) {
			if (((ScaleWorld) NewWorldTestScreen.getWorld()).getActivePlayer().getInventory().addItem (
					getArrowSelection().checkString(selectedArrow)) == false) {
				System.err.println("in Main: doArrowSelectionAddingArrows\n\t"
						+ "Der Pfeil + " + selectedArrow + " konnte nicht hinzugefügt werden."); 
			}
		}
		ArrowSelectionScreen.getInstance().updateInventoryList();
	}

	class NewWorldFoo {

		public Point p;
		public Color c;
		public int h;
		public Class<? extends BaseTile> tileType;

	}

	/* DEFAULT IMPLEMENTATION WITH NO PROBABILITIES
	private Color decide(LinkedList<Color> objects) {
		return objects.get(new Random().nextInt(objects.size()));
	}
	*/

	///*
	private Color decide(LinkedList<Color> objects) {
		Random r = new Random();
		double val = r.nextDouble();
		if(val < 0.4) {
			return objects.get(0);
		} else {
			return objects.get(1);
		}
	}
	//*/

	private Class<? extends BaseTile> decide(java.util.List<Class<? extends BaseTile>> objs) {
		return objs.get(new Random().nextInt(objs.size()));
	}

	public LinkedList<Integer> filterNotEvenNumbers(LinkedList<Integer> input) {
		LinkedList<Integer> result = new LinkedList<Integer>();
		for(Integer i : input) {
			if(i % 2 == 0) result.add(i);
		}
		return result;
	}

	public LinkedList<Integer> filterEvenNumbers(LinkedList<Integer> input) {
		LinkedList<Integer> result = new LinkedList<Integer>();
		for(Integer i : input) {
			if(i % 2 == 1) result.add(i);
		}
		return result;
	}

	/** FIXME: hier kann man eigentlich auch gleich den Punkt verwenden: LinkedList<Point> */
	private class SHBCoordinator {
		public Point p = null;
	}

	private void newWorldTest() {
		IWorld w = new ScaleWorld(30, 30);
		/*
		world.World w = new world.World(50, 50);
		*/
		//NewWorldTestScreen.setWorld(w);
		NewWorldTestScreen.setWorld(w);
		//EditableTerrain terrain = (EditableTerrain) w.getTerrain();
		EditableBaseTerrain terrain = (EditableBaseTerrain) w.getTerrain();
		Random r = new Random();

		LinkedList<NewWorldFoo> points = new LinkedList<NewWorldFoo>();
		LinkedList<SHBCoordinator> smoothCoordinators = new LinkedList<SHBCoordinator>();

		HeightBrush hb = new HeightBrush();
		TileTypeBrush tb = new TileTypeBrush(w);
		SmoothHeightBrush shb = new SmoothHeightBrush();

		entity.Player p = new entity.Player(r.nextInt(w.getSizeX() - 1), r.nextInt(w.getSizeY() - 1), Mechanics.getUsername());


		hb.setThickness(3);

		LinkedList<Color> colors = new LinkedList<Color>();
		colors.add(new Color(0x1C9618)); // GRÜN
		//colors.add(new Color(0xD5F5EF)); // EISWEIß
		//colors.add(new Color(0xEBEE31)); // GELB
		colors.add(new Color(0x6D75E8)); // BLAU
		//colors.add(new Color(0x646464)); // GRAU
		//colors.add(new Color(0x19D7E6));
		//colors.add(new Color(0xD37D3C));
		LinkedList<Class<? extends BaseTile>> baseTiles = new LinkedList<Class<? extends BaseTile>>();
		baseTiles.add(GrassTile.class);
		baseTiles.add(SeaTile.class);

		int amtOfPoints = r.nextInt(w.getSizeX() * w.getSizeY()) + w.getSizeX() * w.getSizeY();
		int maxHeightPerPaint = 3;
		for(int i = 0; i < amtOfPoints; i++) {
			NewWorldFoo f = new NewWorldFoo();
			f.p = new Point(r.nextInt(w.getSizeX()), r.nextInt(w.getSizeY()));
			f.c = decide(colors);
			//f.h = r.nextInt(maxHeightPerPaint) + 3;
			//f.h = r.nextInt(maxHeightPerPaint) + 2; // TODO Remember, if you want random heights, comment that in!!!!!!!!!!!!!
			f.h = maxHeightPerPaint;
			f.tileType = decide(baseTiles);
			points.add(f);
			//System.out.println("i = " + i);

			SHBCoordinator c = new SHBCoordinator();
			c.p = new Point(r.nextInt(w.getSizeX()), r.nextInt(w.getSizeY()));
			smoothCoordinators.add(c);
		}

		int count = r.nextInt(8) + 3;
		int i = 0;
		LinkedList<Point> tempPoints = new LinkedList<Point>();
		for(NewWorldFoo f : points) {
			tempPoints.add(f.p);
			if(i >= count) {
				// reset the counter variables
				i = 0;
				count = r.nextInt(8) + 3;

				// reset the brush and other stuff
				//cb.setColor(f.c);
				//cb.setThickness(r.nextInt(6) + 3);


				//hb.setThickness(r.nextInt(6) + 3);
				//hb.setHeightIncrement(f.h);


				tb.setTileClass(f.tileType);
				tb.setThickness(r.nextInt(4) + 2);

				/*
				terrain.edit(cb, tempPoints);
				*/

				//terrain.edit(hb, tempPoints);


				terrain.edit(tb, tempPoints);
				tempPoints.clear();
			}

			/*
			LinkedList<Point> p = new LinkedList<Point>();
			p.add(f.p);
			terrain.edit(cb, p);
			*/
			i++;
			//System.out.print("i = " + i);
			//System.out.println(" Main.newWorldTest");
		}

		tempPoints.clear();
		i = 0;

		for(NewWorldFoo f : points) {
			if(r.nextDouble() < 0.4) {
				tempPoints.add(f.p);
			}
			if(i >= count) {
				// reset the counter variables
				i = 0;
				count = r.nextInt(15) + 3;

				// reset the brush and other stuff
				/*
				cb.setColor(f.c);
				cb.setThickness(r.nextInt(6) + 3);
				*/

				hb.setThickness(r.nextInt(3) + 1);
				hb.setHeightIncrement(f.h);

				/*
				tb.setTileClass(f.tileType);
				tb.setThickness(r.nextInt(4) + 2);
				*/

				/*
				terrain.edit(cb, tempPoints);
				*/
				terrain.edit(hb, tempPoints);
				//terrain.edit(shb, tempPoints); TODO WTF!!

				/*
				terrain.edit(tb, tempPoints);
				*/
				tempPoints.clear();
			}

			i++;
		}

		tempPoints.clear();
		i = 0;

		for(NewWorldFoo f : points) {
			tempPoints.add(f.p);
			if(i >= count) {
				i = 0;
				count = r.nextInt(3) + 1;
				shb.setThickness(r.nextInt(5) + 1);
				terrain.edit(shb, tempPoints);
				tempPoints.clear();
			}
			i++;
		}

		for(int x = 0; x < w.getSizeX(); x++) {
			for(int y = 0; y < w.getSizeY(); y++) {
				BaseTile t = (BaseTile) w.getTileAt(x, y);
				if(t instanceof SeaTile) continue;
				world.tile.package$ tilehelper = package$.MODULE$;
				Iterable<Tuple2<Direction, IBaseTile>> dict = JavaConversions.asJavaIterable(tilehelper.neighborsOf(t).toList());
				boolean has = false;
				for(Tuple2<Direction, IBaseTile> tuple : dict) {
					if(tuple._2() instanceof SeaTile) {
						has = true;
						break;
					}
				}

				if(has) {
					int avg = 0, sum = 0, ct = 1;
					for(Tuple2<Direction, IBaseTile> tuple : dict) {
						if(tuple._2() instanceof SeaTile) continue;
						if(tuple._2() == null) continue;

						sum += tuple._2().getTileHeight();
						ct++;
					}
					avg = sum / ct;
					t.setMetadata(HeightBrush.meta_key, avg);
				}
			}
		}

		//printHeights(terrain);
		/*
		try {
			ImageIO.write(terrain.getColorMap(), "png", new File("colormap.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/

		/* OLD COMPUTATION
		for(int x = 0; x < terrain.getSizeX(); x++) {
			for(int y = 0; y < terrain.getSizeY(); y++) {
				Tile t = terrain.getTileAt(x, y);
				t.getCage().recomputeBase();
				t.updateGUI();
			}
		}

		// double recomputing is needed
		for(int x = 0; x < terrain.getSizeX(); x++) {
			for(int y = 0; y < terrain.getSizeY(); y++) {
				Tile t = terrain.getTileAt(x, y);
				t.getCage().recomputeBase();
			}
		}*/

		terrain.adjustHeights();

		NewWorldTestScreen.bindTileComponents();
		NewWorldTestScreen.add(p);
		NewWorldTestScreen.forcePullFront(p);

		p.world();

		System.out.println("Runtime.getRuntime().freeMemory() / (1024 * 1024) = " + Runtime.getRuntime().freeMemory() / (1024 * 1024));
		System.out.println("Runtime.getRuntime().totalMemory() / (1024 * 1024) = " + Runtime.getRuntime().totalMemory() / (1024 * 1024));
		//System.exit(0);
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
//				graphicsDevice.setDisplayMode(new DisplayMode(getWindowWidth(), getWindowHeight(), DisplayMode.BIT_DEPTH_MULTI, DisplayMode.REFRESH_RATE_UNKNOWN));

				gameWindow = new GameWindow();
				gameWindow.initializeScreens();
				// TODO dieser Methodenaufruf müsste in einen anderen Thread, glaube ich
				// so funktioniert es aber auch...
//				main.postInitScreens();    // empty method
				

				GameWindow.adjustWindow(Main.getWindowWidth(), Main.getWindowHeight(),
						gameWindow);
				toggleFullscreen(true);
				gameWindow.setVisible(false);
			}
		});
		thread.start();
	}

	// ###### GENERATE WORLD 
	// ###### SPAWN-SYSTEM
	/**
	 * Generates the world.
	 */
	private void generateWorld() {
		// a callable object has to be used, as the world cannot be saved instantly to
		// a GameScreen object
		futures.add(exec.submit(worldGen));
	}
	
	private Callable<World> worldGen = new Callable<World>() {
		@Override
		public World call() throws Exception {
			return WorldFactory.generateDefault(Mechanics.worldSizeX, Mechanics.worldSizeY);
		}
	};
	
	/**
	 * Neueste Änderungen an der Erstellung von Spielern unten bei den r.nextInt() Aufrufen.
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
			// Kein Randfeld fuer x oder y: zuerst durch 'getSize() - 2' Randfeld oben / unten verhindern; dann durch + 2 den Wert (0 und 1) verhindern
			// FIXME: durch die Spawnmethode kommt das richtige raus, wenn e.setSpawnX(...) nicht 0-basierte Werte erhählt
			do {
				x = r.nextInt(s.getWorld().getSizeX() - 2) + 1;
				// x = r.nextInt(13 - 2); 
				// --> x = 0 bis 11
				// x = x + 2;
				// --> x = 2 bis 13
				y = r.nextInt(s.getWorld().getSizeY() - 2) + 1;
				
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
	
	/**
	 * Initialiert die TimeClock
	 */
	private void initTimeClock() {
		/* Instanziert 'timeClock' */
		timeObj = new TimeClock();
		
		/* TimeClock wird zu Thread */
		stopwatchThread = new Thread(timeObj);
		stopwatchThread.setDaemon(true);
		
		timeObj.initNewPosition();
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
				System.out.println("Total memory in JVM: " + (int) (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " MB");
				System.out.println("Free memory  in JVM: " + (int) (Runtime.getRuntime().freeMemory() / (1024 * 1024)) + " MB");
				System.out.println("Java version: " + sys_props.getProperty("java.version"));
				System.out.println("OS: " + sys_props.getProperty("os.name") + "; version " + sys_props.getProperty("os.version"));
				System.out.println("----------------------------------\n");
			}
		});
		sys_props_out_thread.setPriority(Thread.MIN_PRIORITY);
		sys_props_out_thread.start();
	}
	
	
	/**
	 * Toggles between fullscreen mode and windowed mode.
	 *
	 * @param fullscreen The fullscreen flag.
	 */
	protected void toggleFullscreen(boolean fullscreen) {
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

	// ########################################################################
	// UNIMPORTANT METHODS -- NOT USED METHODS -- DEPRECATED METHODS ##########
	// ########################################################################
	
	/** empty: aufruf in Main ist auskommentiert */
	protected void postInitScreens() {

	}
	
	/**
	 * ruft die eigentlichen Schleifen auf, je nachdem ob man gewonnen /
	 * Gewonnen: doEndSequenceWonLoop (Aktuell 1000 MilliSekunden lang);
	 * Verloren: doEndSequenceDiedLoop (Aktuell 1000 MilliSekunden lang)
	 */
	@SuppressWarnings("unused")
	private void endSequenceLoop() {
//		if (lifePlayer.getLife() <= 0 || timeObj.getMilliDeath() < 0) {
//			// Hier einstellen, wie lange Die End-Sequenz Schleife laufen soll
//			doEndSequenceDiedLoop(1000);
//		} else if (lifeKI.getLife() <= 0) {
//			// Hier einstellen, wie lange die End-Sequenz Schleife laufen soll
//			doEndSequenceWonLoop(1000);
//		}
	}

	/**
	 * Berechnung der Endsequence: Gewonnen Aufruf durch: endSequenceLoop()
	 * milliSec: Die Zeit in Millisekunden, bis er automaisch terminiert
	 */
	@SuppressWarnings("unused")
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
	@SuppressWarnings({ "deprecation", "unused" })
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
	
	// ########################################
	// GETTERS ################################
	// SETTERS ################################
	// ########################################
	
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
	
	/** Abmessungen des Fensters: <code> new Rectangle (0, 0, Main.getWindowWidth(), Main.getWindowHeight()); </code> */
	public static Rectangle getWindowDimensions() {
		return new Rectangle(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
	}

	/** <code> while (isRunning() == true) main.render(); </code> */
	public boolean isRunning() {
		return running;
	}

	/** <code> while (isRunning() == true) main.render(); </code> */
	void setRunning(boolean running) {
		this.running = running;
	}
	
	/**
	 * GETTER: PreWindow
	 */
	public PreWindow getPreWindow() {
		return Main.settingWindow;
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

}
