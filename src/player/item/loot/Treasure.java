package player.item.loot;

import general.LogFacility;
import general.Main;
import newent.InventoryEntity;
import player.item.coin.BronzeCoin;
import player.item.coin.SilverCoin;
import player.item.ore.CopperOre;
import player.item.ore.IronOre;
import player.item.potion.PotionOfFortune;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * The treasure contains some valuables and can be found by either Player or Bot. The difference to chests is, that
 * you don't need to open them (i.e. you need to do something). If a player or bot moves on the field the treasure is
 * placed, the treasure will be added to the inventory (after confirming the content). The Bounds of a treasure are saved
 * as {@link comp.ImageComponent} in {@link LootUI}. Treasures generally contain more Coins then Chest.
 *
 * @see Chest
 * @see DefaultChest
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

    /**
     * If the player has an increased fortune stat during this turn, this method, will add additional content.
     * Additional content only works with the active player.
     */
    @Override
    public void additionalContent () {
        Random ranGen = new Random();
        int fortuneStat = Main.getContext().getActivePlayer().getFortuneStat();

        if (fortuneStat > 110)
            if (ranGen.nextFloat() < 0.2f)
                add(new PotionOfFortune((byte) 3));
        if (fortuneStat > 60) {
            if (ranGen.nextFloat() < 0.1f)
                add(new KeyRoundChest());
        }
        if (fortuneStat > 30) {
            if (ranGen.nextFloat() < 0.2f)
                add(new SilverCoin());
        }
        if (fortuneStat > 17) {
            if (ranGen.nextFloat() < 0.15f)
                add(new KeyRoundChest());
        }
        if (fortuneStat > 12)
            add(new IronOre());

        if (fortuneStat > 8)
            if (ranGen.nextFloat() < 0.03f * fortuneStat) {
                add(new CopperOre());
                add(new CopperOre());
            }

        for (int i = 0; i < fortuneStat/2; i++) {
            if (ranGen.nextFloat() < 0.98f)
                add(new BronzeCoin());
            else
                add(new IronOre());
        }

        if (ranGen.nextFloat() < 0.05f * fortuneStat) {
            add(new SilverCoin());
        }
    }
}
