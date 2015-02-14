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
 * A round chest is chest, which only appears once a round. It contains the most striking weapons, but it must be
 * protected by some creeps and spawn in a fair distance between the players or nearer to the disadvantaged player.
 * However, it might also trigger some bad, world-effecting catastrophe (in far feature...).
 */
public class RoundChest extends Chest {

    /** the texture of a RoundChest */
    private static BufferedImage image;

    static {
        try {
            image = ImageIO.read(BagOfLoots.class.getClassLoader().getResourceAsStream(
                    "resources/gfx/item textures/roundChest.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RoundChest (int gridX, int gridY, String name) {
        super(gridX, gridY, "Round Chest");
        setLootUI(createUI());

    }

    @Override
    public LootUI createUI () {
        Rectangle2D tileBounds = getTile().getComponent().getPreciseRectangle();

        ImageComponent component = new ImageComponent(
                (int) (tileBounds.getCenterX() - 0.5 * getImage().getWidth()),
                (int) (tileBounds.getCenterY() - 0.5 * getImage().getHeight()), getImage(), GameScreen.getInstance());

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
