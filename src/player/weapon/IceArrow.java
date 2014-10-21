package player.weapon;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/** <b> Eispfeil </b> 
 * 
 * @version 25.11.2013
 */
public class IceArrow extends AbstractArrow {
	/** Index des Eispfeils */
	public static final int INDEX = 4;
	
	/** Name des Pfeils */
	public static final String NAME = "Eispfeil";
	
	public static final Color UNIFIED_COLOR = new Color(204, 228, 237, 200);
	
	
	/** Bild des Pfeils */
	private static BufferedImage img_Ice;
	/** Laden des Bildes */
	static {
		try {
			img_Ice = ImageIO.read(IceArrow.class.getClassLoader().getResourceAsStream (
					"resources/gfx/arrow textures/iceArrow.png"));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	
	/** ruft den Konstrucktor von 'AbstractArrow' auf */
	public IceArrow() {
		
		super(43f, 32f, 500, 0.035f, 0.025f, 0.13f, 0.06f, 5, "Eispfeil");
		
	}
	
	/** Gibt das Bild des Eispfeiles zur�ck 
	 * @see <code> ArrowHelper.getArrowImage(int selectedIndex) </code> */
	@Override
	public BufferedImage getImage () {
		return img_Ice;
	}
}
