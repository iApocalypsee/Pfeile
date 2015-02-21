package player.item.potion;

import general.LogFacility;
import general.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Using this potion will allow the active Player to move further (level * 5 additional movement points).
 */
public class PotionOfMovement extends Potion {

    /** the image of the potion */
    private static BufferedImage image;

    static {
        String path = "resources/gfx/item textures/potion textures/potionOfMovement.png";
        try {
            image = ImageIO.read(PotionOfHealing.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("Image of PotionOfMovement could not be loaded: " + path);
        }
    }

    /**
     *  Creating a new PotionOfMovement with the level 1 (5 additional movement points).
     */
    public PotionOfMovement () {
        super("Potion of Movement");
        potionUI.createComponent(image);
    }

    /**
     * @param level the level of the potion (the number of additional MovementPoints is multiplied with the level)
     */
    public PotionOfMovement (byte level) {
        super(level, "Potion of Movement");
        potionUI.createComponent(image);
    }

    @Override
    public void triggerEffect () {
        Main.getContext().getActivePlayer().addMovementPoints((byte) (level * 5));

        // removing the potion after the effect has been triggered.
        remove();
    }
}
