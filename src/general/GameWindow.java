package general;

import geom.Point;
import geom.primitives.package$;
import gui.screen.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The JFrame of the Game. Redirects the calls to the active screen
 */
public class GameWindow extends JFrame {

    private static final long serialVersionUID = 7012286076598906440L;

	/** Rectangle with the maximum window bounds: new Rectangle(0, 0, GameWindow.WIDTH, GameWindow.HEIGHT) */
	public static final Rectangle BOUNDS = getScreenBounds();

	/** The width of the window */
	public static final int WIDTH = BOUNDS.width;

	/** The height of the window */
	public static final int HEIGHT = BOUNDS.height;

	/** Returns the Dimension of the Screen: new Dimension(GameWindow.WIDTH, GameWindow.HEIGHT) */
	public static final Dimension DIMENSION = new Dimension(WIDTH, HEIGHT);

	private BufferStrategy strat;

    private ScreenManager screenManager;

    private MouseHandler mouseHandler;

    private final CoordinateGrid grid = new CoordinateGrid(0, 0);

    private final Color backgroundColor = new Color(7, 3, 31);

	/**
	 * Constructor of GameWindow. Adds the Mouse-, MouseMotion-, MouseWeehl- and KeyListener basic calls (redirected to
	 * the screen listeners).
	 */
	public GameWindow() {
		super("Pfeile");
		setLayout(null);

        screenManager = new ScreenManager();
        mouseHandler = new MouseHandler(screenManager);

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addMouseWheelListener(mouseHandler);
        addKeyListener(new Keys());
    }

	/**
	 * Controls the threaded initialization of all screens. The initialization process of ArrowSelectionScreen and
	 * AimSelectionScreen requires loaded arrow images (must already be loaded beforehand for resizing etc.).
	 * The process is spread onto four threads and waits until all threads are finished to set PreWindowScreen as
	 * active screen. Calling this method early in the constructor of GameWindow will cause an exception.
	 */
	void initializeScreens(Thread arrowInitializationThread) {
		// a bit ugly, but I want to initialize PreWindowScreen in a thread as well
		final Screen[] preWindow = new Screen[1];

		Thread z = new Thread(() -> {
			// waiting for loading arrow images
			try {
				arrowInitializationThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// method: init(PfeileContext) is called later during ContextCreator#Stage: ApplyingOtherStuff
			ArrowSelectionScreen.getInstance();
			ArrowSelectionScreenPreSet.getInstance();
		}, "Screen Initializer #1");
		z.setDaemon(true);
		z.start();

        Thread x = new Thread(() -> {
            LoadingWorldScreen.getInstance();
            preWindow[0] = new PreWindowScreen();
			preWindow[0].onScreenLeft.registerJava(event -> Main.getMain().disposeInitialResources());
        }, "Screen Initializer #2");
        x.setDaemon(true);
        x.start();

        Thread y = new Thread(() -> {
            GameScreen.getInstance();
            new InventoryScreen();
            new WaitingScreen();
        }, "Screen Initializer #3");
        y.setDaemon(true);
        y.start();

        Thread w = new Thread(() -> {
            new GameOverScreen();
            new AimSelectionScreen();
            AttackingScreen.getInstance();
        }, "Screen Initializer #4");
        w.setDaemon(true);
        w.start();

        try {
            x.join();
            y.join();
            w.join();
            z.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        screenManager.setActiveScreen(preWindow[0]);
    }

	/**
	 * Buffering: 2 Buffers
	 */
	 void createBufferStrategy() {
		createBufferStrategy(2);
		strat = getBufferStrategy();
	}

	/**
	 * Changes the settings of GameWindow. For better capability should always be called after the initialization of
	 * GameWindow (if not there is no grantee, that it doesn't throw an exception). This call will prepare full screen
	 * mode and finish the initialization of the GameWindow window. The GameWindow will be toggled into full screen mode,
	 * if the param fullScreen flag is set true.
	 * @return true - if it successfully switched to full screen mode; false - if param activeFullscreen
	 * 			is false or it failed to switch into full screen mode.
	 */
	synchronized static boolean adjustWindow (GameWindow window, boolean activateFullscreen) {
		GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setSize(WIDTH, HEIGHT);
		window.setExtendedState(Frame.MAXIMIZED_BOTH);
		window.setResizable(false);
		window.setLocationRelativeTo(null);

		if (window.isAlwaysOnTopSupported()) {
			window.setAlwaysOnTop(true);
		} else {
			LogFacility.log("Always on top option is not supported by operating system. GameWindow won't have the always on top status.",
					LogFacility.LoggingLevel.Warning);
		}

		boolean isFullscreen = false;
		if (activateFullscreen) {
			if (graphicsDevice.isFullScreenSupported()) {
				window.setUndecorated(true);
				graphicsDevice.setFullScreenWindow(window);
				isFullscreen = true;
			} else {
				LogFacility.log("Fullscreen is not natively supported by the default graphics device! Check AWTPermission! Pfeile is started in a normal frame window.",
						LogFacility.LoggingLevel.Warning);
			}
		}

        // adding the icon on the upper right corner or on the task bar.
        BufferedImage windowIcon = null;
        try {
            windowIcon = ImageIO.read(GameWindow.class.getClassLoader().getResourceAsStream(
                    "resources/gfx/comp/windowIcon.png"));
        } catch (IOException e) { e.printStackTrace(); }

        if (windowIcon != null)
            window.setIconImage(windowIcon);
        return isFullscreen;
	}

	/** Returns a Rectangle with the screen dimensions. It takes multiple screens into account (or rather can). In
	 * multiple screen system, the size of the primary display is returned. */
	private static Rectangle getScreenBounds () {
		// for one display this is perfect. If it's a multiple screen system, only the primary display size is returned.
		return new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
	}

	/** Updates the KeyListener and afterwards sends a call to screen manager to draw the active screen */
	public void update() {
		Keys.updateKeys();
        screenManager.screenCycle();
        mouseHandler.flushCallbacks();
	}

	/** Gets the Graphics2D object, sets the rendering hints, draws the backgrounds, calls the screen to draw their
	 * part and finally disposes the graphics object. */
	public void draw() {
		Graphics2D g = (Graphics2D) strat.getDrawGraphics();

        // set some properties for the graphics object
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setColor(backgroundColor);
		g.fillRect(0, 0, GameWindow.WIDTH, GameWindow.HEIGHT);

        final package$ primitivesPackageObj = package$.MODULE$;
        primitivesPackageObj.setGraphics(g);

        screenManager.draw(g);

        if(Main.isDebug() && grid.isActivated()) {
            grid.draw(g);
        }

        primitivesPackageObj.setGraphics(null);

        g.dispose();
        strat.show();
    }

    public CoordinateGrid getGrid() {
        return grid;
    }

    public Point getCenterPosition() {
        return new Point(getWidth() / 2, getHeight() / 2);
    }

    /**
     * @return the screenManager
     */
    public ScreenManager getScreenManager() {
        return screenManager;
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        grid.setCanvasWidth(width);
        grid.setCanvasHeight(height);
    }
}

