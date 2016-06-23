package player.item.loot;

import comp.Component;
import comp.ImageComponent;
import general.Delegate;
import gui.Drawable;
import gui.screen.GameScreen;
import world.Tile;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Every Loot can be seen somehow, so this is the abstract class for any LootUI.
 * Override the drawMethod and it's done.
 */
public abstract class LootUI implements Drawable {

    protected Component component;
    private Tile tilePosition;
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
    protected void setOnTile (Tile tile) {
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

    /** <code>image</code> is used as new for the underlying ImageComponent.
     * The center of the image is the same as the old one.
     * Every {@link java.awt.event.MouseListener}, {@link java.awt.event.MouseMotionListener}, {@link java.awt.event.MouseWheelListener}
     * from the old component is used for the new component as well.
     * <p></p>
     * This method allows to change the image of LootUI without replacing LootUI, it's Listeners and it's position.
     *
     * @param image the new BufferedImage of LootUI.
     */
    void changeUI (BufferedImage image) {
        java.util.List<MouseListener> mouseListeners = component.getMouseListeners();
        java.util.List<MouseMotionListener> mouseMotionListeners = component.getMouseMotionListeners();
        java.util.List<MouseWheelListener> mouseWheelListeners = component.getMouseWheelListeners();

        Rectangle2D tileBounds = component.getPreciseRectangle();

        component = new ImageComponent(
                (int) (tileBounds.getCenterX() - 0.5 * image.getWidth()),
                (int) (tileBounds.getCenterY() - 0.5 * image.getHeight()), image, GameScreen.getInstance());

        mouseListeners.forEach(component:: addMouseListener);
        mouseMotionListeners.forEach(component:: addMouseMotionListener);
        mouseWheelListeners.forEach(component:: addMouseWheelListener);
    }

    /**
     * @return the Component of LootUI
     */
    public Component getComponent () {
        return component;
    }
}
