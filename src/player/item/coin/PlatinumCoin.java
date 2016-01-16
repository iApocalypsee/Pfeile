package player.item.coin;

import general.LogFacility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A platinum coin is the most valuable coin in the game.
 */
public class PlatinumCoin extends Coin {

    /** the texture of a PlatinumCoin */
    private static BufferedImage image;

    static {
        String path = "resources/gfx/item textures/coin textures/platinumCoin.png";
        try {
            image = ImageIO.read(PlatinumCoin.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class PlatinumCoin couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    public PlatinumCoin () {
        super("PlatinumCoin");
    }

    /** the value of a PlatinumCoin is equal to 400 BronzeCoins */
    public static final int VALUE = 400;

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
        return "Platinum coin";
    }

    /**
     * Returns the name of the item in German for the user
     */
    @Override
    public String getNameGerman () {
        return "Platinmünze";
    }
}
