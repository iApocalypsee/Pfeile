package player.item;

import general.LogFacility;
import newent.InventoryEntity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The treasure contains some valuables and can be found by either Player or Bot. The difference to chests is, that
 * you don't need to open them (i.e. you need to do something). If a player or bot moves on the field the treasure is
 * placed, the treasure will be added to the inventory (after confirming the content). The Bounds of a treasure are saved
 * as {@link comp.ImageComponent} in {@link LootUI}. Treasures generally contain more Coins then Chest.
 *
 * @see player.item.Chest
 * @see player.item.DefaultChest
 * */
public class Treasure extends Loot {

    private static BufferedImage image;

    static {
        String path = "resources/gfx/item textures/loot textures/treasure.png";
        try {
            image = ImageIO.read(Treasure.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class Treasure couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    /**
     * Creates a new treasure on the tile (<code>gridX</code>|<code>gridY</code>). The center of the bounds of the
     * component is the center of the tile. The width and height of the ImageComponent are the width and height of the
     * of the BufferedImage.
     *
     * @param gridX the x position of the tile of the Treasure
     * @param gridY the y position of the tile of the Treasure
     */
    public Treasure (int gridX, int gridY) {
        super(gridX, gridY, "Treasure");
    }

    @Override
    public boolean collect (InventoryEntity entity) {
        return defaultCollect(entity.inventory(), this);
    }

    @Override
    public BufferedImage getImage () {
        return image;
    }

    @Override
    protected String getTranslationIdentifier() {
        return "item/loot/treasure";
    }
}
