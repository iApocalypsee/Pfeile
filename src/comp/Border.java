package comp;

import gui.Drawable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * <b>21.1.2014:</b> Die Farbauswahl und dein Mechanismus zur Farbberechnung kapsele ich
 * in dieser Klasse zur leichteren Handhabung. Jede Component besitzt von Standard aus
 * ein Border-Objekt.
 * @author Josip
 * @version 21.4.2014
 *
 */
public class Border implements Drawable {
	
	/**
	 * Die Farbe, die außen dargestellt werden soll.
	 */
	private Color outer;
	
	/**
	 * Die Farbe, die innen drin sein soll.
	 */
	private Color inner;
	
	/**
	 * Die Farbe, die beim Clicken hergenommen werden soll.
	 */
	private Color click;
	
	/**
	 * Die Farbe, die verwendet wird, wenn die Component nicht verf�gbar ist.
	 */
	private Color na;
	
	/**
	 * Die Farbe, die verwendet wird, wenn der Mauszeiger auf die Component zeigt.
	 */
	private Color hover;
	
	/**
	 * Gibt an, ob der Border gerundet ist.
	 */
	private boolean roundedBorder;

	/**
	 * Die Weite der Rundung, falls eine runde Grenze gezeichnet werden soll.
	 */
	private int arcWidth;

	/**
	 * Die Höhe der Rundung, falls eine runde Grenze gezeichnet werden soll.
	 */
	private int arcHeight;
	
	/**
	 * Das vom Border umgebende Component. Lesegeschütztes Feld.
	 */
	private IComponent surr;
	
	private BasicStroke stroke;

	/**
	 * Erstellt ein Border-Objekt mit durchsichtigem Rand und schwarzem
	 * Hintergrund.
	 */
	public Border() {
		this(Color.black, Color.gray);
	}
	
	/**
	 * Erstellt ein Border-Objekt mit angegebenen Farben.
	 * @param inner Die Farbe, die innen verwendet wird.
	 * @param outer Die Farbe, die außen verwendet wird.
	 */
	public Border(Color inner, Color outer) {
		this.inner = inner;
		this.outer = outer;
		click = Color.orange;
		hover = Color.cyan;
		na = Color.darkGray;
		recalcHover();
		stroke = new BasicStroke(2);
	}
	
	public Color getOuterColor() {
		return outer;
	}
	
	public Color getInnerColor() {
		return inner;
	}
	
	public void setOuterColor(Color borderColor) {
		this.outer = borderColor;
	}
	
	public void setInnerColor(Color insideColor) {
		this.inner = insideColor;
		recalcHover();
	}
	
	public final boolean isRoundedBorder() {
		return roundedBorder;
	}
	
	public void setRoundedBorder(boolean roundedBorder) {
		this.roundedBorder = roundedBorder;
	}
	
	public Color getClickColor() {
		return click;
	}
	
	public void setClickColor(Color clickColor) {
		this.click = clickColor;
	}
	
	public Color getNotAvailableColor() {
		return na;
	}
	
	public void setNotAvailableColor(Color na) {
		this.na = na;
	}
	
	/**
	 * Setzt die Component, um die herum die Border gezeichnet werden soll.
	 * @param surrounding Die neue Component.
	 */
	public final void setComponent(IComponent surrounding) {
		this.surr = surrounding;
	}
	
	/**
	 * Berechnet die Farbe für den Zustand ComponentStatus.MOUSE neu.
	 */
	void recalcHover() {
		hover = new Color(
				Math.round((inner.getRed() + click.getRed()) / 2),
				Math.round((inner.getGreen() + click.getGreen()) / 2),
				Math.round((inner.getBlue() + click.getBlue()) / 2),
				255);
	}
	
	/**
	 * Zeichnet das Border-Objekt.
	 * @param g
	 */
	@Override
	public void draw(Graphics2D g) {
		g.setColor(inner);
		if(roundedBorder) {
			g.fillRoundRect(surr.getX(), surr.getY(), surr.getWidth(), surr.getHeight(), arcWidth, arcHeight);
		} else {
			g.fillRect(surr.getX(), surr.getY(), surr.getWidth(), surr.getHeight());
		}
		
		switch(surr.getStatus()) {
		case NO_MOUSE:
			g.setColor(outer);
			break;
		case MOUSE:
			g.setColor(hover);
			break;
		case CLICK:
			g.setColor(click);
			break;
		case NOT_AVAILABLE:
			g.setColor(na);
			break;
		default:
			System.err.println("Status not defined.");
			System.exit(1);
		}
		
		g.setStroke(stroke);
		
		if(roundedBorder) {
			g.drawRoundRect(surr.getX(), surr.getY(), surr.getWidth(), surr.getHeight(), arcWidth, arcHeight);
		} else {
			g.drawRect(surr.getX(), surr.getY(), surr.getWidth(), surr.getHeight());
		}
	}
	
	public void setStroke(BasicStroke s) {
		stroke = s;
	}

	public int getArcHeight() {
		return arcHeight;
	}

	public int getArcWidth() {
		return arcWidth;
	}

	public void setArcWidth(int arcWidth) {
		this.arcWidth = arcWidth;
	}

	public void setArcHeight(int arcHeight) {
		this.arcHeight = arcHeight;
	}
}
