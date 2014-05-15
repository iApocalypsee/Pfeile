package gui;

import general.Main;
import general.World;
import general.field.Field;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import comp.Button;

public class AimSelectionScreen extends Screen {

	public static final String SCREEN_NAME = "AimSelection";
	
	public static final int SCREEN_INDEX = 4;
	
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
	
	/** Konstrucktor von AimSelectionScreen: ruft super(...) auf und setzt nur die Variabelnwerte nach der Initialisierung; */
	public AimSelectionScreen() {
		super (SCREEN_NAME, SCREEN_INDEX);
		
		posX_selectedField = -1;
		posY_selectedField = -1;
		
		isRunning = false;
		
		confirm = new Button (Main.getWindowWidth() - 300, Main.getWindowHeight() - 200, this, "Confirm");
		
		
		FieldSelector x = new FieldSelector ();
		selectFieldThread = new Thread (x);
		selectFieldThread.setDaemon(true);
		selectFieldThread.setPriority(Thread.NORM_PRIORITY - 2);
		x.start();
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
	
	private class FieldSelector extends Thread {

		@Override
		public void run() {
			while (true) {
				while (AimSelectionScreen.isRunningThread()) {
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {e.printStackTrace();}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}
	
	public static boolean isRunningThread () {
		return isRunning;
	}
}
