package comp;

/**
 * @author Josip
 * @version 29.05.2014
 */
public class GUIUpdateEvent {

	int delta;

	public GUIUpdateEvent(int delta) {
		this.delta = delta;
	}

	public int getDelta() {
		return delta;
	}
}
