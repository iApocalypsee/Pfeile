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

    /** the possible images of a potion: length is 3 because of three possible levels */
    private static BufferedImage[] images;

    static {
        images = new BufferedImage[3];

        String generalPath = "resources/gfx/item_textures/potion textures/potionOfHealing";
        String path = "<No path selected>";
        try {
            path = generalPath + "[0].png";
            images[0] = ImageIO.read(PotionOfHealing.class.getClassLoader().getResourceAsStream(path));
            path = generalPath + "[1].png";
            images[1] = ImageIO.read(PotionOfHealing.class.getClassLoader().getResourceAsStream(path));
            path = generalPath + "[2].png";
            images[2] = ImageIO.read(PotionOfHealing.class.getClassLoader().getResourceAsStream(path));

        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("Image of PotionOfHealing could not be loaded: " + path);
        }
    }

    /**
     * Creating a new healing Potion requires nothing (the level of the healing potion is 0).
     */
    public PotionOfHealing () {
        this((byte) 0);
    }

    /**
     * creating a new healing Potion
     *
     * @param level the level (--> effectiveness) of the potion. Must be <code>0 <= level <= 2</code>
     */
    public PotionOfHealing (byte level) {
        super(level, images[level], "PotionOfHealing");
    }

    /**
     *
     * @param level the level of the potion. Must be <code>0, 1 or 2</code>
     * @param posX the X-position of the screen
     * @param posY the Y-position of the screen
     */
    public PotionOfHealing (byte level, int posX, int posY) {
        super(level, "PotionOfHealing");
        potionUI.createComponent(images[getLevel()], posX, posY);
    }

    @Override
    public boolean triggerEffect () {
        Life life = Main.getContext().getActivePlayer().getLife();
        life.setLife(life.getLife() + 10 * (getLevel() + 1) + life.getMaxLife() * 0.1 * (getLevel() + 1));
        // after using the potion is should be removed at all.
        return remove();
    }

    @Override
    public BufferedImage getImage () {
        return images[getLevel()];
    }

    /**
     * Returns the name of the item in English for the user
     */
    @Override
    public String getNameEnglish () {
        return "Healing potion";
    }

    /**
     * Returns the name of the item in German for the user
     */
    @Override
    public String getNameGerman () {
        return "Heiltrank";
    }

    @Override
    protected String getTranslationIdentifier() {
        return "item/potion/heal";
    }
}
