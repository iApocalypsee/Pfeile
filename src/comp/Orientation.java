package comp;

import java.awt.*;

/**
 * Created by jolecaric on 08/04/15.
 */
public class Orientation {

    public final HorizontalOrientation horizontal;
    public final VerticalOrientation vertical;

    public Orientation(HorizontalOrientation horizontal, VerticalOrientation vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public Point place(String text, Font font, Rectangle inside) {
        final Dimension textBounds = Component.getTextBounds(text, font);
        int widthInset = horizontal.apply(textBounds.width, inside.width);
        int heightInset = vertical.apply(textBounds.height, inside.height);
        return new Point(inside.x + widthInset, inside.y + heightInset);
    }

}
