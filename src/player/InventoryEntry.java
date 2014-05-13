package player;

import general.Mechanics;

import java.util.EmptyStackException;
import java.util.LinkedList;

import player.weapon.AbstractArrow;
import player.weapon.Item;

class InventoryEntry<T extends Item> {
	
	/**
	 * Die Standard-Anzahl der maximalen Gr��e des Stacks.
	 */
	private static final int DEFAULT_MAX_STACK = general.Mechanics.arrowNumberPreSet;
	
	/**
	 * Die einzelnen Items, die organisiert werden.
	 */
	private LinkedList<T> items = new LinkedList<T>();
	
	/**
	 * Die maximale Anzahl vom Item, die auf dem Stack liegen k�nnen.
	 * @see InventoryEntry#DEFAULT_MAX_STACK
	 */
	private int maxStack = DEFAULT_MAX_STACK;
	
	public InventoryEntry() {
	}
	
	/**
	 * Legt ein Item auf den Eintrag ab.
	 * @param item Das Item.
	 */
	public boolean push(T item) {
		if(hasRemainingSpace()) {
			items.add(item);
			gui.ArrowSelectionScreen.getInstance().updateInventoryList();
			if (item instanceof AbstractArrow)
				if (Mechanics.arrowNumberFreeSetUseable > 0) 
					return true;
				else {
					System.err.println("Could not Arrow: " + item.getClass() + " because of " + Mechanics.arrowNumberFreeSetUseable);
					return false;
				}
		}
		return false;
	}
	
	/**
	 * L�scht ein Item vom Stack und gibt dieses als R�ckgabewert
	 * dieser Funktion zur�ck. Diese Funktion kann auch null
	 * zur�ckgeben, wenn der Stack leer ist. In diesem Fall
	 * sollte das {@link InventoryEntry}-Objekt gar nicht mehr
	 * existieren.
	 * @return Ein Item vom definierten Typ, oder null, wenn der
	 * Stack bereits leer ist.
	 */
	public T pop() {
		try {
			return items.pop();
		} catch(EmptyStackException e) {
			return null;
		}
	}
	
	T peek() {
		return items.peek();
	}
	
	public int getMaximumStack() {
		return maxStack;
	}
	
	/**
	 * Setzt die Anzahl der maximal aufnehmbaren Eintr�ge neu.
	 * TODO Entfernt noch keine Items, die dann �berfl�ssig w�ren.
	 * @param maxStack Neue Anzahl.
	 */
	public void resize(int maxStack) {
		this.maxStack = maxStack;
	}
	
	/**
	 * Gibt den noch verf�gbaren Platz im Eintrag zur�ck.
	 * @return Den noch verf�gbaren Platz im Eintrag.
	 */
	public int getRemainingSpace() {
		return maxStack - items.size();
	}
	
	/**
	 * Sagt aus, ob dieser Inventareintrag noch Platz f�r weitere
	 * Items dieses Typs hat.
	 * @return
	 */
	public boolean hasRemainingSpace() {
		return getRemainingSpace() > 0;
	}
	
	/**
	 * Gibt die Anzahl der Items zur�ck, die sich in diesem Eintrag befinden.
	 * @return items.size()
	 */
	public int getItemCount() {
		return items.size();
	}
	
	public boolean isEmpty() {
		return maxStack - items.size() == maxStack;
	}
	
}
