package gui.screen;

import comp.Button;
import comp.Component;
import comp.*;
import general.Main;
import general.PfeileContext;
import newent.InventoryLike;
import player.weapon.arrow.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
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

	/** This is when the background need to be darkened. [an Dialog is open] */
	private static final Color COLOR_IS_CONFIRM_DIALOG_OPEN = new Color(0, 0, 0, 0.13f);

	/** This is when the background need to be darkened. [an Dialog is open] */
	private static final Color COLOR_IS_CONFIRM_DIALOG_VISIBLE = new Color(0, 0, 0, 0.17f);
	
	private comp.List inventoryList;
	
	private List<String> arrowList;

	private int inventoryList_PosX = 60;

	private int inventoryList_PosY = 300;

	private int inventoryList_Height = 210;

	private int inventoryList_Width;

    private WarningMessage warningMessage;
	
	private ConfirmDialog confirmDialog;

    /** apart form creating a new ArrowSelectionScreen, this also initialized it's values Threaded */
	public ArrowSelectionScreen() {
		super(ArrowSelectionScreen.SCREEN_NAME, ArrowSelectionScreen.SCREEN_INDEX);
	}

    /** this initializes ArrowSelectionScreen. It is a threaded method. */
	public void init (PfeileContext context) {
        // inside run
        Thread initThread = new Thread (() -> {

            // Compare the following implemention of the arrow selections buttons with ArrowSelectionScreenPreSet, since the design is the same.

            /* X-Position des ersten Buttons (Screen) */
            int posXButton = 38;
            /* Y-Position des ersten Buttons (Bildschirm) */
            int posYButtons = 85;
            /** the gap between the arrow buttons. */
            int gap = 45;

            fireArrowButton = new Button(posXButton, posYButtons, ArrowHelper.getArrowImage(FireArrow.INDEX, 0.8f),
                    ArrowSelectionScreen.this, Main.tr("fireArrow"));
            waterArrowButton = new Button(posXButton + fireArrowButton.getWidth() + gap, posYButtons, ArrowHelper.getArrowImage(WaterArrow.INDEX, 0.8f),
                    ArrowSelectionScreen.this, Main.tr("waterArrow"));
            stormArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + gap) * 2, posYButtons, ArrowHelper.getArrowImage(StormArrow.INDEX, 0.8f),
                    ArrowSelectionScreen.this, Main.tr("stormArrow"));
            stoneArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + gap) * 3, posYButtons, ArrowHelper.getArrowImage(StoneArrow.INDEX, 0.8f),
                    ArrowSelectionScreen.this, Main.tr("stoneArrow"));
            iceArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + gap) * 4, posYButtons, ArrowHelper.getArrowImage(IceArrow.INDEX, 0.8f),
                    ArrowSelectionScreen.this, Main.tr("iceArrow"));
            lightningArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + gap) * 5, posYButtons, ArrowHelper.getArrowImage(LightningArrow.INDEX, 0.8f),
                    ArrowSelectionScreen.this, Main.tr("lightningArrow"));
            lightArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + gap) * 6 , posYButtons, ArrowHelper.getArrowImage(LightArrow.INDEX, 0.8f),
                    ArrowSelectionScreen.this, Main.tr("lightArrow"));
            shadowArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + gap) * 7, posYButtons, ArrowHelper.getArrowImage(ShadowArrow.INDEX, 0.8f),
                    ArrowSelectionScreen.this, Main.tr("shadowArrow"));

            buttonListArrows.add(fireArrowButton);
            buttonListArrows.add(waterArrowButton);
            buttonListArrows.add(stoneArrowButton);
            buttonListArrows.add(iceArrowButton);
            buttonListArrows.add(stormArrowButton);
            buttonListArrows.add(lightningArrowButton);
            buttonListArrows.add(lightArrowButton);
            buttonListArrows.add(shadowArrowButton);

			// resizing for higher resolutions, if necessary. The Resolution changes with mini-screens as well, but the Strings of the names can't be read probably.
            if (Main.getWindowWidth() != 1366) {
                for (Button button : buttonListArrows) {
                    button.setWidth(button.getWidth() * Main.getWindowWidth() / 1366);
                    button.setX(button.getX() * Main.getWindowWidth() / 1366);
                }
            }

            warningMessage = new WarningMessage("No warning yet", 40, Main.getWindowHeight() - 105, this);
            warningMessage.setFont(warningMessage.getFont().deriveFont(Component.STD_FONT.getSize() * 2f));

            MouseHandler mListener = new MouseHandler();

            for (int i = 0; i < ArrowHelper.NUMBER_OF_ARROW_TYPES; i++) {
                //buttonListArrows.get(i).setWidth(shadowArrowButton.getWidth() + 14);
                buttonListArrows.get(i).addMouseListener(mListener);
            }

            arrowList = new ArrayList<>();

            final int[] arrowsCount = ArrowHelper.emptyArrowCount();
            arrowList.add(Main.tr("fireArrow") + " [" + arrowsCount[FireArrow.INDEX] + "]");
            arrowList.add(Main.tr("waterArrow") + " [" + arrowsCount[WaterArrow.INDEX] + "]");
            arrowList.add(Main.tr("stormArrow") + " [" + arrowsCount[StormArrow.INDEX] + "]");
            arrowList.add(Main.tr("stoneArrow") + " [" + arrowsCount[StoneArrow.INDEX] + "]");
            arrowList.add(Main.tr("iceArrow") + " [" + arrowsCount[IceArrow.INDEX] + "]");
            arrowList.add(Main.tr("lightningArrow") + " [" + arrowsCount[LightningArrow.INDEX] + "]");
            arrowList.add(Main.tr("lightArrow") + " [" + arrowsCount[LightArrow.INDEX] + "]");
            arrowList.add(Main.tr("shadowArrow") + " [" + arrowsCount[ShadowArrow.INDEX] + "]");

            inventoryList_Width = fireArrowButton.getWidth() + 30;

            inventoryList = new comp.List(inventoryList_PosX, inventoryList_PosY, inventoryList_Width, inventoryList_Height, ArrowSelectionScreen.this, arrowList);
            inventoryList.setRoundBorder(true);
            inventoryList.setVisible(true);

	        inventoryList.onItemSelected.registerJava((Integer selectedIndex) -> {
		        String selectedArrowName = ArrowHelper.arrowIndexToName(selectedIndex);
		        selectedArrowBox.setEnteredText(selectedArrowName);
	        });

			inventoryList.acceptInput();

			String text = Main.tr("selectArrow");
			selectedArrowBox = new TextBox(Main.getWindowWidth() - (Component.getTextBounds(text, Component.STD_FONT).width + 30) - 37,
					300, text, ArrowSelectionScreen.this);
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
                        if (!(ArrowHelper.instanceArrow(selectedIndex).equip())) {
                            final InventoryLike inventory = Main.getContext().getActivePlayer().inventory();
                            if (inventory.maximumSize() - inventory.currentSize() <= 0) {
								warningMessage.setMessage(Main.tr("warningInventoryFull", inventory.maximumSize()));
							} else if (Main.getContext().getActivePlayer().arrowNumberFreeSetUsable().get() <= 0) {
								warningMessage.setMessage(Main.tr("warningMaxArrows", PfeileContext.arrowNumberFreeSet().get()));
                            } else {
								String msg = Main.tr("errorInventoryFull", inventory.maximumSize() - inventory.currentSize());
								warningMessage.setMessage(msg);
							}
                            warningMessage.activateMessage();
                        } else {
                            Main.getContext().getActivePlayer().arrowNumberFreeSetUsable().set(Main.getContext().getActivePlayer().arrowNumberFreeSetUsable().get() - 1);
							// when a new arrow is selected, it is usually also the arrow the user is going to use.
							selectedArrowBox.setEnteredText(ArrowHelper.instanceArrow(selectedIndex).getName());
                            updateInventoryList();
                        }
                    } else {
						warningMessage.setMessage(Main.tr("warningMaxArrows", PfeileContext.arrowNumberFreeSet()));
                        warningMessage.activateMessage();
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

            onScreenEnter.registerJava(() -> {
	            updateInventoryList();
	            warningMessage.setTransparency(0);
            });

            context.getTurnSystem().onTurnGet().registerJava ( team -> {
                        context.getActivePlayer().arrowNumberFreeSetUsable().set(PfeileContext.arrowNumberFreeSet().get());
            });

        });
        initThread.setDaemon(true);
        initThread.setPriority(7);
        initThread.start();
    }
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);

        GameScreen.getInstance().getMap().draw(g);
        AttackingScreen.getInstance().getAttackDrawer().draw(g);

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

        warningMessage.draw(g);
	}

	
	// LISTENER
	@Override
	public void keyDown (KeyEvent e) {
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
				//openConfirmQuestion ("Wollen Sie einen Feuerpfeil hinzufügen?");
				selectedIndex = FireArrow.class;
			}
			if (iceArrowButton.getBounds().contains(e.getPoint())) {
				//openConfirmQuestion ("Wollen Sie einen Eispfeil hinzufügen?");
				selectedIndex = IceArrow.class;
			}
			if (waterArrowButton.getBounds().contains(e.getPoint())) {
				//openConfirmQuestion ("Wollen Sie einen Wasserpfeil hinzufügen?");
				selectedIndex = WaterArrow.class;
			}
			if (stormArrowButton.getBounds().contains(e.getPoint())) {
				//openConfirmQuestion ("Wollen Sie einen Sturmpfeil hinzufügen?");
				selectedIndex = StormArrow.class;
			}
			if (stoneArrowButton.getBounds().contains(e.getPoint())) {
				//openConfirmQuestion ("Wollen Sie einen Steinpfeil hinzufügen?");
				selectedIndex = StoneArrow.class;
            }
			if (lightningArrowButton.getBounds().contains(e.getPoint())) {
				//openConfirmQuestion ("Wollen Sie einen Blitzpfeil hinzufügen?");
				selectedIndex = LightningArrow.class;
			}
			if (lightArrowButton.getBounds().contains(e.getPoint())) {
				//openConfirmQuestion ("Wollen Sie einen Lichtpfeil hinzufügen?");
				selectedIndex = LightArrow.class;
			}
			if (shadowArrowButton.getBounds().contains(e.getPoint())) {
				//openConfirmQuestion ("Wollen Sie einen Schattenpfeil hinzufügen?");
				selectedIndex = ShadowArrow.class;
			}
			if (cancelButton.getBounds().contains(e.getPoint())) {
				//onLeavingScreen(GameScreen.SCREEN_INDEX);
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
                        if (Main.isEnglish())
                            warningMessage.setMessage("No " + selectedArrowBox.getEnteredText() + " in inventory!");
                        else
                            warningMessage.setMessage("Kein " + selectedArrowBox.getEnteredText() + " im Inventar!");
						warningMessage.activateMessage();
					} else
                        return;
				} else {
                    if (Main.isEnglish())
					    warningMessage.setMessage("No arrow selected");
                    else
                        warningMessage.setMessage("Kein Pfeil ausgewählt");
					warningMessage.activateMessage();
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

				final int[] arrowsCount = ArrowHelper.arrowCountInventory(Main.getContext().getActivePlayer().inventory());
                arrowList.add(Main.tr("fireArrow") + " [" + arrowsCount[FireArrow.INDEX] + "]");
                arrowList.add(Main.tr("waterArrow") + " [" + arrowsCount[WaterArrow.INDEX] + "]");
                arrowList.add(Main.tr("stormArrow") + " [" + arrowsCount[StormArrow.INDEX] + "]");
                arrowList.add(Main.tr("stoneArrow") + " [" + arrowsCount[StoneArrow.INDEX] + "]");
                arrowList.add(Main.tr("iceArrow") + " [" + arrowsCount[IceArrow.INDEX] + "]");
                arrowList.add(Main.tr("lightningArrow") + " [" + arrowsCount[LightningArrow.INDEX] + "]");
                arrowList.add(Main.tr("lightArrow") + " [" + arrowsCount[LightArrow.INDEX] + "]");
                arrowList.add(Main.tr("shadowArrow") + " [" + arrowsCount[ShadowArrow.INDEX] + "]");

                inventoryList.setItems(arrowList);
            }
        };
        updateThreaded.setDaemon(true);
        updateThreaded.setPriority(7);
        updateThreaded.start();
	}
}
