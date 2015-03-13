package player.armour;

import player.item.EquippableItem;

/**
 * Every piece of armour, which physical (or magical) abilities serve protection (<code>getDefenceValue()</code>) and
 * can't attack enemies. Magical abilities may still increase the damage or provide other offensive skills.
 *
 * <p>Further information of possible pieces of armour see at:
 * See <a href="http://www.medievalwarfare.info/armour.htm">http://www.medievalwarfare.info/armour.htm</a>
 */
public abstract class Armour extends EquippableItem implements Defence {
    private float defenceCutting;
    private float defenceStabbing;
    private float defenceMagic;

    /**
     * @param name the name - part of <code>Item</code>
     * @param defenceCutting the amount of protection provided by the piece of armour against cutting weapons
     * @param defenceStabbing the amount of protection against stabbing weapons
     * @param defenceMagic amount of protection against magic
     */
    public Armour (String name, float defenceCutting, float defenceStabbing, float defenceMagic) {
        super(name);
        this.defenceCutting = defenceCutting;
        this.defenceStabbing = defenceStabbing;
        this.defenceMagic = defenceMagic;
    }

    /**
     * the value of protection the piece of arming provides against any cutting weapon (for example against clubs)
     */
    @Override
    public float getDefenceCutting () {
        return defenceCutting;
    }

    /**
     * the value of protection the piece of arming provides against any stabbing weapon (for example against daggers)
     */
    @Override
    public float getDefenceStabbing () {
        return defenceStabbing;
    }

    /**
     * the value of protection the piece of arming provides against any magic attack (including arrows)
     */
    @Override
    public float getDefenceMagic () {
        return defenceMagic;
    }

    /**
     * sets the defence value against cutting weapons
     *
     * @param defenceCutting ArmingType.CUTTING
     */
    @Override
    public void setDefenceCutting (float defenceCutting) {
        this.defenceCutting = defenceCutting;
    }

    /**
     * sets the defence value against stabbing weapons
     *
     * @param defenceStabbing ArmingType.STABBING
     */
    @Override
    public void setDefenceStabbing (float defenceStabbing) {
        this.defenceStabbing = defenceStabbing;
    }

    /**
     * sets the defence value against magic
     *
     * @param defenceMagic ArmingType.MAGIC
     */
    @Override
    public void setDefenceMagic (float defenceMagic) {
        this.defenceMagic = defenceMagic;
    }
}
