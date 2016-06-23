package player.item.loot;

import general.LogFacility;
import general.Main;
import gui.screen.GameScreen;
import player.item.coin.BronzeCoin;
import player.item.coin.SilverCoin;
import player.item.ore.CopperOre;
import player.item.ore.IronOre;
import player.weapon.arrow.ArrowHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * A round chest is chest, which only appears once a round. It contains the most striking weapons, but it must be
 * protected by some creeps and spawn in a fair distance between the players or nearer to the disadvantaged player.
 * However, it might also trigger some bad, world-effecting catastrophe (in far feature...).
 */
public class RoundChest extends Chest {

    /** the texture of a RoundChest */
    private static BufferedImage image;

    private static BufferedImage imageOpenChest;

    static {
        String path = "resources/gfx/item textures/loot textures/roundChest.png";
        try {
            image = ImageIO.read(RoundChest.class.getClassLoader().getResourceAsStream(path));

            path = "resources/gfx/item textures/loot textures/roundChestOpen.png";
            imageOpenChest = ImageIO.read(RoundChest.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class RoundChest couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    public RoundChest (int gridX, int gridY) {
        super(gridX, gridY, "RoundChest");

    }

    @Override
    public BufferedImage getImage () {
        return image;
    }

    @Override
    public void open () {
        getLootUI().changeUI(imageOpenChest);
        isOpen = true;
        GameScreen.getInstance().setWarningMessage(Main.tr("roundChestOpened"));
        GameScreen.getInstance().activateWarningMessage();
    }

    /**
     * If the player has an increased fortune stat during this turn, this method, will add additional content.
     * Additional content only works with the active player.
     */
    @Override
    public void additionalContent () {
        Random ranGen = new Random();
        int fortuneStat = Main.getContext().getActivePlayer().getFortuneStat();

        if (fortuneStat > 100) {
            if (ranGen.nextFloat() < 0.35f)
                add(new KeyRoundChest());
        }
        if (fortuneStat > 80) {
            if (ranGen.nextFloat() < 0.7f)
                add(ArrowHelper.instanceArrow(ranGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
        }
        if (fortuneStat > 22) {
            if (ranGen.nextFloat() < 0.3f)
                add(new KeyDefaultChest());
        }
        if (fortuneStat > 10) {
            if (ranGen.nextFloat() < 0.4f) {
                for (int i = 0; i < ranGen.nextInt(7); i++) {
                    if (ranGen.nextFloat() > 0.3f)
                        add(new IronOre());
                    else
                        add(new CopperOre());
                }
            }
        }
        for (int i = 0; i < fortuneStat; i++) {
            if (ranGen.nextFloat() < 0.01f)
                add(new SilverCoin());
            else
                add(new BronzeCoin());
        }
    }
}
