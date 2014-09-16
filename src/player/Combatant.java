package player;

import player.weapon.AttackContainer;
import player.weapon.AttackEvent;

/**
 * Represents a combatant which can carry items and other stuff.
 * @author Josip
 * @version 9.2.2014
 * @deprecated Not compatible with the new World classes.
 */
@Deprecated
public interface Combatant extends AttackContainer {

    /**
     * Lets the combatant attack.
     * @param event The event to fire.
     */
    void attack(AttackEvent event);

}
