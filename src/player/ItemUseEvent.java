package player;

import java.util.Date;

public class ItemUseEvent {
	
	private Item usedItem;
	private Combatant usedBy;
	private long when;
	
	public ItemUseEvent(Item item, Combatant user, long when) {
		usedItem = item;
		usedBy = user;
		this.when = when;
	}

	public Item getUsedItem() {
		return usedItem;
	}

	public Combatant getUsedBy() {
		return usedBy;
	}

	public long getWhen() {
		return when;
	}
	
	
	
}
