package player.item;

import general.LogFacility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * You need to possess such a key to open an {@link player.item.RoundChest}.
 */
public class KeyRoundChest extends Item {

    /** the texture of a Key. */
    private static BufferedImage image;

    static {
        String path = "resources/gfx/item_textures/loot textures/keyRoundChest.png";
        try {
            image = ImageIO.read(KeyRoundChest.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class KeyRoundChest couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    public KeyRoundChest () {
        super("Key_RoundChest");
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

    /**
     * Returns the name of the item in English for the user
     */
    @Override
    public String getNameEnglish () {
        return "key (Roundchest)";
    }

    /**
     * Returns the name of the item in German for the user
     */
    @Override
    public String getNameGerman () {
        return "Schlüssel (Rundentruhe)";
    }

    @Override
    protected String getTranslationIdentifier() {
        return "item/key/round";
    }
}
