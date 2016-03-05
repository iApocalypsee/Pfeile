package player.weapon.arrow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/** <b> Schattenpfeil </b>  
 * 
 * @version 25.11.2013
 */
public class ShadowArrow extends AbstractArrow {
	/** Index des Schattenpfeils */
	public static final int INDEX = 7;
	
	/** Name des Pfeils */
	public static final String NAME = "ShadowArrow", LANG_IDENT = "item/arrow/shadow";
	
	public static final Color UNIFIED_COLOR = new Color(0, 0, 0);
	
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
		super(57f, 25f, 32, 0.0675f, 0.005f, 0.085f, 0.03f, 6.5, 5.3f, NAME);
	}
	
	/** Gibt das Bild des Schattenpfeils zur�ck 
	 * @see <code> ArrowHelper.getArrowImage(int selectedIndex) </code> */
	@Override
	public BufferedImage getImage () {
		return img_Shadow;
	}

    @Override
    protected String getTranslationIdentifier() {
        return LANG_IDENT;
    }
}
