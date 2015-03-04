package player.armour;

/**
 * <code>LegArmour</code> provides a little amount of protection. It contains leg armour (Tasset or Tuille, Cuisse, ...)
 * and foot armour (Sabaton or Solleret). You can only equip one <code>LegArmour</code> for both legs and feet.
 */
public abstract class LegArmour extends Armour {

    /**
     * @param name         the name - part of <code>Item</code>
     * @param defenceValue the amount of protection provided by the piece of armour
     */
    public LegArmour (String name, float defenceValue) {
        super(name, defenceValue);
    }
}
