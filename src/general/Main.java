package general;

import akka.actor.ActorSystem;
import animation.SoundPool;
import general.io.PreInitStage;
import general.langsupport.LangInitialization;
import gui.screen.ArrowSelectionScreenPreSet;
import misc.ArmingInitialization;
import misc.ItemInitialization;

import java.awt.*;
import java.util.Arrays;

public class Main {

    // NUR INTIALISIERUNG - WIE WERTE UND VARIABLEN ###############

    /** the width of the displayed window
     * @return getMaximumWindowBounds().width
     * @see general.Main#getWindowHeight()
     * @see general.Main#getWindowDimensions() */
    public static int getWindowWidth() { return getWindowDimensions().width; }
    
    /** the height of the displayed window
     * @return getMaximumWindowBounds().height
     * @see Main#getWindowHeight()
     * @see Main#getWindowDimensions() */
    public static int getWindowHeight() { return getWindowDimensions().height; }

    private static GameWindow gameWindow;
    private static GraphicsDevice graphicsDevice;

    /**
     * Ditto.
     * @see Main#isDebug
     */
    private static boolean debug = true;

    /**
     * Determines whether Pfeile is running in debug mode.
     */
    public static boolean isDebug() {
        return debug;
    }

    private static Main main;

    // Just user data. User data is not intertwined with game data.
	// The game data uses data from user, but the user does not use any data from world.
    private static User user;

	// The game context in which the game currently is.
	// Every time a game is started, the context variable should be non-null.
	private static PfeileContext context = null;

	// The actor system taking care of threaded actors.
	private static ActorSystem actorSystem = ActorSystem.create("system");

    private static DebugWindows debugWindows = new DebugWindows();

    private static long programStartTime;

    // DONE WITH ALL VARIABLES;
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
     * List of possible program arguments to Pfeile:
     *  "-nofullscreen" => Do not enter fullscreen upon program startup.
     *  "-dbgwindows"   => Enables the debug window object.
     */
    public static void main(String[] arguments) {

        // Determines if the game should switch directly to fullscreen mode.
        // This line makes it possible for users to specify on the command line that he does
        // not want to enter fullscreen mode.
        boolean activateFullscreen = !Arrays.stream(arguments).anyMatch(arg -> arg.equals("-nofullscreen"));

        boolean activateDbgWindows = Arrays.stream(arguments).anyMatch(arg -> arg.equals("-dbgwindows"));
        debugWindows.setWindowEnabled(activateDbgWindows);

        programStartTime = System.currentTimeMillis();

        GraphicsEnvironment environmentG = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // This will load the background melodies of SoundPool and SoundEffectTimeClock in an Thread and start to play
        // the main melodie, if it's ready.
        SoundPool.isLoaded();

        System.out.println("Running Pfeile on... " + SystemProperties.getComputerName() + "\n");

        SystemProperties.printSystemProperties();

        LogFacility.log("Beginning initialization process...", "Info", "initprocess");

        main = new Main();
        user = new User(SystemProperties.getComputerName());

        PreInitStage.execute();

        LogFacility.log("PreInitStage done!", "Info", "initprocess");

        LangInitialization.apply();
        LogFacility.log("LangInitialization done!", "Info", "initprocess");

        graphicsDevice = environmentG.getDefaultScreenDevice();
        gameWindow = new GameWindow();
        LogFacility.log("GameWindow instantiated.", "Info", "initprocess");

        // initialize Weapons and Armours (internally threaded)
        Thread arrowInitializationThread = ArmingInitialization.initialize();

        // initialize Loots, Coins and Potions (internally threaded)
        ItemInitialization.initialize();

        gameWindow.initializeScreens(arrowInitializationThread);
        LogFacility.log("Screens initialized.", "Info", "initprocess");

        GameWindow.adjustWindow(gameWindow);
        toggleFullscreen(activateFullscreen);

        // window showing process
        gameWindow.setVisible(true);
        gameWindow.createBufferStrategy();

	    ArrowSelectionScreenPreSet.getInstance().onScreenLeft.registerJava(event -> {
            main.disposeInitialResources();
        });

        LogFacility.log("Pfeile is ready.", "Info", "initprocess");
        LogFacility.putSeparationLine();

        // starten wir das Spiel
        main.runGame();

        // sanftes Schlie�en des GameWindows anstelle des harten System.exit(0)
        gameWindow.dispose();

        // stop all melodies
        //SoundPool.stop_allMelodies();

        actorSystem.shutdown();

        // There is no other way, that closes the games.
        // Some Threads were still running in background, that continued the game without seeing a screen.
        System.exit(0);
    }

    // ############ RUN GAME

    private void runGame() {
        GameLoop.run(1 / 60.0);
    }

    // #########################################################
    // METHODEN: v. a. alle Threads ############################
    // #########################################################

    /**
     * Toggles between fullscreen mode and windowed mode.
     *
     * @param fullscreen The fullscreen flag.
     */
    protected static void toggleFullscreen(boolean fullscreen) {
        if (fullscreen) {
            if (graphicsDevice.getFullScreenWindow() == gameWindow) {
                LogFacility.log("Already in fullscreen mode.", "Info");
                return;
            }
            graphicsDevice.setFullScreenWindow(gameWindow);
        } else {
            if (graphicsDevice.getFullScreenWindow() != gameWindow) {
                LogFacility.log("Already in windowed mode.", "Info");
                return;
            }
            graphicsDevice.setFullScreenWindow(null);
        }
    }

    // ########################################################################
    // UNIMPORTANT METHODS -- NOT USED METHODS -- DEPRECATED METHODS ##########
    // ########################################################################

    /**
     * Called when there are some additional resources to return to the OS.
     * Right now, there are no resources we need to release explicitly.
     */
    private void disposeInitialResources() {
    }

    // ########################################
    // GETTERS ################################
    // SETTERS ################################
    // ########################################

    /**
     * Abmessungen des Fensters: <code> new Rectangle (0, 0, Main.getWindowWidth(), Main.getWindowHeight()); </code>
     */
    public static Rectangle getWindowDimensions() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
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

    public static long getProgramStartTime() {
        return programStartTime;
    }

    /**
     * Returns the object holding all data for the game.
     * <b>DO NOT USE THIS OBJECT UNTIL IT HAS BEEN INITIALIZED!</b>
     */
	public static PfeileContext getContext() {
        if(context == null) throw new NullPointerException();
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
