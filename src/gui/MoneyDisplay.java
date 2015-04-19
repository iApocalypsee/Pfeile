package gui;

import comp.Component;
import general.LogFacility;
import general.Main;
import general.PfeileContext;
import gui.screen.Screen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A small Place, where the money can be seen.
 */
public class MoneyDisplay extends Component {

    private static BufferedImage image;

    private String money;

    static {
        String path = "resources/gfx/comp/moneyDisplay.png";
        try {
            image = ImageIO.read(MoneyDisplay.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class MoneyDisplay couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    public MoneyDisplay (int x, int y, Screen backing) {
        super(x, y, 140, 50, backing);
        getBorder().setOuterColor(new Color(107, 0, 140));
        getBorder().setInnerColor(new Color(228, 238, 236));
        getBorder().setClickColor(new Color(175, 0, 235));
        getBorder().setStroke(new BasicStroke(3.2f));
        PfeileContext context = Main.getContext();

        money = "0";

        /*
        TODO: during initialization this causes a NullPointerException because of activePlayer

        context.getActivePlayer().onMoneyChanged().registerJava(() ->
                money = "" + context.getActivePlayer().getPurse().numericValue());

        context.getTurnSystem().onTurnGet().registerJava(team ->
                money = "" + context.getActivePlayer().getPurse().numericValue());
        */
    }


    @Override
    public void draw (Graphics2D g) {
        getBorder().draw(g);
        g.drawImage(image, getX() + 5, getY() + 7, null);

        if (getStatus() != ComponentStatus.CLICK)
            g.setColor(Color.BLACK);

        g.drawString(money, getX() + image.getWidth() + 10, getY() + 27);
    }
}
