package player.weapon;

import java.util.LinkedList;

import player.Combatant;

public abstract class Item {
	
	/**
	 * Der Name des Items.
	 */
	private String name;
	
	/**
	 * Der Eigentümer des Items.
	 */
	private Combatant owner;
	
	/**
	 * Die Item-Listener.
	 */
	private LinkedList<ItemListener> itemListeners = new LinkedList<ItemListener>();
	
	public Item(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Gibt den Eigentümer des Items zurück, oder null, falls es keinem
	 * gehört.
	 * @return
	 */
	public Combatant getOwner() {
		return owner;
	}
	
	/**
	 * Setzt den Eigentümer des Items neu.
	 * @param owner
	 */
	public void setOwner(Combatant owner) {
		this.owner = owner;
	}
	
	/**
	 * Fügt einen {@link ItemListener} hinzu.
	 * @param l Der ItemListener.
	 */
	public void addItemListener(ItemListener l) {
		itemListeners.add(l);
	}
	
	/**
	 * Entfernt einen {@link ItemListener}.
	 * @param l Der ItemListener.
	 */
	public void removeItemListener(ItemListener l) {
		itemListeners.remove(l);
	}

    /**
     * Benutzt das Item und benachrichtigt die registrierten ItemListener.
     */
    public void use() {
        // instanciate the event
        ItemUseEvent event = new ItemUseEvent(this, owner, System.currentTimeMillis());
        // fire the event to the listeners
        for (ItemListener i : itemListeners) {
            i.itemUsed(event);
        }
    }

	/**
	 * Returns the maximum stack size of the item, meaning: This number
	 * represents how many items of this type can be stacked together on one
	 * stack in the inventory.
	 * @return The maximum stack size of the item.
	 */
	public abstract int getMaximumStackCount();

}
