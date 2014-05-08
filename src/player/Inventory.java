package player;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import general.Mechanics;
import gui.ArrowSelectionScreen;

import java.util.LinkedList;

/**
 * Repräsentiert das Inventar einer Entity.
 * @version 5.5.2014
 *
 */
public class Inventory {
	
	/**
	 * Die Standardgröße für das Inventar.
	 */
	private static final int DEFAULT_INVENTORY_SIZE = Mechanics.arrowNumberPreSet;
	//                                                Mechanics.arrowNumberPreSet + 1;
	// FIXME Das "+1" macht Sachen nur noch komplizierter und schwerer zu debuggen. 
	// Allerdings muss es minderstens Mechanics.arrowNumberPreSet sein, da sonst vieleicht das Grundinventar nicht hineinpassen w�rde
	/**
	 * Die Einträge in der Inventory.
	 */
	private LinkedList<InventoryEntry<?>> items = new LinkedList<InventoryEntry<?>>();
	
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
	 * ob das Inventar das Item aufnehmen konnte oder nicht.
	 * @param i Das Item, das hinzugefügt werden soll.
	 * @return <code>true</code>, wenn das Item hinzugefügt werden konnte.
	 */
	@SuppressWarnings("unchecked")
	public boolean addItem(Item i) {
		// first of all, a sort of checks
		if (i == null) {
			System.err.println("Inventory.addItem(Item):");
			System.err.println("\ti == null");
			return false;
		}
		if(getRemainingSpace() == 0) 
			return false;

		for (InventoryEntry e : items) {
			if(e.peek().getClass() == i.getClass()) {
				e.push(i);
				ArrowSelectionScreen.getInstance().updateInventoryList();
				return true;
			}
		}

		InventoryEntry newEntry = new InventoryEntry();
		newEntry.resize(i.getMaximumStackCount());
		newEntry.push(i);
		items.add(newEntry);
		ArrowSelectionScreen.getInstance().updateInventoryList();
		return true;
	}

	/**
	 * Fügt ein neues Item-Objekt zum Inventar basierend auf dessen Class-Objekt hinzu.
	 * @param clazz Die Klasse.
	 * @return <code>addItem(clazz.newInstance())</code>
	 */
	public boolean addItem(Class<? extends Item> clazz) {
		try {
			return addItem(clazz.newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Wird später entfernt.
	 * Registriert, wenn nötig, einen neuen Eintrag im Inventar der Entity
	 * und fügt das Item dem Inventar hinzu.
	 * @param i Das Item, das hinzugefügt werden soll.
	 * @param stackSize Die Größe des neuen Inventareintrags.
	 */
	@Deprecated
	public void addArrow(AbstractArrow i, int stackSize) {
		throw new NotImplementedException();
	}
	
	/**
	 * Entfernt das Item aus dem Inventar und den dazugehörigen Eintrag,
	 * falls die Anzahl der Einträge 0 sein sollte.
	 * @param i Das Item, das entfernt werden soll.
	 */
	public void removeItem(Class<? extends Item> i) {
		for (InventoryEntry e : items) {
			if(e.peek().getClass() == i) {
				e.pop();
				if(e.isEmpty()) {
					items.remove(e);
				}
				break;
			}
		}
		ArrowSelectionScreen.getInstance().updateInventoryList();
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

	/**
	 * Returns the amount of items that the inventory can hold.
	 * @return The amount of items that the inventory can hold.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns an integer representing how much space is in the inventory left
	 * to add new items.
	 * @return Amount of space left in the inventory.
	 */
	public int getRemainingSpace() {
		return size - items.size();
	}
}
