package general;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Josip
 * @version 2/9/14
 */
public class ForestField extends Field {

    private static BufferedImage image = null;

    static {
        try {
            image = ImageIO.read(DesertField.class.getClassLoader().
                    getResourceAsStream("resources/gfx/field textures/forest_Field.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ForestField(int x, int y, World w) {
        super(x, y, w);
        
        isAccessible = true;
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
		return "Forest";
	}

}
