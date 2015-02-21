package gui.screen;

import comp.Button;
import comp.List;
import comp.TextBox;
import general.JavaInterop;
import general.Main;
import newent.InventoryLike;
import player.item.Item;
import player.weapon.arrow.ArrowHelper;
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

    /** "Best�tigen" */
    private Button confirmButton;

    /** "Abbrechen" */
    private Button cancelButton;

    /** The name of the selected item is shown here */
    private TextBox selectedItem;

    /**
     * Creates a new Instance of {@link gui.screen.InventoryScreen}.
     */
    public InventoryScreen () {
        super(SCREEN_NAME, SCREEN_INDEX);

        selectedItem = new TextBox(Main.getWindowWidth() - 300, Main.getWindowHeight() - 500, "<Item ausw�hlen>", this);
        selectedItem.declineInput();
        selectedItem.setRoundBorder(true);

        // getItems() can't be used yet as the active player is null
        java.util.List<String> itemList = new ArrayList<>(2);
        itemList.add("<keine Items>");

        inventoryList = new List(50, 50, 150, 400, this, itemList);

        cancelButton = new Button(Main.getWindowWidth() - 300, Main.getWindowHeight() - 180, this, "Abbrechen");

        confirmButton = new Button(Main.getWindowWidth() - 300, Main.getWindowHeight() - 350, this, "Best�tigen");

        inventoryList.setRoundBorder(true);
        inventoryList.setVisible(true);

        Function1<Integer, Object> listSelectCallback = JavaInterop.asScalaFunction((Integer selectedIndex) -> {
            selectedItem.setEnteredText(getItems().get(selectedIndex));
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
                // TODO trigger the effect
                onLeavingScreen(GameScreen.SCREEN_INDEX);
            }
        });

        onScreenEnter.register(new AbstractFunction0<BoxedUnit>() {
            @Override
            public BoxedUnit apply () {
                inventoryList = new List(inventoryList.getX(), inventoryList.getY(), inventoryList.getWidth(), inventoryList.getHeight(),
                            InventoryScreen.this, getItems());
                selectedItem.setEnteredText(selectedItem.getStdText());

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
            if (ArrowHelper.arrowNameToIndex(item.getName()) == -1)
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
