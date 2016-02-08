package player.weapon.arrow;

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
	public static final String NAME = "StoneArrow", LANG_IDENT = "item/arrow/stone";
	
	public static final Color UNIFIED_COLOR = new Color(142, 120, 93);
	
	
	/** Bild des Pfeils */
	private static BufferedImage img_Stone;
	/** Laden des Bildes */
	static {
		try {
			img_Stone = ImageIO.read(StoneArrow.class.getClassLoader().getResourceAsStream (
					"resources/gfx/arrow_textures/stoneArrow.png"));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	
	/** ruft den Konstrucktor von 'AbstractArrow' auf */
	public StoneArrow() {
		super(59f, 41f, 12, 0.0075f, 0.025f, 0.14f, 0.04f, 5, 3.5f, NAME);
	}
	
	/** Gibt das Bild des Steinpfeiles zur�ck 
	 * @see <code> ArrowHelper.getArrowImage(int selectedIndex) </code> */
	@Override
	public BufferedImage getImage () {
		return img_Stone;
	}

    /**
     * Returns the name of the item in English for the user
     */
    @Override
    public String getNameEnglish () {
        return "Stone arrow";
    }

    /**
     * Returns the name of the item in German for the user
     */
    @Override
    public String getNameGerman () {
        return "Steinpfeil";
    }

    @Override
    protected String getTranslationIdentifier() {
        return LANG_IDENT;
    }
}
