package comp;

import gui.screen.Screen;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConfirmDialog extends comp.Component {

	private String question;
	private comp.Button ok, cancel;
	
	public ConfirmDialog(int posX, int posY, Screen backing, String question) {
		super(posX, posY, Component.getTextBounds(question, STD_FONT).width * 2 + 50 + STD_INSETS.bottom, Component.getTextBounds(question, STD_FONT).height + 67, backing);
		this.question = question;
		declineInput();
		
		ok = new Button(Component.getTextBounds(question, STD_FONT).width + posX + 10, comp.Component.getTextBounds("Ok", STD_FONT).height + posY + 20, backing, "Ok");
		cancel = new Button(ok.getX() + ok.getWidth() + 10, ok.getY(), backing, "Abbrechen");

		ok.addMouseListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 *
			 * @param e
			 */
			@Override
			public void mouseReleased(MouseEvent e) {
				setVisible(false);
			}
		});

		cancel.addMouseListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 *
			 * @param e
			 */
			@Override
			public void mouseReleased(MouseEvent e) {
				setVisible(false);
			}
		});
		
		if (cancel.getWidth() + ok.getWidth() + cancel.getX() - ok.getX() - ok.getWidth() + STD_INSETS.left * 2 + STD_INSETS.right * 2 > getWidth()) {
			setWidth(cancel.getWidth() + ok.getWidth() + cancel.getX() - ok.getX() - ok.getWidth() + STD_INSETS.left * 2 + STD_INSETS.right * 2);
		}
		
		getBorder().setInnerColor(Color.DARK_GRAY);
		getBorder().setOuterColor(Color.BLACK);
		getBorder().setStroke(new BasicStroke(5));
		getBorder().setRoundedBorder(true);
	}
	
	/**
	 * Berechnet die Bounds des Buttons neu.
	 */
	void recalculateDimension() {
		Dimension d;
		// leerer Text bei text == null
		if(question != null) {
			d = Component.getTextBounds(question, STD_FONT);
		} else {
			d = Component.getTextBounds("", STD_FONT);
		}
		
		setWidth(STD_INSETS.left + d.width + STD_INSETS.right);
		setHeight(STD_INSETS.top + d.height + STD_INSETS.top + STD_INSETS.bottom + 17 + ok.getHeight());
	}
	
	/** Gibt den Text der Frage zur�ck */
	public String getQuestionText () {
		return question;
	}
	
	/** �ndert den Frage des Texts und gleicht die H�he / Breite an */
	public void setQuestionText (String question) {
		this.question = question; 
		recalculateDimension();
	}

	/**
	 * Returns the "OK" button object itself.
	 * @return The "OK" button.
	 */
	public Button getOk() {
		return ok;
	}

	public Button getCancel() {
		return cancel;
	}

	/**
	 * Sets the visibility flag not only for the dialog, but for the buttons
	 * inside it aswell.
	 * @param vvvvvv Der neue Sichtbarkeitswert.
	 */
	@Override
	public void setVisible(boolean vvvvvv) {
		super.setVisible(vvvvvv);
		ok.setVisible(vvvvvv);
		cancel.setVisible(vvvvvv);
	}

    @Override
    public void setX (int x) {
        super.setX(x);
        ok.setX(Component.getTextBounds(question, STD_FONT).width + getX() + 10);
        cancel.setX(ok.getX() + ok.getWidth() + 10);
    }

    @Override
    public void setY (int y) {
        super.setY(y);
        ok.setY(comp.Component.getTextBounds("Ok", STD_FONT).height + getY() + 20);
        cancel.setY(ok.getY());
    }

    @Override
	public void draw(Graphics2D g) {
        if (isVisible()) {
            getBorder().draw(g);
            g.setColor(Color.white);
            g.drawString(question, getX() + 10, getY() + 20);
            ok.draw(g);
            cancel.draw(g);
        }
	}
}
