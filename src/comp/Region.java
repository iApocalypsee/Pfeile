package comp;

import gui.screen.Screen;

import java.awt.*;

public class Region extends Component {

    public Region(int x, int y, int width, int height, Screen screen) {
        super(x, y, width, height, screen);
    }

    @Override
    public void draw(Graphics2D g) {
        // Nothing, it's a region.
    }

}
