package gui;

import general.Converter;
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
import javax.swing.JScrollPane;

import player.weapon.*;

/**
 * Wir w�ren bald soweit, diese Klasse hier aus unserem System herauszunehmen, da
 * du ja ArrowSelectionScreen entwickelt hast.
 */
@Deprecated
public class ArrowSelection extends JFrame {

	private static final long serialVersionUID = -6246665378267488656L;

	private static final String REMAINING_ARROW = new String("Übrige Pfeile: ");

	private JLabel remainingArrows;
	private JPanel listsPanel;
	// Hauptpanel
	private JPanel thisPanel;
	private JScrollPane scrollPane;
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
			String[] values = new String[] {FireArrow.NAME, WaterArrow.NAME, StormArrow.NAME, StoneArrow.NAME, IceArrow.NAME, LightningArrow.NAME, LightArrow.NAME, ShadowArrow.NAME};

			@Override
			public String getElementAt(int arg0) {
				return values[arg0];
			}

			@Override
			public int getSize() {
				return values.length;
			}
		});
		selectedArrows.add("<keine Pfeile>");

		arrowListSelected.setListData(Converter.convert(selectedArrows));

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
					arrowListSelected.setListData(Converter.convert(selectedArrows));
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
						arrowListSelected.setListData(Converter.convert(selectedArrows));
						remainingArrows.setText(REMAINING_ARROW
								+ Mechanics.arrowNumberPreSet);
					} else {
						arrowListSelected.setListData(Converter.convert(selectedArrows));
						remainingArrows.setText(REMAINING_ARROW
								+ (Mechanics.arrowNumberPreSet - selectedArrows
										.size()));
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent eEntered) {}

			@Override
			public void mouseExited(MouseEvent eExited) {}

			@Override
			public void mousePressed(MouseEvent ePressed) {}

			@Override
			public void mouseReleased(MouseEvent eReleased) {}
		});

		arrowList.setFixedCellHeight(30);
		arrowList.setFixedCellWidth(90);
		
		arrowListSelected.setFixedCellHeight(30);
		arrowListSelected.setFixedCellWidth(90);
		
		scrollPane = new JScrollPane(arrowListSelected, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		listsPanel.add(arrowList);
		listsPanel.add(scrollPane);
		thisPanel.add(listsPanel);
		thisPanel.add(remainingArrows);
	}
	
	/**
	 * �berpr�ft einen String und gibt daraufhin den korrespondierenden
	 * Pfeil zur�ck.
     *
     * TODO: USE <code> ArrowHelper.instanceArrow(String selectedArrowName)</code>
	 * 
	 * @param s
	 * @return
	 */
    @Deprecated
	public AbstractArrow checkString(String s) {
		// �berpr�ft den String
		if (s.equals(FireArrow.NAME)) {
			return new FireArrow();
		} else if (s.equals(IceArrow.NAME)) {
			return new IceArrow();
		} else if (s.equals(LightArrow.NAME)) {
			return new LightArrow();
		} else if (s.equals(LightningArrow.NAME)) {
			return new LightningArrow();
		} else if (s.equals(ShadowArrow.NAME)) {
			return new ShadowArrow();
		} else if (s.equals(StoneArrow.NAME)) {
			return new StoneArrow();
		} else if (s.equals(StormArrow.NAME)) {
			return new StormArrow();
		} else if (s.equals(WaterArrow.NAME)) {
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

			if(couldBeReady == true) {
				
				// nach Fehlern kontrolieren (d.h. zum Beispiel Pfeil wie <�brige Pfeile ausw�hlen>)
				for (int i = 0; i < selectedArrows.size(); i++) {
					if (checkString(selectedArrows.get(i)) == null) {
						selectedArrows.remove(i);
					}
				}
				
				dispose();
			}
		}
	}
}
