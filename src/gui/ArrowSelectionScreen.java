package gui;

import general.Main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import player.FireArrow;
import player.IceArrow;
import player.InventoryArrows;
import player.LightArrow;
import player.LightningArrow;
import player.ShadowArrow;
import player.StoneArrow;
import player.StormArrow;
import player.WaterArrow;
import comp.Button;
import comp.Component;
import comp.ConfirmDialog;
import comp.TextBox;

public class ArrowSelectionScreen extends Screen {

	public static final int SCREEN_INDEX = 3;
	
	private static ArrowSelectionScreen instance; 
	
	/**
	 * Singleton-Methode.
	 * @return The object.
	 */
	public static ArrowSelectionScreen getInstance() {
		if(instance == null) {
			instance = new ArrowSelectionScreen();
		}
		return instance;
	}
	
	public static final String SCREEN_NAME = "ArrowSelection";
	
	/** Die TextBox, die Anzeigt, welcher Pfeil ausgewählt worden ist */
	private TextBox selectedArrowBox; 
	
	/** Ausgewählter Pfeil zum Hinzufügen */
	private int selectedIndex; 
	
	/** Button zur Benutzung des Ausgewählten Pfeils */
	private Button confirmButton; 
	
	/** Button, um Schussvorgang abzubrechen */
	private Button cancelButton; 
	
	/** Liste der Button mit den möglichen Pfeilen */
	List<Button> buttonListArrows = new ArrayList<Button>(); 
	
	/** Liste der Button für andere Aufgaben */
	List<Button> buttonList = new ArrayList<Button>();
	
	/** confirmOpenDialog */
	private boolean isConfirmDialogOpen; 
	
	/** Y-Position des ersten Buttons (Bildschirm) */
	private int posYButtons = 85;
	/** X-Position des ersten Buttons (Screen) */
	private int posXButton = 38; 
	
	Button fireArrowButton, waterArrowButton, iceArrowButton, stormArrowButton, lightningArrowButton, lightArrowButton, shadowArrowButton, stoneArrowButton;
	
	private static final Color TRANSPARENT_BACKGROUND = new Color(0, 0, 0, 185);
	
	private comp.List inventoryList; 
	
	private List<String> arrowList;

	private int inventoryList_PosX = 60;

	private int inventoryList_PosY = 300;

	private int inventoryList_Height = 210;

	private int inventoryList_Width; 
	
	private comp.ConfirmDialog confirmDialog;
	
	public ArrowSelectionScreen() {
		super(ArrowSelectionScreen.SCREEN_NAME, ArrowSelectionScreen.SCREEN_INDEX);
		isConfirmDialogOpen = false; 
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
		
		fireArrowButton.iconify(FireArrow.getImage());
		waterArrowButton.iconify(WaterArrow.getImage());
		stoneArrowButton.iconify(StoneArrow.getImage());
		iceArrowButton.iconify(IceArrow.getImage());
		stormArrowButton.iconify(StormArrow.getImage());
		lightningArrowButton.iconify(LightningArrow.getImage());
		lightArrowButton.iconify(LightArrow.getImage());
		shadowArrowButton.iconify(ShadowArrow.getImage());
		
		MouseHandler mListner = new MouseHandler();
		
		for (int i = 0; i < buttonListArrows.size(); i++) {
			buttonListArrows.get(i).setWidth(shadowArrowButton.getWidth() + 12);
			buttonListArrows.get(i).addMouseListener (mListner); 
		}
		
		arrowList = new ArrayList <String> (); 
		InventoryArrows inventory = GameScreen.getInstance().getWorld().getActivePlayer().getInventory();
		
		arrowList.add("Feuerpfeil " + "[" + inventory.getArrowCount(FireArrow.class) + "]");
		arrowList.add("Wasserpfeil " + "[" + inventory.getArrowCount(WaterArrow.class) + "]");
		arrowList.add("Sturmpfeil " + "[" + inventory.getArrowCount(StormArrow.class)+ "]"); 
		arrowList.add("Steinpfeil " + "[" + inventory.getArrowCount(StoneArrow.class) + "]"); 
		arrowList.add("Eispfeil " + "[" + inventory.getArrowCount(IceArrow.class) + "]"); 
		arrowList.add("Blitzpfeil " + "[" + inventory.getArrowCount(LightningArrow.class) + "]");
		arrowList.add("Lichtpfeil " + "[" + inventory.getArrowCount(LightArrow.class) + "]"); 
		arrowList.add("Schattenpfeil " + "[" + inventory.getArrowCount(ShadowArrow.class) + "]"); 
		
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
		confirmDialog.addMouseListener(new MouseHandler());
	}
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		GameScreen.getInstance().getWorld().draw(g); 
		Main.timeObj.draw(g);
		
		g.setColor(TRANSPARENT_BACKGROUND);
		g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
		
		// Zeichnen der Pfeilauswahl-Buttons
		for (int i = 0; i < buttonListArrows.size(); i++) {
			buttonListArrows.get(i).draw(g);
		}
		
		// Zeichen des Pfeilinventars
		inventoryList.draw(g);
		
		// Zeichnen der Auswahlbox
		selectedArrowBox.draw(g);
		
		if (isConfirmDialogOpen == true) {
			g.setColor(new Color(0, 0, 0, 0.13f));
			g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
		}
		
		// Zeichnen der Button zur Auswahl
		for (Button button : buttonList) {
			button.draw(g);
		} 
		
		if (isConfirmDialogOpen == true) {
			g.setColor(new Color(0, 0, 0, 0.17f));
			g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
		}
		
		confirmDialog.draw(g);
	}

	@Override
	public void keyDown (KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			onLeavingScreen(this, PauseScreen.SCREEN_INDEX);
		} 
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			onLeavingScreen(this, GameScreen.SCREEN_INDEX);
		}
	}
	
	/** Listener für die Liste */
	private class MouseListHandler implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent arg0) {}

		@Override
		public void mouseEntered(MouseEvent arg0) {}

		@Override
		public void mouseExited(MouseEvent arg0) {}

		@Override
		public void mousePressed(MouseEvent arg0) {}

		@Override
		public void mouseReleased(MouseEvent e) {
			
			// TODO: Man muss zweimal klicken, um hierher zu kommen. --> Auf einmal reduzieren
			if (inventoryList.getBounds().contains(e.getPoint())) {
				
				switch (inventoryList.getSelectedIndex()) {
				
				case FireArrow.INDEX: 
					selectedArrowBox.setEnteredText("Feurpfeil");
					break;
				case WaterArrow.INDEX: 
					selectedArrowBox.setEnteredText("Wasserpfeil");
					break;
				case StormArrow.INDEX: 
					selectedArrowBox.setEnteredText("Sturmpfeil");
					break;
				case StoneArrow.INDEX: 
					selectedArrowBox.setEnteredText("Steinpfeil");
					break;
				case IceArrow.INDEX: 
					selectedArrowBox.setEnteredText("Eispfeil");
					break;
				case LightningArrow.INDEX: 
					selectedArrowBox.setEnteredText("Blitzpfeil");
					break;
				case LightArrow.INDEX: 
					selectedArrowBox.setEnteredText("Lichtpfeil");
					break;
				case ShadowArrow.INDEX: 
					selectedArrowBox.setEnteredText("Schattenpfeil");
					break;
				default: 
					System.err.println("Not possible ArrowIndex!: " + inventoryList.getSelectedIndex() + "   in <ArrowSelectionScreen.MouseListHandler.mouseReleased()>");
					break;
				}
			}
		}
	}
	
	/** Listener für die Buttons */
	private class MouseHandler implements MouseListener {

		/*
		 * (non-Javadoc)
		 * @deprecated Anstatt mouseClicked() mouseReleased() benutzen!
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		@Deprecated
		public void mouseClicked(MouseEvent arg0) {}
		
		@Override
		public void mouseEntered(MouseEvent arg0) {}
		@Override
		public void mouseExited(MouseEvent arg0) {}
		@Override
		public void mousePressed(MouseEvent arg0) {}
		@Override
		public void mouseReleased(MouseEvent e) {
			
			InventoryArrows inventory = GameScreen.getInstance().getWorld().getActivePlayer().getInventory(); 
			
			if (fireArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Feuerpfeil hinzufügen?"); 
				selectedIndex = FireArrow.INDEX; 
			}
			if (iceArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Eispfeil hinzufügen?"); 
				selectedIndex = IceArrow.INDEX; 
			}
			if (waterArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Wasserpfeil hinzufügen?"); 
				selectedIndex = WaterArrow.INDEX;
			}
			if (stormArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Sturmpfeil hinzufügen?"); 
				selectedIndex = StormArrow.INDEX;
			}
			if (stoneArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Steinpfeil hinzufügen?"); 
				selectedIndex = StoneArrow.INDEX;			}
			if (lightningArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Blitzpfeil hinzufügen?"); 
				selectedIndex = LightningArrow.INDEX;
			}
			if (lightArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Lichtpfeil hinzufügen?"); 
				selectedIndex = LightArrow.INDEX;
			}
			if (shadowArrowButton.getBounds().contains(e.getPoint())) {
				openConfirmQuestion ("Wollen Sie einen Schattenpfeil hinzufügen?"); 
				selectedIndex = ShadowArrow.INDEX; 
			}
			if (cancelButton.getBounds().contains(e.getPoint())) {
				onLeavingScreen(this, GameScreen.SCREEN_INDEX);
			} 
			if (confirmButton.getBounds().contains(e.getPoint())) {
//				onLeavingScreen(this, AimSelectionScreen.SCREEN_INDEX);
				// TODO Bestätigen
				// TODO dann irgentwie die Auswahl für das Zielfeld treffen: AimSelectionScreen
			}
			
			if (isConfirmDialogOpen){
				if (confirmDialog.getBoundingBoxOkButton().contains(e.getPoint())) {
					
					switch (selectedIndex) {
					case FireArrow.INDEX:
						if (inventory.addArrow(new FireArrow()) == false) {
							System.err.println("Der Feuerpfeil konnte nicht hinzugefügt werden.");
						}
						break;
					case IceArrow.INDEX: 
						if (inventory.addArrow(new IceArrow()) == false) {
							System.err.println("Der Eispfeil konnte nicht hinzugefügt werden.");
						}
						break;
					case WaterArrow.INDEX: 
						if (inventory.addArrow(new WaterArrow()) == false) {
							System.err.println("Der Wasserpfeil konnte nicht hinzugefügt werden.");
						}
						break;
					case StormArrow.INDEX: 
						if (inventory.addArrow(new StormArrow()) == false) {
							System.err.println("Der Sturmpfeil konnte nicht hinzugefügt werden.");
						}
						break;
					case StoneArrow.INDEX: 
						if (inventory.addArrow(new StoneArrow()) == false) {
							System.err.println("Der Steinpfeil konnte nicht hinzugefügt werden.");
						}
						break;
					case LightningArrow.INDEX: 
						if (inventory.addArrow(new LightningArrow()) == false) {
							System.err.println("Der Blitzpfeil konnte nicht hinzugefügt werden.");
						}
						break;
					case LightArrow.INDEX: 
						if (inventory.addArrow(new LightArrow()) == false) {
							System.err.println("Der Lichtpfeil konnte nicht hinzugefügt werden.");
						}
						break;
					case ShadowArrow.INDEX: 
						if (inventory.addArrow(new ShadowArrow()) == false) {
							System.err.println("Der Schattenpfeil konnte nicht hinzugefügt werden.");
						}
						break;
					default:
						System.err.println("Not Defined Arrow Index!: " + selectedIndex + "   in <ArrowSelectionScreen.MouseHandler.mouseReleased()>");
						break;
					}					
					closeConfirmDialogQuestion();
				}
				if (confirmDialog.getBoundingBoxCancelButton().contains(e.getPoint())) {
					closeConfirmDialogQuestion();
				}
			}
			
			updateInventoryList();
		}
	}
	
	/** Bestätigen der Eingabe */
	private void openConfirmQuestion (String question) {
		confirmDialog.setQuestionText(question);
		confirmDialog.setVisible(true);
		isConfirmDialogOpen = true; 
		
		for (Button button : buttonList) {
			button.declineInput();
		}
		inventoryList.declineInput();
	}
	
	/** Schießt den Bestätigungsaufruf */
	private void closeConfirmDialogQuestion () {
		confirmDialog.setQuestionText("");
		confirmDialog.setVisible(false);
		isConfirmDialogOpen = false; 
		
		for (Button button : buttonList) {
			button.acceptInput();
		}
		inventoryList.acceptInput();
	}
	
	/** bei Aenderung der Anzahl der Pfeile muss diese Methode aufgerufen werden, um es beim Pfeilinventar zu aendern */
	public void updateInventoryList () {
		InventoryArrows inventory = GameScreen.getInstance().getWorld().getActivePlayer().getInventory();
		
		arrowList.clear();
		
		arrowList.add("Feuerpfeil " + "[" + inventory.getArrowCount(FireArrow.class) + "]");
		arrowList.add("Wasserpfeil " + "[" + inventory.getArrowCount(WaterArrow.class) + "]");
		arrowList.add("Sturmpfeil " + "[" + inventory.getArrowCount(StormArrow.class)+ "]"); 
		arrowList.add("Steinpfeil " + "[" + inventory.getArrowCount(StoneArrow.class) + "]"); 
		arrowList.add("Eispfeil " + "[" + inventory.getArrowCount(IceArrow.class) + "]"); 
		arrowList.add("Blitzpfeil " + "[" + inventory.getArrowCount(LightningArrow.class) + "]");
		arrowList.add("Lichtpfeil " + "[" + inventory.getArrowCount(LightArrow.class) + "]"); 
		arrowList.add("Schattenpfeil " + "[" + inventory.getArrowCount(ShadowArrow.class) + "]"); 
		
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
