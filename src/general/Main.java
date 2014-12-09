package general;

import akka.actor.ActorSystem;
import animation.SoundPool;
import general.io.PreInitStage;
import gui.*;
import player.weapon.ArrowHelper;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;

import java.awt.*;
import java.util.HashSet;
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
    // Zeichenvariablen.
    private boolean running = true;
    public static int getWindowWidth() { return 1366;}
    public static int getWindowHeight() { return 768; }
    /** Returns the time in between two frames. The timeout is recalculated in every drawing process.
     * @return The time in between two frames. */
    public static int getTimeSinceLastFrame() {
        return main.timeSinceLastFrame;
    }
    private int timeSinceLastFrame = 0;
    private static GameWindow gameWindow;
    private static GraphicsDevice graphicsDevice;
    private static Main main;
    // threading vars
    private static ExecutorService exec = Executors.newCachedThreadPool();
    private static Set<Future<?>> futures = new HashSet<Future<?>>(5);

	// Just user data. User data is not intertwined with game data.
	// The game data uses data from user, but the user does not use any data from world.
    private static User user;

	// The game context in which the game currently is.
	// Every time a game is started, the context variable should be non-null.
	private static PfeileContext context = null;

	// The actor system taking care of threaded actors.
	private static ActorSystem actorSystem = ActorSystem.create("system");

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
    private Main() {
    }

    // ###########################################################
    // HIER STEHEN DIE HAUPTAUFRUFE ##############################
    // ################### IMPORTANT #############################
    // ###########################################################

    /**
     * Main-Method �ffnet eine neue Instanz von Main: main
     */
    public static void main(String[] arguments) {
	    PreInitStage.execute();

        // Let's begin playing the title song (so the user knows, that something is done while loading the game)
        SoundPool.play_titleMelodie();

        user = new User("Just a user");

        main = new Main();
        main.printSystemProperties();

        GraphicsEnvironment environmentG = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsDevice = environmentG.getDefaultScreenDevice();
        gameWindow = new GameWindow();

        new player.weapon.ArrowHelper();

        gameWindow.initializeScreens();

        GameWindow.adjustWindow(gameWindow);
        toggleFullscreen(true);

        // window showing process
        gameWindow.createBufferStrategy();
        gameWindow.setVisible(true);

	    ArrowSelectionScreenPreSet.getInstance().onScreenLeft.register(new AbstractFunction1<Screen.ScreenChangedEvent, BoxedUnit>() {
		    @Override
		    public BoxedUnit apply(Screen.ScreenChangedEvent v1) {
			    main.disposeInitialResources();
			    return BoxedUnit.UNIT;
		    }
	    });

	    LoadingWorldScreen.getInstance().onScreenLeft.register(new AbstractFunction1<Screen.ScreenChangedEvent, BoxedUnit>() {
		    @Override
		    public BoxedUnit apply(Screen.ScreenChangedEvent v1) {
			    main.doArrowSelectionAddingArrows();
			    return BoxedUnit.UNIT;
		    }
	    });

        // play the sound
        SoundPool.stop_titleMelodie();
        SoundPool.playLoop_mainThemeMelodie(SoundPool.LOOP_COUNTINOUSLY);

        // starten wir das Spiel
        main.runGame();

		/*
         * TODO Hier kommt cleanup-code, z.B. noch offene Dateien schließen.
		 * Ich weis, noch nichts zum Aufr�umen hinterher, aber wir werden es sp�ter
		 * 100% brauchen.
		 */

        // sanftes Schlie�en des GameWindows anstelle des harten System.exit(0)
        gameWindow.dispose();
    }

    /** EMPTY */
    private void disposeInitialResources() {

    }

    // ############ RUN GAME

    /**
     * Hier laeuft das Spiel nach allen Insizialisierungen
     */
    private void runGame() {
	    /*
        // reset TimeClock first, because with the change of the activePlayer a delegate in PfeileContext starts TimeClock and the activePlayer is set before this line
        getContext().getTimeClock().reset();
        // start TimeClock
        getContext().getTimeClock().start();
        */

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
            if (!Main.getContext().getActivePlayer().inventory().put(
		            ArrowHelper.instanceArrow(selectedArrow)))
                System.err.println("Cannot add " + selectedArrow + " at Main.doArrowSelectionAddingArrows() - adding the arrowNumberPreSet");
        }
    }

    // ###### GENERATE WORLD
    // ###### SPAWN-SYSTEM

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
	// None right now.

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
        //recalculateTimeout();
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

	public static PfeileContext getContext() {
		return context;
	}

	public static ActorSystem getActorSystem() {
		return actorSystem;
	}

	public static void setContext(PfeileContext context) {
		scala.Predef.require(context != null);
		Main.context = context;
	}
}
