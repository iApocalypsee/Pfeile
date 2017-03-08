package player.shop.trader;

import comp.Component;
import comp.ImageComponent;
import general.LogFacility;
import general.Main;
import gui.Drawable;
import gui.screen.GameScreen;
import player.shop.Article;
import world.GrassTile;
import world.Terrain;
import world.World;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * This is a trader. He can wander around. <p>
 * The money per turn changes with a normal distribution approximately between -initialMoneyPerTurn and
 * +initialMoneyPerTurn.
 * The value {@link WanderingTrader#initialMoney()} defines the amount of money the trader has at the beginning.
 * It affects e.g. the ability to exchange money or to buy need stock items. <p>
 * The value {@link WanderingTrader#initialMoneyPerTurn()} determines how the money of the trader changes independently
 * from the money the Trader earns from sells to players. Since it changes per turn with a gaussian normal distribution
 * it must be internally scaled with {@link WanderingTrader#GAUSSIAN_DISTRIBUTION_NORMINATION_FACTOR} to accomplish a
 * probability integral of 1. Every input and output value of initialMoneyPerTurn will be internally scaled and do not
 * effect the in/output.
 */
 // TODO: add exchanging abilities and add new items in the stock depending on the money the trader has
 // TODO: Interaction of the Trader with the user must be made possible (Mouse Event)
 // TODO: wandering trader image must be moved, when the world is moved. Need extra class for that.
public class WanderingTrader extends Trader implements Drawable {

    private int initialMoney;
    private double initialMoneyPerTurn;
    /** the distribution function needs to be stretched in order to get a proper moneyPerTurnChange. It's approximately
     *  sqrt(2*PI) ~ 2.5 */
    private static final double GAUSSIAN_DISTRIBUTION_NORMINATION_FACTOR = Math.sqrt(2*Math.PI);
    private Random random;
    private ImageComponent imageComponent;

    /** The articles, which the trader can sell, and the number of articles */
    private Map<Article, Integer> stock;

    // load the buffered image of a wandering trader in a static context:
    private static BufferedImage img;
    static {
        final String path = "resources/gfx/entities/friendly/wanderingTrader.png";
        try {
            img = ImageIO.read(WanderingTrader.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            LogFacility.log("Image of Wandering Trader failed to load: " + path, LogFacility.LoggingLevel.Error);
        }
    }

    // Constructor

    /**
     * This creates a new Wandering Trader without a stock.
     *
     * @param spawnX x position in the world grid
     * @param spawnY y position in the world grid
     * @param initialMoney the money the trader has at the beginning
     * @param initialMoneyPerTurn the change of money per turn.
     * @param world the world, necessary parameter in the initialization process, after that, you can use <code>Main.getContext().getWorld()</code>
     * @param name the name of the trader
     */
    public WanderingTrader (int spawnX, int spawnY, int initialMoney, int initialMoneyPerTurn, World world, String name) {
        super(world, spawnX, spawnY, name);

        this.initialMoney = initialMoney;
        //Due to the standard deviation of 1.0 used by the JAVA library, the values generated have to scaled
        this.initialMoneyPerTurn = initialMoneyPerTurn / GAUSSIAN_DISTRIBUTION_NORMINATION_FACTOR;

        random = new Random();
        onTurnEnded.registerJava(() -> {
            this.getPurse().setMoneyPerTurn(moneyPerTurnChanger());
        });
        onTurnCycleEnded.registerJava(this :: movement);

        stock = new HashMap<>();

        final Rectangle2D tileBounds = world.getTerrain().getTileAt(spawnX, spawnY).getComponent().getPreciseRectangle();
        imageComponent = new ImageComponent(0, 0, img, GameScreen.getInstance());
        // only center the imageComponent after the initialisation as description of the constructor of ImageComponent suggests.
        imageComponent.setCenteredLocation((int) tileBounds.getCenterX(), (int) tileBounds.getCenterY());
        setComponent(imageComponent);
    }

    /**
     * This creates a new Wandering Trader with the articles in his stock.
     *
     * @param articles articles the articles the trader should have at the beginning
     * @param spawnX x position in the world grid
     * @param spawnY y position in the world grid
     * @param initialMoney the money the trader has at the beginning
     * @param initialMoneyPerTurn the change of money per turn.
     * @param world the world. <code>Main.getContext().getWorld()</code>. It's an necessary parameter, because of a
     *              NullPointerException during the Initialization Process.
     * @param name the name of the trader
     */
    public WanderingTrader (Map<Article, Integer> articles, int spawnX, int spawnY, int initialMoney, int initialMoneyPerTurn, World world, String name) {
        this(spawnX, spawnY, initialMoney, initialMoneyPerTurn, world, name);
        stock = articles;
    }

    // Methods

    /** returns the new money per turn. It's distributed by the Gaussian Function. Very likely to be between
     * +/- initialMoneyPerTurn */
    private int moneyPerTurnChanger () {
        int newMoneyPerTurn = (int) (random.nextGaussian() * initialMoneyPerTurn);
        if (getPurse().getMoney() - newMoneyPerTurn < 0)
            return -getPurse().getMoney();
        else
            return newMoneyPerTurn;
    }

    @Override
    public Map<Article, Integer> stock () {
        return stock;
    }

    @Override
    public List<Article> retrieve (Predicate<Article> article, int amount) {
        Stream<Article> articleStream = stock.keySet().stream().filter(article);
        ArrayList<Article> resultArticles = new ArrayList<>(amount);
        articleStream.forEach(art -> {
            Integer amt = stock.get(art);
            if(amt != null && amt > 0) {
                for(int i = 0; i < amt; i++) {
                    if(resultArticles.size() >= amt) {
                        break;
                    }
                    resultArticles.add(art);
                }
            }
        });
        return resultArticles;
    }

    @Override
    public int initialMoneyPerTurn () {
        // We need to rescale the moneyPerTurn property to get proper values for classes outside of WanderingTrader
        return (int) (initialMoneyPerTurn * GAUSSIAN_DISTRIBUTION_NORMINATION_FACTOR);
    }

    @Override
    public int initialMoney () {
        return initialMoney;
    }

    @Override
    public int defaultMovementPoints () {
        return 4;
    }

    /** Called every turn, to determine, if the trader needs a new destination */
    private void movement () {
        // only create a new way point, if there isn't an old already.
        if (!getCurrentPath().isPresent()) {
            // TODO: it may be better to define an amount of turns to wait
            // Probability of 70% to stay for one turn cycle.
            if(random.nextFloat() < 0.3f) {
                // waited enough, let's go
                final Terrain world = Main.getContext().getWorld().getTerrain();

                for (int i = 0; i < 200; i++) {
                    int x = random.nextInt(world.width());
                    int y = random.nextInt(world.height());

                    if (world.getTileAt(x, y) instanceof GrassTile) {
                        moveTowards(x, y);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Wandering Trader (" + getGridX() + "|" + getGridY() + ")";
    }

    @Override
    public Component startComponent () {
        return  imageComponent;
    }

    @Override
    public void draw (Graphics2D g) {
        imageComponent.draw(g);
    }
}
