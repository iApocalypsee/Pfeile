package gui;


import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageHelper {

    /**
     * Converts a given Image into a BufferedImage. Usually it should work with casting, but in some cases, it may cause
     * RuntimeExceptions. So use this method.
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
            return (BufferedImage) img;

        // Create a buffered image with transparency
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return bufferedImage;
    }

    /** Returns a the scaled BufferedImaged, the scaleFactor and the rendering algorithm scaleHints. The possible rendering algorithm are listed at {@link java.awt.Image}:
     * {@link Image#SCALE_DEFAULT}, {@link Image#SCALE_AREA_AVERAGING}, {@link Image#SCALE_FAST}, {@link Image#SCALE_SMOOTH},
     * {@link Image#SCALE_REPLICATE}.
     * This method is quite slowly, so try not use it very often. The method operates like this: <p>
     * <code>ImageHelper.toBufferedImage(image.getScaledInstance(newWidth, newHeight, scaleHints));</code>
     *
     * @return the scaled image
     * */
    public static BufferedImage scaleBufferedImage (BufferedImage image, float scaleFactorX, float scaleFactorY, int scaleHints) {
        Image scaledImg = image.getScaledInstance((int) (scaleFactorX * image.getWidth()), (int) (scaleFactorY * image.getHeight()), scaleHints);
        return ImageHelper.toBufferedImage(scaledImg);
    }

    /** See the description of {@link gui.ImageHelper#scaleBufferedImage(java.awt.image.BufferedImage, float, float, int)} (java.awt.image.BufferedImage, float, float, int)}.
     * The scaling algorithm type is replaced with {@link java.awt.Image#SCALE_DEFAULT}.
     *
     * @param image the image to be scaled
     * @param scaleFactorX the scaleFactor in X-direction
     * @param scaleFactorY the scaleFactor in Y-direction
     * @return the scaledBufferedImage
     * */
    public static BufferedImage scaleBufferedImage (BufferedImage image, float scaleFactorX, float scaleFactorY) {
        return scaleBufferedImage(image, scaleFactorX, scaleFactorY, Image.SCALE_DEFAULT);
    }

    /** See the description of {@link gui.ImageHelper#scaleBufferedImage(java.awt.image.BufferedImage, float, float, int)}.
     * The scaling algorithm type is replaced with {@link java.awt.Image#SCALE_DEFAULT} and both the width and the height are
     * scaled with the same factor <code>scaleFactor</code>.
     * */
    public static BufferedImage scaleBufferedImage (BufferedImage image, float scaleFactor) {
        return scaleBufferedImage(image, scaleFactor, scaleFactor, Image.SCALE_DEFAULT);
    }

    /** See the description of {@link gui.ImageHelper#scaleBufferedImage(java.awt.image.BufferedImage, float, float, int)}
     * The x and y direction are both scaled with <code>scaleFactor</code>. */
    public static BufferedImage scaleBufferedImage (BufferedImage image, float scaleFactor, int scaleHint) {
        return scaleBufferedImage(image, scaleFactor, scaleFactor, scaleHint);
    }
}
