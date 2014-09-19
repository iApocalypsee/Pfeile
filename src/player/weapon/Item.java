package player.weapon;

/** An empty item class for now.
 *
 */
public class Item {

	private String name;

	public Item(String name) {
		if(name == null) throw new NullPointerException();
		this.name = name;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
