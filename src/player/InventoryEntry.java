package player;

import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Stack;

class InventoryEntry<T extends Item> {
	
	/**
	 * Die Standard-Anzahl der maximalen Größe des Stacks.
	 */
	private static final int DEFAULT_MAX_STACK = 10;
	// FIXME Wenn das die Gr��e von Inventory.DEFAULT_INVENTORY_SIZE sein muss, dann m�sste es Mechanics.arrowNumberPreSet sein.
	
	/**
	 * Die einzelnen Items, die organisiert werden.
	 */
	private LinkedList<T> items = new LinkedList<T>();
	
	/**
	 * Die maximale Anzahl vom Item, die auf dem Stack liegen können.
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
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Löscht ein Item vom Stack und gibt dieses als Rückgabewert
	 * dieser Funktion zurück. Diese Funktion kann auch null
	 * zurückgeben, wenn der Stack leer ist. In diesem Fall
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
	 * Setzt die Anzahl der maximal aufnehmbaren Einträge neu.
	 * TODO Entfernt noch keine Items, die dann überflüssig wären.
	 * @param maxStack Neue Anzahl.
	 */
	public void resize(int maxStack) {
		this.maxStack = maxStack;
	}
	
	/**
	 * Gibt den noch verfügbaren Platz im Eintrag zurück.
	 * @return Den noch verfügbaren Platz im Eintrag.
	 */
	public int getRemainingSpace() {
		return maxStack - items.size();
	}
	
	/**
	 * Sagt aus, ob dieser Inventareintrag noch Platz für weitere
	 * Items dieses Typs hat.
	 * @return
	 */
	public boolean hasRemainingSpace() {
		return maxStack - items.size() > 0;
	}
	
	/**
	 * Gibt die Anzahl der Items zurück, die sich in diesem Eintrag befinden.
	 * @return
	 */
	public int getItemCount() {
		return items.size();
	}
	
	public boolean isEmpty() {
		return maxStack - items.size() == maxStack;
	}
	
}
