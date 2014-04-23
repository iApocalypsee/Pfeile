package player;

/**
 * Represents an attack on an attack queue.
 * <p>2.3.2014</p>
 * <ul>
 *     <li>See update <code>2.3.2014</code> in {@link player.AttackEvent}.</li>
 * </ul>
 * 
 * @author Josip
 * @version 2.3.2014
 */
public class AttackQueue {

	/**
	 * The event with which the class {@link player.AttackQueue} is backed.
	 */
	private AttackEvent event;

	/**
	 * The current x position of the attack.
	 */
	private double currentX;

	/**
	 * The current y position of the attack.
	 */
	private double currentY;

	/**
	 * The total distance of the attack.
	 */
	private double distance;

	/**
	 * Creates a new attack queue and registers a new attack on the target.
	 * @param e The event to handle.
	 */
	public AttackQueue(AttackEvent e) {
		event = e;
		event.getTarget().registerAttack(this);
	}

	/**
	 * Returns the weapon with which the attack is drawn.
	 * @return The weapon with which the attack is drawn.
	 */
	public Weapon getWeapon() {
		return event.getWeapon();
	}

	/**
	 * Returns the target.
	 * @return The target of this attack.
	 */
	public AttackContainer getTarget() {
		return event.getTarget();
	}

	/**
	 * Returns the combatant who authorized the attack.
	 * @return The aggressor.
	 */
	public Combatant getAggressor() {
		return event.getAggressor();
	}

	/**
	 * Targets the attack queue to a new attack container.
	 * @param target The new target.
	 */
	public void target(AttackContainer target) {
		getTarget().unregisterAttack(this);
		event.setTarget(target);
		getTarget().registerAttack(this);
	}

	public double getCurrentX() {
		return currentX;
	}

	public double getCurrentY() {
		return currentY;
	}

	public double getDistance() {
		return distance;
	}

	/**
	 * TODO FIXME TODO TODO TODO TODO TODO
	 * Hier sollten currentX, currentY und distance geändert werden.
	 */
	public void calculateTurn() {
		long finalx = Math.round(currentX);
		long finaly = Math.round(currentY);

		if(getTarget().getBoardX() == finalx) {
			if(getTarget().getBoardY() == finaly) {
				// if the coordinates match, just calculate the attack
				// and jump out of the function
				// I don't need additional calculations
				impact();
				return;
			}
		}




	}

	/**
	 * Berechnet den Schaden, der durch den Angriff entsteht.
	 */
	void impact() {
		// TODO Schadensberechnung
        getTarget().unregisterAttack(this);
	}

}
