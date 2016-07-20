package player.shop

import java.util.function._
import java.util.{List => IList}

import newent.MoneyEarner

/**
  * The most basic trait for trader-like objects.
  * Contains only abstract method definitions and some default implementations.
  *
  * This trait was originally designed to be a pure interface, but the necessity of having default implementations
  * for some methods is too pressing right now.
  */
trait TraderLike {

  /**
    * Checks if the given article is in the trader's stock.
    *
    * Use it like this
    * {{{
    *   traderLike.isAvailable(article => article.name == "Cheese" && article.price < 10)
    * }}}
    *
    * Translate example to Java use cases if necessary.
    *
    * @param f The article predicate.
    * @return If the article predicate is at least once in the trader's stock, `true`.
    */
  def isAvailable(f: Predicate[Article]): Boolean

  def sell(to: MoneyEarner, article: Predicate[Article]): Boolean = this.sell(to, article, 1)

  /**
    * Sells the specified article to the given entity.
    *
    * @param to The entity to sell to.
    * @param article The function which determines what article is to be sold.
    * @param amount How many articles to sell. Based on this value, the total's transaction value is calculated.
    * @return Was the transaction successful?
    */
  def sell(to: MoneyEarner, article: Predicate[Article], amount: Int): Boolean

  /**
    * A listing of all articles that the trader can sell.
    * Note that the list should not have the same article twice in it.
    *
    * @return A list of all articles that the trader can sell.
    */
  def articles: IList[Article]

  /**
    * @see [[player.shop.TraderLike#articles()]]
    */
  def getArticles = articles

  /**
    * Abstraction method for receiving money.
    *
    * @param from Who is paying the trader?
    * @param moneyAmount How much money to receive.
    */
  def receive(from: MoneyEarner, moneyAmount: Int): Unit

}
