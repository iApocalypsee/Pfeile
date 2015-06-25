package gui.screen;

import comp.*;
import comp.Button;
import comp.Component;
import comp.Label;
import general.LogFacility;
import general.Main;
import newent.Team;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * This is the Screen, you're going to wait for your foe.
 */
public class WaitingScreen extends Screen {

    public static final String SCREEN_NAME = "Waiting Screen";
    public static final int SCREEN_INDEX = 70;

    private static BufferedImage continueImage;
    static {
        String path = "resources/gfx/comp/continueButton.png";
        try {
            continueImage = ImageIO.read(WaitingScreen.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class WaitingScreen couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    private Button continueButton;
    private Label label;
    private ImageLikeComponent circle;

    public WaitingScreen () {
        super(SCREEN_NAME, SCREEN_INDEX);

        continueButton = new Button(Main.getWindowWidth() - 200, Main.getWindowHeight() - 300, this, "Weiter");
        continueButton.setRoundBorder(true);
        continueButton.iconify(continueImage);
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
            if (!nextTeam.isBarbarian()) {
                label.setText("Warte auf nächsten Spieler: " + nextTeam.asCommandTeam().getHead().name());
            } else {
                label.setText("Warte auf nächsten Spieler:... ");
            }
            circle.setX(label.getX() + label.getWidth() + 20);
            // FIXME: This only changes the rectangle within ImageLikeComponent, but does NOT effect the position used in the draw method
        });

        WaitCircle waitCircle = new WaitCircle(100, new Color(159, 107, 255, 153));
        waitCircle.setAnglePerDrawing(2.5);
        circle = new ImageLikeComponent(label.getX() + label.getWidth() + 20, label.getY() - (int) (0.4 * label.getHeight()), waitCircle, this);
        // FIXME: The x and y position of the circle aren't the same like I coded in the line above.
        circle.setVisible(true);
    }

    @Override
    public void keyPressed (KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_ENTER || event.getKeyCode() == KeyEvent.VK_W) {
            onLeavingScreen(GameScreen.SCREEN_INDEX);
        }
    }

    @Override
    public void draw (Graphics2D g) {
        continueButton.draw(g);
        label.draw(g);
        circle.draw(g);
    }
}
