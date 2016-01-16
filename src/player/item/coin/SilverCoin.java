package player.item.coin;

import general.LogFacility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A little more valuable.
 */
public class SilverCoin extends Coin {

    /** the texture of a SilverCoin */
    private static BufferedImage image;

    static {
        String path = "resources/gfx/item textures/coin textures/silverCoin.png";
        try {
            image = ImageIO.read(SilverCoin.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class SilverCoin couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    public SilverCoin () {
        super("SilverCoin");
    }

    /** The value of a silver coin measured in number of BronzeCoins */
    public static final int VALUE = 10;

    @Override
    public int getValue () {
        return VALUE;
    }

    /**
     * The basic appearance of a coin overwritten by it's subclasses helps to draw it (for example at InventoryScreen).
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
        return "Silver coin";
    }

    /**
     * Returns the name of the item in German for the user
     */
    @Override
    public String getNameGerman () {
        return "Silbermünze";
    }
}
