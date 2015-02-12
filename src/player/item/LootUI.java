package player.item;

import comp.Component;
import comp.ImageComponent;
import gui.Drawable;

import java.awt.*;

/**
 * Every Loot can be seen somehow, so this is the abstract class for any LootUI.
 * Override the drawMethod and it's done.
 */
public abstract class LootUI implements Drawable {

    private Component component;

    public LootUI (Component component) {
        this.component = component;
    }

    /** the component of the Loot for GUI. */
    public Component getComponent () {
        return component;
    }
}
