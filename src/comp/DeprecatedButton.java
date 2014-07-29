package comp;

import gui.Screen;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Die Button-Klasse stellt einen drückbaren Knopf dar, basierend auf Bildern.
 * Die Bilder werden je nach bestimmtem Status geladen, der am besten mit der
 * Mausposition verändert wird.
 * 
 * @author Josip Palavra
 * @version 19.10.2013
 * @deprecated Texturen werden nicht richtig dargestellt. Ich versuche in Bälde
 *             eine neue Button-Klasse zu machen, die ohne externe Texturen
 *             auskommen soll. Weiterentwickeln werde ich diese Klasse nicht.
 * 
 */
@Deprecated
public class DeprecatedButton extends Component {

	/**
	 * Der derzeitige Text auf dem Button.
	 */
	private String displayText = "";

	/**
	 * Die Höhe der Schrift.
	 */
	private int fontHeight;

	/**
	 * Die Anzahl der Button-Bilder von der Mitte, die gebraucht werden, um den
	 * Texthintergrund auszufüllen.
	 */
	private int imageLength;

	/**
	 * Die Schriftart des Buttons.
	 */
	private Font font;

	/**
	 * Die für diesen Button benutzten Bilder.
	 */
	private BufferedImage[] usedImages;

	/**
	 * Die Standardschriftart. Kann (und sollte) auch später geändert werden.
	 */
	public static final Font STD_FONT = new Font("Consolas", Font.PLAIN, 14);

	/**
	 * Die Bilder. Untenstehende Flags bezeichnen das Bild.
	 */
	private static BufferedImage[] imgs;

	/* Die Flags für die Bilder. */
	public static final int LEFT_NOMOUSE = 0, LEFT_MOUSE = 1, LEFT_CLICK = 2,
			LEFT_NA = 3, MID_NOMOUSE = 4, MID_MOUSE = 5, MID_CLICK = 6,
			MID_NA = 7, RIGHT_NOMOUSE = 8, RIGHT_MOUSE = 9, RIGHT_CLICK = 10,
			RIGHT_NA = 11;

	static {
		// Die Pfade für jedes Bild
		String[] path = new String[] {
				"com/github/pfeile/resources/gfx/button textures/left_nomouse.png", // left
																	// nomouse
				"com/github/pfeile/resources/gfx/button textures/mid_nomouse.png", // left mouse
				"com/github/pfeile/resources/gfx/button textures/right_nomouse.png", // left click
				"com/github/pfeile/resources/gfx/button textures/mid_nomouse.png", // left na
				"com/github/pfeile/resources/gfx/button textures/mid_nomouse.png", // mid nomouse
				"com/github/pfeile/resources/gfx/button textures/test_active.png", // mid mouse
				"com/github/pfeile/resources/gfx/button textures/test_active.png", // mid click
				"com/github/pfeile/resources/gfx/button textures/test_inactive.png", // mid na
				"com/github/pfeile/resources/gfx/button textures/right_nomouse.png", // right
																	// nomouse
				"com/github/pfeile/resources/gfx/button textures/test_active.png", // right mouse
				"com/github/pfeile/resources/gfx/button textures/test_active.png", // right click
				"com/github/pfeile/resources/gfx/button textures/test_inactive.png", // right na
		};
		// Das Bildarray
		imgs = new BufferedImage[12];

		try {

			for (int i = 0; i < path.length; i++) {
				imgs[i] = ImageIO.read(DeprecatedButton.class.getClassLoader()
						.getResourceAsStream(path[i]));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Erstellt einen Button. Kann nur im Zusammenhang mit einem Screen
	 * verwendet werden.
	 * 
	 * @param text
	 *            Der Text, der dargestellt werden soll.
	 * @param x
	 *            Die x Position des Buttons
	 * @param y
	 *            Die y Position des Buttons
	 * @param backing
	 *            Der Screen, der den Button beinhält.
	 */
	public DeprecatedButton(String text, int x, int y, Screen backing) {

		super(x, y,
				imgs[LEFT_NOMOUSE].getWidth() + imgs[MID_NOMOUSE].getWidth()
						+ imgs[RIGHT_NOMOUSE].getWidth(), 50, backing);

		// Festlegung der Koordinaten auf dem Bildschirm
		setX(x);
		setY(y);

		// Festlegung des Texts
		setDisplayText(text);

		// Festlegung der Standardschriftgröße auf 13.
		setFontHeight(13);

		// FIXME Implement LineMetrics

		// Variable wird benutzt, um herauszufinden, wieviele Bilder in der
		// Mitte benötigt werden. Die 10 ist geschätzter Wert. TODO
		int textlength_pixels = text.length() * 10;

		// Speichert die Divison von der (ungefähren) Pixelbreite des Texts mit
		// der Breite des Bilds.
		setImageLength((int) Math.ceil(textlength_pixels
				/ imgs[(12 % 4) + 1].getHeight(null)));

		setUsedImages(imgs);
	}

	/**
	 * Erstellt einen Button. Kann nur im Zusammenhang mit einem Screen
	 * verwendet werden.
	 * 
	 * @param text
	 *            Der Text, der dargestellt werden soll.
	 * @param x
	 *            Die x Position des Buttons
	 * @param y
	 *            Die y Position des Buttons
	 * @param font
	 *            Die Schriftart des Buttons.
	 * @param backing
	 *            Der Screen, der den Button beinhält.
	 */
	public DeprecatedButton(String text, int x, int y, Font font, Screen backing) {

		super(x, y,
				imgs[LEFT_NOMOUSE].getWidth() + imgs[MID_NOMOUSE].getWidth()
						+ imgs[RIGHT_NOMOUSE].getWidth(), 50, backing);

		// Festlegung des Texts
		setDisplayText(text);

		// Festlegung der Standardschriftgröße auf Font.getSize()
		setFontHeight(font.getSize());
		// Legt die Schriftart fest
		setFont(font);

		// Variable wird benutzt, um herauszufinden, wieviele Bilder in der
		// Mitte benötigt werden. Die 10 ist geschätzter Wert. TODO
		int textlength_pixels = text.length() * 5;

		// Speichert die Divison von der (ungefähren) Pixelbreite des Texts mit
		// der Breite des Bilds.
		setImageLength((int) Math.ceil(textlength_pixels
				/ imgs[(12 % 4) + 1].getWidth()));

		setUsedImages(imgs);
	}

	/**
	 * Gibt true zurück, wenn der angegebene Punkt innerhalb des Buttons liegt,
	 * sonst false.
	 * 
	 * @param x
	 *            Die x Position, mit der geprüft werden soll.
	 * @param y
	 *            Die y Position, mit der geprüft werden soll.
	 * @return 42
	 */
	public boolean contains(Point p) {
		if (p.x > getX()
				&& p.y > getY()
				&& p.y < getY() + getHeight()
				&& p.x < getX() + getUsedImages()[LEFT_NOMOUSE].getWidth()
						+ getUsedImages()[MID_NOMOUSE].getWidth()
						* getImageLength()
						+ getUsedImages()[RIGHT_NOMOUSE].getWidth()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the displayText
	 */
	public String getDisplayText() {
		return displayText;
	}

	/**
	 * @param displayText
	 *            the displayText to set
	 */
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	/**
	 * Gibt die Anzahl der Bilder zurück, die zum Ausfüllen des
	 * {@link displayText} notwendig sind.
	 * 
	 * @return the imageLength
	 */
	public int getImageLength() {
		return imageLength;
	}

	/**
	 * Legt die Anzahl der Bilder fest. Nicht empfohlen, da es von der
	 * Standardanzahl abweichen kann.
	 * 
	 * @param imageLength
	 *            the imageLength to set
	 */
	public void setImageLength(int imageLength) {
		this.imageLength = imageLength;
	}

	/**
	 * @return the fontHeight
	 */
	public int getFontHeight() {
		return fontHeight;
	}

	/**
	 * @param fontHeight
	 *            the fontHeight to set
	 */
	public void setFontHeight(int fontHeight) {
		this.fontHeight = fontHeight;
	}

	/**
	 * @return the font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * @param font
	 *            the font to set
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * @deprecated Funktionalität muss komplett überarbeitet werden.
	 * @return
	 */
	public BufferedImage[] getUsedImages() {
		return usedImages;
	}

	/**
	 * @deprecated Funktionalität muss komplett überarbeitet werden.
	 * @param images
	 */
	public void setUsedImages(BufferedImage[] images) {
		if (images.length != 12) {
			throw new IllegalArgumentException();
		} else {
			usedImages = images;
		}
	}

	public void draw(Graphics2D g) {

		// setzt die Buttonschriftart
		g.setFont(getFont());

		// Berechnung des Schrumpf-/Streckfaktors
		int factor = getUsedImages()[LEFT_NOMOUSE].getHeight() / getHeight();

		if (getStatus() == ComponentStatus.NO_MOUSE) {

			// Zeichnet den linken Abschnitt.
			g.drawImage(getUsedImages()[LEFT_NOMOUSE], getX(), getY(),
					getUsedImages()[LEFT_NOMOUSE].getWidth() / factor,
					getHeight(), null);

			// Zeichnet den mittleren Abschnitt.
			for (int i = 0; i < getImageLength(); i++) {

				g.drawImage(getUsedImages()[MID_NOMOUSE], getX()
						+ getUsedImages()[LEFT_NOMOUSE].getWidth()
						+ getUsedImages()[MID_NOMOUSE].getWidth() * i, getY(),
						getUsedImages()[MID_NOMOUSE].getWidth() / factor,
						getHeight(), null);

			}

			g.drawImage(getUsedImages()[RIGHT_NOMOUSE], getX()
					+ getUsedImages()[LEFT_NOMOUSE].getWidth()
					+ getUsedImages()[MID_NOMOUSE].getWidth()
					* getImageLength(), getY(),
					getUsedImages()[RIGHT_NOMOUSE].getWidth() / factor,
					getHeight(), null);

		} else if (getStatus().equals(ComponentStatus.MOUSE)) {

			// Zeichnet den linken Abschnitt.
			g.drawImage(getUsedImages()[LEFT_MOUSE], getX(), getY(),
					getUsedImages()[LEFT_MOUSE].getWidth() / factor,
					getHeight(), null);

			// Zeichnet den mittleren Abschnitt.
			for (int i = 0; i < getImageLength(); i++) {

				g.drawImage(getUsedImages()[MID_MOUSE], getX()
						+ getUsedImages()[LEFT_MOUSE].getWidth()
						+ getUsedImages()[MID_MOUSE].getWidth() * i, getY(),
						getUsedImages()[MID_MOUSE].getWidth() / factor,
						getHeight(), null);

			}

			g.drawImage(getUsedImages()[RIGHT_MOUSE], getX()
					+ getUsedImages()[LEFT_MOUSE].getWidth()
					+ getUsedImages()[MID_MOUSE].getWidth() * getImageLength(),
					getY(), getUsedImages()[RIGHT_MOUSE].getWidth() / factor,
					getHeight(), null);

		} else if (getStatus().equals(ComponentStatus.CLICK)) {

			// Zeichnet den linken Abschnitt.
			g.drawImage(getUsedImages()[LEFT_CLICK], getX(), getY(),
					getUsedImages()[LEFT_CLICK].getWidth(), getHeight(), null);

			// Zeichnet den mittleren Abschnitt.
			for (int i = 0; i < getImageLength(); i++) {

				g.drawImage(getUsedImages()[MID_CLICK], getX()
						+ getUsedImages()[LEFT_CLICK].getWidth()
						+ getUsedImages()[MID_CLICK].getWidth() * i, getY(),
						getUsedImages()[MID_CLICK].getWidth() / factor,
						getHeight(), null);

			}

			g.drawImage(getUsedImages()[RIGHT_CLICK], getX()
					+ getUsedImages()[LEFT_CLICK].getWidth()
					+ getUsedImages()[MID_CLICK].getWidth() * getImageLength(),
					getY(), getUsedImages()[RIGHT_CLICK].getWidth() / factor,
					getHeight(), null);

		} else {

			// Zeichnet den linken Abschnitt.
			g.drawImage(getUsedImages()[LEFT_NA], getX(), getY(),
					getUsedImages()[LEFT_NA].getWidth() / factor, getHeight(),
					null);

			// Zeichnet den mittleren Abschnitt.
			for (int i = 0; i < getImageLength(); i++) {

				g.drawImage(getUsedImages()[MID_NA], getX()
						+ getUsedImages()[LEFT_NA].getWidth()
						+ getUsedImages()[MID_NA].getWidth() * i, getY(),
						getUsedImages()[MID_NA].getWidth() / factor,
						getHeight(), null);

			}

			g.drawImage(getUsedImages()[RIGHT_NA], getX()
					+ getUsedImages()[LEFT_NA].getWidth()
					+ getUsedImages()[MID_NA].getWidth() * getImageLength(),
					getY(), getUsedImages()[RIGHT_NA].getWidth() / factor,
					getHeight(), null);

		}

		g.drawString(getDisplayText(),
				getX() + getUsedImages()[LEFT_MOUSE].getWidth(), getY()
						+ ((getHeight() - getFontHeight()) / 2));

	}

}
