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
public class WastelandField extends Field {

    private static BufferedImage image = null;

    static {
        try {
            image = ImageIO.read(DesertField.class.getClassLoader().
                    getResourceAsStream("resources/gfx/field textures/wasteland_Field.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public WastelandField(int x, int y, World w) {
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
	@Override
	public String getFieldType() {
		return "�dland";
	}

	/**
	 * Returns <code>true</code> for wastelands.
	 * @return <code>true</code>
	 */
	@Override
	public boolean isAccessable() {
		return true;
	}
}
