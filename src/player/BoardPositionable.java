package player;

import java.awt.*;

/**
 * @author Josip
 * @version 2/12/14
 */
public interface BoardPositionable {

    int getGridX();
    int getGridY();

    default Point getPosition() {
        return new Point(getGridX(), getGridY());
    }

}
