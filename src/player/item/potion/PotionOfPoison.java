package player.item.potion;

import general.LogFacility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * This is a potion, which allows the arrows, which have been or will be shoot during this round, to be poisoned.
 */
public class PotionOfPoison extends Potion {

    /** the possible images of a potion: length is 3 because of three possible levels */
    private static BufferedImage[] images;

    static {
        images = new BufferedImage[3];

        String generalPath = "resources/gfx/item textures/potion textures/potionOfPoison";
        String path = "<No path selected>";
        try {
            path = generalPath + "[0].png";
            images[0] = ImageIO.read(PotionOfDamage.class.getClassLoader().getResourceAsStream(path));
            path = generalPath + "[1].png";
            images[1] = ImageIO.read(PotionOfDamage.class.getClassLoader().getResourceAsStream(path));
            path = generalPath + "[2].png";
            images[2] = ImageIO.read(PotionOfDamage.class.getClassLoader().getResourceAsStream(path));

        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("Image of PotionOfPoison could not be loaded: " + path);
        }
    }

    /**
     * The default constructor with the <code>name</code> and the <code>level = 0</code> and with a default PotionUI {@link
     * PotionUI#PotionUI()}
     *
     * @see PotionOfPoison#PotionOfPoison(byte)
     */
    public PotionOfPoison () {
        this((byte) 0);
    }

    /**
     * Creating a new potion with the defined values and a default PotionUI {@link PotionUI#PotionUI()}
     *
     * @param level the level of the item <code>level >= 0 && level <= 2</code>
     */
    public PotionOfPoison (byte level) {
        super(level, images[level], "PotionOfPoison");
    }

    /**
     * trigger the effects from the Potion and finally <b>removes the Potions with {@link Potion#remove()}</b>.
     *
     * @return <code>true</code> - if the potion could be removed (return type of <code>potion.remove()</code>
     */
    @Override
    public boolean triggerEffect () {
        // TODO: The effect is missing.
        return false;
    }
}
