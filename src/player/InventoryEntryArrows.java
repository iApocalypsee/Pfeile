package player;

import java.util.EmptyStackException;
import java.util.Stack;

class InventoryEntryArrows {
	
	/**
	 * Die Standard-Anzahl der maximalen Größe des Stacks.
	 */
	private static final int DEFAULT_MAX_STACK = 10;
	
	/**
	 * Die einzelnen Items, die organisiert werden.
	 */
	private Stack<Arrow> arrows = new Stack<Arrow>();
	
	/**
	 * Die maximale Anzahl vom Item, die auf dem Stack liegen können.
	 * @see InventoryEntryArrows#DEFAULT_MAX_STACK
	 */
	private int maxStack = DEFAULT_MAX_STACK;
	
	public InventoryEntryArrows() {
		arrows.setSize(maxStack);
	}
	
	/**
	 * Legt ein Item auf den Eintrag ab.
	 * @param item
	 */
	public boolean push(Arrow item) {
		if(hasRemainingSpace()) {
			arrows.add(item);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Löscht ein Item vom Stack und gibt dieses als Rückgabewert
	 * dieser Funktion zurück. Diese Funktion kann auch null
	 * zurückgeben, wenn der Stack leer ist. In diesem Fall
	 * sollte das {@link InventoryEntryArrows}-Objekt gar nicht mehr
	 * existieren.
	 * @return Ein Item vom definierten Typ, oder null, wenn der
	 * Stack bereits leer ist.
	 */
	public Arrow pop() {
		try {
			return arrows.pop();
		} catch(EmptyStackException e) {
			return null;
		}
	}
	
	Arrow peek() {
		return arrows.peek();
	}
	
	public int getMaximumStack() {
		return maxStack;
	}
	
	/**
	 * Setzt die Anzahl der maximal aufnehmbaren Einträge neu.
	 * @param maxStack
	 */
	public void resize(int maxStack) {
		this.maxStack = maxStack;
		arrows.setSize(maxStack);
	}
	
	/**
	 * Gibt den noch verfügbaren Platz im Eintrag zurück.
	 * @return
	 */
	public int getRemainingSpace() {
		return maxStack - arrows.size();
	}
	
	/**
	 * Sagt aus, ob dieser Inventareintrag noch Platz für weitere
	 * Items dieses Typs hat.
	 * @return
	 */
	public boolean hasRemainingSpace() {
		return maxStack - arrows.size() > 0;
	}
	
	/**
	 * Gibt die Anzahl der Items zurück, die sich in diesem Eintrag befinden.
	 * @return
	 */
	public int getItemCount() {
		return arrows.size();
	}
	
	public boolean isEmpty() {
		return maxStack - arrows.size() == maxStack;
	}
	
}
