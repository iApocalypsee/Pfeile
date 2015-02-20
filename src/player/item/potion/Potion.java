package player.item.potion;

import gui.Drawable;
import player.item.Item;

import java.awt.*;

/**
 * Any Potion. Override the PotionUI for the correct look.
 */
public abstract class Potion extends Item implements Drawable {

    /**
     * how the Potion looks, when it needs to be drawn
     */
    protected PotionUI potionUI;

    /**
     * The level of the potion: 1, 2 or 3.
     */
    protected byte level;

    /**
     * Every potion has it's own level.
     *
     * @return the level (--> the effectiveness) of the potion
     */
    public byte getLevel () {
        return level;
    }

    /**
     * The container of everything, that has to do with gui elements. Use the component of Potion to change the position
     * of the potion.
     *
     * @return the PotionUI
     */
    public PotionUI getPotionUI () {
        return potionUI;
    }

    /**
     * trigger the effects from the Potion
     */
    public abstract void triggerEffect ();

    /**
     * remove the potion from the inventory or from the loot
     */
    public abstract void remove ();

    /**
     * The default constructor with the <code>name</code> and the <code>level = 1</code> and with a
     * default PotionUI {@link PotionUI#PotionUI()}
     *
     * @param name the name of the potion. (z.B. "<code>Potion of Healing</code>")
     * @see player.item.potion.Potion#Potion(byte, String)
     */
    public Potion (String name) {
        super(name);
        level = 1;
        potionUI = new PotionUI();
    }

    /**
     * Creating a new potion with the defined values and a default PotionUI {@link PotionUI#PotionUI()}
     *
     * @param level the level of the item
     * @param name the name like "<code>Potion of Healing</code>"
     */
    public Potion (byte level, String name) {
        super(name);
        this.level = level;
        potionUI = new PotionUI();
    }

    @Override
    public void draw (Graphics2D g) {
        potionUI.draw(g);
    }

    @Override
    public String toString () {
        return getName();
    }
}
