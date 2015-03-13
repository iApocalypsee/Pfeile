package player.weapon;

import newent.Player;
import player.ArmingType;

/**
 * @author Josip Palavra
 * @version 26.07.2014
 */
public abstract class MeleeWeapon extends Weapon {

    /**
     * Creating a new Weapon.
     *
     * @param name            the name of the weapon
     * @param attackValue     the attack value of the weapon in the category <code>armingType</code>
     * @param armingType      the type of the weapon
     * @param defenceCutting  the additional defence against cutting weapons
     * @param defenceStabbing the additional defence against stabbing weapons
     * @param defenceMagic    the additional defence against magic (including arrows)
     */
    public MeleeWeapon (String name, float attackValue, ArmingType armingType, float defenceCutting, float defenceStabbing, float defenceMagic) {
        super(name, attackValue, armingType, defenceCutting, defenceStabbing, defenceMagic);
    }

    /**
     * creating a new weapon with the defence value <code>0</code> in every {@link player.ArmingType} category.
     *
     * @param name       the name of the weapon
     * @param attackVal  the attack value of the weapon in the category <code>armingType</code>
     * @param armingType the type of the weapon
     */
    public MeleeWeapon (String name, float attackVal, ArmingType armingType) {
        super(name, attackVal, armingType);
    }

    public abstract boolean equip (Player s);
}
