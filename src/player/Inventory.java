package player;

import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Das Inventar einer Entity.
 * <p>31.3.2014</p>
 * <ul>
 *     <li>Die {@link player.Inventory.InventoryEntry}-Klasse wurde in die Klasse
 *     Inventory verschoben.</li>
 * </ul>
 * @author josip
 * @version 13.2.2014
 *
 */
public class Inventory<T extends Item> {
	
	/**
	 * Die Standardgröße für das Inventar.
	 */
	private static final int DEFAULT_INVENTORY_SIZE = 20;
	
	/**
	 * Die Einträge in der Inventory.
	 */
	private LinkedList<InventoryEntry> items = new LinkedList<InventoryEntry>();
	
	/**
	 * Die Entity, die dieses Inventar trägt.
	 */
	private AttackContainer carrier;
	
	/**
	 * Die maximale Größe des Inventars.
	 */
	private int size = DEFAULT_INVENTORY_SIZE;
	
	/**
	 * Erstellt ein Inventar mit dem angegebenen Träger des Inventars,
	 * der aus diesem dann Items beziehen und benutzen kann.
	 * @param carrier Der Träger des Inventars.
	 */
	public Inventory(AttackContainer carrier) {
		this.carrier = carrier;
	}
	
	/**
	 * Fügt ein Item dem Inventar hinzu und gibt einen boolean-Wert raus,
	 * ob das Inventar das Item aufnehmen konnte oder nicht. Wenn diese Methode
	 * <code>false</code> zurückgibt, versuchs mit {@link #addItem(Item, int)}.
	 * Diese Methode erstellt keine neuen Einträge, sondern versucht, das Item
	 * den bestehenden Einträgen zuzuteilen.
	 * @param i Das Item, das hinzugefügt werden soll.
	 * @return <code>true</code>, wenn das Item hinzugefügt werden konnte.
	 */
	public boolean addItem(T i) {
		for (InventoryEntry e : items) {
			if(e.peek().getClass() == i.getClass()) {
				if(e.hasRemainingSpace()) {
					e.push(i);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Registriert, wenn nötig, einen neuen Eintrag im Inventar der Entity
	 * und fügt das Item dem Inventar hinzu.
	 * @param i Das Item, das hinzugefügt werden soll.
	 * @param stackSize Die Größe des neuen Inventareintrags. Wird ignoriert, wenn
	 * {@link Inventory#addItem(Item)} für den Parameter i true zurückgeben würde.
	 */
	public void addItem(T i, int stackSize) {
		for (InventoryEntry e : items) {
			if(e.peek().getClass() == i.getClass()) {
				if(e.hasRemainingSpace()) {
					e.push(i);
					return;
				}
			}
		}// TODO
		// erstellt einen neuen Eintrag im Inventar, da sonst ja
		// kein Platz im Inventar ist
		InventoryEntry<T> entry = new InventoryEntry<T>();
		entry.resize(stackSize);
		entry.push(i);
		items.add(entry);
	}
	
	/**
	 * Entfernt das Item aus dem Inventar und den dazugehörigen Eintrag,
	 * falls die Anzahl der Einträge 0 sein sollte.
	 * @param i Das Item, das entfernt werden soll.
	 */
	public void removeItem(Class<? extends Item> i, int amount) throws OutOfItemsException {
		for (InventoryEntry e : items) {
			if(e.peek().getClass() == i) {
				for(int it = 0; it < amount; it++) {
					if(!contains(i)) {
						throw new OutOfItemsException();
					}

					if(e.isEmpty()) {
						items.remove(e);
						break;
					}

					e.pop();

				}
			}
		}
	}
	
	public boolean contains(Class<? extends Item> i) {
		for (InventoryEntry e : items) {
			if(e.peek().getClass() == i) {
				return true;
			}
		}
		return false;
	}

    /**
     * Returns the amount of specified item type in the inventory. The returned
     * value can be zero, but never less than 0.
     * @param i The type of item to look for.
     * @return The amount of specified item type in the inventory.
     */
	public int getItemCount(Class<? extends Item> i) {
		int x = 0;
		for (InventoryEntry e : items) {
			if(e.peek().getClass() == i) {
				x += e.getItemCount();
			}
		}
		return x;
	}

    /**
     * Returns the carrier of this inventory.
     * @return The carrier of this inventory.
     */
	public AttackContainer getCarrier() {
		return carrier;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	static class InventoryEntry<T extends Item> {

		/**
		 * Die Standard-Anzahl der maximalen Größe des Stacks.
		 */
		private static final int DEFAULT_MAX_STACK = 10;

		/**
		 * Die einzelnen Items, die organisiert werden.
		 */
		private Stack<T> items = new Stack<T>();

		/**
		 * Die maximale Anzahl vom Item, die auf dem Stack liegen können.
		 * @see player.Inventory.InventoryEntry#DEFAULT_MAX_STACK
		 */
		private int maxStack = DEFAULT_MAX_STACK;

		public InventoryEntry() {
			items.setSize(maxStack);
		}

		/**
		 * Legt ein Item auf den Eintrag ab.
		 * @param item
		 */
		public void push(T item) {
			items.add(item);
		}

		/**
		 * Löscht ein Item vom Stack und gibt dieses als Rückgabewert
		 * dieser Funktion zurück. Diese Funktion kann auch null
		 * zurückgeben, wenn der Stack leer ist. In diesem Fall
		 * sollte das {@link player.Inventory.InventoryEntry}-Objekt gar nicht mehr
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
		 * @param maxStack
		 */
		public void resize(int maxStack) {
			this.maxStack = maxStack;
			items.setSize(maxStack);
		}

		/**
		 * Gibt den noch verfügbaren Platz im Eintrag zurück.
		 * @return
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

	public static class OutOfItemsException extends Throwable {
	}
}
