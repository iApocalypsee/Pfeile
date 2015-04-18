package comp;

import gui.screen.Screen;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Label extends Component {

    /**
     * Der Text, der vom Label dargestellt werden soll.
     */
    private String text;

    private Color noMouseColor = Color.lightGray;

    private Color declineInputColor = Color.darkGray;

    private Color backgroundColor = null;

    private BufferedImage optImage = null;

    private Font font = Component.STD_FONT;

    /** null: The Color of the border is used */
    private Color fontColor = null;

    private Point textDrawLocation = new Point(getX(), getY());
    private Point imageDrawLocation;
    private Point imageDrawScale;

    private int imageTextInset = STD_INSETS.left;

    public Label(int x, int y, Screen backing, String text) {
        super(0, 0, 0, 0, backing);
        this.text = text;

        Dimension text_bounds = Component.getTextBounds(text, font);
        setSourceShape(new Rectangle(-text_bounds.width / 2, -text_bounds.height / 2, text_bounds.width, text_bounds.height));
        getTransformation().translate(x, y);

        onTransformed.registerJava(this::recalculateDimension);

        declineInput();
        setName("Label " + hashCode());
    }

    @Override
    public void draw(Graphics2D g) {

        if (isVisible()) {

            // Only draw a background if it is desired.
            if (backgroundColor != null) {
                g.setColor(backgroundColor);
                g.fill(getBounds());
            }

            if (isAcceptingInput()) {
                switch (getStatus()) {
                    case NO_MOUSE:
                        if (fontColor == null)
                            g.setColor(getBorder().getOuterColor());
                        else
                            g.setColor(fontColor);
                        break;
                    case MOUSE:
                        if (fontColor == null)
                            g.setColor(getBorder().getHoverColor());
                        else
                            g.setColor(getColorDiff(fontColor, getBorder().getHoverColor()));
                        break;
                    case CLICK:
                        if (fontColor == null)
                            g.setColor(getBorder().getClickColor());
                        else
                            g.setColor(getColorDiff(fontColor, getBorder().getClickColor()));
                        break;
                    case NOT_AVAILABLE:
                        if (fontColor == null)
                            g.setColor(getBorder().getNotAvailableColor());
                        else
                            g.setColor(getColorDiff(fontColor, getBorder().getNotAvailableColor()));
                        break;
                    default:
                        System.out.println("Status not defined.");
                        System.exit(1);
                }
            } else {
                if (fontColor == null)
                    g.setColor(declineInputColor);
                else
                    g.setColor(getColorDiff(declineInputColor, fontColor));
            }

            if (optImage == null) {
                g.setFont(font);
                g.drawString(text, textDrawLocation.x, textDrawLocation.y);
            } else {
                g.drawImage(optImage, imageDrawLocation.x, imageDrawLocation.y, imageDrawScale.x, imageDrawScale.y, null);
                g.setFont(font);
                g.drawString(text, textDrawLocation.x, textDrawLocation.y);
            }
        }
    }

    public void setText(String text) {
        this.text = text;
        setWidth(Component.getTextBounds(text, font).width);
        setHeight(Component.getTextBounds(text, font).height);
    }

    /**
     * Setzt das Bild, das mit dem Text dargestellt werden soll, und
     * passt gegebenenfalls die Breite und Höhe des Buttons an.
     *
     * @param optimg Das Bild.
     */
    public void iconify(BufferedImage optimg) {
        this.optImage = optimg;
        recalculateDimension();
    }

    /**
     * Returns a new Color, which is the half of color c1 and c2.
     *
     * @param c1 the first color
     * @param c2 the second color
     * @return a new color with the half of each color
     */
    private Color getColorDiff (Color c1, Color c2) {
        int red = (int) ((c1.getRed() + c2.getRed()) * 0.5);
        int green = (int) ((c1.getGreen() + c2.getGreen()) * 0.5);
        int blue = (int) ((c1.getBlue() + c2.getBlue()) * 0.5);
        int alpha = (int) ((c1.getAlpha() + c2.getAlpha()) * 0.5);
        return new Color(red, green, blue, alpha);
    }

    /**
     * Berechnet die Bounds des Buttons neu.
     */
    void recalculateDimension() {
        Dimension d;
        // leerer Text bei text == null
        if (text != null) {
            d = Component.getTextBounds(text, font);
        } else {
            d = Component.getTextBounds(" ", font);
        }

        if (optImage != null) {

            imageDrawLocation = new Point(getX(), getY());
            imageDrawScale = new Point(optImage.getWidth(), optImage.getHeight());

            // Position the text so that it appears right next to the image, centered around about the half-height of
            // the given image.
            // FIXME Causes text to be pushed upwards a little bit. A little bit? :P :D
            textDrawLocation = new Point(imageDrawLocation.x + imageDrawScale.x + imageTextInset,
                    imageDrawLocation.y + imageDrawScale.y / 2 - d.height / 2 + d.height);
//            textDrawLocation = new Point(imageDrawLocation.x + imageDrawScale.x + imageTextInset,
//                    imageDrawLocation.y + imageDrawScale.y / 2);
            /*
            // Causes stack overflow...
            setWidth(imageDrawScale.x + STD_INSETS.left + d.width);
            if (imageDrawScale.y < d.height) {
                setHeight(d.height);
            } else {
                setHeight(imageDrawScale.y);
            }
            */
        } else {
            /*
            // Causes stack overflow...
            if(d.width != getWidth()) {
                setWidth(d.width);
            }
            if(d.height != getHeight()) {
                setHeight(d.height);
            }
            */

            textDrawLocation = new Point(getX(), getY() + d.height);
        }
    }

    public String getText() {
        return text;
    }

    public Font getFont () {
        return font;
    }

    public void setFont (Font font) {
        this.font = font;
        recalculateDimension();
    }

    /**
     * If <code>fontColor</code> is <code>null</code>, the color of the border is used. The standard setting is null.
     * If fontColor is used, either fontColor itself, or the difference of fontColor and the border.
     *
     * @return the color of the font of this label
     */
    public Color getFontColor () {
        return fontColor;
    }

    /**
     * If <code>fontColor</code> is <code>null</code>, the color of the border is used. The standard setting is null.
     * If fontColor is used, either fontColor itself, or the difference of fontColor and the border.
     *
     * @param fontColor the new color
     */
    public void setFontColor (Color fontColor) {
        this.fontColor = fontColor;
    }


    public int getImageTextInset() {
        return imageTextInset;
    }

    public void setImageTextInset(int imageTextInset) {
        this.imageTextInset = imageTextInset;
    }

    /**
     * automatically: Color.lightGray
     */
    public Color getNoMouseColor() {
        return noMouseColor;
    }

    public void setNoMouseColor(Color noMouseColor) {
        this.noMouseColor = noMouseColor;
    }

    /**
     * automatically: Color.darkGray
     */
    public Color getDeclineInputColor() {
        return declineInputColor;
    }

    public void setDeclineInputColor(Color declineInputColor) {
        this.declineInputColor = declineInputColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
