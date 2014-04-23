package general;

import gui.GameScreen;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Josip
 * @version 24.02.14
 */
public class FieldInfoBox extends comp.Component {

	private static final Color BACKGROUND = new Color(0.0f, 0.0f, 0.0f, 0.3f);
	private static final Color OUTER = new Color(1.0f, 1.0f, 1.0f, 0.0f);

	private int insetX = 250, insetY = 175;

	private String queriedFieldType = "";

	public FieldInfoBox() {
		// standardmäßig ist der GameScreen für diese Component zuständig
		setBackingScreen(GameScreen.getInstance());
		// verhindere sofort, dass die InfoBox Input akzeptiert; sie soll ja auch keinen Input akzeptieren
		declineInput();
		// setze die Farben so, dass es ein bisschen dezenter wirkt
		getBorder().setInnerColor(BACKGROUND);
		getBorder().setOuterColor(OUTER);
		// positioniere die InfoBox so, dass sie am unteren rechten Bildschirmrand ist
		setX(Main.getWindowWidth() - insetX);
		setY(Main.getWindowHeight() - insetY);
		setWidth(insetX);
		setHeight(insetY);
	}

	/**
	 * Retrieves data from a field to display information about the field.
	 * @param field The field from which to retrieve the data.
	 */
	void retrieveData(Field field) {
		queriedFieldType = field.getFieldType();
		// TODO wenn noch weitere Informationen bezogen werden können, diese auch hineinschreiben
	}

	@Override
	public void draw(Graphics2D g) {

		if(g.getFont() != STD_FONT) {
			g.setFont(STD_FONT);
		}

		g.drawString("Field type: " + queriedFieldType,
				Main.getWindowWidth() - insetX + 20, Main.getWindowHeight() - insetY + 20);


	}
}
