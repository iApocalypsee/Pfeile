package player.armour;

/**
 * Provides a little amount of protection for the arm (braces, vambraces, ...) your shoulder (spaulders, pauldrons,...)
 * or your hand (gauntlets).
 */
public abstract class ArmArmour extends DefenceArming {

    /**
     * @param name         the name - part of <code>Item</code>
     * @param defenceValue the amount of protection provided by the piece of armour
     */
    public ArmArmour (String name, float defenceValue) {
        super(name, defenceValue);
    }
}
