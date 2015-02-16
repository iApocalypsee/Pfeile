package player.item.potion;

import comp.ImageComponent;
import general.Main;
import gui.screen.GameScreen;
import player.Life;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
        getPotionUI().component = new ImageComponent(0, 0, image, GameScreen.getInstance());
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
        Life lifeOfActivePlayer = Main.getContext().getActivePlayer().getLife();
        lifeOfActivePlayer.setLife(lifeOfActivePlayer.getLife() + 25 * level);
        remove();
    }

    @Override
    public void remove () {

    }
}
