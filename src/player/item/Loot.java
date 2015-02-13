package player.item;

import general.Main;
import player.BoardPositionable;
import world.TileLike;

import java.util.ArrayList;
import java.util.List;

/**
 * Anything dropped by enemies or every chest or treasure is extended from Loot. Loot is a subclass of {@link Item}.
 */
public abstract class Loot extends Item implements BoardPositionable {

    private int gridX;

    private int gridY;

    /** Subclasses must override this value */
    private LootUI lootUI;

    /** everything, that is stored inside this loot and can be received by collecting this Loot. */
    private List<Item> listOfContent;

    /** Creates a new loot with the given name.
     * The name is set in superclass Item.
     * <b>The position, where the Loot drops, is position of the activePlayer</b>*/
    public Loot (String name) {
        super(name);

        this.gridX = Main.getContext().getActivePlayer().getGridX();
        this.gridY = Main.getContext().getActivePlayer().getGridY();

        listOfContent = new ArrayList<>(8);
    }

    /** Creates a new Loot on the Tile at (gridX, gridY) with the specified name. */
    public Loot (int gridX, int gridY, String name) {
        super(name);

        this.gridX = gridX;
        this.gridY = gridY;

        listOfContent = new ArrayList<>(8);
    }

    /**
     * Adds a new item to the Loot. The loot now contains that item and if the loot is found, the player or a bot will
     * receive the stored item.
     *
     * @param item the item, that is added to the list of content of the Loot
     */
    public void add (Item item) {
        listOfContent.add(item);
    }

    /** The list of everything, that is possible to get, if the loot is collected.
     *
     * @return the list of all Items in the loot
     */
    public List<Item> getStoredItems () {
        return listOfContent;
    }

    /** The x position of the tile, where the Loot is placed. */
    @Override
    public int getGridX () {
        return gridX;
    }

    /** The y position of the tile, where the Loot is placed. */
    @Override
    public int getGridY () {
        return gridY;
    }

    /** Setter for the x position
     *
     * @param gridX the new x position of the tile, where the Loot is placed
     */
    public void setGridX (int gridX) {
        this.gridX = gridX;
    }

    /** Setter for the y position
     *
     * @param gridY the new y position of the tile, where the Loot is placed
     */
    public void setGridY (int gridY) {
        this.gridY = gridY;
    }

    /** Returns the tile on which the Loot is placed.
     *
     * This is what this method is doing: <p>
     * {@code return (TileLike) Main.getContext().getWorld().terrain().tileAt(gridX, gridY);}
     *
     * @return the Tile on with the Loot is placed.
     */
    public TileLike getTile () {
        return (TileLike) Main.getContext().getWorld().terrain().tileAt(gridX, gridY);
    }

    /** the outward appearance of a Loot. Use the LootUI to draw an Loot or to change its Component. */
    public LootUI getLootUI () {
        return lootUI;
    }
}
