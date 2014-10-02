package general;

import gui.Drawable;
import gui.GameScreen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/** Box um Leben und Uhr */
public class TimeLifeBox implements Drawable {
	private final Color fillColor = new Color (150, 145, 145, 255); 
	private final Color borderColor = new Color(20, 20, 40, 255);
	private int width; 
	private int posX;
	private int posY; 
	private int height; 
	private int line;
	private boolean isVisible; 
	
	TimeLifeBox (int x, int y, int width, int height) {
        posX = x;
        posY = y;
        this.width = width;
        this.height = height;

		line = 6;
		isVisible = true; 
	}

    @Override
    public void draw(Graphics2D g) {
        if (isVisible == true) {
            g.setColor(borderColor);
            g.fillRect(posX, posY, width, height);
            g.setColor(fillColor);
            g.fillRect(posX + line, posY + line, width - 2 * line, height - 2 * line);
        }
    }

	public boolean isVisible () {
		return isVisible; 
	}
	
	public void setVisible (boolean isVisible) {
		this.isVisible = isVisible; 
	}
}
