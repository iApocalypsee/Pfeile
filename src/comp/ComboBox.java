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

        this.values = values;
        containerLabel = new Label(x, y, screenBacking, "Computerstärke");
        containerLabel.declineInput();
        containerLabel.setVisible(true);

        clickButton = new Button(x + containerLabel.getWidth(), y, screenBacking, "Click");
        clickButton.setVisible(true);
        clickButton.setRoundBorder(false);
        clickButton.iconify(icon);
        clickButton.recalculateDimension();

        selectionList = new comp.List(x, y + containerLabel.getHeight(), getWidth(), getHeight() - containerLabel.getHeight(), screenBacking, Converter.convertToList(values));
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

    @Override
    public void draw(Graphics2D g) {
        selectionList.draw(g);
        containerLabel.draw(g);
        clickButton.draw(g);
        getBorder().draw(g);
    }
}
