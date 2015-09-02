package player.armour;

import player.weapon.ArmingType;

/**
 * Getter and Setter for defence/armour attributes: {@link player.weapon.ArmingType}
 * <p>defenceCutting</p>
 * <p>defenceStabbing</p>
 * <p>defenceMagic</p>
 */
public interface Defence {

    /** the value of protection the piece of arming provides against any cutting weapon (for example against clubs) */
    float getDefenceCutting ();

    /**  the value of protection the piece of arming provides against any stabbing weapon (for example against daggers) */
    float getDefenceStabbing ();

    /** the value of protection the piece of arming provides against any magic attack (including arrows)*/
    float getDefenceMagic ();

    /** sets the defence value against cutting weapons
     *
     * @param defenceCutting defence value against <code>ArmingType.CUTTING</code>
     * */
    void setDefenceCutting (float defenceCutting);

    /** sets the defence value against stabbing weapons
     *
     * @param defenceStabbing defence value against <code>ArmingType.STABBING</code>
     * */
    void setDefenceStabbing (float defenceStabbing);

    /** sets the defence value against magic
     *
     * @param defenceMagic defence value against <code>ArmingType.MAGIC</code>*/
    void setDefenceMagic (float defenceMagic);

    /** returns the defence value of the specified armingType */
    default float getDefence (ArmingType armingType) {
        switch (armingType) {
            case CUTTING: return getDefenceCutting();
            case STABBING: return getDefenceStabbing();
            case MAGIC: return getDefenceMagic();
            default: throw new IllegalArgumentException("Invalid ArmingType " + armingType);
        }
    }

    /** sets the defence value of the specified armingType */
    default void setDefence (float defenceValue, ArmingType armingType) {
        switch (armingType) {
            case CUTTING: setDefenceCutting(defenceValue); break;
            case STABBING: setDefenceStabbing(defenceValue); break;
            case MAGIC: setDefenceMagic(defenceValue); break;
            default: throw new IllegalArgumentException("Invalid ArmingType " + armingType);
        }
    }

    /** sets the defence value of every <code>ArmingType</code>. */
    default void setDefence (float defenceValue) {
        setDefenceCutting(defenceValue);
        setDefenceStabbing(defenceValue);
        setDefenceMagic(defenceValue);
    }

    /**
     * Returns the average defence meaning that full defence is divided through 3 ArmingTpys.
     *
     * @return <code>(getDefenceCutting() + getDefenceStabbing() + getDefenceMagic()) / 3f;</code>
     */
    default float getAverageDefence () {
        return (getDefenceCutting() + getDefenceStabbing() + getDefenceMagic()) / 3f;
    }
}
