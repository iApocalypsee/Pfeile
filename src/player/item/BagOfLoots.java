package player.item;

import general.LogFacility;
import newent.*;

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
        String path = "resources/gfx/item textures/loot textures/bagOfLoots.png";
        try {
            image = ImageIO.read(BagOfLoots.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class BagOfLoots couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    /** Creating a new BagOfLoots from a deadEntity. All values are taken from the deadEntity.
     * Everything the deadEntity carried (if it is an InventoryEntity) is added to BagOfLoots and everything
     * the deadEntity was equipped with (if it is a Combatant) is added to BagOfLoots, too.
     *
     * @param deadEntity the entity, which dropped a BagOfLoots (--> usually a dead Entity)
     * @see player.item.Loot#Loot(int, int, LootUI, String)
     * @see player.item.BagOfLoots#BagOfLoots(int, int) */
    public BagOfLoots (Entity deadEntity) {
        super(deadEntity.getGridX(), deadEntity.getGridY(), "BagOfLoots [from " + deadEntity.name() + "]");

        if (deadEntity instanceof InventoryEntity) {
            InventoryEntity entity = (InventoryEntity) deadEntity;
            for (Item item : entity.inventory().javaItems()) {
                add(item);
            }
            entity.inventory().clear();
        }

        if (deadEntity instanceof Combatant) {
            EquipmentStrategy equipment = ((Combatant) deadEntity).getEquipment();
            for (EquippableItem item : equipment.getEquippedItems())
                add(item);
            equipment.getEquippedItems().clear();
        }
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
    public boolean collect (InventoryEntity entity) {
        return defaultCollect(entity.inventory(), this);
    }

    @Override
    public BufferedImage getImage () {
        return image;
    }
}
