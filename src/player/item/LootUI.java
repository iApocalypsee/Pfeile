package player.item;

import comp.Component;
import general.Delegate;
import gui.Drawable;
import world.TileLike;

import java.awt.*;

/**
 * Every Loot can be seen somehow, so this is the abstract class for any LootUI.
 * Override the drawMethod and it's done.
 */
public abstract class LootUI implements Drawable {

    protected Component component;
    private TileLike tilePosition;
    private Delegate.DelegateLike.Handle activeCallback;

    public LootUI (Component component) {
        this.component = component;
        component.setListenerTransparent(true);
    }

    /**
     * Sets the LootUI on the new Tile. It does not change the position (gridX|gridY), it only affects GUI elements.
     * The center point of the tile component must be set upon the center point of the lootUI component.
     *
     * @param tile the new tile
     */
    protected void setOnTile (TileLike tile) {
        if(tilePosition != null) {
            activeCallback.dispose();
            activeCallback = null;
        }
        this.tilePosition = tile;
        if(tilePosition != null) {
            activeCallback = tilePosition.getComponent().onTransformed.registerJava(transformationEvent -> {
                relocateGuiPosition();
            });
        }
    }

    public void relocateGuiPosition() {
        Point centerPoint = tilePosition.component().center();
        component.setLocation(centerPoint.x - component.getWidth() / 2, centerPoint.y - component.getHeight() / 2);
    }

    /**
     * @return the Component of LootUI
     */
    public Component getComponent () {
        return component;
    }
}
