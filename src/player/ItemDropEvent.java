package player;

import java.awt.Point;

public class ItemDropEvent {
	
	/**
	 * Die Entity, die das Item droppt.
	 */
	private Combatant droppedBy;
	
	/**
	 * Das Item, das gedroppt wird.
	 */
	private Arrow item;
	
	private int x;
	private int y;
	
	public ItemDropEvent(Combatant droppedBy, Arrow item, int dropX, int dropY) {
		this.droppedBy = droppedBy;
		this.item = item;
		x = dropX;
		y = dropY;
	}
	
	public Combatant getDroppedBy() {
		return droppedBy;
	}
	
	public Arrow getItem() {
		return item;
	}
	
	public Point getDropLocation() {
		return new Point(x, y);
	}
	
}
