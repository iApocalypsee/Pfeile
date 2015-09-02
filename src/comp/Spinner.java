package comp;

import gui.screen.Screen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Spinner
 */
public class Spinner<T> extends Component {

    private ISpinnerModel<T> spinnerModel;
    private Rectangle downButton, upButton;
    private TextBox valueBox;
    private KeyListener keyListener;
    private Timer timer;

    private static BufferedImage img_downButton, img_upButton;

    static {
        try {
            img_downButton = ImageIO.read(Spinner.class.getClassLoader().
                    getResourceAsStream("resources/gfx/comp/Spinner_downButton.png"));
            img_upButton = ImageIO.read(Spinner.class.getClassLoader().
                    getResourceAsStream("resources/gfx/comp/Spinner_upButton.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * this constructs a Spinner with automatically chosen width and height and with
     *
     * @param x                  the x position on the screen
     * @param y                  the y position on the screen
     * @param backingScreen      the Screen the Spinner will be shown (usually this)
     * @param spinnerNumberModel the spinnerModel this Spinner will work with
     */
    public Spinner(int x, int y, Screen backingScreen, SpinnerModel spinnerNumberModel) {
        this(x, y, backingScreen, (ISpinnerModel<T>) new RangeSpinnerModel(spinnerNumberModel));
    }

    public Spinner(int x, int y, Screen backingScreen, ISpinnerModel<T> spinnerModel) {
        super(x, y, 90, 50, backingScreen);

        this.spinnerModel = spinnerModel;

        valueBox = new TextBox(x + 1, y + 1, spinnerModel.currentAsString(), backingScreen);

        // adjust the width and height of the textBox
        valueBox.setHeight(img_downButton.getHeight() + img_upButton.getHeight());

        int textBoxWidth_Maximum = Component.getTextBounds(String.valueOf(spinnerModel.getMaximum()), STD_FONT).width;
        int textBoxWidth_Minimum = Component.getTextBounds(String.valueOf(spinnerModel.getMinimum()), STD_FONT).width;
        if (textBoxWidth_Maximum > textBoxWidth_Minimum)
            valueBox.setWidth(textBoxWidth_Maximum);
        else
            valueBox.setWidth(textBoxWidth_Minimum);

        upButton = new Rectangle(valueBox.getX() + valueBox.getWidth(), y + 1,
                img_upButton.getWidth(), img_upButton.getHeight());
        downButton = new Rectangle(upButton.x, valueBox.getY() + upButton.height,
                upButton.width, upButton.height);

        setWidth(valueBox.getWidth() + img_downButton.getWidth() + STD_INSETS.left + STD_INSETS.right);
        setHeight(valueBox.getHeight() + 1);

        upButton.x = valueBox.getX() + valueBox.getWidth();
        downButton.x = upButton.x;
        upButton.y = upButton.y++;
        downButton.y = downButton.y++;

        timer = new Timer("Spinner Timer", true);

        addMouseListener(new MouseAdapter() {
            TimerTask timerTask;
            boolean isTaskActive = false;

            @Override
            public void mousePressed(MouseEvent e) {
                if (isAcceptingInput()) {
                    if (downButton.contains(e.getPoint())) {
                        Spinner.this.spinnerModel.previous();
                        valueBox.setEnteredText(Spinner.this.spinnerModel.currentAsString());
                        timerTask = new TimerTask() {
                            @Override
                            public void run () {
                                Spinner.this.spinnerModel.previous();
                                valueBox.setEnteredText(Spinner.this.spinnerModel.currentAsString());
                            }
                        };
                        isTaskActive = true;

                        timer.schedule(timerTask, 300, 185);
                    } else if (upButton.contains(e.getPoint())) {
                        Spinner.this.spinnerModel.next();
                        valueBox.setEnteredText(Spinner.this.spinnerModel.currentAsString());
                        timerTask = new TimerTask() {
                            @Override
                            public void run () {
                                Spinner.this.spinnerModel.next();
                                valueBox.setEnteredText(Spinner.this.spinnerModel.currentAsString());
                            }
                        };
                        isTaskActive = true;

                        timer.schedule(timerTask, 300, 185);
                    }
                }
            }

            @Override
            public void mouseReleased (MouseEvent e) {
                if (isTaskActive) {
                    timer.cancel();
                    timer = new Timer("Spinner Timer", true);
                }
            }
        });

        // the listener needs to recognize the entered number,
        // and set spinnerModel.setValue(String.valueOf(valueBox.getEnteredText());

        keyListener = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (isAcceptingInput()) {
                    if (Character.isDigit(e.getKeyCode())) {
                        // FIXME: the new value must be between minimum and maximum
                        valueBox.enterText(e);

                    }
                }
            }
        };
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        valueBox.setX(x);
        upButton.x = x + valueBox.getWidth();
        downButton.x = upButton.x;
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        valueBox.setY(y);
        upButton.y = y;
        downButton.y = y + upButton.height;
    }

    @Override
    public void setWidth(int width) {
        if (width < upButton.width)
            width = upButton.width;
        super.setWidth(width);
        valueBox.setWidth(width - upButton.width);
    }

    @Override
    public void acceptInput() {
        super.acceptInput();
        valueBox.acceptInput();
    }

    @Override
    public void declineInput() {
        super.declineInput();
        valueBox.declineInput();
    }

    @Override
    public void setVisible(boolean vvvvvv) {
        super.setVisible(vvvvvv);
        if (vvvvvv)
            valueBox.acceptInput();
        else
            valueBox.declineInput();
    }


    /**
     * returns the SpinnerModel
     */
    public ISpinnerModel<T> getSpinnerModel() {
        return spinnerModel;
    }

    /**
     * the old spinnerModel will be replaced by the new <code>spinnerModel</code>.
     * the value, which could be seen in the textBox changes to the currentValue of the <code>spinnerModel</code>
     */

    @Deprecated
    public void setSpinnerModel(SpinnerModel spinnerModel) {
        this.spinnerModel = (ISpinnerModel<T>) new RangeSpinnerModel(spinnerModel);
        valueBox.setEnteredText(this.spinnerModel.currentAsString());
        valueBox.setStdText(this.spinnerModel.currentAsString());
    }

    public void setSpinnerModel(ISpinnerModel<T> model) {
        this.spinnerModel = model;
        valueBox.setEnteredText(spinnerModel.currentAsString());
        valueBox.setStdText(spinnerModel.currentAsString());
    }

    @Override
    public void draw(Graphics2D g) {
        if (isVisible()) {
            getBorder().draw(g);
            valueBox.draw(g);

            switch (getStatus()) {
                case NO_MOUSE:
                    g.setColor(getBorder().getOuterColor());
                    break;
                case MOUSE:
                    g.setColor(getBorder().getHoverColor());
                    break;
                case CLICK:
                    g.setColor(getBorder().getClickColor());
                    break;
                case NOT_AVAILABLE:
                    g.setColor(getBorder().getNotAvailableColor());
                    break;
                default:
                    System.err.println("Status not defined.");
                    System.exit(1);
            }
            g.drawRect(upButton.x - 1, upButton.y - 1, upButton.width + 1, getHeight() + 1);

            g.drawImage(img_downButton, downButton.x, downButton.y, downButton.width, downButton.height, null);
            g.drawImage(img_upButton, upButton.x, upButton.y, upButton.width, upButton.height, null);

            g.drawLine(downButton.x, downButton.y, downButton.x + downButton.width, downButton.y);
        }
    }
}
