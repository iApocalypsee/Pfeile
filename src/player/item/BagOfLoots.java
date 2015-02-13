package player.item;

import newent.Entity;

/**
 * Loots dropped by dead enemies or creeps are BagOfLoots, no {@link player.item.Treasure}.
 */
public class BagOfLoots extends Loot {

    /** Creating a new BagOfLoots from a deadEntity. All values are taken from the deadEntity. */
    public BagOfLoots (Entity deadEntity) {
        super(deadEntity.getGridX(), deadEntity.getGridY(), "Bag Of Loots from " + deadEntity.name());
    }
}
