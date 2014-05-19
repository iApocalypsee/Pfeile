package gui;

import general.Main;
import general.Mechanics;
import general.World;
import general.field.Field;
import comp.Button;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import sun.security.jgss.spi.MechanismFactory;


public class AimSelectionScreen extends Screen {

	public static final String SCREEN_NAME = "AimSelection";
	
	public static final int SCREEN_INDEX = 4;
	
	/** save the reference to world, for a faster access */
	private static World world = GameScreen.getInstance().getWorld();
	
	/** Background Color, if it need to be Transparent: 185/255 is black */
	private static final Color TRANSPARENT_BACKGROUND = new Color(0, 0, 0, 185);

	/** X-Position des Ausgewählten Feldes 
	 * * (wenn noch nie auf <code> AimSelectionScreen </code> gecklickt wurde, ist der Wert -1)*/
	private int posX_selectedField;
	
	private static boolean isRunning;
	
	/** Y-Position des Ausgewählten Feldes 
	 * (wenn noch nie auf <code> AimSelectionScreen </code> gecklickt wurde, ist der Wert -1) */
	private int posY_selectedField;
	
	private Button confirm;
	
	private Thread selectFieldThread; 
	
	/** Konstrucktor von AimSelectionScreen: ruft super(...) auf und setzt die Variabelnwerte nach der Initialisierung; start den thread of <code> FieldSelector </code> */
	public AimSelectionScreen() {
		super (SCREEN_NAME, SCREEN_INDEX);
		
		setPosX_selectedField(-1);
		setPosY_selectedField(-1);
		setRunningThread(false);
		
		confirm = new Button (Main.getWindowWidth() - 300, Main.getWindowHeight() - 200, this, "Confirm");
		
		
		FieldSelector x = new FieldSelector ();
		selectFieldThread = new Thread (x);
		selectFieldThread.setDaemon(true);
		selectFieldThread.setPriority(Thread.MIN_PRIORITY + 1);
		selectFieldThread.start();
	}

	@Override 
	public void draw (Graphics2D g) {
		// Background will be drawn
		super.draw(g);
		
		World.timeLifeBox.draw(g);
		Field.infoBox.draw(g);
		Main.timeObj.draw(g);
		GameScreen.getInstance().getWorld().getActivePlayer().drawLife(g);
		
		g.setColor(TRANSPARENT_BACKGROUND);
		g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
		
		// The World will be drawn 
		GameScreen.getInstance().getWorld().drawReduced(g);
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
	public void setPosX_selectedField(int posX) {
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
	public void setPosY_selectedField(int posY) {
		this.posY_selectedField = posY;
	}

	
	// #######
	// THREADS
	// #######
	
	/** Thread for testing, if there was a click and at which field it has been set */
	private class FieldSelector implements Runnable {

		@Override
		public void run() {
			
			// point, describing the last click
			Point lastSavedClickPosition = getLastClickPosition();
			
			// this, is instead of wait and notify 
			// TODO use wait & notify
			while (true) {
				
				// Let's start the testing loop
				while (AimSelectionScreen.isRunningThread()) {
					
					// only run, if there was another click
					if (lastSavedClickPosition.x == getLastClickPosition().x && 
							lastSavedClickPosition.y == getLastClickPosition().y) {
						try {
							Thread.sleep(80);
						} catch (InterruptedException e) {e.printStackTrace();}
						
						continue;
					}
					
					// only run, if the position of the new click is on the map
					if (getLastClickPosition().x < world.getFieldAt(Mechanics.worldSizeX - 1, Mechanics.worldSizeY - 1).getAbsoluteX() + world.getFieldAt(Mechanics.worldSizeX - 1, Mechanics.worldSizeY - 1).getWidth() || 
							getLastClickPosition().y < world.getFieldAt(Mechanics.worldSizeX - 1, Mechanics.worldSizeY - 1).getAbsoluteY() + world.getFieldAt(Mechanics.worldSizeX - 1, Mechanics.worldSizeY - 1).getHeight()) {
						
						
						// Let's find the selectedField
						LOOPxPosition: for (int x = 0; x < Mechanics.worldSizeX; x++) {
							for (int y = 0; y < Mechanics.worldSizeY; y++) {
								if (world.getFieldAt(x, y).getSimplifiedBounds().contains(getLastClickPosition())) {
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
						Thread.sleep(80);
					} catch (InterruptedException e) {e.printStackTrace();}
				}
				
				// if 'isRunning == false': sleep longer
				try {
					Thread.sleep(400);
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
	 */
	public static void setRunningThread (boolean isRunningFieldSelector) {
		isRunning = isRunningFieldSelector;
	}
}
