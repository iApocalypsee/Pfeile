package general;

import gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferStrategy;

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

    public static final Rectangle STD_WND_RECT = new Rectangle(0, 0, Main.getWindowWidth(), Main.getWindowHeight());

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
	void initializeScreens() {
		new MainMenuScreen();
		new PauseScreen();
		new AimSelectionScreen();
		GameScreen.getInstance();
		ArrowSelectionScreen.getInstance();
		// NewWorldTestScreen$.MODULE$;
		//screenManager.setActiveScreen(GameScreen.SCREEN_INDEX);
		screenManager.setActiveScreen(NewWorldTestScreen$.MODULE$);
	}

	/**
	 * Buffering
	 */
	public void createBufferStrategy() {
		createBufferStrategy(3);
		strat = getBufferStrategy();
	}

	/**
	 * Einstellung, wenn 'GameWindow' in Main initialisiert wird.
	 */
	public synchronized static void adjustWindow(int width, int heigth, GameWindow window) {
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setSize(width, heigth);
		window.setUndecorated(true);
		window.setResizable(true);
		window.setLocationRelativeTo(null);
		window.setExtendedState(Frame.MAXIMIZED_BOTH);
	}

	/**
	 * Initialiseriet erst die notwendigen Eingstellungen und ruft dann die
	 * 'draw'-Methode auf
	 */
	public void update() {
		Keys.updateKeys();
		// TODO Very important, insert game code update calls here!
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

		g.dispose();
		strat.show();
	}

	/**
	 * Hauptbereich für EndSequence --> Initialisierungen, dann Aufruf der
	 * Zeichenmethoden für die eigentliche Darstellung
	 * TODO Muss auf andere Screens ausgelagert werden
	 */
	@Deprecated
	public void endSequenceDied() {
		Graphics g = strat.getDrawGraphics();
		Graphics2D g2D = (Graphics2D) g;

		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);


		//drawFieldGray(g2D);

		drawEndSequenceDied(g2D);

		g2D.dispose();
		strat.show();
	}

	/**
	 * Zeichnet EndSequence - Aufruf i.d.R. durch 'endSequence()'
	 */
	private void drawEndSequenceDied(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());

		g.setColor(Color.RED);
		Font endSequenceFont = new Font("18thCentury", Font.BOLD, 150);
		g.setFont(endSequenceFont);
		g.drawString("DIED", Main.getWindowWidth() / 2 - 100,
				Main.getWindowHeight() / 2 - 10);
	}

	public void endSequenceWon() {
		Graphics g = strat.getDrawGraphics();
		Graphics2D g2D = (Graphics2D) g;

		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		//drawFieldGray(g2D);

		drawEndSequenceWon(g2D);

		g2D.dispose();
		strat.show();
	}

	private void drawEndSequenceWon(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());

		g.setColor(Color.RED);
		Font endSequenceFont = new Font("18thCentury", Font.BOLD, 150);
		g.setFont(endSequenceFont);
		g.drawString("WON", Main.getWindowWidth() / 2 - 100,
				Main.getWindowHeight() / 2 - 10);

	}


	// ANDERE METHODEN ############################################

	/**
	 * @return the screenManager
	 */
	public ScreenManager getScreenManager() {
		return screenManager;
	}
}

