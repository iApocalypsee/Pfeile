package player.shop.trader;

import general.LogFacility;
import general.PfeileContext;
import gui.Drawable;
import newent.CommandTeam;
import newent.Player;
import newent.VisionMap;
import player.item.loot.KeyDefaultChest;
import player.item.loot.KeyRoundChest;
import player.item.loot.WorldLootList;
import player.item.ore.CopperOre;
import player.item.ore.IronOre;
import player.item.potion.*;
import player.shop.Article;
import world.GrassTile;
import world.Terrain;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class allows to access all registered traders on the world. It provides a shortcut to draw the traders (it draws
 * only the wandering traders visible to the active player! but you could use it otherwiese, but why would you? :D)
 * and it serves as helper class for the {@link WanderingTrader} to provide e.g. a new or changing stock.
 * The value <code>numberOfInitialTraders</code> in the constructor defines how many traders there will spawn at the
 * beginning of the game.
 */
public class WanderingTraderList implements Drawable {
    /** The number of traders, which spawn at the beginning of the game. */
    private static final int NUMBER_OF_INITIAL_TRADERS = 3;

    /** contains all the wandering traders registered in the world */
    private final List<WanderingTrader> traders;

    /** contains only the wandering traders, which are registered and visible to the active player (subset of the set
     *  traders). It it used to draw the visible traders faster, but it need to be updated on every turn, just like the
     *  WorldLootList, which is a similar construct.
     */
    private final List<WanderingTrader> visibleTraders;

    /** for initialization perposes to avoid NullPointerExceptions */
    private final PfeileContext context;

    /** Creates a new WanderingTraderList.
     *  @param context PfeileContext must be given here, since in the Initialization process, there is no way to ensure,
     *                 that there is an visionMap of the active player */
    public WanderingTraderList (PfeileContext context) {
        traders = new ArrayList<>(NUMBER_OF_INITIAL_TRADERS);
        visibleTraders = new ArrayList<>(NUMBER_OF_INITIAL_TRADERS);
        this.context = context;

        // every time the turn changes, the visibleTraders list has to change.
        context.turnSystem().onTurnGet().registerJava(team -> updateVisibleTraders());

        // every time a player moves, the visibleTraders list has to change.
        context.getTurnSystem().getTeams().forEach(team -> {
            Player player = ((CommandTeam) team).getHead();
            player.onLocationChanged.registerJava(locationChangedEvent -> updateVisibleTraders());
        });
    }

    /** Spawns NUMBER_OF_INITIAL_TRADERS wandering traders at the beginning of the game. They are completely randomized,
     *  but if you want to have a special trader at the beginning, you can do it here. */
    public void spawnInitialTraders() {
        for (int i = 0; i < NUMBER_OF_INITIAL_TRADERS; i++)
            spawnNewRandomWanderingTrader();
    }

    /** Spawns and <b>registers</b> a new Trader with random stock {@see generateRandomStock} and a random spawn location
     * on a grass tile. */
    public void spawnNewRandomWanderingTrader () {
        Point spawnPoint = spawnPoint();
        Random random = new Random();
        registerTrader(new WanderingTrader(generateRandomStock(), (int) spawnPoint.getX(), (int) spawnPoint.getY(),
                random.nextInt(1200) + 500, random.nextInt(20) + 5, context.getWorld()));
    }

    /** Returns the grid position of a randomly chosen grass tile. It is used here to define the spawn position of a
     *  randomly generated wandering trader.
     *
     * @return the grid position of a random grass tile.
     */
    private Point spawnPoint () {
        Point spawnPoint = new Point(-1, -1);
        Terrain terrain = context.getWorld().terrain();
        Random random = new Random();

        do {
            int spawnX = random.nextInt(PfeileContext.worldSizeX().get());
            int spawnY = random.nextInt(PfeileContext.worldSizeY().get());

            if (terrain.tileAt(spawnX, spawnY) instanceof GrassTile)
                spawnPoint.setLocation(spawnX, spawnY);
        } while (spawnPoint.x == -1 || spawnPoint.y == -1);

        return spawnPoint;
    }

    /** Generates a HashMap, the stock of a new wandering trader. It only contains random items and an random amount. */
    private Map<Article, Integer> generateRandomStock () {
        int maximumNumberOfArticles = 13; // number of calls "put(createRandomArticle(...), ...)"

        Map<Article, Integer> stock = new HashMap<>(maximumNumberOfArticles);
        Random random = new Random();

        // at least some different articles (a few could be removed later)
        stock.put(createRandomArticle(0.05f), 6 + random.nextInt(7));
        stock.put(createRandomArticle(0.18f), 3 + random.nextInt(2));
        stock.put(createRandomArticle(0.32f), 3 + random.nextInt(6));
        stock.put(createRandomArticle(0.63f), 3 + random.nextInt(1));
        stock.put(createRandomArticle(0.74f), 1 + random.nextInt(3));
        stock.put(createRandomArticle(0.92f), 1);

        // possibly some more
        if (random.nextFloat() < 0.8f)
            stock.put(createRandomArticle(0.07f), 4 + random.nextInt(4));
        if (random.nextFloat() < 0.6f)
            stock.put(createRandomArticle(0.28f), 3 + random.nextInt(2));
        if (random.nextFloat() < 0.4f)
            stock.put(createRandomArticle(0.36f), 3 + random.nextInt(2));
        if (random.nextFloat() < 0.3f)
            stock.put(createRandomArticle(0.47f), 3 + random.nextInt(4));
        if (random.nextFloat() < 0.25f)
            stock.put(createRandomArticle(0.66f), 1 + random.nextInt(3));
        if (random.nextFloat() < 0.2f)
            stock.put(createRandomArticle(0.88f), 2 + random.nextInt(2));
        if (random.nextFloat() < 0.1f)
            stock.put(createRandomArticle(0.98f), 1 + random.nextInt(1));

        // Checking for multiple articles
        // multiple items will not override the same key, since they are different objects (functions)
        List<String> itemNames = new ArrayList<>(maximumNumberOfArticles);
        Map<Article, Integer> sortedStock = new HashMap<>(maximumNumberOfArticles);
        for (Article article : stock.keySet()) {
            String name = article.cachedItem().getName();
            if (!itemNames.contains(name)) {
                itemNames.add(name);
                sortedStock.put(article, stock.get(article));
            } // if the same item is contained twice, the second one, will not be added to sortedStock
        }

        return sortedStock;
    }

    /** Creates an article with random probability. The higher the parameter <code>rarity</code>, the higher the change,
     * that rare articles are added.
     *
     * @param rarity between 0 and 1 - changes the probability for rare and good articles
     * @return a new randomized article
     */
    // TODO: changes prizes to be more adequate, compare with Loots and internal shop
    // TODO: add weapons and armors
    private Article createRandomArticle (float rarity) {
        assert rarity >= 0 && rarity <= 1;
        Random random = new Random();
        float prop = 0.5f * (random.nextFloat() + rarity);

        // the higher values are for the if-calls should correspond to rarer items. --> articles higher in that code part are rarer
        if (prop > 0.98f) {
            return new Article(() -> new KeyRoundChest(), 280 + random.nextInt(60));
        } else if (prop > 0.94f) {
            return new Article(() -> new KeyDefaultChest(), 90 + random.nextInt(80));
        } else if (prop > 0.87f) {
            byte level = (byte) (Math.floor(random.nextFloat() * rarity * 2.9));
            return new Article(() -> new PotionOfHealing(level), 12 + random.nextInt(10) + level * (8 * random.nextInt(3)));
        } else if (prop > 0.82f) {
            byte level = (byte) (Math.floor(random.nextFloat() * rarity * 2.8));
            return new Article(() -> new PotionOfPoison(level), 20 + random.nextInt(4) + level * (10 + random.nextInt(5)));
        } else if (prop > 0.76f) {
            byte level = (byte) (Math.floor(random.nextFloat() * rarity * 2.7));
            return new Article(() -> new PotionOfDamage(level), 25 + random.nextInt(7) + level * (9 + random.nextInt(4)));
        } else if (prop > 0.68f) {
            byte level = (byte) (Math.floor(random.nextFloat() * rarity * 2.9));
            return new Article(() -> new PotionOfMovement(level), 15 + random.nextInt(3) + level * (8 + random.nextInt(6)));
        } else if (prop > 0.61f) {
            byte level = (byte) (Math.floor(random.nextFloat() * rarity * 2.75));
            return new Article(() -> new PotionOfFortune(level), 11 + random.nextInt(20) + level * (6 + random.nextInt(9)));
        } else if (prop > 0.3f) {
            return new Article(() -> new CopperOre(), 4 + random.nextInt(3));
        } else {
            return new Article(() -> new IronOre(), 4 + random.nextInt(4));
        }
    }

    /** registers a new WanderingTrader in the list. Do not register the same trader twice.*/
    public void registerTrader (WanderingTrader trader) {
        synchronized (traders) {
            traders.add(trader);
        }
        // Reduces the time needed for an updateVisibleTraders call.
        if (context.getActivePlayer().visionMap().isVisible(trader.getGridX(), trader.getGridY())) {
            synchronized (visibleTraders) {
                visibleTraders.add(trader);
            }
        }
    }

    /**
     * Removes the specified WanderingTrader trader.
     * If the element could be removed, it updates the list of the visibleLoots {@link WorldLootList#updateVisibleLoot()}.
     *
     * @param trader the trader to be removed
     * @return <code>true</code> - if the list contained the <code>collectedLoot</code>
     */
    public boolean remove (WanderingTrader trader) {
        boolean removed;

        synchronized (traders) {
            removed = traders.remove(trader);
        }

        if (removed) {
            updateVisibleTraders();
        } else {
            LogFacility.log("Can't remove trader: " + trader.toString() + " from the list: " + this.toString(),
                    LogFacility.LoggingLevel.Error);
        }
        return removed;
    }

    /** updates the visible trader list. Adds every wandering trader on a visible field from the traders-list to the
     *  visibleTraders-list. Needs to be called, e.g. it's another players turn.
     */
    private void updateVisibleTraders () {
        VisionMap visionMap = context.getActivePlayer().visionMap();

        synchronized (visibleTraders) {
            for (WanderingTrader trader : traders) {
                // only add, if in the trader is on a visible tile.
                if (visionMap.isVisible(trader.getGridX(), trader.getGridY()))
                    visibleTraders.add(trader);
            }
        }
    }

    @Override
    public String toString () {
        return "WanderingTraderList{" + traders.size() + " traders registered}";
    }

    /**
     * Draws every WanderingTrader, but only in the visible area of the activePlayer.
     * (The VisionStatus is not {@link newent.VisionStatus#Hidden})
     *
     * @param g the graphics object used by every {@link gui.Drawable}.
     */
    @Override
    public void draw (Graphics2D g) {
        for (WanderingTrader trader: visibleTraders) {
            trader.draw(g);
        }
    }
}
