package player.item;

import general.LogFacility;
import newent.Bot;
import newent.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A round chest is chest, which only appears once a round. It contains the most striking weapons, but it must be
 * protected by some creeps and spawn in a fair distance between the players or nearer to the disadvantaged player.
 * However, it might also trigger some bad, world-effecting catastrophe (in far feature...).
 */
public class RoundChest extends Chest {

    /** the texture of a RoundChest */
    private static BufferedImage image;

    static {
        String path = "resources/gfx/item textures/roundChest.png";
        try {
            image = ImageIO.read(BagOfLoots.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class RoundChest couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    public RoundChest (int gridX, int gridY) {
        super(gridX, gridY, "Round Chest");

    }

    @Override
    public BufferedImage getImage () {
        return image;
    }
}
