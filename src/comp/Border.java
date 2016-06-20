package comp;

import gui.Drawable;

import java.awt.*;

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
	private Component surr;
	
	private BasicStroke stroke;

	/**
	 * Erstellt ein Border-Objekt mit durchsichtigem Rand und schwarzem
	 * Hintergrund.
	 */
	public Border() {
		this(new Color(0x565461), new Color(0xA9A9A9));
	}
	
	/**
	 * Erstellt ein Border-Objekt mit angegebenen Farben.
	 * @param inner Die Farbe, die innen verwendet wird.
	 * @param outer Die Farbe, die außen verwendet wird.
	 */
	public Border(Color inner, Color outer) {
		this.inner = inner;
		this.outer = outer;
		click = new Color(0x7B00FF);
        hover = new Color(0x8E6CD8);
		na = new Color(0xD3D3D3);
		stroke = new BasicStroke(2);
        arcHeight = 3;
        arcWidth = 4;
	}
	
	public Color getOuterColor() {
		return outer;
	}
	
	public Color getInnerColor() {
		return inner;
	}

    public Color getHoverColor() { return hover; }

	public void setOuterColor(Color borderColor) {
		this.outer = borderColor;
	}
	
	public void setInnerColor(Color insideColor) {
		this.inner = insideColor;
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

    public Color getHover() {
        return hover;
    }

    public void setHover(Color hover) {
        this.hover = hover;
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
	public final void setComponent(Component surrounding) {
		this.surr = surrounding;
	}
	
	/**
	 * Berechnet die Farbe für den Zustand ComponentStatus.MOUSE neu. Es wird der Mittelwert aus <code>getInnerColor()</code> und <code>getClickColor()</code> gebildet.
	 */
	public void recalcHover() {
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

        // TODO: Implement an option where rounded borders are drawn, without creating a new component.

        /*
		if(roundedBorder) {
			g.fillRoundRect(surr.getX(), surr.getY(), surr.getWidth(), surr.getHeight(), arcWidth, arcHeight);
		} else {
			g.fillRect(surr.getX(), surr.getY(), surr.getWidth(), surr.getHeight());
		}
		*/

        g.fill(surr.getBounds());
		
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

		/*
		if(roundedBorder) {
			g.drawRoundRect(surr.getX(), surr.getY(), surr.getWidth(), surr.getHeight(), arcWidth, arcHeight);
		} else {
			g.drawRect(surr.getX(), surr.getY(), surr.getWidth(), surr.getHeight());
		}
		*/

		g.draw(surr.getBounds());
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
