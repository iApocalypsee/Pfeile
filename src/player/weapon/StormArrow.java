package player.weapon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/** <b> Sturmpfeil </b> 
 * 
 * @version 25.11.2013
 */
public class StormArrow extends AbstractArrow {
	/** Index des Sturmpfeils */
	public static final int INDEX = 2;
	
	/** Name des Pfeils */
	public static final String NAME = "Sturmpfeil";
	
	public static final Color UNIFIED_COLOR = new Color(141, 237, 195);
	
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
		super(51f, 38f, 30, 0.0575f, 0.01f, 0.6f, 0.08f, 3.3, 10f, "Sturmpfeil");
	}
	
	/** Gibt das Bild vom Sturmpfeil zur�ck 
	 * @see <code> ArrowHelper.getArrowImage(int selectedIndex) </code> */
	@Override
	public BufferedImage getImage () {
		return img_Storm;
	}
}
