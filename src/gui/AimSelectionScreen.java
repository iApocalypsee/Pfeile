package gui;

import general.Field;
import general.Main;
import general.World;

import java.awt.Color;
import java.awt.Graphics2D;

public class AimSelectionScreen extends Screen {

	public static final String SCREEN_NAME = "AimSelection";
	
	public static final int SCREEN_INDEX = 4;
	
	/** Background Color, if it need to be Transparent: 185/255 is black */
	private static final Color TRANSPARENT_BACKGROUND = new Color(0, 0, 0, 185);
	
	
	public AimSelectionScreen() {
		super (SCREEN_NAME, SCREEN_INDEX);
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
		
		// The World will be drawn drawed
		GameScreen.getInstance().getWorld().drawReduced(g);
		
		
	}
}
