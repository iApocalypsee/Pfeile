package general;

import animation.SoundPool;
import gui.*;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;
import world.WorldLike;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Hauptklasse mit der Main-Methode und den abstraktesten Objekten unseres Spiels.
 * <p>27.02.2014</p>
 * <ul>
 * <li>Updater hinzugef�gt. Der Updater stellt sicher, dass die Daten von Objekten geupdated werden.</li>
 * </ul>
 *
 * @version 1.3.2014
 */
public class Main {

    // NUR INTIALISIERUNG - WIE WERTE UND VARIABLEN ###############

    /** Die derzeitige Anzahl an FPS, die das Spiel gerade erzielt. */
    private volatile int fps;
    /** Die erw�nschte FPS-Rate. Wenn <code>setFPS == 0</code>, ist die Framerate unbegrenzt.
     * Unbegrenzte Framerate noch nicht implementiert! */
    private volatile int setFPS = 35;
    /** Der Timeout, der in der <code>Thread.sleep()</code>-Methode als Argument verwendet wird. */
    private int framerateTimeout = 1000 / setFPS;
    // Zeichenvariablen.
    private boolean running = true;
    public static int getWindowWidth() { return 1366;}
    public static int getWindowHeight() { return 768; }
    /** Returns the time in between two frames. The timeout is recalculated in every drawing process.
     * @return The time in between two frames. */
    public int getTimeSinceLastFrame() {
        return this.timeSinceLastFrame;
    }
    public static int delta() {
        return main.timeSinceLastFrame;
    }
    private int timeSinceLastFrame = 0;
    private static GameWindow gameWindow;
    private static GraphicsEnvironment environmentG;
    private static GraphicsDevice graphicsDevice;
    public static TimeClock timeObj;
    private static Thread stopwatchThread;
    private static Main main;
    // threading vars
    private static ExecutorService exec = Executors.newCachedThreadPool();
    private static Set<Future<?>> futures = new HashSet<Future<?>>(5);

    private static User user;
    // DONE WITH ALL VARIABELS;
    // MOST IMPORTANT METHODS ####################################
    // ###########################################################

    /**
     * F�hrt nicht die main-Funktion aus, sondern gibt eine Referenz auf das
     * Main-Objekt zur�ck.
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
     * in <code>foo()</code> get�tigt.
     */
    private Main() {}

    // ###########################################################
    // HIER STEHEN DIE HAUPTAUFRUFE ##############################
    // ################### IMPORTANT #############################
    // ###########################################################

    /**
     * Main-Method �ffnet eine neue Instanz von Main: main
     */
    public static void main(String[] arguments) {
        user = new User("Just a user");

        main = new Main();
        main.printSystemProperties();

        environmentG = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsDevice = environmentG.getDefaultScreenDevice();
        gameWindow = new GameWindow();

        new player.weapon.ArrowHelper();

        gameWindow.initializeScreens();
        // Let's begin playing the title song
        SoundPool.play_titleMelodie();

        GameWindow.adjustWindow(gameWindow);
        toggleFullscreen(true);

        // window showing process
        gameWindow.createBufferStrategy();
        gameWindow.setVisible(true);

        // run the game until GameWindow
        GameLoop.run(1 / 60.0);

        // TODO: all gerate world methods need to be here or if they can be declared as a thread before GameLoop.run(1 / 60.0);
        // empty method
        main.newWorldTest();
        // empty method
        main.generateWorld();
        // empty method
        main.populateWorld();
        // add the arrows to the inventory
        main.doArrowSelectionAddingArrows();
        // and TimeClock with stopWatchThread
        main.initTimeClock();
        // empty method
        main.postInitScreens();
        // empty method
        main.disposeInitialResources();

        // play the sound
        SoundPool.stop_titleMelodie();
        SoundPool.playLoop_mainThemeMelodie(SoundPool.LOOP_COUNTINOUSLY);

        // Schalten wir auf den GameScreen evtl. über Warte-Screen wechseln
        getGameWindow().getScreenManager().setActiveScreen(GameScreen.SCREEN_INDEX);

        // starten wir das Spiel
        main.runGame();

		/*
         * TODO Hier kommt cleanup-code, z.B. noch offene Dateien schließen.
		 * Ich weis, noch nichts zum Aufr�umen hinterher, aber wir werden es sp�ter
		 * 100% brauchen.
		 */

        // sanftes Schlie�en des GameWindows anstelle des harten System.exit(0)
        gameWindow.dispose();
        // und dann trotzdem HARTES BEENDEN!!! :D
        System.exit(0);
    }

    /** EMPTY */
    private void disposeInitialResources() {

    }

    // ############ RUN GAME

    /**
     * Hier laeuft das Spiel nach allen Insizialisierungen
     */
    private void runGame() {

        // start TimeClock
        stopwatchThread.start();
        timeObj.start();

        GameLoop.run(1 / 60.0);

        // TODO: System, bei der nach jeder Runde der Bonusauswahlbildschirm und ArrowSelectionPreSet kommt
        // TODO: System muss auch auf Niederlage überprüfen
    }

    // #########################################################
    // METHODEN: v. a. alle Threads ############################
    // #########################################################

    /**
     * Fuegt die Pfeile in das Inventar des Spielers ein
     */
    private void doArrowSelectionAddingArrows() {
        final ArrowSelectionScreenPreSet arrowSelection = (ArrowSelectionScreenPreSet) (getGameWindow().getScreenManager().getScreens().get(ArrowSelectionScreenPreSet.SCREEN_INDEX));

        for (String selectedArrow : arrowSelection.selectedArrows) {
            GameScreen.getInstance().getActivePlayer().inventory().put(
                    player.weapon.ArrowHelper.instanceArrow(selectedArrow));
        }

        ArrowSelectionScreen.getInstance().updateInventoryList();
    }

	// DEFAULT IMPLEMENTATION WITH NO PROBABILITIES
	private <T> T decide(LinkedList<T> objects) {
		return objects.get(new Random().nextInt(objects.size()));
	}

    public LinkedList<Integer> filterNotEvenNumbers(LinkedList<Integer> input) {
        LinkedList<Integer> result = new LinkedList<Integer>();
        for (Integer i : input) {
            if (i % 2 == 0) result.add(i);
        }
        return result;
    }

    public LinkedList<Integer> filterEvenNumbers(LinkedList<Integer> input) {
        LinkedList<Integer> result = new LinkedList<Integer>();
        for (Integer i : input) {
            if (i % 2 == 1) result.add(i);
        }
        return result;
    }

    /**
     * FIXME: hier kann man eigentlich auch gleich den Punkt verwenden: LinkedList<Point>
     */
    private class SHBCoordinator {
        public Point p = null;
    }

    private void newWorldTest() {
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

                new player.weapon.ArrowHelper();

                gameWindow.initializeScreens();
                main.initTimeClock();
                main.newWorldTest();

                GameWindow.adjustWindow(gameWindow);
                toggleFullscreen(true);

                // window showing process
                gameWindow.setVisible(true);
                gameWindow.createBufferStrategy();
            }
        });

        thread.setPriority(7);
        thread.start();

        synchronized(this) {
            main.notify();
        }
    }

    // ###### GENERATE WORLD
    // ###### SPAWN-SYSTEM

    /**
     * Generates the world.
     */
    private void generateWorld() {
        // a callable object has to be used, as the world cannot be saved instantly to
        // a GameScreen object
        //futures.add(exec.submit(worldGen));
    }

    /**
     * Neueste �nderungen an der Erstellung von Spielern unten bei den r.nextInt() Aufrufen.
     */
    private void populateWorld() {
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
    }

    /**
     * Prints all system properties to console.
     */
    private void printSystemProperties() {
        SystemProperties sys_props_out_thread = new SystemProperties();
        sys_props_out_thread.setPriority(Thread.MIN_PRIORITY);
        sys_props_out_thread.start();
    }


    /**
     * Toggles between fullscreen mode and windowed mode.
     *
     * @param fullscreen The fullscreen flag.
     */
    protected static void toggleFullscreen(boolean fullscreen) {
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

    /**
     * empty: aufruf in Main ist auskommentiert
     */
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
    @SuppressWarnings({"deprecation", "unused"})
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
     * @param fps Die neu erw�nschte FPS-Anzahl.
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
     * Gibt die vom Spiel gerade erzielten FPS zur�ck.
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

    /**
     * Abmessungen des Fensters: <code> new Rectangle (0, 0, Main.getWindowWidth(), Main.getWindowHeight()); </code>
     */
    public static Rectangle getWindowDimensions() {
        return new Rectangle(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
    }

    /**
     * <code> while (isRunning() == true) main.render(); </code>
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * <code> while (isRunning() == true) main.render(); </code>
     */
    void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * GETTER: GameWindow
     */
    public static GameWindow getGameWindow() {
        return gameWindow;
    }

    public static User getUser() {
        return user;
    }
}
