package player.item;

import general.Main;
import gui.screen.GameScreen;
import newent.*;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * The superclass for chests. To open a chest you need to give up something (arrows, gold, ...) or find a key
 * (maybe in the future). That's why only Players and Bots can open an Chest. Generally Chest contains more Potions than
 * Treasures.
 */
public abstract class Chest extends Loot {
    protected boolean isOpen;

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
        isOpen = false;
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
        this(gridX, gridY, null, name);
    }

    /** you need to open a chest.
     * <p>
     * <b>Call {@link player.item.LootUI#changeUI(java.awt.image.BufferedImage)} at the end. </b>*/
    public abstract void open();

    @Override
    public boolean collect (InventoryEntity entity) {
        return defaultCollect(entity.inventory(), this);
    }

    /**
     * This adds a MouseListener [mouseReleased] to the loot, which registers a click at the component. After triggering
     * this mouseListener, a Thread is created, which controls that the selectedEntity and the loot are placed on the same
     * tile. The same Thread calls the <code>collect</code> method from {@link player.item.Collectible}, which also removes
     * the loot from being drawn anymore (if it has been successfully collect).
     * <p>The MouseListener added by this method also triggers the <code>additionalContent()</code>method</p>
     * <p>
     * If the <code>lootUI</code>/<code>getLootUI()</code> is <code>null</code>, the method returns doing nothing.
     * <p>
     * I must override it to control, that the chest is open, before collecting items.
     */
    @Override
    protected void addCollectListener(LootUI lootUI) {
        if(lootUI == null)
            throw new NullPointerException();

        lootUI.component.addMouseListener(new MouseAdapter() {
            private Thread x;

            @Override
            public void mouseReleased (MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {

                    // If there is an old Thread, wait until the thread has stopped...
                    if (x != null) {
                        if (x.isAlive()) {
                            try {
                                x.join();
                            } catch (InterruptedException e1) { e1.printStackTrace(); }
                        }
                    }

                    x = new Thread(() -> {
                        Entity selectedEntity = Main.getContext().entitySelection().selectedEntity();

                        if (isOpen) {
                            // only trigger collect, when the selectedEntity is on the same tile as the loot
                            if (Chest.this.getGridX() == selectedEntity.getGridX() && Chest.this.getGridY() == selectedEntity.getGridY()) {
                                if (selectedEntity instanceof InventoryEntity) {
                                    if (collect((InventoryEntity) selectedEntity))
                                        lootUI.getComponent().removeMouseListener(this);
                                }
                            }
                        } else {
                            // if the chest isn't open, it will be opened now, if the chest and the player are on the tile.
                            // It can only be opened by players, but collected by every InventoryEntity. You need a key to open it.
                            if (selectedEntity instanceof Player || selectedEntity instanceof Bot) {
                                if (Chest.this.getGridX() == selectedEntity.getGridX() && Chest.this.getGridY() == selectedEntity.getGridY()) {

                                    // removing the key
                                    boolean removed = false;

                                    InventoryLike inventory = ((CombatUnit) selectedEntity).inventory();

                                    List<Item> inventoryList = inventory.javaItems();
                                    if (Chest.this instanceof DefaultChest) {
                                        for (Item item : inventoryList) {
                                            if (item instanceof KeyDefaultChest) {
                                                inventory.remove(item);
                                                removed = true;
                                                break;
                                            }
                                        }
                                    } else if (Chest.this instanceof RoundChest) {
                                        for (Item item : inventoryList) {
                                            if (item instanceof KeyRoundChest) {
                                                inventory.remove(item);
                                                removed = true;
                                                break;
                                            }
                                        }
                                    } else { // register new Chest
                                        try {
                                            throw new ClassNotFoundException("add the typ of chest your using here");
                                        } catch (ClassNotFoundException e1) { e1.printStackTrace(); }
                                    }

                                    if (removed) {
                                        // adding additional drops (depending on the fortune-stat)
                                        additionalContent();
                                        open();
                                    } else {
                                        GameScreen.getInstance().setWarningMessage(Main.tr("keyRequired"));
                                        GameScreen.getInstance().activateWarningMessage();
                                    }
                                }
                            }
                        }
                    }, "CollectLootListener");

                    x.setDaemon(true);
                    x.start();
                }
            }
        });
    }
}
