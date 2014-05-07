package player;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/** <b> Schattenpfeil </b>  
 * 
 * @version 25.11.2013
 */
public class ShadowArrow extends AbstractArrow {
	/** Index des Schattenpfeils */
	public static final int INDEX = 7;
	
	/** Name des Pfeils */
	public static final String NAME = "Schattenpfeil";
	
	public static final Color UNIFIED_COLOR = new Color(74, 74, 50, 75);
	
	/** Bild des Pfeils */
	private static BufferedImage img_Shadow;
	/** Laden des Bildes */
	static {
		try {
			img_Shadow = ImageIO.read(ShadowArrow.class.getClassLoader().getResourceAsStream (
									"resources/gfx/arrow textures/shadowArrow.png"));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	
	/** ruft den Konstrucktor von 'AbstractArrow' auf */
	public ShadowArrow() {
		
		super(39f, 25f, 1200, 0.0675f, 0.005f, 0.085f, 0.03f, "Schattenpfeil");
		
	}
	
	/** Gibt das Bild des Schattenpfeils zur�ck */
	public static BufferedImage getImage () {
		return img_Shadow;
	}
}
