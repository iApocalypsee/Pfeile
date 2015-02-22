package player.weapon;

import newent.Player;

/**
 * @author Josip Palavra
 * @version 26.07.2014
 */
public abstract class MeleeWeapon extends Weapon {
	public MeleeWeapon(String name) {
		super(name);
	}

    public abstract boolean equip (Player s);
}
