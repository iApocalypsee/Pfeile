package gui.screen;

import comp.Button;
import comp.*;
import comp.Component;
import comp.Label;
import general.Main;
import general.PfeileContext;
import general.TimeClock;
import general.io.FontLoader;
import newent.*;
import scala.concurrent.duration.FiniteDuration;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.List;

/**
 * This is the Screen in which some PfeileContext values like worldSize are set. It replaces the old PreWindow.
 * TODO there should be a button to select how many loots are spawning or valuable their content is.
 */
public class PreWindowScreen extends Screen {
    public static final int SCREEN_INDEX = 21;
    public static final String SCREEN_NAME = "PreWindow";

    /** The Big ComboBox on the right side to select the value i.e. Computerstärke, Weltgröße,... */
    private ComboBox selectorComboBox;

    /** These are Labels that show the values, that are selected right now */
    private comp.Label[] labels = new comp.Label [13];

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
    private Spinner<Integer> spinner;

    /** SpinnerModel for choosing <code>PfeileContext.ARROW_NUMBER_FREE_SET</code> - "Pfeilanzahl [frei wählbar]" */
    private RangeSpinnerModel spinnerModelPreSet;

    /** SpinnerModel for choosing <code>PfeileContext.ARROW_NUMBER_PRE_SET</code> - "Pfeilanzahl [vorher wählbar]" */
    private RangeSpinnerModel spinnerModelFreeSet;

    /** SpinnerModel for choosing <code>PfeileContext.TURNS_PER_ROUND</code> - "Züge pro Runde" */
    private RangeSpinnerModel spinnerModelTurnsPerRound;

    /** SpinnerModel for choosing "Startgold" */
    private RangeSpinnerModel spinnerModelStartGold;

    /** SpinnerModel for choosing "Gold pro Zug" */
    private RangeSpinnerModel spinnerModelGoldPerTurn;

    /** enter the name of the player */
    private TextBox boxPlayer;

    /** enter the name of the opponent */
    private TextBox boxOpponent;

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

    private final String pfeileLabel, strategyGameLabel, authorsLabel, selectSelections;

    // <<< Test code >>>
    private ComboBox test;
    private Button reloadCombobox = new Button(1000, 400, this, "Reload test combobox");
    private void reloadTestCombobox() {
        test = new ComboBox(1000, 500, 300, 100, this, new String[]{"Test 1", "Test 2", "3. Test"});
        test.setVisible(true);
        this.forcePullFront(test);
        this.forcePullFront(reloadCombobox);
    }
    private comp.NormalList list2;
    private comp.Label multilineLabel = new comp.Label(400, 400, this, "Multiline\nstring that should be\nawesome to read.");
    // <<< Test code ende >>>

    public PreWindowScreen() {
        super(SCREEN_NAME, SCREEN_INDEX);

        final String defaultSettings = Main.tr("defaultSettings"),
                     aiStrength = Main.tr("aiStrength"), //in the translation is a spelling error
                     initialMoney = Main.tr("initialMoney"),
                     moneyPerTurn = Main.tr("moneyPerTurn"),
                     arrowAmountFreeSet = Main.tr("arrowAmountFreeSet"),
                     arrowAmountPreSet = Main.tr("arrowAmountPreSet"),
                     maxLife = Main.tr("maxLife"),
                     lifeRegen = Main.tr("lifeRegen"),
                     dmgScale = Main.tr("dmgScale"),
                     turnsPerRound = Main.tr("turnsPerRound"),
                     timePerTurn = Main.tr("timePerTurn"),
                     handicap = Main.tr("handicap"),
                     worldsize = Main.tr("worldsize"),
                     confirm = Main.tr("confirm"),
                     done = Main.tr("done"); // --> TODO to "ready" for unique key-input

        final String high = Main.tr("high"),
                     balanced = Main.tr("balanced"),
                     low = Main.tr("low"),
                     huge = Main.tr("huge"),
                     large = Main.tr("large"),
                     normal = Main.tr("normal"),
                     small = Main.tr("small"),
                     tiny = Main.tr("tiny"),
                     brutal = Main.tr("brutal"),
                     strong = Main.tr("strong"),
                     decent = Main.tr("decent"),
                     weak = Main.tr("weak"),
                     miserable = Main.tr("miserable"),
                     balancedHigh = balanced + "-" + high,
                     balancedLow = balanced + "-" + low;

        pfeileLabel = "Pfeile";
        strategyGameLabel = Main.tr("label_strategyGame");
        authorsLabel = Main.tr("label_authors");
        selectSelections = Main.tr("done");

        // Initialise the Components
        confirmButton = new Button(550, 400, this, confirm);
        confirmButton.setRoundBorder(true);

        readyButton = new Button(Main.getWindowWidth() - 220, Main.getWindowHeight() - 95, this, done);
        standardButton = new Button(readyButton.getX(), readyButton.getY() - 60, this, defaultSettings);
        readyButton.setWidth(standardButton.getWidth());

        final String[] labelValues = {
                aiStrength, initialMoney, moneyPerTurn,
                arrowAmountFreeSet, arrowAmountPreSet,
                maxLife, lifeRegen, dmgScale,
                turnsPerRound, timePerTurn, handicap + " [" + Main.getUser().getUsername() + "]", handicap + " [" + "Opponent" + "]", worldsize};

        int labelPosX = 100;
        int labelPosY = 370;
        int labelYGap = 4;

        labels[0] = new Label(labelPosX, labelPosY, this, labelValues[0] + ":");
        for (int i = 1; i < labels.length; i++)
            labels[i] = new Label(labelPosX, labels[i-1].getY() + labels[i-1].getHeight() + labelYGap, this, labelValues[i] + ":");

        for (Label label : labels) {
            label.declineInput();
            label.setDeclineInputColor(new Color(202, 199, 246));
        }

        final String[] comboBoxValuesSelector = { aiStrength, initialMoney, moneyPerTurn,
                arrowAmountFreeSet, arrowAmountPreSet,
                maxLife, lifeRegen, dmgScale,
                turnsPerRound, timePerTurn, handicap, worldsize};

        selectorComboBox = new ComboBox (labelPosX, 80, 250, 500, this, comboBoxValuesSelector);

        final String[] comboBoxValuesHigh = { high, balancedHigh, balanced, balancedLow, low };
        boxSelectHigh = new ComboBox (confirmButton.getX() - 10, selectorComboBox.getY(), this, comboBoxValuesHigh);
        boxSelectHigh.setSelectedIndex(2);
        boxSelectHigh.setVisible(false);
        boxSelectHigh.declineInput();

        final String[] comboBoxValuesSize = { huge, large, normal, small, tiny };
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

        final String[] comboBoxValuesKI = { brutal, strong, decent, weak, miserable };
        boxSelectKI = new ComboBox(boxSelectHigh.getX(), selectorComboBox.getY(), this, comboBoxValuesKI);
        boxSelectKI.setSelectedIndex(2);

        final String[] comboBoxValuesTime = {"10min", "5 min", "3 min", "2 min", "1 min", "40sec"};
        boxSelectTime = new ComboBox(boxSelectHigh.getX(), selectorComboBox.getY(), this, comboBoxValuesTime);
        boxSelectTime.setSelectedIndex(3);
        boxSelectTime.setVisible(false);
        boxSelectTime.declineInput();

        confirmDialog = new ConfirmDialog(500, 500, this, "");
        confirmDialog.setX(Main.getWindowWidth() / 2 - confirmDialog.getWidth() / 2);
        confirmDialog.setY(Main.getWindowHeight() / 2 - confirmDialog.getHeight() / 2);
        confirmDialog.addMouseListener(new MouseAdapterConfirmDialog());
        confirmDialog.setVisible(false);


        spinnerModelStartGold = new RangeSpinnerModel(250, 0, 1200, 15);
        spinnerModelGoldPerTurn = new RangeSpinnerModel(10, 0, 100, 2);
        spinnerModelPreSet = new RangeSpinnerModel(15, 0, 50, 1);
        spinnerModelFreeSet = new RangeSpinnerModel(4, 0, 20, 1);
        spinnerModelTurnsPerRound = new RangeSpinnerModel(8, 1, 40, 1);

        spinner = new Spinner<>(boxSelectHigh.getX(), selectorComboBox.getY(), this, spinnerModelPreSet);
        spinner.setVisible(false);

        //TODO: Translation for Player 1 & 2; key input is missing; player name must be saved and used (e.g. in ArrowSelectionScreenPreSet)
        boxPlayer = new TextBox(standardButton.getX(), standardButton.getY() - 150, "Player 1", this);
        boxPlayer.setWidth(standardButton.getWidth());
        boxPlayer.setHeight(standardButton.getHeight());

        boxOpponent = new TextBox(boxPlayer.getX(), boxPlayer.getY() + 60, "Player 2", this);
        boxOpponent.setWidth(boxPlayer.getWidth());
        boxOpponent.setHeight(boxPlayer.getHeight());

        // Logo
        colorBig = new Color (159, 30, 29);
        colorMiddle = new Color (213, 191, 131);
        colorSmall = new Color (205, 212, 228);

        fontBig = FontLoader.loadFont("Augusta", 140, Font.BOLD, FontLoader.FontType.TTF);
        fontMiddle = FontLoader.loadFont("ShadowedGermanica", 45, FontLoader.FontType.TTF);
        fontSmall = FontLoader.loadFont("Berylium", 20, Font.ITALIC, FontLoader.FontType.TTF);

        // the position of all points should be the same like in ArrowSelectionScreenPreSet
        fontBigPosition = new Point(confirmButton.getX() + 230, Component.getTextBounds("Pfeile", fontBig).height + 65);
        fontMiddlePosition = new Point(fontBigPosition.x + 43, fontBigPosition.y + Component.getTextBounds(strategyGameLabel, fontMiddle).height + 15);
        fontSmallPosition = new Point(fontMiddlePosition.x,
                   fontMiddlePosition.y + Component.getTextBounds(authorsLabel, fontSmall).height + 10);

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
            public void mouseReleased(MouseEvent e) {
                triggerConfirmButton(e);
            }
        });

        selectorComboBox.registerOnItemSelected(this::triggerSelectorComboBoxByIndex);

        reloadTestCombobox();
        this.forcePullFront(test);
        this.forcePullFront(reloadCombobox);
        list2 = new NormalList();

        list2.appendElement(new Label(0, 0, this, "First element"));
        list2.appendElement(new Label(0, 0, this, "Second element"));
        list2.appendElement(new Label(0, 0, this, "Third element"));
        list2.setX(250);
        list2.setY(300);
        list2.setBackingScreen(this);

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
        boxPlayer.declineInput();
        boxOpponent.declineInput();
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
        boxPlayer.acceptInput();
        boxOpponent.acceptInput();
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
                if (confirmDialog.getOk().getPreciseRectangle().contains(e.getPoint()))
                    closeConfirmDialog();

                if (confirmDialog.getCancel().getPreciseRectangle().contains(e.getPoint()))
                    closeConfirmDialog();

            }
        }
    }

    @Override
    public void keyPressed (KeyEvent e) {
        if (Main.isEnglish()) {
            // Standardeinstellungen
            if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_SPACE)
                triggerStandardButton();

            // Fertig-Button
            if (e.getKeyCode() == KeyEvent.VK_F)
                triggerReadyButton();

            // Bestätigen
            if (e.getKeyCode() == KeyEvent.VK_C || e.getKeyCode() == KeyEvent.VK_ENTER)
                triggerConfirmButton(null);

        } else {
            if (e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_SPACE)
                triggerStandardButton();

            // Fertig-Button
            if (e.getKeyCode() == KeyEvent.VK_R)
                triggerReadyButton();

            // Bestätigen
            if (e.getKeyCode() == KeyEvent.VK_B || e.getKeyCode() == KeyEvent.VK_ENTER)
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
        if (MoneyValues.startMoney().isEmpty()) {
            openConfirmDialog(selectSelections + ": initial money");
            return;
        }
        if (MoneyValues.moneyPerTurn().isEmpty()) {
            openConfirmDialog(selectSelections + ": money per turn");
            return;
        }
        if (PfeileContext.arrowNumberFreeSet().isEmpty()) {
            openConfirmDialog(selectSelections + ": number of arrows");
            return;
        }
        if (PfeileContext.arrowNumberPreSet().isEmpty()) {
            openConfirmDialog(selectSelections + ": number of arrows before round");
            return;
        }
        if (PfeileContext.turnsPerRound().isEmpty()) {
            openConfirmDialog(selectSelections + ": turns per round");
            return;
        }
        if (Player.maximumLife().isEmpty()) {
            openConfirmDialog(selectSelections + ": maximum life");
            return;
        }
        if (Player.lifeRegeneration().isEmpty()) {
            openConfirmDialog(selectSelections + ": life regeneration");
            return;
        }
        if (PfeileContext.damageMultiplicator().isEmpty()) {
            openConfirmDialog(selectSelections + ": damage amplification");
            return;
        }
        if (TimeClock.isTurnTimeInfinite()) {
            openConfirmDialog(selectSelections + ": time per turn");
            return;
        }
        if (PfeileContext.worldSizeX().isEmpty() || PfeileContext.worldSizeY().isEmpty()) {
            openConfirmDialog(selectSelections + ": world size");
            return;
        }
        if (PfeileContext.handicapPlayer().isEmpty() || PfeileContext.handicapAI().isEmpty()) {
            openConfirmDialog(selectSelections + ": handicap");
            return;
        }

        // If it's correctly initialized, the next Screen can be loaded.
        correctInits();

        // If there aren't any {@link PfeileContext.ARROW_NUMBER_PRE_SET()} to set, the LoadingWorldScreen can be loaded */
        if (PfeileContext.arrowNumberPreSet().get() > 0)
            onLeavingScreen(ArrowSelectionScreenPreSet.SCREEN_INDEX);
        else
            onLeavingScreen(LoadingWorldScreen$.MODULE$.SCREEN_INDEX);
    }

    /** this method triggers the action which is produced by standardButton ("Standardeinstellungen") or pressing "s" */
    private void triggerStandardButton () {
        BotStrength.Strength = BotStrength.NORMAL;
        labels[0].setText("AI strength: " + "normal");

        MoneyValues.startMoney().set(250);
        labels[1].setText("Initial money: " + MoneyValues.startMoney().get());

        MoneyValues.moneyPerTurn().set(10);
        labels[2].setText("Money per turn: " + MoneyValues.moneyPerTurn().get());

        PfeileContext.arrowNumberFreeSet().set(4);
        labels[3].setText("Number of arrows: " + PfeileContext.arrowNumberFreeSet().get());

        PfeileContext.arrowNumberPreSet().set(15);
        labels[4].setText("Number of arrows before round: " + PfeileContext.arrowNumberPreSet().get());

        Player.maximumLife().set(400.);
        labels[5].setText("Maximum life: " + "balanced");

        Player.lifeRegeneration().set(3.);
        labels[6].setText("Life regeneration: " + "balanced");

        PfeileContext.damageMultiplicator().set(1.0f);
        labels[7].setText("Damage amplification: " + "balanced");

        PfeileContext.turnsPerRound().set(10);
        labels[8].setText("Moves per round: " + PfeileContext.turnsPerRound().get());

        TimeClock.setTurnTime(new FiniteDuration(2, TimeUnit.MINUTES));
        labels[9].setText("Time per round: " + "2 min");

        PfeileContext.handicapPlayer().set(0);
        labels[10].setText("Handicap [" + Main.getUser().getUsername() + "]: " + "0%");

        PfeileContext.handicapAI().set(0);
        labels[11].setText("Handicap [" + "Opponent" + "]: " + "0%");

        PfeileContext.worldSizeX().set(28);
        PfeileContext.worldSizeY().set(25);
        labels[12].setText("World size: " + "normal");
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
                labels[0].setText("AI strenth: " + boxSelectKI.getSelectedValue());
                return;
            }
            case 1 : { // Startgold
                MoneyValues.startMoney().set(spinnerModelStartGold.getCurrent());
                labels[1].setText("Initial money: " + spinnerModelStartGold.getCurrent());
                return;
            }
            case 2 : { // Gold pro Zug
                MoneyValues.moneyPerTurn().set(spinnerModelGoldPerTurn.getCurrent());
                labels[2].setText("Money per turn: " + spinnerModelGoldPerTurn.getCurrent());
                return;
            }
            case 3 : {
                // Pfeilanzahl [frei wählbar]
                PfeileContext.arrowNumberFreeSet().set(spinnerModelFreeSet.getCurrent());
                labels[3].setText("Number of arrows: " + PfeileContext.arrowNumberFreeSet().get());
                return;
            }
            case 4 : {
                // Pfeilanzahl [vorher wählbar]
                PfeileContext.arrowNumberPreSet().set(spinnerModelPreSet.getCurrent());
                labels[4].setText("Number of arrows before round: " + PfeileContext.arrowNumberPreSet().get());
                return;
            }
            case 5 : {
                // maximales Leben
                if (e != null)
                    boxSelectHigh.triggerListeners(e);
                switch (boxSelectHigh.getSelectedIndex()) {
                    case 0: Player.maximumLife().set(600.0); break;
                    case 1: Player.maximumLife().set(480.0); break;
                    case 3: Player.maximumLife().set(320.0); break;
                    case 4: Player.maximumLife().set(270.0); break;
                    default: Player.maximumLife().set(400.0);
                }
                labels[5].setText("Maximum life: " + boxSelectHigh.getSelectedValue());
                return;
            }
            case 6 : {
                // Lebensregeneration
                if (e != null)
                    boxSelectHigh.triggerListeners(e);
                switch (boxSelectHigh.getSelectedIndex()) {
                    case 0: Player.lifeRegeneration().set(5.0); break; // hoch
                    case 1: Player.lifeRegeneration().set(4.0); break;
                    case 3: Player.lifeRegeneration().set(2.0); break;
                    case 4: Player.lifeRegeneration().set(1.0); break; // niedrig
                    default: Player.lifeRegeneration().set(3.0); // mittel
                }
                labels[6].setText("Health regeneration: " + boxSelectHigh.getSelectedValue());
                return;
            }
            case 7 : {
                // Schadensmuliplikator
                if (e != null)
                    boxSelectHigh.triggerListeners(e);
                switch (boxSelectHigh.getSelectedIndex()) {
                    case 0: PfeileContext.damageMultiplicator().set(1.9f); break; // hoch
                    case 1: PfeileContext.damageMultiplicator().set(1.35f); break;
                    case 3: PfeileContext.damageMultiplicator().set(0.85f); break;
                    case 4: PfeileContext.damageMultiplicator().set(0.65f); break; // niedrig
                    default: PfeileContext.damageMultiplicator().set(1.0f);       // mittel
                }
                labels[7].setText("Damage amplification: " + boxSelectHigh.getSelectedValue());
                return;
            }
            case 8 : {
                // Züge pro Runde
                PfeileContext.turnsPerRound().set(spinnerModelTurnsPerRound.getCurrent());
                labels[8].setText("Turns per round: " + spinnerModelTurnsPerRound.getCurrent());
                return;
            }
            case 9 : {
                // Zeit pro Zug: "10min", "5 min", "3 min", "2 min", "1 min", "40sec"
                if (e != null)
                    boxSelectTime.triggerListeners(e);
                switch (boxSelectTime.getSelectedIndex()) {
                    case 0: TimeClock.setTurnTime(new FiniteDuration(10, TimeUnit.MINUTES)); break;
                    case 1: TimeClock.setTurnTime(new FiniteDuration(5, TimeUnit.MINUTES)); break;
                    case 2: TimeClock.setTurnTime(new FiniteDuration(3, TimeUnit.MINUTES)); break;
                    case 4: TimeClock.setTurnTime(new FiniteDuration(1, TimeUnit.MINUTES)); break;
                    case 5: TimeClock.setTurnTime(new FiniteDuration(40, TimeUnit.SECONDS)); break;
                    default: TimeClock.setTurnTime(new FiniteDuration(2, TimeUnit.MINUTES));
                }
                labels[9].setText("Time per turn: " + boxSelectTime.getSelectedValue());
                return;
            }
            case 10 : {
                // Handicap
                if (e != null)
                    boxSelectHandicapPlayer.triggerListeners(e);
                switch (boxSelectHandicapPlayer.getSelectedIndex()) {
                    case 0: PfeileContext.handicapPlayer().set(+25); break;
                    case 1: PfeileContext.handicapPlayer().set(+20); break;
                    case 2: PfeileContext.handicapPlayer().set(+15); break;
                    case 3: PfeileContext.handicapPlayer().set(+10); break;
                    case 4: PfeileContext.handicapPlayer().set(+ 5); break;
                    case 6: PfeileContext.handicapPlayer().set(- 5); break;
                    case 7: PfeileContext.handicapPlayer().set(-10); break;
                    case 8: PfeileContext.handicapPlayer().set(-15); break;
                    case 9: PfeileContext.handicapPlayer().set(-20); break;
                    case 10: PfeileContext.handicapPlayer().set(-25); break;
                    default: PfeileContext.handicapPlayer().set(0);
                }
                labels[10].setText("Handicap [" + Main.getUser().getUsername() + "]: " + boxSelectHandicapPlayer.getSelectedValue());

                if (e != null)
                    boxSelectHandicapKI.triggerListeners(e);
                switch (boxSelectHandicapKI.getSelectedIndex()) {
                    case 0: PfeileContext.handicapAI().set(+25); break;
                    case 1: PfeileContext.handicapAI().set(+20); break;
                    case 2: PfeileContext.handicapAI().set(+15); break;
                    case 3: PfeileContext.handicapAI().set(+10); break;
                    case 4: PfeileContext.handicapAI().set(+ 5); break;
                    case 6: PfeileContext.handicapAI().set(- 5); break;
                    case 7: PfeileContext.handicapAI().set(-10); break;
                    case 8: PfeileContext.handicapAI().set(-15); break;
                    case 9: PfeileContext.handicapAI().set(-20); break;
                    case 10: PfeileContext.handicapAI().set(-25); break;
                    default: PfeileContext.handicapAI().set(0);
                }
                labels[11].setText("Handicap [" + "Opponent" + "]: " + boxSelectHandicapKI.getSelectedValue());
                return;
            }
            case 11: {
                // Weltgröße
                if (e != null)
                    boxSelectSize.triggerListeners(e);
                switch (boxSelectSize.getSelectedIndex()) {
                    case 0: PfeileContext.worldSizeX().set(55); PfeileContext.worldSizeY().set(48); break;
                    case 1: PfeileContext.worldSizeX().set(35); PfeileContext.worldSizeY().set(30); break;
                    case 3: PfeileContext.worldSizeX().set(22); PfeileContext.worldSizeY().set(18); break;
                    case 4: PfeileContext.worldSizeX().set(15); PfeileContext.worldSizeY().set(12); break;
                    default: PfeileContext.worldSizeX().set(28); PfeileContext.worldSizeY().set(25);
                }
                labels[12].setText("World size: " + boxSelectSize.getSelectedValue());
                return;
            }
            default: {
                String msg = Main.tr("errorIndexOutOfBounds", selectorComboBox.getSelectedIndex());
                openConfirmDialog(msg);
                System.err.println(msg);
                System.err.println("at: PreWindowScreen.triggerConfirmButton()\n");
            }
        }
    }

    private void triggerSelectorComboBoxByIndex(int index) {
        switch (index) {
            case 0: { // Computerstärke
                setSelectingComponentsNotVisible();
                boxSelectKI.setVisible(true);
                break;
            }
            case 1: { // Startgeld
                setSelectingComponentsNotVisible();
                spinner.setSpinnerModel(spinnerModelStartGold);
                spinner.setVisible(true);
                break;
            }
            case 2: { // Geld pro Zug
                setSelectingComponentsNotVisible();
                spinner.setSpinnerModel(spinnerModelGoldPerTurn);
                spinner.setVisible(true);
                break;
            }
            case 3: { // Pfeilanzahl [frei wählbar]
                setSelectingComponentsNotVisible();
                spinner.setSpinnerModel(spinnerModelFreeSet);
                spinner.setVisible(true);
                break;
            }
            case 4: { // Pfeilanzahl [vorher wählbar]
                setSelectingComponentsNotVisible();
                spinner.setVisible(true);
                spinner.setSpinnerModel(spinnerModelPreSet);
                break;
            }
            case 5: { // maximales Leben
                setSelectingComponentsNotVisible();
                boxSelectHigh.setVisible(true);
                break;
            }
            case 6: { // Lebensregeneration
                setSelectingComponentsNotVisible();
                boxSelectHigh.setVisible(true);
                break;
            }
            case 7: { // Schadensmultiplikator
                setSelectingComponentsNotVisible();
                boxSelectHigh.setVisible(true);
                break;
            }
            case 8: { // Züge pro Runde
                setSelectingComponentsNotVisible();
                spinner.setVisible(true);
                spinner.setSpinnerModel(spinnerModelTurnsPerRound);
                break;
            }
            case 9: { // Zeit pro Zug
                setSelectingComponentsNotVisible();
                boxSelectTime.setVisible(true);
                break;
            }
            case 10: { // Handicap
                setSelectingComponentsNotVisible();
                boxSelectHandicapKI.setVisible(true);
                boxSelectHandicapPlayer.setVisible(true);
                break;
            }
            case 11: { // Weltgröße
                setSelectingComponentsNotVisible();
                boxSelectSize.setVisible(true);
                break;
            }
            default: {
                String msg = Main.tr("errorIndexOutOfBounds", selectorComboBox.getSelectedIndex());
                openConfirmDialog(msg);
                System.err.println(msg);
                System.err.println("at: PreWindowScreen.triggerSelectorComboBoxByIndex\n");
                selectorComboBox.setSelectedIndex(0);
            }
        }
    }

    /** This method should only be used by triggerSelectorComboBox(). It sets every box/spinner .setVisible(false), so
     * that triggerSelectorComboBox() can set visible, whatever it needs.
     */
    private void setSelectingComponentsNotVisible () {
        spinner.setVisible(false);
        boxSelectHandicapPlayer.setVisible(false);
        boxSelectHandicapKI.setVisible(false);
        boxSelectHigh.setVisible(false);
        boxSelectKI.setVisible(false);
        boxSelectSize.setVisible(false);
        boxSelectTime.setVisible(false);
    }



    /**
     * Use that methods after clicking at <code> readyButton </code>, because here the missing corrections
     * i.e.<code> Player.LifeRegeneration().get() </code>
     */
    public static void correctInits() {
        if (Player.lifeRegeneration().get() == 1) { // 5 = hoch; 1 == niedrig
            Player.lifeRegeneration().set((0.5 * (Player.maximumLife().get() * 0.008 + 2)));
        } else if (Player.lifeRegeneration().get() == 2) {
            Player.lifeRegeneration().set(0.5 * (Player.maximumLife().get() * 0.001 + 3));
        } else if (Player.lifeRegeneration().get() == 4) {
            Player.lifeRegeneration().set(0.5 * (Player.maximumLife().get() * 0.02 + 4.5));
        } else if (Player.lifeRegeneration().get() == 5) {
            Player.lifeRegeneration().set(0.5 * (Player.maximumLife().get() * 0.025 + 7));
        } else { // Standard: lifeRegeneration: normal
            Player.lifeRegeneration().set(0.5 * (Player.maximumLife().get() * 0.015 + 3.5));
        }
        Bot.maximumLife().set(Player.maximumLife().get());
        Bot.lifeRegeneration().set(Player.lifeRegeneration().get());
    }

    /** this updates arrowNumberFreeSetUsable (from Player and Bot)
     * Only use this method directly after added all Players (and Bots) to the entityList
     * TODO: List<GameObject> doesn't fit here</GameObject>*/
    public static void correctArrowNumber (List<GameObject> entities) {
	    for(GameObject e : entities) {
		    if(e instanceof Player) {
			    Player player = (Player) e;
			    player.arrowNumberFreeSetUsable().set(PfeileContext.arrowNumberFreeSet().get());
		    }
		    else if (e instanceof Bot) {
                Bot bot = (Bot) e;
                bot.selectArrowsPreSet();
                bot.arrowNumberFreeSetUsable().set(PfeileContext.arrowNumberFreeSet().get());
            }
	    }
    }

    @Override
    public void mouseMoved(MouseEvent e){
        super.mouseMoved(e);
    }

    @Override
    public void draw (Graphics2D g) {
        super.draw(g);



        g.setColor(colorBig);
        g.setFont(fontBig);
        g.drawString(pfeileLabel, fontBigPosition.x, fontBigPosition.y);
        g.setColor(colorMiddle);
        g.setFont(fontMiddle);
        g.drawString(strategyGameLabel, fontMiddlePosition.x, fontMiddlePosition.y);
        g.setColor(colorSmall);
        g.setFont(fontSmall);
        g.drawString(authorsLabel, fontSmallPosition.x, fontSmallPosition.y);

        g.setFont(Component.STD_FONT);

        list2.draw(g);
        multilineLabel.draw(g);

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
        boxPlayer.draw(g);
        boxOpponent.draw(g);
        confirmButton.draw(g);
        standardButton.draw(g);
        readyButton.draw(g);
        confirmDialog.draw(g);

        reloadCombobox.draw(g);
        test.draw(g);

    }
}
