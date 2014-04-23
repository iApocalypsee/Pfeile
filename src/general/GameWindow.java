package general;

import gui.ArrowSelectionScreen;
import gui.Field;
import gui.GameScreen;
import gui.MainMenuScreen;
import gui.PauseScreen;
import gui.ScreenManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * <b>4.1.2014 (Josip):</b> Einfachere Initialisierung der Screens. <br>
 * <b>10.1.2014 (Josip):</b> Verschiebung von GameWindow ins Package "general"
 * <b>24.1.2014 (Josip):</b> Entfernung von <code>readyToShow</code>, unnötige Variable
 * 
 * @version 10.1.2014
 *
 */
public class GameWindow extends JFrame {
	
	private static final long serialVersionUID = 7012286076598906440L;
	
	final GraphicsDevice device;
	private BufferStrategy strat;
	
	private ScreenManager screenManager;

    public static final Rectangle STD_WND_RECT = new Rectangle(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
	
	/**
	 * Konstrucktor: Layout = null; KeyListener: Keys
	 * 
	 * @param timeObj
	 */
	public GameWindow(GraphicsDevice device, TimeClock timeObj) {
		super("Pfeile");
		setLayout(null);

		this.device = device;
		
		screenManager = new ScreenManager();
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				screenManager.getActiveScreen().mouseReleased(arg0);
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				screenManager.getActiveScreen().mousePressed(arg0);
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				screenManager.getActiveScreen().mouseMoved(e);
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				screenManager.getActiveScreen().mouseDragged(e);
				
			}
		});
		
		addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				screenManager.getActiveScreen().mouseWheelMoved(e);
			}
			
		});

		addKeyListener(new Keys());
	}
	
	/**
	 * Initialisiert alle Screens. Hier kommen die Konstruktoraufrufe der einzelnen Screens
	 * rein. <b>Darf nicht im Konstruktor von GameWindow selbst aufgerufen werden.</b>
	 */
	void initializeScreens() {
		new MainMenuScreen();
		GameScreen.getInstance();
		new PauseScreen();
		new ArrowSelectionScreen();
		screenManager.setActiveScreen(MainMenuScreen.SCREEN_INDEX);
	}

	/** Buffering */
	public void createBufferStrategy() {
		createBufferStrategy(3);
		strat = getBufferStrategy();
	}

	/** Einstellung, wenn 'GameWindow' in Main initialiert wird */
	public synchronized static void createWindow(int width, int heigth,
			GameWindow window) {
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setSize(width, heigth);
		window.setUndecorated(true);
		window.setAlwaysOnTop(false);
		window.setResizable(true);
		window.setLocationRelativeTo(null);
	}

	/**
	 * Initialiseriet erst die notwendigen Eingstellungen und ruft dann die
	 * 'draw'-Methode auf
	 */
	public void update() {
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
	
	/** Zeichnet die Felder, allerdings in dem Zustand, wie sie sind, wenn sie nicht sichtbar sind (grau)
     * @deprecated Alte Field-Klasse wird hier benutzt.
     */
    @Deprecated
	private void drawFieldGray(Graphics2D g) {
		for (int i = 0; i < Field.fieldsList.size(); i++) {
			g.drawImage(Field.getImage(Field.INDEX_IMAGE_TEXTURENOTFOUND), 
					Field.fieldsList.get(i).getPosXscreen(), Field.fieldsList.get(i).getPosYscreen(), 
					Math.round(Field.getImage(Field.INDEX_IMAGE_NOTVISIBLEFIELD).getWidth() * Mechanics.widthStretching), 
					Math.round(Field.getImage(Field.INDEX_IMAGE_NOTVISIBLEFIELD).getHeight() * Mechanics.heightStretching), null);
			
		}
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
		
		drawEndSequenceDied(g2D);

		g2D.dispose();
		strat.show();
	}

	/** Zeichnet EndSequence - Aufruf i.d.R. durch 'endSequence()' */
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

