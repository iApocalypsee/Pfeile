package player.weapon;

import player.Combatant;
import player.Entity;
import general.field.Field;

/**
 * Represents an attack on a conbatant.
 * <p>Beim Auftreffen des Angriffs wird jetzt der Angriff ausgeloggt.</p>
 * 
 * @author Josip
 * @version 9.2.2014
 */
public class AttackQueue {

	/**
	 * The combatant who autorised the attack.
	 */
	private Combatant aggressor;

	/**
	 * The type of weapon with which the {@link #aggressor} attacks.
	 */
	private AttackEvent event;

	/**
	 * Reference to the attacked container.
	 */
	private AttackContainer target;

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

	public AttackQueue(AttackEvent e) {
		aggressor = e.getAggressor();
		event = e;
		target.registerAttack(this);
	}

	/**
	 * @return Den Spielerindex, der den Pfeil geschossen hat.
	 */
	public Combatant getAggressor() {
		return this.aggressor;
	}

	/**
	 * Returns the weapon with which the attack is drawn.
	 * @return The weapon with which the attack is drawn.
	 */
	public Weapon getWeapon() {
		return event.getWeapon();
	}

	/**
	 * @return the targetField
	 */
	public AttackContainer getTarget() {
		return target;
	}

	/**
	 * Changes the target to an entity.
	 * @param entity The entity to attack.
	 */
	public void varyTarget(Entity entity) {
		event.setTargetX(entity.getBoardX());
		event.setTargetY(entity.getBoardY());
		target.unregisterAttack(this);
		entity.registerAttack(this);
		target = entity;
	}

	/**
	 * Changes the target to a field.
	 * @param field The field to attack.
	 */
	public void varyTarget(Field field) {
		event.setTargetX(field.getBoardX());
		event.setTargetY(field.getBoardY());
		target.unregisterAttack(this);
		field.registerAttack(this);
		target = field;
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

		if(target.getBoardX() == finalx) {
			if(target.getBoardY() == finaly) {
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
        target.unregisterAttack(this);
	}

}
