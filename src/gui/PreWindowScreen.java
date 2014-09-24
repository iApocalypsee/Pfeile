package gui;

import comp.*;
import comp.Button;
import comp.Component;
import comp.Label;
import general.Main;
import general.Mechanics;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * This is the Screen in which all (Mechanics) values like worldSize are set. It replaces the old PreWindow.
 */
public class PreWindowScreen extends Screen {
    public static final int SCREEN_INDEX = 21;
    public static final String SCREEN_NAME = "PreWindow";

    private static boolean isReady = false;

    /** The Big ComboBox on the right side to select the value i.e. Computerstärke, Weltgröße,... */
    private ComboBox selectorComboBox;

    /** These are Labels that show the values, that are selected right now */
    private comp.Label[] labels = new comp.Label [11];

    /** The StandardSettingButton on the right down corner */
    private comp.Button standardButton;

    /** The Button to click, when your ready */
    private comp.Button readyButton;

    /** The Button, which need to be pressed for confirming the selection of the new value */
    private Button confirmButton;

    /** ComboBox for choosing the difficulty of the computer */
    private ComboBox boxSelectKI;

    /** ComboBox for choosing the Size of Something i.e. worldSize */
    private ComboBox boxSelectSize;

    /** ComboBox for choosing the Hight of Something i.e. life, lifeRegeneration */
    private ComboBox boxSelectHigh;

    /** ComboBox for choosing the time limit used by TimeClock */
    private ComboBox boxSelectTime;

    /** ComboBox for choosing the percentage of Handicap for the player */
    private ComboBox boxSelectHandicapPlayer;

    /** ComboBox for choosing the percentage of Handicap for the KI */
    private ComboBox boxSelectHandicapKI;

    /** ConfirmDialog to show questions and warnings */
    private ConfirmDialog confirmDialog;

    /** The Spinner for selecting the amount of arrows. */
    private Spinner spinner;

    /** SpinnerModel for choosing <code>Mechanics.arrowNumberPreSet</code> - "Pfeilanzahl [frei wählbar]" */
    private SpinnerModel spinnerModelPreSet;

    /** SpinnerModel for choosing <code>Mechanics.arrowNumberFreeSet</code> - "Pfeilanzahl [vorher wählbar]" */
    private SpinnerModel spinnerModelFreeSet;

    /** SpinnerModel for choosing <code>Mechanics.turnsPerRound</code> - "Züge pro Runde" */
    private SpinnerModel spinnerModelTurnsPerRound;

    /** backgroundColor */
    private static final Color TRANSPARENT_BACKGROUND = new Color(39, 47, 69, 204);

    /** Font for "Pfeile", printed in the upper right corner */
    private Font fontBig;

    /** position of <code>g.drawString("Pfeile", fontBigPosition.x, fontBigPosition.y); </code> */
    private Point fontBigPosition;

    /** Font for "Ein Strategiespiel" */
    private Font fontMiddle;

    /** position of <code>g.drawString("ein Strategiespiel", fontMiddlePosition.x, fontMiddlePosition.y); </code>*/
    private Point fontMiddlePosition;

    /** Font for "Josip Palavra und Daniel Schmaus" */
    private Font fontSmall;

    /** position of <code>g.drawString("von Josip Palavra und Daniel Schmaus", fontSmallPosition.x, fontSmallPosition.y")</code> */
    private Point fontSmallPosition;

    public PreWindowScreen() {
        super(SCREEN_NAME, SCREEN_INDEX);

        // Initialise the Components
        confirmButton = new Button(550, 400, this, "Bestätigen");
        confirmButton.setRoundBorder(true);

        readyButton = new Button(Main.getWindowWidth() - 220, Main.getWindowHeight() - 150, this, "Fertig");
        standardButton = new Button(readyButton.getX(), readyButton.getY() - 100, this, "Standardeinstellung");
        readyButton.setWidth(standardButton.getWidth());

        int labelPosX = 100;
        int labelPosY = 500;
        labels[0] = new Label(labelPosX, labelPosY, this, "Computerstärke: ");
        labels[1] = new Label(labelPosX, labelPosY + labels[0].getHeight(), this, "Pfeilanzahl [frei wählbar]: ");
        labels[2] = new Label(labelPosX, labels[1].getY() + labels[1].getHeight(), this, "Pfeilanzahl [vorher wählbar]: ");
        labels[3] = new Label(labelPosX, labels[2].getY() + labels[2].getHeight(), this, "maximales Leben: ");
        labels[4] = new Label(labelPosX, labels[3].getY() + labels[3].getHeight(), this, "Lebensregeneration: ");
        labels[5] = new Label(labelPosX, labels[4].getY() + labels[4].getHeight(), this, "Schadensmultiplikator: ");
        labels[6] = new Label(labelPosX, labels[5].getY() + labels[5].getHeight(), this, "Züge pro Runde: ");
        labels[7] = new Label(labelPosX, labels[6].getY() + labels[6].getHeight(), this, "Zeit pro Zug: ");
        labels[8] = new Label(labelPosX, labels[7].getY() + labels[7].getHeight(), this, "Handicap [Spieler]: ");
        labels[9] = new Label(labelPosX, labels[8].getY() + labels[8].getHeight(), this, "Handicap [Computer]: ");
        labels[10] = new Label(labelPosX, labels[9].getY() + labels[9].getHeight(), this, "Weltgröße: ");
        for (Label label : labels) {
            label.declineInput();
        }

        String[] comboBoxValuesSelector = { "Computerstärke", "Pfeilanzahl [frei wählbar]", "Pfeilanzahl [vorher wählbar]",
                "maximales Leben", "Lebensregeneration", "Schadensmultiplikator", "Züge pro Runde", "Zeit pro Zug",
                "Handicap", "Weltgröße"};

        selectorComboBox = new ComboBox (labelPosX, 80, 250, 500, this, comboBoxValuesSelector);

        String[] comboBoxValuesHigh = { "hoch", "hoch-mittel", "mittel", "mittel-niedrig", "niedrig" };
        boxSelectHigh = new ComboBox (confirmButton.getX(), selectorComboBox.getY(), this, comboBoxValuesHigh);
        boxSelectHigh.setSelectedIndex(2);
        boxSelectHigh.setVisible(false);
        boxSelectHigh.declineInput();

        String[] comboBoxValuesSize = { "gigantisch", "groß", "normal", "klein", "winzig" };
        boxSelectSize = new ComboBox (confirmButton.getX(), selectorComboBox.getY(), this, comboBoxValuesSize);
        boxSelectSize.setSelectedIndex(2);
        boxSelectSize.setVisible(false);
        boxSelectSize.declineInput();

        final String[] comboBoxValuesHandicap =
                    {"+ 25%", "+ 20%", "+ 15%", "+ 10%", "+ 5%", "0%", "- 5%", "- 10%", "- 15%", "- 20%", "- 25%"};
        boxSelectHandicapPlayer = new ComboBox (confirmButton.getX(), selectorComboBox.getY(), this, comboBoxValuesHandicap);
        boxSelectHandicapKI = new ComboBox (confirmButton.getX() - selectorComboBox.getY() - 30, selectorComboBox.getY(), this, comboBoxValuesHandicap);
        boxSelectHandicapPlayer.setSelectedIndex(5);
        boxSelectHandicapKI.setSelectedIndex(5);
        boxSelectHandicapPlayer.setVisible(false);
        boxSelectHandicapKI.setVisible(false);
        boxSelectHandicapPlayer.declineInput();
        boxSelectHandicapKI.declineInput();

        String[] comboBoxValuesKI = { "brutal", "stark", "normal", "schwach", "erbärmlich" };
        boxSelectKI = new ComboBox(confirmButton.getX(), selectorComboBox.getY(), this, comboBoxValuesKI);
        boxSelectKI.setSelectedIndex(2);

        String[] comboBoxValuesTime = {"5min", "2 min", "1 min", "40sec",
                "30sec", "20sec"};
        boxSelectTime = new ComboBox(confirmButton.getX(), selectorComboBox.getY(), this, comboBoxValuesTime);
        boxSelectTime.setSelectedIndex(2);
        boxSelectTime.setVisible(false);
        boxSelectTime.declineInput();

        confirmDialog = new ConfirmDialog(500, 500, this, "");
        confirmDialog.setX(Main.getWindowWidth() / 2 - confirmDialog.getWidth() / 2);
        confirmDialog.setY(Main.getWindowHeight() / 2 - confirmDialog.getHeight() / 2);
        confirmDialog.addMouseListener(new MouseAdapterConfirmDialog());
        confirmDialog.setVisible(false);

        spinnerModelPreSet = new SpinnerModel(10, 0, 50, 1);
        spinnerModelFreeSet = new SpinnerModel(5, 0, 30, 1);
        spinnerModelTurnsPerRound = new SpinnerModel(7, 1, 40, 1);
        spinner = new Spinner(confirmButton.getX(), selectorComboBox.getY(), this, spinnerModelPreSet);
        spinner.setVisible(false);

        fontBig = new Font("Blade 2", Font.BOLD, 220);
        fontMiddle = new Font("Calligraphic", Font.PLAIN, 48);
        fontSmall = new Font ("Aladdin", Font.ITALIC, 21);

        if (comp.Component.isFontInstalled(fontBig) == false)
            fontBig = new Font("Viking", Font.BOLD, 105);
        if (comp.Component.isFontInstalled(fontMiddle) == false)
            fontMiddle = new Font("ShadowedGermanica", Font.PLAIN, 45);
        if (comp.Component.isFontInstalled(fontSmall) == false)
            fontSmall = new Font("Berylium", Font.ITALIC, 15);

        fontBigPosition = new Point(confirmButton.getX() + 240, Component.getTextBounds("Pfeile", fontBig).height + 5);
        fontMiddlePosition = new Point(fontBigPosition.x + 43, fontBigPosition.y + Component.getTextBounds("ein Strategiespiel", fontMiddle).height);
        fontSmallPosition = new Point(fontMiddlePosition.x,
                   fontMiddlePosition.y + Component.getTextBounds("von Josip Palavra und Daniel Schmaus", fontSmall).height + 5);

        standardButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                Mechanics.KI = 2;
                labels[0].setText("Computerstärke: " + "normal");

                Mechanics.arrowNumberFreeSet = 5;
                labels[1].setText("Pfeilanzahl [frei wählbar]: " + Mechanics.arrowNumberFreeSet);

                Mechanics.arrowNumberPreSet = 10;
                labels[2].setText("Pfeilanzahl [vorher wählbar]: " + Mechanics.arrowNumberPreSet);

                Mechanics.lifeMax = 400;
                labels[3].setText("maximales Leben: " + "mittel");

                Mechanics.lifeRegeneration = 3;
                labels[4].setText("Lebensregeneration: " + "mittel");

                Mechanics.damageMulti = 3;
                labels[5].setText("Schadensmultiplikator: " + "mittel");

                Mechanics.turnsPerRound = 7;
                labels[6].setText("Züge pro Runde: " + Mechanics.turnsPerRound);

                Mechanics.timePerPlay = 60000;
                labels[7].setText("Zeit pro Zug: " + "1 min");

                Mechanics.handicapPlayer = 0;
                labels[8].setText("Handicap [Spieler]: " + "0%");

                Mechanics.handicapKI = 0;
                labels[9].setText("Handicap [Computer]: " + "0%");

                Mechanics.worldSizeX = 13;
                Mechanics.worldSizeY = 11;
                labels[10].setText("Weltgröße: " + "normal");
            }
        });

        readyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                // tests, if every value was correctly added (i.e. not not-added)
                // and if necessary he opens the confirmDialog
                if (Mechanics.KI == -1) {
                    openConfirmDialog("Select Unselected Selections: Computerstärke");
                    return;
                }
                if (Mechanics.arrowNumberFreeSet == -1) {
                    openConfirmDialog("Select unselected Selections: Pfeilanzahl [frei wählbar]");
                    return;
                }
                if (Mechanics.arrowNumberPreSet == -1) {
                    openConfirmDialog("Select unselected Selections: Pfeilanzahl [vorher wählbar]");
                    return;
                }
                if (Mechanics.turnsPerRound == -1) {
                    openConfirmDialog("Select unselected Selections: Züge pro Runde");
                    return;
                }
                if (Mechanics.lifeMax == -1) {
                    openConfirmDialog("Select unselected Selections: maximales Leben");
                    return;
                }
                if (Mechanics.lifeRegeneration == -1) {
                    openConfirmDialog("Select unselected Selections: Lebensregeneration");
                    return;
                }
                if (Mechanics.damageMulti == -1f) {
                    openConfirmDialog("Select unselected Selections: Schadensmultiplikator");
                    return;
                }
                if (Mechanics.timePerPlay == -1) {
                    openConfirmDialog("Select unselected Selections: Zeit pro Zug");
                    return;
                }
                if (Mechanics.worldSizeX == -1 || Mechanics.worldSizeY == -1) {
                    openConfirmDialog("Select unselected Selections: Weltgröße");
                    return;
                }
                if (Mechanics.handicapPlayer == -1 || Mechanics.handicapKI == -1) {
                    openConfirmDialog("Select unselected Selections: Handicap");
                    return;
                }

                correctInits();
                new ArrowSelectionScreenPreSet();

                if (Mechanics.arrowNumberPreSet > 0)
                    onLeavingScreen(this, ArrowSelectionScreenPreSet.SCREEN_INDEX);
                else
                    onLeavingScreen(this, GameScreen.SCREEN_INDEX);
            }
        });

        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                if (confirmButton.getSimplifiedBounds().contains(e.getPoint())) {
                    //selectorComboBox.triggerListeners(e);
                    switch (selectorComboBox.getSelectedIndex()) {
                        case 0 : {
                            // Computerstärke: erbärmlich = 0 --> brutal = 4
                            boxSelectKI.triggerListeners(e);
                            switch (boxSelectKI.getSelectedIndex()) {
                                case 0: Mechanics.KI = 4; break;
                                case 1: Mechanics.KI = 3; break;
                                case 3: Mechanics.KI = 1; break;
                                case 4: Mechanics.KI = 0; break;
                                default: Mechanics.KI = 2;
                            }
                            labels[0].setText("Computerstärke: " + boxSelectKI.getSelectedValue());
                            return;
                        }
                        case 1 : {
                            // Pfeilanzahl [frei wählbar]
                            Mechanics.arrowNumberFreeSet = spinnerModelFreeSet.getValue();
                            labels[1].setText("Pfeilanzahl [frei wählbar]: " + Mechanics.arrowNumberFreeSet);
                            return;
                        }
                        case 2 : {
                            // Pfeilanzahl [vorher wählbar]
                            Mechanics.arrowNumberPreSet = spinnerModelPreSet.getValue();
                            labels[2].setText("Pfeilanzahl [vorher wählbar]: " + spinnerModelPreSet.getValue());
                            return;
                        }
                        case 3 : {
                            // maximales Leben
                            boxSelectHigh.triggerListeners(e);
                            switch (boxSelectHigh.getSelectedIndex()) {
                                case 0: Mechanics.lifeMax = 600; break;
                                case 1: Mechanics.lifeMax = 480; break;
                                case 3: Mechanics.lifeMax = 320; break;
                                case 4: Mechanics.lifeMax = 270; break;
                                default: Mechanics.lifeMax = 400;
                            }
                            labels[3].setText("maximales Leben: " + boxSelectHigh.getSelectedValue());
                            return;
                        }
                        case 4 : {
                            // Lebensregeneration
                            boxSelectHigh.triggerListeners(e);
                            switch (boxSelectHigh.getSelectedIndex()) {
                                case 0: Mechanics.lifeRegeneration = 5; break; // hoch
                                case 1: Mechanics.lifeRegeneration = 4; break;
                                case 3: Mechanics.lifeRegeneration = 2; break;
                                case 4: Mechanics.lifeRegeneration = 1; break; // niedrig
                                default: Mechanics.lifeRegeneration = 3; // mittel
                            }
                            labels[4].setText("Lebensregeneration: " + boxSelectHigh.getSelectedValue());
                            return;
                        }
                        case 5 : {
                            // Schadensmuliplikator
                            boxSelectHigh.triggerListeners(e);
                            switch (boxSelectHigh.getSelectedIndex()) {
                                case 0: Mechanics.damageMulti = 1.8f; break; // hoch
                                case 1: Mechanics.damageMulti = 1.3f; break;
                                case 3: Mechanics.damageMulti = 0.8f; break;
                                case 4: Mechanics.damageMulti = 0.6f; break; // niedrig
                                default: Mechanics.damageMulti = 1.0f;       // mittel
                            }
                            labels[5].setText("Schadensmultiplikator: " + boxSelectHigh.getSelectedValue());
                            return;
                        }
                        case 6 : {
                            // Züge pro Runde
                            Mechanics.turnsPerRound = spinnerModelTurnsPerRound.getValue();
                            labels[6].setText("Züge pro Runde: " + spinnerModelTurnsPerRound.getValue());
                            return;
                        }
                        case 7 : {
                            // Zeit pro Zug
                            boxSelectTime.triggerListeners(e);
                            switch (boxSelectTime.getSelectedIndex()) {
                                case 0: Mechanics.timePerPlay = 5 * 60000; break;
                                case 1: Mechanics.timePerPlay = 2 * 60000; break;
                                case 3: Mechanics.timePerPlay = Math.round((40f / 60f) * 60000); break;
                                case 4: Mechanics.timePerPlay = Math.round((30f / 60f) * 60000); break;
                                case 5: Mechanics.timePerPlay = Math.round((20f / 60f) * 60000); break;
                                default: Mechanics.timePerPlay = 60000; // 1 min
                            }
                            labels[7].setText("Zeit pro Zug: " + boxSelectTime.getSelectedValue());
                            return;
                        }
                        case 8 : {
                            // Handicap
                            boxSelectHandicapPlayer.triggerListeners(e);
                            switch (boxSelectHandicapPlayer.getSelectedIndex()) {
                                case 0: Mechanics.handicapPlayer = +25; break;
                                case 1: Mechanics.handicapPlayer = +20; break;
                                case 2: Mechanics.handicapPlayer = +15; break;
                                case 3: Mechanics.handicapPlayer = +10; break;
                                case 4: Mechanics.handicapPlayer = + 5; break;
                                case 6: Mechanics.handicapPlayer = - 5; break;
                                case 7: Mechanics.handicapPlayer = -10; break;
                                case 8: Mechanics.handicapPlayer = -15; break;
                                case 9: Mechanics.handicapPlayer = -20; break;
                                case 10: Mechanics.handicapPlayer= -25; break;
                                default: Mechanics.handicapPlayer=   0;
                            }
                            labels[8].setText("Handicap [Spieler]: " + boxSelectHandicapPlayer.getSelectedValue());

                            boxSelectHandicapKI.triggerListeners(e);
                            switch (boxSelectHandicapKI.getSelectedIndex()) {
                                case 0: Mechanics.handicapKI = +25; break;
                                case 1: Mechanics.handicapKI = +20; break;
                                case 2: Mechanics.handicapKI = +15; break;
                                case 3: Mechanics.handicapKI = +10; break;
                                case 4: Mechanics.handicapKI = + 5; break;
                                case 6: Mechanics.handicapKI = - 5; break;
                                case 7: Mechanics.handicapKI = -10; break;
                                case 8: Mechanics.handicapKI = -15; break;
                                case 9: Mechanics.handicapKI = -20; break;
                                case 10: Mechanics.handicapKI= -25; break;
                                default: Mechanics.handicapKI=   0;
                            }
                            labels[9].setText("Handicap [Computer]: " + boxSelectHandicapKI.getSelectedValue());
                            return;
                        }
                        case 9: {
                            // Weltgröße
                            boxSelectSize.triggerListeners(e);
                            switch (boxSelectSize.getSelectedIndex()) {
                                case 0: Mechanics.worldSizeX = 23; Mechanics.worldSizeY = 21; break;
                                case 1: Mechanics.worldSizeX = 17; Mechanics.worldSizeY = 15; break;
                                case 3: Mechanics.worldSizeX =  9; Mechanics.worldSizeY =  7; break;
                                case 4: Mechanics.worldSizeX =  6; Mechanics.worldSizeY =  4; break;
                                default:Mechanics.worldSizeX = 13; Mechanics.worldSizeY = 11;
                            }
                            labels[10].setText("Weltgröße: " + boxSelectSize.getSelectedValue());
                            return;
                        }
                        default: {
                            openConfirmDialog("Der ausgewählte Index von der <code> selectorComboBox </code> konnte nicht gefunden werden.");
                            System.err.println("The selected Index of selectorComboBox couldn't be found. " +
                                    selectorComboBox.getSelectedValue() + " at " + selectorComboBox.getSelectedIndex() +
                                    " This error is in PreWindowScreen at: confirmButton.addMouseListener(...).");
                        }
                    }
                }
            }
        });

        selectorComboBox.addMouseListener(new MouseAdapter() {
            private int oldSelectedIndex = selectorComboBox.getSelectedIndex();
            @Override
            public void mouseReleased (MouseEvent e) {
                // if this check is true, it is like an itemChancedEvent
                if (selectorComboBox.getSelectedIndex() != oldSelectedIndex) {
                    switch (selectorComboBox.getSelectedIndex()) {
                        case 0: { // Computerstärke
                            boxSelectKI.setVisible(true);
                            spinner.setVisible(false);
                            boxSelectHandicapKI.setVisible(false);
                            boxSelectHandicapPlayer.setVisible(false);
                            boxSelectHigh.setVisible(false);
                            boxSelectSize.setVisible(false);
                            boxSelectTime.setVisible(false);
                            break;
                        }
                        case 1: { // Pfeilanzahl [frei wählbar]
                            spinner.setVisible(true);
                            spinner.setSpinnerModel(spinnerModelFreeSet);
                            boxSelectHandicapKI.setVisible(false);
                            boxSelectHandicapPlayer.setVisible(false);
                            boxSelectHigh.setVisible(false);
                            boxSelectKI.setVisible(false);
                            boxSelectSize.setVisible(false);
                            boxSelectTime.setVisible(false);
                            break;
                        }
                        case 2: { // Pfeilanzahl [vorher wählbar]
                            spinner.setVisible(true);
                            spinner.setSpinnerModel(spinnerModelPreSet);
                            boxSelectHandicapKI.setVisible(false);
                            boxSelectHandicapPlayer.setVisible(false);
                            boxSelectHigh.setVisible(false);
                            boxSelectKI.setVisible(false);
                            boxSelectSize.setVisible(false);
                            boxSelectTime.setVisible(false);
                            break;
                        }
                        case 3: { // maximales Leben
                            boxSelectHigh.setVisible(true);
                            spinner.setVisible(false);
                            boxSelectHandicapKI.setVisible(false);
                            boxSelectHandicapPlayer.setVisible(false);
                            boxSelectKI.setVisible(false);
                            boxSelectSize.setVisible(false);
                            boxSelectTime.setVisible(false);
                            break;
                        }
                        case 4: { // Lebensregeneration
                            boxSelectHigh.setVisible(true);
                            spinner.setVisible(false);
                            boxSelectHandicapKI.setVisible(false);
                            boxSelectHandicapPlayer.setVisible(false);
                            boxSelectKI.setVisible(false);
                            boxSelectSize.setVisible(false);
                            boxSelectTime.setVisible(false);
                            break;
                        }
                        case 5: { // Schadensmultiplikator
                            boxSelectHigh.setVisible(true);
                            spinner.setVisible(false);
                            boxSelectHandicapKI.setVisible(false);
                            boxSelectHandicapPlayer.setVisible(false);
                            boxSelectKI.setVisible(false);
                            boxSelectSize.setVisible(false);
                            boxSelectTime.setVisible(false);
                            break;
                        }
                        case 6: { // Züge pro Runde
                            spinner.setVisible(true);
                            spinner.setSpinnerModel(spinnerModelTurnsPerRound);
                            boxSelectHandicapKI.setVisible(false);
                            boxSelectHandicapPlayer.setVisible(false);
                            boxSelectHigh.setVisible(false);
                            boxSelectKI.setVisible(false);
                            boxSelectSize.setVisible(false);
                            boxSelectTime.setVisible(false);
                            break;
                        }
                        case 7: { // Zeit pro Zug
                            boxSelectTime.setVisible(true);
                            spinner.setVisible(false);
                            boxSelectHandicapKI.setVisible(false);
                            boxSelectHandicapPlayer.setVisible(false);
                            boxSelectHigh.setVisible(false);
                            boxSelectKI.setVisible(false);
                            boxSelectSize.setVisible(false);
                            break;
                        }
                        case 8: { // Handicap
                            boxSelectHandicapKI.setVisible(true);
                            spinner.setVisible(false);
                            boxSelectHandicapPlayer.setVisible(true);
                            boxSelectHigh.setVisible(false);
                            boxSelectKI.setVisible(false);
                            boxSelectSize.setVisible(false);
                            boxSelectTime.setVisible(false);
                            break;
                        }
                        case 9: { // Weltgröße
                            boxSelectSize.setVisible(true);
                            spinner.setVisible(false);
                            boxSelectHandicapKI.setVisible(false);
                            boxSelectHandicapPlayer.setVisible(false);
                            boxSelectHigh.setVisible(false);
                            boxSelectKI.setVisible(false);
                            boxSelectTime.setVisible(false);
                            break;
                        }
                        default: {
                            openConfirmDialog("Fehler bei der Auswahl von der ComboBox <code> selectorComboBox </code>");
                            System.err.println("Error: trying to reach " + selectorComboBox.getSelectedIndex() + " " +
                                    "in PreWindowScreen at confirmButton.addMouseListner(...), " +
                                    "however there is not such an index.");
                            selectorComboBox.setSelectedIndex(0);
                        }
                        oldSelectedIndex = selectorComboBox.getSelectedIndex();
                    }
                }
            }
        });
    }

    /**
     * Opens the "Are you sure?" dialog with specified question.
     * @param question The question to display.
     */
    private void openConfirmDialog (String question) {
        confirmDialog.setQuestionText(question);
        confirmDialog.setVisible(true);

        readyButton.declineInput();
        standardButton.declineInput();
        confirmButton.declineInput();
        selectorComboBox.declineInput();
        boxSelectHandicapKI.declineInput();
        boxSelectHandicapPlayer.declineInput();
        boxSelectHigh.declineInput();
        boxSelectKI.declineInput();
        boxSelectSize.declineInput();
        boxSelectTime.declineInput();
        spinner.declineInput();
    }

    /**
     * Closes the "Are you sure?" dialog.
     */
    private void closeConfirmDialog () {
        confirmDialog.setQuestionText("");
        confirmDialog.setVisible(false);

        readyButton.acceptInput();
        standardButton.acceptInput();
        confirmButton.acceptInput();
        selectorComboBox.acceptInput();
        if (boxSelectHandicapKI.isVisible()) {
            boxSelectHandicapKI.acceptInput();
            boxSelectHandicapPlayer.acceptInput();
            return;
        }
        if (boxSelectHigh.isVisible())
            boxSelectHigh.acceptInput();
        if (boxSelectKI.isVisible())
            boxSelectKI.acceptInput();
        if (boxSelectSize.isVisible())
            boxSelectSize.acceptInput();
        if (boxSelectTime.isVisible())
            boxSelectTime.acceptInput();
        if (spinner.isVisible())
            spinner.acceptInput();
    }

    /** private class to close an open ConfirmDialog.
     *  It is registering, if there was a click at confirmDialog.okButton or confirmDialog.cancelButton */
    private class MouseAdapterConfirmDialog extends MouseAdapter {
        @Override
        public void mousePressed (MouseEvent e) {
            if (confirmDialog.isVisible()) {
                if (confirmDialog.getOk().getSimplifiedBounds().contains(e.getPoint()))
                    closeConfirmDialog();

                if (confirmDialog.getCancel().getSimplifiedBounds().contains(e.getPoint()))
                    closeConfirmDialog();

            }
        }
    }

    /**
     * Use that methods after clicking at <code> readyButton </code>, because here the missing corrections
     * i.e.<code> Mechanics.lifeRegeneration </code>
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

    /** upper methods: setLifeRegenerationCorrect() --> Corrects the totalArrowNumber() */
    private static void setTotalArrowNumberCorrect() {
        Mechanics.totalArrowNumber = Mechanics.arrowNumberFreeSet
                + Mechanics.arrowNumberPreSet;
        Mechanics.arrowNumberFreeSetUseable = Mechanics.arrowNumberFreeSet;
    }


    @Override
    public void draw (Graphics2D g) {
        // Backgound
        g.setColor(TRANSPARENT_BACKGROUND);
        g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());

        g.setColor(new Color (159, 30, 29));
        g.setFont(fontBig);
        g.drawString("Pfeile", fontBigPosition.x, fontBigPosition.y);
        g.setColor(new Color (213, 191, 131));
        g.setFont(fontMiddle);
        g.drawString("ein Strategiespiel", fontMiddlePosition.x, fontMiddlePosition.y);
        g.setColor(new Color (205, 212, 228));
        g.setFont(fontSmall);
        g.drawString("von Josip Palavra und Daniel Schmaus", fontSmallPosition.x, fontSmallPosition.y );

        // Components
        boxSelectKI.draw(g);
        boxSelectHigh.draw(g);
        boxSelectHandicapKI.draw(g);
        boxSelectHandicapPlayer.draw(g);
        boxSelectSize.draw(g);
        boxSelectTime.draw(g);
        spinner.draw(g);
        for (Label label : labels)
            label.draw(g);
        selectorComboBox.draw(g);
        confirmButton.draw(g);
        standardButton.draw(g);
        readyButton.draw(g);
        confirmDialog.draw(g);
    }
}
