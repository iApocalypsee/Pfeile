package comp;

import gui.screen.Screen;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Label extends Component {
	
	/**
	 * Der Text, der vom Label dargestellt werden soll.
	 */
	private String text;
	
	/** Abstand nach oben, damit die Schrift nicht den oberen Rad ber�hrt */
	private final int STD_INSET_Y = 15; 
	
	/** Abstand links, damit die Schrift nicht am Rand steht */
	private final int STD_INSET_X = 5;

    private Color noMouseColor = Color.lightGray;

    private Color declineInputColor = Color.darkGray;

	private Color backgroundColor = null;

    private BufferedImage optImage;

	public Label() {
		declineInput();
		setName("Label " + hashCode());
	}

	public Label(int x, int y, Screen backing, String text) {
		super(0, 0, 0, 0, backing);
		
		Dimension text_bounds = Component.getTextBounds(text, STD_FONT);
		setSourceShape(new Rectangle(-text_bounds.width / 2, -text_bounds.height / 2, text_bounds.width, text_bounds.height));
		getTransformation().translate(x, y);
		
		this.text = text;
		declineInput();
	}

	@Override
	public void draw(Graphics2D g) {
		
		if(isVisible()) {

			// Only draw a background if it is desired.
			if(backgroundColor != null) {
				g.setColor(backgroundColor);
				g.fill(getBounds());
			}

			if(isAcceptingInput()) {
			
			switch(getStatus()) {
			case NO_MOUSE:
				g.setColor(getBorder().getOuterColor());
				break;
			case MOUSE:
				g.setColor(getBorder().getHoverColor());
				break;
			case CLICK:
				g.setColor(getBorder().getClickColor());
				break;
			case NOT_AVAILABLE:
				g.setColor(getBorder().getNotAvailableColor());
				break;
			default:
				System.out.println("Status not defined.");
				System.exit(1);
			}
			
			} else {
				g.setColor(declineInputColor);
			}

            if(optImage == null) {
                g.drawString(text, getX(), getY());
            } else {
                g.drawImage(optImage, getX(), getY(), optImage.getWidth(), optImage.getHeight(), null);
                g.drawString(text, getX() + optImage.getWidth() + STD_INSETS.left,
                        getY() + STD_INSETS.top + 20);
            }
		}
	}

    public void setText(String text) {
        this.text = text;
        setWidth(Component.getTextBounds(text, STD_FONT).width);
        setHeight(Component.getTextBounds(text, STD_FONT).height);
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
            setWidth(optImage.getWidth() +
                    STD_INSETS.left + d.width);
            if(optImage.getHeight() < d.height) {
                setHeight(d.height);
            } else {
                setHeight(optImage.getHeight());
            }
        } else {
            setWidth(d.width);
            setHeight(d.height);
        }
    }

    public String getText() {
        return text;
    }

    /** automatically: Color.lightGray */
    public Color getNoMouseColor () {
        return noMouseColor;
    }

    public void setNoMouseColor (Color noMouseColor) {
        this.noMouseColor = noMouseColor;
    }

    /** automatically: Color.darkGray */
    public Color getDeclineInputColor () {
        return declineInputColor;
    }

    public void setDeclineInputColor (Color declineInputColor) {
        this.declineInputColor = declineInputColor;
    }

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
}
