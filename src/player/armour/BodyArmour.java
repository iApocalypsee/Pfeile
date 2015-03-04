package player.armour;

/**
 * The most important piece of defence: the breast armour (like Brigandines, Cuirasses,...).
 */
public abstract class BodyArmour extends Armour {

    /**
     * @param name         the name - part of <code>Item</code>
     * @param defenceValue the amount of protection provided by the piece of armour
     */
    public BodyArmour (String name, float defenceValue) {
        super(name, defenceValue);
    }
}
