package player;

import java.util.Date;

public class ItemUseEvent {
	
	private Arrow usedItem;
	private Combatant usedBy;
	private long when;
	
	public ItemUseEvent(Arrow item, Combatant user, long when) {
		usedItem = item;
		usedBy = user;
		this.when = when;
	}

	public Arrow getUsedItem() {
		return usedItem;
	}

	public Combatant getUsedBy() {
		return usedBy;
	}

	public long getWhen() {
		return when;
	}
	
	
	
}
