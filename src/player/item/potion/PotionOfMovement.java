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

    /** the possible images of a potion: length is 3 because of three possible levels */
    private static BufferedImage[] images;

    static {
        images = new BufferedImage[3];

        String generalPath = "resources/gfx/item textures/potion textures/potionOfMovement";
        String path = "";
        try {
            path = generalPath + "[0].png";
            images[0] = ImageIO.read(PotionOfMovement.class.getClassLoader().getResourceAsStream(path));
            path = generalPath + "[1].png";
            images[1] = ImageIO.read(PotionOfMovement.class.getClassLoader().getResourceAsStream(path));
            path = generalPath + "[2].png";
            images[2] = ImageIO.read(PotionOfMovement.class.getClassLoader().getResourceAsStream(path));

        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("Image of PotionOfMovement could not be loaded: " + path);
        }
    }

    /**
     *  Creating a new PotionOfMovement with the level 0 (5 additional movement points).
     */
    public PotionOfMovement () {
        this((byte) 0);
    }

    /**
     * <code>assert 0 <= level && level <= 2</code>
     *
     * @param level the level of the potion (the number of additional MovementPoints is multiplied with the level)
     */
    public PotionOfMovement (byte level) {
        super(level, "Potion of Movement");
        potionUI.createComponent(images[level]);
    }

    /**
     *
     * @param level the level of the position 0, 1 or 2
     * @param posX the X-position in px for <code>PotionUI</code>
     * @param posY and the Y-position
     */
    public PotionOfMovement (byte level, int posX, int posY) {
        super(level, "Potion of Movement");
        potionUI.createComponent(images[level], posX, posY);
    }

    @Override
    public boolean triggerEffect () {
        Main.getContext().getActivePlayer().addMovementPoints((byte) ((getLevel() + 1) * 5));

        // removing the potion after the effect has been triggered.
        return remove();
    }
}
