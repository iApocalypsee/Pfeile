package player;

import general.Main;
import world.Terrain;
import world.Tile;

import java.awt.*;

/**
 * @author Josip
 * @version 2/12/14
 */
public interface BoardPositionable {

    int getGridX();
    int getGridY();

    /** @return  a new Point (getGridX(), getGridY()) */
    default Point getPosition() {
        return new Point(getGridX(), getGridY());
    }

    /** Returns the tile on which the Object (usually a GameObject is standing); of course this method is only available
     * after the World Generation. This method does not save the Tile (it is computed with the <class>Terrain</class>
     * method {@link Terrain#getTileAt(int, int)}, so if you want to use the Tile regulary, save it
     * locally (till it changes).
     * @return The Position the <class>BoardPositionable</class> is standing on.
     */
    default Tile getTile() {
        return Main.getContext().getWorld().getTerrain().getTileAt(getGridX(), getGridY());
    }

}
