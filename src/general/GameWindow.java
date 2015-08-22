package general;

import geom.Vector2;
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
 * <b>4.1.2014 (Josip):</b> Einfachere Initialisierung der Screens. <br>
 * <b>10.1.2014 (Josip):</b> Verschiebung von GameWindow ins Package "com.github.pfeile.general"
 * <b>24.1.2014 (Josip):</b> Entfernung von <code>readyToShow</code>, unn�tige Variable
 *
 * @version 10.1.2014
 *
 */
public class GameWindow extends JFrame {

	private static final long serialVersionUID = 7012286076598906440L;

	private BufferStrategy strat;

	private ScreenManager screenManager;

	private final CoordinateGrid grid = new CoordinateGrid(0, 0);

	/**
	 * Konstruktor von GameWindow.
	 */
	public GameWindow() {
		super("Pfeile");
		setLayout(null);

		screenManager = new ScreenManager();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				screenManager.getActiveScreen().mouseReleased(arg0);
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
				screenManager.getActiveScreen().mousePressed(arg0);
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				screenManager.getActiveScreen().mouseMoved(e);
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				screenManager.getActiveScreen().mouseDragged(e);
			}
		});

		addMouseWheelListener(new MouseAdapter() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				screenManager.getActiveScreen().mouseWheelMoved(e);
			}

		});

		addKeyListener(new Keys());
	}

	/**
	 * Initialisiert alle Screens. Hier kommen die Konstruktoraufrufe der einzelnen Screens
	 * rein. GameScreen und ArrowSelectionScreen wurden bereits vorher initialisiert.
	 * <b>Darf nicht im Konstruktor von GameWindow selbst aufgerufen werden.</b>
	 */
	void initializeScreens(Thread arrowInitializationThread) {
        Thread x = new Thread(() -> {
            new MainMenuScreen();
            new PauseScreen();
        }, "Screen Initializer #1");
        x.setDaemon(true);
        x.start();

        Thread y = new Thread(() -> {
            GameScreen.getInstance();
            new InventoryScreen();
            LoadingWorldScreen.getInstance();
            new WaitingScreen();
        }, "Screen Initializer #2");
        y.setDaemon(true);
        y.start();

        Thread w = new Thread(() -> {
            new GameOverScreen();
            new AimSelectionScreen();
        }, "Screen Initializer #3");
        w.setDaemon(true);
        w.start();

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
        }, "Screen Initializer #4");
        z.setDaemon(true);
        z.start();

        try {
            x.join();
            y.join();
            w.join();
            z.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        screenManager.setActiveScreen(new PreWindowScreen());
	}

	/**
	 * Buffering
	 */
	public void createBufferStrategy() {
		createBufferStrategy(2);
		strat = getBufferStrategy();
	}

	/**
	 * Einstellung, wenn 'GameWindow' in Main initialisiert wird.
	 */
	public synchronized static void adjustWindow(GameWindow window) {
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Toolkit tk = Toolkit.getDefaultToolkit();  
	    int xSize = ((int) tk.getScreenSize().getWidth());  
	    int ySize = ((int) tk.getScreenSize().getHeight());  
		window.setSize(xSize, ySize);
		window.setExtendedState(Frame.MAXIMIZED_BOTH);
		window.setUndecorated(true);
		window.setResizable(false);
		window.setLocationRelativeTo(null);

        // adding the icon on the upper right corner or on the task bar.
        BufferedImage windowIcon = null;
        try {
            windowIcon = ImageIO.read(GameWindow.class.getClassLoader().getResourceAsStream(
                    "resources/gfx/comp/windowIcon.png"));
        } catch (IOException e) { e.printStackTrace(); }
        if (windowIcon != null)
            window.setIconImage(windowIcon);
	}

	/**
	 * Initialiseriet erst die notwendigen Eingstellungen und ruft dann die
	 * 'draw'-Methode auf
	 */
	public void update() {
		Keys.updateKeys();
	}

	public void draw() {
		Graphics2D g = (Graphics2D) strat.getDrawGraphics();

		// set some properties for the graphics object
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setColor(Color.black);
		g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());

		screenManager.draw(g);

		if(Main.isDebug() && grid.isActivated()) {
			grid.draw(g);
		}

		g.dispose();
		strat.show();
	}

	public CoordinateGrid getGrid() {
		return grid;
	}

	public Vector2 getCenterPosition() {
		return new Vector2(getWidth() / 2, getHeight() / 2);
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

