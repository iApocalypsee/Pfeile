package gui.screen;

import comp.Button;
import comp.ConfirmDialog;
import comp.Label;
import comp.List;
import general.GameWindow;
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
import java.util.stream.Collectors;

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

    /** Array for the arrow buttons */
    private Button [] buttonListArrows = new Button[8];
    private List arrowListSelected;
    private ConfirmDialog confirmDialog;
    public LinkedList<AbstractArrow> selectedArrows;

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

    private final String chooseVarArrows, chooseOneArrow, chooseFirstLastArrow, noMoreArrows, authorsLabel, strategyGameLabel;

    /**
     * Screen für die Pfeilauswahl für vorhersetzbaren Pfeilen.
     * äquivalent zu <code> ArrowSelection </code>.
     */
    private ArrowSelectionScreenPreSet() {
        super(SCREEN_NAME, SCREEN_INDEX);

        chooseVarArrows = Main.tr("defineArrows");
        chooseOneArrow = Main.tr("defineLastArrow");
        chooseFirstLastArrow = Main.tr("defineFirstLastArrow");
        noMoreArrows = Main.tr("defineNoMoreArrows");
        strategyGameLabel = Main.tr("label_strategyGame");
        authorsLabel = Main.tr("label_authors");

        final String randomArrow = Main.tr("randomArrow"),
                     confirm = Main.tr("confirm");

        selectedArrows = new LinkedList<>();

        arrowListSelected = new List(50, 200, 200, 350, this, selectedArrows.stream()
                .map(AbstractArrow::getNameDisplayed).collect(Collectors.toList()));
        arrowListSelected.setName("arrowListSelected");

        remainingArrows = new Label(GameWindow.WIDTH - 232, GameWindow.HEIGHT - 200, this, chooseVarArrows);

        remainingArrows.setDeclineInputColor(new Color(202, 199, 246));

        colorBig = new Color (159, 30, 29);
        colorMiddle = new Color (213, 191, 131);
        colorSmall = new Color (205, 212, 228, 50);

        fontBig = FontLoader.loadFont("Augusta", 140, Font.BOLD, FontLoader.FontType.TTF);
        fontMiddle = FontLoader.loadFont("ShadowedGermanica", 45, FontLoader.FontType.TTF);
        fontSmall = FontLoader.loadFont("Berylium", 20, Font.ITALIC, FontLoader.FontType.TTF);

        // fontBigPosition.x = PreWindowScreen.fontBigPosition.x --> if you change the value there you have to change it here, too.
        fontBigPosition = new Point(780, comp.Component.getTextBounds("Pfeile", fontBig).height + 65);
        fontMiddlePosition = new Point(fontBigPosition.x + 43, fontBigPosition.y + comp.Component.getTextBounds(strategyGameLabel, fontMiddle).height + 15);
        fontSmallPosition = new Point(fontMiddlePosition.x,
                fontMiddlePosition.y + comp.Component.getTextBounds(authorsLabel, fontSmall).height + 10);


        // Compare the following initialisation process with ArrowSelectionScreen, since the design is the same.

        // y position of the first arrow button
        int posYButtons = 60;
        // x position of the first arrow button
        int posXButton = 38;
        // distance between two arrow buttons next to each other
        int gap = 45;

        buttonListArrows[0] = new Button(posXButton, posYButtons, ArrowHelper.getArrowImage(FireArrow.INDEX, 0.8f),
                ArrowSelectionScreenPreSet.this, Main.tr("FireArrow"));
        buttonListArrows[1] = new Button(posXButton + buttonListArrows[0].getWidth() + gap, posYButtons, ArrowHelper.getArrowImage(WaterArrow.INDEX, 0.8f),
                ArrowSelectionScreenPreSet.this, Main.tr("WaterArrow"));
        buttonListArrows[2] = new Button(posXButton + (buttonListArrows[0].getWidth() + gap) * 2, posYButtons, ArrowHelper.getArrowImage(StormArrow.INDEX, 0.8f),
                ArrowSelectionScreenPreSet.this, Main.tr("StormArrow"));
        buttonListArrows[3] = new Button(posXButton + (buttonListArrows[0].getWidth() + gap) * 3, posYButtons, ArrowHelper.getArrowImage(StoneArrow.INDEX, 0.8f),
                ArrowSelectionScreenPreSet.this, Main.tr("StoneArrow"));
        buttonListArrows[4] = new Button(posXButton + (buttonListArrows[0].getWidth() + gap) * 4, posYButtons, ArrowHelper.getArrowImage(IceArrow.INDEX, 0.8f),
                ArrowSelectionScreenPreSet.this, Main.tr("IceArrow"));
        buttonListArrows[5] = new Button(posXButton + (buttonListArrows[0].getWidth() + gap) * 5, posYButtons, ArrowHelper.getArrowImage(LightningArrow.INDEX, 0.8f),
                ArrowSelectionScreenPreSet.this, Main.tr("LightningArrow"));
        buttonListArrows[6] = new Button(posXButton + (buttonListArrows[0].getWidth() + gap) * 6 , posYButtons, ArrowHelper.getArrowImage(LightArrow.INDEX, 0.8f),
                ArrowSelectionScreenPreSet.this, Main.tr("LightArrow"));
        buttonListArrows[7] = new Button(posXButton + (buttonListArrows[0].getWidth() + gap) * 7, posYButtons, ArrowHelper.getArrowImage(ShadowArrow.INDEX, 0.8f),
                ArrowSelectionScreenPreSet.this, Main.tr("ShadowArrow"));

        // resizing for higher resolutions, if necessary. The Resolution changes with mini-screens as well, but the Strings of the names can't be read probably.
        if (GameWindow.WIDTH != 1366) {
            for (Button button : buttonListArrows) {
                button.setWidth(button.getWidth() * GameWindow.WIDTH / 1366);
                button.setX(button.getX() * GameWindow.WIDTH / 1366);
            }
        }

        ButtonHelper buttonHelper = new ButtonHelper();
        for (Button button : buttonListArrows) {
            //button.setWidth(buttonListArrows[7].getWidth() + 14);
            button.addMouseListener(buttonHelper);
        }

        playerName = new Label(40, GameWindow.HEIGHT - 85, this, Main.getUser().getUsername());
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
        readyButton = new Button(GameWindow.WIDTH - 220, GameWindow.HEIGHT - 150, this, confirm);
        readyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (readyButton.getPreciseRectangle().contains(e.getPoint())) {
                    triggerReadyButton();
                }
            }
        });

        randomButton = new Button (readyButton.getX(), readyButton.getY() - 200, this, randomArrow);
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
                    arrowListSelected.removeListEntry(arrowListSelected.getSelectedIndex());
                    remainingArrows.setText(getRemainingArrowsString());
                }
            }
        });

        onScreenEnter.registerJava(this :: resetArrowList);
    }

    private int getRemainingArrows() {
        return PfeileContext.arrowNumberPreSet().get() - selectedArrows.size();
    }

    private String getRemainingArrowsString() {
        if(getRemainingArrows() > 1)
            return String.format(chooseVarArrows, getRemainingArrows());
        else if(getRemainingArrows() == 1 && PfeileContext.arrowNumberPreSet().get() == 1)
            return chooseFirstLastArrow;
        else if(getRemainingArrows() == 1)
            return chooseOneArrow;
        else return noMoreArrows;
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
        arrowListSelected.removeAllListEntries();
        remainingArrows.setText(getRemainingArrowsString());
    }

    private class ButtonHelper extends MouseAdapter {
        @Override
        public void mouseReleased (MouseEvent e) {
            for (int i = 0; i < ArrowHelper.NUMBER_OF_ARROW_TYPES; ++i) {
               if (buttonListArrows[i].getPreciseRectangle().contains(e.getPoint())) {
                   selectArrow(ArrowHelper.instanceArrow(i));
                   return;
               }
            }
        }
    }

    private void selectArrow(AbstractArrow a) {
        if (PfeileContext.arrowNumberPreSet().get() > selectedArrows.size()) {
            selectedArrows.add(a);
            arrowListSelected.appendListEntry(a.getNameDisplayed());
            remainingArrows.setText(getRemainingArrowsString());
        }
    }

    /** this will execute all effects the readyButton or pressing at "" will have */
    private void triggerReadyButton () {
        if (selectedArrows.size() > PfeileContext.arrowNumberPreSet().get()) {
            throw new IllegalStateException("To many arrows added: They can't be more than "
                    + PfeileContext.arrowNumberPreSet().get());
        }

        if (selectedArrows.size() < PfeileContext.arrowNumberPreSet().get()) {
            if (Main.isEnglish())
                openConfirmQuestion("Please, select all arrows!");
            else
                openConfirmQuestion("Bitte wählen sie alle Pfeile aus!");
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
        selectArrow(ArrowHelper.instanceArrow(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
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
        selectedArrows.forEach(a -> {
            if (!a.equip(player))
                LogFacility.log("Cannot add " + a.getNameDisplayed(), LogFacility.LoggingLevel.Error);
        });
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
            while (selectedArrows.size() < PfeileContext.arrowNumberPreSet().get()) {
                selectArrow(ArrowHelper.instanceArrow(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
            }
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
        g.drawString(strategyGameLabel, fontMiddlePosition.x, fontMiddlePosition.y);
        g.setColor(colorSmall);
        g.setFont(fontSmall);
        g.drawString(authorsLabel, fontSmallPosition.x, fontSmallPosition.y);


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
