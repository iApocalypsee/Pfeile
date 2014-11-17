package player.weapon;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/** <b> Wasserpfeil </b> 
 * 
 * @version 25.11.2013
 */
public class WaterArrow extends AbstractArrow {
	/** Index des Wasserpfeils */
	public static final int INDEX = 1;
	
	/** Name des Pfeils */
	public static final String NAME = "Wasserpfeil";
	
	public static final Color UNIFIED_COLOR = new Color(60, 166, 221, 200);
	
	
	/** Bild des Pfeils */
	private static BufferedImage img_Water;
	/** Laden des Bildes */
	static {
		try {
			img_Water = ImageIO.read(WaterArrow.class.getClassLoader().getResourceAsStream (
					"resources/gfx/arrow textures/waterArrow.png"));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	
	/** ruft den Konstrucktor von 'AbstractArrow' auf */
	public WaterArrow() {
		
		super(38f, 34f, 600, 0.0125f, 0.0125f, 0.065f, 0.13f, 3.1, "Wasserpfeil");
		
	}
	
	/** Gibt das Bild des Wasserpfeiles zur�ck 
	 * @see <code> ArrowHelper.getArrowImage(int selectedIndex) </code> */
	@Override
	public BufferedImage getImage () {
		return img_Water;
	}
}
