package comp;

import gui.Screen;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Eine neue Button-Klasse, die ohne externe Texturen klarkommt. Auf Texturen
 * verzichte ich, weil sie falsch oder sogar überhaupt nicht
 * dargestellt werden.
 * <b>18.12.2013:</b> Erstellung des Buttons.
 * <b>3.2.2014:</b> Bilder können als Icons vom Button verwendet werden. Bugmeldungen erwünscht.
 * 
 * @author Josip
 * @version 3.2.2014
 * 
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

	public Button(int x, int y, Screen backing, String text) {
		super(x, y, Component.getTextBounds(text, Component.STD_FONT).width + 50,
				Component.getTextBounds(text, Component.STD_FONT).height + 20,
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
	
	/**
	 * Berechnet die Bounds des Buttons neu.
	 */
	void recalculateDimension() {
		Dimension d;
		// leerer Text bei text == null
		if(text != null) {
			d = Component.getTextBounds(text, STD_FONT);
		} else {
			d = Component.getTextBounds("", STD_FONT);
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
