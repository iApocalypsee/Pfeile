package gui;

import comp.*;
import comp.Button;
import comp.Label;
import general.Main;
import general.Mechanics;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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

    // TODO: Spinner is not implemented yet!
    /** The Spinner for selecting the amount of arrows. */
    //private comp.Spinner spinner;

    /** backgroundColor */
    private static final Color TRANSPARENT_BACKGROUND = new Color(0, 0, 0, 185);

    /** a Clip for playing the title melodie */
    private static Clip backgroundSound;
    static {
        try{
            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(PreWindowScreen.class.getClassLoader().getResourceAsStream("resources/sfx/introBackground.WAV"));
            AudioFormat audioFormat = audioInputStream.getFormat();
            int size = (int) (audioFormat.getFrameSize() * audioInputStream.getFrameLength());
            byte[] audio = new byte[size];
            DataLine.Info info = new DataLine.Info(Clip.class, audioFormat, size);
            audioInputStream.read(audio, 0, size);
            backgroundSound = (Clip) AudioSystem.getLine(info);
            backgroundSound.open(audioFormat, audio, 0, size);
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** This plays the background title melodie of pfeile in an endless loop.
     * It will always start at the beginning after calling <code> playLoop() </code>.
     * To stop it again use <code> stopLoop </code> */
    public void playLoop () {
        backgroundSound.setLoopPoints(0, 1);
        backgroundSound.loop(Integer.MAX_VALUE);
    }
    /** Stops the playing of the endless loop started with <code> playLoop </code>.
     * To Start again use <code> playLoop()</code>.
     */
    public void stopLoop () {
        backgroundSound.stop();
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
        labels[2] = new Label(labelPosX, labelPosY + labels[1].getHeight(), this, "Pfeilanzahl [vorher wählbar]: ");
        labels[3] = new Label(labelPosX, labelPosY + labels[2].getHeight(), this, "Zuganzahl pro Runde: ");
        labels[4] = new Label(labelPosX, labelPosY + labels[3].getHeight(), this, "maximales Leben: ");
        labels[5] = new Label(labelPosX, labelPosY + labels[4].getHeight(), this, "Lebensregeneration: ");
        labels[6] = new Label(labelPosX, labelPosY + labels[5].getHeight(), this, "Schadensmultiplikator: ");
        labels[7] = new Label(labelPosX, labelPosY + labels[6].getHeight(), this, "Zeit pro Runde: ");
        labels[8] = new Label(labelPosX, labelPosY + labels[7].getHeight(), this, "Handicap [Spieler]: ");
        labels[9] = new Label(labelPosX, labelPosY + labels[8].getHeight(), this, "Handicap [Computer]: ");
        labels[10] = new Label(labelPosX, labelPosY + labels[9].getHeight(), this, "Weltgröße: ");
        for (int i = 0; i < labels.length; i++) {
            labels[i].declineInput();
        }

        String[] comboBoxValuesSelector = { "Computerstärke", "Pfeilanzahl [frei wählbar]", "Pfeilanzahl [vorher wählbar]",
                "Zuganzahl pro Runde", "maximales Leben", "Lebensregeneration", "Schadensmultiplikator", "Zeit pro Zug",
                "Handicap", "Weltgröße"};

        selectorComboBox = new ComboBox (labelPosX, 80, this, comboBoxValuesSelector);

        String[] comboBoxValuesHigh = { "hoch", "hoch-mittel", "mittel", "mittel-niedrig", "niedrig" };
        boxSelectHigh = new ComboBox (confirmButton.getX(), selectorComboBox.getY(), this, comboBoxValuesHigh);
        boxSelectHigh.setSelectedIndex(2);
        boxSelectHigh.setVisible(false);
        boxSelectHigh.declineInput();

        String[] comboBoxValuesSize = { "gigantisch", "groß", "normal", "klein", "winzig" };
        boxSelectSize = new ComboBox (confirmButton.getX(), selectorComboBox.getY(), this, comboBoxValuesHigh);
        boxSelectSize.setSelectedIndex(2);
        boxSelectSize.setVisible(false);
        boxSelectSize.declineInput();

        String[] comboBoxValuesHandicap =
                    {"+ 25%", "+ 20%", "+ 15%", "+ 10%", "+ 5%", "0%", "- 5%", "- 10%", "- 15%", "- 20 %", "- 25"};
        boxSelectHandicapPlayer = new ComboBox (confirmButton.getX(), selectorComboBox.getY(), this, comboBoxValuesHandicap);
        boxSelectHandicapKI = new ComboBox (confirmButton.getX(), selectorComboBox.getY() + boxSelectHandicapPlayer.getWidth() + 15, this, comboBoxValuesHandicap);
        boxSelectHandicapPlayer.setSelectedIndex(5);
        boxSelectHandicapKI.setSelectedIndex(5);
        boxSelectHandicapPlayer.setVisible(false);
        boxSelectHandicapKI.setVisible(false);
        boxSelectHandicapPlayer.declineInput();
        boxSelectHandicapKI.declineInput();

        String[] comboBoxValuesKI = { "Brutal", "Stark", "Normal", "Schwach", "Erbärmlich" };
        boxSelectKI = new ComboBox(confirmButton.getX(), selectorComboBox.getY(), this, comboBoxValuesKI);
        boxSelectKI.setSelectedIndex(2);

        String[] comboBoxValuesTime = {"5min", "2 min", "1 min", "40sec",
                "30sec", "20sec"};
        boxSelectTime = new ComboBox(confirmButton.getX(), selectorComboBox.getY(), this, comboBoxValuesTime);
        boxSelectTime.setSelectedIndex(2);
        boxSelectTime.setVisible(false);
        boxSelectTime.declineInput();

        confirmDialog = new ConfirmDialog(500, 500, this, "warningMessage");
        confirmDialog.setX(Main.getWindowWidth() / 2 - confirmDialog.getWidth() / 2);
        confirmDialog.setY(Main.getWindowHeight() / 2 - confirmDialog.getHeight() / 2);
        confirmDialog.setVisible(false);

        // TODO: Initialising the Listner



    }

    /**
     * TODO: Everything, that need to be done
     */
    private void TODO (int width, int height, int type, String heading) {
            //this.initSpinner = new JSpinner();
            //Dimension prefSizeForSpinner = new Dimension(50, 20);
            //this.initSpinner.setPreferredSize(prefSizeForSpinner);
            //this.initSpinner.setVisible(false);
            //confirmButton.addActionListener(new PreWindow.ListenToButton());
            //selectorComboBox.addItemListener(new PreWindow.ListenToItem());
            //boxSelectHigh.addItemListener(new PreWindow.ListenToItem());
            //boxSelectSize.addItemListener(new PreWindow.ListenToItem());
            //boxSelectHandicapPlayer.addItemListener(new ListenToItem());
            //boxSelectHandicapKI.addItemListener(new ListenToItem());
            //boxSelectKI.addItemListener(new PreWindow.ListenToItem());
            //boxSelectTime.addItemListener(new PreWindow.ListenToItem());
            //readyButton.addActionListener(new ListenToButton());
            //standardButton.addActionListener(new ListenToButton());
    }

    /** TODO all these Listener

    /**
     * ACTION_LISTENER FÜR BUTTON ist für alle Button und leider steht alles
     * hier drin, ohne auf mehrere Methoden aufgeteilt zu sein

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

                    // 'SELECTBOXGR' für "WELTENGRÖßE
                    else if (PreWindow.this.initBox.getSelectedIndex() == 8) { // "Weltengröße"

                        if (PreWindow.this.selectBoxGr.getSelectedIndex() == 0) { // "gigantisch"
                            Mechanics.worldSizeX = 23;
                            Mechanics.worldSizeY = 21;
                            isEnteredRight = true;
                        } else if (PreWindow.this.selectBoxGr
                                .getSelectedIndex() == 1) { // "gro�"
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

                        // WERTE M�SSEN NOCH ZUGEWIESEN WERDEN!!!

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

                        if (PreWindow.this.initBox.getSelectedIndex() == 1) { // "Pfeilanzahl [frei wählbar]"
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
                        } else if (PreWindow.this.initBox.getSelectedIndex() == 2) { // "Pfeilanzahl [vorher wählbar]"
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
                        } else if (PreWindow.this.initBox.getSelectedIndex() == 3) { // "Züge pro Runde"
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
                    } else if (PreWindow.this.initBox.getSelectedIndex() == 0) { // "Computerstärke":
                        // 5=Brutal
                        // -->
                        // 1=erbärmlich

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
                            Mechanics.KI = 1; // "erb�rmlich"
                        } else
                            isEnteredRight = false;
                    }

                    // ändert den Text in den Labels und setzt ggf. die
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
                // n�tig eine Warnung aus
                if (Mechanics.KI == -1) {
                    PreWindow.this.warningMessage = ("Select unselected Selections: Computerst�rke");
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
                    PreWindow.this.warningMessage = ("Select unselected Selections: Weltgr��e");
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
                PreWindow.this.label0.setText("Computerst�rke: " + "mittel");

                Mechanics.arrowNumberFreeSet = 5;
                PreWindow.this.label1.setText("Pfeilanzahl [frei w�hlbar]: "
                        + Mechanics.arrowNumberFreeSet);

                Mechanics.arrowNumberPreSet = 10;

                PreWindow.this.label2.setText("Pfeilanzahl [vorher w�hlbar]: "
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
                PreWindow.this.label8.setText("Weltgr��e: " + "normal");

                Mechanics.handicapPlayer = 0;
                PreWindow.this.label9.setText("Handicap [Player]: " + "0%");
                Mechanics.handicapKI = 0;
                PreWindow.this.label10.setText("Handicap [KI]: " + "0%");
            }
        }
    }

    /**
     * ITEM_LISTENER f�r JComboBoxen: stellt die richtige Box, je nach Auswahl
     * in der 'initBox' ein


    class ListenToItem implements ItemListener {

        ListenToItem() {
        }

        public void itemStateChanged(ItemEvent e) {
            if (e.getSource() == PreWindow.this.initBox) {

                // F�r Auswahl Computerst�rke
                if (PreWindow.this.initBox.getSelectedIndex() == 0) {

                    // setzt in diesen Moment unn�tige JComboBoxen auf
                    // setVisible(false)
                    PreWindow.this.selectBox.setVisible(false);
                    PreWindow.this.timeBox.setVisible(false);
                    PreWindow.this.initSpinner.setVisible(false);
                    PreWindow.this.selectBoxGr.setVisible(false);
                    PreWindow.this.handicapSelectionPlayer.setVisible(false);
                    PreWindow.this.handicapSelectionKI.setVisible(false);
                    PreWindow.this.selectBoxKI.setVisible(true);
                }

                // f�r die Auswahlen mit Spinner: Pfeileanzahl und Anzahl der
                // Z�ge pro Runde
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

                // f�r Auswahl mit 'selectBox' ==> Leben, Lebensregeneration und
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

                // f�r Auswahl mit 'timeBox' ==> Zeit pro Zug
                if (this.initBox.getSelectedIndex() == 7) {
                    PreWindow.this.initSpinner.setVisible(false);
                    this.selectBox.setVisible(false);
                    PreWindow.this.selectBoxKI.setVisible(false);
                    PreWindow.this.selectBoxGr.setVisible(false);
                    PreWindow.this.handicapSelectionPlayer.setVisible(false);
                    PreWindow.this.handicapSelectionKI.setVisible(false);
                    PreWindow.this.timeBox.setVisible(true);
                }

                // f�r Auswahl mit 'selectBoxGr' ==> Weltgr��e
                if (PreWindow.this.initBox.getSelectedIndex() == 8) {
                    PreWindow.this.initSpinner.setVisible(false);
                    PreWindow.this.selectBox.setVisible(false);
                    PreWindow.this.selectBoxKI.setVisible(false);
                    PreWindow.this.timeBox.setVisible(false);
                    PreWindow.this.handicapSelectionPlayer.setVisible(false);
                    PreWindow.this.handicapSelectionKI.setVisible(false);
                    PreWindow.this.selectBoxGr.setVisible(true);
                }

                // f�r die Auswahl mit 'handicapBox' ==> Handicap
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
    */

    @Override
    public void draw (Graphics2D g) {
        g.setColor(TRANSPARENT_BACKGROUND);
        g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
        boxSelectKI.draw(g);
        boxSelectHigh.draw(g);
        boxSelectHandicapKI.draw(g);
        boxSelectHandicapPlayer.draw(g);
        boxSelectSize.draw(g);
        boxSelectTime.draw(g);
        for(int i = 0; i < labels.length; i++) {
            labels[i].draw(g);
        }
        selectorComboBox.draw(g);
        confirmButton.draw(g);
        standardButton.draw(g);
        readyButton.draw(g);
        confirmDialog.draw(g);
    }
}
