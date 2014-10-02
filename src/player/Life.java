package player;

import general.Main;
import general.Mechanics;
import gui.Drawable;

import java.awt.*;

/**
 * <b>1.1.2014:</b> Lebensklasse entschlackt.
 * @version 1.1.2014
 */
public class Life implements Drawable {
	private int lifemax;
	private int liferegen;
	private int life;
	private int relativeLife = 100;
	private Rectangle boundingLife;;
	private int PosX;
	private int PosY;
	
	
	
	/** KONSTRUCKTOR: zuerst allgemein: bei 'type = TYPE_PLAYER': Initialieserung für speziel für Spieler 
	 * @version 1.2 */
	public Life () {
		
		lifemax = Mechanics.lifeMax;
		liferegen = Mechanics.lifeRegeneration;
		life = lifemax;
        
		PosX = Main.getWindowWidth() - (55 + 125);
		PosY = Main.getWindowHeight() - 80;
		boundingLife = new Rectangle(PosX, PosY, Math.round(relativeLife * 1.25f), 14);
	}
	
	
	/** nach jeden Zug aufrufen, um die neuen Werte für Leben und Darstellung des Leben zu berechnen */ 
	public void updateLife() {
		life = life + liferegen; 
		if (life > lifemax) 
			life = lifemax;
		relativeLife = Math.round((life * 100) / lifemax );
		boundingLife.width = Math.round(relativeLife * 1.25f); 
	}
	
	/** GETTER: gibt das Rectangle 'boundingLife' zurück; z.B. für GUI-Darstellung notwendig (v.a. da auf ein BufferedImage verzichtet wurde) */
	public Rectangle getBoundingLife () {
		return boundingLife; 
	}
	/** GETTER: return 'life */
	public int getLife() {
		return life; 
	}
	/** GETTER: return 'LIFEMAX' */
	public int getMaxLife() {
		return lifemax;
	}
	/** GETTER: return 'relativeLife' */
	public int getRelativeLife() {
		return relativeLife;
	}
	/** SETTER: set 'life' */
	public void setLife(int newLife) {
		this.life = newLife; 
		this.relativeLife = (this.life / this.lifemax) * 100;
		this.boundingLife.width = Math.round(relativeLife * 1.25f);
	}


    @Override
    public void draw (Graphics2D g) {
        // Lebensleiste
        // Rechteck + Hintergrund für Lebensleiste
        g.setColor(Color.RED);
        g.drawRect(boundingLife.x - 1, boundingLife.y - 1,
                125 + 1, boundingLife.height + 1);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(boundingLife.x, boundingLife.y, 125,
                boundingLife.height);
        // eigentliche Lebensleiste
        if (life > 0) {
            Color currentLifeColor = new Color(250 - getRelativeLife(),
                    Math.round(getRelativeLife() * 2.5f), 0);
            g.setColor(currentLifeColor);
            g.fillRect(boundingLife.x, boundingLife.y,
                    boundingLife.width, boundingLife.height);
            g.draw(boundingLife);
        }
        // Prozentanzeige für das Leben
        int percentPosX = boundingLife.x + 127 - 36;
        int percentPosY = boundingLife.y + 40;
        // Hintergrund
        g.setColor(Color.BLACK);
        g.drawRect(percentPosX - 6, percentPosY - 15, 44, 19);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(percentPosX - 5, percentPosY - 14, 43, 18);
        // Prozent
        g.setColor(Color.WHITE);
        g.setFont(comp.Component.STD_FONT);
        if (getRelativeLife() >= 10)
            g.drawString(getRelativeLife() + "%", percentPosX, percentPosY);
        else
            g.drawString(getRelativeLife() + " %", percentPosX,
                    percentPosY);
        // Leben / LebenMax - Anzeige
        // Variablen
        int lifePosX = getBoundingLife().x + 4;
        int lifePosY = getBoundingLife().y + 40;
        // Hintergrund
        g.setColor(Color.BLACK);
        g.drawRect(lifePosX - 4, lifePosY - 15, 81, 19);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(lifePosX - 3, lifePosY - 14, 80, 18);
        // Prozent
        g.setColor(Color.WHITE);
        if (getLife() >= 100)
            g.drawString(getLife() + " / " + getMaxLife(), lifePosX,
                    lifePosY);
        else if (getLife() >= 10)
            g.drawString(" " + getLife() + " / " + getMaxLife(),
                    lifePosX, lifePosY);
        else
            g.drawString(" " + getLife() + " / " + getMaxLife(),
                    lifePosX, lifePosY);
    }
}
