package gui;

import comp.Button;
import comp.Component;
import comp.ConfirmDialog;
import comp.TextBox;
import general.Main;
import general.Mechanics;
import player.*;
import player.weapon.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import world.ScaleWorld;

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
	List<Button> buttonListArrows = new ArrayList<Button>(); 
	
	/** Liste der Button f�r andere Aufgaben */
	List<Button> buttonList = new ArrayList<Button>();
	
	/** confirmOpenDialog */
	private boolean isConfirmDialogOpen = false;
	
	/** Y-Position des ersten Buttons (Bildschirm) */
	private int posYButtons = 85;
	/** X-Position des ersten Buttons (Screen) */
	private int posXButton = 38; 
	
	private Button fireArrowButton, waterArrowButton, iceArrowButton, stormArrowButton, lightningArrowButton, lightArrowButton, shadowArrowButton, stoneArrowButton;
	
	private static final Color TRANSPARENT_BACKGROUND = new Color(0, 0, 0, 185);
	
	private comp.List inventoryList;
	
	private List<String> arrowList;

	private int inventoryList_PosX = 60;

	private int inventoryList_PosY = 300;

	private int inventoryList_Height = 210;

	private int inventoryList_Width; 
	
	private float transparencyWarningMessage = 0;
	
	private Point pointWarningMessage = new Point(40, Main.getWindowHeight() - 105);
	
	private String warningMessage = "";
	
	private ConfirmDialog confirmDialog;
	
	public ArrowSelectionScreen() {
		super(ArrowSelectionScreen.SCREEN_NAME, ArrowSelectionScreen.SCREEN_INDEX);
	}
	
	public void init () {
		fireArrowButton = new Button(posXButton, posYButtons, this, "Feuerpfeil");
		waterArrowButton = new Button(posXButton + fireArrowButton.getWidth() + 43, posYButtons, this, "Wasserpfeil");
		stormArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 2, posYButtons, this, "Sturmpfeil");
		stoneArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 3, posYButtons, this, "Steinpfeil"); 
		iceArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 4, posYButtons, this, "Eispfeil");  
		lightningArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 5, posYButtons, this, "Blitzpfeil");
		lightArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 6 , posYButtons, this, "Lichtpfeil"); 
		shadowArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 7, posYButtons, this, "Schattenpfeil"); 
		
		buttonListArrows.add(fireArrowButton);
		buttonListArrows.add(waterArrowButton);
		buttonListArrows.add(stoneArrowButton);
		buttonListArrows.add(iceArrowButton);
		buttonListArrows.add(stormArrowButton); 
		buttonListArrows.add(lightningArrowButton);
		buttonListArrows.add(lightArrowButton);
		buttonListArrows.add(shadowArrowButton);
		
		fireArrowButton.iconify(ArrowHelper.getArrowImage(FireArrow.INDEX));
		waterArrowButton.iconify(ArrowHelper.getArrowImage(WaterArrow.INDEX));
		stoneArrowButton.iconify(ArrowHelper.getArrowImage(StoneArrow.INDEX));
		iceArrowButton.iconify(ArrowHelper.getArrowImage(IceArrow.INDEX));
		stormArrowButton.iconify(ArrowHelper.getArrowImage(StormArrow.INDEX));
		lightningArrowButton.iconify(ArrowHelper.getArrowImage(LightningArrow.INDEX));
		lightArrowButton.iconify(ArrowHelper.getArrowImage(LightArrow.INDEX));
		shadowArrowButton.iconify(ArrowHelper.getArrowImage(ShadowArrow.INDEX));
		
		MouseHandler mListner = new MouseHandler();
		
		for (int i = 0; i < buttonListArrows.size(); i++) {
			buttonListArrows.get(i).setWidth(shadowArrowButton.getWidth() + 12);
			buttonListArrows.get(i).addMouseListener (mListner); 
		}
		
		arrowList = new ArrayList <String> (); 
		final Inventory inventory = ((ScaleWorld) NewWorldTestScreen.getWorld()).getActivePlayer().getInventory();
		
		arrowList.add("Feuerpfeil " + "[" + inventory.getItemCount(FireArrow.class) + "]");
		arrowList.add("Wasserpfeil " + "[" + inventory.getItemCount(WaterArrow.class) + "]");
		arrowList.add("Sturmpfeil " + "[" + inventory.getItemCount(StormArrow.class)+ "]");
		arrowList.add("Steinpfeil " + "[" + inventory.getItemCount(StoneArrow.class) + "]");
		arrowList.add("Eispfeil " + "[" + inventory.getItemCount(IceArrow.class) + "]");
		arrowList.add("Blitzpfeil " + "[" + inventory.getItemCount(LightningArrow.class) + "]");
		arrowList.add("Lichtpfeil " + "[" + inventory.getItemCount(LightArrow.class) + "]");
		arrowList.add("Schattenpfeil " + "[" + inventory.getItemCount(ShadowArrow.class) + "]");
		
		inventoryList_Width = fireArrowButton.getWidth() + 30; 
		
		inventoryList = new comp.List(inventoryList_PosX, inventoryList_PosY, inventoryList_Width, inventoryList_Height, this, arrowList);
		inventoryList.setRoundBorder(true);
		inventoryList.setVisible(true); 
		inventoryList.addMouseListener(new MouseListHandler());
		inventoryList.acceptInput();	
		
		selectedArrowBox = new TextBox(Main.getWindowWidth() - (Component.getTextBounds("<Pfeil auswählen>", Component.STD_FONT).width + 30) - 37,
				300, "<Pfeil auswählen>", this);
		selectedArrowBox.setVisible(true);
		selectedArrowBox.setRoundBorder(true);
		selectedArrowBox.setHeight(selectedArrowBox.getHeight() + 1);
		selectedArrowBox.setEnteredText(selectedArrowBox.getStdText());
		selectedArrowBox.setWidth(Component.getTextBounds(selectedArrowBox.getEnteredText(), Component.STD_FONT).width + Component.STD_INSETS.left + Component.STD_INSETS.right + 3);
		selectedArrowBox.declineInput();
		
		confirmButton = new Button(selectedArrowBox.getX(), selectedArrowBox.getY() + selectedArrowBox.getHeight() + 30, this, "Confirm");
		confirmButton.setRoundBorder(true);
		confirmButton.setVisible(true);
		confirmButton.addMouseListener(new MouseHandler());
		
		cancelButton = new Button(confirmButton.getX(), confirmButton.getY() + selectedArrowBox.getHeight() + 30, this, "Abbrechen");
		cancelButton.setRoundBorder(true);
		cancelButton.setVisible(true);
		cancelButton.addMouseListener(new MouseHandler());
		
		buttonList.add(confirmButton); 
		buttonList.add(cancelButton);
		
		confirmDialog = new ConfirmDialog(stoneArrowButton.getX(), stoneArrowButton.getY() + 260, this, "");
		confirmDialog.setVisible(false);
		confirmDialog.getOk().addMouseListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 *
			 * @param e
			 */
			@Override
			public void mouseReleased(MouseEvent e) {
				if (!inventory.addItem(selectedIndex)) {
					if (inventory.getRemainingSpace() == 0) {
						warningMessage = "Das Inventar ist voll: Maximale Inventargröße " + inventory.getSize();
					} else if (Mechanics.arrowNumberFreeSetUseable <= 0){
						warningMessage = "Es wurden bereits die maximale Anzahl von freisetzbaren Pfeilen hinzugefügt. Sie beträgt: " + Mechanics.arrowNumberFreeSet + "";
					} else if (inventory.maxStack(selectedIndex) >= 
								inventory.getItemCount(selectedIndex)){
						
						warningMessage = "Das Inventar kann maximal " + inventory.maxStack(selectedIndex) + " " + 
											selectedIndex.getSimpleName() + " Pfeile aufnehmen";
						
					}else {
						System.err.println("Could not add arrow to inventory (with " + inventory.getRemainingSpace() + " remaining space) arrow index: " + selectedIndex);
						warningMessage = "Could not add arrow to inventory (with " + inventory.getRemainingSpace() + " remaining space) arrow index: " + selectedIndex;
					}
					transparencyWarningMessage = 1f;
				} else
					Mechanics.arrowNumberFreeSetUseable--;
				closeConfirmDialogQuestion();
			}
		});
		confirmDialog.getCancel().addMouseListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 *
			 * @param e
			 */
			@Override
			public void mouseReleased(MouseEvent e) {
				closeConfirmDialogQuestion();
			}
		});
	}
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		GameScreen.getInstance().getWorld().draw(g); 
		
		g.setColor(TRANSPARENT_BACKGROUND);
		g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
		
		// Zeichnen der Pfeilauswahl-Buttons
		for (Button buttonListArrow : buttonListArrows) {
			buttonListArrow.draw(g);
		}
		
		// Zeichen des Pfeilinventars
		inventoryList.draw(g);
		
		// Zeichnen der Auswahlbox
		selectedArrowBox.draw(g);
		
		if (isConfirmDialogOpen) {
			g.setColor(new Color(0, 0, 0, 0.13f));
			g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
		}
		
		// Zeichnen der Button zur Auswahl
		for (Button button : buttonList) {
			button.draw(g);
		} 
		
		if (confirmDialog.isVisible()) {
			g.setColor(new Color(0, 0, 0, 0.17f));
			g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
			confirmDialog.draw(g);
		}
		
		g.setColor(new Color(1f, 0f, 0f, transparencyWarningMessage));
		g.setFont(new Font(Component.STD_FONT.getFontName(), Font.BOLD, 26));
		g.drawString(warningMessage, pointWarningMessage.x, pointWarningMessage.y);
		
		transparencyWarningMessage = transparencyWarningMessage - 0.013f;
		
		if (transparencyWarningMessage < 0) 
			transparencyWarningMessage = 0;
	}

	
	// LISTENER
	@Override
	public void keyDown (KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			onLeavingScreen(this, PauseScreen.SCREEN_INDEX);
		} 
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			onLeavingScreen(this, GameScreen.SCREEN_INDEX);
		}
	}

	public Class<? extends AbstractArrow> getSelectedIndex() {
		return selectedIndex;
	}

	/** Listener f�r die Liste */
	private class MouseListHandler extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			
			// TODO: Man muss zweimal klicken, um hierher zu kommen. --> Auf einmal reduzieren
			if (inventoryList.getBounds().contains(e.getPoint())) {
				
				// �bernimmt ausgew�hlten Pfeil und schreibt ihn in den ausgew�hlten Pfeil f�r 'commit'
				selectedArrowBox.setEnteredText(ArrowHelper.arrowIndexToName(inventoryList.getSelectedIndex()));
//					System.err.println("Not possible ArrowIndex!: " + inventoryList.getSelectedIndex() + "   in <ArrowSelectionScreen.MouseListHandler.mouseReleased()>");
//					warningMessage = "Not possible ArrowIndex!: " + inventoryList.getSelectedIndex();
//					transparencyWarningMessage = 1.0f;
			}
		}
	}
	
	/** Listener f�r die Buttons */
	private class MouseHandler extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent e) {
			
			final Inventory inventory = ((ScaleWorld) NewWorldTestScreen.getWorld()).getActivePlayer().getInventory();
			
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
				selectedIndex = StoneArrow.class;			}
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
				onLeavingScreen(this, GameScreen.SCREEN_INDEX);
			} 
			if (confirmButton.getBounds().contains(e.getPoint())) {
				if (selectedArrowBox.getEnteredText().equals(selectedArrowBox.getStdText()) == false) {
					if (inventory.contains(
									ArrowHelper.reformArrow(selectedArrowBox.getEnteredText()))) {
										
						onLeavingScreen(this, AimSelectionScreen.SCREEN_INDEX);
					} else {
						warningMessage = "Kein " + selectedArrowBox.getEnteredText() + " im Inventar.";
						transparencyWarningMessage = 1f;
					}
					
				} else {
					warningMessage = "Kein Pfeil ausgewählt";
					transparencyWarningMessage = 1f;
				}				
			}
			
			if (confirmDialog.isVisible()) {
				if (confirmDialog.getCancel().getSimplifiedBounds().contains(e.getPoint())) 
					closeConfirmDialogQuestion();
				if (confirmDialog.getOk().getSimplifiedBounds().contains(e.getPoint())) 
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
	 * Is already used by Inventory. So there might be no need in using <code> updateInventoryList </code>
	 */
	public void updateInventoryList () {
		final Inventory inventory = ((ScaleWorld) NewWorldTestScreen.getWorld()).getActivePlayer().getInventory();
		
		arrowList.clear();
		
		arrowList.add("Feuerpfeil " + "[" + inventory.getItemCount(FireArrow.class) + "]");
		arrowList.add("Wasserpfeil " + "[" + inventory.getItemCount(WaterArrow.class) + "]");
		arrowList.add("Sturmpfeil " + "[" + inventory.getItemCount(StormArrow.class)+ "]");
		arrowList.add("Steinpfeil " + "[" + inventory.getItemCount(StoneArrow.class) + "]");
		arrowList.add("Eispfeil " + "[" + inventory.getItemCount(IceArrow.class) + "]");
		arrowList.add("Blitzpfeil " + "[" + inventory.getItemCount(LightningArrow.class) + "]");
		arrowList.add("Lichtpfeil " + "[" + inventory.getItemCount(LightArrow.class) + "]");
		arrowList.add("Schattenpfeil " + "[" + inventory.getItemCount(ShadowArrow.class) + "]");
		
		if (inventoryList.isAcceptingInput()) {
			inventoryList = new comp.List(inventoryList_PosX, inventoryList_PosY, inventoryList_Width, inventoryList_Height, this, arrowList);
			inventoryList.setRoundBorder(true);
			inventoryList.setVisible(true); 
			inventoryList.addMouseListener(new MouseListHandler());
			inventoryList.acceptInput();
		} else {
			inventoryList = new comp.List(inventoryList_PosX, inventoryList_PosY, inventoryList_Width, inventoryList_Height, this, arrowList);
			inventoryList.setRoundBorder(true);
			inventoryList.setVisible(true); 
			inventoryList.addMouseListener(new MouseListHandler());
			inventoryList.declineInput();
		}
	}
}
