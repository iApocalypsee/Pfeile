package gui.screen;

import comp.Button;
import comp.Component;
import comp.ConfirmDialog;
import comp.TextBox;
import general.JavaInterop;
import general.Main;
import general.PfeileContext;
import newent.InventoryLike;
import player.weapon.arrow.*;
import scala.Function1;
import scala.runtime.AbstractFunction0;
import scala.runtime.BoxedUnit;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ArrowSelectionScreen extends Screen {

	public static final int SCREEN_INDEX = 3;

	private static ArrowSelectionScreen instance;
	
	/**
	 * Singleton-Methode.
	 * @return The object of ArrowSelectionScreen
	 */
	public static ArrowSelectionScreen getInstance() {
		if(instance == null) {
			instance = new ArrowSelectionScreen();
		}
		return instance;
	}
	
	public static final String SCREEN_NAME = "ArrowSelection";
	
	/** Die TextBox, die Anzeigt, welcher Pfeil ausgew�hlt worden ist */
	private TextBox selectedArrowBox; 
	
	/** Ausgew�hlter Pfeil zum Hinzuf�gen */
	private Class<? extends AbstractArrow> selectedIndex;
	
	/** Button zur Benutzung des Ausgew�hlten Pfeils */
	private Button confirmButton; 
	
	/** Button, um Schussvorgang abzubrechen */
	private Button cancelButton; 
	
	/** Liste der Button mit den m�glichen Pfeilen */
	List<Button> buttonListArrows = new ArrayList<>();
	
	/** Liste der Button f�r andere Aufgaben */
	List<Button> buttonList = new ArrayList<>();
	
	/** confirmOpenDialog */
	private boolean isConfirmDialogOpen = false;

    private Button fireArrowButton, waterArrowButton, iceArrowButton, stormArrowButton, lightningArrowButton, lightArrowButton, shadowArrowButton, stoneArrowButton;
	
	private static final Color TRANSPARENT_BACKGROUND = new Color(0, 0, 0, 185);

	/** This is when the background need to be darkened. [an Dialog is open] */
	private static final Color COLOR_IS_CONFIRM_DIALOG_OPEN = new Color(0, 0, 0, 0.13f);

	/** This is when the background need to be darkened. [an Dialog is open] */
	private static final Color COLOR_IS_CONFIRM_DIALOG_VISIBLE = new Color(0, 0, 0, 0.17f);

	/** the Color of warning message need to be red, bold and it's size need to be doubled (-> size 26/28) */
	private static final Font FONT_WARNING_MESSAGE = new Font(Component.STD_FONT.getFontName(), Font.BOLD, Component.STD_FONT.getSize() * 2);
	
	private comp.List inventoryList;
	
	private List<String> arrowList;

	private int inventoryList_PosX = 60;

	private int inventoryList_PosY = 300;

	private int inventoryList_Height = 210;

	private int inventoryList_Width; 
	
	private float transparencyWarningMessage = 0;
	
	private Point pointWarningMessage;
	
	private String warningMessage = "";
	
	private ConfirmDialog confirmDialog;

    /** apart form creating a new ArrowSelectionScreen, this also initialized it's values Threaded */
	public ArrowSelectionScreen() {
		super(ArrowSelectionScreen.SCREEN_NAME, ArrowSelectionScreen.SCREEN_INDEX);
	}

    /** this initializes ArrowSelectionScreen. It is a threaded method. */
	public void init () {
        // inside run
        Thread initThread = new Thread (() -> {
            /* X-Position des ersten Buttons (Screen) */
            int posXButton = 38;
            /* Y-Position des ersten Buttons (Bildschirm) */
            int posYButtons = 85;

            fireArrowButton = new Button(posXButton, posYButtons, ArrowSelectionScreen.this, "Feuerpfeil");
            waterArrowButton = new Button(posXButton + fireArrowButton.getWidth() + 43, posYButtons, ArrowSelectionScreen.this, "Wasserpfeil");
            stormArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 2, posYButtons, ArrowSelectionScreen.this, "Sturmpfeil");
            stoneArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 3, posYButtons, ArrowSelectionScreen.this, "Steinpfeil");
            iceArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 4, posYButtons, ArrowSelectionScreen.this, "Eispfeil");
            lightningArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 5, posYButtons, ArrowSelectionScreen.this, "Blitzpfeil");
            lightArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 6 , posYButtons, ArrowSelectionScreen.this, "Lichtpfeil");
            shadowArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 7, posYButtons, ArrowSelectionScreen.this, "Schattenpfeil");

            buttonListArrows.add(fireArrowButton);
            buttonListArrows.add(waterArrowButton);
            buttonListArrows.add(stoneArrowButton);
            buttonListArrows.add(iceArrowButton);
            buttonListArrows.add(stormArrowButton);
            buttonListArrows.add(lightningArrowButton);
            buttonListArrows.add(lightArrowButton);
            buttonListArrows.add(shadowArrowButton);

			// resizing for higher resolutions, if necessary
			for (Button button : buttonListArrows) {
				button.setWidth(button.getWidth() * Main.getWindowWidth() / 1366);
				button.setX(button.getX() * Main.getWindowWidth() / 1366);
			}

            fireArrowButton.iconify(ArrowHelper.getArrowImage(FireArrow.INDEX, 0.8f));
            waterArrowButton.iconify(ArrowHelper.getArrowImage(WaterArrow.INDEX, 0.8f));
            stoneArrowButton.iconify(ArrowHelper.getArrowImage(StoneArrow.INDEX, 0.8f));
            iceArrowButton.iconify(ArrowHelper.getArrowImage(IceArrow.INDEX, 0.8f));
            stormArrowButton.iconify(ArrowHelper.getArrowImage(StormArrow.INDEX, 0.8f));
            lightningArrowButton.iconify(ArrowHelper.getArrowImage(LightningArrow.INDEX, 0.8f));
            lightArrowButton.iconify(ArrowHelper.getArrowImage(LightArrow.INDEX, 0.8f));
            shadowArrowButton.iconify(ArrowHelper.getArrowImage(ShadowArrow.INDEX, 0.8f));

            pointWarningMessage = new Point(40, Main.getWindowHeight() - 105);

            MouseHandler mListener = new MouseHandler();

            for (int i = 0; i < ArrowHelper.NUMBER_OF_ARROW_TYPES; i++) {
                buttonListArrows.get(i).setWidth(shadowArrowButton.getWidth() + 14);
                buttonListArrows.get(i).addMouseListener (mListener);
            }

            arrowList = new ArrayList<>();

            final int[] arrowsCount = ArrowHelper.arrowCountInventory();
            arrowList.add("Feuerpfeil " + "[" + arrowsCount[FireArrow.INDEX] + "]");
            arrowList.add("Wasserpfeil " + "[" + arrowsCount[WaterArrow.INDEX] + "]");
            arrowList.add("Sturmpfeil " + "[" + arrowsCount[StormArrow.INDEX] + "]");
            arrowList.add("Steinpfeil " + "[" + arrowsCount[StoneArrow.INDEX] + "]");
            arrowList.add("Eispfeil " + "[" + arrowsCount[IceArrow.INDEX] + "]");
            arrowList.add("Blitzpfeil " + "[" + arrowsCount[LightningArrow.INDEX] + "]");
            arrowList.add("Lichtpfeil " + "[" + arrowsCount[LightArrow.INDEX] + "]");
            arrowList.add("Schattenpfeil " + "[" + arrowsCount[ShadowArrow.INDEX] + "]");

            inventoryList_Width = fireArrowButton.getWidth() + 30;

            inventoryList = new comp.List(inventoryList_PosX, inventoryList_PosY, inventoryList_Width, inventoryList_Height, ArrowSelectionScreen.this, arrowList);
            inventoryList.setRoundBorder(true);
            inventoryList.setVisible(true);

			final Function1<Integer, Object> listSelectCallback = JavaInterop.asScalaFunction((Integer selectedIndex) -> {
				String selectedArrowName = ArrowHelper.arrowIndexToName(selectedIndex);
				selectedArrowBox.setEnteredText(selectedArrowName);
				return BoxedUnit.UNIT;
			});

			inventoryList.onItemSelected.register(listSelectCallback);

			inventoryList.acceptInput();

            selectedArrowBox = new TextBox(Main.getWindowWidth() - (Component.getTextBounds("<Pfeil auswählen>", Component.STD_FONT).width + 30) - 37,
                    300, "<Pfeil auswählen>", ArrowSelectionScreen.this);
            selectedArrowBox.setVisible(true);
            selectedArrowBox.setRoundBorder(true);
            selectedArrowBox.setHeight(selectedArrowBox.getHeight() + 1);
            selectedArrowBox.setEnteredText(selectedArrowBox.getStdText());
            selectedArrowBox.setWidth(Component.getTextBounds(selectedArrowBox.getEnteredText(), Component.STD_FONT).width + Component.STD_INSETS.left + Component.STD_INSETS.right + 3);
            selectedArrowBox.declineInput();

            confirmButton = new Button(selectedArrowBox.getX(), selectedArrowBox.getY() + selectedArrowBox.getHeight() + 30, ArrowSelectionScreen.this, "Confirm");
            confirmButton.setRoundBorder(true);
            confirmButton.setVisible(true);
            confirmButton.addMouseListener(new MouseHandler());

            cancelButton = new Button(confirmButton.getX(), confirmButton.getY() + selectedArrowBox.getHeight() + 30, ArrowSelectionScreen.this, "Abbrechen");
            cancelButton.setRoundBorder(true);
            cancelButton.setVisible(true);
            cancelButton.addMouseListener(new MouseHandler());

            buttonList.add(confirmButton);
            buttonList.add(cancelButton);

            confirmDialog = new ConfirmDialog(stoneArrowButton.getX(), stoneArrowButton.getY() + 260, ArrowSelectionScreen.this, "");
            confirmDialog.setVisible(false);
            confirmDialog.getOk().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (Main.getContext().getActivePlayer().arrowNumberFreeSetUsable().get() > 0) {
                        final InventoryLike inventory = Main.getContext().getActivePlayer().inventory();
                        if (!inventory.put(ArrowHelper.instanceArrow(selectedIndex))) {
                            if (inventory.maximumSize() - inventory.currentSize() <= 0) {
                                warningMessage = "Das Inventar ist voll: Maximale Inventargröße " + inventory.maximumSize();
                            } else if (Main.getContext().getActivePlayer().arrowNumberFreeSetUsable().get() <= 0){
                                warningMessage = "Es wurde bereits die maximale Anzahl von freisetzbaren Pfeilen hinzugefügt. Sie beträgt: " + PfeileContext.ARROW_NUMBER_FREE_SET().get() + "";

                                // Es können jetzt beliebig viele Pfeile eines Types ausgewählt werden
                                // } else if (inventory.maxStack(selectedIndex) >= inventory.getItemCount(selecteddIndex)) {
                                //    warningMessage = "Das Inventar kann maximal " + inventory.maxStack(selectedIndex) + " " +
                                //                        selectedIndex.getSimpleName() + " Pfeile aufnehmen";

                            } else {
                                System.err.println("Could not add arrow to inventory (with " +
                                        (inventory.maximumSize() - inventory.currentSize()) + " remaining space) arrow index: " + selectedIndex);
                                warningMessage = "Could not add arrow to inventory (with " +
                                        (inventory.maximumSize() - inventory.currentSize()) + " remaining space) arrow index: " + selectedIndex;
                            }
                            transparencyWarningMessage = 1f;
                        } else {
                            Main.getContext().getActivePlayer().arrowNumberFreeSetUsable().set(Main.getContext().getActivePlayer().arrowNumberFreeSetUsable().get() - 1);
							// when a new arrow is selected, it is usually also the arrow the user is going to use.
							selectedArrowBox.setEnteredText(ArrowHelper.instanceArrow(selectedIndex).getName());
                            updateInventoryList();
                        }
                    } else {
                        warningMessage = "Es wurde bereits die maximale Anzahl von freisetzbaren Pfeilen hinzugefügt. Sie beträgt: " + PfeileContext.ARROW_NUMBER_FREE_SET().get();
                        transparencyWarningMessage = 1f;
                    }
                    closeConfirmDialogQuestion();
                }
            });
            confirmDialog.getCancel().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    closeConfirmDialogQuestion();
                }
            });

            updateInventoryList();
            onScreenEnter.register(new AbstractFunction0<BoxedUnit>() {
                @Override
                public BoxedUnit apply () {
                    updateInventoryList();
                    transparencyWarningMessage = 0;
                    return BoxedUnit.UNIT;
                }
            });
        });
        initThread.setPriority(7);
        initThread.start();
    }
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		
		g.setColor(TRANSPARENT_BACKGROUND);
		g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());

        GameScreen.getInstance().getMap().draw(g);
        GameScreen.getInstance().getAttackDrawer().draw(g);

        Main.getContext().getTimeClock().draw(g);
		
		// Zeichnen der Pfeilauswahl-Buttons
		for (Button buttonListArrow : buttonListArrows) {
			buttonListArrow.draw(g);
		}
		
		// Zeichen des Pfeilinventars
		inventoryList.draw(g);
		
		// Zeichnen der Auswahlbox
		selectedArrowBox.draw(g);
		
		if (isConfirmDialogOpen) {
			g.setColor(COLOR_IS_CONFIRM_DIALOG_OPEN);
			g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
		}
		
		// Zeichnen der Button zur Auswahl
		for (Button button : buttonList) {
			button.draw(g);
		} 
		
		if (confirmDialog.isVisible()) {
			g.setColor(COLOR_IS_CONFIRM_DIALOG_VISIBLE);
			g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
			confirmDialog.draw(g);
		}

		if (transparencyWarningMessage > 0) {
			g.setColor(new Color(1f, 0f, 0f, transparencyWarningMessage));
			g.setFont(FONT_WARNING_MESSAGE);
			g.drawString(warningMessage, pointWarningMessage.x, pointWarningMessage.y);

			transparencyWarningMessage = transparencyWarningMessage - 0.013f;

			if (transparencyWarningMessage < 0)
				transparencyWarningMessage = 0;
		}
	}

	
	// LISTENER
	@Override
	public void keyDown (KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			onLeavingScreen(PauseScreen.SCREEN_INDEX);
		}
        // "abbrechen" --> KeyEvent.VK_A
		if (e.getKeyCode() == KeyEvent.VK_A) {
			onLeavingScreen(GameScreen.SCREEN_INDEX);
		}
	}

	public Class<? extends AbstractArrow> getSelectedIndex() {
		return selectedIndex;
	}
	
	/** Listener f�r die Buttons */
	private class MouseHandler extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent e) {
            final InventoryLike inventory = Main.getContext().getActivePlayer().inventory();
			
			if (fireArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Feuerpfeil hinzufügen?");
				selectedIndex = FireArrow.class;
			}
			if (iceArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Eispfeil hinzufügen?");
				selectedIndex = IceArrow.class;
			}
			if (waterArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Wasserpfeil hinzufügen?");
				selectedIndex = WaterArrow.class;
			}
			if (stormArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Sturmpfeil hinzufügen?");
				selectedIndex = StormArrow.class;
			}
			if (stoneArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Steinpfeil hinzufügen?");
				selectedIndex = StoneArrow.class;
            }
			if (lightningArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Blitzpfeil hinzufügen?");
				selectedIndex = LightningArrow.class;
			}
			if (lightArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Lichtpfeil hinzufügen?");
				selectedIndex = LightArrow.class;
			}
			if (shadowArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Schattenpfeil hinzufügen?");
				selectedIndex = ShadowArrow.class;
			}
			if (cancelButton.getBounds().contains(e.getPoint())) {
				onLeavingScreen(GameScreen.SCREEN_INDEX);
			} 
			if (confirmButton.getBounds().contains(e.getPoint())) {
				if (!selectedArrowBox.getEnteredText().equals(selectedArrowBox.getStdText())) {
                    for (int i = 0; i < inventory.currentSize(); i++) {
                        if (inventory.javaItems().get(i).getClass() == ArrowHelper.reformArrow(selectedArrowBox.getEnteredText())) {
                            selectedIndex = ArrowHelper.reformArrow(selectedArrowBox.getEnteredText());
	                        onLeavingScreen(AimSelectionScreen.SCREEN_INDEX);
	                        break;
                        }
                    }
					if (getManager().getActiveScreen() == ArrowSelectionScreen.getInstance()) {
						warningMessage = "Kein " + selectedArrowBox.getEnteredText() + " im Inventar.";
						transparencyWarningMessage = 1f;
					} else
                        return;
				} else {
					warningMessage = "Kein Pfeil ausgewählt";
					transparencyWarningMessage = 1f;
				}				
			}
			
			if (confirmDialog.isVisible()) {
				if (confirmDialog.getCancel().getPreciseRectangle().contains(e.getPoint()))
					closeConfirmDialogQuestion();
				if (confirmDialog.getOk().getPreciseRectangle().contains(e.getPoint()))
					closeConfirmDialogQuestion();
			}
		}
	}
	


	/**
	 * Opens the "Are you sure?" dialog with specified question.
	 * @param question The question to display.
	 */
	private void openConfirmQuestion (String question) {
		confirmDialog.setQuestionText(question);
		confirmDialog.setVisible(true);
		isConfirmDialogOpen = true;

		for (Button button : buttonList) {
			button.declineInput();
		}
		inventoryList.declineInput();
	}

	/**
	 * Closes the "Are you sure?" dialog.
	 */
	private void closeConfirmDialogQuestion () {
		confirmDialog.setQuestionText("");
		confirmDialog.setVisible(false);
		isConfirmDialogOpen = false; 
		
		for (Button button : buttonList) {
			button.acceptInput();
		}
		inventoryList.acceptInput();
	}

	/**
	 * Updates the inventory list of the player registered in the client as the "active" player.
	 */
	protected void updateInventoryList () {
        Thread updateThreaded = new Thread() {
            @Override
            public void run () {
                arrowList.clear();

                final int[] arrowsCount = ArrowHelper.arrowCountInventory();
                arrowList.add("Feuerpfeil " + "[" + arrowsCount[FireArrow.INDEX] + "]");
                arrowList.add("Wasserpfeil " + "[" + arrowsCount[WaterArrow.INDEX] + "]");
                arrowList.add("Sturmpfeil " + "[" + arrowsCount[StormArrow.INDEX] + "]");
                arrowList.add("Steinpfeil " + "[" + arrowsCount[StoneArrow.INDEX] + "]");
                arrowList.add("Eispfeil " + "[" + arrowsCount[IceArrow.INDEX] + "]");
                arrowList.add("Blitzpfeil " + "[" + arrowsCount[LightningArrow.INDEX] + "]");
                arrowList.add("Lichtpfeil " + "[" + arrowsCount[LightArrow.INDEX] + "]");
                arrowList.add("Schattenpfeil " + "[" + arrowsCount[ShadowArrow.INDEX] + "]");

				inventoryList = new comp.List(inventoryList_PosX, inventoryList_PosY, inventoryList_Width, inventoryList_Height, ArrowSelectionScreen.getInstance(), arrowList);
				inventoryList.setRoundBorder(true);
				inventoryList.setVisible(true);

                if (inventoryList.isAcceptingInput()) {
                    inventoryList.acceptInput();
                } else {
                    inventoryList.declineInput();
                }
            }
        };
        updateThreaded.setDaemon(true);
        updateThreaded.setPriority(7);
        updateThreaded.start();
	}
}
