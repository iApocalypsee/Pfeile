package gui;

import entity.Player;
import general.Main;
import general.Mechanics;
import general.World;
import general.field.Field;
import comp.Button;
import comp.Component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import world.BaseTile;
import world.IWorld;


public class AimSelectionScreen extends Screen {
	
	public static final String SCREEN_NAME = "AimSelection";
	
	public static final int SCREEN_INDEX = 4;
	
	/** Background Color, if it need to be Transparent: 185/255 is black */
	private static final Color TRANSPARENT_BACKGROUND = new Color(0, 0, 0, 185);

	/** X-Position des Ausgewählten Feldes 
	 * * (wenn noch nie auf <code> AimSelectionScreen </code> gecklickt wurde, ist der Wert -1)*/
	private volatile int posX_selectedField;
	
	
	
	/** Y-Position des Ausgewählten Feldes 
	 * (wenn noch nie auf <code> AimSelectionScreen </code> gecklickt wurde, ist der Wert -1) */
	private volatile int posY_selectedField;
	
	/** Bestätigen-Button */
	private Button confirm;
	
	/** läuft der Thread noch */
	private static boolean isRunning;
	/** this is the actual thread running from an instance of FieldSelector */
	private Thread selectFieldThread; 
	/** This is the static-Instance of FieldSelector, which is served by the Thread selectedFieldThread */
	private static FieldSelector x;
	
	// These are only for the warning Message
	private String warningMessage = "";
	private float transparencyWarningMessage = 0;
	private Point positionWarningMessage = new Point (40, Main.getWindowHeight() - 105);
	
	/** Konstrucktor von AimSelectionScreen: ruft super(...) auf und setzt die Variabelnwerte nach der Initialisierung; start den thread of <code> FieldSelector </code> */
	public AimSelectionScreen() {
		super (SCREEN_NAME, SCREEN_INDEX);
		
		setPosX_selectedField(-1);
		setPosY_selectedField(-1);
		isRunning = false;
		
		confirm = new Button (1178, 491, this, "Bestätigen");
		
		
		// MouseListener für confirm-Button
		confirm.addMouseListener ( new MouseAdapter () {
			@Override
			public void mouseReleased (MouseEvent e) {
				if (confirm.getSimplifiedBounds().contains(e.getPoint())) {
					if (posX_selectedField == -1|| posY_selectedField == -1) {
						warningMessage = "Kein Zielfeld ausgewählt";
						transparencyWarningMessage = 1f;
					} else {
						//TODO: Den Screen richtig verlassen
						//onLeavingScreen(this, NewWorldTestScreen$.MODULE$);
						System.err.println("'onLeavingScreen' in 'AimSelectionScreen.confirm.mouseReleased' is not implemented yet.");
						// remove, if the "TODO" is done
						warningMessage = "NOT IMPLEMENTED";
						transparencyWarningMessage = 1f;
					}
				}
			}
		});
		confirm.setRoundBorder(true);
		confirm.setVisible(true);
		confirm.acceptInput();
		
		x = new FieldSelector ();
		selectFieldThread = new Thread (x);
		selectFieldThread.setDaemon(true);
		selectFieldThread.setPriority(Thread.MIN_PRIORITY + 3);
		selectFieldThread.start();
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		super.keyPressed(arg0);
		NewWorldTestScreen.keyPressed(arg0);
	}

	@Override 
	public void draw (Graphics2D g) {
		// Background will be drawn
		super.draw(g);
		
		// include the world
		IWorld w = NewWorldTestScreen.getWorld();
		
		// The World will be drawn 
		w.draw(g);
		
		for (entity.Player player: w.getPlayers()) {
			player.draw(g);
		}
		
		// draw the selectedField 
		if (posX_selectedField >= 0 && posY_selectedField >= 0) {
			g.setColor(new Color (255, 227, 227, 250));
			g.drawPolygon(((BaseTile) (NewWorldTestScreen.getWorld().getTileAt(posX_selectedField, posY_selectedField))).getBounds());
			g.setColor(new Color (240, 37, 47, (int) (255 * 0.7)));
			g.fillPolygon(((BaseTile) (NewWorldTestScreen.getWorld().getTileAt(posX_selectedField, posY_selectedField))).getBounds());
		}
		
		// TODO: auf die neue World-Klasse ändern
		World.timeLifeBox.draw(g);
		Field.infoBox.draw(g);
		Main.timeObj.draw(g);
		GameScreen.getInstance().getWorld().getActivePlayer().drawLife(g);
		
		confirm.draw(g);
		
		// Finally, draw the waringMessage
		g.setColor(new Color(1f, 0f, 0f, transparencyWarningMessage));
		g.setFont(new Font(Component.STD_FONT.getFontName(), Font.BOLD, 26));
		g.drawString(warningMessage, positionWarningMessage.x, positionWarningMessage.y);
		
		transparencyWarningMessage = transparencyWarningMessage - 0.013f;
		
		if (transparencyWarningMessage < 0) 
			transparencyWarningMessage = 0;
	}
	
	
	// ###############
	// GETTER & SETTER
	// ###############
	
	/** Getter
	 * @return posX_selectedField: It's the X-Position of the selected Field 
	 * @see getPosY_selectedField
	 */
	public int getPosX_selectedField() {
		return posX_selectedField;
	}

	/** Setter
	 * @param posX_selectedField: the posX_selectedField to set
	 * @see setPosY_selectedField
	 */
	private void setPosX_selectedField(int posX) {
		this.posX_selectedField = posX;
	}

	/** Getter
	 * @return posY_selectedField: The Y-Position of the selected Field 
	 * @see getPosX_selectedField
	 */
	public int getPosY_selectedField() {
		return posY_selectedField;
	}

	/** Setter
	 * @param posY_selectedField: the posY_selectedField to set
	 * @see setPosX_selectedField
	 */
	private void setPosY_selectedField(int posY) {
		this.posY_selectedField = posY;
	}

	
	// #######
	// THREADS
	// #######
	
	/** Thread for testing, if there was a click and at which field it has been set */
	private class FieldSelector implements Runnable {
		
		/** point, describing the last click */
		private Point lastSavedClickPosition = getLastClickPosition();
		
		void setSavedClickPosition (Point lastSavedClickPosition) {
			this.lastSavedClickPosition = lastSavedClickPosition;
		}
		
		
		// Finally: let's run the Thread 
		@Override
		public void run() {
			
			// this, is instead of wait and notify 
			// TODO use wait & notify
			while (true) {
				
				// Let's start the testing loop
				while (AimSelectionScreen.isRunningThread()) {
					
					// only run, if there was another click
					if (lastSavedClickPosition.x == getLastClickPosition().x && 
							lastSavedClickPosition.y == getLastClickPosition().y) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {e.printStackTrace();}
						
						continue;
					}
					
					// This is the world
					IWorld w = NewWorldTestScreen.getWorld();
					
					// TODO: This should just contain the visible World, nothing more
					// no invisible tiles should be selected
					// setup des Polygons, der die gesamte Welt umfasst
					Polygon poly = new Polygon();
					poly.xpoints = new int[4];
					poly.ypoints = new int[4];
					
					poly.xpoints[0] = ((int) ((BaseTile) w.getTileAt(0, 0))._gridElem().westCorner().getX());
					poly.xpoints[1] = ((int) ((BaseTile) w.getTileAt(0, w.getSizeY() - 1))._gridElem().northCorner().getX());
					poly.xpoints[2] = ((int) ((BaseTile) w.getTileAt(w.getSizeX() - 1, w.getSizeY() - 1))._gridElem().eastCorner().getX());
					poly.xpoints[3] = ((int) ((BaseTile) w.getTileAt(w.getSizeX() - 1, 0))._gridElem().southCorner().getX());
					
					poly.ypoints[0] = ((int) ((BaseTile) w.getTileAt(0, 0))._gridElem().westCorner().getY());
					poly.ypoints[1] = ((int) ((BaseTile) w.getTileAt(0, w.getSizeY() - 1))._gridElem().northCorner().getY());
					poly.ypoints[2] = ((int) ((BaseTile) w.getTileAt(w.getSizeX() - 1, w.getSizeY() - 1))._gridElem().eastCorner().getY());
					poly.ypoints[3] = ((int) ((BaseTile) w.getTileAt(w.getSizeX() - 1, 0))._gridElem().southCorner().getY());
					
					poly.npoints = 4;
					poly.invalidate();
					
					// only run, if the position of the new click is on the map 
					if (poly.contains(getLastClickPosition())) {
						
						// Let's find the selectedField
						LOOPxPosition: for (int x = 0; x < w.getSizeX(); x++) {
							for (int y = 0; y < w.getSizeY(); y++) {
								BaseTile tile = (BaseTile) w.getTileAt(x, y);
								if (tile.getBounds().contains(getLastClickPosition())) {
									setPosX_selectedField(x);
									setPosY_selectedField(y);
									lastSavedClickPosition = getLastClickPosition();
									
									break LOOPxPosition;
								}
							}
						} 
					}
					
					// now, sleep a bit 
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {e.printStackTrace();}
				}
				
				// if 'isRunning == false': sleep longer
				try {
					Thread.sleep(480);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}
	
	/** is the Thread of <code> FieldSelector </code> still running?
	 * @return isRunning - 
	 * 			should be true, as long as <code> AimSelectionScreen </code> is active. (If it isn't: use <code> AimSelectionScreen.setRunningThread(true)</code>)
	 * @see AimSelectionScreen.setRunningThread */
	public static boolean isRunningThread () {
		return isRunning;
	}
	
	/** should the Thread of <code> FieldSelector </code> run? ||
	 * 
	 * set(false): if <code> AimSelectionScreen </code> isn't active;
	 * set(true): if <code> AimSelectionScreen </code> is active;
	 * 
	 * @param isRunningFieldSelector
	 * @return 
	 */
	public static void setRunningThread (boolean isRunningFieldSelector) {
		isRunning = isRunningFieldSelector;
		if (isRunning = true) {
			x.setSavedClickPosition(getLastClickPosition());
		}
	}
}
