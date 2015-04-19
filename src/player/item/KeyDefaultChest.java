package player.item;

import general.LogFacility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * You need to possess such a key to open an {@link player.item.DefaultChest}.
 */
public class KeyDefaultChest extends Item {

    /** the texture of a Key. */
    private static BufferedImage image;

    static {
        String path = "resources/gfx/item textures/loot textures/keyDefaultChest.png";
        try {
            image = ImageIO.read(KeyDefaultChest.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class KeyDefaultChest couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    public KeyDefaultChest () {
        super("Key_DefaultChest");
    }

    /**
     * Every item can be drawn, so it must have a BufferedImage. Override this call with
     * a link to the component or a loaded static BufferedImage.
     *
     * @return the {@link java.awt.image.BufferedImage} of the item
     */
    @Override
    public BufferedImage getImage () {
        return image;
    }
}
