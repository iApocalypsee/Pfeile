package player.item;

import general.LogFacility;
import general.Main;
import gui.screen.GameScreen;
import player.item.coin.CoinHelper;
import player.item.ore.CopperOre;
import player.item.ore.IronOre;
import player.item.potion.PotionOfDamage;
import player.item.potion.PotionOfFortune;
import player.item.potion.PotionOfPoison;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * This is the usual chest (compared to {@link player.item.RoundChest}. It can be found and opened by players and bots.
 * */
public class DefaultChest extends Chest {

    private static BufferedImage image;
    private static BufferedImage imageOpenChest;

    static {
        String path = "resources/gfx/item textures/loot textures/defaultChest.png";
        try {
            image = ImageIO.read(DefaultChest.class.getClassLoader().getResourceAsStream(path));

            path = "resources/gfx/item textures/loot textures/defaultChestOpen.png";
            imageOpenChest = ImageIO.read(DefaultChest.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class DefaultChest couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    /**
     * Creating a new basic Chest on the position (<code>gridX</code>|<code>gridY</code>).
     *
     *
     * @param gridX the x-position of the tile, where the DefaultChest should be placed
     * @param gridY and the y-position
     */
    public DefaultChest (int gridX, int gridY) {
        super(gridX, gridY, "DefaultChest");
    }

    @Override
    public BufferedImage getImage () {
        return image;
    }

    @Override
    public void open () {
        getLootUI().changeUI(imageOpenChest);
        isOpen = true;
        GameScreen.getInstance().setWarningMessage(Main.tr("chestOpened"));
        GameScreen.getInstance().activateWarningMessage();
    }

    /**
     * If the player has an increased fortune stat during this turn, this method, will add additional content.
     */
    @Override
    public void additionalContent () {
        Random ranGen = new Random();
        int fortuneStat = Main.getContext().getActivePlayer().getFortuneStat();
        if (fortuneStat > 10) {
            if (ranGen.nextFloat() < 0.24f)
                add(new PotionOfDamage());
        }
        if (fortuneStat > 20) {
            if (ranGen.nextFloat() < 0.8f)
                add(new IronOre());
            if (ranGen.nextFloat() < 0.6f) {
                add(new CopperOre());
                add(new CopperOre());
            }
        }
        if (fortuneStat > 37) {
            if (ranGen.nextFloat() < 0.35f)
                add(new PotionOfPoison());
        }
        if (fortuneStat > 45) {
            if (ranGen.nextFloat() < 0.18f)
                add(new PotionOfFortune());
        }
        if (fortuneStat > 70) {
            if (ranGen.nextFloat() < 0.05f)
                add(new PotionOfFortune((byte) 2));
        }

        if (ranGen.nextFloat() < fortuneStat/100.0f)
            add(CoinHelper.getCoins((int) (fortuneStat * ranGen.nextFloat())));
    }
}
