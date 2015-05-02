package player.item;

import comp.ImageComponent;
import general.Main;
import general.Property;
import general.PropertyWorkaround;
import gui.screen.GameScreen;
import newent.Entity;
import newent.InventoryEntity;
import player.BoardPositionable;
import player.item.coin.Coin;
import world.TileLike;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Anything dropped by enemies or every chest or treasure is extended from Loot. Loot is a subclass of {@link Item}.
 */
public abstract class Loot extends Item implements BoardPositionable, Collectible {

    protected int gridX;

    protected int gridY;

    /** Subclasses must override this value */
    private Property<LootUI> lootUI = PropertyWorkaround.apply();

    /** everything, that is stored inside this loot and can be received by collecting this Loot. */
    protected List<Item> listOfContent;

    /**
     * The LootUI will be created automatically, based on {@link Loot#createUI()}
     * You can override the lootUI later.
     *
     * @param gridX the x-position of tile where the loot should be placed
     * @param gridY the y-position of the where the loot should be placed
     * @param name the name of the item
     * @see player.item.Loot#Loot(int, int, LootUI, String)
     */
    protected Loot (int gridX, int gridY, String name) {
        this(gridX, gridY, null, name);

        lootUI.setLazyInit(() -> {
            final LootUI returnedUI = createUI();
            // Only after LootUI has been created, the mouseListener for collecting the Loot can be added.
            addCollectListener(returnedUI);
            return returnedUI;
        });
    }

    /** Creates a new Loot on the Tile at (gridX, gridY) with the specified name and the lootUI. */
    protected Loot (int gridX, int gridY, LootUI lootUI, String name) {
        super(name);

        this.gridX = gridX;
        this.gridY = gridY;
        listOfContent = new LinkedList<>();

        // Property mechanism would not be happy about null values given to it.
        if(lootUI != null) {
            this.lootUI.set(lootUI);
            addCollectListener(this.lootUI.get());
        }
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

    /**
     * This adds a MouseListener [mouseReleased] to the loot, which registers a click at the component. After triggering
     * this mouseListener, a Thread is created, which controls that the selectedEntity and the loot are placed on the same
     * tile. The same Thread calls the <code>collect</code> method from {@link player.item.Collectible}, which also removes
     * the loot from being drawn anymore (if it has been successfully collect).
     * <p>
     * If the <code>lootUI</code>/<code>getLootUI()</code> is <code>null</code>, the method returns doing nothing.
     */
    protected void addCollectListener (LootUI lootUI) {
        if(lootUI == null) throw new NullPointerException();
        lootUI.component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                // only register it at left-click
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Thread x = new Thread(() -> {
                        Entity selectedEntity = Main.getContext().entitySelection().selectedEntity();
                        // only trigger collect, when the selectedEntity is on the same tile as the loot
                        if (Loot.this.getGridX() == selectedEntity.getGridX() && Loot.this.getGridY() == selectedEntity.getGridY()) {
                            if (selectedEntity instanceof InventoryEntity) {
                                if (collect((InventoryEntity) selectedEntity))
                                    getLootUI().component.removeMouseListener(this);
                            }
                        }
                    });

                    x.setDaemon(true);
                    x.setName("Collect Loot Listener");
                    x.start();
                }
            }
        });
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

    /** Setter for the x position. Changes the GUI position based on the new tile as well.
     *
     * @param gridX the new x position of the tile, where the Loot is placed
     */
    public void setGridX (int gridX) {
        this.gridX = gridX;

        // changing GUI elements
        lootUI.ifdef(definedUI -> {
            definedUI.setOnTile(getTile());
        });
    }

    /** Setter for the y position. Changes the GUI position as well.
     *
     * @param gridY the new y position of the tile, where the Loot is placed
     */
    public void setGridY (int gridY) {
        this.gridY = gridY;

        // changing GUI elements
        lootUI.ifdef(definedUI -> {
            definedUI.setOnTile(getTile());
        });
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
        return lootUI.get();
    }

    /**
     * Replacing the LootUI with the specified new LootUI
     *
     * @param lootUI the new outward appearance
     */
    protected void setLootUI (LootUI lootUI) {
        this.lootUI.set(lootUI);
    }

    /**
     * Creates a default LootUI, when {@link comp.ImageComponent} is used.
     * Calling the draw method will draw the imageComponent.
     *
     * @return a LookUI object based on an ImageComponent
     */
    protected LootUI createUI () {
        Rectangle2D tileBounds = getTile().getComponent().getPreciseRectangle();

        ImageComponent component = new ImageComponent(
                (int) (tileBounds.getCenterX() - 0.5 * getImage().getWidth()),
                (int) (tileBounds.getCenterY() - 0.5 * getImage().getHeight()), getImage(), GameScreen.getInstance());

        return new LootUI(component) {
            @Override
            public void draw (Graphics2D g) {
                ImageComponent lootComponent = (ImageComponent) getComponent();
                lootComponent.draw(g);
            }
        };
    }

    protected LootUI createUI (BufferedImage image) {
        Rectangle2D tileBounds = getTile().getComponent().getPreciseRectangle();

        ImageComponent component = new ImageComponent(
                (int) (tileBounds.getCenterX() - 0.5 * image.getWidth()),
                (int) (tileBounds.getCenterY() - 0.5 * image.getHeight()), image, GameScreen.getInstance());

        return new LootUI(component) {
            @Override
            public void draw (Graphics2D g) {
                ImageComponent lootComponent = (ImageComponent) getComponent();
                lootComponent.draw(g);
            }
        };
    }

    @Override
    public String toString () {
        int money = 0;
        List<Item> otherItems = new ArrayList<>(7);

        for (Item item : getStoredItems()) {
            if (item instanceof Coin)
                money = money + ((Coin) item).getValue();
            else
                otherItems.add(item);
        }

        return getName() + " [@Tile: " + gridX + "|" + gridY + "]" + "-{Money: " + money + " | Items: " + otherItems + "}";
    }
}
