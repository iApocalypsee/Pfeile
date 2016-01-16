package player.weapon.arrow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * <b> Blitzpfeil </b>
 * 
 * @version 25.11.2013
 */
public class LightningArrow extends AbstractArrow {
	/** Index des Blitzpfeils */
	public static final int INDEX = 5;

	public static final String NAME = "Lightning arrow";
	
	public static final Color UNIFIED_COLOR = new Color(194, 198, 255);

	/** Bild des Pfeils */
	private static BufferedImage img_Lightning;
	/** Laden des Bildes */
	static {
		try {
			img_Lightning = ImageIO.read(LightningArrow.class.getClassLoader().getResourceAsStream (
					"resources/gfx/arrow textures/lightningArrow.png"));
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** ruft den Konstrucktor von 'AbstractArrow' auf */
	public LightningArrow() {
		super(98f, 14f, 26, 0.0475f, 0.0175f, 0.11f, 0.04f, 7.5, 3.0f, NAME);
	}

	/** Gibt das Bild vom Blitzpfeils zur�ck
	 * @see <code> ArrowHelper.getArrowImage(int selectedIndex) </code>  */
	@Override
	public BufferedImage getImage() {
		return img_Lightning;
	}

    /**
     * Returns the name of the item in English for the user
     */
    @Override
    public String getNameEnglish () {
        return "Lightening arrow";
    }

    /**
     * Returns the name of the item in German for the user
     */
    @Override
    public String getNameGerman () {
        return "Blitzpfeil";
    }
}
