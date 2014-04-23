package player;

public abstract class Weapon extends Item {
	
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
