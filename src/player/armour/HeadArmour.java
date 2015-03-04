package player.armour;

/**
 * A Helmet provides a medium amount of protection.
 * The class is called <code>HeadArmour</code> because their are many of head armour. It contains helmets, neck armour
 * (like Aventails, Bevor, ...) and magic hats. You can only equip one piece of <code>HeadArmour</code> even if you can
 * choose between a helmet or an piece of neck armour.
 */
public abstract class HeadArmour extends Armour {

    /**
     * @param name         the name - part of <code>Item</code>
     * @param defenceValue the amount of protection provided by the piece of armour
     */
    public HeadArmour (String name, float defenceValue) {
        super(name, defenceValue);
    }
}
