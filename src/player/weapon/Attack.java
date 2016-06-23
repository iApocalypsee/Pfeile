package player.weapon;

/**
 *
 */
public class Attack {

    /**
     * The attack value of the weapon in the category armingType.
     */
    private float attackVal;

    /** Defining what kind of weapon it is and therefore defining what's best defence against it */
    private ArmingType armingType;

    public Attack (ArmingType type) {
        armingType = type;
    }
}
