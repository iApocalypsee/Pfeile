package player.shop;

import newent.MoneyEarner;

import java.util.List;
import java.util.function.Predicate;

public interface TraderLike {

    /**
     * Checks if the given article is in the trader's stock.
     *
     * Use it like this
     *
     * <code>
     *   traderLike.isAvailable(article => article.name == "Cheese" && article.price < 10)
     * </code>
     *
     * Translate example to Java use cases if necessary.
     *
     * @param f The article predicate.
     * @return If the article predicate is at least once in the trader's stock, true.
     */
    boolean isAvailable(Predicate<Article> f);

    /**
     * Sells the specified article to the given entity.
     *
     * @param to The entity to sell to.
     * @param article The function which determines what article is to be sold.
     * @param amount How many articles to sell. Based on this value, the total's transaction value is calculated.
     * @return Was the transaction successful?
     */
    boolean sell(MoneyEarner to, Predicate<Article> article, int amount);

    default boolean sell(MoneyEarner to, Predicate<Article> article) {
        return sell(to, article, 1);
    }

    /**
     * A listing of all articles that the trader can sell.
     * Note that the list should not have the same article twice in it.
     *
     * TODO Change return type of this method from 'List' to 'Set' (since no double elements are allowed).
     *
     * @return A list of all articles that the trader can sell.
     */
    List<Article> articles();

    /**
     * @see {@link TraderLike#articles}
     */
    default List<Article> getArticles() {
        return articles();
    }

    /**
     * Abstraction method for receiving money.
     *
     * @param from Who is paying the trader?
     * @param moneyAmount How much money to receive.
     */
    void receive(MoneyEarner from, int moneyAmount);

}
