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
        try {
            image = ImageIO.read(DefaultChest.class.getClassLoader().getResourceAsStream
                            ("resources/gfx/item textures/defaultChest.png"));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("The BufferedImage of class DefaultChest couldn't be loaded! Path: " +
                    "resources/gfx/item textures/defaultChest.png", LogFacility.LoggingLevel.Error);

            //image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public DefaultChest (int gridX, int gridY) {
        super(gridX, gridY, "Default Chest");
        // cannot reference createUI() before calling the super constructor
        setLootUI(createUI());
    }


    @Override
    public LootUI createUI () {
        Rectangle2D locationOfTile = getTile().getComponent().getPreciseRectangle();

        ImageComponent component = new ImageComponent(
                (int) (locationOfTile.getCenterX() - 0.5 * getImage().getWidth()),
                (int) (locationOfTile.getCenterY() - 0.5 * getImage().getHeight()), getImage(), GameScreen.getInstance());

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
