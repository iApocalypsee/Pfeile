package player.item;

import newent.Bot;
import newent.Player;

/**
 * Any Loot is collectible. So it has to be removed from the world and added to the inventory of the player.
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
}
