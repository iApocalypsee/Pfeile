package player;

public class ItemPickEvent {
	
	/**
	 * Sagt aus, von wem das Item aufgehoben wird.
	 */
	private Combatant pickedUpBy;
	
	/**
	 * Das Item, das aufgehoben wird.
	 */
	private Arrow item;
	
	public ItemPickEvent(Combatant pickedUpBy, Arrow item) {
		this.pickedUpBy = pickedUpBy;
		this.item = item;
	}
	
	public Combatant getPickedUpBy() {
		return pickedUpBy;
	}
	
	public Arrow getItem() {
		return item;
	}
	
}
