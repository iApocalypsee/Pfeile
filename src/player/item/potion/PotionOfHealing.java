package player.item.potion;

import general.Main;
import newent.InventoryLike;
import player.Life;
import player.item.Item;
import player.item.Loot;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * a potion healing <code>25 * level</code> life.
 */
public class PotionOfHealing extends Potion {

    private static BufferedImage image;

    static {
        try {
            image = ImageIO.read(PotionOfHealing.class.getClassLoader().getResourceAsStream(
                    "resources/gfx/item textures/potion textures/potionOfHealing.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creating a new healing Potion requires nothing (the level of the healing potion is 1).
     */
    public PotionOfHealing () {
        super("Potion of Healing");
        getPotionUI().createComponent(image);
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
        life.setLife(life.getLife() + 25 * level);
        // after using the potion is should be removed at all.
        remove();
    }
}
