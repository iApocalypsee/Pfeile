package comp;

import gui.screen.Screen;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This is the "new" Button class, which doesn't need textures.
 */
public class Button extends Component {

	/**
	 * Der Text, der vom Button dargestellt werden soll.
	 */
	private String text;

    /** The distance between the image and the text. */
    private int insetsBetween = 3;
    
    private Insets insets = new Insets(6, 8, 8, 5);

	/**
	 * Ein optionales Bild, das mit dem Text dargestellt werden kann.
	 */
	private BufferedImage optImage = null;

    /**
     * The width and height of the text used with the selected font.
     */
    private Dimension textDimension;

    private Font font = Component.STD_FONT;

	public Button(int x, int y, Screen backing, String text) {
		super(x, y, backing);
		this.text = text;
        calculateTextDimension();
        recalculateDimension();
	}

    public Button (int x, int y, BufferedImage icon, Screen backing, String text) {
        super(x, y, backing);
        this.text = text;
        optImage = icon;
        calculateTextDimension();
        recalculateDimension();
    }
	
	/**
	 * Gibt den Text des Buttons zurück.
	 * @return Den Text des Buttons.
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Setzt den neuen Text und verändert die Größe des Buttons, wenn nötig.
	 * @param t Der neue Text
	 */
	public void setText(String t) {
		text = t;
        calculateTextDimension();
		recalculateDimension();
	}
	
	/**
	 * Setzt das Bild, das mit dem Text dargestellt werden soll, und
	 * passt gegebenenfalls die Breite und Höhe des Buttons an.
	 * @param optimg Das Bild.
	 */
	public void iconify(BufferedImage optimg) {
		this.optImage = optimg;
		recalculateDimension();
	}

    /** Changes the font used in the button. Don't use it to often, since the bounds of the button have to be
     * recalculated and changed. Generally speaking, don't use other fonts then the Component.STD_FONT fontFamilyType,
     * because it may look strange.
     *
     * @param font the new font of the button.
     */
    public void setFont(Font font) {
        this.font = font;
        calculateTextDimension();
        recalculateDimension();
    }

    /** Returns the font of this button; the default value is <code>Component.STD_VALUE</code> */
    public Font getFont() {
        return font;
    }

    /** Calculates the width and height the text uses. */
    private void calculateTextDimension() {
        // leerer Text bei text == null
        if(text != null) {
            textDimension = Component.getTextBounds(text, font);
        } else {
            textDimension = new Dimension(0, font.getSize());
        }

    }
	
	/** Berechnet die Bounds des Buttons neu. <b>Vorher muss gegebenenfalls noch <code>calculateTextDimension()</code> aufgerufen werden.</b>*/
	void recalculateDimension() {
		if(optImage != null) {
			setWidth(insets.left + optImage.getWidth() +
                    insetsBetween + textDimension.width + insets.right);
            if(optImage.getHeight() < textDimension.height) {
				setHeight(insets.top + textDimension.height + insets.bottom);
			} else {
				setHeight(insets.top + optImage.getHeight() + insets.bottom);
			}
		} else {
			setWidth(insets.left + textDimension.width + insets.right);
			setHeight(insets.top + textDimension.height + insets.bottom);
		}
	}

    /** Returns the insets of this Button. By default, it differs from Component.STD_INSETS. */
    public Insets getInsets() {
        return insets;
    }

    /** Changes the insets of this Button and recalculates its dimension. */
    public void setInsets(Insets insets) {
        this.insets = insets;
        recalculateDimension();
    }

    /** the distance between the image and the text. If there's is no image, that property has no impact at all. */
    public int getInsetsBetween() {
        return insetsBetween;
    }

    /** Changes the distance between image and text. If there's no image, the change won't have any effect until an image has been added.
     *  If this Button contains an image, the new dimension is calculated and set.
     *
     * @param inset the gap between image and text
     */
    public void setInsetsBetween(int inset) {
        insetsBetween = inset;
        if (optImage != null)
            recalculateDimension();
    }
	
	/** setzt, ob das Rechteck rund ist oder nicht */
	public void setRoundBorder(boolean isRoundRect) {
		getBorder().setRoundedBorder(isRoundRect);
	}
	
	/** gibt zur�ck, ob das Rechteck (Border) das gezeichnet werden soll, rund ist oder nicht */
	public boolean isRoundBorder () {
		return getBorder().isRoundedBorder();
	}

    @Override
    public void draw(Graphics2D g) {
        getBorder().draw(g);
        g.setColor(Color.white);
        g.setFont(font);

        if(optImage == null) {
            g.drawString(text, getX() + insets.left, (int) (getY() + insets.top + textDimension.getHeight()));
        } else {
            g.drawImage(optImage, getX() + insets.left, getY() + insets.top,
                    optImage.getWidth(), optImage.getHeight(), null);
            g.drawString(text, getX() + insets.left + optImage.getWidth() + insetsBetween,
                    (int) (getY() + insets.top + textDimension.getHeight()));
        }

        if(getAdditionalDrawing() != null) {
            getAdditionalDrawing().apply(g);
        }
    }
}
