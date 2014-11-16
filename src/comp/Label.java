package comp;

import gui.Screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

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

	public Label() {
		declineInput();
		setName("Label " + hashCode());
	}

	public Label(int x, int y, Screen backing, String text) {
		super(x, y, 0, 0, backing);
		
		Dimension text_bounds = Component.getTextBounds(text, STD_FONT);
		setWidth(text_bounds.width);
		setHeight(text_bounds.height);
		
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

			g.setFont(STD_FONT);
			
			g.drawString(text, getX() + STD_INSET_X, getY() + STD_INSET_Y);
		}
	}

    public void setText(String text) {
        this.text = text;
        setWidth(Component.getTextBounds(text, STD_FONT).width);
        setHeight(Component.getTextBounds(text, STD_FONT).height);
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
