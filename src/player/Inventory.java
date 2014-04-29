package player;

import java.util.LinkedList;

/**
 * Das Inventar einer Entity.
 * <p><b>13.2.2014</b></p>
 * <ul>
 *     <li>Anstelle von Combatant wird jetzt AttackContainer benutzt.</li>
 * </ul>
 * @author josip
 * @version 13.2.2014
 *
 */
public class Inventory {
	
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
	public boolean addItem(Item i) {
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
	public void addItem(Item i, int stackSize) {
		for (InventoryEntry e : items) {
			if(e.peek().getClass() == i.getClass()) {
				if(e.hasRemainingSpace()) {
					e.push(i);
					return;
				}
			}
		}
		// erstellt einen neuen Eintrag im Inventar, da sonst ja
		// kein Platz im Inventar ist
		InventoryEntry entry = new InventoryEntry();
		entry.resize(stackSize);
		entry.push(i);
		items.add(entry);
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
	
}
