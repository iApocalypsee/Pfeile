package player.armour;

import player.item.EquippableItem;

/**
 * Every piece of armour, which physical (or magical) abilities serve protection (<code>getDefenceValue()</code>) and
 * can't attack enemies. Magical abilities may still increase the damage or provide other offensive skills.
 *
 * Further information of possible pieces of armour see at:
 * http://www.medievalwarfare.info/armour.htm
 */
public abstract class Armour extends EquippableItem {

    private float defenceValue;

    /**
     * @param name the name - part of <code>Item</code>
     * @param defenceValue the amount of protection provided by the piece of armour
     */
    public Armour (String name, float defenceValue) {
        super(name);
        this.defenceValue = defenceValue;
    }

    /** the value of protection the piece of arming provides */
    public float getDefenceValue () {
        return defenceValue;
    }

    /** Sets the value of protection */
    public void setDefenceValue (float defenceValue) {
        this.defenceValue = defenceValue;
    }
}
