package player.item;

import java.awt.image.BufferedImage;

/** An empty item class for now.
 * Weapons are items (and Arrows therefore, too), but they are placed in the package <code>player.weapon</code>.
 * Loots are items, which are handled in the package item.
 */
public abstract class Item {

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

    public abstract BufferedImage getImage();

    @Override
    public String toString () {
        return getName() + " [@Item]";
    }
}
