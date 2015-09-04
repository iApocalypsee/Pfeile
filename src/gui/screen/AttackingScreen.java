package gui.screen;

import comp.Button;
import general.Main;
import newent.AttackProgress;
import player.weapon.AttackDrawer;
import player.weapon.AttackingCalculator;
import player.weapon.arrow.ImpactDrawerHandler;

import java.util.List;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Every attack is handled in this screen.
 */
public class AttackingScreen extends Screen {

    public static final int SCREEN_INDEX = 71;

    public static final String SCREEN_NAME = "AttackingScreen";

    private static AttackingScreen instance = null;

    public static AttackingScreen getInstance() {
        if (instance == null)
            instance = new AttackingScreen();
        return instance;
    }

    private final AttackDrawer attackDrawer = new AttackDrawer();

    public AttackDrawer getAttackDrawer () {
        return attackDrawer;
    }

    private comp.Button continueButton;



    public AttackingScreen () {
        super(SCREEN_NAME, SCREEN_INDEX);
        continueButton = new Button(Main.getWindowWidth() - 100, Main.getWindowHeight() - 50, this, "Weiter");
        continueButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed (MouseEvent e) {
                triggerContinueButton();
            }
        });

        onScreenEnter.registerJava(() -> {
            continueButton.declineInput();

            List<AttackProgress> filteredProgresses = AttackDrawer.getAttackProgressesOfArrows();

            // if there are no attacks... you don't need this screen
            if (filteredProgresses.isEmpty())
                onLeavingScreen(WaitingScreen.SCREEN_INDEX);

            AttackingCalculator.getInstance().arrowsFlying(filteredProgresses);
            continueButton.acceptInput();
        });
    }

    @Override
    public void keyPressed (KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER: triggerContinueButton(); break;
            case KeyEvent.VK_W: triggerContinueButton(); break;
            case KeyEvent.VK_SPACE: triggerContinueButton(); break;
        }
    }

    private void triggerContinueButton () {
        if (continueButton.isAcceptingInput())
            onLeavingScreen(WaitingScreen.SCREEN_INDEX);
    }

    /**
     * Zeichnet den Screen mit dem Graphics2D Objekt.
     *
     * @param g Der Grafikkontext
     */
    @Override
    public void draw (Graphics2D g) {
        super.draw(g);

        GameScreen.getInstance().getMap().draw(g);

        attackDrawer.draw(g);
        ImpactDrawerHandler.draw(g);

        Main.getContext().getActivePlayer().drawLifeUI(g);

        Main.getContext().getTimeClock().draw(g);

        continueButton.draw(g);
    }
}
