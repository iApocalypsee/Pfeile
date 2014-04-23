package player;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/** <b> Sturmpfeil </b> 
 * 
 * @version 25.11.2013
 */
public class StormArrow extends AbstractArrow {
	/** Index des Sturmpfeils */
	public static final int index = 2;
	
	/** Name des Pfeils */
	public static final String name = "Sturmpfeil";
	
	public static final Color UNIFIED_COLOR = new Color(102, 237, 199, 75);
	
	
	/** Bild des Pfeils */
	private static BufferedImage img_Storm;
	/** Laden des Bildes */
	static {
		try {
			img_Storm = ImageIO.read(StormArrow.class.getClassLoader().getResourceAsStream (
									"resources/gfx/arrow textures/stormArrow.png"));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	
	/** ruft den Konstrucktor von 'AbstractArrow' auf */
	public StormArrow() {
		
		super(36f, 36f, 1000, 0.0575f, 0.01f, 0.6f, 0.08f, "Sturmpfeil");
		
	}
	
	/** Gibt das Bild vom Sturmpfeil zur�ck */
	public BufferedImage getImage () {
		return img_Storm;
	}
}
