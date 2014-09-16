package player.weapon;

/**
 * Dynamisches Event-Handling von verschiedenen Items.
 * @author josip
 * @version 8.2.2014
 *
 */
@Deprecated
public interface ItemListener {
	
	void itemUsed(ItemUseEvent e);
	void itemPickedUp(ItemPickEvent e);
	void itemDropped(ItemDropEvent e);
	
}
