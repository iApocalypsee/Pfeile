package general;

import akka.actor.ActorSystem;
import animation.SoundPool;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import general.io.PreInitStage;
import general.langsupport.*;
import gui.screen.ArrowSelectionScreenPreSet;
import misc.ArmingInitialization;
import misc.ItemInitialization;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.Future$;

import java.awt.*;
import java.awt.image.BufferedImage;
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
     * Determines whether Pfeile is running in debug mode.
     */
    public static boolean isDebug() {
        return true;
    }

    private static Main main;

    // Just user data. User data is not intertwined with game data.
	// The game data uses data from user, but the user does not use any data from world.
    private static User user;

	// The game context in which the game currently is.
	// Every time a game is started, the context variable should be non-null.
	private static PfeileContext context = null;

    // The language the user speaks (or is supposed to speak).
    private static Language language = English.instance();

	// The actor system taking care of threaded actors.
	private static ActorSystem actorSystem;

    private static DebugWindows debugWindows = new DebugWindows();

    // Central translation instance.
    private static LangDict dict = new LangDict("general/CommonStrings.json")
            .addJSON("general/EverythingElse.json")
            .addJSON("general/GameMeta.json")
            .addJSON("general/Messages.json")
            .addJSON("item/Arrows.json")
            .addJSON("item/Items.json")
            .addJSON("rest/WorldStrings.json")
            .addJSON("screen/ArrowSelectionScreen.json")
            .addJSON("screen/ArrowSelectionScreenPreSet.json")
            .addJSON("screen/GameScreen.json")
            .addJSON("screen/LoadScreen.json")
            .addJSON("screen/PreWindowScreen.json")
            .addJSON("screen/WaitingScreen.json");

    private static long programStartTime;

    // DONE WITH ALL VARIABLES;
    // MOST IMPORTANT METHODS ####################################
    // ###########################################################

    public static Main getMain() {
        return main;
    }

    // KONSTRUKTOR ###############################################
    private Main() {
    }

    // ###########################################################
    // HIER STEHEN DIE HAUPTAUFRUFE ##############################
    // ################### IMPORTANT #############################
    // ###########################################################

    public static boolean isMute = false;

    /**
     * List of possible program arguments to Pfeile:
     *  "-nofullscreen" => Do not enter fullscreen upon program startup.
     *  "-dbgwindows"   => Enables the debug window object.
     *  "-nosound"      => Disables sound (bug: game-over-sound still played).
     */
    public static void main(String[] arguments) {

        //dict.printTree();

        // Determines if the game should switch directly to fullscreen mode.
        // This line makes it possible for users to specify on the command line that he does
        // not want to enter fullscreen mode.
        boolean activateFullscreen = !Arrays.stream(arguments).anyMatch(arg -> arg.equals("-nofullscreen"));
        // For debug purposes only.
        boolean activateDbgWindows = Arrays.stream(arguments).anyMatch(arg -> arg.equals("-dbgwindows"));
        // For users who do not want to hear sound.
        //boolean mute = Arrays.stream(arguments).anyMatch(arg -> arg.equals("-nosound"));
        // This is not good. SoundPool depends on isMute for being completed.
        isMute = Arrays.stream(arguments).anyMatch(arg -> arg.equals("-nosound"));

        final Config akkaConfig = ConfigFactory.parseString("akka {\nloglevel = \"DEBUG\" \n}");
        actorSystem = ActorSystem.create("system", akkaConfig);

        debugWindows.setWindowEnabled(activateDbgWindows);

        programStartTime = System.currentTimeMillis();

        GraphicsEnvironment environmentG = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // This will load the background melodies of SoundPool and SoundEffectTimeClock in an Thread and start to play
        // the main melodie, if it's ready.
        SoundPool.isLoaded();

        System.out.println("Running Pfeile on... " + SystemProperties.getComputerName() + "\n");

        SystemProperties.printSystemProperties();

        LogFacility.log("Beginning initialization process...", "Info", "initprocess");

        LangInitialization.apply();
        LogFacility.log("LangInitialization done!", "Info", "initprocess");

        main = new Main();
        user = new User(SystemProperties.getComputerName());

        PreInitStage.execute();

        LogFacility.log("PreInitStage done!", "Info", "initprocess");

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

	    ArrowSelectionScreenPreSet.getInstance().onScreenLeft.registerJava(event -> main.disposeInitialResources());

        LogFacility.log("Pfeile is ready.", "Info", "initprocess");
        LogFacility.putSeparationLine();

        // starten wir das Spiel
        main.runGame();

        // sanftes Schlie�en des GameWindows anstelle des harten System.exit(0)
        gameWindow.dispose();

        // stop all melodies
        //SoundPool.stop_allMelodies();

        actorSystem.terminate();

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
    public static void toggleFullscreen(boolean fullscreen) {
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
        if(image.getColorModel().equals(graphicsDevice.getDefaultConfiguration().getColorModel())) return image;
        else {
            final BufferedImage compatibleImage = graphicsDevice.getDefaultConfiguration().createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());
            final Graphics2D graphics = (Graphics2D) compatibleImage.getGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();
            return compatibleImage;
        }
    }

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

    /** Returns <code>true</code>, if the language is set to english. */
    public static boolean isEnglish() {
        return language == English$.MODULE$;
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
