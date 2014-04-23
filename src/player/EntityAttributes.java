package player;

/**
 * Represents numerical attributes of an entity.
 * @author Josip
 * @version 03.03.14
 */
public class EntityAttributes {

	/**
	 * The current health points of the entity.
	 */
	private double healthPoints;

	/**
	 * The health regeneration per round.
	 */
	private double healthRegeneration;

	/**
	 * The maximum health of the entity.
	 */
	private double maximumHealth;

	/**
	 * The defense value of the entity. Used in combat situations.
	 */
	private double armor;

	/**
	 * The amount of movements per round.
	 */
	private int movements;

	/**
	 * The maximum amount of movements per round.
	 */
	private int maximumMovements;

	/**
	 * Creates a new instance of the EntityAttributes class.
	 * @param healthPoints The health points the entity has. This parameter will
	 *                     be assigned as maximum health, too.
	 * @param healthRegeneration The health regeneration of the entity per round.
	 * @param armor The armor value of the entity.
	 * @param movements The available movements per round for the entity.
	 */
	public EntityAttributes(double healthPoints, double healthRegeneration,
	                        double armor, int movements) {
		this.healthPoints = healthPoints;
		this.maximumHealth = healthPoints;
		this.healthRegeneration = healthRegeneration;
		this.armor = armor;
		this.movements = movements;
		this.maximumMovements = movements;
	}

	/**
	 * Returns the armor value for the entity.
	 * @return The armor value.
	 */
	public double getArmor() {
		return armor;
	}

	void setArmor(double armor) {
		this.armor = armor;
	}

	/**
	 * Returns the current health of the entity.
	 * @return The current health.
	 */
	public double getHealthPoints() {
		return healthPoints;
	}

	void setHealthPoints(double healthPoints) {
		this.healthPoints = healthPoints;
	}

	/**
	 * Returns the health regeneration per round for the entity.
	 * @return The health regeneration.
	 */
	public double getHealthRegeneration() {
		return healthRegeneration;
	}

	void setHealthRegeneration(double healthRegeneration) {
		this.healthRegeneration = healthRegeneration;
	}

	/**
	 * Returns the maximum health for the entity.
	 * @return The maximum health.
	 */
	public double getMaximumHealth() {
		return maximumHealth;
	}

	void setMaximumHealth(double maximumHealth) {
		this.maximumHealth = maximumHealth;
	}

	/**
	 * Returns the movements left.
	 * @return The movements.
	 */
	public int getMovements() {
		return movements;
	}

	void setMovements(int movements) {
		this.movements = movements;
	}

	/**
	 * Returns the maximum movements.
	 * @return The movements.
	 */
	public int getMaximumMovements() {
		return maximumMovements;
	}

	void setMaximumMovements(int maximumMovements) {
		this.maximumMovements = maximumMovements;
	}
}
