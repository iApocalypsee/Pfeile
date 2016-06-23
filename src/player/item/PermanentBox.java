package player.item;

import general.LogFacility;
import general.Main;
import gui.screen.GameScreen;
import newent.Entity;
import newent.InventoryEntity;
import newent.Player;
import player.item.coin.Coin;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a Chest, which can't be removed from the map by clicking at it twice. It isn't a real chest - it's more like
 * a box, because you don't need a key to open it. That's why it isn't a subclass of <code>Chest</code>.
 * This is a subclass of Loot, which doesn't drop additional content.
 */
public class PermanentBox extends Loot {

    private static BufferedImage image;

    static {
        String path = "resources/gfx/item textures/loot textures/permanentBox.png";
        try {
            image = ImageIO.read(PermanentBox.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("Cannot load " + path, LogFacility.LoggingLevel.Error);
        }
    }

    /**
     * The LootUI will be created automatically, based on {@link player.item.Loot#createUI()}
     * You can override the lootUI later.
     *
     * @param gridX the x-position of tile where the loot should be placed
     * @param gridY the y-position of the where the loot should be placed
     */
    public PermanentBox (int gridX, int gridY) {
        super(gridX, gridY, "PermanentBox");
    }

    /**
     * The PermanentBox spawns at the position (<code>owner.getGridX()</code>|<code>owner.getGridY()</code>).
     * The Player isn't the owner of the box, as anybody is able to open the box without Key, but usually, the enemy
     * doesn't go to the tile, you're standing.
     *
     * @param owner it determines the position of the box.
     */
    public PermanentBox (Player owner) {
        this(owner.getGridX(), owner.getGridY());
    }

    /**
     * <b>Every stored Item is added to the inventory. You may change that, if you need.</b>
     *
     * @param entity any Entity, which has an Inventory
     * @return Has the loot been successfully added to the inventory?
     */
    @Override
    public boolean collect (InventoryEntity entity) {
        for (Item item : getStoredItems()) {
            // must return true
            entity.inventory().put(item);
        }

        // The user must now, what he/she just got.
        int money = 0;
        List<Item> otherItems = new ArrayList<>(7);

        for (Item item : getStoredItems()) {
            if (item instanceof Coin)
                money = money + ((Coin) item).getValue();
            else
                otherItems.add(item);
        }
        GameScreen.getInstance().setWarningMessage(getName() + ": {Geld: " + money + " | Items: " + otherItems + "}");
        GameScreen.getInstance().activateWarningMessage();

        // secure, that no item remains
        getStoredItems().clear();

        // the money has changed probably
        Main.getContext().getActivePlayer().onMoneyChanged().apply();

        return getStoredItems().isEmpty();
    }

    /**
     * Every item can be drawn, so it must have a BufferedImage. Override this call with
     * a link to the component or a loaded static BufferedImage.
     *
     * @return the {@link java.awt.image.BufferedImage} of the item
     */
    @Override
    public BufferedImage getImage () {
        return image;
    }

    /**
     * This adds a MouseListener [mouseReleased] to the loot, which registers a click at the component. After triggering
     * this mouseListener, a Thread is created, which controls that the selectedEntity and the loot are placed on the same
     * tile. The same Thread calls the <code>collect</code> method from {@link player.item.Collectible}, which also removes
     * the loot from being drawn anymore (if it has been successfully collect).
     * <p>
     * If the <code>lootUI</code>/<code>getLootUI()</code> is <code>null</code>, the method returns doing nothing.
     *
     * @param lootUI the UI
     */
    @Override
    protected void addCollectListener (LootUI lootUI) {
        if(lootUI == null)
            throw new NullPointerException();

        lootUI.component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {

                if (SwingUtilities.isLeftMouseButton(e)) {
                    Thread x = new Thread(() -> {
                        Entity selectedEntity = Main.getContext().entitySelection().selectedEntity();

                        // only trigger collect, when the selectedEntity is on the same tile as the loot
                        if (PermanentBox.this.getGridX() == selectedEntity.getGridX() && PermanentBox.this.getGridY() == selectedEntity.getGridY()) {
                            if (selectedEntity instanceof InventoryEntity) {
                                // A click only adds everything from the box into the inventory.
                                collect((InventoryEntity) selectedEntity);
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

    /**
     * If the player has an increased fortune stat during this turn, this method, will add additional content.
     * Additional content only works with the active player.
     */
    @Override
    public void additionalContent () {
        // A permanent box is not allowed to have additional drops.
    }
}
