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

import java.awt.*;
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

        selectedItem = new Button(Main.getWindowWidth() - 300, Main.getWindowHeight() - 380, this, "<Item auswählen>");
        selectedItem.declineInput();
        selectedItem.setRoundBorder(true);

        // getItems() can't be used yet as the active player is null
        java.util.List<String> itemList = new ArrayList<>(2);
        itemList.add("<keine Items>");

        inventoryList = new List(50, 70, 200, 350, this, itemList);

        cancelButton = new Button(Main.getWindowWidth() - 300, Main.getWindowHeight() - 220, this, "Abbrechen");

        confirmButton = new Button(Main.getWindowWidth() - 300, Main.getWindowHeight() - 300, this, "Bestätigen");

        warningMessage = new WarningMessage("null", 40, Main.getWindowHeight() - 105, this);
        warningMessage.setFont(warningMessage.getFont().deriveFont(Component.STD_FONT.getSize2D() * 2));

        //inventoryList.setRoundBorder(true);
        inventoryList.setVisible(true);

        inventoryList.onItemSelected.registerJava(selectedIndex -> {
            final Tuple2<java.util.List<String>, java.util.List<Item>> items = getItems();

            // Are these three lines of code even necessary? I think that this should be
            // applied by default when no item is to be displayed.
            String selectedName = inventoryList.getItems().get(selectedIndex);
            if (selectedName.equals("<keine Items>"))
                selectedName = "<Item auswählen>";

            selectedItem.setText(selectedName);
            selectedItem.iconify(items._2().get(selectedIndex).getImage());
        });

        inventoryList.acceptInput();

        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                onLeavingScreen(GameScreen.SCREEN_INDEX);
            }
        });

        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                // if nothing is selected yet, you don't need to trigger the rest
                if (selectedItem.getText().equals("<Item auswählen>")) {
                    warningMessage.setMessage("Wähle erst ein Item aus!");
                    warningMessage.activateMessage();
                    return;
                }

                boolean isLeavingScreen = true;

                InventoryLike inventory = Main.getContext().getActivePlayer().inventory();
                for (Item item : inventory.javaItems()) {
                    if (selectedItem.getText().equals(item.getName())) {
                        if (item instanceof Potion) {
                            Potion potion = (Potion) item;
                            if (!potion.triggerEffect()) {
                                warningMessage.setMessage("Die " + potion.getName() + " konnte nicht entfernt werden.");
                                warningMessage.activateMessage();
                                isLeavingScreen = false;
                            }

                        } else if (item instanceof EquippableItem) {
                            EquippableItem equippableItem = (EquippableItem) item;
                            if (!equippableItem.equip()) {
                                warningMessage.setMessage("Die " + equippableItem.getName() + " kann nicht ausgerüstet werden.");
                                warningMessage.activateMessage();
                                isLeavingScreen = false;
                            }

                        } else {
                            warningMessage.setMessage("Das ausgewählte Item " + item.getName() + " kann nicht verwendet werden.");
                            warningMessage.activateMessage();
                            isLeavingScreen = false;
                        }
                        break;
                    }
                }
                if (isLeavingScreen)
                    onLeavingScreen(GameScreen.SCREEN_INDEX);
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

            selectedItem.setText("<Item auswählen>");
            selectedItem.iconify(null);
            warningMessage.setTransparency(0);
        });

        setPreprocessedDrawingEnabled(false);
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
        for(Item item : inventory.javaItems()) {
            if (!(item instanceof AbstractArrow)) {
                if (!(item instanceof Coin)) {
                    // the name
                    itemList.add(item.getName());
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
                    itemList.add(aCoinList.get(0).getName() + ": " + aCoinList.size());
                    realItemObjList.add(aCoinList.get(0));
                }
            }
        }

        if (itemList.size() == 0)
            itemList.add("<keine Items>");

        return new Tuple2<>(itemList, realItemObjList);
    }

    @Override
    public void draw (Graphics2D g) {
        super.draw(g);

        inventoryList.draw(g);
        confirmButton.draw(g);
        cancelButton.draw(g);
        selectedItem.draw(g);
        warningMessage.draw(g);
    }
}
