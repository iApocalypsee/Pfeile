package gui;

import general.Mechanics;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

/**
 * Klasse, um Einstellungsmenï¿½ aufzurufen
 */
@SuppressWarnings("serial")
public class PreWindow extends JFrame {
	
	protected JPanel thisPanel;
	public static final int FIRST_SETTING = 0;
	private static boolean isReady = false; 

	private JPanel containPanel, containPanelEnd; 
	private JPanel labelPanel;
	private JComboBox<String> initBox;
	private JComboBox<String> selectBox;
	private JComboBox<String> selectBoxGr;
	private JComboBox<String> selectBoxKI;
	private JComboBox<String> timeBox;
	private JComboBox<String> handicapSelectionPlayer, handicapSelectionKI; 
	private ConfirmButton confirmButton;
	private JButton readyButton, standardButton;
	private JSpinner initSpinner;
	private JLabel label0, label1, label2, label3, label4, label5, label6,
			label7, label8, label9, label10;
	private String warningMessage;

	/**
	 * Initialisiert PreWindow mit den Konstruktor-Werten; erstellt JPanel
	 * 'thisPanel' als Hauptpanel
	 */
	public PreWindow(int width, int height, int type, String heading) {
		super(heading);

		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		
		this.thisPanel = new JPanel();
		thisPanel.setLayout(new FlowLayout());

		add(this.thisPanel);



		// TYPE 0

		// wenn type = 0 wird das Einstellungsfenster geï¿½ffnet
		if (type == 0) {

			this.initSpinner = new JSpinner();
			Dimension prefSizeForSpinner = new Dimension(50, 20);
			this.initSpinner.setPreferredSize(prefSizeForSpinner);
			this.initSpinner.setVisible(false);
			
			this.confirmButton = new ConfirmButton("Bestätigen");
			this.confirmButton
					.addActionListener(new PreWindow.ListenToButton());
			this.confirmButton.type = 0;

			this.readyButton = new JButton("Ready");
			this.standardButton = new JButton("Standard-Einstellungen");

			this.label0 = new JLabel("Computerstärke: "); // "Computerstï¿½rke"
			this.label1 = new JLabel("Pfeilanzahl [frei wählbar]: "); // "Pfeilanzahl [frei wï¿½hlbar]"
			this.label2 = new JLabel("Pfeilanzahl [vorher wälbar]: "); // "Pfeilanzahl [vorher wï¿½hlbar]"
			this.label3 = new JLabel("Zuganzahl pro Runde: "); // "Zuganzahl pro Runde"
			this.label4 = new JLabel("maximales Leben: "); // "maximales Leben"
			this.label5 = new JLabel("Lebensregeneration: "); // "Lebensregeneration"
			this.label6 = new JLabel("Schaden: "); // "Schaden"
			this.label7 = new JLabel("Zeit pro Runde: "); // "Zeit pro Runde"
			this.label8 = new JLabel("Weltgröße: "); // "Weltgrï¿½ï¿½e"
			this.label9 = new JLabel("Handicap [Player]: "); // "Handicap [Player]"
			this.label10 = new JLabel("Handicap [KI]: "); // "Handicap [KI]"

			// Hauptauswahlfeld: initBox
			this.initBox = new JComboBox<String>();

			String[] stringArray = {"Computerstärke",
					"Pfeilanzahl [frei wählbar]",
					"Pfeilanzahl [vorher wählbar]", "Zuganzahl pro Runde",
					"maximales Leben", "Lebensregeneration", "Schaden",
					"Zeit pro Zug", "Weltgröße", "Handicap"};

			for (int i = 0; i < stringArray.length; i++) {
				this.initBox.addItem(stringArray[i]);
			}
			this.initBox.setSelectedIndex(0);
			this.initBox.addItemListener(new PreWindow.ListenToItem());

			// selectBox: Auswahlfeld
			this.selectBox = new JComboBox<String>();
			String[] stringArray2 = {"hoch", "hoch-mittel", "mittel",
					"mittel-niedrig", "niedrig"};

			for (int i = 0; i < stringArray2.length; i++) {
				this.selectBox.addItem(stringArray2[i]);
			}
			this.selectBox.setSelectedIndex(2);
			this.selectBox.addItemListener(new PreWindow.ListenToItem());
			this.selectBox.setVisible(false);

			// selectBoxGr: Auswahlfeld
			this.selectBoxGr = new JComboBox<String>();
			String[] stringArray5 = {"gigantisch", "groß", "normal", "klein",
					"winzig"};

			for (int i = 0; i < stringArray5.length; i++) {
				this.selectBoxGr.addItem(stringArray5[i]);
			}
			this.selectBoxGr.setSelectedIndex(2);
			this.selectBoxGr.addItemListener(new PreWindow.ListenToItem());
			this.selectBoxGr.setVisible(false);
			
			String[] stringHandicap = 
				{"+ 25%", "+ 20%", "+ 15%", "+ 10%", "+ 5%", "0%", "- 5%", "- 10%", "- 15%", "- 20 %", "- 25"};  
			handicapSelectionPlayer = new JComboBox<String>(stringHandicap);
			handicapSelectionPlayer.setSelectedIndex(5);
			handicapSelectionPlayer.addItemListener(new ListenToItem());
			handicapSelectionPlayer.setVisible(false);
			handicapSelectionKI = new JComboBox<String>(stringHandicap);
			handicapSelectionKI.setSelectedIndex(5);
			handicapSelectionKI.addItemListener(new ListenToItem());
			handicapSelectionKI.setVisible(false);
			
			// selectBoxKI zur Auswahl der Stï¿½rke des Computers
			this.selectBoxKI = new JComboBox<String>();

			String[] stringArray4 = {"brutal", "stark", "mittel", "schwach",
					"erbärmlich"};

			for (int i = 0; i < stringArray4.length; i++) {
				this.selectBoxKI.addItem(stringArray4[i]);
			}
			this.selectBoxKI.setSelectedIndex(2);
			this.selectBoxKI.addItemListener(new PreWindow.ListenToItem());
			this.selectBoxKI.setVisible(true);

			// JComboBox 'timeBox' fï¿½r Zeiteinstellung
			this.timeBox = new JComboBox<String>();
			String[] stringArray3 = {"5min", "2 min", "1 min", "40sec",
					"30sec", "20sec"};

			for (int i = 0; i < stringArray3.length; i++) {
				this.timeBox.addItem(stringArray3[i]);
			}
			this.timeBox.setSelectedIndex(2);
			this.timeBox.addItemListener(new PreWindow.ListenToItem());
			this.timeBox.setVisible(false);
			
			
			// JPANEL_ERSTELLUNG und ADDEN der Label

			// 'thisPanel' = Hauptpanel;
			// 'labelPanel' = Labels zur Textangabe;
			// 'containPanel' = Buttons fï¿½r Auswahlmï¿½glichkeiten;
			// 'containPanelEnd' fï¿½r Bestï¿½tigung der Auswahl
			this.containPanel = new JPanel();
			this.containPanel.setLayout(new FlowLayout());
			this.thisPanel.add(containPanel);

			this.containPanel.add(this.initBox);
			this.containPanel.add(this.selectBoxKI);
			this.containPanel.add(this.selectBox);
			this.containPanel.add(this.selectBoxGr);
			this.containPanel.add(this.timeBox);
			this.containPanel.add(this.initSpinner);
			this.containPanel.add(this.handicapSelectionPlayer);
			this.containPanel.add(this.handicapSelectionKI);
			this.containPanel.add(this.confirmButton);

			this.containPanelEnd = new JPanel();
			this.containPanelEnd.setLayout(new GridLayout(2, 1));
			this.thisPanel.add(containPanelEnd);

			this.containPanelEnd.add(this.standardButton);
			this.containPanelEnd.add(this.readyButton);

			this.labelPanel = new JPanel();
			this.labelPanel.setLayout(new GridLayout(10, 1));
			this.thisPanel.add(labelPanel);

			this.labelPanel.add(this.label0);
			this.labelPanel.add(this.label1);
			this.labelPanel.add(this.label2);
			this.labelPanel.add(this.label3);
			this.labelPanel.add(this.label4);
			this.labelPanel.add(this.label5);
			this.labelPanel.add(this.label6);
			this.labelPanel.add(this.label7);
			this.labelPanel.add(this.label8);
			this.labelPanel.add(this.label9); 
			this.labelPanel.add(this.label10);
			
			this.readyButton.addActionListener(new ListenToButton());
			this.standardButton.addActionListener(new ListenToButton());
		}

		setVisible(true);
	}

	/** fÃ¼r Main-Methode - Getter um zu Ã¼bermitteln, ob der Benutzer ferig ist */
	public static boolean getReady() {
		return isReady;
	}

	/**
	 * ACTION_LISTENER FÃœR BUTTON ist fÃ¼r alle Button und leider steht alles
	 * hier drin, ohne auf mehrere Methoden aufgeteilt zu sein
	 */
	class ListenToButton implements ActionListener {

		ListenToButton() {
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == PreWindow.this.confirmButton) {
				if (PreWindow.this.confirmButton.type == 0) {
					int numberEntered = -1;
					String stackTrace = "";
					boolean isEnteredRight = false;

					// 'TIMEBOX'
					if (PreWindow.this.initBox.getSelectedIndex() == 7) { // "Zeit pro Runde":
						// Wert wird in
						// 'Mechanics.timePerPlay'
						// in Millisekunden gespeichert
						if (PreWindow.this.timeBox.getSelectedIndex() == 0) {
							Mechanics.timePerPlay = 5 * (60000);
							isEnteredRight = true;
						} else if (PreWindow.this.timeBox.getSelectedIndex() == 1) {
							Mechanics.timePerPlay = 2 * (60000);
							isEnteredRight = true;
						} else if (PreWindow.this.timeBox.getSelectedIndex() == 2) {
							Mechanics.timePerPlay = 1 * (60000);
							isEnteredRight = true;
						} else if (PreWindow.this.timeBox.getSelectedIndex() == 3) {
							Mechanics.timePerPlay = Math.round((40f / 60f) * (60000));
							isEnteredRight = true;
						} else if (PreWindow.this.timeBox.getSelectedIndex() == 4) {
							Mechanics.timePerPlay = Math.round((30f / 60f) * (60000));
							isEnteredRight = true;
						} else if (PreWindow.this.timeBox.getSelectedIndex() == 5) {
							Mechanics.timePerPlay = Math.round((20f / 60f) * (60000));
							isEnteredRight = true;
						}
					}

					// 'SELECTBOXGR' fÃ¼r "WELTENGRÃ–ÃŸE
					else if (PreWindow.this.initBox.getSelectedIndex() == 8) { // "WeltengrÃ¶ÃŸe"

						if (PreWindow.this.selectBoxGr.getSelectedIndex() == 0) { // "gigantisch"
							Mechanics.worldSizeX = 23;
							Mechanics.worldSizeY = 21;
							isEnteredRight = true;
						} else if (PreWindow.this.selectBoxGr
								.getSelectedIndex() == 1) { // "groß"
							Mechanics.worldSizeX = 17;
							Mechanics.worldSizeY = 15;
							isEnteredRight = true;
						} else if (PreWindow.this.selectBoxGr
								.getSelectedIndex() == 2) { // "normal"
							Mechanics.worldSizeX = 13;
							Mechanics.worldSizeY = 11;
							isEnteredRight = true;
						} else if (PreWindow.this.selectBoxGr
								.getSelectedIndex() == 3) { // "klein"
							Mechanics.worldSizeX = 9;
							Mechanics.worldSizeY = 7;
							isEnteredRight = true;
						} else if (PreWindow.this.selectBoxGr
								.getSelectedIndex() == 4) { // "winzig"
							Mechanics.worldSizeX = 6;
							Mechanics.worldSizeY = 4;
							isEnteredRight = true;
						}
					} 
					
					// HANDICAP
					else if (PreWindow.this.initBox.getSelectedIndex() == 9) {
						
						// Handicap vom Spieler
						switch (handicapSelectionPlayer.getSelectedIndex()) {
						case 0:
							label9.setText("Handicap [Spieler]: " + "+ 25%");
							break;
						case 1: 
							label9.setText("Handicap [Spieler]: " + "+ 20%");
							break;
						case 2: 
							label9.setText("Handicap [Spieler]: " + "+ 15%");
							break;
						case 3: 
							label9.setText("Handicap [Spieler]: " + "+ 10%");
							break;
						case 4: 
							label9.setText("Handicap [Spieler]: " + "+ 5%");
							break;
							// case 5 ist default !!! 
						case 6: 
							label9.setText("Handicap [Spieler]: " + "- 5%");
							break;
						case 7: 
							label9.setText("Handicap [Spieler]: " + "- 10%");
							break;
						case 8: 
							label9.setText("Handicap [Spieler]: " + "- 15%");
							break;
						case 9:
							label9.setText("Handicap [Spieler]: " + "- 20%");
							break;
						case 10: 
							label9.setText("Handicap [Spieler]: " + "- 25%");
							break;
						default:
							label9.setText("Handicap [Spieler]: " + "0%");
							break;
						}
						
						// Handicap vom Computer
						switch (handicapSelectionKI.getSelectedIndex()) {
						case 0:
							label10.setText("Handicap [KI]: " + "+ 25%");
							break;
						case 1: 
							label10.setText("Handicap [KI]: " + "+ 20%");
							break;
						case 2: 
							label10.setText("Handicap [KI]: " + "+ 15%");
							break;
						case 3: 
							label10.setText("Handicap [KI]: " + "+ 10%");
							break;
						case 4: 
							label10.setText("Handicap [KI]: " + "+ 5%");
							break;
							// case 5 ist default !!! 
						case 6: 
							label10.setText("Handicap [KI]: " + "- 5%");
							break;
						case 7: 
							label10.setText("Handicap [KI]: " + "- 10%");
							break;
						case 8: 
							label10.setText("Handicap [KI]: " + "- 15%");
							break;
						case 9:
							label10.setText("Handicap [KI]: " + "- 20%");
							break;
						case 10: 
							label10.setText("Handicap [KI]: " + "- 25%");
							break;
						default:
							label10.setText("Handicap [KI]: " + "0%");
							break;
						}
						isEnteredRight = true; 
						
					} else if (PreWindow.this.initBox.getSelectedIndex() == 4
							|| PreWindow.this.initBox.getSelectedIndex() == 5
							|| PreWindow.this.initBox.getSelectedIndex() == 6) {

						// WERTE Mï¿½SSEN NOCH ZUGEWIESEN WERDEN!!!

						if (PreWindow.this.initBox.getSelectedIndex() == 4) { // "maximales Leben":
							// Speicherung in 'Mechanics.lifeMax' als
							// 5 = hoch
							// und
							// 1 = niedrig
							// ==> Werte noch zuweisen

							if (PreWindow.this.selectBox.getSelectedIndex() == 0) { // "hoch"
								Mechanics.lifeMax = 600;
								isEnteredRight = true;
							}
							if (PreWindow.this.selectBox.getSelectedIndex() == 1) { // "hoch-mittel"
								Mechanics.lifeMax = 480;
								isEnteredRight = true;
							}
							if (PreWindow.this.selectBox.getSelectedIndex() == 2) { // "mittel"
								Mechanics.lifeMax = 400;
								isEnteredRight = true;
							}
							if (PreWindow.this.selectBox.getSelectedIndex() == 3) { // "mittel-niedrig"
								Mechanics.lifeMax = 320;
								isEnteredRight = true;
							}
							if (PreWindow.this.selectBox.getSelectedIndex() == 4) { // "niedrig"
								Mechanics.lifeMax = 275;
								isEnteredRight = true;
							}
						}
						if (PreWindow.this.initBox.getSelectedIndex() == 5) { // "Lebensregeneration"
							// 5 = hoch;
							// 1=niedrig
							// BEREITS ZUGEWIESN!

							if (PreWindow.this.selectBox.getSelectedIndex() == 0) { // "hoch"
								Mechanics.lifeRegeneration = 5;
								isEnteredRight = true;
							}
							if (PreWindow.this.selectBox.getSelectedIndex() == 1) { // "hoch-mittel"
								Mechanics.lifeRegeneration = 4;
								isEnteredRight = true;
							}
							if (PreWindow.this.selectBox.getSelectedIndex() == 2) { // "mittel"
								Mechanics.lifeRegeneration = 3;
								isEnteredRight = true;
							}
							if (PreWindow.this.selectBox.getSelectedIndex() == 3) { // "mittel-niedrig"
								Mechanics.lifeRegeneration = 2;
								isEnteredRight = true;
							}
							if (PreWindow.this.selectBox.getSelectedIndex() == 4) { // "niedrig"
								Mechanics.lifeRegeneration = 1;
								isEnteredRight = true;
							}
						}
						if (PreWindow.this.initBox.getSelectedIndex() == 6) { // "Schaden"
							// -
							// 5=hoch;
							// 1=niedrig
							// TODO --> Werte nur vorzeitig

							if (PreWindow.this.selectBox.getSelectedIndex() == 0) { // "hoch"
								Mechanics.damageMulti = 1.8f;
								isEnteredRight = true;
							}
							if (PreWindow.this.selectBox.getSelectedIndex() == 1) { // "hoch-mittel"
								Mechanics.damageMulti = 1.3f;
								isEnteredRight = true;
							}
							if (PreWindow.this.selectBox.getSelectedIndex() == 2) { // "mittel"
								Mechanics.damageMulti = 1.0f;
								isEnteredRight = true;
							}
							if (PreWindow.this.selectBox.getSelectedIndex() == 3) { // "mittel-niedrig"
								Mechanics.damageMulti = 0.8f;
								isEnteredRight = true;
							}
							if (PreWindow.this.selectBox.getSelectedIndex() == 4) { // "niedrig"
								Mechanics.damageMulti = 0.6f;
								isEnteredRight = true;
							}
						}

					}

					// SPINNER-AUSWAHL
					else if (PreWindow.this.initBox.getSelectedIndex() == 1
							|| PreWindow.this.initBox.getSelectedIndex() == 2
							|| PreWindow.this.initBox.getSelectedIndex() == 3) {

						if (PreWindow.this.initBox.getSelectedIndex() == 1) { // "Pfeilanzahl [frei wÃ¤hlbar]"
							try {
								numberEntered = ((Integer) PreWindow.this.initSpinner
										.getValue()).intValue();
								if ((numberEntered >= 0)
										&& (numberEntered <= 25)) {
									isEnteredRight = true;
								} else
									isEnteredRight = false;
							} catch (ClassCastException exception) {
								stackTrace = exception.getMessage();
								isEnteredRight = false;
							}
						} else if (PreWindow.this.initBox.getSelectedIndex() == 2) { // "Pfeilanzahl [vorher wÃ¤hlbar]"
							try {
								numberEntered = ((Integer) PreWindow.this.initSpinner
										.getValue()).intValue();
								if ((numberEntered >= 0)
										&& (numberEntered <= 25)) {
									isEnteredRight = true;
								} else
									isEnteredRight = false;
							} catch (ClassCastException exception) {
								stackTrace = exception.getMessage();
								isEnteredRight = false;
							}
						} else if (PreWindow.this.initBox.getSelectedIndex() == 3) { // "ZÃ¼ge pro Runde"
							try {
								numberEntered = ((Integer) PreWindow.this.initSpinner
										.getValue()).intValue();
								if ((numberEntered > 0)
										&& (numberEntered <= 50)) {
									isEnteredRight = true;
								} else
									isEnteredRight = false;
							} catch (ClassCastException exception) {
								stackTrace = exception.getMessage();
								isEnteredRight = false;
							}
						}
					} else if (PreWindow.this.initBox.getSelectedIndex() == 0) { // "ComputerstÃ¤rke":
						// 5=Brutal
						// -->
						// 1=erbÃ¤rmlich

						isEnteredRight = true;

						if (PreWindow.this.selectBoxKI.getSelectedIndex() == 0) {
							Mechanics.KI = 5; // "Brutal"
						} else if (PreWindow.this.selectBoxKI
								.getSelectedIndex() == 1) {
							Mechanics.KI = 4; // "stark"
						} else if (PreWindow.this.selectBoxKI
								.getSelectedIndex() == 2) {
							Mechanics.KI = 3; // "mittel"
						} else if (PreWindow.this.selectBoxKI
								.getSelectedIndex() == 3) {
							Mechanics.KI = 2; // "schwach"
						} else if (PreWindow.this.selectBoxKI
								.getSelectedIndex() == 4) {
							Mechanics.KI = 1; // "erbï¿½rmlich"
						} else
							isEnteredRight = false;
					}

					// Ã¤ndert den Text in den Labels und setzt ggf. die
					// 'Mechanics....'-Werte
					if (isEnteredRight) {

						if (PreWindow.this.initBox.getSelectedIndex() == 0) {
							PreWindow.this.label0.setText(
									PreWindow.this.initBox.getSelectedItem() + ": " + 
											PreWindow.this.selectBoxKI.getSelectedItem());
						} else if (PreWindow.this.initBox.getSelectedIndex() == 1) {
							Mechanics.arrowNumberFreeSet = numberEntered;
							PreWindow.this.label1
									.setText(PreWindow.this.initBox
											.getSelectedItem()
											+ ": "
											+ Integer.toString(numberEntered));
						} else if (PreWindow.this.initBox.getSelectedIndex() == 2) {
							Mechanics.arrowNumberPreSet = numberEntered;
							PreWindow.this.label2
									.setText(PreWindow.this.initBox
											.getSelectedItem()
											+ ": "
											+ Integer.toString(numberEntered));
						} else if (PreWindow.this.initBox.getSelectedIndex() == 3) {
							Mechanics.turnsPerRound = numberEntered;
							PreWindow.this.label3
									.setText(PreWindow.this.initBox
											.getSelectedItem()
											+ ": "
											+ Integer.toString(numberEntered));
						} else if (PreWindow.this.initBox.getSelectedIndex() == 4) {
							PreWindow.this.label4
									.setText(PreWindow.this.initBox
											.getSelectedItem()
											+ ": "
											+ PreWindow.this.selectBox
											.getSelectedItem());
						} else if (PreWindow.this.initBox.getSelectedIndex() == 5) {
							PreWindow.this.label5
									.setText(PreWindow.this.initBox
											.getSelectedItem()
											+ ": "
											+ PreWindow.this.selectBox
											.getSelectedItem());
						} else if (PreWindow.this.initBox.getSelectedIndex() == 6) {
							PreWindow.this.label6
									.setText(PreWindow.this.initBox
											.getSelectedItem()
											+ ": "
											+ PreWindow.this.selectBox
											.getSelectedItem());
						} else if (PreWindow.this.initBox.getSelectedIndex() == 7) {
							PreWindow.this.label7
									.setText(PreWindow.this.initBox
											.getSelectedItem()
											+ ": "
											+ PreWindow.this.timeBox
											.getSelectedItem());
						} else if (PreWindow.this.initBox.getSelectedIndex() == 8) {
							PreWindow.this.label8
									.setText(PreWindow.this.initBox
											.getSelectedItem()
											+ ": "
											+ PreWindow.this.selectBoxGr
											.getSelectedItem());
						} else if (PreWindow.this.initBox.getSelectedIndex() == 9) {
							label9.setText(PreWindow.this.initBox
											.getSelectedItem()
											+ ": " + PreWindow.this.handicapSelectionPlayer
											.getSelectedItem()); 
							label10.setText(PreWindow.this.initBox
									.getSelectedItem()
									+ ": " + PreWindow.this.handicapSelectionKI
									.getSelectedItem()); 
						}
					} else {
						PreWindow.this.warningMessage = ("Enter a valid number!\n\n" + stackTrace);
						JOptionPane.showMessageDialog(PreWindow.this,
								PreWindow.this.warningMessage, "Warning", 2);
						PreWindow.this.warningMessage = "";
						stackTrace = "";
					}
				}
			}

			// ~~~~~~~~~~~~~
			// READY-BUTTON
			// ~~~~~~~~~~~~~

			else if (e.getSource() == PreWindow.this.readyButton) {

				// Test, ob jede Zahl korrekt eingegeben wurde und gibt falls
				// nï¿½tig eine Warnung aus
				if (Mechanics.KI == -1) {
					PreWindow.this.warningMessage = ("Select unselected Selections: Computerstärke");
					JOptionPane.showMessageDialog(PreWindow.this,
							PreWindow.this.warningMessage, "Warning", 1);
					return;
				}
				if (Mechanics.arrowNumberFreeSet == -1) {
					PreWindow.this.warningMessage = ("Select unselected Selections: Pfeilanzahl [Freeset]");
					JOptionPane.showMessageDialog(PreWindow.this,
							PreWindow.this.warningMessage, "Warning", 1);
					return;
				}
				if (Mechanics.arrowNumberPreSet == -1) {
					PreWindow.this.warningMessage = ("Select unselected Selections: Pfeilanzahl [Preset]");
					JOptionPane.showMessageDialog(PreWindow.this,
							PreWindow.this.warningMessage, "Warning", 1);
					return;
				}
				if (Mechanics.turnsPerRound == -1) {
					PreWindow.this.warningMessage = ("Select unselected Selections: Zeit pro Zug");
					JOptionPane.showMessageDialog(PreWindow.this,
							PreWindow.this.warningMessage, "Warning", 1);
					return;
				}
				if (Mechanics.lifeMax == -1) {
					PreWindow.this.warningMessage = ("Select unselected Selections: Leben");
					JOptionPane.showMessageDialog(PreWindow.this,
							PreWindow.this.warningMessage, "Warning", 1);
					return;
				}
				if (Mechanics.lifeRegeneration == -1) {
					PreWindow.this.warningMessage = ("Select unselected Selections: Lebensregeneration");
					JOptionPane.showMessageDialog(PreWindow.this,
							PreWindow.this.warningMessage, "Warning", 1);
					return;
				}
				if (Mechanics.damageMulti == -1f) {
					PreWindow.this.warningMessage = ("Select unselected Selections: Schadensmultiplikator");
					JOptionPane.showMessageDialog(PreWindow.this,
							PreWindow.this.warningMessage, "Warning", 1);
					return;
				}
				if (Mechanics.timePerPlay == -1) {
					PreWindow.this.warningMessage = ("Select unselected Selections: Zeit pro Zug");
					JOptionPane.showMessageDialog(PreWindow.this,
							PreWindow.this.warningMessage, "Warning", 1);
					return;
				}
				if (Mechanics.worldSizeX == -1 || Mechanics.worldSizeY == -1) {
					PreWindow.this.warningMessage = ("Select unselected Selections: Weltgröße");
					JOptionPane.showMessageDialog(PreWindow.this,
							PreWindow.this.warningMessage, "Warning", 1);
					return;
				}
				if (Mechanics.handicapPlayer == -1 || Mechanics.handicapKI == -1) {
						PreWindow.this.warningMessage = ("Select unselected Selections: Handicap");
						JOptionPane.showMessageDialog(PreWindow.this,
								PreWindow.this.warningMessage, "Warning", 1);
						return;
				}

				correctInits();
				setTotalArrowNumberCorrect();
				
				dispose();
			}
			
			// ~~~~~~~~~~~~~~~~~
			// STANDARD-BUTTON
			// ~~~~~~~~~~~~~~~~~

			// setzt die Werte auf hier festgelegte Standard-Werte und zeigt
			// diese im Label an
			else if (e.getSource() == PreWindow.this.standardButton) {
				Mechanics.KI = 2;
				PreWindow.this.label0.setText("Computerstärke: " + "mittel");
				
				Mechanics.arrowNumberFreeSet = 5;
				PreWindow.this.label1.setText("Pfeilanzahl [frei wählbar]: "
						+ Mechanics.arrowNumberFreeSet);
				
				Mechanics.arrowNumberPreSet = 10;
				
				PreWindow.this.label2.setText("Pfeilanzahl [vorher wählbar]: "
						+ Mechanics.arrowNumberPreSet);
				
				Mechanics.turnsPerRound = 5;
				PreWindow.this.label3.setText("Zuganzahl pro Runde: "
						+ Mechanics.turnsPerRound);
				
				Mechanics.lifeMax = 400;
				PreWindow.this.label4.setText("maximales Leben: " + "mittel");
				
				Mechanics.lifeRegeneration = 3;
				PreWindow.this.label5
						.setText("Lebensregeneration: " + "mittel");
				
				Mechanics.damageMulti = 3;
				PreWindow.this.label6.setText("Schaden: " + "mittel");
				
				Mechanics.timePerPlay = 1 * (60000);
				PreWindow.this.label7.setText("Zeit pro Zug: " + "1 min");
				
				Mechanics.worldSizeX = 13;
				Mechanics.worldSizeY = 11;
				PreWindow.this.label8.setText("Weltgröße: " + "normal");
				
				Mechanics.handicapPlayer = 0;
				PreWindow.this.label9.setText("Handicap [Player]: " + "0%");
				Mechanics.handicapKI = 0; 
				PreWindow.this.label10.setText("Handicap [KI]: " + "0%");
			}
		}
	}

	/**
	 * ITEM_LISTENER fï¿½r JComboBoxen: stellt die richtige Box, je nach Auswahl
	 * in der 'initBox' ein
	 */

	class ListenToItem implements ItemListener {

		ListenToItem() {
		}

		public void itemStateChanged(ItemEvent e) {
			if (e.getSource() == PreWindow.this.initBox) {

				// Fï¿½r Auswahl Computerstï¿½rke
				if (PreWindow.this.initBox.getSelectedIndex() == 0) {

					// setzt in diesen Moment unnï¿½tige JComboBoxen auf
					// setVisible(false)
					PreWindow.this.selectBox.setVisible(false);
					PreWindow.this.timeBox.setVisible(false);
					PreWindow.this.initSpinner.setVisible(false);
					PreWindow.this.selectBoxGr.setVisible(false);
					PreWindow.this.handicapSelectionPlayer.setVisible(false); 
					PreWindow.this.handicapSelectionKI.setVisible(false);
					PreWindow.this.selectBoxKI.setVisible(true);
				}

				// fï¿½r die Auswahlen mit Spinner: Pfeileanzahl und Anzahl der
				// Zï¿½ge pro Runde
				if (PreWindow.this.initBox.getSelectedIndex() > 0
						&& PreWindow.this.initBox.getSelectedIndex() <= 3) {

					PreWindow.this.selectBox.setVisible(false);
					PreWindow.this.timeBox.setVisible(false);
					PreWindow.this.selectBoxKI.setVisible(false);
					PreWindow.this.selectBoxGr.setVisible(false);
					PreWindow.this.handicapSelectionPlayer.setVisible(false); 
					PreWindow.this.handicapSelectionKI.setVisible(false);
					PreWindow.this.initSpinner.setVisible(true);

					// stellt Spinner-Model je nach auswahl in der 'initBox'
					// (Hauptbox) ein
					if (PreWindow.this.initBox.getSelectedIndex() == 1) {
						PreWindow.this.initSpinner
								.setModel(new SpinnerNumberModel(5, 0, 25, 1));
					} else if (PreWindow.this.initBox.getSelectedIndex() == 2) {
						PreWindow.this.initSpinner
								.setModel(new SpinnerNumberModel(10, 0, 25, 1));
					} else if (PreWindow.this.initBox.getSelectedIndex() == 3) {
						PreWindow.this.initSpinner
								.setModel(new SpinnerNumberModel(5, 1, 50, 1));
					} else {
						PreWindow.this.initSpinner
								.setModel(new SpinnerNumberModel(0, 0, 0, 0));
					}
				}

				// fï¿½r Auswahl mit 'selectBox' ==> Leben, Lebensregeneration und
				// Schaden
				if (PreWindow.this.initBox.getSelectedIndex() >= 4
						&& PreWindow.this.initBox.getSelectedIndex() <= 6) {
					PreWindow.this.initSpinner.setVisible(false);
					PreWindow.this.timeBox.setVisible(false);
					PreWindow.this.selectBoxKI.setVisible(false);
					PreWindow.this.selectBoxGr.setVisible(false);
					PreWindow.this.handicapSelectionPlayer.setVisible(false); 
					PreWindow.this.handicapSelectionKI.setVisible(false);
					PreWindow.this.selectBox.setVisible(true);
				}

				// fï¿½r Auswahl mit 'timeBox' ==> Zeit pro Zug
				if (PreWindow.this.initBox.getSelectedIndex() == 7) {
					PreWindow.this.initSpinner.setVisible(false);
					PreWindow.this.selectBox.setVisible(false);
					PreWindow.this.selectBoxKI.setVisible(false);
					PreWindow.this.selectBoxGr.setVisible(false);
					PreWindow.this.handicapSelectionPlayer.setVisible(false); 
					PreWindow.this.handicapSelectionKI.setVisible(false);
					PreWindow.this.timeBox.setVisible(true);
				}

				// fï¿½r Auswahl mit 'selectBoxGr' ==> Weltgrï¿½ï¿½e
				if (PreWindow.this.initBox.getSelectedIndex() == 8) {
					PreWindow.this.initSpinner.setVisible(false);
					PreWindow.this.selectBox.setVisible(false);
					PreWindow.this.selectBoxKI.setVisible(false);
					PreWindow.this.timeBox.setVisible(false);
					PreWindow.this.handicapSelectionPlayer.setVisible(false); 
					PreWindow.this.handicapSelectionKI.setVisible(false);
					PreWindow.this.selectBoxGr.setVisible(true);
				}
				
				// fï¿½r die Auswahl mit 'handicapBox' ==> Handicap
				if (PreWindow.this.initBox.getSelectedIndex() == 9) {
					PreWindow.this.initSpinner.setVisible(false);
					PreWindow.this.selectBox.setVisible(false);
					PreWindow.this.selectBoxKI.setVisible(false);
					PreWindow.this.timeBox.setVisible(false);
					PreWindow.this.selectBoxGr.setVisible(false);
					handicapSelectionPlayer.setVisible(true);
					handicapSelectionKI.setVisible(true);
				}
			}
		}
	}

	/**
	 * nach Inititialisierung fï¿½r 'Mechanics.lifeRegeneration' (erst nachher
	 * durchfï¿½rbar wegen Abhï¿½ngigkeit zu 'lifeMax')
	 */
	public static void correctInits() {
		if (Mechanics.lifeRegeneration != -1 && Mechanics.lifeMax != -1) {

			if (Mechanics.lifeRegeneration == 1) { // 5 = hoch; 1 == niedrig
				Mechanics.lifeRegeneration = (int) Math
						.round(0.5 * (Mechanics.lifeMax * 0.008 + 2));
				return;

			} else if (Mechanics.lifeRegeneration == 2) {
				Mechanics.lifeRegeneration = (int) Math
						.round(0.5 * (Mechanics.lifeMax * 0.001 + 3));
				return;

			} else if (Mechanics.lifeRegeneration == 3) {
				Mechanics.lifeRegeneration = (int) Math
						.round(0.5 * (Mechanics.lifeMax * 0.015 + 3.5));
				return;

			} else if (Mechanics.lifeRegeneration == 4) {
				Mechanics.lifeRegeneration = (int) Math
						.round(0.5 * (Mechanics.lifeMax * 0.02 + 4.5));
				return;

			} else if (Mechanics.lifeRegeneration == 5) {
				Mechanics.lifeRegeneration = (int) Math
						.round(0.5 * (Mechanics.lifeMax * 0.025 + 7));
				return;
			}
		}

		Mechanics.lifeMax = 400;
		Mechanics.lifeRegeneration = (int) Math
				.round(0.5 * (Mechanics.lifeMax * 0.015 + 3.5));

		setTotalArrowNumberCorrect();
	}

	/** Aufruf durch 'setLifeRegenerationCorrect' --> setzt die Gesamtpfeilzahl */
	private static void setTotalArrowNumberCorrect() {
		Mechanics.totalArrowNumber = Mechanics.arrowNumberFreeSet
				+ Mechanics.arrowNumberPreSet;
		Mechanics.arrowNumberFreeSetUseable = Mechanics.arrowNumberFreeSet;
	}
}
