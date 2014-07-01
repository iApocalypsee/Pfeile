package player.weapon;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/** 
 * <b> Feuerpfeil </b>
 * 
 * @version 25.11.2013
 */
public class FireArrow extends AbstractArrow {
	/** Index des Feuerpfeils */
	public static final int INDEX = 0;
	
	/** Name des Pfeils: */
	public static final String NAME = "Feuerpfeil";
	
	public static final Color UNIFIED_COLOR = new Color(255, 0, 0, 75);
	
	/** Bild des Pfeils */
	private static BufferedImage img_Fire;
	/** Laden des Bildes */
	static {
		try {
			img_Fire = ImageIO.read(FireArrow.class.getClassLoader().getResourceAsStream (
					"resources/gfx/arrow textures/fireArrow.png"));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	
	/** ruft den Konstruktor von 'AbstractArrow' auf */
	public FireArrow() {
		
		super(57f, 23f, 800, 0.045f, 0.02f, 0.095f, 0.10f, "Feuerpfeil");
		
	}
	
	/** Gibt das Bild des Feuerpfeiles zur�ck 
	 * @see <code> ArrowHelper.getArrowImage(int selectedIndex) </code> */
	@Override
	public BufferedImage getImage () {
		return img_Fire;
	}
}
