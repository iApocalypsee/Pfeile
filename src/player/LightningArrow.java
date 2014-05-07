package player;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * <b> Blitzpfeil </b>
 * 
 * @version 25.11.2013
 */
public class LightningArrow extends AbstractArrow {
	/** Index des Blitzpfeils */
	public static final int INDEX = 5;

	public static final String NAME = "Blitzpfeil";
	
	public static final Color UNIFIED_COLOR = new Color(235, 234, 176, 75);

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

		super(52f, 19f, 900, 0.0475f, 0.0175f, 0.11f, 0.04f, "Blitzpfeil");

	}

	/** Gibt das Bild vom Blitzpfeils zur�ck */
	public static BufferedImage getImage() {
		return img_Lightning;
	}
}
