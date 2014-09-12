package gui;

import comp.Button;
import comp.Label;
import comp.List;
import general.Main;
import general.Mechanics;
import player.weapon.*;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

/**
 * Created by Daniel on 09.09.2014.
 */
public class ArrowSelectionScreenPreSet extends Screen {

    public static final int SCREEN_INDEX = 256;
    public static final String SCREEN_NAME = "ArrowSelectionScreenPreSet";

    private Label remainingArrows;
    private Button readyButton;

    /** Liste der Button f�r andere Aufgaben */
    Button [] buttonListArrows = new Button[8];
    private List arrowListSelected;
    public LinkedList<String> selectedArrows;

    /** Hintergrund Farbe */
    private static final Color TRANSPARENT_BACKGROUND = new Color(0, 0, 0, 201);


    /**
     * Screen für die Pfeilauswahl für vorhersetzbaren Pfeilen.
     * äquivalent zu <code> ArrowSelection </code>.
     */
    public ArrowSelectionScreenPreSet() {
        super(SCREEN_NAME, SCREEN_INDEX);

        arrowListSelected = new List();
        selectedArrows = new LinkedList<String>();

        // TODO: bei 0 arrows pre set nicht in diesen Screen wechseln
        if (Mechanics.arrowNumberPreSet > 1) {
            remainingArrows = new Label(100, 200, this, "Verfügbare Pfeile definieren!");
        } else {
            remainingArrows = new Label (100, 200, this, "Verfügbaren Pfeil definieren!");
        }

        /** Y-Position des ersten Buttons (Bildschirm) */
        int posYButtons = 85;
        /** X-Position des ersten Buttons (Screen) */
        int posXButton = 38;

        buttonListArrows [0] = new Button(posXButton, posYButtons, this, "Feuerpfeil");
        buttonListArrows [1] = new Button(posXButton + buttonListArrows [0].getWidth() + 43, posYButtons, this, "Wasserpfeil");
        buttonListArrows [2] = new Button(posXButton + (buttonListArrows [0].getWidth() + 43) * 2, posYButtons, this, "Sturmpfeil");
        buttonListArrows [3] = new Button(posXButton + (buttonListArrows [0].getWidth() + 43) * 3, posYButtons, this, "Steinpfeil");
        buttonListArrows [4] = new Button(posXButton + (buttonListArrows [0].getWidth() + 43) * 4, posYButtons, this, "Eispfeil");
        buttonListArrows [5] = new Button(posXButton + (buttonListArrows [0].getWidth() + 43) * 5, posYButtons, this, "Blitzpfeil");
        buttonListArrows [6] = new Button(posXButton + (buttonListArrows [0].getWidth() + 43) * 6 , posYButtons, this, "Lichtpfeil");
        buttonListArrows [7] = new Button(posXButton + (buttonListArrows [0].getWidth() + 43) * 7, posYButtons, this, "Schattenpfeil");


        buttonListArrows [0].iconify(ArrowHelper.getArrowImage(FireArrow.INDEX));
        buttonListArrows [1].iconify(ArrowHelper.getArrowImage(WaterArrow.INDEX));
        buttonListArrows [2].iconify(ArrowHelper.getArrowImage(StoneArrow.INDEX));
        buttonListArrows [3].iconify(ArrowHelper.getArrowImage(IceArrow.INDEX));
        buttonListArrows [4].iconify(ArrowHelper.getArrowImage(StormArrow.INDEX));
        buttonListArrows [5].iconify(ArrowHelper.getArrowImage(LightningArrow.INDEX));
        buttonListArrows [6].iconify(ArrowHelper.getArrowImage(LightArrow.INDEX));
        buttonListArrows [7].iconify(ArrowHelper.getArrowImage(ShadowArrow.INDEX));

        readyButton = new Button(800, 600, this, "Bestätigen");
        readyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                boolean couldBeReady = true;
                String warningMessage = "";

                if (e.getSource() == readyButton) {

                    if (Mechanics.arrowNumberPreSet == -1
                            || Mechanics.arrowNumberFreeSet == -1) {
                        couldBeReady = false;

                        warningMessage = "Unmögliche Pfeilanzahl!";

                        // TODO : comp.Dialog verwenden

                        //JOptionPane.showMessageDialog(ArrowSelectionScreenPreSet.this,
                        //        warningMessage, "Warning", 1);

                    }

                    if (selectedArrows.size() > Mechanics.arrowNumberPreSet) {
                        couldBeReady = false;

                        warningMessage = "Fehler im System: zu viele Pfeile ausgew�hlt";

                        // TODO : comp.Dialog verwenden
                        // JOptionPane.showMessageDialog(ArrowSelection.this,
                        //         warningMessage, "Warning", 1);
                    }

                    if (selectedArrows.size() < Mechanics.arrowNumberPreSet) {
                        warningMessage = "Fortfahren, obwohl nicht alle Pfeile ausgewählt wurden?";
                        // TODO : comp.Dialog verwenden
                        //if (JOptionPane.showConfirmDialog(ArrowSelection.this,
                        //      warningMessage, "Warning",
                        //      JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                        //     couldBeRea dy = false;
                        //}
                    }
                }

                if (couldBeReady == true) {

                    // nach Fehlern kontrolieren (d.h. zum Beispiel Pfeil wie <�brige Pfeile ausw�hlen>)
                    for (int i = 0; i < selectedArrows.size(); i++) {
                        if (ArrowHelper.instanceArrow(selectedArrows.get(i)) == null) {
                            selectedArrows.remove(i);
                        }
                    }
                    onLeavingScreen(this, GameScreen.SCREEN_INDEX);
                }
            }
        });

        selectedArrows.add("<keine Pfeile>");
        setArrowListSelected(selectedArrows);

//        // MouseListener f�r 'arrowList'
//        arrowList.addMouseListener(new MouseListener() {
//
//            @Override
//            public void mouseClicked(MouseEvent eClicked) {
//                if (Mechanics.arrowNumberPreSet - selectedArrows.size() == 0) {
//                    return; // DO NOTHING. DO NOT ADD ARROWS ANYMORE.
//                }
//                if (Mechanics.arrowNumberPreSet != -1 && arrowList.isEnabled()) {
//                    if (selectedArrows.contains("<keine Pfeile>")) {
//                        selectedArrows.clear();
//                    }
//                    selectedArrows.add(arrowList.getSelectedValue());
//                    arrowListSelected.setListData(convert(selectedArrows));
//                    remainingArrows.setText(REMAINING_ARROW
//                            + (Mechanics.arrowNumberPreSet - selectedArrows
//                            .size()));
//                }
//            }
//
//            @Override
//            public void mouseEntered(MouseEvent eEntered) {
//            }
//
//            @Override
//            public void mouseExited(MouseEvent eExited) {
//            }
//
//            @Override
//            public void mousePressed(MouseEvent ePressed) {
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent eReleased) {
//            }
//
//        });

        // Mouselistener f�r 'arrowListSelected'
        arrowListSelected.addMouseListener(new MouseListener() {
            private final String REMAINING_ARROW = "Übrige Pfeile: ";

            @Override
            public void mouseClicked(MouseEvent eClicked) {
                if (Mechanics.arrowNumberPreSet - selectedArrows.size() == 0) {
                    return; // DO NOTHING. DO NOT ADD ARROWS ANYMORE.
                }

                if (Mechanics.arrowNumberPreSet != -1
                        && arrowListSelected.isAcceptingInput()) {
                    selectedArrows.remove(arrowListSelected.getSelectedIndex());
                    if (selectedArrows.isEmpty()) {
                        selectedArrows.add("<keine Pfeile>");
                        setArrowListSelected(selectedArrows);
                        remainingArrows.setName(REMAINING_ARROW
                                + Mechanics.arrowNumberPreSet);
                    } else {
                        setArrowListSelected(selectedArrows);
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
    }

    private void setArrowListSelected(LinkedList<String> selectedArrows) {
        this.remove(this.arrowListSelected);
        this.arrowListSelected = new List (arrowListSelected.getX(), arrowListSelected.getY(), arrowListSelected.getWidth(), arrowListSelected.getHeight(), this, selectedArrows);
        this.add(this.arrowListSelected);
    }

    /**
    * KONTROLLE, OB READYBUTTON GEKLICKED WURDE Kontrolle, ob alle Pfeile
    * ausgew�hlt wurden
    */
    protected class ReadyButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO: nach Fehlen kontrollieren und Warnung ausgeben
            /*
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
        }*/
           // onLeavingScreen(this, GameScreen.SCREEN_INDEX);
            System.err.println("onLeavingScreen(ArrowSelectionScreenPreSet.this, GameScreen.SCREEN_INDEX");
            System.out.println("Not right now...");
            Main.getGameWindow().dispose();
            System.exit(0);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(TRANSPARENT_BACKGROUND);
        g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
        for(Button arrowButton : buttonListArrows) {
            arrowButton.draw(g);
        }
        arrowListSelected.draw(g);
        readyButton.draw(g);
        remainingArrows.draw(g);
    }
}
