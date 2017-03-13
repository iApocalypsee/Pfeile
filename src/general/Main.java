package general;

import akka.actor.ActorSystem;
import animation.SoundPool;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import general.io.PreInitStage;
import general.langsupport.*;
import misc.ArmingInitialization;
import misc.ItemInitialization;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.Future$;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/** The Main class of Pfeile. The main-method of class Main initializes the game, controlls the run&update loop of the
  * draw method and provides some static references for important other classes. */
public class Main {

    /** The static reference to the GameWindow. */
    private static GameWindow gameWindow;

    /** Determines whether Pfeile is running in debug mode. */
    public static boolean isDebug() {
        return false;
    }

    /** main instance */
    private static Main main;

    /** Just user data. User data is not intertwined with game data.
	  * The game data uses data from user, but the user does not use any data from world. */
    private static User user;

	// The game context in which the game currently is.
	// Every time a game is started, the context variable should be non-null.
	private static PfeileContext context = null;

    /** The language the user speaks (or is supposed to speak). */
    private static Language language = English.instance();

	/** The actor system taking care of threaded actors. */
	private static ActorSystem actorSystem;

    /** Central translation instance. */
    private static LangDict dict = null;

    /** Only load the sound system / sound files, if isMute is true. isMute is changed depending on the arguments of
     * the main method. */
    public static boolean isMute = false;

    /** System.currentTimeMillis() at the start of the game. */
    private static long programStartTime;

    // DONE WITH ALL VARIABLES INITIALIZATION

    /**
     * Only returns the instance of the main object.
     *
     * @return returns the reference of the main object.
     */
    public static Main getMain() {
        return main;
    }

    // Constructor

    /**
     * an empty constructor, creates the main instance {@link Main#getMain()}.
     * The code is executed in the main method.
     */
    private Main() {}

    // MAIN METHOD

    /**
     * List of possible program arguments to Pfeile:
     *  "-nofullscreen" => Do not enter fullscreen upon program startup.
     *  "-dbgwindows"   => Enables the debug window object.
     *  "-nosound"      => Disables sound (bug: game-over-sound still played).
     */
    public static void main(String[] arguments) {
        programStartTime = System.currentTimeMillis();

        Thread startThread = new Thread(() -> {
            main = new Main();
            user = new User(SystemProperties.getComputerName());

            System.out.println("\nRunning Pfeile on... " + user.getUsername() + "\n");
            SystemProperties.printSystemProperties();
        }, "StartThread");
        startThread.setPriority(Thread.MAX_PRIORITY);
        startThread.setDaemon(true);
        startThread.start();

        // Determines if the game should switch directly to fullscreen mode.
        // This line makes it possible for users to specify on the command line that he does
        // not want to enter fullscreen mode.
        boolean activateFullscreen = Arrays.stream(arguments).noneMatch(arg -> arg.equals("-nofullscreen"));

        // For debug purposes only.
        boolean activateDbgWindows = Arrays.stream(arguments).anyMatch(arg -> arg.equals("-dbgwindows"));

        // For users who do not want to hear sound.
        isMute = Arrays.stream(arguments).anyMatch(arg -> arg.equals("-nosound"));

        // This will load the background melodies of SoundPool and SoundEffectTimeClock in an Thread and start to play
        // the main melodie, if it's ready.
        SoundPool.isLoaded();

        LogFacility.log("Beginning initialization process...", "Info", "init process");

        final Config akkaConfig = ConfigFactory.parseString("akka {\nloglevel = \"DEBUG\" \n}");
        actorSystem = ActorSystem.create("system", akkaConfig);

        // if deactivated, save time and resources.
        if (activateDbgWindows) {
            DebugWindows debugWindows = new DebugWindows();
            debugWindows.enable();
            LogFacility.log("Activated debug windows. ", LogFacility.LoggingLevel.Debug, "init process");
        }

        Thread langInit = new Thread(() -> {
            dict = LangInitialization.loadLanguageFiles();
            LogFacility.log("JSON directories for the language system loaded.", LogFacility.LoggingLevel.Info, "init process");

            LangInitialization.apply();
            LogFacility.log("LangInitialization done!", "Info", "init process");
        }, "LangInitThread");
        langInit.setDaemon(true);
        langInit.setPriority(4);
        langInit.start();

        PreInitStage.execute(); // save game directory etc.
        LogFacility.log("PreInitStage done!", "Info", "init process");

        gameWindow = new GameWindow();
        LogFacility.log("Instance of GameWindow & ScreenManager created.", LogFacility.LoggingLevel.Info, "init process");

        // wait for the language system to finish. Screens and items need the language system for their names, so make
        // sure, that the language initialization has finished before. The language initialization is quite fast, though.
        try {
            langInit.join();
        } catch (InterruptedException e) { e.printStackTrace(); }

        // initialize Weapons and Armours (internally threaded)
        Thread arrowInitializationThread = ArmingInitialization.initialize();

        // initialize Loots, Coins and Potions (internally threaded)
        ItemInitialization.initialize();

        gameWindow.initializeScreens(arrowInitializationThread);
        LogFacility.log("Screens initialized.", "Info", "init process");

        // window showing process
        boolean isFullscreen = GameWindow.adjustWindow(gameWindow, activateFullscreen);
        gameWindow.setVisible(true);
        gameWindow.createBufferStrategy();
        LogFacility.log("GameWindow ready. Activated Fullscreen: " + isFullscreen, "Info", "init process");

        LogFacility.log("Pfeile is ready...", "Info", "init process");
        LogFacility.putSeparationLine();

        // Let's start the game
        main.runGame();

        System.out.println();
        LogFacility.putSeparationLine();
        LogFacility.log("Exiting Pfeile.", LogFacility.LoggingLevel.Info, "closing process");

        // begin of a softer process, than just calling System.exit(0)
        gameWindow.dispose();
        LogFacility.log("Disposed GameWindow.", LogFacility.LoggingLevel.Info, "closing process");

        // stop all melodies
        SoundPool.stop_allMelodies();
        LogFacility.log("Stopped sound system.", LogFacility.LoggingLevel.Info, "closing process");

        actorSystem.terminate();
        LogFacility.log("Terminated actor system. ", LogFacility.LoggingLevel.Info, "closing process");

        // There is no other way, that closes the games.
        // Some Threads were still running in background, that continued the game without seeing a screen.
        System.exit(0);
    }

    // RUN GAME

    /** Starts the run loop with a defined update speed in seconds. The update speed is 1/refreshRate of
     * the display (or 1/60.0 if unknown)*/
    private void runGame() {
        // the refresh rate of the display measured in Hertz.
        int refreshRate = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate();
        // if the display only runs at 30Hz, we can reduce the update speed to 1/30.
        if (refreshRate != DisplayMode.REFRESH_RATE_UNKNOWN)
            GameLoop.run(1 / (double) refreshRate);
        else
            GameLoop.run(1 / 60.0);
    }

    // general methods, threads

    /**
     * Gets the translation for given ID and the currently selected language as the translation language.
     * @param identifier The translation node ID to look for.
     * @return A translation, or throws an exception if it is not found.
     */
    public static String tr(String identifier) {
        return dict.translate(identifier, language.langCode());
    }

    public static String tr(String identifier, Object ...args) {
        return String.format(tr(identifier), args);
    }

    /**
     * Returns a buffered image which data model is optimized for the underlying system, allowing for better performance.
     * @param image The image to be converted to a compatible format.
     * @return A compatible buffered image, or the same image if the data model is already compatible with the system.
     */
    public static BufferedImage toCompatibleImage(BufferedImage image) {
        GraphicsConfiguration graphicsConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        if(image.getColorModel().equals(graphicsConfig.getColorModel()))
            return image;
        else {
            final BufferedImage compatibleImage = graphicsConfig.createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());
            final Graphics2D graphics = (Graphics2D) compatibleImage.getGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();
            return compatibleImage;
        }
    }

    /** Returns a future object to compute asynchronous an compatible buffered image {@link Main#toCompatibleImage(BufferedImage)}*/
    public static Future<BufferedImage> askForCompatibleImage(BufferedImage image) {
        final scala.compat.java8.JFunction0<BufferedImage> convert = () -> toCompatibleImage(image);
        return Future$.MODULE$.apply(convert, getGlobalExecutionContext());
    }

    /**
     * Returns the global execution context, mainly for Scala's Future class to be happy about its implicit parameter
     * which Java cannot fill in because... it's Java.
     * @return The Scala Future's global execution context.
     */
    public static ExecutionContext getGlobalExecutionContext() {
        return ExecutionContext.Implicits$.MODULE$.global();
    }

    // UNIMPORTANT METHODS -- NOT USED METHODS -- DEPRECATED METHODS

    /**
     * Called when there are some additional resources to return to the OS. The call
     * Right now, there are no resources we need to release explicitly.
     */
    void disposeInitialResources () {
        LogFacility.log("Initial resources disposed. ", LogFacility.LoggingLevel.Debug, "init process");
    }

    // GETTERS & SETTERS

    /** GETTER: GameWindow */
    public static GameWindow getGameWindow() {
        return gameWindow;
    }

    /** Returns the User of the system. Only providing some knowledge about the host (host name etc.). */
    public static User getUser() {
        return user;
    }

    public static long getProgramStartTime() {
        return programStartTime;
    }

    /** Returns <code>true</code>, if the language is set to english. */
    public static boolean isEnglish() {
        return language == English$.MODULE$;
    }

    /**
     * Returns the object holding all data for the game.
     * <b>DO NOT USE THIS OBJECT UNTIL IT HAS BEEN INITIALIZED!</b>
     */
	public static PfeileContext getContext() {
        if(context == null)
            throw new NullPointerException("PfeileContext is not yet initialized. Wait for ContextCreator to finish in order to use PfeileContext");
		return context;
	}

	/** returns the central static instance of the actor system. */
	public static ActorSystem getActorSystem() {
		return actorSystem;
	}

	/** sets the PfeileContext of this game. Does nothing, if <code>Main.getContext() != null</code> because it would
     * unforeseeable consequences. It is required, that context != null. */
	public static void setContext(PfeileContext context) {
		if (Main.context == null && context != null)
		    Main.context = context;
		else
		    LogFacility.log("Cannot change PfeileContext: Old context: " + Main.context + "; new Context: " + context,
                    LogFacility.LoggingLevel.Warning);
	}
}
