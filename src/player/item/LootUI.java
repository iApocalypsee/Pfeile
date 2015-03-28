package player.item;

import comp.Component;
import gui.Drawable;

/**
 * Every Loot can be seen somehow, so this is the abstract class for any LootUI.
 * Override the drawMethod and it's done.
 */
public abstract class LootUI implements Drawable {

    protected Component component;

    public LootUI (Component component) {
        this.component = component;
    }

    /**
     * @return the Component of LootUI
     */
    public Component getComponent () {
        return component;
    }
}
