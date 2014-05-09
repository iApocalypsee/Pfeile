package player.weapon;

public abstract class RangedWeapon extends Weapon {
	
	private int range;

	public RangedWeapon(String name) {
		super(name);
	}
	
	public int getRange() {
		return range;
	}
	
	public void setRange(int range) {
		this.range = range;
	}

}
