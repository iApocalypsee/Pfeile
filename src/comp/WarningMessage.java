package comp;

import gui.screen.Screen;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * draws a warning message on the screen
 */
public class WarningMessage extends Component {

    /** what's printed on the screen */
    private String message;

    /** the color of the warning message */
    private Color warningColor;

    /** the font, with which the warning message is printed */
    private Font font;

    /** the time in milliseconds that it takes the warning message to disappear again. */
    private long duration;

    /** the border of component should usually not be drawn */
    private boolean isBorderVisible;

    /**
     * Creating a new Warning message with <code>Color.red</code> and <code>STD_FONT.deriveFont(Font.BOLD, 18f)</code> as basic settings.
     * The border isn't drawn by default and the transparency is 0.
     *
     * @param warningMessage what is printed on the screen, if the message appears
     * @param posX the x-position on the screen
     * @param posY the y-position on the screen
     * @param backingScreen the backing screen - should be <code>this</code> usually-
     */
    public WarningMessage (String warningMessage, int posX, int posY, Screen backingScreen) {
        super(posX, posY, Component.getTextBounds(warningMessage, STD_FONT).width, Component.getTextBounds(warningMessage, STD_FONT).height,
                backingScreen);

        message = warningMessage;
        font = STD_FONT.deriveFont(Font.BOLD, Component.STD_FONT.getSize2D() * 1.8f);
        warningColor = new Color(1f, 0, 0, 0);
        isBorderVisible = false;
        getBorder().setInnerColor(new Color (0, 0, 0, 168));
        getBorder().setOuterColor(new Color(128, 128, 128, 168));
        duration = 2000;

        // if the user clicks on the warningMessage, it's completely visible again
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                if (getTransparency() > 0)
                    setTransparency(1);
            }
        });
    }

    /**
     * Creates a new Warning message {@link comp.WarningMessage#WarningMessage(String, int, int, gui.screen.Screen)} on
     * the position (0|0).
     *
     * @param warningMessage what is printed on the screen
     * @param backingScreen the background - usually <code>this</code> in a screen
     */
    public WarningMessage (String warningMessage, Screen backingScreen) {
        this (warningMessage, 0, 0, backingScreen);
    }

    /** the method is called, if the current frame has been drawn. It changes the transparency value of warningColor */
    private void update () {
        if (getTransparency() - 0.012f > 0)
            setTransparency(getTransparency() - 0.012f);
        else
            setTransparency(0);
    }

    /**
     * This is what the warning writes / would right on the screen.
     *
     * @return the warning message
     */
    public String getMessage () {
        return message;
    }

    /** the Color the warning message is printed with */
    public Color getWarningColor () {
        return warningColor;
    }

    /** the font the warning message is printed with */
    public Font getFont () {
        return font;
    }

    /** the current transparency of the warning message. It is between 0 and 1.
     *
     * @return <code>getWarningColor().getAlpha() / 255</code>
     */
    public float getTransparency () {
        return getWarningColor().getAlpha() / 255f;
    }

    /** an estimation, how long the warning message is printed on the screen. */
    public long getDuration () {
        return duration;
    }

    /** sets the warning message and recalculates the bounds again */
    public void setMessage (String message) {
        this.message = message;
        recalculateBounds();
    }

    /** sets the font of the warning message and recalculates the bounds */
    public void setFont (Font font) {
        this.font = font;
        recalculateBounds();
    }

    /** changes the color the warning message is printed with */
    public void setWarningColor (Color warningColor) {
        this.warningColor = warningColor;
    }

    /** sets the transparency.
     *
     * @param transparency must be between 0 and 1.
     */
    public void setTransparency (float transparency) {
        warningColor = new Color(warningColor.getRed(), warningColor.getGreen(), warningColor.getBlue(), (int) (255 * transparency));
    }

    /**
     * Starts printing the message on the screen. Equal to: <code>setTransparency(1)</code>
     */
    public void activateMessage() {
        setTransparency(1);
    }

    /** sets the duration of the effect (begin of drawing the warning message (transparency is 1) until the warning message disappears -->
     *  {@link WarningMessage#getTransparency()}is 0). It's just an estimation as GameLoop easily drops frames. */
    public void setDuration (long duration) {
        this.duration = duration;
    }

    /** if the border is visible {@link comp.Border} is drawn. The border can be changed by <code>getBorder()</code>
     * (part of component). Usually it is false. The inner and the outer color of border is in warning message slightly
     * opaque at the beginning.
     *
     * @return <code>true</code> - if the border is drawn.
     */
    public boolean isBorderVisible () {
        return isBorderVisible;
    }

    public void setBorderVisible (boolean isBorderVisible) {
        this.isBorderVisible = isBorderVisible;
    }

    /**
     * sets the width and the height as returned by <code>Component.getTextBounds(message, font)</code>
     */
    private void recalculateBounds () {
        Dimension textBounds = Component.getTextBounds(message, font);
        setWidth(textBounds.width);
        setHeight(textBounds.height);
    }

    @Override
    public void draw (Graphics2D g) {
        if (getTransparency() > 0) {
            if (isBorderVisible) {
                getBorder().draw(g);
            }
            g.setColor(warningColor);
            g.setFont(font);
            g.drawString(message, getX(), getY());
            update();
        }
    }
}
