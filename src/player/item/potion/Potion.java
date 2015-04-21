package player.item.potion;

import general.Main;
import gui.Drawable;
import newent.InventoryLike;
import player.item.Item;
import player.item.Loot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Any Potion. Override the PotionUI for the correct look. To change the GUI-Position of an Potion use {@link player.item.potion.PotionUI}
 */
public abstract class Potion extends Item implements Drawable {

    /**
     * how the Potion looks, when it needs to be drawn
     */
    protected PotionUI potionUI;

    /**
     * The level (effectiveness) of the potion: 0, 1 or 2.
     */
    private final byte level;

    /**
     * The default constructor with the <code>name</code> and the <code>level = 0</code> and with a
     * default PotionUI {@link PotionUI#PotionUI()}
     *
     * @param name the name of the potion. (z.B. "<code>Potion of Healing</code>")
     * @see player.item.potion.Potion#Potion(byte, String)
     */
    protected Potion (String name) {
        this((byte) 0, name);
    }

    /**
     * Creating a new potion with the defined values and a default PotionUI {@link PotionUI#PotionUI()}
     *
     * @param level the level of the item <code>level >= 0 && level <= 2</code>
     * @param name the name like "<code>Potion of Healing</code>"
     */
    protected Potion (byte level, String name) {
        super(name);
        if (level < 0 && level > 2)
            throw new IllegalArgumentException("The level (here: " + level + ") of a potion must be 0, 1 or 2.");
        this.level = level;
        potionUI = new PotionUI();
    }

    protected Potion (byte level, BufferedImage image, String name) {
        super(name);
        if (level < 0 && level > 2)
            throw new IllegalArgumentException("The level (here: " + level + ") of a potion must be 0, 1 or 2.");
        this.level = level;
        potionUI = new PotionUI(image);
    }

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
     * trigger the effects from the Potion and finally <b>removes the Potions with {@link Potion#remove()}</b>.
     *
     * @return <code>true</code> - if the potion could be removed (return type of <code>potion.remove()</code>
     */
    public abstract boolean triggerEffect ();

    /**
     * Removes the potion from the inventory or from the loot by searching the inventory of
     * <code>Main.getContext().getActivePlayer()</code> or loot <code>Main.getContext().getWorldLootList()</code>
     * every <code>anyLoot.getStoredItems()</code>. The search for the Potion ends, if the Potion has been found
     * (so a Potion in the inventory and in a loot would for example only be removed in the inventory). Other players
     * then the activePlayer aren't searched, because you can only use an potion, when it's your turn.
     *
     * @return <code>true</code> - if the item has been successfully removed.
     */
    public boolean remove () {
        // first the inventory of the active player
        InventoryLike inventory = Main.getContext().getActivePlayer().inventory();
        for (Item item : inventory.javaItems()) {
            if (this.equals(item)) {
                return inventory.remove(item).isDefined();
            }
        }

        // secondly the loots
        List<Loot> lootList = Main.getContext().getWorldLootList().getLoots();
        for (Loot loot : lootList) {
            for (Item item : loot.getStoredItems()) {
                if (this.equals(item)) {
                    return loot.getStoredItems().remove(item);
                }
            }
        }
        return false;
    }

    @Override
    public void draw (Graphics2D g) {
        potionUI.draw(g);
    }


    @Override
    public BufferedImage getImage () {
        return potionUI.getComponent().getBufferedImage();
    }

    @Override
    public String toString () {
        return getName() + "[Level: " + level + "]";
    }
}
