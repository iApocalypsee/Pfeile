package comp;

import general.LogFacility;
import gui.Drawable;
import gui.screen.Screen;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class Label extends Component {

    /**
     * Der Text, der vom Label dargestellt werden soll.
     */
    private TextSequence textSequence;

    private Color noMouseColor = Color.lightGray;

    private Color declineInputColor = Color.darkGray;

    private Color backgroundColor = null;

    private BufferedImage optImage = null;

    private Font font = Component.STD_FONT;

    /** null: The Color of the border is used */
    private Color fontColor = null;

    private Point textDrawLocation;
    private Point imageDrawLocation;
    private Dimension imageDrawScale;
    // ATTENTION: Considers only ONE LINE OF TEXT FOR HEIGHT. NOT MORE.
    private int textDrawScale;

    public static final Insets STD_INSETS = new Insets(5, 5, 5, 6);

    /** The gap between the picture and the text */
    private int imageTextInset = Component.STD_INSETS.left;

    public Label(int x, int y, Screen backing, String text) {
        super(x, y, backing);
        this.textSequence = new TextSequence(text);
        textDrawLocation = new Point(getX(), getY());
        imageDrawScale = new Dimension();

        Dimension text_bounds = recalculateBounds();
        setSourceShape(new Rectangle(-text_bounds.width / 2, -text_bounds.height / 2, text_bounds.width, text_bounds.height));

        getTransformation().translate(x, y);
        getTransformation().onTransformed().registerJava(transformationEvent -> recalculateInternalData());

        declineInput();
        setName("Label " + hashCode());
    }

    public Label(int x, int y, int width, int height, Screen backing) {
        super(x, y, 0, 0, backing);
        textSequence = new TextSequence();
        textDrawLocation = new Point(getX(), getY());
        imageDrawScale = new Dimension();

        setSourceShape(new Rectangle(-width / 2, -height / 2, width, height));

        getTransformation().translate(x, y);
        getTransformation().onTransformed().registerJava(transformationEvent -> recalculateInternalData());

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
                        LogFacility.log("Component status \'" + getStatus() + "\' not defined (component=" + toString() + ")!" , "Error");
                }
            } else {
                if (fontColor == null)
                    g.setColor(declineInputColor);
                else
                    g.setColor(getColorDiff(declineInputColor, fontColor));
            }

            if (optImage == null) {
                g.setFont(font);
                textSequence.draw(g);
            } else {
                g.drawImage(optImage, imageDrawLocation.x, imageDrawLocation.y, imageDrawScale.width, imageDrawScale.height, null);
                g.setFont(font);
                textSequence.draw(g);
            }
            g.setFont(Component.STD_FONT);
        }
    }

    public void setText(String text) {
        this.textSequence = new TextSequence(text);
        recalculateDimension();
    }

    /**
     * Adds the BufferedImage to the label on the right-hand side of the text. The bounds are recalculated and the
     * imageDrawScale is reseted.
     *
     * @param optimg the picture
     */
    public void iconify(BufferedImage optimg) {
        this.optImage = optimg;

        if (optimg != null) {
            imageDrawScale.width = optimg.getWidth();
            imageDrawScale.height = optimg.getHeight();
        } else {
            imageDrawScale.width = 0;
            imageDrawScale.height = 0;
        }

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

    /** Resets the width and the height of a label, if the labels' content has changed. */
    protected void recalculateDimension () {
        Dimension dimension = recalculateBounds();
        if (dimension.width > getWidth())
            setWidth(dimension.width);
        if (dimension.height > getHeight())
            setHeight(dimension.height);
    }

    private void recalculateInternalData() {
        Dimension d = textSequence.formattedDimension();

        // Considers the whole string to be written in one line, does not care about new line chars
        textDrawScale = Component.getTextBounds(getText(), font).height;

        if(optImage != null) {
            imageDrawLocation = new Point(getX(), getY());
            textDrawLocation = new Point(imageDrawLocation.x + imageTextInset + imageDrawScale.width, getY() + d.height + STD_INSETS.top);
        } else {
            textDrawLocation = new Point(getX(), getY());
        }
    }

    /**
     * Returns a dimension with new suitable bounds.
     * Also resets some of the text-related positions and dimensions.
     */
    private Dimension recalculateBounds() {
        recalculateInternalData();

        final Dimension d = textSequence.formattedDimension();

        if (optImage != null) {
            // If image is larger than current height, assign height of image as new label height.
            // Assign new width to label based on given text.
            d.setSize(d.width + imageDrawScale.width + imageTextInset, d.height > imageDrawScale.height ? d.height : imageDrawScale.height);
        }

        return d;
    }

    // <editor-fold desc="Getters and setters">

    public String getText() {
        return textSequence.getSourceString();
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

    // </editor-fold>

    class TextSequence implements Drawable {

        private java.util.List<String> textTokens;
        private String sourceString;

        public TextSequence(String text) {
            if(text == null) throw new NullPointerException();
            sourceString = text;
            textTokens = Arrays.asList(text.split("\n"));
        }

        private TextSequence() {
            sourceString = "";
            textTokens = Collections.emptyList();
        }

        @Override
        public void draw(Graphics2D g) {
            for(int i = 0; i < textTokens.size(); i++) {
                final int yInset = i * textDrawScale + textDrawScale;
                g.drawString(textTokens.get(i), textDrawLocation.x, textDrawLocation.y + yInset);
            }
        }

        public String getSourceString() {
            return sourceString;
        }

        /**
         * Find the longest string token in this text sequence.
         * As a reminder, tokens in the TextSequence class are considered to be snippets generated by
         * splitting the original string with <code>text.split("\n")</code>
         * @return The longest string token.
         */
        private String longestString() {
            String longest = null;
            int longestWidth = 0;
            for(String s : textTokens) {
                final Dimension textBounds = Component.getTextBounds(s, getFont());
                if(textBounds.width > longestWidth) {
                    longest = s;
                    longestWidth = textBounds.width;
                }
            }
            return longest;
        }

        public Dimension formattedDimension() {
            final String longest = longestString();
            final int widthOfLongest = Component.getTextBounds(longest, getFont()).width;
            final Optional<Integer> accumulatedHeight = textTokens.stream().map(string -> Component.getTextBounds(string, getFont()).height)
                                                                           .reduce((c, e) -> c + e);
            return new Dimension(widthOfLongest, accumulatedHeight.get());
        }

    }

}
