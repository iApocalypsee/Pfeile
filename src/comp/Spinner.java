package comp;

import gui.Screen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
                    getResourceAsStream("resources/gfx/comp/Spinner_downButton.png"));
            img_upButton = ImageIO.read(Spinner.class.getClassLoader().
                    getResourceAsStream("resources/gfx/comp/Spinner_upButton.png"));
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
        super(x, y, 90, 50, backingScreen);

        spinnerModel = spinnerNumberModel;
        if (Math.abs(spinnerModel.getMinimum()) < Math.abs(spinnerModel.getMaximum()))
            valueBox = new TextBox(x + 1, y + 1, String.valueOf(spinnerModel.getMaximum()), backingScreen);
        else
            valueBox = new TextBox(x + 1, y + 1, String.valueOf(spinnerModel.getMinimum()), backingScreen);
        valueBox.setHeight(img_downButton.getHeight() + img_upButton.getHeight() + 1);

        upButton = new Rectangle(valueBox.getX() + valueBox.getWidth() + 1, y + 2,
                img_upButton.getWidth() - 1, img_upButton.getHeight() - 1);
        downButton = new Rectangle(upButton.x, valueBox.getY() + upButton.height,
                img_downButton.getWidth() - 1, img_downButton.getHeight() - 1);

        setWidth(valueBox.getWidth() + img_downButton.getWidth() + 6);
        setHeight(valueBox.getHeight() + 2);

        upButton.x = valueBox.getX() + valueBox.getWidth() + 1;
        downButton.x = upButton.x;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                if (isAcceptingInput()) {
                    if (downButton.contains(e.getPoint())) {
                        spinnerModel.setValue(spinnerModel.getPreviousValue());
                    } else if (upButton.contains(e.getPoint()))
                        spinnerModel.setValue(spinnerModel.getNextValue());
                    valueBox.setEnteredText(String.valueOf(spinnerModel.getValue()));
                }
            }
        });

        // TODO: add Listener for valueBox
        // the listener needs to recognize the entered number,
        // and set spinnerModel.setValue(String.valueOf(valueBox.getEnteredText());
    }

    @Override
    public void setX (int x) {
        super.setX(x);
        valueBox.setX(x);
        upButton.x = x + valueBox.getWidth();
        downButton.x = upButton.x;
    }

    @Override
    public void setAbsoluteX (int x) {
        super.setAbsoluteX(x);
        valueBox.setX(x);
        upButton.x = x + valueBox.getWidth();
        downButton.x = upButton.x;
    }

    @Override
    public void setAbsoluteY (int y) {
        super.setAbsoluteY(y);
        valueBox.setY(y);
        upButton.y = y;
        downButton.y = y + upButton.height;
    }

    @Override
    public void setY (int y) {
        super.setY(y);
        valueBox.setY(y);
        upButton.y = y;
        downButton.y = y + upButton.height;
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
            getBorder().draw(g);
            g.setFont(STD_FONT);
            valueBox.draw(g);
            g.drawImage(img_downButton, downButton.x, downButton.y, downButton.width, downButton.height, null);
            g.drawImage(img_upButton, upButton.x, upButton.y, upButton.width, upButton.height, null);
        }
    }
}
