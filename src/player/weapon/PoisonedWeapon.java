package player.weapon;

/**
 * A Weapon can be poisoned. The maximum amount of damage the poison effect can deal is half of the poisoned amount, if
 * the enemy has an poison resistance stat of 0.
 */
interface PoisonedWeapon {

    /** Returns the amount of poison used to strengthen this weapon. The higher value, the more toxic is this Weapon */
    int getPoisonedAmount ();

    /** Sets the poison to the defined value. */
    void setPoisonedAmount (int amountOfPoison);

    /** Changes the amount of poison used to strengthen this weapon. */
    void changePoisonedAmount (int changeValue);

    /** Removes all poison effects */
    default void removePoisonedEffect () {
        setPoisonedAmount(0);
    }
}
