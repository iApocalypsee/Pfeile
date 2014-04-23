package player;

/**
 * Represents an upcoming attack.
 * <p>2.3.2014</p>
 * <ul>
 *     <li>Eliminated possible buggy coding. Fields <code>targetX</code> and <code>targetY</code>
 *     do not exist anymore. Replaced by {@link player.AttackEvent#target}.</li>
 * </ul>
 * @author Josip
 * @version 2.3.2014
 */
public class AttackEvent {

	private AttackContainer target;
	private Weapon weapon;
	private Combatant aggressor;

	/**
	 * Constructs an attack event.
	 * @param target The target.
	 * @param weapon The weapon used.
	 * @param aggressor The aggressor authorizing the attack.
	 */
	public AttackEvent(AttackContainer target, Weapon weapon, Combatant aggressor) {
		this.target = target;
		this.weapon = weapon;
		this.aggressor = aggressor;
	}

	/**
	 * Returns the target.
	 * @return The target of this attack.
	 */
	public AttackContainer getTarget() {
		return target;
	}

    /**
     * Returns the weapon.
     * @return The weapon.
     */
    public Weapon getWeapon() {
	    return weapon;
    }

	/**
	 * Returns the combatant who authorized the attack.
	 * @return The aggressor.
	 */
    public Combatant getAggressor() {
        return aggressor;
    }

	/**
	 * Sets the target to a new one. Method is internally used when the attack
	 * is changing the direction.
	 * @param target The new target.
	 */
	void setTarget(AttackContainer target) {
		this.target = target;
	}
}
