package player.item.coin;

import general.LogFacility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A coin of small value.
 */
public class BronzeCoin extends Coin {

    /** the texture of a BronzeCoin */
    private static BufferedImage image;

    static {
        String path = "resources/gfx/item textures/coin textures/bronzeCoin.png";
        try {
            image = ImageIO.read(BronzeCoin.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class BronzeCoin couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    public BronzeCoin () {
        super("BronzeCoin");
    }

    /** the value of a bronze coin is <code>1</code> */
    public final static int VALUE = 1;

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
        return "bronzeCoin";
    }
}
