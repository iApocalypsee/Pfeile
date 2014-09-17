package general.field;

import javax.imageio.ImageIO;

import general.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Josip
 * @version 2/9/14
 */
@Deprecated
public class GrassField extends Field {

    private static BufferedImage image = null;

    static {
        try {
            image = ImageIO.read(DesertField.class.getClassLoader().
                    getResourceAsStream("resources/gfx/field textures/grass_Field.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public GrassField(int x, int y, World w) {
        super(x, y, w);
    }

    @Override
    public void draw(Graphics2D g) {
        drawHelper(g, image);
    }

	/**
	 * Returns the field type name.
	 *
	 * @return The field type name.
	 */
    @Deprecated
	@Override
	public String getFieldType() {
		return "Wiese";
	}

	@Override
	public boolean isAccessable() {
		return true;
	}
}
