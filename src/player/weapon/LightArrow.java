package player.weapon;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/** <b> Lichtpfeil </b>
 * 
 * @version 25.11.2013
 */
public class LightArrow extends AbstractArrow {
	/** Index des Lichtpfeils */
	public static final int INDEX = 6;
	
	public static final String NAME = "Lichtpfeil";
	
	public static final Color UNIFIED_COLOR = new Color(227, 200, 232, 75);
	
	/** Bild des Pfeils */
	private static BufferedImage img_Light;
	/** Laden des Bildes */
	static {
		try {
			img_Light = ImageIO.read(LightArrow.class.getClassLoader().getResourceAsStream (
					"resources/gfx/arrow textures/lightArrow.png"));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	
	/** ruft den Konstrucktor von 'AbstractArrow' auf */
	public LightArrow() {
		
		super(29f, 30f, 1200, 0.0025f, 0.005f, 0.10f, 0.02f, "Lichtpfeil");
		
	}
	
	/** Gibt das Bild vom Lichtpfeil zur�ck
	 * @see <code> ArrowHelper.getArrowImage(int selectedIndex) </code>  */
	@Override
	public BufferedImage getImage () {
		return img_Light;
	}
}
