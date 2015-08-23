package gui;

import comp.Component;
import general.LogFacility;
import general.PfeileContext;
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

    private volatile String money;

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
        setMoney(data.getPurse().numericValue());
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

    private void setMoney(int numericValue) {
        money = Integer.toString(numericValue);
    }

    /**
     * <b>only called once by ContextCreator#ApplyingOtherStuffStage</b>
     * <n>This call adds to each player's delegate <code>onMoneyChanged</code> a function which 
     * changes the String of MoneyDisplay. </n>
     */
    public void initializeDataActualization (PfeileContext context) {
        context.getTurnSystem().getHeadOfCommandTeams().forEach((player) -> {
            // A thread is way too overkill for such a tiny computation.
            player.onMoneyChanged().registerJava(() -> setMoney(player.getPurse().numericValue()));
        });
    }

    @Override
    public void draw (Graphics2D g) {
        getBorder().draw(g);
        g.drawImage(image, getX() + 5, getY() + 7, null);

        g.setFont(font);
        if (getStatus() != ComponentStatus.CLICK)
            g.setColor(Color.BLACK);

        g.drawString(money, getX() + image.getWidth() + 10, getY() + 27);
    }
}
