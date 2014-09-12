package comp;

import general.Converter;
import gui.Screen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

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

        init(values, screenBacking);
    }

    public ComboBox (int x, int y, Screen screenBacking, String [] values) {
        super (x, y, 100, 100, screenBacking);

        init(values, screenBacking);

        setWidth(containerLabel.getWidth() + clickButton.getWidth());
        selectionList.setHeight(selectionList.tfits().height);
        setHeight(containerLabel.getHeight() + selectionList.getHeight());

    }

    private void init (String [] values, Screen screenBacking) {
        this.values = values;
        containerLabel = new Label(getX(), getY(), screenBacking, "Computerstärke");
        containerLabel.declineInput();
        containerLabel.setVisible(true);

        clickButton = new Button(getX() + containerLabel.getWidth(), getY(), screenBacking, "");
        clickButton.setVisible(true);
        clickButton.setRoundBorder(false);
        clickButton.iconify(icon);
        clickButton.recalculateDimension();

        selectionList = new comp.List(getX(), getY() + containerLabel.getHeight(), clickButton.getWidth() + containerLabel.getWidth(),
                getHeight() - containerLabel.getHeight(), screenBacking, Converter.convertToList(values));
        selectionList.declineInput();
        selectionList.setVisible(false);

        getBorder().setInnerColor(Color.DARK_GRAY);
        getBorder().setOuterColor(Color.LIGHT_GRAY);

        clickButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // only if click button was surly clicked
                if (clickButton.getSimplifiedBounds().contains(e.getPoint())) {
                    if (selectionList.isVisible()) {
                        selectionList.setVisible(false);
                        selectionList.declineInput();
                    } else {
                        selectionList.setVisible(true);
                        selectionList.acceptInput();
                    }
                }
            }
        });

        selectionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectionList.getSimplifiedBounds().contains(e.getPoint()) && selectionList.isAcceptingInput()) {
                    containerLabel.setText(getValues()[selectionList.getSelectedIndex()]);
                    selectionList.declineInput();
                    selectionList.setVisible(false);
                }
            }
        });
    }

    /** sets the selectedIndex of the <code> selectionList</code> to index. It also selects at <code> containerLabel </code>.
     * @param index - The selected Index
     */
    public void setSelectedIndex (int index) {
        selectionList.setSelectedIndex(index);
        containerLabel.setText(getValues()[selectionList.getSelectedIndex()]);
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
    public void setAbsoluteX (int x) {
        int diff = x - getX();
        super.setX(x);
        containerLabel.setX(getX() + diff);
        clickButton.setX(getX() + diff);
        selectionList.setX(getX() + diff);
    }

    @Override
    public void setAbsoluteY (int y) {
        int diff = y - getY();
        super.setY(y);
        containerLabel.setY(getY() + diff);
        clickButton.setY(getY() + diff);
        selectionList.setY(getY() + diff);
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
    public void draw(Graphics2D g) {
        if (isVisible()) {
            selectionList.draw(g);
            containerLabel.draw(g);
            clickButton.draw(g);
            getBorder().draw(g);
        }
    }
}
