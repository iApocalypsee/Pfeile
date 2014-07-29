package player;

import general.Main;
import general.Mechanics;

import java.awt.Rectangle;

/**
 * <b>1.1.2014:</b> Lebensklasse entschlackt.
 * @version 1.1.2014
 */
public class Life {
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
		
		this.lifemax = Mechanics.lifeMax;
		this.liferegen = Mechanics.lifeRegeneration; 
		life = lifemax;
		
		/* Konstrucktoraufruf für den Player */
//		if(!com.github.pfeile.player.isBot()) {
//			// INITIALISIERUNG
//
//			PosX = Main.getWindowWidth() - (55 + 125);
//			PosY = Main.getWindowHeight() - 80;
//
//			boundingLife = new Rectangle(PosX, PosY, Math.round(relativeLife * 1.25f), 14);
//		}
		
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
}
