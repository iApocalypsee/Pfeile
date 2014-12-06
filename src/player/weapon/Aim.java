package player.weapon;


import player.BoardPositionable;

/** The Aim is used by every {@link player.weapon.RangedWeapon} to hold the information of the aim,
 * in that the attack impacts. */
public class Aim implements BoardPositionable {

    /** the x-position of the aim in tiles */
    private int posXGrid;

    /** the y-position of the aim in tiles */
    private int posYGrid;


    /** the radius of the attack, in which the attack effects */
    private double damageRadius;

    /** Creating a new Aim object.
     *
     * @param posXGrid the x-position of the aim in tiles
     * @param posYGrid the y-position of the aim in tiles
     * @param damageRadius the radius the attack in tiles
     */
    public Aim (int posXGrid, int posYGrid, double damageRadius) {
        this.posXGrid = posXGrid;
        this.posYGrid = posYGrid;
        this.damageRadius = damageRadius;
    }

    /** the x-coordinate of the aim in tiles */
    public void setGridX (int posXGrid) {
        this.posXGrid = posXGrid;
    }

    /** the y-coordinate of the aim in tiles */
    public void setGridY (int posYGrid) {
        this.posYGrid = posYGrid;
    }

    /** the x-coordinate of the aim in tiles */
    @Override
    public int getGridX () {
        return posXGrid;
    }

    /** the y-coordinate of the aim in tiles */
    @Override
    public int getGridY () {
        return posYGrid;
    }

    /** The radius of the attack, in which the attacks effects the surrounding area. <p>
     * That doesn't mean that the damage of {@link player.weapon.RangedWeapon} is in the whole area the same.
     * Maximum damage is made in the center (the tile at <code>getGridX(), getGridY()</code>).
     * <p>
     *     The radius is stated in tiles. */
    public double getDamageRadius () {
        return damageRadius;
    }

    /** changes the radius of the attack, in which the attacks effects the surrounding area. <p>
     * That doesn't mean that the damage of {@link player.weapon.RangedWeapon} is in the whole area the same.
     * Maximum damage is made in the center (the tile at <code>getGridX(), getGridY()</code>).
     * <p>
     *     the radius is stated in tiles. */
    public void setDamageRadius (double damageRadius) {
        this.damageRadius = damageRadius;
    }
}
