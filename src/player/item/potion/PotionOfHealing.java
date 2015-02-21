package player.item.potion;

import general.LogFacility;
import general.Main;
import player.Life;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * a potion healing <code>10 * level + (10% * level) of life.getMaximumLife()</code> life.
 */
public class PotionOfHealing extends Potion {

    private static BufferedImage image;

    static {
        String path = "resources/gfx/item textures/potion textures/potionOfHealing.png";
        try {
            image = ImageIO.read(PotionOfHealing.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("Image of PotionOfHealing could not be loaded: " + path);
        }
    }

    /**
     * Creating a new healing Potion requires nothing (the level of the healing potion is 1).
     */
    public PotionOfHealing () {
        super("Potion of Healing");
        potionUI.createComponent(image);
    }

    /**
     * creating a new healing Potion
     *
     * @param level the level (--> effectiveness) of the potion
     */
    public PotionOfHealing (byte level) {
        super(level, "Potion of Healing");
    }

    @Override
    public void triggerEffect () {
        Life life = Main.getContext().getActivePlayer().getLife();
        life.setLife(life.getLife() + 10 * level + life.getMaxLife() * 0.1 * level);
        // after using the potion is should be removed at all.
        remove();
    }
}
