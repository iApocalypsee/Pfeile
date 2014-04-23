package gui;

import general.Main;
import general.Mechanics;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import player.AbstractArrow;
import player.FireArrow;
import player.IceArrow;
import player.LightArrow;
import player.LightningArrow;
import player.ShadowArrow;
import player.StoneArrow;
import player.StormArrow;
import player.WaterArrow;

public class ArrowSelection extends JFrame {

	private static final long serialVersionUID = -6246665378267488656L;

	private static final String REMAINING_ARROW = new String("Übrige Pfeile: ");

	private static boolean isReady = false;

	private JLabel remainingArrows;

	private JPanel listsPanel;

	// Hauptpanel
	private JPanel thisPanel;

	private JList<String> arrowList;
	private JList<String> arrowListSelected;
	public LinkedList<String> selectedArrows;

//	private JScrollPane scrollPaneSelected;

	private JButton readyButton;

	public ArrowSelection(int heigth, int width) {

		super("Pfeilauswahl");
		setSize(heigth, width);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(true);

		thisPanel = new JPanel();
		thisPanel.setLayout(new FlowLayout());
		add(this.thisPanel);

		arrowList = new JList<String>();
		arrowListSelected = new JList<String>();
		selectedArrows = new LinkedList<String>();
		listsPanel = new JPanel();
		listsPanel.setLayout(new GridLayout(1, 2, 5, 5));

		remainingArrows = new JLabel();
		remainingArrows.setText("Verf�gbare Pfeil(e) definieren!");

		readyButton = new JButton("Ready");
		readyButton.addActionListener(new ReadyButtonHandler());
		thisPanel.add(readyButton);

		arrowList.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = -1412378778727410007L;
			private boolean sorted = false;

			String[] values = new String[] { FireArrow.name, IceArrow.name,
					LightArrow.name, LightningArrow.name, ShadowArrow.name,
					StoneArrow.name, StormArrow.name, WaterArrow.name };

			@Override
			public String getElementAt(int arg0) {
				// wenn das Array noch nicht sortiert ist, sortier es
				if (sorted == false) {
					Arrays.sort(values);
					// verhindere, dass das Array noch einmal sortiert wird
					sorted = true;
				}
				return values[arg0];
			}

			@Override
			public int getSize() {
				return values.length;
			}
		});
		selectedArrows.add("<keine Pfeile>");

		arrowListSelected.setListData(convert(selectedArrows));
//		scrollPaneSelected = new JScrollPane();
		// arrowListSelected.add(scrollPaneSelected);
		// scrollPaneSelected = new JScrollPane (arrowListSelected);
		// listsPanel.add(scrollPaneSelected);

		// MouseListener f�r 'arrowList'
		arrowList.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent eClicked) {
				if (Mechanics.arrowNumberPreSet - selectedArrows.size() == 0) {
					return; // DO NOTHING. DO NOT ADD ARROWS ANYMORE.
				}
				if (Mechanics.arrowNumberPreSet != -1 && arrowList.isEnabled()) {
					if (selectedArrows.contains("<keine Pfeile>")) {
						selectedArrows.clear();
					}
					selectedArrows.add(arrowList.getSelectedValue());
					arrowListSelected.setListData(convert(selectedArrows));
					remainingArrows.setText(REMAINING_ARROW
							+ (Mechanics.arrowNumberPreSet - selectedArrows
									.size()));
				}

			}

			@Override
			public void mouseEntered(MouseEvent eEntered) {
			}

			@Override
			public void mouseExited(MouseEvent eExited) {
			}

			@Override
			public void mousePressed(MouseEvent ePressed) {
			}

			@Override
			public void mouseReleased(MouseEvent eReleased) {
			}

		});

		// Mouselistener f�r 'arrowListSelected'
		arrowListSelected.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent eClicked) {
				if (Mechanics.arrowNumberPreSet != -1
						&& arrowListSelected.isEnabled()) {
					selectedArrows.remove(arrowListSelected.getSelectedIndex());
					if (selectedArrows.isEmpty()) {
						selectedArrows.add("<keine Pfeile>");
						arrowListSelected.setListData(convert(selectedArrows));
						remainingArrows.setText(REMAINING_ARROW
								+ Mechanics.arrowNumberPreSet);
					} else {
						arrowListSelected.setListData(convert(selectedArrows));
						remainingArrows.setText(REMAINING_ARROW
								+ (Mechanics.arrowNumberPreSet - selectedArrows
										.size()));
					}

				}
			}

			@Override
			public void mouseEntered(MouseEvent eEntered) {/*
															 * TODO BEVELBORDER
															 * WHEN ENTERING
															 */
			}

			@Override
			public void mouseExited(MouseEvent eExited) {/*
														 * TODO EMPTYBORDER WHEN
														 * EXITING
														 */
			}

			@Override
			public void mousePressed(MouseEvent ePressed) {
			}

			@Override
			public void mouseReleased(MouseEvent eReleased) {
			}

		});

		arrowList.setFixedCellHeight(30);
		arrowList.setFixedCellWidth(90);
		arrowListSelected.setFixedCellHeight(30);
		arrowListSelected.setFixedCellWidth(90);

		if (Mechanics.arrowNumberPreSet == -1) {
			arrowList.setEnabled(false);
			arrowListSelected.setEnabled(false);
		}

		listsPanel.add(arrowList);
		listsPanel.add(arrowListSelected);
		thisPanel.add(listsPanel);
		thisPanel.add(remainingArrows);

		this.setVisible(true);
	}

	/**
	 * Convert-Methode: Wandelt die LinkedList 'selectedArrows2' in ein
	 * Sting-Array um
	 * 
	 * @param selectedArrows2
	 * @return String[] mit Inhalt aus der LinkedList 'selectedArrows2'
	 */
	public static String[] convert(LinkedList<String> selectedArrows2) {
		String[] values = new String[selectedArrows2.size()];

		for (int i = 0; i < selectedArrows2.size(); i++) {
			values[i] = selectedArrows2.get(i);
		}

		return values;
	}

	public static boolean getReady() {
		return isReady;
	}
	
	/**
	 * �berpr�ft einen String und gibt daraufhin den korrespondierenden
	 * Pfeil zur�ck.
	 * 
	 * @param s
	 * @return
	 */
	public AbstractArrow checkString(String s) {
		// �berpr�ft den String
		if (s.equals(FireArrow.name)) {
			return new FireArrow();
		} else if (s.equals(IceArrow.name)) {
			return new IceArrow();
		} else if (s.equals(LightArrow.name)) {
			return new LightArrow();
		} else if (s.equals(LightningArrow.name)) {
			return new LightningArrow();
		} else if (s.equals(ShadowArrow.name)) {
			return new ShadowArrow();
		} else if (s.equals(StoneArrow.name)) {
			return new StoneArrow();
		} else if (s.equals(StormArrow.name)) {
			return new StormArrow();
		} else if (s.equals(WaterArrow.name)) {
			return new WaterArrow();
		} else {
			return null;
		}
	}

	/**
	 * KONTROLLE, OB READYBUTTON GEKLICKED WURDE Kontrolle, ob alle Pfeile
	 * ausgew�hlt wurden
	 */
	protected class ReadyButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			boolean couldBeReady = true;
			String warningMessage = "";

			if (e.getSource() == readyButton) {

				if (Mechanics.arrowNumberPreSet == -1
						|| Mechanics.arrowNumberFreeSet == -1) {
					couldBeReady = false;

					warningMessage = "Unm�gliche Pfeilanzahl!";

					JOptionPane.showMessageDialog(ArrowSelection.this,
							warningMessage, "Warning", 1);

				}

				if (selectedArrows.size() > Mechanics.arrowNumberPreSet) {
					couldBeReady = false;

					warningMessage = "Fehler im System: zu viele Pfeile ausgew�hlt";

					JOptionPane.showMessageDialog(ArrowSelection.this,
							warningMessage, "Warning", 1);
				}

				if (selectedArrows.size() < Mechanics.arrowNumberPreSet) {
					warningMessage = "Fortfahren, obwohl nicht alle Pfeile ausgew�hlt wurden?";

					if (JOptionPane.showConfirmDialog(ArrowSelection.this,
							warningMessage, "Warning",
							JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
						couldBeReady = false;
					}
				}

			}

			isReady = couldBeReady;
			
			if(couldBeReady == true) {
				synchronized(Main.getMain()) {
					Main.getMain().notify();
				}
			}

		}

	}
}
