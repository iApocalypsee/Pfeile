package player.item.potion;

import general.LogFacility;
import general.Main;
import player.item.Item;
import player.weapon.arrow.AbstractArrow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * This is a potion, which allows the arrows, which adds an poison effect to all arrows in the inventory of the
 * active player (the player using the potion). This effect is permanent right now, but the amount of poison added by
 * this effect is small (level 0: +5; level 1: +9; level 2: +13).
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
        final List<Item> items = Main.getContext().getActivePlayer().inventory().getItems();
        for (Item item: items) {
            if (item instanceof AbstractArrow) {
                AbstractArrow arrow = (AbstractArrow) item;
                arrow.changePoisonedAmount((int) ((getLevel() + 1.65) * 3.6));
            }
        }
        return remove();
    }
}
