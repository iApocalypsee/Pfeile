package gui.screen;

import comp.Button;
import comp.Component;
import comp.List;
import comp.WarningMessage;
import general.Main;
import newent.InventoryLike;
import player.item.EquippableItem;
import player.item.Item;
import player.item.coin.Coin;
import player.item.coin.CoinHelper;
import player.item.potion.Potion;
import player.weapon.arrow.AbstractArrow;
import scala.Tuple2;
import scala.collection.Seq;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The <code>InventoryScreen</code> allows the user to choose items out of the inventory of the active player.
 */
public class InventoryScreen extends Screen {

    public static final int SCREEN_INDEX = 22;

    public static final String SCREEN_NAME = "InventoryScreen";

    /** the List where the items (excluded arrows) are saved */
    private List inventoryList;

    /** "Bestätigen" */
    private Button confirmButton;

    /** "Abbrechen" */
    private Button cancelButton;

    /** The name of the selected item is shown here */
    private Button selectedItem;

    /** The warningMessage which is printed on the screen to say the user something */
    WarningMessage warningMessage;

    /**
     * Creates a new Instance of {@link gui.screen.InventoryScreen}.
     */
    public InventoryScreen () {
        super(SCREEN_NAME, SCREEN_INDEX);

        selectedItem = new Button(Main.getWindowWidth() - 300, Main.getWindowHeight() - 380, this, Main.tr("selectItem"));
        selectedItem.declineInput();
        selectedItem.setRoundBorder(true);

        // getItems() can't be used yet as the active player is null
        java.util.List<String> itemList = new ArrayList<>(2);
        itemList.add(Main.tr("noItems"));

        inventoryList = new List(50, 70, 200, 350, this, itemList);

        cancelButton = new Button(Main.getWindowWidth() - 300, Main.getWindowHeight() - 220, this, "Cancel");

        confirmButton = new Button(Main.getWindowWidth() - 300, Main.getWindowHeight() - 300, this, "Confirm");

        warningMessage = new WarningMessage("null", 40, Main.getWindowHeight() - 105, this);
        warningMessage.setFont(warningMessage.getFont().deriveFont(Component.STD_FONT.getSize2D() * 2));

        //inventoryList.setRoundBorder(true);
        inventoryList.setVisible(true);

        inventoryList.onItemSelected.registerJava(selectedIndex -> {
            final Tuple2<java.util.List<String>, java.util.List<Item>> items = getItems();

            // Are these three lines of code even necessary? I think that this should be
            // applied by default when no item is to be displayed.
            String selectedName = inventoryList.getItems().get(selectedIndex);
            if (selectedName.equals(Main.tr("noItems")))
                selectedName = Main.tr("selectItem");

            selectedItem.setText(selectedName);
            selectedItem.iconify(items._2().get(selectedIndex).getImage());
        });

        inventoryList.acceptInput();

        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                triggerCancelButton();
            }
        });

        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                triggerConfirmButton();
            }
        });

        onScreenEnter.registerJava(() -> {
            final Tuple2<java.util.List<String>, java.util.List<Item>> items = getItems();

            // creating a new List the
            inventoryList.setItems(items._1());

            // iconify the list
            for (int i = 0; i < items._2().size(); i++) {
                if (items._2().get(i) != null)
                    inventoryList.iconify(i, items._2().get(i).getImage());
            }

            selectedItem.setText(Main.tr("selectItem"));
            selectedItem.iconify(null);
            warningMessage.setTransparency(0);
        });

        setPreprocessedDrawingEnabled(false);
    }

    private void triggerCancelButton() {
        onLeavingScreen(GameScreen.SCREEN_INDEX);
    }

    private void triggerConfirmButton() {
        // if nothing is selected yet, you don't need to trigger the rest
        if (selectedItem.getText().equals(Main.tr("selectItem"))) {
            warningMessage.setMessage(Main.tr("pleaseSelectItem"));
            warningMessage.activateMessage();
            return;
        }

        InventoryLike inventory = Main.getContext().getActivePlayer().inventory();
        for (Item item : inventory.getItems()) {
            if (selectedItem.getText().equals(item.getNameDisplayed())) {
                if (item instanceof Potion) {
                    Potion potion = (Potion) item;
                    if (!potion.triggerEffect()) {
                        warningMessage.setMessage(Main.tr("unableToRemove", potion.getNameDisplayed()));
                        warningMessage.activateMessage();
                    } else {
                        warningMessage.setMessage(Main.tr("usedPotion", potion.getNameDisplayed()));
                        warningMessage.activateMessage();
                    }

                } else if (item instanceof EquippableItem) {
                    EquippableItem equippableItem = (EquippableItem) item;

                    Seq<EquippableItem> equippedItemSeq = Main.getContext().getActivePlayer().getEquipment().equippedItems();
                    EquippableItem[] equippedItems = new EquippableItem[equippedItemSeq.size()];
                    equippedItemSeq.copyToArray(equippedItems);

                    if (!equippableItem.equip()) {
                        warningMessage.setMessage(Main.tr("unableToEquip", equippableItem.getNameDisplayed()));
                        warningMessage.activateMessage();
                    } else {
                        warningMessage.setMessage(Main.tr("equippedItem",
                                equippableItem.getNameDisplayed()));
                        warningMessage.activateMessage();
                    }

                } else {
                    warningMessage.setMessage(Main.tr("unableToUse", item.getNameDisplayed()));
                    warningMessage.activateMessage();
                }
                break;
            }
        }
    }

    @Override
    public void keyReleased (KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER: triggerConfirmButton(); break;
            case KeyEvent.VK_B: triggerConfirmButton(); break;
            case KeyEvent.VK_A: triggerCancelButton(); break;
            case KeyEvent.VK_ESCAPE: triggerCancelButton(); break;
        }
    }

    /**
     * Returns a tuple with a <tt>List&lt;String&gt;</tt> containing every name and the associated item
     * at the same index in the other list. <b>No arrows are being added to the list.</b>
     *
     * @return a <code>scala.Tuple2</code> containing a string list with every item's name
     *              and an Item list containing the associated items.
     */
    public scala.Tuple2<java.util.List<String>, java.util.List<Item>> getItems () {
        java.util.List<String> itemList = new LinkedList<>();
        java.util.List<Item> realItemObjList = new LinkedList<>();
        java.util.LinkedList<Coin> coins = new LinkedList<>();

        InventoryLike inventory = Main.getContext().getActivePlayer().inventory();
        // filtering the inventory: only items, which aren't arrows should be added.
        for(Item item : inventory.getItems()) {
            if (!(item instanceof AbstractArrow)) {
                if (!(item instanceof Coin)) {
                    // the name
                    itemList.add(item.getNameDisplayed());
                    // Not the image, the item object itself is needed!
                    realItemObjList.add(item);
                } else {
                    // coins are added to the list later
                    coins.add((Coin) item);
                }
            }
        }

        if (!coins.isEmpty()) {
            java.util.List<Coin>[] coinList = CoinHelper.getSortedCoins(coins);
            for (java.util.List<Coin> aCoinList : coinList) {
                if (!aCoinList.isEmpty()) {
                    itemList.add(aCoinList.get(0).getNameDisplayed() + ": " + aCoinList.size());
                    realItemObjList.add(aCoinList.get(0));
                }
            }
        }

        if (itemList.size() == 0)
            itemList.add(Main.tr("noItems"));

        return new Tuple2<>(itemList, realItemObjList);
    }

    @Override
    public void draw (Graphics2D g) {
        super.draw(g);

        inventoryList.drawChecked(g);
        confirmButton.drawChecked(g);
        cancelButton.drawChecked(g);
        selectedItem.drawChecked(g);
        warningMessage.draw(g);
    }
}
