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
    public static Thread initialize () {
        Thread returnThread = initializeArrows();
        returnThread.setDaemon(true);
        returnThread.setPriority(Thread.MAX_PRIORITY);
        returnThread.start();

        Thread x = new Thread (() -> {
            initializeWeapon();
            LogFacility.log("Weapon images loaded.", "Info", "initprocess");

            initializeArmour();
            LogFacility.log("Armour images loaded.", "Info", "initprocess");
        });
        x.setDaemon(true);
        x.setName("Arming Initialization");
        x.setPriority(1);
        x.start();

        return returnThread;
    }

    private static Thread initializeArrows () {
          return new Thread (ArrowHelper::new, "Arrow Initialization");
    }

    private static void initializeWeapon () {

    }

    private static void initializeArmour () {

    }
}
