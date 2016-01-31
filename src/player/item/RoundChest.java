package player.item;

import general.LogFacility;
import gui.screen.GameScreen;

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

    /**
     * Returns the name of the item in English for the user
     */
    @Override
    public String getNameEnglish () {
        return "Roundchest";
    }

    /**
     * Returns the name of the item in German for the user
     */
    @Override
    public String getNameGerman () {
        return "Rundenkiste";
    }

    @Override
    public void open () {
        getLootUI().changeUI(imageOpenChest);
        isOpen = true;
        GameScreen.getInstance().setWarningMessage("Rundenkiste geöffnet. Schlüssel wurde aus Inventar entfernt.");
        GameScreen.getInstance().activateWarningMessage();
    }
}
