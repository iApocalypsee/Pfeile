package player.weapon.arrow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/** <b> Lichtpfeil </b>
 * 
 * @version 25.11.2013
 */
public class LightArrow extends AbstractArrow {
	/** Index des Lichtpfeils */
	public static final int INDEX = 6;
	
	public static final String NAME = "LightArrow", LANG_IDENT = "item/arrow/light";
	
	public static final Color UNIFIED_COLOR = new Color(255, 253, 193);
	
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
		super(42f, 32f, 34, 0.0025f, 0.005f, 0.10f, 0.02f, 6.5, 8.3f, NAME);
	}
	
	/** Gibt das Bild vom Lichtpfeil zur�ck
	 * @see <code> ArrowHelper.getArrowImage(int selectedIndex) </code>  */
	@Override
	public BufferedImage getImage () {
		return img_Light;
	}


    @Override
    protected String getTranslationIdentifier() {
        return LANG_IDENT;
    }
}
