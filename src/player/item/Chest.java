package player.item;

import newent.Bot;
import newent.InventoryEntity;
import newent.Player;

/**
 * The superclass for chests. To open a chest you need to give up something (arrows, gold, ...) or find a key
 * (maybe in the future). That's why only Players and Bots can open an Chest. Generally Chest cointains more Potions than
 * Treasures.
 */
public abstract class Chest extends Loot {

    /**
     * Create a new Chest with the given parameter. Look to {@link player.item.Loot} for further information.
     *
     * @param gridX the x-position of the tile, where the chest should be placed
     * @param gridY and the y-position
     * @param lootUI the outward appearance of the chest
     * @param name the name of the Chest ("Round Chest" or "Default Chest")
     * @see player.item.Loot#Loot(int, int, LootUI, String)
     * @see player.item.Loot#Loot(int, int, String)
     * @see player.item.Chest#Chest(int, int, String)
     */
    protected Chest (int gridX, int gridY, LootUI lootUI, String name) {
        super(gridX, gridY, lootUI, name);
    }

    /**
     * Create a new Chest with the given parameter. Look to {@link player.item.Loot} for further information.
     * You should set the LookUI later in the constructor, when all methods are available.
     *
     * @param gridX the x-position of the tile, where the chest should be placed
     * @param gridY and the y-position
     * @param name the name of the Chest ("Round Chest" or "Default Chest")
     * @see player.item.Loot#Loot(int, int, LootUI, String)
     * @see player.item.Loot#Loot(int, int, String)
     * @see player.item.Chest#Chest(int, int, LootUI, String)
     * */
    protected Chest (int gridX, int gridY, String name) {
        super(gridX, gridY, name);
    }

    /** you need to open a chest */
    //public abstract void open();


    @Override
    public boolean collect (Player activePlayer) {
        return defaultCollect(activePlayer.inventory(), this);
    }

    @Override
    public boolean collect (Bot activeBot) {
        return defaultCollect(activeBot.inventory(), this);
    }

    @Override
    public boolean collect (InventoryEntity entity) {
        // they can't open a chest
        return false;
    }
}
