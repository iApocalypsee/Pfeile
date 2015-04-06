package comp;

import geom.Vector2;
import gui.screen.Screen;

import java.awt.*;

/**
 * A component that does nothing.
 */
public class Region extends Component {

    public Region() {
    }

    public Region(Vector2 initialPosition, Shape srcShape, Screen backing) {
        super(initialPosition, srcShape, backing);
    }

    public Region(int x, int y, int width, int height, Screen screen) {
        super(x, y, width, height, screen);
    }

    @Override
    public void draw(Graphics2D g) {
        // Nothing, it's a region.
    }

}
