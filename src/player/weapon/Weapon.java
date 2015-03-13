package player.weapon;

import player.ArmingType;
import player.armour.Defence;
import player.item.EquippableItem;

public abstract class Weapon extends EquippableItem implements Defence {
	
	/**
	 * The attack value of the weapon in the category armingType.
	 */
	private float attackVal;

    /** Defining what kind of weapon it is and therefore defining what's best defence against it */
    private ArmingType armingType;

    private float defenceCutting;
    private float defenceStabbing;
    private float defenceMagic;

    /**
     * creating a new weapon with the defence value <code>0</code> in every {@link player.ArmingType} category.
     *
     * @param name the name of the weapon
     * @param attackVal the attack value of the weapon in the category <code>armingType</code>
     * @param armingType the type of the weapon
     */
	public Weapon(String name, float attackVal, ArmingType armingType) {
		this(name, attackVal, armingType, 0, 0, 0);
	}

    /**
     * Creating a new Weapon.
     *
     * @param name the name of the weapon
     * @param attackValue the attack value of the weapon in the category <code>armingType</code>
     * @param armingType the type of the weapon
     * @param defenceCutting the additional defence against cutting weapons
     * @param defenceStabbing the additional defence against stabbing weapons
     * @param defenceMagic the additional defence against magic (including arrows)
     */
    public Weapon(String name, float attackValue, ArmingType armingType, float defenceCutting, float defenceStabbing, float defenceMagic) {
        super(name);
        this.attackVal = attackValue;
        this.armingType = armingType;
        this.defenceCutting = defenceCutting;
        this.defenceStabbing = defenceStabbing;
        this.defenceMagic = defenceMagic;
    }

    /** the attack value of the weapon in the category {@link Weapon#getArmingType()}*/
	public float getAttackValue() {
		return attackVal;
	}

    /** Sets the attack value */
	public void setAttackValue(float attackVal) {
		this.attackVal = attackVal;
	}

    /** the type of the weapon */
    public ArmingType getArmingType () {
        return armingType;
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
     * @param defenceCutting defence value against <code>ArmingType.CUTTING</code>
     */
    @Override
    public void setDefenceCutting (float defenceCutting) {
        this.defenceCutting = defenceCutting;
    }

    /**
     * sets the defence value against stabbing weapons
     *
     * @param defenceStabbing defence value against <code>ArmingType.STABBING</code>
     */
    @Override
    public void setDefenceStabbing (float defenceStabbing) {
        this.defenceStabbing = defenceStabbing;
    }

    /**
     * sets the defence value against magic
     *
     * @param defenceMagic defence value against <code>ArmingType.MAGIC</code>
     */
    @Override
    public void setDefenceMagic (float defenceMagic) {
        this.defenceMagic = defenceMagic;
    }
}
