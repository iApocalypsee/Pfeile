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
    private Color fillColor;
    private double anglePerDrawing;

    public WaitCircle(int sideLength, Color fillColor) {
        this.sideLength = sideLength;
        this.fillColor = fillColor;
        this.image = recreate(angle, sideLength);
        anglePerDrawing = WaitCircle.AnglePerDrawing;
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
        g.setColor(fillColor);
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

    /** the angle, the WaitCircle turns after each draw-process. It is "3" by default.
     * <b>The value is in degrees.</b> */
    public double getAnglePerDrawing () {
        return Math.toDegrees(anglePerDrawing);
    }

    /**
     * The default value is 3 <b>degree</b>. This allows to set the speed of the wait circle, but it still depends on
     * the speed of the system very much.
     *
     * @param anglePerDrawingDegree the angle, the wait circle turns after each draw process.
     */
    public void setAnglePerDrawing (double anglePerDrawingDegree) {
        synchronized (this) {
            anglePerDrawing = Math.toRadians(anglePerDrawingDegree);
        }
    }

    /*
    @Override
    public BufferedImage getImage() {
        return ImageLike$class.getImage(this);
    }
    */

    @Override
    public BufferedImage previousKeyframe() {
        return recreate(angle - anglePerDrawing, sideLength);
    }

    @Override
    public BufferedImage nextKeyframe() {
        return recreate(angle + anglePerDrawing, sideLength);
    }

    @Override
    public void drawImage(Graphics2D g, int x, int y, int width, int height) {
        super.drawImage(g, x, y, width, height);
        image = nextKeyframe();
        angle += anglePerDrawing;
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
