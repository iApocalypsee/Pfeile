package player.item;

import general.LogFacility;
import gui.screen.GameScreen;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

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

    /**
     * Returns the name of the item in English for the user
     */
    @Override
    public String getNameEnglish () {
        return "Chest";
    }

    /**
     * Returns the name of the item in German for the user
     */
    @Override
    public String getNameGerman () {
        return "Truhe";
    }

    @Override
    protected String getTranslationIdentifier() {
        return "item/loot/chest";
    }

    @Override
    public void open () {
        getLootUI().changeUI(imageOpenChest);
        isOpen = true;
        GameScreen.getInstance().setWarningMessage("Kiste geöffnet: Schlüssel wurde aus Inventar entfernt.");
        GameScreen.getInstance().activateWarningMessage();
    }
}
