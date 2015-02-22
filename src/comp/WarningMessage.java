package comp;

import gui.screen.Screen;

import java.awt.*;

/**
 * draws a warning message on the screen
 */
public class WarningMessage extends Component {
    private String message;

    private Color warningColor;

    private Font font = STD_FONT;

    private float transparency;

    private long duration;

    private boolean isBorderVisible;

    public WarningMessage (String warningMessage, int posX, int posY, Screen backingScreen) {
        super(posX, posY, Component.getTextBounds(warningMessage, STD_FONT).width, Component.getTextBounds(warningMessage, STD_FONT).height,
                backingScreen);

        message = warningMessage;
        warningColor = Color.RED;
        isBorderVisible = false;
        transparency = 0;
        duration = 2000;
    }

    public WarningMessage (String warningMessage, Screen backingScreen) {
        this (warningMessage, 0, 0, backingScreen);
    }

    private void update () {
        if (transparency > 0) {
            warningColor = new Color(warningColor.getRed(), warningColor.getGreen(), warningColor.getBlue(), transparency);
            transparency = transparency - 0.013f;

            if (transparency < 0)
                transparency = 0;
        }
    }

    public String getMessage () {
        return message;
    }

    public Color getWarningColor () {
        return warningColor;
    }

    public Font getFont () {
        return font;
    }

    public float getTransparency () {
        return transparency;
    }

    public long getDuration () {
        return duration;
    }

    public void setMessage (String message) {
        this.message = message;
        recalculateBounds();
    }

    public void setFont (Font font) {
        this.font = font;
        recalculateBounds();
    }

    public void setWarningColor (Color warningColor) {
        this.warningColor = warningColor;
    }

    public void setTransparency (float transparency) {
        this.transparency = transparency;
    }

    public void setDuration (long duration) {
        this.duration = duration;
    }

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
        if (isBorderVisible) {
            getBorder().draw(g);
        }
        g.setColor(warningColor);
        g.setFont(font);
        g.drawString(message, getX(), getY());
        update();
    }
}
