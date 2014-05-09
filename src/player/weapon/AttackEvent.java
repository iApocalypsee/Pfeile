package player.weapon;

import player.Combatant;

/**
 * @author Josip
 * @version 16.2.2014
 */
public class AttackEvent {

	private int targetX;
	private int targetY;
	private Weapon weapon;
	private Combatant aggressor;

	/**
	 * Constructs an attack event.
	 * @param targetX The target x position.
	 * @param targetY The target y position.
	 * @param weapon The weapon used.
	 * @param aggressor The aggressor authorizing the attack.
	 */
	public AttackEvent(int targetX, int targetY, Weapon weapon, Combatant aggressor) {
		this.targetX = targetX;
		this.targetY = targetY;
		this.weapon = weapon;
		this.aggressor = aggressor;
	}

    public int getTargetX() {
	    return targetX;
    }

    public int getTargetY() {
	    return targetY;
    }

    /**
     * Returns the weapon. Delegate.
     * @return The weapon.
     */
    public Weapon getWeapon() {
	    return weapon;
    }

    void setTargetX(int targetX) {
	    this.targetX = targetX;
    }

    void setTargetY(int targetY) {
	    this.targetY = targetY;
    }

    public Combatant getAggressor() {
        return aggressor;
    }

}
