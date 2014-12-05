package gui;

import comp.Button;
import comp.ConfirmDialog;
import comp.Label;
import comp.List;
import general.GameLoop;
import general.Main;
import general.PfeileContext;
import player.weapon.*;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Random;

/**
 * This Screen is used to set the Arrows before the Game (<code>PfeileContext.arrowNumberPreSet</code>). It directly replaces
 * ArrowSelection, but it is obviously a Screen.
 * <p>
 * The Problem right now is, that if you've added an arrow to <code>arrowListSelected</code> (List on the right side of the Screen)
 * and you want to remove it, he always removes the first Index (0). This is because in <code>list.getSelectedIndex()</code>,
 * you need to press it twice, but here the List always refreshes after an action.
 */
public class ArrowSelectionScreenPreSet extends Screen {

    public static final int SCREEN_INDEX = 256;
    public static final String SCREEN_NAME = "ArrowSelectionScreenPreSet";

    private Label remainingArrows;
    private Button readyButton, randomButton;

    /** Liste der Button f�r andere Aufgaben */
    Button [] buttonListArrows = new Button[8];
    private List arrowListSelected;
    private ConfirmDialog confirmDialog;
    public LinkedList<String> selectedArrows;

    /** Hintergrund Farbe */
    private static final Color TRANSPARENT_BACKGROUND = new Color(39, 47, 69, 204);

    /** Font for "Ein Strategiespiel" */
    private Font fontMiddle;

    /** position of <code>g.drawString("ein Strategiespiel", fontMiddlePosition.x, fontMiddlePosition.y); </code> */
    private Point fontMiddlePosition;

    /** position of <code>g.drawString("ein Strategiespiel", fontMiddlePosition.x, fontMiddlePosition.y); </code> */
    private Color colorMiddle;

    /** Font for "Pfeile", printed in the upper right corner */
    private Font fontBig;

    /** position of <code>g.drawString("Pfeile", fontBigPosition.x, fontBigPosition.y); </code> */
    private Point fontBigPosition;

    /** Color of <code>g.drawString("Pfeile", fontBigPosition.x, fontBigPosition.y); </code> */
    private Color colorBig;

    /**
     * Screen für die Pfeilauswahl für vorhersetzbaren Pfeilen.
     * äquivalent zu <code> ArrowSelection </code>.
     */
    public ArrowSelectionScreenPreSet() {
        super(SCREEN_NAME, SCREEN_INDEX);

        onScreenLeft.register(new AbstractFunction1<ScreenChangedEvent, BoxedUnit>() {
            @Override
            public BoxedUnit apply (ScreenChangedEvent v1) {
                Main.getContext().getTimeClock().reset();
                Main.getContext().getTimeClock().start();
                System.out.println(Main.getContext().getTimeClock().getMilliDeath());

                return BoxedUnit.UNIT;
            }
        });

        selectedArrows = new LinkedList<String>();
        arrowListSelected = new List(50, 200, 200, 350, this, selectedArrows);

        if (PfeileContext.ARROW_NUMBER_PRE_SET().get() > 1) {
            remainingArrows = new Label(Main.getWindowWidth() - 232, Main.getWindowHeight() - 200, this, "Verfügbare Pfeile definieren!");
        } else {
            remainingArrows = new Label(Main.getWindowWidth() - 232, Main.getWindowHeight() - 200, this, "Verfügbaren Pfeil definieren!");
        }

        remainingArrows.setDeclineInputColor(new Color(202, 199, 246));

        colorBig = new Color (159, 30, 29);
        colorMiddle = new Color (213, 191, 131);

        fontBig = new Font("Blade 2", Font.BOLD, 220);
        fontMiddle = new Font("Calligraphic", Font.PLAIN, 48);

        if (comp.Component.isFontInstalled(fontBig) == false)
            fontBig = new Font("Viking", Font.BOLD, 105);
        if (comp.Component.isFontInstalled(fontMiddle) == false)
            fontMiddle = new Font("ShadowedGermanica", Font.PLAIN, 45);

        // fontBigPosition.x = PreWindowScreen.fontBigPosition.x --> if you change the value there you have to change it here, too.
        fontBigPosition = new Point(790, comp.Component.getTextBounds("Pfeile", fontBig).height + 5);
        fontMiddlePosition = new Point(fontBigPosition.x + 43, fontBigPosition.y + comp.Component.getTextBounds("ein Strategiespiel", fontMiddle).height);

        /** Y-Position des ersten Buttons (Bildschirm) */
        int posYButtons = 60;
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
        buttonListArrows [2].iconify(ArrowHelper.getArrowImage(StormArrow.INDEX));
        buttonListArrows [3].iconify(ArrowHelper.getArrowImage(StoneArrow.INDEX));
        buttonListArrows [4].iconify(ArrowHelper.getArrowImage(IceArrow.INDEX));
        buttonListArrows [5].iconify(ArrowHelper.getArrowImage(LightningArrow.INDEX));
        buttonListArrows [6].iconify(ArrowHelper.getArrowImage(LightArrow.INDEX));
        buttonListArrows [7].iconify(ArrowHelper.getArrowImage(ShadowArrow.INDEX));

        for (Button button : buttonListArrows)
            button.addMouseListener(new ButtonHelper());

        confirmDialog = new ConfirmDialog(500, 300, this, "");
        confirmDialog.setVisible(false);
        confirmDialog.getOk().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                closeConfirmDialogQuestion();
            }
        });
        confirmDialog.getCancel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                closeConfirmDialogQuestion();
            }
        });

        // Position is equal to PreWindowScreen.readyButton
        readyButton = new Button(Main.getWindowWidth() - 220, Main.getWindowHeight() - 150, this, "Bestätigen");
        readyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (readyButton.getSimplifiedBounds().contains(e.getPoint())) {
                    if (selectedArrows.size() > PfeileContext.ARROW_NUMBER_PRE_SET().get()) {
                        throw new IllegalStateException("To many arrows added: They can't be more than "
                                + PfeileContext.ARROW_NUMBER_PRE_SET().get());
                    }

                    if (selectedArrows.size() < PfeileContext.ARROW_NUMBER_PRE_SET().get()) {
                        openConfirmQuestion("Bitten wählen sie alle Pfeile aus!");
                    } else {
                        GameLoop.setRunFlag(false);
                    }
                }
            }
        });

        randomButton = new Button (readyButton.getX(), readyButton.getY() - 200, this, "Zufälliger Pfeil");
        randomButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                if (randomButton.getSimplifiedBounds().contains(e.getPoint())) {
                    java.util.Random randomGen = new Random();
                    String arrow = ArrowHelper.arrowIndexToName(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES));

                    if (PfeileContext.ARROW_NUMBER_PRE_SET().get() > selectedArrows.size()) {
                        if (selectedArrows.get(0).equals("<keine Pfeile>")) {
                            selectedArrows.remove(0);
                        }
                        selectedArrows.add(arrow);
                        remainingArrows.setText("Übrige Pfeile: " + (PfeileContext.ARROW_NUMBER_PRE_SET().get() - selectedArrows.size()));
                        setArrowListSelected(selectedArrows);
                    }
                }
            }
        });

        selectedArrows.add("<keine Pfeile>");
        setArrowListSelected(selectedArrows);

        // Mouselistener f�r 'arrowListSelected'
        arrowListSelected.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed (MouseEvent eClicked) {
                if (arrowListSelected.getSimplifiedBounds().contains(eClicked.getPoint())
                            && arrowListSelected.isAcceptingInput()) {
                    arrowListSelected.triggerListeners(eClicked);
                    selectedArrows.remove(arrowListSelected.getSelectedIndex());
                    if (selectedArrows.isEmpty()) {
                        selectedArrows.add("<keine Pfeile>");
                        setArrowListSelected(selectedArrows);

                        if (PfeileContext.ARROW_NUMBER_PRE_SET().get() > 1)
                            remainingArrows.setText("Verfügbare Pfeile auswählen!");
                        else
                            remainingArrows.setText("Verfügbaren Pfeil auswählen!");

                    } else {
                        setArrowListSelected(selectedArrows);
                        remainingArrows.setText("Übrige Pfeile: "
                                + (PfeileContext.ARROW_NUMBER_PRE_SET().get() - selectedArrows
                                .size()));
                    }
                }
            }
        });
    }

    private void setArrowListSelected(LinkedList<String> selectedArrows) {
        arrowListSelected = new List (arrowListSelected.getX(), arrowListSelected.getY(),
                arrowListSelected.getWidth(), arrowListSelected.getHeight(), this, selectedArrows);
    }

    /**
     * Opens the "Are you sure?" dialog with specified question.
     * @param question The question to display.
     */
    private void openConfirmQuestion (String question) {
        confirmDialog.setQuestionText(question);
        confirmDialog.setVisible(true);
        for (Button button : buttonListArrows)
            button.declineInput();
        readyButton.declineInput();
        arrowListSelected.declineInput();
    }

    /**
     * Closes the "Are you sure?" dialog.
     */
    private void closeConfirmDialogQuestion () {
        confirmDialog.setQuestionText("");
        confirmDialog.setVisible(false);
        for (Button button : buttonListArrows)
            button.acceptInput();
        readyButton.acceptInput();
        arrowListSelected.acceptInput();
    }

    private class ButtonHelper extends MouseAdapter {
        @Override
        public void mouseReleased (MouseEvent e) {
            for (Button buttonListArrow : buttonListArrows) {
                if (buttonListArrow.getSimplifiedBounds().contains(e.getPoint())) {
                    if (PfeileContext.ARROW_NUMBER_PRE_SET().get() > selectedArrows.size()) {
                        if (selectedArrows.get(0).equals("<keine Pfeile>")) {
                            selectedArrows.remove(0);
                        }
                        selectedArrows.add(buttonListArrow.getText());
                        remainingArrows.setText("Übrige Pfeile: " + (PfeileContext.ARROW_NUMBER_PRE_SET().get() - selectedArrows.size()));
                        setArrowListSelected(selectedArrows);
                    }
                }
            }
        }
    }

    @Override
    public void keyPressed (KeyEvent e) {
        super.keyPressed(e);

        // Bestätigen. Code hier drinn muss der selbe sein wie in dem Listener von confirmButton
        if (e.getKeyCode() == KeyEvent.VK_B) {
            if (selectedArrows.size() > PfeileContext.ARROW_NUMBER_PRE_SET().get()) {
                throw new IllegalStateException("To many arrows added: They can't be more than " + PfeileContext.ARROW_NUMBER_PRE_SET().get());
            }

            if (selectedArrows.size() < PfeileContext.ARROW_NUMBER_PRE_SET().get()) {
                openConfirmQuestion("Bitten wählen sie alle Pfeile aus!");
            } else {
                GameLoop.setRunFlag(false);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_Z) {
            java.util.Random randomGen = new Random();
            String arrow = ArrowHelper.arrowIndexToName(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES));

            if (PfeileContext.ARROW_NUMBER_PRE_SET().get() > selectedArrows.size()) {
                if (selectedArrows.get(0).equals("<keine Pfeile>")) {
                    selectedArrows.remove(0);
                }
                selectedArrows.add(arrow);
                remainingArrows.setText("Übrige Pfeile: " + (PfeileContext.ARROW_NUMBER_PRE_SET().get() - selectedArrows.size()));
                setArrowListSelected(selectedArrows);
            }
        }
        // FIXME: remove this code here later
        // die Anzahl an zufälligen Pfeilen durch alle anderen Tasten direkt auswählen
        else {
            java.util.Random randomGen = new Random();
            if (selectedArrows.get(0).equals("<keine Pfeile>")) {
                selectedArrows.remove(0);
            }
            while (selectedArrows.size() < PfeileContext.ARROW_NUMBER_PRE_SET().get()) {
                selectedArrows.add(ArrowHelper.arrowIndexToName(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
            }
            remainingArrows.setText("Übrige Pfeile: " + (PfeileContext.ARROW_NUMBER_PRE_SET().get() - selectedArrows.size()));
            setArrowListSelected(selectedArrows);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(TRANSPARENT_BACKGROUND);
        g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
        g.setColor(colorBig);
        g.setFont(fontBig);
        g.drawString("Pfeile", fontBigPosition.x, fontBigPosition.y);
        g.setColor(colorMiddle);
        g.setFont(fontMiddle);
        g.drawString("ein Strategiespiel", fontMiddlePosition.x, fontMiddlePosition.y);

        for(Button arrowButton : buttonListArrows) {
            arrowButton.draw(g);
        }
        arrowListSelected.draw(g);
        randomButton.draw(g);
        readyButton.draw(g);
        remainingArrows.draw(g);
        confirmDialog.draw(g);
    }
}
