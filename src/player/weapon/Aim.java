package player.weapon;


import general.Main;
import player.BoardPositionable;
import world.IsometricPolygonTile;
import world.TileLike;

import java.awt.*;

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

    /** this calculates the Position of the center of the tile(<code>getGridX(), getGridY()</code>) for the Gui.
     * Save this value, if you need to this method to draw something, because getPositionGui() isn't a standard getter,
     * it first has to get the attackedTile and than it is able to return the point.
     *
     * @return the center of the attacked Tile
     */
     public Point getPositionGui () {
        TileLike tile = (TileLike) Main.getContext().getWorld().terrain().tileAt(getGridX(), getGridY());
        Rectangle rect = tile.getComponent().getBounds().getBounds();
        return new Point((int) rect.getCenterX(), (int) rect.getCenterY());
    }

    /** <code>return attackedTile.getComponent().getBounds()</code>. If you need this method for any drawing process (i.e. called frequently),
     * you should save this value, as the attackedTile isn't saved in Aim (it is returned after a longer getter call).
     *
     * @return the bounds of the Tile at (<code>getAim().getGridX()</code>|<code>getAim().getGridY()</code>)
     *
     * @see player.weapon.Aim#getPositionGui()
     * @see player.weapon.Aim#getPosXGui()
     * @see player.weapon.Aim#getPosYGui() */
    public Shape getBoundsOfAttackedTile () {
        TileLike tile = (TileLike) Main.getContext().getWorld().terrain().tileAt(getGridX(), getGridY());
        return tile.getComponent().getBounds();
    }

    /** This returns the more accurate x-position of the center of the attacked Tile then the similar method <code>getPositionGui()</code>.
     *
     * @return the accurate x position of the center of the attackedTile
     * @see player.weapon.Aim#getPosYGui()
     * @see Aim#getPositionGui()
     *
     */
    public double getPosXGui () {
        TileLike attackedTile = (TileLike) Main.getContext().getWorld().terrain().tileAt(getGridX(), getGridY());
        return attackedTile.getComponent().getBounds().getBounds2D().getCenterX();
    }

    /** This returns the more accurate y-position of the center of the attacked Tile then the similar method <code>getPositionGui()</code>.
     *
     * @return the accurate y position of the center of the attackedTile
     * @see player.weapon.Aim#getPosXGui()
     * @see Aim#getPositionGui()
     */
    public double getPosYGui () {
        TileLike attackedTile = (TileLike) Main.getContext().getWorld().terrain().tileAt(getGridX(), getGridY());
        return attackedTile.getComponent().getBounds().getBounds2D().getCenterY();
    }

    /** The radius of the attack, in which the attacks effects the surrounding area. <p>
     * That doesn't mean that the damage of {@link player.weapon.RangedWeapon} is in the whole area the same.
     * Maximum damage is made in the center (the tile at <code>getGridX(), getGridY()</code>).
     * <p>
     *     The radius is stated in tiles. */
    public double getDamageRadius () {
        return damageRadius;
    }

    /** the width and height of the damageRadius on Screen. It is similar to: <code>anyTile.component().getWidth() * getAim().getDamageRadius</code>
     * for the width and <code>anyTile.component().getHeight() * getAim().getDamageRadius</code> for the height.
     * @return the Dimension of the damageRadius in pixel for displaying the damageRadius on GUI-Elements
     * @see Aim#getDamageRadius()
     * @see Aim#getDamageRadiusGUIWidth()
     * @see Aim#getDamageRadiusGUIHeight()
     */
    public Dimension getDamageRadiusGUI () {
        return new Dimension((int) getDamageRadiusGUIWidth(), (int) getDamageRadiusGUIHeight());
    }

    /** the width of the damageRadius on GUI applications: The width of a tile multiplied with its damageRadius.
     * @see Aim#getDamageRadius()
     * @see Aim#getDamageRadiusGUIWidth()
     * @see Aim#getDamageRadiusGUIHeight()  */
    public double getDamageRadiusGUIWidth () {
        return IsometricPolygonTile.TileWidth() * getDamageRadius();
    }

    /** the height of the damageRadius in pixel for GUI applications: The height of a tile multiplied with its damageRadius.
     * @see Aim#getDamageRadius()
     * @see Aim#getDamageRadiusGUIWidth()
     * @see Aim#getDamageRadiusGUIHeight() */
    public double getDamageRadiusGUIHeight () {
        return IsometricPolygonTile.TileHeight() * getDamageRadius();
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
