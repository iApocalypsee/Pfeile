package general.field;

import general.World;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Josip
 * @version 2/9/14
 */
public class DesertField extends Field {

    private static BufferedImage image = null;

    static {
        try {
            image = ImageIO.read(DesertField.class.getClassLoader().
                    getResourceAsStream("resources/gfx/field textures/desert_Field.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DesertField(int x, int y, World w) {
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
		return "W�ste";
	}

	/**
	 * Returns <code>false</code> for desert fields.
	 * @return <code>false</code>
	 */
	@Override
	public boolean isAccessable() {
		return false;
	}
}
