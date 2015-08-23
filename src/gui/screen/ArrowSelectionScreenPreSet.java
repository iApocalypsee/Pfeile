package gui.screen;

import comp.*;
import comp.Button;
import comp.Label;
import comp.List;
import general.LogFacility;
import general.Main;
import general.PfeileContext;
import general.io.FontLoader;
import newent.Player;
import player.weapon.arrow.*;

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

    private Label remainingArrows, playerName;
    private Button readyButton, randomButton;

    /** Liste der Button f�r andere Aufgaben */
    Button [] buttonListArrows = new Button[8];
    private List arrowListSelected;
    private ConfirmDialog confirmDialog;
    public LinkedList<String> selectedArrows;

	private static ArrowSelectionScreenPreSet instance = null;

	public static ArrowSelectionScreenPreSet getInstance() {
		if(instance == null) {
			instance = new ArrowSelectionScreenPreSet();
		}
		return instance;
	}

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

    /** Font for "Josip Palavra und Daniel Schmaus" */
    private Font fontSmall;

    /** Color for "Josip Palavra und Daniel Schmaus" */
    private Color colorSmall;

    /** position of <code>g.drawString("von Josip Palavra und Daniel Schmaus", fontSmallPosition.x, fontSmallPosition.y")</code> */
    private Point fontSmallPosition;

    /** The player currently selecting his set of arrows. */
    private Player activePlayer;

    /**
     * Screen für die Pfeilauswahl für vorhersetzbaren Pfeilen.
     * äquivalent zu <code> ArrowSelection </code>.
     */
    private ArrowSelectionScreenPreSet() {
        super(SCREEN_NAME, SCREEN_INDEX);

        //setBackground(new SolidColor(TRANSPARENT_BACKGROUND));

        selectedArrows = new LinkedList<>();
        selectedArrows.add("<keine Pfeile>");

        arrowListSelected = new List(50, 200, 200, 350, this, selectedArrows);
        arrowListSelected.setName("arrowListSelected");

        remainingArrows = new Label(Main.getWindowWidth() - 232, Main.getWindowHeight() - 200, this, "Verfügbare Pfeile definieren!");

        remainingArrows.setDeclineInputColor(new Color(202, 199, 246));

        colorBig = new Color (159, 30, 29);
        colorMiddle = new Color (213, 191, 131);
        colorSmall = new Color (205, 212, 228, 50);

        fontBig = FontLoader.loadFont("Augusta", 140, Font.BOLD, FontLoader.FontType.TTF);
        fontMiddle = FontLoader.loadFont("ShadowedGermanica", 45, FontLoader.FontType.TTF);
        fontSmall = FontLoader.loadFont("Berylium", 20, Font.ITALIC, FontLoader.FontType.TTF);

        // fontBigPosition.x = PreWindowScreen.fontBigPosition.x --> if you change the value there you have to change it here, too.
        fontBigPosition = new Point(780, comp.Component.getTextBounds("Pfeile", fontBig).height + 65);
        fontMiddlePosition = new Point(fontBigPosition.x + 43, fontBigPosition.y + comp.Component.getTextBounds("ein Strategiespiel", fontMiddle).height + 15);
        fontSmallPosition = new Point(fontMiddlePosition.x,
                fontMiddlePosition.y + comp.Component.getTextBounds("von Josip Palavra und Daniel Schmaus", fontSmall).height + 10);


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

        // resizing for higher resolutions, if necessary
        for (Button button : buttonListArrows) {
            button.setWidth(button.getWidth() * Main.getWindowWidth() / 1366);
            button.setX(button.getX() * Main.getWindowWidth() / 1366);
        }

        buttonListArrows [0].iconify(ArrowHelper.getArrowImage(FireArrow.INDEX, 0.8f));
        buttonListArrows [1].iconify(ArrowHelper.getArrowImage(WaterArrow.INDEX, 0.8f));
        buttonListArrows [2].iconify(ArrowHelper.getArrowImage(StormArrow.INDEX, 0.8f));
        buttonListArrows [3].iconify(ArrowHelper.getArrowImage(StoneArrow.INDEX, 0.8f));
        buttonListArrows [4].iconify(ArrowHelper.getArrowImage(IceArrow.INDEX, 0.8f));
        buttonListArrows [5].iconify(ArrowHelper.getArrowImage(LightningArrow.INDEX, 0.8f));
        buttonListArrows [6].iconify(ArrowHelper.getArrowImage(LightArrow.INDEX, 0.8f));
        buttonListArrows [7].iconify(ArrowHelper.getArrowImage(ShadowArrow.INDEX, 0.8f));

        for (Button button : buttonListArrows) {
            button.setWidth(buttonListArrows[7].getWidth() + 14);
            button.addMouseListener(new ButtonHelper());
        }

        playerName = new Label(40, Main.getWindowHeight() - 85, this, Main.getUser().getUsername());
        playerName.setFont(new Font(comp.Component.STD_FONT.getFontName(), Font.BOLD, 40));
        playerName.setFontColor(new Color(206, 3, 255));

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
                if (readyButton.getPreciseRectangle().contains(e.getPoint())) {
                    triggerReadyButton();
                }
            }
        });

        randomButton = new Button (readyButton.getX(), readyButton.getY() - 200, this, "Zufälliger Pfeil");
        randomButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                if (randomButton.getPreciseRectangle().contains(e.getPoint())) {
                    triggerRandomButton();
                }
            }
        });

        // Mouselistener f�r 'arrowListSelected'
        arrowListSelected.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed (MouseEvent eClicked) {
                if (arrowListSelected.getPreciseRectangle().contains(eClicked.getPoint())
                            && arrowListSelected.isAcceptingInput()) {
                    arrowListSelected.triggerListeners(eClicked);
                    selectedArrows.remove(arrowListSelected.getSelectedIndex());
                    if (selectedArrows.isEmpty()) {
                        selectedArrows.add("<keine Pfeile>");
                        setArrowListSelected(selectedArrows);

                        if (PfeileContext.arrowNumberPreSet().get() > 1)
                            remainingArrows.setText("Verfügbare Pfeile auswählen!");
                        else
                            remainingArrows.setText("Verfügbaren Pfeil auswählen!");

                    } else {
                        setArrowListSelected(selectedArrows);
                        remainingArrows.setText("Übrige Pfeile: "
                                + (PfeileContext.arrowNumberPreSet().get() - selectedArrows.size()));
                    }
                }
            }
        });

        onScreenEnter.registerJava(this :: resetArrowList);
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

    private void resetArrowList () {
        selectedArrows.clear();
        selectedArrows.add("<keine Pfeile>");
        setArrowListSelected(selectedArrows);

        if (PfeileContext.arrowNumberPreSet().get() > 1)
            remainingArrows.setText("Verfügbare Pfeile auswählen!");
        else
            remainingArrows.setText("Verfügbaren Pfeil auswählen!");
    }

    private class ButtonHelper extends MouseAdapter {
        @Override
        public void mouseReleased (MouseEvent e) {
            for (Button buttonListArrow : buttonListArrows) {
                if (buttonListArrow.getPreciseRectangle().contains(e.getPoint())) {
                    if (PfeileContext.arrowNumberPreSet().get() > selectedArrows.size()) {
                        if (selectedArrows.get(0).equals("<keine Pfeile>")) {
                            selectedArrows.remove(0);
                        }
                        selectedArrows.add(buttonListArrow.getText());
                        remainingArrows.setText("Übrige Pfeile: " + (PfeileContext.arrowNumberPreSet().get() - selectedArrows.size()));
                        setArrowListSelected(selectedArrows);
                    }
                }
            }
        }
    }

    /** this will execute all effects the readyButton or pressing at "" will have */
    private void triggerReadyButton () {
        if (selectedArrows.size() > PfeileContext.arrowNumberPreSet().get()) {
            throw new IllegalStateException("To many arrows added: They can't be more than "
                    + PfeileContext.arrowNumberPreSet().get());
        }

        if (selectedArrows.size() < PfeileContext.arrowNumberPreSet().get()) {
            openConfirmQuestion("Bitten wählen sie alle Pfeile aus!");
        } else {
            if (LoadingWorldScreen.hasLoaded()) {
                // the first player should have the name Main.getUser().getUsername(). Compare with the initialization at
                // ContextCreator#PopulatorStage
                doAddingArrows();

                if (playerName.getText().equals(Main.getUser().getUsername())) {
                    // only after the arrows are added...
                    java.util.List<Player> commandTeamHeads = Main.getContext().getTurnSystem().getHeadOfCommandTeams();
                    commandTeamHeads.forEach((player) -> {
                        if (player.name().equals("Opponent")) {
                            setActivePlayer(player);
                        }
                    });
                } else if (playerName.getText().equals("Opponent")) {
                    onLeavingScreen(GameScreen.SCREEN_INDEX);
                } else {
                    throw new RuntimeException("Unknown name of activePlayer" + playerName.getText() + "; registered  Player: " + activePlayer);
                }
            } else {
                LoadingWorldScreen.getInstance().setAddingArrowList(playerName.getText(), selectedArrows);
                if (playerName.getText().equals(Main.getUser().getUsername())) {
                    // manually switching players...
                    playerName.setText("Opponent");
                    resetArrowList();

                } else if (playerName.getText().equals("Opponent")) {
                    onLeavingScreen(LoadingWorldScreen.getInstance().SCREEN_INDEX);
                } else {
                    throw new RuntimeException("Unknown name of activePlayer" + playerName.getText() + "; registered  Player: " + activePlayer);
                }
            }
        }
    }

    /** the button randomButton is executed (also pressing "r"). A randomly selected Arrow is added to the inventory. */
    private void triggerRandomButton () {
        java.util.Random randomGen = new Random();
        String arrow = ArrowHelper.arrowIndexToName(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES));

        if (PfeileContext.arrowNumberPreSet().get() > selectedArrows.size()) {
            if (selectedArrows.get(0).equals("<keine Pfeile>")) {
                selectedArrows.remove(0);
            }
            selectedArrows.add(arrow);
            remainingArrows.setText("Übrige Pfeile: " + (PfeileContext.arrowNumberPreSet().get() - selectedArrows.size()));
            setArrowListSelected(selectedArrows);
        }
    }

    /** Changes the player and the GUI */
    public void setActivePlayer (Player activePlayer) {
        this.activePlayer = activePlayer;
        activePlayerChanged();
    }

    /** If the activePlayer changes, call this method, to change the GUI */
    private void activePlayerChanged () {
        playerName.setText(activePlayer.name());
        resetArrowList();
    }

    private void doAddingArrows () {
        doAddingArrows(activePlayer);
    }

    /**
     * Puts all selected arrows from <code>ArrowSelectionScreenPreSet.getInstance()</code> to the inventory of the
     * Player by calling {@link player.weapon.Weapon#equip()}.
     */
    private void doAddingArrows (Player player) {
        for (String selectedArrow : selectedArrows) {
            if (!ArrowHelper.instanceArrow(selectedArrow).equip(player))
                LogFacility.log("Cannot add " + selectedArrow + " at " + LogFacility.getCurrentMethodLocation(), LogFacility.LoggingLevel.Error);
        }
    }

    @Override
    public void keyPressed (KeyEvent e) {
        super.keyPressed(e);

        if (e.getKeyCode() == KeyEvent.VK_B) {
            triggerReadyButton();
        } else if (e.getKeyCode() == KeyEvent.VK_Z) {
            triggerRandomButton();
        }
        // by pressing "KeyEvent.VK_SPACE" all arrows are added randomly
        else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            java.util.Random randomGen = new Random();
            if (selectedArrows.get(0).equals("<keine Pfeile>")) {
                selectedArrows.remove(0);
            }
            while (selectedArrows.size() < PfeileContext.arrowNumberPreSet().get()) {
                selectedArrows.add(ArrowHelper.arrowIndexToName(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
            }
            remainingArrows.setText("Übrige Pfeile: " + (PfeileContext.arrowNumberPreSet().get() - selectedArrows.size()));
            setArrowListSelected(selectedArrows);
        }
    }

    @Override
    public void draw(Graphics2D g) {

        super.draw(g);
        // drawing the background and the "Pfeile"-slogan

        g.setColor(colorBig);
        g.setFont(fontBig);
        g.drawString("Pfeile", fontBigPosition.x, fontBigPosition.y);
        g.setColor(colorMiddle);
        g.setFont(fontMiddle);
        g.drawString("ein Strategiespiel", fontMiddlePosition.x, fontMiddlePosition.y);
        g.setColor(colorSmall);
        g.setFont(fontSmall);
        g.drawString("von Josip Palavra und Daniel Schmaus", fontSmallPosition.x, fontSmallPosition.y);


        // resetting the font and draw the rest
        g.setFont(comp.Component.STD_FONT);
        for(Button arrowButton : buttonListArrows) {
            arrowButton.draw(g);
        }
        playerName.draw(g);
        arrowListSelected.draw(g);
        randomButton.draw(g);
        readyButton.draw(g);
        remainingArrows.draw(g);
        confirmDialog.draw(g);

    }
}
