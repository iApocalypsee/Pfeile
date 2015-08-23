package comp;

import java.awt.*;

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
        return new Point(inside.x + widthInset, inside.y + heightInset + textBounds.height);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("comp.Orientation{");
        sb.append("horizontal=").append(horizontal);
        sb.append(", vertical=").append(vertical);
        sb.append('}');
        return sb.toString();
    }
}
