package player.item.coin;

import general.LogFacility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Gold is already quite valuable.
 */
public class GoldCoin extends Coin {

    /** the texture of a BagOfLoots */
    private static BufferedImage image;

    static {
        String path = "resources/gfx/item textures/coin textures/goldCoin.png";
        try {
            image = ImageIO.read(GoldCoin.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class GoldCoin couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    public GoldCoin () {
        super("GoldCoin");
    }

    /** the value of GoldCoin measured in number of BronzeCoins. It's 80. */
    public static final int VALUE = 80;

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

    @Override
    protected String getTranslationIdentifier() {
        return "item/coin/gold";
    }
}
