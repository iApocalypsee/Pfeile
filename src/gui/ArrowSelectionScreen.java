package gui;

import general.Main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import comp.Button;

public class ArrowSelectionScreen extends Screen {

	public static final int SCREEN_INDEX = 3;
	
	public static final String SCREEN_NAME = "ArrowSelection";
	
	/** Liste der Button mit den möglichen Pfeilen */
	List<Button> buttonList = new ArrayList<Button>(); 
	
	/** Y-Position des ersten Buttons (Bildschirm) */
	private int posYButtons = 100;
	/** X-Position des ersten Buttons (Screen) */
	private int posXButton = 70; 
	
	Button fireArrowButton, waterArrowButton, iceArrowButton, stormArrowButton, lightningArrowButton, lightArrowButton, shadowArrowButton, stoneArrowButton;
	
	private static final Color TRANSPARENT_BACKGROUND = new Color(0, 0, 0, 185);
	
	public ArrowSelectionScreen() {
		
		super(ArrowSelectionScreen.SCREEN_NAME, ArrowSelectionScreen.SCREEN_INDEX);
		
		fireArrowButton = new Button(posXButton, posYButtons, this, "Feuerpfeil");
		waterArrowButton = new Button(posXButton + fireArrowButton.getWidth() + 43, posYButtons, this, "Wasserpfeil");
		iceArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 2, posYButtons, this, "Eispfeil"); 
		stormArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 3, posYButtons, this, "Sturmpfeil"); 
		lightningArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 4, posYButtons, this, "Blitzpfeil");
		lightArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 5 , posYButtons, this, "Lichtpfeil"); 
		shadowArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 6, posYButtons, this, "Schattenpfeil"); 
		stoneArrowButton = new Button(posXButton + (fireArrowButton.getWidth() + 43) * 7, posYButtons, this, "Steinpfeil"); 
		
		buttonList.add(fireArrowButton);
		buttonList.add(waterArrowButton);
		buttonList.add(iceArrowButton);
		buttonList.add(stormArrowButton); 
		buttonList.add(lightningArrowButton);
		buttonList.add(lightArrowButton);
		buttonList.add(shadowArrowButton);
		buttonList.add(stoneArrowButton);
		
		MouseHandler mListner = new MouseHandler();
		
		for (int i = 0; i < buttonList.size(); i++) {
			buttonList.get(i).setWidth(fireArrowButton.getWidth());
			buttonList.get(i).addMouseListener (mListner);
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		// Zeichnen des Hintergrunds: Welt + mit Schwarz und Transparent überzeichnen
		// TODO Habe ich auskommentiert, die Felder überzeichnen derzeitig die Buttons und auch
		// der transparente "Hintergrund" überzeichnet die Buttons. Muss Josip noch fixen.
		GameScreen.getInstance().getWorld().draw(g);
		
		g.setColor(TRANSPARENT_BACKGROUND);
		g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
		Main.timeObj.draw(g);
		
		
		// Zeichnen der Pfeilauswahl-Buttons
		for (int i = 0; i < buttonList.size(); i++) {
			buttonList.get(i).draw(g);
		}
	}

	@Override
	public void keyDown (KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			onLeavingScreen(this, PauseScreen.SCREEN_INDEX);
		} 
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			onLeavingScreen(this, GameScreen.SCREEN_INDEX);
		}
	}
	
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
			if (fireArrowButton.getBounds().contains(e.getPoint())) {
				// TODO CODE HIER EINFÜGEN!
			}
			if(iceArrowButton.getBounds().contains(e.getPoint())) {
				// TODO CODE HIER EINFÜGEN!
			}
			
			// TODO ALLE IF STATEMENTS FEHLEN! :D
		}
	}
}
