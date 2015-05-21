package gui.screen;

import comp.Button;
import comp.Component;
import comp.Label;
import general.Main;
import newent.Team;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This is the Screen, you're going to wait for your foe.
 */
public class WaitingScreen extends Screen {

    public static final String SCREEN_NAME = "Waiting Screen";
    public static final int SCREEN_INDEX = 70;

    private Button continueButton;
    private Label label;

    public WaitingScreen () {
        super(SCREEN_NAME, SCREEN_INDEX);

        continueButton = new Button(Main.getWindowWidth() - 200, Main.getWindowHeight() - 300, this, "Weiter");
        continueButton.setRoundBorder(true);
        continueButton.setVisible(true);
        continueButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                onLeavingScreen(GameScreen.SCREEN_INDEX);
            }
        });

        label = new Label(80, 80, this, "Warte auf nächsten Spieler:... ");
        label.setFont(new Font(Component.STD_FONT.getFontName(), Font.ITALIC, 28));
        label.setFontColor(new Color(108, 63, 255, 140));

        onScreenEnter.registerJava(() -> {
            Team nextTeam = Main.getContext().getTurnSystem().peekNext();
            if (!nextTeam.isBarbarian())
                label.setText("Warte auf nächsten Spieler: " + nextTeam.asCommandTeam().getHead().name());
        });
    }

    @Override
    public void keyPressed (KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_ENTER || event.getKeyCode() == KeyEvent.VK_W) {
            onLeavingScreen(GameScreen.SCREEN_INDEX);
        }
    }

    @Override
    public void draw (Graphics2D g) {
        super.draw(g);

        continueButton.draw(g);
        label.draw(g);
    }
}
