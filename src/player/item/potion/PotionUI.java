package player.item.potion;

import comp.ImageComponent;
import gui.Drawable;
import gui.screen.GameScreen;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The outward appearance of a potion. Use it's {@link comp.ImageComponent} to change the position of the PotionUI
 * as well as the height and width.
 */
public class PotionUI implements Drawable {

    private ImageComponent component;

    /**
     * Creates a new PotionUI without specified BufferedImage, so a default BufferedImage as place-keeper is used.
     * The visible value is <code>false</code> by default.
     * <b>Use {@link player.item.potion.PotionUI#createComponent(java.awt.image.BufferedImage)} or
     * {@link player.item.potion.PotionUI#createComponent(java.awt.image.BufferedImage, int, int)} to add an BufferedImage</b>
     *
     * @see player.item.potion.PotionUI#PotionUI(java.awt.image.BufferedImage)
     */
    public PotionUI () {
        component = new ImageComponent(0, 0, new BufferedImage(15, 20, BufferedImage.TYPE_4BYTE_ABGR), GameScreen.getInstance());
        component.setVisible(false);
    }

    /**
     * Creates a new PotionUI with the specified BufferedImage
     * ({@link player.item.potion.PotionUI#createComponent(java.awt.image.BufferedImage)} is used).
     * The visible value of the PotionUI is <code>false</code> by default.
     *
     * @param image the BufferedImage, which is displayed as texture for the Potion
     *
     * @see PotionUI#PotionUI()
     */
    public PotionUI (BufferedImage image) {
        createComponent(image);
        component.setVisible(false);
    }


    /** Creates a default ImageComponent with the BufferedImage <code>image</code> at the position <code>(0|0)</code> with
     * <code>GameScreen</code> as backing screen. The component
     *
     * @param image the image of the new component
     */
    public void createComponent (BufferedImage image) {
        component = new ImageComponent(0, 0, image, GameScreen.getInstance());
    }

    /**
     * Creates an {@link comp.ImageComponent} with the specified values to represent the look of a PotionUI on screen
     * (backingScreen is <code>GameScreen</code>).
     *
     * @param image the BufferedImage (texture) of the PotionUI
     * @param posX the x position on the screen
     * @param posY the y position on the screen
     */
    public void createComponent (BufferedImage image, int posX, int posY) {
        component = new ImageComponent(posX, posY, image, GameScreen.getInstance());
    }

    /** the component of the PotionUI. Use it for example to change the x and y position of the component.
     *
     * @return the imageComponent, which is created with default values or after <code>createComponent(...)</code> is called.
     */
    public ImageComponent getComponent () {
        return component;
    }

    /**
     * @return Is the Potion visible on the screen?
     */
    public boolean isVisible () {
        return component.isVisible();
    }

    /**
     * Changes the visible value and therefore if the PotionUI is drawn or not.
     *
     * @param isVisible the new visible value
     */
    public void setVisible (boolean isVisible) {
        component.setVisible(isVisible);
    }

    @Override
    public void draw (Graphics2D g) {
        if (isVisible())
            component.draw(g);
    }
}
