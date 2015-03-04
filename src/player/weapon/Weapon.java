package player.weapon;

import player.item.EquippableItem;

public abstract class Weapon extends EquippableItem {
	
	/**
	 * Der Angriffswert der Waffe.
	 */
	private float attackVal;
	
	/**
	 * Der Verteidigungswert der Waffe.
	 */
	private float defenseVal;

	public Weapon(String name) {
		super(name);
        attackVal = 0;
        defenseVal = 0;
	}

    public Weapon(String name, float attackValue, float defenseValue) {
        super(name);
        attackVal = attackValue;
        defenseVal = defenseValue;
    }

    /**
     * the defence value is 0.
     *
     * @param attackValue the damage a weapon makes
     * @param name the name of the weapon
     */
    public Weapon(String name, float attackValue) {
        super(name);
        attackVal = attackValue;
        defenseVal = 0;
    }
	
	public float getAttackValue() {
		return attackVal;
	}
	
	public void setAttackValue(float attackVal) {
		this.attackVal = attackVal;
	}
	
	public float getDefenseValue() {
		return defenseVal;
	}
	
	public void setDefenseValue(float defenseVal) {
		this.defenseVal = defenseVal;
	}
}
