package gui.screen;

import comp.*;
import comp.Button;
import comp.Component;
import comp.List;
import general.JavaInterop;
import general.Main;
import newent.InventoryLike;
import player.item.EquippableItem;
import player.item.Item;
import player.item.potion.Potion;
import player.weapon.arrow.AbstractArrow;
import scala.Function1;
import scala.runtime.AbstractFunction0;
import scala.runtime.BoxedUnit;

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

    public static final String SCREEN_NAME = "Inventory";

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

        inventoryList = new List(50, 70, 150, 300, this, itemList);

        cancelButton = new Button(Main.getWindowWidth() - 300, Main.getWindowHeight() - 220, this, "Abbrechen");

        confirmButton = new Button(Main.getWindowWidth() - 300, Main.getWindowHeight() - 300, this, "Bestätigen");

        warningMessage = new WarningMessage("null", 40, Main.getWindowHeight() - 105, this);
        warningMessage.setFont(warningMessage.getFont().deriveFont(Component.STD_FONT.getSize2D() * 2));

        inventoryList.setRoundBorder(true);
        inventoryList.setVisible(true);

        Function1<Integer, Object> listSelectCallback = JavaInterop.asScalaFunction((Integer selectedIndex) -> {
            String selectedName = getItems().get(selectedIndex);
            if (selectedName.equals("<keine Items>"))
                selectedName = "<Item auswählen>";
            selectedItem.setText(selectedName);

            InventoryLike inventory = Main.getContext().getActivePlayer().inventory();
            for (Item item : inventory.javaItems()) {
                if (selectedItem.getText().equals(item.getName())) {
                    if (item instanceof Potion) {
                        Potion potion = (Potion) item;
                        selectedItem.iconify(potion.getPotionUI().getComponent().getBufferedImage());

                    } else if (item instanceof EquippableItem) {
                        EquippableItem equippableItem = (EquippableItem) item;
                        selectedItem.iconify(equippableItem.getImage());
                    } else {
                        warningMessage.setMessage("Das Item " + item.getName() + " wurde nicht gefunden.");
                        warningMessage.activateMessage();
                    }
                    break;
                }
            }
            return BoxedUnit.UNIT;
        });

        inventoryList.onItemSelected.register(listSelectCallback);

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

                InventoryLike inventory = Main.getContext().getActivePlayer().inventory();
                for (Item item : inventory.javaItems()) {
                    if (selectedItem.getText().equals(item.getName())) {
                        if (item instanceof Potion) {
                            Potion potion = (Potion) item;
                            if (!potion.triggerEffect()) {
                                warningMessage.setMessage("Die " + potion.getName() + " kann jetzt nicht verwendet werden.");
                                warningMessage.activateMessage();
                            }

                        } else if (item instanceof EquippableItem) {
                            EquippableItem equippableItem = (EquippableItem) item;
                            if (!equippableItem.equip()) {
                                warningMessage.setMessage("Die " + equippableItem.getName() + " kann nicht ausgerüstet werden.");
                                warningMessage.activateMessage();
                            }

                        } else {
                            warningMessage.setMessage("Das ausgewählte item " + item.getName() + " kann nicht verwendet werden.");
                            warningMessage.activateMessage();
                        }
                        break;
                    }
                }
                onLeavingScreen(GameScreen.SCREEN_INDEX);
            }
        });

        onScreenEnter.register(new AbstractFunction0<BoxedUnit>() {
            @Override
            public BoxedUnit apply () {
                inventoryList = new List(inventoryList.getX(), inventoryList.getY(), inventoryList.getWidth(), inventoryList.getHeight(),
                            InventoryScreen.this, getItems());
                selectedItem.setText("<Item auswählen>");
                selectedItem.iconify(null);
                warningMessage.setTransparency(0);

                return BoxedUnit.UNIT;
            }
        });

        setPreprocessedDrawingEnabled(true);
    }

    /**
     * No arrows are added.
     *
     * @return a <code>LinkedList</code> containing a String for every name of an item.
     */
    public java.util.List<String> getItems () {
        java.util.List<String> itemList = new LinkedList<>();
        InventoryLike inventory = Main.getContext().getActivePlayer().inventory();
        // filtering the inventory: only items, which aren't arrows should be added.
        for(Item item : inventory.javaItems()) {
            if (!(item instanceof AbstractArrow))
                itemList.add(item.getName());
        }
        if (itemList.size() == 0)
            itemList.add("<keine Items>");
        return itemList;
    }

    @Override
    public void draw (Graphics2D g) {
        super.draw(g);
    }
}
