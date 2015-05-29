package gui;

import comp.Component;
import general.LogFacility;
import gui.screen.Screen;
import newent.MoneyEarner;

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

    private MoneyEarner data;

    private Font font;

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

        setMoney("0");
        font = new Font(STD_FONT.getFontName(), Font.ITALIC, 14);
    }

    public void retrieveDataFrom(MoneyEarner earner) {
        data = earner;
        setMoney(Integer.toString(data.getPurse().numericValue()));
    }

    public MoneyEarner getData() {
        return data;
    }

    public String getMoneyString() {
        return money;
    }

    private void setMoney(String money) {
        this.money = money;
    }

    boolean detected = false;

    @Override
    public void draw (Graphics2D g) {
        if(!detected) {
            if(money.equals("0")) {
                LogFacility.logCurrentStackTrace();
                LogFacility.log("BUG!", "Warning");
                detected = true;
            }
        }
        getBorder().draw(g);
        g.drawImage(image, getX() + 5, getY() + 7, null);

        g.setFont(font);
        if (getStatus() != ComponentStatus.CLICK)
            g.setColor(Color.BLACK);

        g.drawString(money, getX() + image.getWidth() + 10, getY() + 27);
    }
}
