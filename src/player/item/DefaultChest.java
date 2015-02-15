package player.item;

import comp.ImageComponent;
import general.LogFacility;
import gui.screen.GameScreen;
import newent.Bot;
import newent.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * This is the usual chest (compared to {@link player.item.RoundChest}. It can be found and opened by players and bots.
 * */
public class DefaultChest extends Chest {

    private static BufferedImage image;

    static {
        String path = "resources/gfx/item textures/defaultChest.png";
        try {
            image = ImageIO.read(DefaultChest.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class DefaultChest couldn't be loaded! Path: " + path,
                    LogFacility.LoggingLevel.Error);
        }
    }

    /**
     * Creating a new basic Chest on the position (<code>gridX</code>|<code>gridY</code>).
     *
     *
     * @param gridX the x-position of the tile, where the DefaultChest should be placed
     * @param gridY and the y-position
     */
    public DefaultChest (int gridX, int gridY) {
        super(gridX, gridY, "Default Chest");
    }

    @Override
    public BufferedImage getImage () {
        return image;
    }
}
