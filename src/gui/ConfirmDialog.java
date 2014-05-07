package gui;

import comp.*;
import comp.Button;
import comp.Component;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConfirmDialog extends comp.Component {

	private String question;
	private comp.Button ok, cancel;
	
	public ConfirmDialog(int posX, int posY, gui.Screen backing, String question) {
		super(posX, posY, comp.Component.getTextBounds(question, STD_FONT).width * 2 + 50 + STD_INSETS.bottom, comp.Component.getTextBounds(question, STD_FONT).height + 67, backing);
		this.question = question;
		declineInput();
		
		ok = new comp.Button(comp.Component.getTextBounds(question, STD_FONT).width + posX + 10, comp.Component.getTextBounds("Ok", STD_FONT).height + posY + 20, backing, "Ok");
		cancel = new comp.Button(ok.getX() + ok.getWidth() + 10, ok.getY(), backing, "Abbrechen");

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
			d = comp.Component.getTextBounds(question, STD_FONT);
		} else {
			d = comp.Component.getTextBounds("", STD_FONT);
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

	/**
	 * Returns the bounding box of the "OK" button.
	 * @return The bounding box of the "OK" button.
	 * @deprecated Use getOkButton().getSimplifiedBounds() instead.
	 */
	@Deprecated
	Rectangle getBoundingBoxOkButton () {
		return ok.getSimplifiedBounds();
	}

	/**
	 * Returns the bounding box of the "Cancel" button.
	 * @return The bounding box of the "Cancel" button.
	 * @deprecated Use getCancelButton().getSimplifiedBounds() instead.
	 */
	@Deprecated
	Rectangle getBoundingBoxCancelButton () {
		return cancel.getSimplifiedBounds();
	}

	/**
	 * Returns the "OK" button object itself.
	 * @return The "OK" button.
	 */
	Button getOk() {
		return ok;
	}

	Button getCancel() {
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
	public void draw(Graphics2D g) {
		getBorder().draw(g);
		g.setFont(comp.Component.STD_FONT);
		g.setColor(Color.white);
		g.drawString(question, getAbsoluteX() + 10, getAbsoluteY() + 20);
		ok.draw(g);
		cancel.draw(g);
	}
}
