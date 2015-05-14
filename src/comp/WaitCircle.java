package comp;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * A ring with a line spinning in it. To show that something is being calculated
 * or that the user got to wait for something.
 */
public class WaitCircle extends AnimationLike {

    private BufferedImage image;
    private double angle = 0.0;
    private int sideLength;

    public WaitCircle(int sideLength, Color fillColor) {
        this.sideLength = sideLength;
        this.image = recreate(angle, sideLength);
    }

    public WaitCircle(int sideLength) {
        this(sideLength, WaitCircle.DefaultWaitColor);
    }

    private BufferedImage recreate(double highlightAngle, int sideLength) {
        final BufferedImage image = new BufferedImage(sideLength, sideLength, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        final double inset = sideLength * WaitCircle.RingThicknessRatio;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
        g.setStroke(WaitCircle.LineStroke);

        // Ring interior
        g.setColor(WaitCircle.DefaultWaitColor);
        g.fillOval(0, 0, sideLength, sideLength);

        g.setColor(Color.white);

        final AffineTransform oldTransform = g.getTransform();
        final AffineTransform rotateTransform = AffineTransform.getRotateInstance(highlightAngle),
                              translateTransform = AffineTransform.getTranslateInstance(sideLength / 2, sideLength / 2),
                              concatenated = new AffineTransform(translateTransform);
        concatenated.concatenate(rotateTransform);

        g.setTransform(concatenated);
        g.drawLine(0, 0, 0, -(sideLength / 2));
        g.setTransform(oldTransform);

        // Draw outer line
        g.setColor(Color.black);
        g.drawOval(0, 0, sideLength, sideLength);

        // Cut out inner space of ring
        g.setColor(WaitCircle.Translucent);
        g.fillOval((int) inset, (int) inset, (int) (sideLength - 2 * inset), (int) (sideLength - 2 * inset));

        // Interior border
        g.setColor(Color.black);
        g.drawOval((int) inset, (int) inset, (int) (sideLength - 2 * inset), (int) (sideLength - 2 * inset));

        g.dispose();
        return image;
    }

    @Override
    public BufferedImage getImage() {
        return ImageLike$class.getImage(this);
    }

    @Override
    public BufferedImage previousKeyframe() {
        return recreate(angle - WaitCircle.AnglePerDrawing, sideLength);
    }

    @Override
    public BufferedImage nextKeyframe() {
        return recreate(angle + WaitCircle.AnglePerDrawing, sideLength);
    }

    @Override
    public void drawImage(Graphics2D g, int x, int y, int width, int height) {
        ImageLike$class.drawImage(this, g, x, y, width, height);
        image = nextKeyframe();
        angle += WaitCircle.AnglePerDrawing;
    }

    @Override
    public BufferedImage image() {
        return image;
    }

    public static final Color DefaultWaitColor = new Color(66, 148, 196),
                              Translucent = new Color(0, 0, 0, 255);
    private static final double RingThicknessRatio = 1 / 5.0,
                                AnglePerDrawing = Math.toRadians(3);
    private static final Stroke LineStroke = new BasicStroke(3);

}
