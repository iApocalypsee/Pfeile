package comp;

import gui.Screen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Spinner
 */
public class Spinner extends Component {

    private RangeSpinnerModel spinnerModel;
    private Rectangle downButton, upButton;
    private TextBox valueBox;
    private KeyListener keyListener;

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

        spinnerModel = new RangeSpinnerModel(spinnerNumberModel);
        if (Math.abs(spinnerModel.getMinimum()) < Math.abs(spinnerModel.getMaximum()))
            valueBox = new TextBox(x + 1, y + 1, String.valueOf(spinnerModel.getMaximum()), backingScreen);
        else
            valueBox = new TextBox(x + 1, y + 1, String.valueOf(spinnerModel.getMinimum()), backingScreen);
        valueBox.setHeight(img_downButton.getHeight() + img_upButton.getHeight());

        upButton = new Rectangle(valueBox.getX() + valueBox.getWidth(), y + 1,
                img_upButton.getWidth(), img_upButton.getHeight());
        downButton = new Rectangle(upButton.x, valueBox.getY() + upButton.height,
                upButton.width, upButton.height);

        setWidth(valueBox.getWidth() + img_downButton.getWidth() + 1);
        setHeight(valueBox.getHeight() + 1);

        upButton.x = valueBox.getX() + valueBox.getWidth();
        downButton.x = upButton.x;
        upButton.y = upButton.y++;
        downButton.y = downButton.y++;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed (MouseEvent e) {
                if (isAcceptingInput()) {
                    if (downButton.contains(e.getPoint())) {
	                    spinnerModel.previous();
                    } else if (upButton.contains(e.getPoint()))
	                    spinnerModel.next();
                    valueBox.setEnteredText(spinnerModel.getCurrent().toString());
                }
            }
        });

        // TODO: add Listener for valueBox
        // the listener needs to recognize the entered number,
        // and set spinnerModel.setValue(String.valueOf(valueBox.getEnteredText());

        keyListener = new KeyAdapter() {
            @Override
            public void keyTyped (KeyEvent e) {
                if (isAcceptingInput()) {
                    if (Character.isDigit(e.getKeyCode()))
                        valueBox.enterText(e);
                }
            }
        };
    }

    @Override
    public void setX (int x) {
        super.setX(x);
        valueBox.setX(x);
        upButton.x = x + valueBox.getWidth();
        downButton.x = upButton.x;
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
    public ISpinnerModel<Integer> getSpinnerModel () {
        return spinnerModel;
    }

    /** the old spinnerModel will be replaced by the new <code>spinnerModel</code>.
     *  the value, which could be seen in the textBox changes to the currentValue of the <code>spinnerModel</code>*/
    public void setSpinnerModel (SpinnerModel spinnerModel) {
        this.spinnerModel = new RangeSpinnerModel(spinnerModel);
        valueBox.setEnteredText(String.valueOf(this.spinnerModel.getCurrent()));
        valueBox.setStdText(String.valueOf(this.spinnerModel.getCurrent()));
    }

	public void setSpinnerModel(RangeSpinnerModel model) {
		this.spinnerModel = model;
		valueBox.setEnteredText(String.valueOf(spinnerModel.getCurrent()));
		valueBox.setStdText(String.valueOf(spinnerModel.getCurrent()));
	}

    @Override
    public void draw(Graphics2D g) {
        if (isVisible()) {
            getBorder().draw(g);
            valueBox.draw(g);

            switch(getStatus()) {
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
