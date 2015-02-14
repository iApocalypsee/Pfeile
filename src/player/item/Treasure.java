package player.item;

import comp.ImageComponent;
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
 * The treasure contains some valuables and can be found by either Player or Bot. The difference to chests is, that
 * you don't need to open them (i.e. you need to do something). If a player or bot moves on the field the treasure is
 * placed, the treasure will be added to the inventory (after confirming the content). The Bounds of a treasure are saved
 * as {@link comp.ImageComponent} in {@link LootUI}.
 *
 * @see player.item.Chest
 * @see player.item.DefaultChest
 * */
public class Treasure extends Loot {

    private static BufferedImage image;

    static {
        try {
            image = ImageIO.read(Treasure.class.getClassLoader().getResourceAsStream(
                    "resources/gfx/item textures/treasure.png"));
        } catch (IOException e) {e.printStackTrace();}
    }

    /**
     * Creates a new treasure on the tile (<code>gridX</code>|<code>gridY</code>). The center of the bounds of the
     * component is the center of the tile. The width and height of the ImageComponent are the width and height of the
     * of the BufferedImage.
     *
     * @param gridX the x position of the tile of the Treasure
     * @param gridY the y position of the tile of the Treasure
     */
    public Treasure (int gridX, int gridY) {
        super(gridX, gridY, "Treasure");
        setLootUI(createUI());
    }

    @Override
    public LootUI createUI () {
        Rectangle2D tileBounds = getTile().getComponent().getPreciseRectangle();

        ImageComponent component = new ImageComponent(
                (int) (tileBounds.getCenterX() - 0.5 * image.getWidth()),
                (int) (tileBounds.getCenterY() - 0.5 * image.getHeight()), getImage(), GameScreen.getInstance());

        return new LootUI(component) {
            @Override
            public void draw (Graphics2D g) {
                ImageComponent lootComponent = (ImageComponent) getComponent();
                lootComponent.draw(g);
            }
        };
    }

    @Override
    public boolean collect (Player activePlayer) {
        throw new NotImplementedException();
        //return false;
    }

    @Override
    public boolean collect (Bot activeBot) {
        throw new NotImplementedException();
        //return false;
    }

    @Override
    public BufferedImage getImage () {
        return image;
    }
}
