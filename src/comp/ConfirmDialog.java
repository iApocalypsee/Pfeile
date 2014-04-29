package comp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ConfirmDialog extends Component{

	private String question;
	private Button ok, cancel; 
	
	public ConfirmDialog(int posX, int posY, gui.Screen backing, String question) {
		super(posX, posY, Component.getTextBounds(question, STD_FONT).width * 2 + 50 + STD_INSETS.bottom, Component.getTextBounds(question, STD_FONT).height + 67, backing);
		this.question = question; 
		declineInput();
		
		ok = new Button (Component.getTextBounds(question, STD_FONT).width + posX + 10, Component.getTextBounds("Ok", STD_FONT).height + posY + 20, backing, "Ok"); 
		cancel = new Button (ok.getX() + ok.getWidth() + 10, ok.getY(), backing, "Abbrechen"); 
		
		if (cancel.getWidth() + ok.getWidth() + cancel.getX() - ok.getX() - ok.getWidth() + STD_INSETS.left * 2 + STD_INSETS.right * 2 > getWidth()) {
			setWidth(cancel.getWidth() + ok.getWidth() + cancel.getX() - ok.getX() - ok.getWidth() + STD_INSETS.left * 2 + STD_INSETS.right * 2);
		}
		
		getBorder().setInnerColor(Color.DARK_GRAY);
	}
	
	public ConfirmDialog (int posX, int posY, Component parent, String question) {
		super(posX, posY, Component.getTextBounds(question, STD_FONT).width * 2 + 50 + STD_INSETS.bottom, Component.getTextBounds(question, STD_FONT).height + 67, parent);
		this.question = question; 
		declineInput();
		
		ok = new Button (Component.getTextBounds(question, STD_FONT).width + posX + 10, Component.getTextBounds("Ok", STD_FONT).height + posY + 20, this, "Ok"); 
		cancel = new Button (ok.getX() + ok.getWidth() + 10, ok.getY(), this, "Abbrechen"); 
		
		if (cancel.getWidth() + ok.getWidth() + cancel.getX() - ok.getX() - ok.getWidth() + STD_INSETS.left * 2 + STD_INSETS.right * 2 > getWidth()) {
			setWidth(cancel.getWidth() + ok.getWidth() + cancel.getX() - ok.getX() - ok.getWidth() + STD_INSETS.left * 2 + STD_INSETS.right * 2);
		}
	}
	
	/**
	 * Berechnet die Bounds des Buttons neu.
	 */
	void recalculateDimension() {
		Dimension d = null;
		// leerer Text bei text == null
		if(question != null) {
			d = Component.getTextBounds(question, STD_FONT);
		} else {
			d = Component.getTextBounds("", STD_FONT);
		}
		
		setWidth(STD_INSETS.left + d.width + STD_INSETS.right);
		setHeight(STD_INSETS.top + d.height + STD_INSETS.top + STD_INSETS.bottom + 17 + ok.getHeight());
	}
	
	/** Gibt den Text der Frage zurück */
	public String getQuestionText () {
		return question;
	}
	
	/** Ändert den Frage des Texts und gleicht die Höhe / Breite an */
	public void setQuestionText (String question) {
		this.question = question; 
		recalculateDimension();
	}
	
	/** Gibt die Bounding Box des OK-Buttons zurück */
	public Rectangle getBoundingBoxOkButton () {
		return new Rectangle (ok.getX(), ok.getY(), ok.getWidth(), ok.getHeight()); 
	}
	
	/** Gibt die Bounding Box des Cancel-Buttons zurück */
	public Rectangle getBoundingBoxCancelButton () {
		return new Rectangle (cancel.getX(), cancel.getY(), cancel.getWidth(), cancel.getHeight());
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (isVisible() == true) {
			getBorder().draw(g);
			g.setColor(Color.BLACK);
			if (getBorder().isRoundedBorder()) 
				g.drawRoundRect(getX(), getY(), getWidth(), getHeight(), 10, 10);
			else 
				g.drawRect(getX(), getY(), getWidth(), getHeight());
			g.setFont(Component.STD_FONT);
			g.setColor(Color.white);
			g.drawString(question, getAbsoluteX() + 10, getAbsoluteY() + 20);
			ok.draw(g);
			cancel.draw(g);
		}
	}
}
