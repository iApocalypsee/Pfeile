package general.field;

import comp.Component;
import general.Main;
import gui.GameScreen;

import java.awt.*;

/**
 * @author Josip
 * @version 24.02.14
 */
public class FieldInfoBox extends Component {

	private static final Color BACKGROUND = new Color(0.1f, 0.1f, 0.1f, 0.3f);
	private static final Color OUTER = new Color(1.0f, 1.0f, 1.0f, 0.0f);

	// TODO We need a language file for that!!!
	private static final String YES = "ja", NO = "nein";

	private int insetX = 0; 
	private int insetY = 0;

	private String queriedFieldType = "";
	private String queriedIsAccessible = "";

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
		setWidth(Component.getTextBounds("Feldtyp: Hügelebene", Component.STD_FONT).width + 10);
		setHeight(Component.getTextBounds("Feldtyp: Hügelebene", Component.STD_FONT).height * 2 + 15);
	}

	/**
	 * Retrieves data from a field to display information about the field.
	 * @param field The field from which to retrieve the data.
	 */
	void retrieveData(Field field) {
		queriedFieldType = field.getFieldType();
		queriedIsAccessible = field.isAccessable() ? YES : NO;

		// TODO wenn noch weitere Informationen bezogen werden können, diese auch hineinschreiben
	}

	@Override
	public void draw(Graphics2D g) {
		
		getBorder().draw(g);
		
		if(g.getFont() != STD_FONT) {
			g.setFont(STD_FONT);
		}
		g.setColor(Color.WHITE);
		g.drawString("Feldtyp: " + queriedFieldType,
				Main.getWindowWidth() - insetX + 10, Main.getWindowHeight() - insetY + 18);
		g.drawString("Zugänglich: " + queriedIsAccessible, Main.getWindowWidth() - insetX + 10, Main.getWindowHeight() - insetY + 18 + 18);
	}
	
	/** init Position */
	public void init () {
		insetX = Main.getWindowWidth() - GameScreen.getInstance().getWorld().getTimeLifeBox().getBoundingBox().x - 12;
		setX(Main.getWindowWidth() - insetX);
		insetY = Main.getWindowHeight() - GameScreen.getInstance().getWorld().getTimeLifeBox().getBoundingBox().y + 20 + getHeight();
		setY(Main.getWindowHeight() - insetY); 
	}
}
