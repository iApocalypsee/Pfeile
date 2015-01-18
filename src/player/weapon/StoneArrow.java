package player.weapon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/** <b> Steinpfeil </b> 
 * 
 * @version 25.11.2013
 */
public class StoneArrow extends AbstractArrow {
	/** Index des Steinpfeils */
	public static final int INDEX = 3;
	
	/** Name des Pfeils */
	public static final String NAME = "Steinpfeil";
	
	public static final Color UNIFIED_COLOR = new Color(142, 120, 93, 200);
	
	
	/** Bild des Pfeils */
	private static BufferedImage img_Stone;
	/** Laden des Bildes */
	static {
		try {
			img_Stone = ImageIO.read(StoneArrow.class.getClassLoader().getResourceAsStream (
					"resources/gfx/arrow textures/stoneArrow.png"));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	
	/** ruft den Konstrucktor von 'AbstractArrow' auf */
	public StoneArrow() {
		super(59f, 41f, 12, 0.0075f, 0.025f, 0.14f, 0.04f, 5, 3.5f, "Steinpfeil");
	}
	
	/** Gibt das Bild des Steinpfeiles zur�ck 
	 * @see <code> ArrowHelper.getArrowImage(int selectedIndex) </code> */
	@Override
	public BufferedImage getImage () {
		return img_Stone;
	}
}
