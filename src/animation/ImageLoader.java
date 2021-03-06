package animation;

import general.LogFacility;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Loads images in different formats: {@link java.awt.image.BufferedImage} and {@link javax.swing.ImageIcon}.
 */
public class ImageLoader {

    /**
     * Loads an Image as BufferedImage. You should save the BufferedImage as <code>static</code>.
     * Try to program your code without loading the same BufferedImage twice. You could also load it as <code>Icon</code>
     * or <code>animation.StaticImage</code> directly.
     *
     * @param URL the address (e.g. <code>arrow textures/fireArrow.png</code>). The file "resources/gfx/" is automatically
     *            loaded and must <b>not</b> put "resources/gfx" in front of it.
     * @return the BufferedImage or an <code>IOException</code> and therefore <code>null</code>.
     */
    public static BufferedImage load (String URL) {
        BufferedImage image = null;
        String path = "resources/gfx/" + URL;
        try {
            image = ImageIO.read(ImageLoader.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("Error at loading: " + path, LogFacility.LoggingLevel.Error);
        }
        return image;
    }

    /**
     * Loads an Image as {@link javax.swing.ImageIcon}. Compare with {@link animation.ImageLoader#load(String)}
     *
     * @param URL the String, where the address is: "arrow textures/fireArrow.png". Don't put "resources/gfx/" in front
     *            of it; it is added automatically and can lead to embarrassing bugs.
     * @return the loaded Image as ImageIcon
     */
    public static Icon loadIcon (String URL) {
        return new ImageIcon(load(URL));
    }

    /**
     * Returns a new ImageIcon based upon an the BufferedImage instance.
     *
     * @param image the BufferedImage
     * @return the ImageIcon
     */
    public static ImageIcon toIcon (BufferedImage image) {
        return new ImageIcon(image);
    }

    /**
     * Returns the contained BufferedImage of ImageIcon.
     * Compare with {@link animation.ImageLoader#toBufferedImageNotContained(javax.swing.ImageIcon)}
     *
     * @param icon the ImageIcon
     * @return the BufferedImage
     */
    public static BufferedImage toBufferedImage (ImageIcon icon) {
        Image image = icon.getImage();
        return (BufferedImage) image;
    }

    /**
     * ImageIcon contains the abstract class <code>Image</code> - a superclass of BufferedImage. If you create an
     * ImageIcon without using an Image, you should use this method, even though it is much slower. (However, I don't
     * know, how this should happen, but I included this method nevertheless).
     *
     * @param icon the ImageIcon
     * @return the BufferedImage
     */
    public static BufferedImage toBufferedImageNotContained (ImageIcon icon) {
        BufferedImage bi = new BufferedImage(
                icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        // paint the Icon to the BufferedImage.
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return  bi;
    }
}
