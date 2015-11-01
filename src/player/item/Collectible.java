package player.item;

import general.LogFacility;
import general.Main;
import gui.screen.GameScreen;
import newent.InventoryEntity;
import newent.InventoryLike;
import player.item.coin.Coin;

import java.util.ArrayList;
import java.util.List;

/**
 * Any Loot is collectible. So it has to be removed from the world and added to the inventory of the player.
 * Notice, that every loot is a container and not the loot itself is added to the inventory,
 * but their {@link player.item.Loot#getStoredItems()}.
 */
public interface Collectible {

    /**
     *
     * @param entity any Entity, which has an Inventory
     * @return Has the loot been successfully added to the inventory?
     */
    boolean collect(InventoryEntity entity);

    /**
     * The default "collect" method. If the inventory would be full with the stored items from the loot, the method
     * doesn't add the loot to the inventory and returns false. It is a default implementation, so use the usual <code>collect</code>.
     *
     * @param inventory the inventory
     * @param loot the loot
     * @return <code>true</code> - if loot could be successfully added to the inventory and the loot removed from {@link player.item.WorldLootList}
     *
     * @see player.item.Collectible#collect(newent.InventoryEntity)
     */
    default boolean defaultCollect (InventoryLike inventory, Loot loot) {
        // controlling if the inventory is full, is already done by "put(item)".
        if (inventory.currentSize() + loot.getStoredItems().size() <= inventory.maximumSize()) {

            for (Item item : loot.getStoredItems()) {
                // must return true
                inventory.put(item);
            }

            // only remove "this" from the WorldLootList, if it has been added to inventory successfully.
            boolean removed = Main.getContext().getWorldLootList().remove(loot);

            if (removed) {
                // The user must now, what he/she just got.
                int money = 0;
                List<Item> otherItems = new ArrayList<>(7);

                for (Item item : loot.getStoredItems()) {
                    if (item instanceof Coin)
                        money = money + ((Coin) item).getValue();
                    else
                        otherItems.add(item);
                }
                GameScreen.getInstance().setWarningMessage("Geld: " + money + " | Items: " + otherItems);
                GameScreen.getInstance().activateWarningMessage();

                // secure, that no item remains
                loot.getStoredItems().clear();
            } else {
                GameScreen.getInstance().setWarningMessage(loot.getName() + " konnte nicht entfernt werden.");
                GameScreen.getInstance().activateWarningMessage();
                LogFacility.log(loot.toString() + " konnte nicht entfernt werden", LogFacility.LoggingLevel.Error);
            }

            // the money has changed probably
            Main.getContext().getActivePlayer().onMoneyChanged().apply();

            return removed;

        } else {
            GameScreen.getInstance().setWarningMessage("Das Inventar ist voll! Maximalgröße: " + inventory.maximumSize()
                    + " | Aktuelle Größe: " + inventory.currentSize());
            GameScreen.getInstance().activateWarningMessage();
            LogFacility.log("The inventory is full: " + inventory.toString() + " at adding: " + loot.toString(), LogFacility.LoggingLevel.Info);
            return false;
        }
    }
}
