package comp;

import general.Converter;
import general.JavaInterop;
import gui.screen.Screen;
import scala.Function1;
import scala.runtime.BoxedUnit;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * ComboBox is just for String
 */
public class ComboBox extends Component {
    /** the Strings for the <code> selectionList </code> */
    private String [] values;
    /** the Strings for the <code> selectionList </code> */
    public String [] getValues () { return values; }

    /** the selected String, it is the name of the <code> containerLabel</code> */
    public String getSelectedValue () { return containerLabel.getText(); }

    /** the selected Index; it is the Index of the <code> values </code>.  */
    public int getSelectedIndex () { return selectionList.getSelectedIndex(); }

    /** This is the label indicating the selected String */
    private Label containerLabel;

    /** List to select the item for containerLabel */
    private comp.List selectionList;

    /** Button, at which you have to click to open the list containg the different String-Values.
     *  It is located directly left to the <code> containerLabel </code> and contains the <code> icon </code>*/
    private Button clickButton;

    /** the icon for the Button at which need to be clicked at to open the List */
    private static BufferedImage icon;
    static {
        try {
            icon = ImageIO.read(ComboBox.class.getClassLoader().
                    getResourceAsStream("resources/gfx/comp/ComboBox_Icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ComboBox (int x, int y, int width, int height, Screen screenBacking, String[] values) {
        super(x, y, width,  height, screenBacking);
        this.values = values;

        init(screenBacking);

        if (getWidth() < containerLabel.getWidth() + clickButton.getWidth() + STD_INSETS.left + STD_INSETS.right)
            setWidth(containerLabel.getWidth() + clickButton.getWidth() + STD_INSETS.left + STD_INSETS.right);
        if (getHeight() < containerLabel.getHeight() + selectionList.getHeight() + STD_INSETS.bottom + STD_INSETS.top)
            setHeight(containerLabel.getHeight() + selectionList.getHeight() + STD_INSETS.bottom + STD_INSETS.top);

        selectionList.setWidth(getWidth());
    }

    public ComboBox (int x, int y, Screen screenBacking, String [] values) {
        super (x, y, 100, 250, screenBacking);
        this.values = values;

        init(screenBacking);


        setWidth(containerLabel.getWidth() + clickButton.getWidth() + STD_INSETS.left + STD_INSETS.right);
        setHeight(containerLabel.getHeight() + selectionList.getHeight());
        selectionList.setWidth(getWidth());
    }

    private void init (Screen screenBacking) {
        if (values.length > 0) {
            // l�ngsten Eintrag herausfinden und diese L�nge verwenden
            FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
            double width = 0;
            int index = 0;
            for (int i = 0; i < values.length; i++) {
                if (STD_FONT.getStringBounds(values[i], frc).getWidth() > width) {
                    width = STD_FONT.getStringBounds(values[i], frc).getWidth();
                    index = i;
                }
            }
            containerLabel = new Label(getX(), getY(), screenBacking, values[index]);
        } else
            containerLabel = new Label(getX(), getY(), screenBacking, "              ");
        containerLabel.setNoMouseColor(Color.black);
        containerLabel.setVisible(true);
        containerLabel.declineInput();

        clickButton = new Button(getX() + containerLabel.getWidth() + STD_INSETS.left + STD_INSETS.right, getY(), screenBacking, "");
        clickButton.setVisible(true);
        clickButton.setRoundBorder(false);
        clickButton.iconify(icon);

        selectionList = new comp.List(getX(), getY() + clickButton.getHeight(), screenBacking, Converter.convertToList(values));
        selectionList.setVisible(false);
        selectionList.declineInput();

        if(values.length > 0) {
            int width = containerLabel.getWidth();
            int height = containerLabel.getHeight();
            containerLabel.setText(values[0]);
            containerLabel.setWidth(width);
            containerLabel.setHeight(height);
        }

        clickButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectionList.isVisible()) {
                    selectionList.setVisible(false);
                    selectionList.declineInput();
                } else {
                    selectionList.setVisible(true);
                    selectionList.acceptInput();
                }
            }
        });

        final Consumer<Integer> changeTextJavaCallback = (Integer boxedIndex) -> {
            containerLabel.setText(values[getSelectedIndex()]);
        };

        selectionList.onItemSelected.registerJava(changeTextJavaCallback);
        selectionList.appendListenerToLabels(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                selectionList.setVisible(false);
            }
        });
    }

    /** sets the selectedIndex of the <code> selectionList</code> to index. It also selects at <code> containerLabel </code>.
     * @param index - The selected Index
     */
    public void setSelectedIndex (int index) {
        selectionList.setSelectedIndex(index);
        containerLabel.setText(getValues()[index]);
    }

    @Override
    public void setX (int x) {
        int diff = x - getX();
        super.setX(x);
        containerLabel.setX(getX() + diff);
        clickButton.setX(getX() + diff);
        selectionList.setX(getX() + diff);
    }

    @Override
    public void setY (int y) {
        int diff = y - getY();
        super.setY(y);
        containerLabel.setY(getY() + diff);
        clickButton.setY(getY() + diff);
        selectionList.setY(getY() + diff);
    }

    @Override
    public void declineInput () {
        super.declineInput();
        clickButton.declineInput();
        selectionList.declineInput();

    }

    @Override
    public void acceptInput () {
        super.acceptInput();
        clickButton.acceptInput();
        selectionList.acceptInput();
    }

    @Override
    public void setVisible (boolean vvvvvv) {
        super.setVisible(vvvvvv);
        clickButton.setVisible(vvvvvv);
        selectionList.setVisible(vvvvvv);
        containerLabel.setVisible(vvvvvv);

        if (vvvvvv == true) {
            clickButton.acceptInput();
        } else {
            clickButton.declineInput();
            selectionList.declineInput();
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (isVisible()) {
            g.setColor(Color.BLACK);
            g.fillRect(containerLabel.getX() - 3, containerLabel.getY() - 3, getWidth() + 7, clickButton.getHeight() + 7);
            g.setColor(Color.lightGray);
            g.fillRect(containerLabel.getX(), containerLabel.getY(), getWidth(), clickButton.getHeight() + 1);
            selectionList.draw(g);
            containerLabel.draw(g);
            clickButton.draw(g);
        }
    }
}
