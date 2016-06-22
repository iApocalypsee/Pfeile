package player.item.ore;

import player.item.Item;

/**
 * an abstract item class, which covers all items that are natural resources like <code>IronOre</code> or
 * <code>Iron</code>.
 */
public abstract class RawItem extends Item {

    public RawItem(String name) {
        super(name);
    }

}
