package player.weapon;

import player.BoardPositionable;
import player.Combatant;

import com.sun.istack.internal.Nullable;

/**
 * RegisterAttackEvent und {@link wepeon.github.pfeile.player.AttackEvent} sind dasselbe...
 * @author Josip
 * @version 16.2.2014
 */
public interface AttackContainer extends BoardPositionable {

    /**
     * Registers a new attack on the combatant.
     * @param event The attack to register.
     */
    void registerAttack(AttackEvent event);

    /**
     * Registers an already instanciated attack queue.
     * @param queue The queue to register.
     */
    void registerAttack(AttackQueue queue);

    /**
     * Unregisters an attack on the combatant.
     * @param w The weapon used by the attack to unregister.
     */
    void unregisterAttack(Class<? extends Weapon> w);

    /**
     * Unregisters a defined instance of attack-queue.
     * @param queue The queue object to remove from the combatant's incoming
     *              attacks.
     */
    void unregisterAttack(AttackQueue queue);

    /**
     * Returns <code>true</code> if the combatant is being attacked.
     * @return <code>true</code> if the combatant is being attacked.
     */
    boolean isAttacked();

    /**
     * Returns <code>true</code> if the combatant is being attacked with.
     * @param w The weapon to check.
     * @return <code>true</code> if the combatant is being attacked with.
     */
    boolean isAttackedBy(Class<? extends Weapon> w);

    /**
     * Returns <code>true</code> if the attack container is being attacked
     * by a combatant.
     * @param combatant The combatant from whom to check if he is attacking.
     * @return <code>true</code> if the attack container is being attacked
     * by the combatant.
     */
    boolean isAttackedBy(Combatant combatant);

    /**
     * Returns all attack queues on this attack container which have specified
     * weapon. If no attack queue matches with specified weapon, <code>null</code>
     * is returned.
     * @param aWeapon The weapon.
     * @return All attack queues matching with the weapon, or <code>null</code>
     * if nothing matches.
     */
    @Nullable
    AttackQueue[] getAttackQueuesBy(Class<? extends Weapon> aWeapon);

    /**
     * Returns all attack queues on this attack container which have specified
     * aggressor. If no attack queue matches with specified combatant, <code>null</code>
     * is returned.
     * @param aggressor The combatant from whom to check.
     * @return All attack queues matching with the combatant, or <code>null</code>
     * if nothing matches.
     */
    @Nullable
    AttackQueue[] getAttackQueuesBy(Combatant aggressor);

}
