package player.shop.trader;

import comp.Component;
import general.Main;
import player.shop.Article;
import world.GrassTile;
import world.Terrain;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * This is a trader. He can wander around. <p>
 * The money per turn changes with a normal distribution approximately between -initialMoneyPerTurn and
 * +initialMoneyPerTurn
 */
public class WanderingTrader extends Trader {

    private int initialMoney;
    private double initialMoneyPerTurn;
    private Random random;

    /** The articles, which the trader can sell, and the number of articles */
    private Map<Article, Integer> stock;

    /**
     * This creates a new Wandering Trader without a stock.
     *
     * @param spawnX x position
     * @param spawnY y position
     * @param initialMoney the money the trader has at the beginning
     * @param initialMoneyPerTurn the change of money per turn.
     * @param name the name of the trader
     */
    public WanderingTrader (int spawnX, int spawnY, int initialMoney, int initialMoneyPerTurn, String name) {
        super(Main.getContext().getWorld(), spawnX, spawnY, name);

        this.initialMoney = initialMoney;
        //Due to the standard deviation of 1.0 used by the JAVA libery, the values generated have to scaled
        this.initialMoneyPerTurn = initialMoneyPerTurn / 2.5;

        random = new Random();
        onTurnEnded.registerJava(() -> {
            this.getPurse().setMoneyPerTurn(moneyPerTurnChanger());
        });
        onTurnCycleEnded.registerJava(this :: movement);

        stock = new HashMap<>();
    }

    /**
     * This creates a new Wandering Trader with the articles in his stock.
     *
     * @poram articles articles the articles the trader should have at the beginning
     * @param spawnX x position
     * @param spawnY y position
     * @param initialMoney the money the trader has at the beginning
     * @param initialMoneyPerTurn the change of money per turn.
     * @param name the name of the trader
     */
    public WanderingTrader (Map<Article, Integer> articles, int spawnX, int spawnY, int initialMoney, int initialMoneyPerTurn, String name) {
        this(spawnX, spawnY, initialMoney, initialMoneyPerTurn, name);
        stock = articles;
    }

    /** returns the new money per turn. Approximately between +initialMoneyPerTurn and -initialMoneyPerTurn */
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
        return 0;
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
    public Component startComponent () {
        // TODO: the component for drawing the trader
        return null;
    }
}
