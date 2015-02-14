package player.item;

/**
 * The superclass for chests. To open a chest you need to give up something (arrows, gold, ...) or find a key
 * (maybe in the future).
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
    public Chest (int gridX, int gridY, LootUI lootUI, String name) {
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
    public Chest (int gridX, int gridY, String name) {
        super(gridX, gridY, name);
    }

    /** you need to open a chest */
    //public abstract void open();
}
