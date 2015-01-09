package gui;

import comp.Button;
import comp.*;
import comp.Component;
import comp.Label;
import general.Main;
import general.PfeileContext;
import general.TimeClock;
import newent.Bot;
import newent.BotStrength;
import newent.EntityLike;
import newent.Player;
import scala.concurrent.duration.FiniteDuration;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

/**
 * This is the Screen in which some PfeileContext values like worldSize are set. It replaces the old PreWindow.
 */
public class PreWindowScreen extends Screen {
    public static final int SCREEN_INDEX = 21;
    public static final String SCREEN_NAME = "PreWindow";

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

    /** SpinnerModel for choosing <code>PfeileContext.ARROW_NUMBER_FREE_SET</code> - "Pfeilanzahl [frei wählbar]" */
    private SpinnerModel spinnerModelPreSet;

    /** SpinnerModel for choosing <code>PfeileContext.ARROW_NUMBER_PRE_SET</code> - "Pfeilanzahl [vorher wählbar]" */
    private SpinnerModel spinnerModelFreeSet;

    /** SpinnerModel for choosing <code>PfeileContext.TURNS_PER_ROUND</code> - "Züge pro Runde" */
    private SpinnerModel spinnerModelTurnsPerRound;

    /** backgroundColor */
    private static final Color TRANSPARENT_BACKGROUND = new Color(39, 47, 69, 204);

    /** Font for "Pfeile", printed in the upper right corner */
    private Font fontBig;

    /** Color for "Pfeile" */
    private Color colorBig;

    /** position of <code>g.drawString("Pfeile", fontBigPosition.x, fontBigPosition.y); </code> */
    private Point fontBigPosition;

    /** Font for "Ein Strategiespiel" */
    private Font fontMiddle;

    /** Color of "Ein Strategiespiel" */
    private Color colorMiddle;

    /** position of <code>g.drawString("ein Strategiespiel", fontMiddlePosition.x, fontMiddlePosition.y); </code>*/
    private Point fontMiddlePosition;

    /** Font for "Josip Palavra und Daniel Schmaus" */
    private Font fontSmall;

    /** Color for "Josip Palavra und Daniel Schmaus" */
    private Color colorSmall;

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
        int labelPosY = 480;
        labels[0] = new Label(labelPosX, labelPosY, this, "Computerstärke: ");
        labels[1] = new Label(labelPosX, labelPosY + labels[0].getHeight() + 3, this, "Pfeilanzahl [frei wählbar]: ");
        labels[2] = new Label(labelPosX, labels[1].getY() + labels[1].getHeight() + 3, this, "Pfeilanzahl [vorher wählbar]: ");
        labels[3] = new Label(labelPosX, labels[2].getY() + labels[2].getHeight() + 3, this, "maximales Leben: ");
        labels[4] = new Label(labelPosX, labels[3].getY() + labels[3].getHeight() + 3, this, "Lebensregeneration: ");
        labels[5] = new Label(labelPosX, labels[4].getY() + labels[4].getHeight() + 3, this, "Schadensmultiplikator: ");
        labels[6] = new Label(labelPosX, labels[5].getY() + labels[5].getHeight() + 3, this, "Züge pro Runde: ");
        labels[7] = new Label(labelPosX, labels[6].getY() + labels[6].getHeight() + 3, this, "Zeit pro Zug: ");
        labels[8] = new Label(labelPosX, labels[7].getY() + labels[7].getHeight() + 3, this, "Handicap [Spieler]: ");
        labels[9] = new Label(labelPosX, labels[8].getY() + labels[8].getHeight() + 3, this, "Handicap [Computer]: ");
        labels[10] = new Label(labelPosX, labels[9].getY() + labels[9].getHeight() + 3, this, "Weltgröße: ");
        for (Label label : labels) {
            label.declineInput();
            label.setDeclineInputColor(new Color(202, 199, 246));
        }

        final String[] comboBoxValuesSelector = { "Computerstärke", "Pfeilanzahl [frei wählbar]", "Pfeilanzahl [vorher wählbar]",
                "maximales Leben", "Lebensregeneration", "Schadensmultiplikator", "Züge pro Runde", "Zeit pro Zug",
                "Handicap", "Weltgröße"};

        selectorComboBox = new ComboBox (labelPosX, 80, 250, 500, this, comboBoxValuesSelector);

        final String[] comboBoxValuesHigh = { "hoch", "hoch-mittel", "mittel", "mittel-niedrig", "niedrig" };
        boxSelectHigh = new ComboBox (confirmButton.getX() - 10, selectorComboBox.getY(), this, comboBoxValuesHigh);
        boxSelectHigh.setSelectedIndex(2);
        boxSelectHigh.setVisible(false);
        boxSelectHigh.declineInput();

        final String[] comboBoxValuesSize = { "gigantisch", "groß", "normal", "klein", "winzig" };
        boxSelectSize = new ComboBox (boxSelectHigh.getX(), selectorComboBox.getY(), this, comboBoxValuesSize);
        boxSelectSize.setSelectedIndex(2);
        boxSelectSize.setVisible(false);
        boxSelectSize.declineInput();

        final String[] comboBoxValuesHandicap =
                    {"+ 25%", "+ 20%", "+ 15%", "+ 10%", "+ 5%", "0%", "- 5%", "- 10%", "- 15%", "- 20%", "- 25%"};
        boxSelectHandicapPlayer = new ComboBox (boxSelectHigh.getX(), selectorComboBox.getY(), this, comboBoxValuesHandicap);
        boxSelectHandicapKI = new ComboBox (boxSelectHigh.getX() - selectorComboBox.getY() - 30, selectorComboBox.getY(), this, comboBoxValuesHandicap);
        boxSelectHandicapPlayer.setSelectedIndex(5);
        boxSelectHandicapKI.setSelectedIndex(5);
        boxSelectHandicapPlayer.setVisible(false);
        boxSelectHandicapKI.setVisible(false);
        boxSelectHandicapPlayer.declineInput();
        boxSelectHandicapKI.declineInput();

        final String[] comboBoxValuesKI = { "brutal", "stark", "normal", "schwach", "erbärmlich" };
        boxSelectKI = new ComboBox(boxSelectHigh.getX(), selectorComboBox.getY(), this, comboBoxValuesKI);
        boxSelectKI.setSelectedIndex(2);

        final String[] comboBoxValuesTime = {"5min", "2 min", "1 min", "40sec",
                "30sec", "20sec"};
        boxSelectTime = new ComboBox(boxSelectHigh.getX(), selectorComboBox.getY(), this, comboBoxValuesTime);
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
        spinner = new Spinner(boxSelectHigh.getX(), selectorComboBox.getY(), this, spinnerModelPreSet);
        spinner.setVisible(false);

        colorBig = new Color (159, 30, 29);
        colorMiddle = new Color (213, 191, 131);
        colorSmall = new Color (205, 212, 228);

        fontBig = new Font("Blade 2", Font.BOLD, 220);
        fontMiddle = new Font("Calligraphic", Font.PLAIN, 48);
        fontSmall = new Font ("Aladdin", Font.ITALIC, 21);

        if (comp.Component.isFontInstalled(fontBig) == false)
            fontBig = new Font("Viking", Font.BOLD, 105);
        if (comp.Component.isFontInstalled(fontMiddle) == false)
            fontMiddle = new Font("ShadowedGermanica", Font.PLAIN, 45);
        if (comp.Component.isFontInstalled(fontSmall) == false)
            fontSmall = new Font("Berylium", Font.ITALIC, 15);

        // the position of all points should be the same like in ArrowSelectionScreenPreSet
        fontBigPosition = new Point(confirmButton.getX() + 240, Component.getTextBounds("Pfeile", fontBig).height + 15);
        fontMiddlePosition = new Point(fontBigPosition.x + 43, fontBigPosition.y + Component.getTextBounds("ein Strategiespiel", fontMiddle).height);
        fontSmallPosition = new Point(fontMiddlePosition.x,
                   fontMiddlePosition.y + Component.getTextBounds("von Josip Palavra und Daniel Schmaus", fontSmall).height + 5);

        standardButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                triggerStandardButton();
            }
        });

        readyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                triggerReadyButton();
            }
        });

        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                triggerConfirmButton(e);
            }
        });

        selectorComboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                triggerSelectorComboBox();
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

    @Override
    public void keyPressed (KeyEvent e) {
        // Standardeinstellungen --> standardButton
        if (e.getKeyCode() == KeyEvent.VK_S) {
            triggerStandardButton();
        }
        // Fertig --> readyButton
        else if (e.getKeyCode() == KeyEvent.VK_F) {
            triggerReadyButton();
        }
        // Bestätigen --> confirmButton
        else if (e.getKeyCode() == KeyEvent.VK_B) {
            triggerConfirmButton(null);
        }
    }

    /** this triggers all action caused by pressing the readyButton ("Fertig") or pressing "r" */
    private void triggerReadyButton () {
        // tests, if every value was correctly added (i.e. not not-added)
        // and if necessary he opens the confirmDialog

        /* if (Bot.Strength().get() == -1) {
            openConfirmDialog("Select Unselected Selections: Computerstärke");
            return;
        }
        */
        if (PfeileContext.ARROW_NUMBER_FREE_SET().get() == -1) {
            openConfirmDialog("Select unselected Selections: Pfeilanzahl [frei wählbar]");
            return;
        }
        if (PfeileContext.ARROW_NUMBER_PRE_SET().get() == -1) {
            openConfirmDialog("Select unselected Selections: Pfeilanzahl [vorher wählbar]");
            return;
        }
        if (PfeileContext.TURNS_PER_ROUND().get() == -1) {
            openConfirmDialog("Select unselected Selections: Züge pro Runde");
            return;
        }
        if (Player.MAXIMUM_LIFE().get() == -1) {
            openConfirmDialog("Select unselected Selections: maximales Leben");
            return;
        }
        if (Player.LIFE_REGENERATION().get() == -1) {
            openConfirmDialog("Select unselected Selections: Lebensregeneration");
            return;
        }
        if (PfeileContext.DAMAGE_MULTI().get() == -1f) {
            openConfirmDialog("Select unselected Selections: Schadensmultiplikator");
            return;
        }
        if (TimeClock.isTurnTimeInfinite()) {
            openConfirmDialog("Select unselected Selections: Zeit pro Zug");
            return;
        }
        if (PfeileContext.WORLD_SIZE_X().get() == -1 || PfeileContext.WORLD_SIZE_Y().get() == -1) {
            openConfirmDialog("Select unselected Selections: Weltgröße");
            return;
        }
        if (PfeileContext.HANDICAP_PLAYER().get() == -1 || PfeileContext.HANDICAP_KI().get() == -1) {
            openConfirmDialog("Select unselected Selections: Handicap");
            return;
        }

        // If it's correctly initialized, the next Screen can be loaded.
        correctInits();
        ArrowSelectionScreenPreSet.getInstance();

        // If there aren't any {@link PfeileContext.ARROW_NUMBER_PRE_SET()} to set, the LoadingWorldScreen can be loaded */
        if (PfeileContext.ARROW_NUMBER_PRE_SET().get() > 0)
            onLeavingScreen(this, ArrowSelectionScreenPreSet.SCREEN_INDEX);
        else
            onLeavingScreen(this, LoadingWorldScreen$.MODULE$.SCREEN_INDEX);
    }

    /** this method triggers the action which is produced by standardButton ("Standardeinstellungen") or pressing "s" */
    private void triggerStandardButton () {
        BotStrength.Strength = BotStrength.NORMAL;
        labels[0].setText("Computerstärke: " + "normal");

        PfeileContext.ARROW_NUMBER_FREE_SET().set(5);
        labels[1].setText("Pfeilanzahl [frei wählbar]: " + PfeileContext.ARROW_NUMBER_FREE_SET().get());

        PfeileContext.ARROW_NUMBER_PRE_SET().set(15);
        labels[2].setText("Pfeilanzahl [vorher wählbar]: " + PfeileContext.ARROW_NUMBER_PRE_SET().get());

        Player.MAXIMUM_LIFE().set(400.);
        labels[3].setText("maximales Leben: " + "mittel");

        Player.LIFE_REGENERATION().set(3.);
        labels[4].setText("Lebensregeneration: " + "mittel");

        PfeileContext.DAMAGE_MULTI().set(1.0f);
        labels[5].setText("Schadensmultiplikator: " + "mittel");

        PfeileContext.TURNS_PER_ROUND().set(7);
        labels[6].setText("Züge pro Runde: " + PfeileContext.TURNS_PER_ROUND().get());

        TimeClock.setTurnTime(new FiniteDuration(1, TimeUnit.MINUTES));
        labels[7].setText("Zeit pro Zug: " + "1 min");

        PfeileContext.HANDICAP_PLAYER().set((byte) 0);
        labels[8].setText("Handicap [Spieler]: " + "0%");

        PfeileContext.HANDICAP_KI().set((byte) 0);
        labels[9].setText("Handicap [Computer]: " + "0%");

        PfeileContext.WORLD_SIZE_X().set(28);
        PfeileContext.WORLD_SIZE_Y().set(25);
        labels[10].setText("Weltgröße: " + "normal");
    }

    /** this triggers all action of confirmButton ("Bestätigen"), which also happens by pressing at "b". The usage of a
     * MouseEvent secures the triggering of all List (ComboBox) Listeners in order to make sure, that the index has changed.
     * It is not necessary, but may help against weird bugs.
     * @param e the MouseEvent - If the reason of executing this method isn't a click, use <code>null</code> here. */
    private void triggerConfirmButton (MouseEvent e) {
        // if (e != null)
        //      selectorComboBox.triggerListeners(e);
        switch (selectorComboBox.getSelectedIndex()) {
            case 0 : {
                // Computerstärke: erbärmlich = 0 --> brutal = 4
                if (e != null)
                    boxSelectKI.triggerListeners(e);
                switch (boxSelectKI.getSelectedIndex()) {
                    case 0: BotStrength.Strength = BotStrength.BRUTAL; break;
                    case 1: BotStrength.Strength = BotStrength.STRONG; break;
                    case 3: BotStrength.Strength = BotStrength.WEAK; break;
                    case 4: BotStrength.Strength = BotStrength.MISERABLE; break;
                    default: BotStrength.Strength = BotStrength.NORMAL;
                }
                labels[0].setText("Computerstärke: " + boxSelectKI.getSelectedValue());
                return;
            }
            case 1 : {
                // Pfeilanzahl [frei wählbar]
                PfeileContext.ARROW_NUMBER_FREE_SET().set(spinnerModelFreeSet.getValue());
                labels[1].setText("Pfeilanzahl [frei wählbar]: " + PfeileContext.ARROW_NUMBER_FREE_SET().get());
                return;
            }
            case 2 : {
                // Pfeilanzahl [vorher wählbar]
                PfeileContext.ARROW_NUMBER_PRE_SET().set(spinnerModelPreSet.getValue());
                labels[2].setText("Pfeilanzahl [vorher wählbar]: " + PfeileContext.ARROW_NUMBER_FREE_SET().get());
                return;
            }
            case 3 : {
                // maximales Leben
                if (e != null)
                    boxSelectHigh.triggerListeners(e);
                switch (boxSelectHigh.getSelectedIndex()) {
                    case 0: Player.MAXIMUM_LIFE().set(600.0); break;
                    case 1: Player.MAXIMUM_LIFE().set(480.0); break;
                    case 3: Player.MAXIMUM_LIFE().set(320.0); break;
                    case 4: Player.MAXIMUM_LIFE().set(270.0); break;
                    default: Player.MAXIMUM_LIFE().set(400.0);
                }
                labels[3].setText("maximales Leben: " + boxSelectHigh.getSelectedValue());
                return;
            }
            case 4 : {
                // Lebensregeneration
                if (e != null)
                    boxSelectHigh.triggerListeners(e);
                switch (boxSelectHigh.getSelectedIndex()) {
                    case 0: Player.LIFE_REGENERATION().set(5.0); break; // hoch
                    case 1: Player.LIFE_REGENERATION().set(4.0); break;
                    case 3: Player.LIFE_REGENERATION().set(2.0); break;
                    case 4: Player.LIFE_REGENERATION().set(1.0); break; // niedrig
                    default: Player.LIFE_REGENERATION().set(3.0); // mittel
                }
                labels[4].setText("Lebensregeneration: " + boxSelectHigh.getSelectedValue());
                return;
            }
            case 5 : {
                // Schadensmuliplikator
                if (e != null)
                    boxSelectHigh.triggerListeners(e);
                switch (boxSelectHigh.getSelectedIndex()) {
                    case 0: PfeileContext.DAMAGE_MULTI().set(1.9f); break; // hoch
                    case 1: PfeileContext.DAMAGE_MULTI().set(1.35f); break;
                    case 3: PfeileContext.DAMAGE_MULTI().set(0.85f); break;
                    case 4: PfeileContext.DAMAGE_MULTI().set(0.65f); break; // niedrig
                    default: PfeileContext.DAMAGE_MULTI().set(1.0f);       // mittel
                }
                labels[5].setText("Schadensmultiplikator: " + boxSelectHigh.getSelectedValue());
                return;
            }
            case 6 : {
                // Züge pro Runde
                PfeileContext.TURNS_PER_ROUND().set(spinnerModelTurnsPerRound.getValue());
                labels[6].setText("Züge pro Runde: " + spinnerModelTurnsPerRound.getValue());
                return;
            }
            case 7 : {
                // Zeit pro Zug
                if (e != null)
                    boxSelectTime.triggerListeners(e);
                switch (boxSelectTime.getSelectedIndex()) {
                    case 0: TimeClock.setTurnTime(new FiniteDuration(5, TimeUnit.MINUTES)); break;
                    case 1: TimeClock.setTurnTime(new FiniteDuration(2, TimeUnit.MINUTES)); break;
                    case 3: TimeClock.setTurnTime(new FiniteDuration(40, TimeUnit.SECONDS)); break;
                    case 4: TimeClock.setTurnTime(new FiniteDuration(30, TimeUnit.SECONDS)); break;
                    case 5: TimeClock.setTurnTime(new FiniteDuration(20, TimeUnit.SECONDS)); break;
                    default: TimeClock.setTurnTime(new FiniteDuration(1, TimeUnit.MINUTES)); // 1 min
                }
                labels[7].setText("Zeit pro Zug: " + boxSelectTime.getSelectedValue());
                return;
            }
            case 8 : {
                // Handicap
                if (e != null)
                    boxSelectHandicapPlayer.triggerListeners(e);
                switch (boxSelectHandicapPlayer.getSelectedIndex()) {
                    case 0: PfeileContext.HANDICAP_PLAYER().set((byte) +25); break;
                    case 1: PfeileContext.HANDICAP_PLAYER().set((byte) +20); break;
                    case 2: PfeileContext.HANDICAP_PLAYER().set((byte) +15); break;
                    case 3: PfeileContext.HANDICAP_PLAYER().set((byte) +10); break;
                    case 4: PfeileContext.HANDICAP_PLAYER().set((byte) + 5); break;
                    case 6: PfeileContext.HANDICAP_PLAYER().set((byte) - 5); break;
                    case 7: PfeileContext.HANDICAP_PLAYER().set((byte) -10); break;
                    case 8: PfeileContext.HANDICAP_PLAYER().set((byte) -15); break;
                    case 9: PfeileContext.HANDICAP_PLAYER().set((byte) -20); break;
                    case 10: PfeileContext.HANDICAP_PLAYER().set((byte) -25); break;
                    default: PfeileContext.HANDICAP_PLAYER().set((byte) 0);
                }
                labels[8].setText("Handicap [Spieler]: " + boxSelectHandicapPlayer.getSelectedValue());

                if (e != null)
                    boxSelectHandicapKI.triggerListeners(e);
                switch (boxSelectHandicapKI.getSelectedIndex()) {
                    case 0: PfeileContext.HANDICAP_KI().set((byte) +25); break;
                    case 1: PfeileContext.HANDICAP_KI().set((byte) +20); break;
                    case 2: PfeileContext.HANDICAP_KI().set((byte) +15); break;
                    case 3: PfeileContext.HANDICAP_KI().set((byte) +10); break;
                    case 4: PfeileContext.HANDICAP_KI().set((byte) + 5); break;
                    case 6: PfeileContext.HANDICAP_KI().set((byte) - 5); break;
                    case 7: PfeileContext.HANDICAP_KI().set((byte) -10); break;
                    case 8: PfeileContext.HANDICAP_KI().set((byte) -15); break;
                    case 9: PfeileContext.HANDICAP_KI().set((byte) -20); break;
                    case 10: PfeileContext.HANDICAP_KI().set((byte) -25); break;
                    default: PfeileContext.HANDICAP_KI().set((byte) 0);
                }
                labels[9].setText("Handicap [Computer]: " + boxSelectHandicapKI.getSelectedValue());
                return;
            }
            case 9: {
                // Weltgröße
                if (e != null)
                    boxSelectSize.triggerListeners(e);
                switch (boxSelectSize.getSelectedIndex()) {
                    case 0: PfeileContext.WORLD_SIZE_X().set(55); PfeileContext.WORLD_SIZE_Y().set(48);
                    case 1: PfeileContext.WORLD_SIZE_X().set(35); PfeileContext.WORLD_SIZE_Y().set(30);
                    case 3: PfeileContext.WORLD_SIZE_X().set(22); PfeileContext.WORLD_SIZE_Y().set(18);
                    case 4: PfeileContext.WORLD_SIZE_X().set(15); PfeileContext.WORLD_SIZE_Y().set(12);
                    default: PfeileContext.WORLD_SIZE_X().set(28); PfeileContext.WORLD_SIZE_Y().set(25);
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

    /** this triggers the comboBox for securing, that only the necessary components are active */
    private void triggerSelectorComboBox () {
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
        }
    }




    /**
     * Use that methods after clicking at <code> readyButton </code>, because here the missing corrections
     * i.e.<code> Player.LifeRegeneration().get() </code>
     */
    public static void correctInits() {
        if (Player.LIFE_REGENERATION().get() == 1) { // 5 = hoch; 1 == niedrig
            Player.LIFE_REGENERATION().set((0.5 * (Player.MAXIMUM_LIFE().get() * 0.008 + 2)));
        } else if (Player.LIFE_REGENERATION().get() == 2) {
            Player.LIFE_REGENERATION().set(0.5 * (Player.MAXIMUM_LIFE().get() * 0.001 + 3));
        } else if (Player.LIFE_REGENERATION().get() == 4) {
            Player.LIFE_REGENERATION().set(0.5 * (Player.MAXIMUM_LIFE().get() * 0.02 + 4.5));
        } else if (Player.LIFE_REGENERATION().get() == 5) {
            Player.LIFE_REGENERATION().set(0.5 * (Player.MAXIMUM_LIFE().get() * 0.025 + 7));
        } else { // Standard: lifeRegeneration: normal
            Player.LIFE_REGENERATION().set(0.5 * (Player.MAXIMUM_LIFE().get() * 0.015 + 3.5));
        }
        Bot.MAXIMUM_LIFE().set(Player.MAXIMUM_LIFE().get());
        Bot.LIFE_REGENERATION().set(Player.LIFE_REGENERATION().get());
    }

    /** this updates arrowNumberFreeSetUsable (from Player and Bot)
     * Only use this method directly after added all Players (and Bots) to the entityList */
    public static void correctArrowNumber () {
	    for(EntityLike e : Main.getContext().getWorld().entities().javaEntityList()) {
		    if(e instanceof Player) {
			    Player player = (Player) e;
			    player.arrowNumberFreeSetUsable().set(PfeileContext.ARROW_NUMBER_FREE_SET().get());
		    }
		    else if (e instanceof Bot) {
                Bot bot = (Bot) e;
                bot.selectArrowsPreSet();
                bot.arrowNumberFreeSetUsable().set(PfeileContext.ARROW_NUMBER_FREE_SET().get());
            }
	    }
    }


    @Override
    public void draw (Graphics2D g) {
        // Backgound
        g.setColor(TRANSPARENT_BACKGROUND);
        g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());

        g.setColor(colorBig);
        g.setFont(fontBig);
        g.drawString("Pfeile", fontBigPosition.x, fontBigPosition.y);
        g.setColor(colorMiddle);
        g.setFont(fontMiddle);
        g.drawString("ein Strategiespiel", fontMiddlePosition.x, fontMiddlePosition.y);
        g.setColor(colorSmall);
        g.setFont(fontSmall);
        g.drawString("von Josip Palavra und Daniel Schmaus", fontSmallPosition.x, fontSmallPosition.y );

        g.setFont(Component.STD_FONT);

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
