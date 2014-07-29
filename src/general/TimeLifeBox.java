package general;

import gui.GameScreen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/** Box um Leben und Uhr */
public class TimeLifeBox {
	private final Color fillColor = new Color (150, 145, 145, 255); 
	private final Color borderColor = new Color(20, 20, 40, 255);
	private int width; 
	private int posX;
	private int posY; 
	private int height; 
	private int line;
	private boolean isVisible; 
	
	TimeLifeBox () {
		line = 6;
		isVisible = true; 
	}
	
	void initNewPosition () {
		width = GameScreen.getInstance().getWorld().getActivePlayer().getLife().getBoundingLife().width + 50;
		height = GameScreen.getInstance().getWorld().getActivePlayer().getLife().getBoundingLife().height + 105;
		posX = GameScreen.getInstance().getWorld().getActivePlayer().getLife().getBoundingLife().x - 20; 
		posY = Main.timeObj.getY() - 15;
	}
	
	public void draw(Graphics2D g) {
		if (isVisible == true) {
			g.setColor(borderColor);
			g.fillRect(posX, posY, width, height);
			g.setColor(fillColor);
			g.fillRect(posX + line, posY + line, width -  2* line, height - 2* line);
		}
	}
	
	/** Gibt ein Rectangle_Object zurück, dass alle wichtigen Information über Position und Größe enthählt */
	public Rectangle getBoundingBox () {
		return new Rectangle (posX, posY, width, height); 
	}
	
	public boolean isVisible () {
		return isVisible; 
	}
	
	public void setVisible (boolean isVisible) {
		this.isVisible = isVisible; 
	}
}
