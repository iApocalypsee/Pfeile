package player.item;

import general.Main;
import newent.Bot;
import newent.Entity;
import newent.InventoryEntity;
import newent.Player;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

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
        super(gridX, gridY, name);
        isOpen = false;
    }

    /** you need to open a chest.
     * <p>
     * <b>Call {@link player.item.Chest#changeUIforOpenedChest(java.awt.image.BufferedImage)} at the end. </b>*/
    public abstract void open();

    /** If the chest has been opened, the image of the chest must change.
     *
     * @param imgOfOpenChest the BufferedImage of the opened chest
     */
    public void changeUIforOpenedChest (BufferedImage imgOfOpenChest) {
        setLootUI(createUI(imgOfOpenChest));
    }

    @Override
    public boolean collect (InventoryEntity entity) {
        return defaultCollect(entity.inventory(), this);
    }

    /**
     * This adds a MouseListener [mouseReleased] to the loot, which registers a click at the component. After triggering
     * this mouseListener, a Thread is created, which controls that the selectedEntity and the loot are placed on the same
     * tile. The same Thread calls the <code>collect</code> method from {@link player.item.Collectible}, which also removes
     * the loot from being drawn anymore (if it has been successfully collect).
     * <p>
     * If the <code>lootUI</code>/<code>getLootUI()</code> is <code>null</code>, the method returns doing nothing.
     * <p>
     * I must override it to control, that the chest is open, before collecting items.
     */
    @Override
    protected void addCollectListener(LootUI lootUI) {
        if(lootUI == null) throw new NullPointerException();

        lootUI.component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {

                if (SwingUtilities.isLeftMouseButton(e)) {
                    Thread x = new Thread(() -> {
                        Entity selectedEntity = Main.getContext().entitySelection().selectedEntity();

                        if (isOpen) {
                            // only trigger collect, when the selectedEntity is on the same tile as the loot
                            if (Chest.this.getGridX() == selectedEntity.getGridX() && Chest.this.getGridY() == selectedEntity.getGridY()) {
                                if (selectedEntity instanceof InventoryEntity)
                                    collect((InventoryEntity) selectedEntity);
                            }
                        } else {
                            // TODO remove the key

                            // if the chest isn't open, it will be opened now, if the chest and the player are on the tile.
                            // It can only be opened by players, but collected by every InventoryEntity.
                            if (selectedEntity instanceof Player || selectedEntity instanceof Bot) {
                                if (Chest.this.getGridX() == selectedEntity.getGridX() && Chest.this.getGridY() == selectedEntity.getGridY())
                                    open();
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
}
