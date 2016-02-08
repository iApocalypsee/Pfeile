package player.weapon.arrow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/** 
 * <b> Feuerpfeil </b>
 * 
 * @version 25.11.2013
 */
public class FireArrow extends AbstractArrow {
	/** Index des Feuerpfeils */
	public static final int INDEX = 0;
	
	/** Name des Pfeils: */
	public static final String NAME = "FireArrow", LANG_IDENT = "item/arrow/fire";
	
	public static final Color UNIFIED_COLOR = new Color(207, 41, 6);
	
	/** Bild des Pfeils */
	private static BufferedImage img_Fire;
	/** Laden des Bildes */
	static {
		try {
			img_Fire = ImageIO.read(FireArrow.class.getClass().getResourceAsStream (
					"/resources/gfx/arrow_textures/fireArrow.png"));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/** ruft den Konstruktor von 'AbstractArrow' auf */
	public FireArrow() {
		super(82f, 23f, 80, 0.045f, 0.02f, 0.095f, 0.10f, 3.5, 6.2f, NAME);
	}
	
	/** Gibt das Bild des Feuerpfeiles zur�ck 
	 * @see <code> ArrowHelper.getArrowImage(int selectedIndex) </code> */
	@Override
	public BufferedImage getImage () {
		return img_Fire;
	}

    /**
     * Returns the name of the item in English for the user
     */
    @Override
    public String getNameEnglish () {
        return "Fire arrow";
    }

    /**
     * Returns the name of the item in German for the user
     */
    @Override
    public String getNameGerman () {
        return "Feuerpfeil";
    }

    @Override
    protected String getTranslationIdentifier() {
        return LANG_IDENT;
    }
}
