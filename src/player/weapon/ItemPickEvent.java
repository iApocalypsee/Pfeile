package player.weapon;

import player.Combatant;

public class ItemPickEvent {
	
	/**
	 * Sagt aus, von wem das Item aufgehoben wird.
	 */
	private Combatant pickedUpBy;
	
	/**
	 * Das Item, das aufgehoben wird.
	 */
	private Item item;
	
	public ItemPickEvent(Combatant pickedUpBy, Item item) {
		this.pickedUpBy = pickedUpBy;
		this.item = item;
	}
	
	public Combatant getPickedUpBy() {
		return pickedUpBy;
	}
	
	public Item getItem() {
		return item;
	}
	
}
