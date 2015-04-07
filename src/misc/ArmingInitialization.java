package misc;

import general.LogFacility;
import player.weapon.arrow.ArrowHelper;

/**
 * Initializes <code>Weapon</code> and <code>Armour</code>. It is threaded with a low Priority, because Weapons and
 * Armours (apart from ArrowHelper) don't need to be fast at loading. The Images can also load later then an piece of armour
 * is first instantiated.
 *
 * @see misc.ItemInitialization
 */
public class ArmingInitialization {

    /** For further information see: {@link misc.ArmingInitialization} */
    public static void initialize () {
        Thread x = new Thread (() -> {
            initializeWeapon();
            LogFacility.log("Weapon images loaded.", "Info", "initprocess");

            initializeArmour();
            LogFacility.log("Armour images loaded.", "Info", "initprocess");
        });
        x.setDaemon(true);
        x.setPriority(2);
        x.start();
    }

    private static void initializeWeapon () {
        new ArrowHelper();
    }

    private static void initializeArmour () {

    }
}
