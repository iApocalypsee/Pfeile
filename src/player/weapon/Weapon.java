package player.weapon;

import general.Main;
import newent.Combatant;
import newent.Entity;
import newent.InventoryEntity;
import newent.Player;
import player.item.Item;

public abstract class Weapon extends Item {
	
	/**
	 * Der Angriffswert der Waffe.
	 */
	private float attackVal;
	
	/**
	 * Der Verteidigungswert der Waffe.
	 */
	private float defenseVal;

	public Weapon(String name) {
		super(name);
	}
	
	public float getAttackValue() {
		return attackVal;
	}
	
	public void setAttackValue(float attackVal) {
		this.attackVal = attackVal;
	}
	
	public float getDefenseValue() {
		return defenseVal;
	}
	
	public void setDefenseValue(float defenseVal) {
		this.defenseVal = defenseVal;
	}

    /**
     * Equips the entity with the weapon. If the inventory of the player is full, the weapon cannot be equipped
     * and the method returns <code>false</code>. If the Entity cannot equip
     *
     * @param entity the entity which equips the weapon
     * @return <code>true</code> - if the weapon could be equipped
     */
    public abstract boolean equip (Player entity);

    /**
     * Equips the active player.
     *
     * @return the result of <code>equip(Main.getContext().getActivePlayer())</code>. Compare with {@link Weapon#equip(newent.Player)}
     * */
    public boolean equip () {
        return equip(Main.getContext().getActivePlayer());
    }
}
