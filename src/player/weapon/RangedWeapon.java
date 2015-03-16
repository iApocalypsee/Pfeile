package player.weapon;

import player.ArmingType;

public abstract class RangedWeapon extends Weapon {
	
	/** der Grundwert der Reichweite der Waffe (ggf. innerhalb der ableitenden Klasse noch Wert 'rangeCurrent'*/
	private double range;

    /** the aim of of the RangedWeapon */
    private Aim aim;

    /**
     * <code>aim = new Aim(0, 0, 1)</code>
     * The attack will impact at (<code>0</code>|<code>0</code>) and has an damage radius of <code>1</code>. The range
     * of the attack is <code>1</code>.
     *
     * @param name the name of the weapon
     * @param attackValue the attack value
     * @param armingType the type
     */
    public RangedWeapon(String name, float attackValue, ArmingType armingType) {
        super(name, attackValue, armingType);
        range = 1;
        aim = new Aim(0, 0, 1);
    }

    /**
     *
     * @param name the name
     * @param attackValue the damage
     * @param armingType the type of the weapon
     * @param range the range of the weapon
     * @param damageRadius the damageRadius for {@link player.weapon.Aim}
     */
    public RangedWeapon(String name, float attackValue, ArmingType armingType, double range, double damageRadius) {
        this(name, attackValue, armingType, range, new Aim(0, 0, damageRadius));
    }

    /**
     *
     * @param name the name of the weapon
     * @param attackValue the damage
     * @param armingType the type of the weapon
     * @param range the range
     * @param aim {@link player.weapon.Aim}
     */
    public RangedWeapon(String name, float attackValue, ArmingType armingType, double range, Aim aim) {
        super(name, attackValue, armingType);
        this.range = range;
        this.aim = aim;
    }

    /** This abstract methods has to be overridden in the subclasses of {@link player.weapon.RangedWeapon}, because
     * every attack has a different distribution of damage. So rewrite this method by using {@link player.weapon.Aim}
     * with <code>getAim()</code>. This method is called in the <code>Delegate onImpact</code> of {@link newent.AttackContainer}
     * ({@link newent.LivingEntity} registers the damage function} <p>
     *     This method returns the damage at the tile (posX|posY), with the center of the attack at
     *     <code>getAim().getGridX(), getAim().getGridY()</code> within the borders of <code>getAim().getDamageRadius()</code>.
     *     <b>The damage is already multiplied with the damageMultiplier <code>PfeileContext.DAMAGE_MULTI</code> at
     *     {@link general.PfeileContext}. </b>
     *
     * @param posX the x-position of the tile, where the damage should be calculated
     * @param posY the y-position of the tile, where the attack impacts
     * @return the damage of the attack, at the tile (posX|posY).
     */
    public abstract double damageAt (int posX, int posY);
	
	/** Die Grundreichweite der Waffe (ggf. innerhalb der ableitenden Klasse noch Wert 'rangeCurrent') */
	public double getRange() {
		return range;
	}
	
	/** Die Grundreichweite der Waffe (ggf. innerhalb der ableitenden Klasse noch Wert 'rangeCurrent') */
	public void setRange(double range) {
		this.range = range;
	}

    /** returns the aim, that holds classifying information about the aim. Values can be changed there */
    public Aim getAim () {
        return aim;
    }

    /** The attack is aimed at the tile (posXGrid|posYGrid) with the damageRadius.
     * It sets the aim <code>getAim()</code>, so compare it with the constructor in aim
     *
     * @see player.weapon.RangedWeapon#aiming(Aim)
     * @param posXGrid x-position in tiles, where the attacks impacts
     * @param posYGrid y-position in tiles, where the attacks impacts
     * @param damageRadius the radius of the attack in tiles with the center (posXGrid, posYGrid)
     */
    public void aiming (int posXGrid, int posYGrid, double damageRadius) {
        this.aim = new Aim (posXGrid, posYGrid, damageRadius);
    }

    /**
     * The attack is aimed at (<code>posXGrid</code>|<code>posYGrid</code>).
     *
     * @see player.weapon.RangedWeapon#aiming(int, int, double)
     * @see player.weapon.RangedWeapon#aiming(Aim)
     * @param posXGrid x-position in tile, where the attack should impact now
     * @param posYGrid and the y-position
     */
    public void aimingAt (int posXGrid, int posYGrid) {
        aim.setGridX(posXGrid);
        aim.setGridY(posYGrid);
    }

    /** The attack is aimed at the new Aim.
     * Sets the {@link player.weapon.Aim}.
     *
     * @see player.weapon.RangedWeapon#aiming(int, int, double)
     * @param aim the new Aim.
     */
    public void aiming (Aim aim) {
        this.aim = aim;
    }
}
