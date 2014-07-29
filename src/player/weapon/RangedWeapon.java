package player.weapon;

public abstract class RangedWeapon extends Weapon {
	
	/** der Grundwert der Reichweite der Waffe (ggf. innerhalb der ableitenden Klasse noch Wert 'rangeCurrent'*/
	private int range;

	public RangedWeapon(String name) {
		super(name);
	}
	
	/** Die Grundreichweite der Waffe (ggf. innerhalb der ableitenden Klasse noch Wert 'rangeCurrent') */
	public int getRange() {
		return range;
	}
	
	/** Die Grundreichweite der Waffe (ggf. innerhalb der ableitenden Klasse noch Wert 'rangeCurrent') */
	public void setRange(int range) {
		this.range = range;
	}

}
