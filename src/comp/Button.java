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

	/**
	 * Ein optionales Bild, das mit dem Text dargestellt werden kann.
	 */
	private BufferedImage optImage = null;

    private Font font = Component.STD_FONT;

	public Button(int x, int y, Screen backing, String text) {
		super(x, y, Component.getTextBounds(text, Component.STD_FONT).width + 50,
				Component.getTextBounds(text, Component.STD_FONT).height + 25,
				backing);
		this.text = text;
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
        recalculateDimension();
    }

    /** Returns the font of this button; the default value is <code>Component.STD_VALUE</code> */
    public Font getFont() {
        return font;
    }
	
	/**
	 * Berechnet die Bounds des Buttons neu.
	 */
	void recalculateDimension() {
		Dimension d;
		// leerer Text bei text == null
		if(text != null) {
			d = Component.getTextBounds(text, font);
		} else {
			d = Component.getTextBounds("", font);
		}
		
		if(optImage != null) {
			setWidth(STD_INSETS.left + optImage.getWidth() + 
					STD_INSETS.left + d.width + STD_INSETS.right);
			if(optImage.getHeight() < d.height) {
				setHeight(STD_INSETS.top + d.height + STD_INSETS.bottom);
			} else {
				setHeight(STD_INSETS.top + optImage.getHeight() + STD_INSETS.bottom);
			}
		} else {
			setWidth(STD_INSETS.left + d.width + STD_INSETS.right);
			setHeight(STD_INSETS.top + d.height + STD_INSETS.bottom);
		}
	}

	@Override
	public void draw(Graphics2D g) {
        if (isVisible()) {
            getBorder().draw(g);
            g.setColor(Color.white);
            g.setFont(font);

            if(optImage == null) {
                g.drawString(text, getX() + 10, getY() + 20);
            } else {
                g.drawImage(optImage, getX() + STD_INSETS.left, getY() + STD_INSETS.top,
                        optImage.getWidth(), optImage.getHeight(), null);
                g.drawString(text, getX() + STD_INSETS.left + optImage.getWidth() + STD_INSETS.left,
                        getY() + STD_INSETS.top + 20);
            }

	        if(getAdditionalDrawing() != null) {
		        getAdditionalDrawing().apply(g);
	        }
        }
    }
	
	/** setzt, ob das Rechteck rund ist oder nicht */
	public void setRoundBorder(boolean isRoundRect) {
		getBorder().setRoundedBorder(isRoundRect);
	}
	
	/** gibt zur�ck, ob das Rechteck (Border) das gezeichnet werden soll, rund ist oder nicht */
	public boolean isRoundBorder () {
		return getBorder().isRoundedBorder();
	}
}
