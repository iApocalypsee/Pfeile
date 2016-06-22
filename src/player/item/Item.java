package player.item;

import general.Main;
import java.awt.image.BufferedImage;

/** An empty item class for now.
 * Weapons are items (and Arrows therefore, too), but they are placed in the package <code>player.weapon</code>.
 * Loots are items, which are handled in the package item.
 */
public abstract class Item {

	private final String name;

    /** String <code>name</code> is the unique coding name, like "WaterArrow" in JAVA style. Don't confuse it with the "displayed" names,
     * which are displayed somewhere on the screen to be seen by the user. This need should NOT be seen by the user. */
	public Item(String codeName) {
		if(codeName == null)
            throw new NullPointerException();

		this.name = codeName;
	}

    /** That is the <b>unique coding name</b>, like "WaterArrow" in JAVA style. Don't confuse it with the "displayed" names,
     * which are displayed somewhere on the screen to be seen by the user. This need should NOT be seen by the user.
     *
     * @return the name for programming purposes
     */
	public String getName() {
		return name;
	}

    /**
     * Every item can be drawn, so it must have a BufferedImage. Override this call with
     * a link to the component or a loaded static BufferedImage.
     *
     * @return the {@link java.awt.image.BufferedImage} of the item
     */
    public abstract BufferedImage getImage();


    /** Returns the name of this item in the language, it is given in <code>Main.getLanguage()</code>.
     * this code is equal to <code>getNameDisplayed(Main.getLanguage())</code>. This name changes depending on the
     * language and is therefore not unique.
     *
     * @return the name of this item for the user
     */
    public String getNameDisplayed() {
        return Main.tr(getTranslationIdentifier());
    }

    /**
     * Returns the translation identifier for this item.
     * @return The translation identifier for this item.
     * @deprecated in a future commit, the method getTranslationIdentifier() will be removed. This method is completly
     *             useless, since every item must already have an name. Consequently you can identify every item by it's
     *             name, which must be unique. However, this property is already defined by the use as "name only for
     *             programming purposes". <code>getNameDisplayed()</code> will <code>return Main.tr(name);</code> soon.
     *             The JSON-classes must be changed though, but it's worth, because it's hard to handle three different
     *             names.
     */
    @Deprecated
    protected abstract String getTranslationIdentifier();

    /**
     * Use rather <code>getName()</code>, or <code>getNameDisplayed()</code> instead.
     * This method right now is the same as <code>getName()</code>, but it is not recommended to use this method.
     *
     * @return the unique coding name. Not the name that is displayed.
     */
    @Override
    public String toString () {
        return getName();
    }
}
