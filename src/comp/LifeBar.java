package comp;

import gui.GameScreen;
import player.Entity;

import java.awt.*;

/**
 * TODO
 * @author Josip
 * @version 03.03.14
 */
public class LifeBar extends Component {

	private Entity entity = null;

	public LifeBar() {
		entity = GameScreen.getInstance().getWorld().getActivePlayer();
	}

	@Override
	public void draw(Graphics2D g) {

	}
}
