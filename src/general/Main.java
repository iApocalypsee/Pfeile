package general;

import akka.actor.ActorSystem;
import animation.SoundPool;
import general.io.PreInitStage;
import gui.screen.*;
import newent.CommandTeam;
import newent.Player;
import newent.Team;
import player.weapon.arrow.ArrowHelper;
import scala.collection.Seq;
import scala.runtime.AbstractFunction0;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;
import world.TileLike;

import java.awt.*;

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
    private static Main main;

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
    private Main() {}

    // ###########################################################
    // HIER STEHEN DIE HAUPTAUFRUFE ##############################
    // ################### IMPORTANT #############################
    // ###########################################################

    /**
     * Main-Method �ffnet eine neue Instanz von Main: main
     */
    public static void main(String[] arguments) {

        GraphicsEnvironment environmentG = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // This will load the background melodies of SoundPool and SoundEffectTimeClock in an Thread and start to play
        // the main melodie, if it's ready.
        SoundPool.isLoaded();

        LogFacility.log("Running Pfeile on... " + SystemProperties.getComputerName(), "Info");
        LogFacility.putSeparationLine();

        SystemProperties.printSystemProperties();

        LogFacility.log("Beginning initialization process...", "Info", "initprocess");

        main = new Main();
        user = new User(SystemProperties.getComputerName());

        LogFacility.log("Attempting to execute PreInitStage...", "Info", "initprocess");

        PreInitStage.execute();

        LogFacility.log("PreInitStage done!", "Info", "initprocess");

        graphicsDevice = environmentG.getDefaultScreenDevice();
        gameWindow = new GameWindow();
        LogFacility.log("GameWindow instantiated.", "Info", "initprocess");

        // loading the arrow images is threaded now
        new ArrowHelper();

        gameWindow.initializeScreens();
        LogFacility.log("Screens initialized.", "Info", "initprocess");

        GameWindow.adjustWindow(gameWindow);
        //toggleFullscreen(true);

        // window showing process
        gameWindow.setVisible(true);
        gameWindow.createBufferStrategy();

	    ArrowSelectionScreenPreSet.getInstance().onScreenLeft.register(new AbstractFunction1<Screen.ScreenChangedEvent, BoxedUnit>() {
		    @Override
		    public BoxedUnit apply(Screen.ScreenChangedEvent v1) {
			    main.disposeInitialResources();
			    return BoxedUnit.UNIT;
		    }
	    });

	    LoadingWorldScreen.getInstance().onScreenLeft.register(new AbstractFunction1<Screen.ScreenChangedEvent, BoxedUnit>() {
            @Override
            public BoxedUnit apply (Screen.ScreenChangedEvent v1) {
                final TimeClock timeClock = getContext().getTimeClock();
                main.doArrowSelectionAddingArrows();
                getContext().onStartRunningTimeClock().call();
                // the players have been added to entityList, so this call is valid now
                PreWindowScreen.correctArrowNumber();

                // Notify the first player of the player list that it's his turn now.
                // The delegate has to be called somehow...
                final TurnSystem turnSystem = getContext().getTurnSystem();
                turnSystem.onTurnGet().call(turnSystem.getCurrentPlayer());


                // play the game sound, when the game begins
                //SoundPool.stop_titleMelodie();
                //SoundPool.playLoop_mainThemeMelodie(SoundPool.LOOP_CONTINUOUSLY);

                return BoxedUnit.UNIT;
            }
        });

        GameScreen.getInstance().onScreenEnter.registerOnce(JavaInterop.asScalaFunctionSupplier(() -> {

            final Seq<Team> teamSeq = Main.getContext().getTurnSystem().teams().apply();
            teamSeq.foreach(JavaInterop.asScalaFunction(team -> {
                Player p = ((CommandTeam) team).getHead();
                p.tightenComponentToTile((TileLike) p.tileLocation());
                return BoxedUnit.UNIT;
            }));
            return BoxedUnit.UNIT;
        }));

        LogFacility.log("Pfeile is ready.", "Info", "initprocess");
        LogFacility.putSeparationLine();

        // starten wir das Spiel
        main.runGame();

		/*
         * TODO Hier kommt cleanup-code, z.B. noch offene Dateien schließen.
		 * Ich weis, noch nichts zum Aufr�umen hinterher, aber wir werden es sp�ter
		 * 100% brauchen.
		 */

        // sanftes Schlie�en des GameWindows anstelle des harten System.exit(0)
        gameWindow.dispose();

        // stop all melodies
        //SoundPool.stop_allMelodies();

        // There is no other way, that closes the games.
        // Some Threads were still running in background, that continued the game without seeing a screen.
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

        GameLoop.run(1 / 60.0);

        // TODO: System, bei der nach jeder Runde der Bonusauswahlbildschirm und ArrowSelectionPreSet kommt
    }

    // #########################################################
    // METHODEN: v. a. alle Threads ############################
    // #########################################################

    /**
     * Puts all selected arrows from <code>ArrowSelectionScreenPreSet.getInstance()</code> to the inventory of the
     * Player by calling {@link player.weapon.Weapon#equip()}.
     */
    private void doArrowSelectionAddingArrows() {
        final ArrowSelectionScreenPreSet arrowSelection = ArrowSelectionScreenPreSet.getInstance();

        for (String selectedArrow : arrowSelection.selectedArrows) {
            if (!ArrowHelper.instanceArrow(selectedArrow).equip())
                System.err.println("Cannot add " + selectedArrow + " at Main.doArrowSelectionAddingArrows() - adding the arrowNumberPreSet");
        }
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
