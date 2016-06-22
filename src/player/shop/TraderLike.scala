package player.shop

import java.util.function._

import newent.MoneyEarner

import scala.compat.java8.FunctionConverters._

/**
  * The most basic trait for trader-like objects.
  * Contains only abstract method definitions and some default implementations, but no state.
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
  def isAvailable(f: Article => Boolean): Boolean

  /**
    * Java interoperability method with the actual [[player.shop.TraderLike#isAvailable(scala.Function1)]] function.
    *
    * @param f The Java function to be passed on to the Scala version of 'isAvailable'.
    * @return If the article predicate is at least once in the trader's stock, `true`.
    * @see [[player.shop.TraderLike#isAvailable(java.util.function.Function)]]
    */
  def isAvailable(f: Predicate[Article]): Boolean = isAvailable(asScalaFromPredicate(f))

  /**
    * Sells the specified article to the given entity.
    *
    * @param to The entity to sell to.
    * @param article The function which determines what article is to be sold.
    * @param amount How many articles to sell. Based on this value, the total's transaction value is calculated.
    * @return Was the transaction successful?
    */
  def sell(to: MoneyEarner, article: Article => Boolean, amount: Int = 1): Boolean

  def sell(to: MoneyEarner, article: Predicate[Article], amount: Int): Boolean = sell(to, asScalaFromPredicate(article), amount)

  /**
    * A listing of all articles that the trader can sell.
    * Note that the list should not have the same article twice in it.
    *
    * @return A list of all articles that the trader can sell.
    */
  def articles: Seq[Article]

  /**
    * Abstraction method for receiving money.
    *
    * @param from Who is paying the trader?
    * @param moneyAmount How much money to receive.
    */
  def receive(from: MoneyEarner, moneyAmount: Int): Unit

}
