package comp;

import gui.Screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Label extends Component {
	
	/**
	 * Der Text, der vom Label dargestellt werden soll.
	 */
	private String text;
	
	/** Abstand nach oben, damit die Schrift nicht den oberen Rad ber�hrt */
	private final int STD_INSET_Y = 15; 
	
	/** Abstand links, damit die Schrift nicht am Rand steht */
	private final int STD_INSET_X = 5; 

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
		
		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (isAcceptingInput())
                    System.out.println("Executing label " + getName());
			}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		
	}
	
	public Label(int x, int y, String text, Component parent) {
//		super(x, y, Component.getTextBounds(text, STD_FONT).width, 
//				Component.getTextBounds(text, STD_FONT).height, parent);
		super(x, y, 0, 0, parent);
		this.text = text;
		setWidth(Component.getTextBounds(text, STD_FONT).width);
		setHeight(Component.getTextBounds(text, STD_FONT).height);
		
	}

	@Override
	public void draw(Graphics2D g) {
		
		if(isVisible()) {

			if(isAcceptingInput()) {
			
			switch(getStatus()) {
			case NO_MOUSE:
				g.setColor(Color.lightGray);
				break;
			case MOUSE:
				g.setColor(Color.orange);
				break;
			case CLICK:
				g.setColor(Color.yellow);
				break;
			case NOT_AVAILABLE:
				g.setColor(Color.gray);
				break;
			default:
				System.out.println("Status not defined.");
				System.exit(1);
			}
			
			} else {
				g.setColor(Color.darkGray);
			}

			g.setFont(STD_FONT);
			
			// FIXME Einfachere Zeichnung: anstatt getParent().getX() + this.getX() jetzt this.getAbsoluteX()
			g.drawString(text, getAbsoluteX() + STD_INSET_X, getAbsoluteY() + STD_INSET_Y);
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
}
