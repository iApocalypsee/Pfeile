package player.item.potion;

import comp.ImageComponent;
import gui.Drawable;

import comp.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The outward appearance of a potion
 */
public abstract class PotionUI implements Drawable {

    protected ImageComponent component;

    protected boolean isVisible;

    /**
     * @return is the Potion visible on the screen.
     */
    public boolean isVisible () {
        return isVisible;
    }

    @Override
    public void draw (Graphics2D g) {
        if (isVisible)
            component.draw(g);
    }
}
