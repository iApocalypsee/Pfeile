package player.item;

import general.Main;
import general.langsupport.Language;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.image.BufferedImage;

/** An empty item class for now.
 * Weapons are items (and Arrows therefore, too), but they are placed in the package <code>player.weapon</code>.
 * Loots are items, which are handled in the package item.
 */
public abstract class Item {

	private String name;

    /** String <code>name</code> is the unique coding name, like "WaterArrow" in JAVA style. Don't confuse it with the "displayed" names,
     * which are displayed somewhere on the screen to be seen by the user. This need should NOT be seen by the user. */
	public Item(String codeName) {
		if(codeName == null) throw new NullPointerException();
		this.name = codeName;
	}

    /** That is the unique coding name, like "WaterArrow" in JAVA style. Don't confuse it with the "displayed" names,
     * which are displayed somewhere on the screen to be seen by the user. This need should NOT be seen by the user.
     *
     * @return the name for programming purposes
     */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    /**
     * Every item can be drawn, so it must have a BufferedImage. Override this call with
     * a link to the component or a loaded static BufferedImage.
     *
     * @return the {@link java.awt.image.BufferedImage} of the item
     */
    public abstract BufferedImage getImage();


    /** Returns the name of this item in the language, it is given in <code>Main.getLanguage()</code>.
     * this code is equal to <code>getNameDisplayed(Main.getLanguage())</code>
     *
     * @return the name of this item for the user
     */
    public String getNameDisplayed() {
        return Main.tr(getTranslationIdentifier());
    }

    /**
     * Returns the translation identifier for this item.
     * @return The translation identifier for this item.
     */
    protected abstract String getTranslationIdentifier();

    @Override
    public String toString () {
        return getName();
    }
}
