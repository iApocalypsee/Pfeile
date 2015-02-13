package player.item;

import gui.Drawable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>WorldLootList</code> is saved in PfeileContext. This class provides the information need to store all loots,
 * that are placed in world. Calling the draw method here allows the user to draw every loot saved in the List by drawing
 * it's LootUI.
 */
public class WorldLootList implements Drawable {
    private final List<Loot> lootList;

    /** creating a new WorldLootList with the default size 15 [as java.util.ArrayList].*/
    public WorldLootList () {
        lootList = new ArrayList<>(15);
    }

    /** Registers a new Loot to the loot
     *
     * @param droppedLoot the loot to be added to the list
     */
    public void add (Loot droppedLoot) {
        lootList.add(droppedLoot);
    }

    /**
     * Removes the specified Loot
     *
     * @param collectedLoot the loot to be removed
     * @return <code>true</code> - if the list contained the <code>collectedLoot</code>
     */
    public boolean remove (Loot collectedLoot) {
        return lootList.remove(collectedLoot);
    }

    /** Returns the ArrayList of all Loots. Do not change the list; just use it if you want to find loots or change loots
     *
     * @return the list
     */
    public List getLoots () {
        return lootList;
    }

    @Override
    public void draw (Graphics2D g) {
        for (Loot loot : lootList) {
            loot.getLootUI().draw(g);
        }
    }

    @Override
    public String toString () {
        return "WorldLootList{ " + lootList.toString() + " }";
    }
}
