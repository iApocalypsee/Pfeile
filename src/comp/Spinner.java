package comp;

import gui.Screen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Spinner
 */
public class Spinner extends Component {

    private SpinnerModel spinnerModel;
    private Rectangle downButton, upButton;
    private TextBox valueBox;

    private static BufferedImage img_downButton, img_upButton;
    static {
        try {
            img_downButton = ImageIO.read(Spinner.class.getClassLoader().
                    getResourceAsStream("resources/gfx/comp/Spinner_downButton"));
            img_upButton = ImageIO.read(Spinner.class.getClassLoader().
                    getResourceAsStream("resources/gfx/comp/Spinner_upButton"));
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** this constructs a Spinner with automatically chosen width and height and with
     *
     * @param x the x position on the screen
     * @param y the y position on the screen
     * @param backingScreen the Screen the Spinner will be shown (usually this)
     * @param spinnerNumberModel the spinnerModel this Spinner will work with
     */
    public Spinner (int x, int y, Screen backingScreen, SpinnerModel spinnerNumberModel) {
        super(x, y, 50, 50, backingScreen);

        spinnerModel = spinnerNumberModel;
        valueBox = new TextBox(x, y, String.valueOf(spinnerModel.getValue()), backingScreen);
        upButton = new Rectangle(x + valueBox.getWidth(), y, img_upButton.getWidth(), img_upButton.getHeight());
        downButton = new Rectangle(upButton.x, y + upButton.height, img_downButton.getWidth(), img_downButton.getHeight());
        valueBox.setHeight(upButton.height + downButton.height);

        setWidth(valueBox.getWidth() + upButton.width);
        setHeight(valueBox.getHeight());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                if (isVisible() == true && isAcceptingInput() == true) {
                    if (downButton.contains(e.getPoint())) {
                        spinnerModel.setValue(spinnerModel.getPreviousValue());
                    } else if (upButton.contains(e.getPoint()))
                        spinnerModel.setValue(spinnerModel.getNextValue());
                    valueBox.setEnteredText(String.valueOf(spinnerModel.getValue()));
                }
            }
        });
    }

    @Override
    public void setX (int x) {
        super.setX(x);
        valueBox.setX(x);
        upButton.x = x;
        downButton.x = x;
    }

    @Override
    public void setAbsoluteX (int x) {
        super.setAbsoluteX(x);
        valueBox.setX(x);
        upButton.x = x;
        downButton.x = x;
    }

    @Override
    public void setAbsoluteY (int y) {
        super.setAbsoluteY(y);
        valueBox.setY(y);
        upButton.y = y;
        downButton.y = y;
    }

    @Override
    public void setY (int y) {
        super.setY(y);
        valueBox.setY(y);
        upButton.y = y;
        downButton.y = y;
    }

    @Override
    public void setWidth (int width) {
        if (width < upButton.width)
            width = upButton.width;
        super.setWidth(width);
        valueBox.setWidth(width - upButton.width);
    }

    @Override
    public void acceptInput () {
        super.acceptInput();
        valueBox.acceptInput();
    }

    @Override
    public void declineInput () {
        super.declineInput();
        valueBox.declineInput();
    }

    @Override
    public void setVisible (boolean vvvvvv) {
        super.setVisible(vvvvvv);
        if (vvvvvv == true)
            valueBox.acceptInput();
        else
            valueBox.declineInput();
    }


    /** returns the SpinnerModel */
    public SpinnerModel getSpinnerModel () {
        return spinnerModel;
    }

    /** the old spinnerModel will be replaced by the new <code>spinnerModel</code>.
     *  the value, which could be seen in the textBox changes to the currentValue of the <code>spinnerModel</code>*/
    public void setSpinnerModel (SpinnerModel spinnerModel) {
        this.spinnerModel = spinnerModel;
        valueBox.setEnteredText(String.valueOf(spinnerModel.getValue()));
        valueBox.setStdText(String.valueOf(spinnerModel.getValue()));
    }


    @Override
    public void draw (Graphics2D g) {
        if (isVisible()) {
            valueBox.draw(g);
            g.drawImage(img_downButton, downButton.x, downButton.y, downButton.width, downButton.height, null);
            g.drawImage(img_upButton, upButton.x, upButton.y, upButton.width, upButton.height, null);
        }
    }
}
