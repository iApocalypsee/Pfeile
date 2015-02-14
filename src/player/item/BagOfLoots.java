package player.item;

import general.LogFacility;
import newent.Bot;
import newent.Entity;
import newent.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Loots dropped by dead enemies or creeps are BagOfLoots, no {@link player.item.Treasure}.
 */
public class BagOfLoots extends Loot {

    /** the texture of a BagOfLoots */
    private static BufferedImage image;

    static {
        String path = "resources/gfx/item textures/bagOfLoots.png";
        try {
            image = ImageIO.read(BagOfLoots.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class BagOfLoots couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    /** Creating a new BagOfLoots from a deadEntity. All values are taken from the deadEntity.
     * @param deadEntity the entity, which dropped a BagOfLoots (--> usually a dead Entity)
     * @see player.item.Loot#Loot(int, int, LootUI, String)
     * @see player.item.BagOfLoots#BagOfLoots(int, int) */
    public BagOfLoots (Entity deadEntity) {
        super(deadEntity.getGridX(), deadEntity.getGridY(), "BagOfLoots [from " + deadEntity.name() + "]");

    }

    /**
     * Creates a new BagOfLoots on the position(<code>gridX</code>|<code>gridY</code>).
     *
     * @param gridX the x-position of the tile, where the <code>BagOfLoots</code> should be placed
     * @param gridY and the y-position
     * @see player.item.Loot#Loot(int, int, String)
     * @see player.item.BagOfLoots#BagOfLoots(newent.Entity)
     */
    public BagOfLoots (int gridX, int gridY) {
        super(gridX, gridY, "BagOfLoots");
    }

    @Override
    public boolean collect (Player activePlayer) {
        throw new NotImplementedException();
        //return false;
    }

    @Override
    public boolean collect (Bot activeBot) {
        throw new NotImplementedException();
        //return false;
    }

    @Override
    public BufferedImage getImage () {
        return image;
    }
}
