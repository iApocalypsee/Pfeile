package player.item;

import general.Main;
import newent.*;

import java.awt.image.BufferedImage;

/**
 * Any Loot is collectible. So it has to be removed from the world and added to the inventory of the player.
 * Notice, that every loot is a container and not the loot itself is added to the inventory,
 * but their {@link player.item.Loot#getStoredItems()}.
 */
public interface Collectible {
    /**
     *
     * @param activePlayer The player, which collects the loot
     * @return Has the loot been successfully added to the inventory?
     */
    boolean collect(Player activePlayer);

    /**
     *
     * @param activeBot the bot, which collects the loot
     * @return Has the loot been successfully added to the inventory?
     */
    boolean collect(Bot activeBot);

    /**
     *
     * @param entity any Entity, which has an Inventory
     * @return Has the loot been successfully added to the inventory?
     */
    boolean collect(InventoryEntity entity);

    /**
     * Everything, that is collectable must be seen, so this returns the texture.
     *
     * @return the BufferedImage of the Loot
     */
    BufferedImage getImage();

    /**
     * The default "collect" method. If the inventory would be full with the stored items from the loot, the method
     * doesn't add the loot to the inventory and returns false. It is a default implementation, so use the usual <code>collect</code>.
     *
     * @param inventory the inventory
     * @param loot the loot
     * @return <code>true</code> - if loot could be successfully added to the inventory and the loot removed from {@link player.item.WorldLootList}
     *
     * @see player.item.Collectible#collect(newent.Player)
     * @see player.item.Collectible#collect(newent.Bot)
     * @see player.item.Collectible#collect(newent.InventoryEntity)
     */
    default boolean defaultCollect (InventoryLike inventory, Loot loot) {
        // controlling if the inventory is full, is already done by "put(this)".
        if (inventory.currentSize() + loot.getStoredItems().size() <= inventory.maximumSize()) {

            for (Item item : loot.getStoredItems()) {
                // must return true
                inventory.put(item);
            }

            // only remove "this" from the WorldLootList, if it has been added to inventory successfully.
            Main.getContext().getWorldLootList().remove(loot);
            return true;
        } else {
            return false;
        }
    }
}
