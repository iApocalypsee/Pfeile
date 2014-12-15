package comp;

import gui.Screen;

import java.awt.*;
import java.awt.event.KeyEvent;

public class TextBox extends Component {

	/**
	 * Der Text, der eingegeben wurde.
	 */
	private String enteredText = "";

	/**
	 * Der Standardtext, der angezeigt wird, wenn (noch) nichts eingegeben
	 * wurde.
	 */
	private String stdText;

	/**
	 * Sagt aus, ob der TextBox getriggert wurde.
	 */
	private boolean toggled;

	/**
	 * Erstellt eine TextBox, deren Koordinaten auf (0|0) gesetzt werden. Die
	 * Breite und die Höhe werden anhand des Standardtextes
	 * {@code "<enter something>"} bestimmt.
	 * 
	 * @param backing Der Screen, der die TextBox hält.
	 */
	public TextBox(Screen backing) {
		super(0, 0,
				Component.getTextBounds("<enter something>", STD_FONT).width,
				30, backing);
		this.stdText = "<enter something>";
		this.toggled = false;
		
	}

	/**
	 * Erstellt eine TextBox.
	 * 
	 * @param x
	 *            Die x Koordinate.
	 * @param y
	 *            Die y Koordinate.
	 * @param stdText
	 *            Der Standardtext, anhand dessen die Breite berechnet wird.
	 * @param backing
	 *            Der Screen, der die TextBox h�lt.
	 */
	public TextBox(int x, int y, String stdText, Screen backing) {
		super(x, y, Component.getTextBounds(stdText, STD_FONT).width + STD_INSETS.left + STD_INSETS.right, 30,
				backing);
		this.stdText = stdText;
		this.toggled = false;
	}

	/**
	 * @return the enteredText
	 */
	public String getEnteredText() {
		return enteredText;
	}

	/**
	 * @param enteredText
	 *            the enteredText to set
	 */
	public void setEnteredText(String enteredText) {
		this.enteredText = enteredText;
	}

	/**
	 * @return the stdText
	 */
	public String getStdText() {
		return stdText;
	}

	/**
	 * @param stdText
	 *            the stdText to set
	 */
	public void setStdText(String stdText) {
		this.stdText = stdText;
	}

	/**
	 * @return the toggled
	 */
	public boolean isToggled() {
		return toggled;
	}

	@Override
	public void draw(Graphics2D g) {
		
		getBorder().draw(g);
		
		if(isAcceptingInput()) {
			g.setColor(Color.white);
		}
		
		if (enteredText.isEmpty()) {
			g.drawString(stdText, getX() + STD_INSETS.right + 2, getY() + 18);
        } else {
			g.drawString(enteredText, getX() + STD_INSETS.right + 2, getY() + 18);
		}
	}

	/**
	 * F�gt angegebene Literale hinten ein oder trifft bei speziellen Tasten wie
	 * Backspace Vorkehrungen.
	 * 
	 * @param appendKeyEvent
	 *            Das KeyEvent, das analysiert werden soll
	 */
	public void enterText(KeyEvent appendKeyEvent) {

		// wenn die TextBox schon geschlossen ist, mach erst gar nichts
		if (!isAcceptingInput()) {
			return;
		}

		if (appendKeyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE) {

			// wenn der eingegebene Text schon leer ist, �berspring das L�schen
			if (enteredText.length() != 0) {
				enteredText = enteredText
						.substring(0, enteredText.length() - 1);
			}
			appendKeyEvent.consume();
			return;

		} else {

			enteredText += appendKeyEvent.getKeyChar();

		}

	}
	
	/**
	 * Setzt den Status der TextBox auf {@link ComponentStatus#NOT_AVAILABLE}.
	 * Nach diesem Aufruf kann nicht mehr in die Textbox hineingeschrieben
	 * werden, sondern nur noch der eingegebene Text abgefragt werden. Die
	 * TextBox kann mithilfe von {@link TextBox#acceptInput} wieder f�r Eingaben
	 * ge�ffnet werden.
	 */
	@Override
	public void declineInput() {
		super.declineInput();
	}
	
	/**
	 * Setzt den Status der TextBox auf {@link ComponentStatus#NO_MOUSE}.
	 * Nach diesem Aufruf kann, wenn {@link TextBox#declineInput()} aufgerufen wurde, die
	 * TextBox, wieder zum Schreiben ge�ffnet werden. Dabei wird der eingegebene
	 * Text gel�scht.
	 */
	@Override
	public void acceptInput() {
		if(!isAcceptingInput()) {
			super.acceptInput();
			enteredText = "";
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
