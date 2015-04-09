package player.item;

import comp.ImageComponent;
import general.Main;
import newent.Combatant;
import newent.Player;

import java.awt.image.BufferedImage;

/**
 * Every item, which can be quipped (overrides method equip), inherits from this class.
 */
public abstract class EquippableItem extends Item {

    public EquippableItem (String name) {
        super(name);
    }

    /**
     * Equips the entity with the weapon. If the inventory of the player is full, the weapon cannot be equipped
     * and the method returns <code>false</code>. If the Entity cannot equip
     *
     * @param combatant the entity which equips the weapon
     * @return <code>true</code> - if the weapon could be equipped
     */
    public abstract boolean equip (Combatant combatant);

    /**
     * Equips the active player.
     *
     * @return the result of <code>equip(Main.getContext().getActivePlayer())</code>. Compare with {@link player.item.EquippableItem#equip(newent.Combatant)}
     * */
    public boolean equip () {
        return equip(Main.getContext().getActivePlayer());
    }
}
