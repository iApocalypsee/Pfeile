package player.item;

/**
 * The superclass for chests. To open a chest you need to give up something (arrows, gold, ...) or find a key
 * (maybe in the future).
 */
public abstract class Chest extends Loot {

    public Chest (int gridX, int gridY, String name) {
        super(gridX, gridY, name);
    }

    /** you need to open a chest */
    //public abstract void open();
}
