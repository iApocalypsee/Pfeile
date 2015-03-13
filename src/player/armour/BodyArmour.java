package player.armour;

/**
 * The most important piece of defence: the breast armour (like Brigandines, Cuirasses,...).
 */
public abstract class BodyArmour extends Armour {

    /**
     * @param name            the name - part of <code>Item</code>
     * @param defenceCutting  the amount of protection provided by the piece of armour against cutting weapons
     * @param defenceStabbing the amount of protection against stabbing weapons
     * @param defenceMagic    amount of protection against magic
     */
    public BodyArmour (String name, float defenceCutting, float defenceStabbing, float defenceMagic) {
        super(name, defenceCutting, defenceStabbing, defenceMagic);
    }
}
