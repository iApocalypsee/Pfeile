package gui.screen;

import comp.Button;
import comp.Component;
import comp.*;
import comp.Label;
import general.LogFacility;
import general.Main;
import misc.ImageHelper;
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
            continueImage = ImageHelper.scaleBufferedImage(continueImage, 0.82f);
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

        // The continueButton should be on place as the endTurnButton in GameScreen. It's easier to click in that way...
        continueButton = new Button(30, Main.getWindowHeight() - 50, this, Main.tr("next"));
        continueButton.setRoundBorder(true);
        continueButton.iconify(continueImage);
        // It should also have the same size.
        continueButton.setWidth(108);
        continueButton.setHeight(40);
        continueButton.setVisible(true);
        continueButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                onLeavingScreen(GameScreen.SCREEN_INDEX);
            }
        });

        label = new Label(0, 85, this, Main.tr("waitingForNextPlayer"));
        label.setFont(new Font(Component.STD_FONT.getFontName(), Font.ITALIC, 28));
        label.setFontColor(new Color(163, 139, 255, 214));

        WaitCircle waitCircle = new WaitCircle(75, new Color(132, 0, 255, 125));
        waitCircle.setAnglePerDrawing(2.5);
        circle = new ImageLikeComponent(33, label.getY() - (int) (0.55 * label.getHeight()), waitCircle, this);
        circle.setVisible(true);

        label.setX(circle.getX() + circle.getWidth() + 17);

        onScreenEnter.registerJava(() -> {
            Team nextTeam = Main.getContext().getTurnSystem().peekNext();
            if (!nextTeam.isBarbarian()) {
                label.setText(Main.tr("waitingForPlayer", nextTeam.asCommandTeam().getHead().name()));
            } else {
                label.setText(Main.tr("waitingForNextPlayer"));
            }
            label.setX(circle.getX() + circle.getWidth() + 17);
        });
    }

    @Override
    public void keyPressed (KeyEvent event) {
        int code = event.getKeyCode();
        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_W || code == KeyEvent.VK_SPACE) {
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
