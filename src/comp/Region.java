package comp;

import geom.Vector2;
import gui.screen.Screen;

import java.awt.*;

/**
 * A component for grouping components together, but other than that, it does nothing and is basically nothing.
 * This component cannot be seen
 */
public class Region extends Component {

    public Region() {
        setVisible(false);
    }

    public Region(Vector2 initialPosition, Shape srcShape, Screen backing) {
        super(initialPosition, srcShape, backing);
        setVisible(false);
    }

    public Region(int x, int y, int width, int height, Screen screen) {
        super(x, y, width, height, screen);
        setVisible(false);
    }

    @Override
    public void setVisible(boolean vvvvvv) {
        super.setVisible(vvvvvv);
    }

    @Override
    public void draw(Graphics2D g) {

    }

}
